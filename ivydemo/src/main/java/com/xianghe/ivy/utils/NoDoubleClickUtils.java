package com.xianghe.ivy.utils;

/**
 * author:  ycl
 * date:  2018/11/21 15:00
 * desc:
 */
public class NoDoubleClickUtils {
    private static long lastClickTime = 0;

    public synchronized static void isDoubleClick(long SPACE_TIME, Runnable okRunnable, Runnable noRunnable) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime > SPACE_TIME) {
            lastClickTime = currentTime;
            okRunnable.run();
        } else {
            noRunnable.run();
        }
    }

    public synchronized static void isDoubleClick(long SPACE_TIME, Runnable okRunnable) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime > SPACE_TIME) {
            lastClickTime = currentTime;
            okRunnable.run();
        }
    }

    public synchronized static void isDoubleClick(Runnable okRunnable) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime > 1000) {
            lastClickTime = currentTime;
            okRunnable.run();
        }
    }

}
