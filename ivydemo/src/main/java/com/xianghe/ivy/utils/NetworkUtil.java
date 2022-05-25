package com.xianghe.ivy.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.text.TextUtils;


/**
 * 网络工具类
 */
public class NetworkUtil {
    private static final String TAG = "NetworkUtil";

    /**
     * WIFI网络
     */

    public static final int NETTYPE_WIFI = 0;
    /**
     * CMWAP网络
     */
    public static final int NETTYPE_MOBILE = 1;

    /**
     * 无网络
     */
    public static final int NETTYPE_NONE = 2;

    /**
     * 初始　未检测网络
     */
    public static final int NETTYPE_INIT = 3;

    /**
     * 上一个网络状态
     */
    public static int sLastNetStatus = NETTYPE_INIT;


    /**
     * 检测网络是否可用
     *
     * @return isConnected
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 获取当前网络类型
     *
     * @return 0：WIFI网络 1：移动网络 2：没有网络
     */
    public static int getNetworkType(Context context) {
        int netType = NETTYPE_NONE;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            String extraInfo = networkInfo.getExtraInfo();
            if (!TextUtils.isEmpty(extraInfo)) {
                if (extraInfo.toLowerCase().equals("cmnet")) {
                    netType = NETTYPE_MOBILE;
                } else {
                    netType = NETTYPE_MOBILE;
                }
            }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = NETTYPE_WIFI;
        }
        return netType;
    }


    public static boolean isWifiAvailable(Context context) {
        if (getNetworkType(context) == NETTYPE_WIFI) {
            return true;
        }
        return false;
    }
}
