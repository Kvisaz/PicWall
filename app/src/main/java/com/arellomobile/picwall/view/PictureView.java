package com.arellomobile.picwall.view;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arellomobile.picwall.App;
import com.arellomobile.picwall.Constants;
import com.arellomobile.picwall.R;
import com.arellomobile.picwall.events.LoadSelectedPictureEvent;
import com.arellomobile.picwall.imageloader.ImageLoader;
import com.arellomobile.picwall.model.PictureItem;
import com.arellomobile.picwall.model.PicturePage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.w3c.dom.Text;

import javax.inject.Inject;

public class PictureView {
    @Inject
    public ImageLoader imageLoader;
    private ImageView imageView;
    private ProgressIndicator progressIndicator;

    public PictureView(View rootView) {
        App.getComponent().injectImageLoader(this);

        imageView = (ImageView) rootView.findViewById(R.id.big_picture_imageview);

        ProgressBar progressBar = (ProgressBar)rootView.findViewById(R.id.big_picture_progress_bar);
        TextView progressText = (TextView) rootView.findViewById(R.id.big_picture_progress_text);
        progressIndicator = new ProgressIndicator(progressBar,progressText);
        progressIndicator.setMax(100);
    }

    public void load(String imageUrl) {
        int PICTURE_MAX_WIDTH = imageView.getWidth();
        imageView.setImageBitmap(null);
        imageLoader.load(imageUrl, imageView, PICTURE_MAX_WIDTH, false, progressIndicator);
    }


    // ------------------------ EventBus register --------------
    public void registerEventBus() {
        EventBus.getDefault().register(this);
    }

    public void unregisterEventBus() {
        EventBus.getDefault().unregister(this);
    }

    // ------------------------ EventBus handlers --------------
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onLoadSelectedPicture(LoadSelectedPictureEvent event) {
        PictureItem pictureItem = event.page.getSelectedPicture();
        load(pictureItem.urlFullImage);
    }

}
