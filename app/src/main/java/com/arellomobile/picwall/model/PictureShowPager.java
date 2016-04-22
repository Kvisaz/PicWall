package com.arellomobile.picwall.model;

import android.util.Log;

import com.arellomobile.picwall.Constants;

public class PictureShowPager {
    public PictureItem prev;
    public PictureItem current;
    public PictureItem next;

    public boolean isSame(final PictureShowPager other) {
        if(current==null){
            Log.d(Constants.LOG_TAG,"---current==null in PictureShowPager");
            return false;
        }
        return current.equals(other.current);
    }

    public boolean isNext(final PictureShowPager other) {
        if(next==null) {
            Log.d(Constants.LOG_TAG,"---next==null in PictureShowPager");
            return false;
        }
        return next.equals(other.current);
    }

    public boolean isPrev(final PictureShowPager other) {
        if(prev==null) {
            Log.d(Constants.LOG_TAG,"---prev==null in PictureShowPager");
            return false;
        }
        return prev.equals(other.current);
    }

    public void add(PictureItem otherNext){
        prev = current;
        current = next;
        next = otherNext;
    }

    public void addPrev(PictureItem otherPrev){
        next = current;
        current = prev;
        prev = otherPrev;
    }

    public void loadFrom(final PictureShowPager other){
        prev = other.prev;
        current = other.current;
        next = other.next;
    }

}
