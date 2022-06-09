package com.wwwjf.wanandroidkt

import android.app.Application
import android.content.Context
import android.os.Build
import android.webkit.WebView
import androidx.multidex.MultiDex
import com.alibaba.android.arouter.launcher.ARouter

class WanApplication:Application() {

    fun getInstance() = this

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
    override fun onCreate() {
        super.onCreate()
        initWebView()
        initARouter()
    }

    private fun initWebView() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WebView.setDataDirectorySuffix(getProcessName())
        }
    }

    private fun initARouter() {
        ARouter.openLog()
        ARouter.openDebug()
        ARouter.init(this)
    }


}