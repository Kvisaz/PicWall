package com.arellomobile.picwall.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.widget.ImageView;

import com.arellomobile.picwall.view.progress.ProgressIndicator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

/**
 * Network Image Loader  - MAIN class
 * load from any url to ImageView
 * <p/>
 * can be used for ListView (RecycleView)
 * <p/>
 * <p/>
 * How to use in List/RecycleView
 * <p/>
 * 1. Create ImageLoader instance in List/RecycleView constructor
 * 2. Create inner adapter class for List/RecycleView
 * 3. Use imageLoader.load(String url, ImageView view)
 * <p/>
 * How to use anywhere
 * 1. Create ImageLoader instance (context needed)
 * 2. Use imageLoader.load(String url, ImageView view)
 * <p/>
 * You can use single ImageLoader instance for multiple downloads
 * <p/>
 * I use tutorial from here - http://androidexample.com/Download_Images_From_Web_And_Lazy_Load_In_ListView_-_Android_Example/index.php?view=article_discription&aid=112&aaid=134#
 * <p/>
 * and made some changes
 */


public class ImageLoader {

    // кэш битмапов для быстрой реакции
    MemoryCache memoryCache = new MemoryCache();

    // файловый кэш, инициализируется контекстом
    FileCache fileCache;
    // ограничитель файлового кэша
    public final int MAX_AMOUNT_FILES_IN_CACHE = 50;
    // сколько удаляем, если превышение
    public final int CACHE_CHUNK_SIZE_FOR_CLEAR = 20;
    // индикатор размера большой картинки (удаляем старые файлы из кэша перед заливкой)
    private static final int BIG_PICTURE_MIN_SIZE = 100000;

    // шаг прогрессбара / 100/ шаг = на сколько порций бьем файл при буферизации
    public final int PROGRESS_DOWNLOAD_PERCENT_STEP = 10;
    // распределение процентов для загрузки и декодирования
    public final int PROGRESS_PERCENT_DOWNLOAD_MAX = 100;
    public final int PROGRESS_PERCENT_DECODE_MAX = 20;

