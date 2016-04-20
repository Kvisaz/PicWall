package com.arellomobile.picwall;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.arellomobile.picwall.presenter.Presenter;
import com.arellomobile.picwall.view.GridView;
import com.arellomobile.picwall.view.PictureView;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {

    @Inject
    Presenter presenter;

    private GridView gridView;
    private PictureView pictureView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //  presenter = new Presenter(this);
        App.getComponent().inject(this);

        View rootView = getWindow().getDecorView();
        gridView = new GridView(rootView);
        pictureView = new PictureView(rootView,getSupportFragmentManager());

    }


    // ----------------------- Event Init Handlers --------------------
    @Override
    protected void onStart() {
        super.onStart();
        presenter.registerEventBus();
        gridView.registerEventBus();
        pictureView.registerEventBus();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unregisterEventBus();
        gridView.unregisterEventBus();
        pictureView.unregisterEventBus();

    }

}
