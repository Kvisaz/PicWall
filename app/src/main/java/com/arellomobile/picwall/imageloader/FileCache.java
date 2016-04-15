package com.arellomobile.picwall.imageloader;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.arellomobile.picwall.Constants;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import javax.inject.Inject;

/*
*    File Cache utility class for ImageLoader
*
* */

public class FileCache {
    private File cacheDir;

    public FileCache(Context context){
        cacheDir = context.getCacheDir();
    }

    public File getFile(String url){
        String filename = String.valueOf(url.hashCode());
        File f = new File(cacheDir,filename);
        return f;
    }

    /*
    *       Проверяем размер кэша. Если есть превышение - уменьшаем до (максимум - размер чанка)
    * */
    public int checkSizeAndClearOld(final int MAX_AMOUNT_FILES_IN_CACHE, final int CACHE_CHUNK_SIZE_FOR_CLEAR){
        int n = 0;
        File[] files = cacheDir.listFiles();
        if(files==null || files.length <= MAX_AMOUNT_FILES_IN_CACHE) return n;
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File lhs, File rhs) {
                return Long.valueOf(lhs.lastModified()).compareTo(rhs.lastModified());
            }
        });

        int newCacheSize = MAX_AMOUNT_FILES_IN_CACHE - CACHE_CHUNK_SIZE_FOR_CLEAR;
        int howMuchDelete = files.length-newCacheSize;
        n = 0;
        for(File f: files) {
            if(n>=howMuchDelete) break;
            f.delete();
            n++;
        }
        return  CACHE_CHUNK_SIZE_FOR_CLEAR;
    }

    public int clear(){
        int n = 0;
        File[] files = cacheDir.listFiles();
        if(files==null) return n;
        n = files.length;
        for(File f: files) f.delete();
        return  n;
    }

    public int count(){
        File[] files = cacheDir.listFiles();
        if(files==null) return 0;
        return cacheDir.listFiles().length;
    }
}
