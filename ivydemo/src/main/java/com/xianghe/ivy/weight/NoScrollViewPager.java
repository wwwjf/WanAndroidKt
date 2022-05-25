package com.xianghe.ivy.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

public class NoScrollViewPager extends ViewPager {

    private boolean isEnableScroll;

    public void setEnableScroll(boolean isEnableScroll) {
        this.isEnableScroll = isEnableScroll;
    }

    public NoScrollViewPager(Context context) {
        this(context, null);
    }

    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isEnableScroll) {
            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!isEnableScroll) {
            return false;
        }
        return super.onTouchEvent(ev);
    }
}
