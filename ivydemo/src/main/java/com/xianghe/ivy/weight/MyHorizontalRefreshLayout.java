package com.xianghe.ivy.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import xiao.free.horizontalrefreshlayout.HorizontalRefreshLayout;

/**
 * java.lang.NullPointerException: Attempt to invoke virtual method 'android.view.ViewPropertyAnimator android.view.View.animate()'
 * on a null object reference at xiao.free.horizontalrefreshlayout.HorizontalRefreshLayout.smoothRelease(HorizontalRefreshLayout.java:307)
 *
 * {link #smoothRelease()} 私有方法改不了，只能在调用的地方都抓下异常。
 */
public class MyHorizontalRefreshLayout extends HorizontalRefreshLayout {
    public MyHorizontalRefreshLayout(Context context) {
        super(context);
    }

    public MyHorizontalRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyHorizontalRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            return super.onTouchEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onRefreshComplete() {
        try {
            super.onRefreshComplete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
