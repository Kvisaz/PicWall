package com.arellomobile.picwall.view;


import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.arellomobile.picwall.App;
import com.arellomobile.picwall.Constants;
import com.arellomobile.picwall.R;
import com.arellomobile.picwall.events.LoadSelectedPictureEvent;
import com.arellomobile.picwall.events.NeedNextPictureEvent;
import com.arellomobile.picwall.events.NeedPrevPictureEvent;
import com.arellomobile.picwall.events.PictureScrollNextEvent;
import com.arellomobile.picwall.events.PictureScrollPrevEvent;
import com.arellomobile.picwall.model.PictureItem;
import com.arellomobile.picwall.model.PicturePage;
import com.arellomobile.picwall.presenter.Presenter;
import com.arellomobile.picwall.view.pager.OnPictureViewerScroller;
import com.arellomobile.picwall.view.pager.PictureFragmentAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

public class PictureView {
    @Inject
    Context context;
    @Inject
    Presenter presenter;

    private PicturePage picturePage;
    private ViewPager viewPager;
    private PictureFragmentAdapter pictureFragmentAdapter;

    private PictureItem prevPicture;
    private PictureItem currentPicture;
    private PictureItem nextPicture;


    public PictureView(View rootView, FragmentManager fm) {
        App.getComponent().inject(this);

        viewPager = (ViewPager) rootView.findViewById(R.id.picture_view_pager);
        pictureFragmentAdapter = new PictureFragmentAdapter(fm);
        viewPager.setAdapter(pictureFragmentAdapter);
        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(new OnPictureViewerScroller());
    }


    public void loadPicturePage(PicturePage page) {
        picturePage = page;
        pictureFragmentAdapter.clear();

        int selectedInPage = picturePage.getSelected();
        int currentItem = 0;
        if (selectedInPage > 0) {
            prevPicture = picturePage.pictures.get(selectedInPage - 1);
            pictureFragmentAdapter.addPrev(prevPicture);
            currentItem++;
        }

        currentPicture = picturePage.pictures.get(selectedInPage);
        pictureFragmentAdapter.add(currentPicture);
        viewPager.setCurrentItem(currentItem);

        if (selectedInPage < picturePage.getNumberOfPictures() - 1) {
            nextPicture = picturePage.pictures.get(selectedInPage + 1);
            pictureFragmentAdapter.addNext(nextPicture);
        }
    }

    // ------------------------ load new picture ----------------
    public void loadNewPicture(PicturePage page) {
        pictureFragmentAdapter.clear();
        // 1. показываем текущую картинку
        pictureFragmentAdapter.add(page.getSelectedPicture());
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

        loadPicturePage(event.page);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void needNextPicture(NeedNextPictureEvent event) {
        Log.d(Constants.LOG_TAG, " ------ needNextPicture --- ");
//        presenter.scrollNextPicture();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void needPrevPicture(NeedPrevPictureEvent event) {
        Log.d(Constants.LOG_TAG, " ------ needPrevPicture --- ");
//        presenter.scrollPrevPicture();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getNextPictureScroll(PictureScrollNextEvent event){
        pictureFragmentAdapter.add(event.next);
        viewPager.setCurrentItem(1);
        pictureFragmentAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getPrevPictureScroll(PictureScrollPrevEvent event){
        pictureFragmentAdapter.addPrev(event.prev);
        viewPager.setCurrentItem(1);
        pictureFragmentAdapter.notifyDataSetChanged();
    }

}