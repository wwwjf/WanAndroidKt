package com.wwwjf.wanandroidkt

import android.app.Application
import android.os.Build
import android.webkit.WebView

class WanApplication:Application() {

    override fun onCreate() {
        super.onCreate()
        initWebView()
    }

    private fun initWebView() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WebView.setDataDirectorySuffix(getProcessName())
        }
    }

}