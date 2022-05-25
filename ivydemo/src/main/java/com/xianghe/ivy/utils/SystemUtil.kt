package com.xianghe.ivy.util

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.text.ClipboardManager
import android.text.TextUtils
import androidx.annotation.NonNull
import com.xianghe.ivy.app.IvyApp
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.io.Reader
import java.util.*




/**
 * Created by Ycl on 2017/6/19.
 */
object SystemUtil {

    fun getAppName( context: Context,pID: Int): String? {
        var processName: String? = null
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val l = am.getRunningAppProcesses()
        val i = l.iterator()
        while (i.hasNext()) {
            val info = i.next() as ActivityManager.RunningAppProcessInfo
            try {
                if (info.pid === pID) {
                    processName = info.processName
                    return processName
                }
            } catch (e: Exception) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }

        }
        return processName
    }
    /**
     * 获取进程号对应的进程名
     * @param pid 进程号
     * @return 进程名
     */
    fun getProcessName(pid: Int): String? {
        var reader: BufferedReader? = null
        try {
            reader = BufferedReader(FileReader("/proc/$pid/cmdline") as Reader?)
            var processName = reader!!.readLine()
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim({ it <= ' ' })
            }
            return processName
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        } finally {
            try {
                if (reader != null) {
                    reader!!.close()
                }
            } catch (exception: IOException) {
                exception.printStackTrace()
            }
        }
        return null
    }


    fun getVersionCode(context: Context): String {
        try {
            return getPackageInfo(context).versionCode.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }

    fun getVersionName(context: Context): String {
        try {
            return getPackageInfo(context).versionName.toString()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            return ""
        }
    }

    @Throws(PackageManager.NameNotFoundException::class)
    private fun getPackageInfo(context: Context): PackageInfo {
        return context.packageManager.getPackageInfo(context.packageName, 0)
    }

    /**
     * 获取友盟渠道号
     */
    fun getChannelValue(context: Context): String? {
        try {
            val packageManager = context.packageManager
            if (packageManager != null) {
                //注意此处为ApplicationInfo 而不是 ActivityInfo,因为友盟设置的meta-data是在application标签中，而不是某activity标签中，所以用ApplicationInfo
                val applicationInfo = packageManager!!.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
                if (applicationInfo != null) {
                    if (applicationInfo!!.metaData != null) {
                        return applicationInfo!!.metaData.getString("UMENG_CHANNEL")
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "other"
    }

    /**
     * 获取WIFI的SSID
     */
    fun getSSID(context: Context): String {
        val mWifi = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = mWifi.connectionInfo
        val connec = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI)?.state == NetworkInfo.State.CONNECTED && wifiInfo.ssid != null) {
            return wifiInfo.ssid
        }
        return ""
    }

    /**
     * 复制到剪切板
     */
    fun coptToSystem(text: String): Unit {
        // 从API11开始android推荐使用android.content.ClipboardManager
        // 为了兼容低版本我们这里使用旧版的android.text.ClipboardManager，虽然提示deprecated，但不影响使用。
        val cm = IvyApp.getInstance().applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        // 将文本内容放到系统剪贴板里。
        cm.text = text
    }


    /**
     * 检测是否安装微博
     */
    fun isWeiboInstall(@NonNull context: Context): Boolean {
        val packages = context.packageManager.getInstalledPackages(0)
        if (packages != null) {
            for (info in packages) {
                val name = info.packageName.toLowerCase(Locale.ENGLISH)
                if ("com.sina.weibo" == name) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * 检测是否安装QQ
     */
    /* fun isQQInstall(@NonNull context: Context): Boolean {
         val packages =  context.packageManager.getInstalledPackages(0)
         if (packages != null) {
             for (info in packages) {
                 val name = info.packageName.toLowerCase(Locale.ENGLISH)
                 if (Constants.PACKAGE_QQ == name) {
                     return true
                 }
             }
         }
         return false
     }*/

    /**
     * 检测是否安装微信
     */
    fun isWxInstall(context: Context): Boolean {
        val pinfo = context.packageManager.getInstalledPackages(0)// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (i in pinfo.indices) {
                val pn = pinfo[i].packageName
                if (pn == "com.tencent.mm") {
                    return true
                }
            }
        }
        return false
    }
}