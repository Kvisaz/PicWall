package com.arellomobile.picwall.view;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.arellomobile.picwall.R;
import com.arellomobile.picwall.events.DebugMessageEvent;

public class DebugView {
    private final TextView textView;

    public DebugView(View rootView){
        textView = (TextView)rootView.findViewById(R.id.debugTextView);
    }

    public void show(String message){
        textView.setText(message);
    }

    public void clear(){
        textView.setText("");
    }

    // ------------------------ EventBus register --------------
    public void registerEventBus(){
        EventBus.getDefault().register(this);
    }
    public void unregisterEventBus(){
        EventBus.getDefault().unregister(this);
    }

    // ------------------------ EventBus handlers --------------
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onViewSenderNumber(DebugMessageEvent event){
        show(event.message);
    }


}
