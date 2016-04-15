package com.arellomobile.picwall.dagger;

import android.content.Context;
import android.support.annotation.NonNull;

import com.arellomobile.picwall.imageloader.ImageLoader;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ImageLoaderModule {
    @Provides
    @Singleton
    @NonNull
    public ImageLoader provideImageLoader(Context context){
        return new ImageLoader(context);
    }
}
