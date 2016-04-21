package com.arellomobile.picwall.view.pager;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arellomobile.picwall.App;
import com.arellomobile.picwall.Constants;
import com.arellomobile.picwall.R;
import com.arellomobile.picwall.imageloader.ImageLoader;
import com.arellomobile.picwall.model.PictureItem;
import com.arellomobile.picwall.view.progress.ProgressIndicator;
import com.arellomobile.picwall.view.progress.SignalProgressIndicator;

import javax.inject.Inject;

public class PictureFragment extends Fragment {
    @Inject
    ImageLoader imageLoader;

    private String pictureUrl;
    private final static String PIC_URL_TAG = "imageUrl";

    public static PictureFragment getInstance(String pictureUrl) {
        PictureFragment fragment = new PictureFragment();
        Bundle args = new Bundle();
        args.putString(PIC_URL_TAG, pictureUrl);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pictureUrl = getArguments().getString(PIC_URL_TAG);
        App.getComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.picture_viewer_item, container, false);

        ImageView imageView = (ImageView) view.findViewById(R.id.picture_viewer_imageview);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.picture_viewer_progress_bar);
        TextView progressTextView = (TextView) view.findViewById(R.id.picture_viewer_progress_text);

        ProgressIndicator progressIndicator = new ProgressIndicator(progressBar, progressTextView);
        progressIndicator.setMax(100);

        imageView.setImageBitmap(null);
        int picMaxWidth = Constants.PICTURE_VIEWER_MAX_WIDTH;
        Log.d(Constants.LOG_TAG,"--- imageView.getWidth() = "+picMaxWidth);
        if (pictureUrl != null) {
            imageLoader.load(pictureUrl, imageView, picMaxWidth, false, progressIndicator);
        }


        return view;
    }

}
