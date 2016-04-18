package com.arellomobile.picwall.view.progress;

import android.widget.ProgressBar;
import android.widget.TextView;

import com.arellomobile.picwall.events.ProgressFullEvent;

import org.greenrobot.eventbus.EventBus;

/**
 *  Send message when set max
 */
public class SignalProgressIndicator extends ProgressIndicator {
    private int threshold;
    private boolean isSignaled;

    public SignalProgressIndicator(ProgressBar progressBar, TextView progressText) {
        super(progressBar, progressText);
    }

    @Override
    public void setProgress(int value) {
        super.setProgress(value);
        if(isSignaled == true && value < threshold){
            isSignaled = false;
        }
        else if(isSignaled == false && value>=threshold){
            EventBus.getDefault().post(new ProgressFullEvent());
            isSignaled = true;
        }
    }

    public void setThreshold(int threshold){
        this.threshold = threshold;
    }
}
