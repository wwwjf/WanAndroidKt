package com.wwwjf.webview.client

import android.webkit.WebChromeClient
import android.webkit.WebView
import com.wwwjf.webview.WebCallBack

class WebClient():WebChromeClient() {

    private lateinit var webCallBack: WebCallBack

    constructor(webCallBack:WebCallBack):this(){
        this.webCallBack = webCallBack
    }

    override fun onReceivedTitle(view: WebView?, title: String?) {
        super.onReceivedTitle(view, title)
    }
}