package com.xianghe.ivy.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.view.View;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.Utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * author:  ycl
 * date:  2019/1/18 11:09
 * desc:
 */
public class ActivityUtils {
    private static Activity findActivity(@NonNull Context context) {
        if (context instanceof Activity) {
            return (Activity) context;
        } else if (context instanceof ContextWrapper) {
            return findActivity(((ContextWrapper) context).getBaseContext());
        } else {
            return null;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static boolean assertNotDestroyed(@NonNull Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 判断activity 是否被摧毁
     *
     * @param view
     * @return
     */
    public static boolean assertNotDestroyed(View view) {
        Activity activity = findActivity(view.getContext());
        if (activity != null && assertNotDestroyed(activity)) {
            return true;
        } else {
            return false;
        }
    }

    public static Activity getLastSecondActivity() {
        LinkedList<Activity> activitys = (LinkedList<Activity>) com.blankj.utilcode.util.ActivityUtils.getActivityList();
        if (!activitys.isEmpty()) {
            int size = activitys.size();
            if (size >= 2) {
                final Activity lastSecondActivity = activitys.get(size - 2);
                if (lastSecondActivity != null) {
                    return lastSecondActivity;
                }
            }
        }
        return null;
    }
}
