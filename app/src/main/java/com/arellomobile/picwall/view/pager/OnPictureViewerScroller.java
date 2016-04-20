package com.arellomobile.picwall.view.pager;

import android.support.v4.view.ViewPager;
import android.util.Log;

import com.arellomobile.picwall.Constants;
import com.arellomobile.picwall.events.NeedNextPictureEvent;
import com.arellomobile.picwall.events.NeedPrevPictureEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Builder on 20.04.2016.
 */
public class OnPictureViewerScroller implements ViewPager.OnPageChangeListener {
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPageSelected(int position) {
        if(position>1) {
            EventBus.getDefault().post(new NeedNextPictureEvent());
        }
        else if(position<1){
            EventBus.getDefault().post(new NeedPrevPictureEvent());
        }

    }


}
