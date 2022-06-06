package com.xianghe.ivy.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

/**
 * 作者: andy
 * 时间: 16-5-13
 * 描述:
 * 修订: 暂无
 */
public class DeviceUtils {

    @SuppressLint("NewApi")
    public static boolean checkPermission(Context context, String permission) {
        boolean result = false;
        if (Build.VERSION.SDK_INT >= 23) {
            if (context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                result = true;
            }
        } else {
            PackageManager pm = context.getPackageManager();

            if (pm.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
                result = true;
            }
        }
        return result;
    }

    /**
     * 物理地址加device id
     *
     * @param context
     * @return
     */
    public static String getDeviceInfo(Context context) {
        try {

            android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            String device_id = "";

            /*if (checkPermission(context, Manifest.permission.READ_PHONE_STATE)) {
                device_id = tm.getDeviceId();
            }*/

            android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            String mac = wifi.getConnectionInfo().getMacAddress();

            StringBuilder builder = new StringBuilder();
            builder.append(mac).append(":");

            if (TextUtils.isEmpty(device_id)) {
                device_id = mac;
            }

            if (TextUtils.isEmpty(device_id)) {
                device_id = android.provider.Settings.Secure.getString(context.getContentResolver(),
                        android.provider.Settings.Secure.ANDROID_ID);
            }

            builder.append(device_id);

            return EncriptUtils.MD5(builder.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }
}