    private Map<ImageView, String> imageViewsAndUrls = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());

    // для создания рабочего пула потоков
    ExecutorService executorService;
    // для отправки сообщений в основной поток приложения (Looper уже есть)
    Handler handler = new Handler();

    // ------- Some cache objects will be added here

    private final int TIMEOUT_CONNECT = 3000;
    private final int TIMEOUT_READ = 3000;
    private boolean FOLLOWS_REDIRECT = true;


    @Inject
    public ImageLoader(Context context) {
        fileCache = new FileCache(context);
        executorService = Executors.newFixedThreadPool(5);
        deleteOldCacheFiles();
    }

    /*
    *   url  - Web URL of picture
    *   imageView - imageView to insert after download
    *   maxWidth - downsample picture to maximal width for less memory
     *   if maxWidth==0 then no downsampling
    * */
    public void load(String url, ImageView imageView, int maxWidth, boolean useMemCache, ProgressIndicator progressIndicator) {
        imageViewsAndUrls.put(imageView, url);
        Bitmap bitmap = null;
        // быстрая загрузка из кэша памяти
        if (useMemCache)
            bitmap = memoryCache.get(url);

        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            // отправляем задачу на загрузку
//            executorService.submit(new SmallBitmapLoaderTask(url, imageView, maxWidth));
            executorService.submit(new BitmapLoaderTask(url, imageView, maxWidth, useMemCache, progressIndicator));
        }
    }

    // ----------------------------------------------------------------------
    // ------------------------- load Picture  Task ---------------------------
    // ----------------------------------------------------------------------

    private class BitmapLoaderTask implements Runnable {
        final String url;
        final ImageView imageView;
        final int actualWidth;
        final boolean useMemCache;
        final ProgressIndicator progressIndicator;

        public BitmapLoaderTask(String url, ImageView imageView, int actualWidth, boolean useMemCache, ProgressIndicator progressIndicator) {
            this.url = url;
            this.imageView = imageView;
            this.actualWidth = actualWidth;
            this.useMemCache = useMemCache;
            this.progressIndicator = progressIndicator;
        }

        @Override
        public void run() {
            try {
                //Check if imageView reused
                if (imageViewReused(url, imageView)) return;
                // Progress Bar
                if (progressIndicator != null) {
                    handler.post(new ProgressBarShowTask(progressIndicator));
                }

                Bitmap freshBitmap = downloadBitmap(url, actualWidth, progressIndicator);

                if (useMemCache) memoryCache.put(url, freshBitmap);

                if (imageViewReused(url, imageView)) return;
                // Get bitmap to display
                // Progress Bar
                if (progressIndicator != null) {
                    handler.post(new ProgressBarTask(progressIndicator, PROGRESS_PERCENT_DOWNLOAD_MAX));
                    handler.post(new ProgressBarHideTask(progressIndicator));
                }
                handler.post(new BitmapDisplayTask(freshBitmap, url, imageView));

            } catch (Throwable th) {
                th.printStackTrace();
            }

        }
    }

    private Bitmap downloadBitmap(String url, int maxWidth, ProgressIndicator progressIndicator) {
        Bitmap bitmap = null;
        File fileForCache = fileCache.getFile(url);
        if (fileForCache.getTotalSpace() > 0) {
            bitmap = decodeFile(fileForCache, maxWidth);
        }

        if (bitmap != null)
            return bitmap;

        try {
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            conn.setConnectTimeout(TIMEOUT_CONNECT);
            conn.setReadTimeout(TIMEOUT_READ);
            conn.setInstanceFollowRedirects(FOLLOWS_REDIRECT);
            InputStream is = conn.getInputStream();

            // Constructs a new FileOutputStream that writes to file
            // if file not exist then it will create file
            OutputStream os = new FileOutputStream(fileForCache);

            int fileLength = conn.getContentLength();

            if (fileLength > BIG_PICTURE_MIN_SIZE) {
                deleteOldCacheFiles();
            }

//            Log.d(Constants.LOG_TAG, "------- fileLength -------- " + fileLength);
            CopyStreamProgress(is, os, fileLength, progressIndicator);

            os.close();
            conn.disconnect();
            // decode bitmap to maxWidth
            bitmap = decodeFile(fileForCache, maxWidth);

            return bitmap;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (Throwable ex) {
            ex.printStackTrace();
            // типа тут мы встроили очистку кэша памяти? ну, вариант рабочий
            if (ex instanceof OutOfMemoryError)
                memoryCache.clear();
            return null;
        }

        return null;
    }


    private Bitmap decodeFile(File fileForCache, int REQUIRED_WIDTH) {
        try {
            //Decode image size
            /*
            *   Тут используется хак для установки размеров в
            *   объект BitmapFactory.Options
            *
            * */
            BitmapFactory.Options bfOptions = new BitmapFactory.Options();
            bfOptions.inJustDecodeBounds = true;
            FileInputStream stream1 = new FileInputStream(fileForCache);
            BitmapFactory.decodeStream(stream1, null, bfOptions);
            stream1.close();

            //Find the correct scale value. It should be the power of 2.
            int width_tmp = bfOptions.outWidth, height_tmp = bfOptions.outHeight;
            /*
             *  scale
             *  число исходных пикселей / число пикселей в результате
             *
             *  должно быть равно к степени двойки (все равно округлит)
             * */
            int scale = 1;
            if (REQUIRED_WIDTH > 0) {
                while (true) {
                    if (width_tmp / 2 < REQUIRED_WIDTH) // || height_tmp / 2 < REQUIRED_HEIGHT
                        break;
                    width_tmp /= 2;
                    // height_tmp /= 2;
                    scale *= 2;
                }
            }

            //decode with current scale values
            // если scale > 1, получаем уменьшенный битмап из декодера - экономим память!
//            BitmapFactory.Options o2 = new BitmapFactory.Options();
            bfOptions.inSampleSize = scale;
            bfOptions.inJustDecodeBounds=false;
            FileInputStream stream2 = new FileInputStream(fileForCache);
            Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, bfOptions);
            stream2.close();
            return bitmap;

        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    //Used to display bitmap in the UI thread
    private class BitmapDisplayTask implements Runnable {
        Bitmap bitmap;
        String url;
        ImageView imageView;

        public BitmapDisplayTask(Bitmap bitmap, String url, ImageView imageView) {
            this.bitmap = bitmap;
            this.imageView = imageView;
            this.url = url;
        }

        @Override
        public void run() {
            if (imageViewReused(url, imageView)) return;
            if (bitmap != null)
                imageView.setImageBitmap(bitmap);
        }
    }

    /*
    *   Эта функция проверяет
    *       а. актуален ли вывод для этого ImageView
    *           (null - его уже никто не использует, смотри выше про WeakHashMap)
    *       б. актуален ли вывод для текущего URL
    *           (если URL другой, значит у нас ImageView, скорее всего, ре-используется
    *           и данные нужны уже другие)
    *    СЛЕДУЕТ ИСПОЛЬЗОВАТЬ КАЖДЫЙ РАЗ ПЕРЕД ИСПОЛЬЗОВАНИЕМ ImageView и Url
    *    тк список может двигаться
    *
    *
    * */
    boolean imageViewReused(String url, ImageView imageView) {
        String urlOld = imageViewsAndUrls.get(imageView);
        if (urlOld == null || !urlOld.equals(url))
            return true;
        return false;
    }

    // ----------------------- Cache ---------------------
    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();
    }


    public void deleteOldCacheFiles() {
        fileCache.checkSizeAndClearOld(MAX_AMOUNT_FILES_IN_CACHE, CACHE_CHUNK_SIZE_FOR_CLEAR);
    }

    // ----------------------- Utils -------------------------

    public void CopyStreamProgress(InputStream is, OutputStream os, int fileLength, ProgressIndicator progressIndicator) throws IOException {
        boolean useProgressBar = progressIndicator != null ? true : false;
        int buffer_size = 2048;

        // 00
        if (useProgressBar) {
            int progressStepNum = PROGRESS_PERCENT_DOWNLOAD_MAX / PROGRESS_DOWNLOAD_PERCENT_STEP;
            buffer_size = (int) ((double) fileLength) / progressStepNum;
//            Log.d(Constants.LOG_TAG,"--- CopyStreamProgress buffer size = --- "+buffer_size);
        }

        byte[] bytes = new byte[buffer_size];

        double readedLength = 0;
        int progressPercent = 0;
        for (; ; ) {
            //Read byte from input stream
            // count != buffer size
            int count = is.read(bytes, 0, buffer_size);

            if (count == -1)
                break;
            //Write byte from output stream
            os.write(bytes, 0, count);
            if (useProgressBar) {
                readedLength += count;
                progressPercent = (int) ((readedLength / fileLength) * 100);
/*                Log.d(Constants.LOG_TAG,"--- CopyStreamProgress count = --- "+count);
                Log.d(Constants.LOG_TAG,"--- CopyStreamProgress readedLength = --- "+readedLength);
                Log.d(Constants.LOG_TAG,"--- CopyStreamProgress progressPercent = --- "+progressPercent);*/
                handler.post(new ProgressBarTask(progressIndicator, progressPercent));
            }
        }
    }

    //Used to display ProgressBar in the UI thread
    private class ProgressBarTask implements Runnable {
        ProgressIndicator progressIndicator;
        int progress;

        public ProgressBarTask(ProgressIndicator progressIndicator, int progress) {
            this.progressIndicator = progressIndicator;
            this.progress = progress;
        }

        @Override
        public void run() {
            if (progressIndicator != null)
                progressIndicator.setProgress(progress);
        }
    }

    //Used to display ProgressBar in the UI thread
    private class ProgressBarShowTask implements Runnable {
        ProgressIndicator progressIndicator;


        public ProgressBarShowTask(ProgressIndicator progressIndicator) {
            this.progressIndicator = progressIndicator;
        }

        @Override
        public void run() {
            if (progressIndicator != null) {
                progressIndicator.setProgress(0);
                progressIndicator.show();
            }

        }
    }

    //Used to display ProgressBar in the UI thread
    private class ProgressBarHideTask implements Runnable {
        ProgressIndicator progressIndicator;

        public ProgressBarHideTask(ProgressIndicator progressIndicator) {
            this.progressIndicator = progressIndicator;
        }

        @Override
        public void run() {
            if (progressIndicator != null) {
                progressIndicator.hide();
            }

        }
    }


}
