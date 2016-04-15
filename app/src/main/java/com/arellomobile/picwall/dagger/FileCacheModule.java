package com.arellomobile.picwall.dagger;

import android.content.Context;
import android.support.annotation.NonNull;

import com.arellomobile.picwall.imageloader.FileCache;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class FileCacheModule {
    @Provides
    @NonNull
    @Singleton
    public FileCache provideFileCache(Context context){
        return new FileCache(context);
    }
}
