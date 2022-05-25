package com.xianghe.ivy.weight.basepopuwindow.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by yanchunlan on 2016/1/14.
 * 显示键盘d工具类
 */
public class InputMethodUtils {
    /** 显示软键盘 */
    public static void showInputMethod(View view) {
        InputMethodManager imm = (InputMethodManager)
                view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    /** 强制显示软键盘 */
    public static void showInputMethod(Context context) {
        InputMethodManager imm = (InputMethodManager) context
            .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /** 多少时间后显示软键盘 */
    public static void showInputMethod(final View view, long delayMillis) {
        if (view==null)return;
        // 显示输入法
        view.postDelayed(new Runnable() {

            @Override
            public void run() {
                InputMethodUtils.showInputMethod(view);
            }
        }, delayMillis);
    }


    /**
     * 隐藏软键盘
     * @param act
     */
    public static void hideSoftInput(Activity act) {
        try {
            if (act == null) {
                return;
            }
            final View v = act.getWindow().peekDecorView();
            if (v != null && v.getWindowToken() != null) {
                InputMethodManager imm = (InputMethodManager) act
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                // method 1
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                // method 2
                // imm.hideSoftInputFromWindow(act.getCurrentFocus().getWindowToken(),
                // InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } catch (Exception e) {
        }
    }

    /**
     * 显示软键盘
     * @param context
     */
    public static void showSoftInput(Context context) {
        try {
            InputMethodManager m = (InputMethodManager) context
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            m.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);  //HIDE_NOT_ALWAYS
        } catch (Exception e) {
        }
    }
}
