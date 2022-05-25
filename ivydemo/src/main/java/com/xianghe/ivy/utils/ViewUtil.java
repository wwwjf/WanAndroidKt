package com.xianghe.ivy.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class ViewUtil {
    /**
     * dp -> px
     *
     * @param ctx context
     * @param dp  value dp
     * @return value px
     */
    public static int dp2px(Context ctx, int dp) {
        float scale = ctx.getApplicationContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    /**
     * dp -> px
     *
     * @param ctx context
     * @param dp  value dp
     * @return value px
     */
    public static int dp2px(Context ctx, float dp) {
        float scale = ctx.getApplicationContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    /**
     * px -> dp
     *
     * @param ctx context
     * @param px  value px
     * @return value dp
     */
    public static int px2dp(Context ctx, float px) {
        float scale = ctx.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    /**
     * sp -> px
     *
     * @param ctx context
     * @param sp  value sp
     * @return value px
     */
    public static int sp2px(Context ctx, float sp) {
        float fontScale = ctx.getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * fontScale + 0.5f);
    }

    /**
     * px -> sp
     *
     * @param ctx context
     * @param px  value px
     * @return value sp
     */
    private int px2sp(Context ctx, float px) {
        float fontScale = ctx.getResources().getDisplayMetrics().scaledDensity;
        return (int) (px / fontScale + 0.5f);
    }

    /**
     * 获取屏幕的宽度px
     *
     * @param context 上下文
     * @return 屏幕宽px
     */
    public static int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();// 创建了一张白纸
        windowManager.getDefaultDisplay().getMetrics(outMetrics);// 给白纸设置宽高
        return outMetrics.widthPixels;
    }

    /**
     * 获取屏幕的高度px
     *
     * @param context 上下文
     * @return 屏幕高px
     */
    public static int getScreenHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();// 创建了一张白纸
        windowManager.getDefaultDisplay().getMetrics(outMetrics);// 给白纸设置宽高
        return outMetrics.heightPixels;
    }
}
