package com.wwwjf.base.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;


/**
 * toast ic_warn
 */
public class ToastUtil {

    /**
     * 提示信息
     *
     * @param context 上下文
     * @param message 消息
     */
    public static void show(Context context, String message) {
        Toasty.normal(context, message).show();
    }

    public static void showCenter(Context context, String message) {
        Toast toast = Toasty.normal(context, message);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * 普通toast提示，默认显示时长short
     *
     * @param context
     * @param text
     * @return
     */
    public static Toast makeText(Context context, CharSequence text) {
        Toasty.Config.getInstance().setTextSize(12).apply();
        return Toasty.normal(context, text, Toasty.LENGTH_SHORT);
    }

    /**
     * 普通toast提示，自己定义显示时长
     *
     * @param context  上下文
     * @param text     文字
     * @param duration 时长
     */
    public static void show(Context context, CharSequence text, int duration) {
        Toasty.normal(context, text, duration).show();
    }
}
