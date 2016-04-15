package com.arellomobile.picwall.dagger;

import android.app.Application;
import android.app.WallpaperManager;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    Application mApplication;

    public AppModule(Application application) {
        mApplication = application;
    }

    @Provides
    @Singleton
    Context providesApplication() {
        return mApplication;
    }

}
