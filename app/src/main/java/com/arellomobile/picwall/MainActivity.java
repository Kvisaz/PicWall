package com.arellomobile.picwall;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.arellomobile.picwall.events.ShowPictureEvent;
import com.arellomobile.picwall.view.GridView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {

    @Inject
    Presenter presenter;

    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        App.getComponent().inject(this);
        Constants.getResourcesForOrientation(this);

        View rootView = getWindow().getDecorView();
        gridView = new GridView(rootView);


        if(Constants.IS_TABLET){
     //       Log.d(Constants.LOG_TAG,"------ 0 -- IS_TABLET ---- 0 ------- ");
            DetailFragment fragment = new DetailFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_box_for_picture_viewer,fragment)
                    .commit();
        }
        /*else{
             Log.d(Constants.LOG_TAG,"------- .MainActivity ----------- ");
           Log.d(Constants.LOG_TAG,"------- IS_PHONE ----------- ");
        }*/

    }


    // ----------------------- Event Init --------------------
    @Override
    protected void onStart() {
        super.onStart();
        gridView.registerEventBus();
        if(!Constants.IS_TABLET){
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        gridView.unregisterEventBus();
        if(!Constants.IS_TABLET){
            EventBus.getDefault().unregister(this);
        }
    }

    // ----------------------- Event Handlers --------------------
    @Subscribe(sticky = false, threadMode = ThreadMode.MAIN)
    public void onLoadSelectedPicture(ShowPictureEvent event) {
       if(!Constants.IS_TABLET){
           Context activityContext = this;
           Intent intent = new Intent(activityContext,DetailActivity.class);
           activityContext.startActivity(intent);
       }
    }
}