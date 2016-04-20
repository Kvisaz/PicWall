package com.arellomobile.picwall.dagger;

import com.arellomobile.picwall.MainActivity;
import com.arellomobile.picwall.imageloader.ImageLoader;
import com.arellomobile.picwall.view.GridView;
import com.arellomobile.picwall.view.PictureView;
import com.arellomobile.picwall.view.grid.PageAdapter;
import com.arellomobile.picwall.view.pager.PictureFragment;

import javax.inject.Singleton;

import dagger.Component;

@Component(dependencies = AppModule.class, modules = {PictureGridPresenterModule.class, ImageLoaderModule.class})
@Singleton
public interface AppComponent {
    ImageLoader imageloader();

    void inject(MainActivity activity);
    void inject(GridView gridView);

    void inject(PageAdapter pageAdapter);
    void inject(PictureFragment pictureFragment);

    void inject(PictureView pictureView);
}
