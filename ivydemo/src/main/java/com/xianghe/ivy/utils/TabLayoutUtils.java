package com.xianghe.ivy.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.android.material.tabs.TabLayout;

/**
 * @Author: ycl
 * @Date: 2018/10/29 14:34
 * @Desc:
 */
public class TabLayoutUtils {



    /*8
      int left = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                10, Resources.getSystem().getDisplayMetrics());
        int right = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                10, Resources.getSystem().getDisplayMetrics());
     */
    public static void setIndicator(TabLayout tabLayout, int left, int right) {
        for (int i = 0; i < tabLayout.getChildCount(); i++) {
            View tabView = tabLayout.getChildAt(0);
            ViewGroup.LayoutParams layoutParams = tabView.getLayoutParams();
            if (layoutParams instanceof LinearLayout.LayoutParams) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layoutParams;
                params.leftMargin = left;
                params.rightMargin = right;
                tabView.setLayoutParams(params);
            } else if (layoutParams instanceof FrameLayout.LayoutParams) {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) layoutParams;
                params.leftMargin = left;
                params.rightMargin = right;
                tabView.setLayoutParams(params);
            }
        }
    }
}
