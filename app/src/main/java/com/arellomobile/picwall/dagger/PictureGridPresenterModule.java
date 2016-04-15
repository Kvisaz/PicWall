package com.arellomobile.picwall.dagger;

import android.content.Context;

import com.arellomobile.picwall.presenter.PictureGridPresenter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class PictureGridPresenterModule {
    PictureGridPresenter presenter;

    @Provides
    @Singleton
    PictureGridPresenter providesPresenter(Context context){
        return new PictureGridPresenter(context);
    }
}
