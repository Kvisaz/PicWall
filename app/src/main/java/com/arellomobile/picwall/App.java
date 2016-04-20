package com.arellomobile.picwall;

import android.app.Application;

import com.arellomobile.picwall.dagger.AppComponent;
import com.arellomobile.picwall.dagger.AppModule;
import com.arellomobile.picwall.dagger.DaggerAppComponent;
import com.arellomobile.picwall.dagger.ImageLoaderModule;
import com.arellomobile.picwall.dagger.PictureGridPresenterModule;

public class App extends Application {
    private static AppComponent component;
    public static AppComponent getComponent(){
        return component;
    }
    @Override
    public void onCreate(){
        super.onCreate();
        component = buildComponent();
        Constants.getResources(this);
    }

    protected AppComponent buildComponent() {
        return DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .imageLoaderModule(new ImageLoaderModule())
                .pictureGridPresenterModule(new PictureGridPresenterModule())
                .build();
    }
}
