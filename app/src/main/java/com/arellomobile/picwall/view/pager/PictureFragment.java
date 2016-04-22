package com.arellomobile.picwall.view.pager;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.arellomobile.picwall.App;
import com.arellomobile.picwall.Constants;
import com.arellomobile.picwall.R;
import com.arellomobile.picwall.imageloader.ImageLoader;
import com.arellomobile.picwall.view.progress.ProgressIndicator;

import javax.inject.Inject;

public class PictureFragment extends Fragment {
    @Inject
    ImageLoader imageLoader;

    private String pictureUrl;
    private final static String PIC_URL_TAG = "imageUrl";

    public final static String START_PAGE_TAG = "0";
    public final static String FINAL_PAGE_TAG = "1";

    RelativeLayout splashLay;

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

        splashLay = (RelativeLayout)view.findViewById(R.id.picture_viewer_nopage_layout);
        ImageView imageView = (ImageView) view.findViewById(R.id.picture_viewer_imageview);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.picture_viewer_progress_bar);
        TextView progressTextView = (TextView) view.findViewById(R.id.picture_viewer_progress_text);

        ProgressIndicator progressIndicator = new ProgressIndicator(progressBar, progressTextView);
        progressIndicator.setMax(100);

        imageView.setImageBitmap(null);
        int picMaxWidth = Constants.PICTURE_VIEWER_MAX_WIDTH;
        Log.d(Constants.LOG_TAG,"--- imageView.getWidth() = "+picMaxWidth);

        if(START_PAGE_TAG.equals(pictureUrl)){
            showStartSplash(view);
        }
        else if(FINAL_PAGE_TAG.equals(pictureUrl)){
            showFinalSplash(view);
        }
        else if (pictureUrl != null) {
            splashLay.setVisibility(View.INVISIBLE);
            imageLoader.load(pictureUrl, imageView, picMaxWidth, false, progressIndicator);
        }
        else{
            splashLay.setVisibility(View.INVISIBLE);
            Log.d(Constants.LOG_TAG,"----------- no picture! no tags -------");
        }


        return view;
    }



    private void showStartSplash(View view) {
        splashLay = (RelativeLayout)view.findViewById(R.id.picture_viewer_nopage_layout);
        ImageView splashIcon = (ImageView) view.findViewById(R.id.picture_viewer_nopage_icon);
        TextView splashText = (TextView) view.findViewById(R.id.picture_viewer_nopage_text);

        splashIcon.setAdjustViewBounds(true);
        splashIcon.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.cat_start));
        splashText.setText(Constants.PICTURE_VIEWER_START_PAGE_TEXT);

        splashLay.setVisibility(View.VISIBLE);
    }

    private void showFinalSplash(View view) {

        ImageView splashIcon = (ImageView) view.findViewById(R.id.picture_viewer_nopage_icon);
        TextView splashText = (TextView) view.findViewById(R.id.picture_viewer_nopage_text);

        splashIcon.setAdjustViewBounds(true);
        splashIcon.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.cat_end));
        splashText.setText(Constants.PICTURE_VIEWER_FINAL_PAGE_TEXT);

        splashLay.setVisibility(View.VISIBLE);
    }

}
