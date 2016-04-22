package com.arellomobile.picwall;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arellomobile.picwall.view.PictureView;

public class DetailFragment extends Fragment {

    private PictureView pictureView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.picture_pager,container,false);
        pictureView = new PictureView(rootView,getActivity().getSupportFragmentManager());
        return  rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        pictureView.registerEventBus();
    }

    @Override
    public void onStop() {
        super.onStop();
        pictureView.unregisterEventBus();
    }
}
