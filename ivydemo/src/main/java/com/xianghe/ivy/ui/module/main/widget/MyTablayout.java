package com.xianghe.ivy.ui.module.main.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.google.android.material.tabs.TabLayout;

public class MyTablayout extends TabLayout {

    private boolean mCanScroll = false;

    public MyTablayout(Context context) {
        super(context);
    }

    public MyTablayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyTablayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return mCanScroll && super.onTouchEvent(ev);
    }
}
