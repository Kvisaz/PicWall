package com.arellomobile.picwall;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.arellomobile.picwall.events.DebugMessageEvent;
import com.arellomobile.picwall.events.LogEvent;
import com.arellomobile.picwall.events.ViewRequestPage;
import com.arellomobile.picwall.presenter.PictureGridPresenter;
import com.arellomobile.picwall.view.DebugView;
import com.arellomobile.picwall.view.PictureGridView;
import com.arellomobile.picwall.view.PictureView;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {

    @Inject
    PictureGridPresenter pictureGridPresenter;

    private PictureGridView pictureGridView;
    private PictureView pictureView;

//    private DebugView debugView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



//        pictureGridPresenter = new PictureGridPresenter(this);
        App.getComponent().inject(this);

        View rootView = getWindow().getDecorView();
//        debugView = new DebugView(rootView);
        pictureGridView = new PictureGridView(rootView);
        pictureView = new PictureView(rootView);

    }


    // ----------------------- Event Init Handlers --------------------
    @Override
    protected void onStart() {
        super.onStart();
//        EventBus.getDefault().register(this);
        pictureGridPresenter.registerEventBus();
        pictureGridView.registerEventBus();
        pictureView.registerEventBus();
//        debugView.registerEventBus();

        EventBus.getDefault().post(new LogEvent("--------- Start NEW! -----------"));
        EventBus.getDefault().post(new ViewRequestPage(3));
    }

    @Override
    protected void onStop() {
        super.onStop();
//        EventBus.getDefault().unregister(this);
        pictureGridPresenter.unregisterEventBus();
        pictureGridView.unregisterEventBus();
        pictureView.unregisterEventBus();
//        debugView.unregisterEventBus();
    }

}
