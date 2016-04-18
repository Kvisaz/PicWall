package com.arellomobile.picwall.view.bigpicture;

import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;


public class SnapScrollTouchListener implements RecyclerView.OnItemTouchListener {
    private float lastX;
    private boolean isSwipeLeft;
    private boolean isSwipeRight;
    private byte scrollStep;

    private int currentPosition;

    public void setCurrentPosition(int pos){
        currentPosition = pos;
    }

    public int getCurrentPosition(){
        return currentPosition;
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        return true;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        switch (e.getAction()) {
            // touches on
            case MotionEvent.ACTION_DOWN: {
                lastX = e.getX();
                break;
            }
            // touches off
            case MotionEvent.ACTION_UP: {
                float currentX = e.getX();
                if(lastX < currentX) scrollStep = -1;
                else if (lastX > currentX) scrollStep = +1;
                rv.smoothScrollToPosition(currentPosition+scrollStep);
            }
        }
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }
}
