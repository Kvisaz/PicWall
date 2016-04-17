package com.arellomobile.picwall.presenter;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.arellomobile.picwall.Constants;

import com.arellomobile.picwall.events.LogEvent;

import com.arellomobile.picwall.events.ServerPicturePageEvent;
import com.arellomobile.picwall.events.ViewRequestPage;
import com.arellomobile.picwall.model.PictureItem;
import com.arellomobile.picwall.model.PicturePage;
import com.arellomobile.picwall.network.model.DesktopprPicture;
import com.arellomobile.picwall.network.model.DesktopprResponse;
import com.arellomobile.picwall.network.Client;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class PictureGridPresenter {
    private final Context context;


    public PictureGridPresenter(Context context){
           this.context = context;
    }



    // ------------------------ Check Network --------------------

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

   // ------------------------ EventBus register --------------
    public void registerEventBus(){
        EventBus.getDefault().register(this);
    }

    public void unregisterEventBus(){
        EventBus.getDefault().unregister(this);
    }

    // ------------------------ EventBus handlers --------------
    @Subscribe
    public void onViewRequestPage(ViewRequestPage event){
        Client.getPageAsync(event.pagenumber);
    }

    @Subscribe
    public void onLog(LogEvent event){
        Log.d(Constants.LOG_TAG, event.message);
    }


}
