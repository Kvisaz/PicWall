package com.arellomobile.picwall.dagger;

import com.arellomobile.picwall.MainActivity;
import com.arellomobile.picwall.imageloader.ImageLoader;
import com.arellomobile.picwall.presenter.PictureGridPresenter;
import com.arellomobile.picwall.view.PictureGridView;
import com.arellomobile.picwall.view.PictureView;

import javax.inject.Singleton;

import dagger.Component;

@Component(dependencies = AppModule.class, modules = {PictureGridPresenterModule.class, ImageLoaderModule.class})
@Singleton
public interface AppComponent {
    ImageLoader imageloader();

    void inject(MainActivity activity);

    void injectImageLoader(PictureGridView pictureGridView);
    void injectImageLoader(PictureView pictureView);
}
