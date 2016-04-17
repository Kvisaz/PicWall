package com.arellomobile.picwall;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.arellomobile.picwall.events.LogEvent;
import com.arellomobile.picwall.events.ViewRequestPage;
import com.arellomobile.picwall.presenter.PictureGridPresenter;
import com.arellomobile.picwall.view.GridView;
import com.arellomobile.picwall.view.PictureView;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {

    @Inject
    PictureGridPresenter pictureGridPresenter;

    private GridView gridView;
    private PictureView pictureView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //  pictureGridPresenter = new PictureGridPresenter(this);
        App.getComponent().inject(this);

        View rootView = getWindow().getDecorView();
        gridView = new GridView(rootView);
        pictureView = new PictureView(rootView);

    }


    // ----------------------- Event Init Handlers --------------------
    @Override
    protected void onStart() {
        super.onStart();
        pictureGridPresenter.registerEventBus();
        gridView.registerEventBus();
        pictureView.registerEventBus();

        EventBus.getDefault().post(new LogEvent("--------- Start NEW! -----------"));
        EventBus.getDefault().post(new ViewRequestPage(1));
    }

    @Override
    protected void onStop() {
        super.onStop();
        pictureGridPresenter.unregisterEventBus();
        gridView.unregisterEventBus();
        pictureView.unregisterEventBus();

    }

}
