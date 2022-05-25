package com.xianghe.ivy.manager;

import android.view.View;

import java.util.Calendar;

public abstract class NoDoubleClickListenerManager implements View.OnClickListener {

    private int MIN_CLICK_DELAY_TIME = 500;
    private long lastClickTime = 0;


    public NoDoubleClickListenerManager(int limitTime){
        MIN_CLICK_DELAY_TIME = limitTime;
    }

    public NoDoubleClickListenerManager(){

    }

    @Override
    public void onClick(View v) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            onNoDoubleClick(v);
        }
    }

    public abstract void onNoDoubleClick(View v);
}
