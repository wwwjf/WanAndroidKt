package com.xianghe.ivy.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.widget.Toast;

public class ToastUtil {

    public static void showToastCenter(Context context, String content) {
        Toast toast = Toast.makeText(context.getApplicationContext(), content, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void showToast(Context context, String content) {
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            Toast.makeText(context.getApplicationContext(), content, Toast.LENGTH_SHORT).show();
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context.getApplicationContext(), content, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
