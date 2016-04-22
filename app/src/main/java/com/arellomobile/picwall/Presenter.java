package com.arellomobile.picwall;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.arellomobile.picwall.events.PageLoadEvent;
import com.arellomobile.picwall.events.PictureNextLoadSuccessEvent;
import com.arellomobile.picwall.events.PicturePrevLoadSuccessEvent;
import com.arellomobile.picwall.model.PicturePage;
import com.arellomobile.picwall.network.Client;

import org.greenrobot.eventbus.EventBus;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Presenter {
    private final Context context;
    ExecutorService executorService;
    public LinkedList<PicturePage> pages;

    public static final int NO_SCROLL = 0;
    public static final int CORRECT_SCROLL_PREV = 1;
    public static final int CORRECT_SCROLL_NEXT = 2;

    private final int PAGE_MIN = 1;
    private int PAGE_MAX = 2;
    public Presenter(Context context) {
        this.context = context;
         executorService = Executors.newFixedThreadPool(1);
        pages = new LinkedList<>();
        loadStartPage(); // start page
    }


    // ------------------------ Check Network --------------------
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private boolean checkNetwork() {
        if(!isNetworkAvailable()) {
            Log.d(Constants.LOG_TAG," ----- No connection ------- ");
            return true;
        }
        return false;
    }

    // ------------------------ Pages  --------------------
    public void loadStartPage() {
        asyncLoadPage(1);
        asyncLoadPage(2);
    }

    // ------------------------ Вставляем и убираем страницы  --------------
    // Вставляет страницу в правильном порядке - сверху или снизу, согласно номерам
    private int addPage(PicturePage newPage) {
        int correctScroll = NO_SCROLL;
        if (newPage == null) {
            Log.d(Constants.LOG_TAG, " ------  null page event = " + pages.size());
            return correctScroll;
        }
        newPage.title = Constants.GRID_PAGE_TITLE_PREFIX + " " + newPage.numberCurrent + " " + Constants.GRID_PAGE_TITLE_MID + " " + newPage.totalPageAmount + " " + Constants.GRID_PAGE_TITLE_POSTFIX;

        if (pages.size() == 0 || newPage.numberCurrent > pages.getLast().numberCurrent ) {
            correctScroll = addPageInEnd(newPage);
        } else if (newPage.numberCurrent < pages.getFirst().numberCurrent) {
            correctScroll = addPageInBegin(newPage);
        }
        else {
            Log.d(Constants.LOG_TAG, " ------  page NO prev and NO NEXT ----- ");
            Log.d(Constants.LOG_TAG, " ------   newPage.numberCurrent ----- " + newPage.numberCurrent);
        }

        return correctScroll;
    }

    private int addPageInBegin(PicturePage newPage) {
        int correctScroll = NO_SCROLL;
        pages.addFirst(newPage);
        Log.d(Constants.LOG_TAG, " ------  addPageInBegin = ");

        if (pages.size() > Constants.GRID_MAX_PAGES_IN_MEMORY) {
            pages.removeLast();
            Log.d(Constants.LOG_TAG, " ------  pages.size() > Constants.GRID_MAX_PAGES_IN_MEMORY = ");
            correctScroll =  CORRECT_SCROLL_PREV; // сигнал GridView
        }
        return correctScroll;
    }

    private int addPageInEnd(PicturePage newPage) {
        int correctScroll = NO_SCROLL;
        Log.d(Constants.LOG_TAG, " ------  addPageInEnd = ");
        pages.addLast(newPage);

        if (pages.size() > Constants.GRID_MAX_PAGES_IN_MEMORY) {
            Log.d(Constants.LOG_TAG, " ------  pages.size() > Constants.GRID_MAX_PAGES_IN_MEMORY = ");
            pages.removeFirst();
            correctScroll = CORRECT_SCROLL_NEXT;
        }
        Log.d(Constants.LOG_TAG, " ------  pages.size() > Constants.GRID_MAX_PAGES_IN_MEMORY = "+pages.size());
        return correctScroll;
    }

    // ищем следующую страницу для заказанной страницы
    public void tryNextPage(PicturePage mPicturePage){
        // ищем в пуле страниц следующую
        int index = pages.indexOf(mPicturePage);
         if(index!=-1 && index<pages.size()-1){
             // todo бросить сообщение с картинкой
             Log.d(Constants.LOG_TAG, " ------  tryNextPage FOUND PAGE! index in list =" + index);
             PicturePage nextPage = pages.get(index+1);

             nextPage.setSelected(0);
             EventBus.getDefault().post(new PictureNextLoadSuccessEvent(nextPage));
             return;
         }

        // иначе заказываем новую
        executorService.submit(new LoadNextPageTask(mPicturePage.numberNext));
    }

    // ищем предыдущую страницу для заказанной страницы
    public void tryPrevPage(PicturePage mPicturePage){
        // ищем в пуле страниц следующую
        int index = pages.indexOf(mPicturePage);
         if(index!=-1 && index>0){
             // todo бросить сообщение с картинкой
             Log.d(Constants.LOG_TAG, " ------  tryPrevPage FOUND PAGE! index in list =" + index);
             PicturePage prevPage = pages.get(index-1);

             prevPage.setSelected(prevPage.pictures.size()-1);
             EventBus.getDefault().post(new PicturePrevLoadSuccessEvent(prevPage));
             return;
         }

        // иначе заказываем новую
        executorService.submit(new LoadPrevPageTask(mPicturePage.numberPrev));
    }

    // ------------------------ Async Calls --------------------
    public void asyncLoadPage(int num) {
        executorService.submit(new LoadOnePage(num));
    }

    private class LoadOnePage implements Runnable {
        private final int pageNum;

        private LoadOnePage(int pageNum) {
            this.pageNum = pageNum;
        }

        @Override
        public void run() {
            if (checkNetwork()) return;
            if (pageNum >= PAGE_MIN && pageNum <= PAGE_MAX) {
                PicturePage page = Client.getPage(pageNum);
                if (page == null) return;
                PAGE_MAX = page.totalPageAmount;
                int correctScroll = addPage(page);
                EventBus.getDefault().postSticky(new PageLoadEvent(page, correctScroll));
            }
        }
    }

    // ------------------------ Async Calls Next Page for Next Picture --------------------
    private class LoadNextPageTask implements Runnable {
        private final int pageNum;
        public LoadNextPageTask(int pageNum) {
            this.pageNum = pageNum;
        }

        @Override
        public void run() {
            if (checkNetwork()) return;
            if (pageNum >= PAGE_MIN && pageNum <= PAGE_MAX) {
                PicturePage page = Client.getPage(pageNum);
                if (page == null) return;

                PAGE_MAX = page.totalPageAmount;

                int correctScroll = addPage(page);
                EventBus.getDefault().postSticky(new PageLoadEvent(page, correctScroll)); // for grid
                EventBus.getDefault().postSticky(new PictureNextLoadSuccessEvent(page)); // for viewer
            }
        }
    }

    private class LoadPrevPageTask implements Runnable {
        private final int pageNum;
        public LoadPrevPageTask(int pageNum) {
            this.pageNum = pageNum;
        }

        @Override
        public void run() {
            if (checkNetwork()) return;
            if (pageNum >= PAGE_MIN && pageNum <= PAGE_MAX) {
                PicturePage page = Client.getPage(pageNum);
                if (page == null) return;

                PAGE_MAX = page.totalPageAmount;
                int correctScroll = addPage(page);

                EventBus.getDefault().postSticky(new PageLoadEvent(page, correctScroll)); // for grid
                EventBus.getDefault().postSticky(new PicturePrevLoadSuccessEvent(page)); // for viewer
            }
        }
    }
}