package com.arellomobile.picwall.dagger;

import android.content.Context;

import com.arellomobile.picwall.presenter.Presenter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class PictureGridPresenterModule {
    Presenter presenter;

    @Provides
    @Singleton
    Presenter providesPresenter(Context context){
        return new Presenter(context);
    }
}
