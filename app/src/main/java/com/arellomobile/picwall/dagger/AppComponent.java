package com.arellomobile.picwall.dagger;

import com.arellomobile.picwall.MainActivity;
import com.arellomobile.picwall.imageloader.ImageLoader;
import com.arellomobile.picwall.view.GridView;
import com.arellomobile.picwall.view.PictureView;
import com.arellomobile.picwall.view.bigpicture.PictureAdapter;
import com.arellomobile.picwall.view.grid.PageAdapter;

import javax.inject.Singleton;

import dagger.Component;

@Component(dependencies = AppModule.class, modules = {PictureGridPresenterModule.class, ImageLoaderModule.class})
@Singleton
public interface AppComponent {
    ImageLoader imageloader();

    void inject(MainActivity activity);
    void inject(GridView gridView);

    void inject(PageAdapter pageAdapter);
    void inject(PictureAdapter pictureAdapter);

    void inject(PictureView pictureView);
}
