package com.arellomobile.picwall.imageloader;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/*
*    Memory Cache utility class for ImageLoader
*
*    //  следует реализовать
* */
public class MemoryCache {
    private static final String TAG = "MemoryCache";
    private static final int SIZE = 20;

    //Last argument true for LRU ordering
    private Map<String, Bitmap> cache = Collections.synchronizedMap(
            new LinkedHashMap<String, Bitmap>(SIZE, 1.5f, true));
    //current allocated size
    private long size = 0;

    //max memory cache folder used to download images in bytes
    private long limit = 1000000;

    public void setLimit(long new_limit) {

        limit = new_limit;
        Log.i(TAG, "MemoryCache will use up to " + limit / 1024. / 1024. + "MB");
    }


    public Bitmap get(String url) {
        try {
            if (!cache.containsKey(url))
                return null;

            return cache.get(url);

        } catch (NullPointerException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void put(String url, Bitmap freshBitmap) {
        try {
            if (cache.containsKey(url))
                size -= getSizeInBytes(cache.get(url));
            cache.put(url, freshBitmap);
            size += getSizeInBytes(freshBitmap);
            checkSize();
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    public void clear() {
        try {
            // Clear cache
            cache.clear();
            size = 0;
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    // --------------------- private -----------

    long getSizeInBytes(Bitmap bitmap) {
        if (bitmap == null)
            return 0;
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    private void checkSize() {
        synchronized (cache) {
            if (size > limit) {
//                Log.i(TAG, "cache size=" + size + " length=" + cache.size());
                //least recently accessed item will be the first one iterated
                Iterator<Entry<String, Bitmap>> iter = cache.entrySet().iterator();

                while (iter.hasNext()) {
                    Entry<String, Bitmap> entry = iter.next();
                    size -= getSizeInBytes(entry.getValue());
                    iter.remove();
                    if (size <= limit)
                        break;
                }
//                Log.i(TAG, "remove some elements. New size " + cache.size());
            }
        }
    }
}
