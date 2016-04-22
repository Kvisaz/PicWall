package com.arellomobile.picwall.view;


import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.arellomobile.picwall.App;
import com.arellomobile.picwall.Constants;
import com.arellomobile.picwall.Presenter;
import com.arellomobile.picwall.R;
import com.arellomobile.picwall.events.NeedNextPictureEvent;
import com.arellomobile.picwall.events.NeedPrevPictureEvent;
import com.arellomobile.picwall.events.PictureNextLoadSuccessEvent;
import com.arellomobile.picwall.events.PicturePrevLoadSuccessEvent;
import com.arellomobile.picwall.events.ShowPictureEvent;
import com.arellomobile.picwall.model.PictureItem;
import com.arellomobile.picwall.model.PicturePage;
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

    private PicturePage mPicturePage;
    private PictureItem mPictureItem;
    private ViewPager mViewPager;
    private PictureFragmentAdapter mPictureFragmentAdapter;

    public PictureView(View rootView, FragmentManager fm) {
        App.getComponent().inject(this);

        mViewPager = (ViewPager) rootView.findViewById(R.id.picture_view_pager);
        mPictureFragmentAdapter = new PictureFragmentAdapter(fm);
        mViewPager.setAdapter(mPictureFragmentAdapter);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.addOnPageChangeListener(new OnPictureViewerScroller());

    }

    // ------------------------ load new picture ----------------
    public void showNew(PicturePage page) {
        // 1. показываем текущую картинку
        this.mPicturePage = page;
        this.mPictureItem = page.getSelectedPicture();
        mPictureFragmentAdapter.set(mPictureItem);
        mViewPager.setCurrentItem(1);
    }

    public void addNext(PicturePage page) {
        this.mPicturePage = page;
        this.mPictureItem = page.getSelectedPicture();
        mViewPager.setCurrentItem(1);
        mPictureFragmentAdapter.addNext(mPicturePage.getSelectedPicture());
        Log.d(Constants.LOG_TAG,"--- add next ----");
        Log.d(Constants.LOG_TAG,"--- selected ----"+mPicturePage.getSelected());
    }

    public void addPrev(PicturePage page) {
        this.mPicturePage = page;
        this.mPictureItem = page.getSelectedPicture();
        mViewPager.setCurrentItem(1);
        mPictureFragmentAdapter.addPrev(mPicturePage.getSelectedPicture());
        Log.d(Constants.LOG_TAG,"--- add prev ----");
        Log.d(Constants.LOG_TAG,"--- selected ----"+mPicturePage.getSelected());
    }



    // ------------------------ EventBus register --------------
    public void registerEventBus() {
        EventBus.getDefault().register(this);
    }

    public void unregisterEventBus() {
        EventBus.getDefault().unregister(this);
    }

    // ------------------------ EventBus handlers --------------
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onShow(ShowPictureEvent event){
        Log.d(Constants.LOG_TAG,"--- ShowPictureEvent ---");
        Log.d(Constants.LOG_TAG,"--- currentPage ---"+event.currentPage.numberCurrent);
        Log.d(Constants.LOG_TAG,"--- pictureItem ---"+event.pictureItem.urlFullImage);
        showNew(event.currentPage);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNeedNextPicture(NeedNextPictureEvent event){
        int selected = mPicturePage.getSelected();
        int max = mPicturePage.pictures.size()-1;

        if(selected<max){
            selected++;
            mPicturePage.setSelected(selected);
            addNext(mPicturePage);
        }else if(mPicturePage.numberNext==0){
            // Сообщить что страница в принципе последняя
            mPictureFragmentAdapter.setFinalPage();
        }
        else{
            // заказать у презентера следующую страницу если есть
            // повесить ответ на onLoadNextPicture
            presenter.tryNextPage(mPicturePage);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNextPictureSuccess(PictureNextLoadSuccessEvent event){
        addNext(event.page);
        Log.d(Constants.LOG_TAG,"--- add next ----");
        Log.d(Constants.LOG_TAG,"--- selected ----"+event.page.getSelected());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNeedPrevPicture(NeedPrevPictureEvent event){
        int selected = mPicturePage.getSelected();
        // предыдущая картинка на этой странице
        if(selected>0){
            selected--;
            mPicturePage.setSelected(selected);
            addPrev(mPicturePage);
        }else if(mPicturePage.numberPrev==0){
            // Сообщить что страница в принципе первая
            mPictureFragmentAdapter.setStartPage();

        }
        else{
            // заказать у презентера следующую страницу если есть
            // повесить ответ на onLoadNextPicture
            presenter.tryPrevPage(mPicturePage);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPrevPictureSuccess(PicturePrevLoadSuccessEvent event){
        event.page.setSelected(event.page.pictures.size()-1);
        addPrev(event.page);
        Log.d(Constants.LOG_TAG,"--- add onPrevPictureSuccess ----");
        Log.d(Constants.LOG_TAG,"--- selected ----"+event.page.getSelected());
    }



}