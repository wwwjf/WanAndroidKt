package com.wwwjf.webview

interface WebCallBack {
    fun onPageStarted(url: String)

    fun onPageFinished(url: String)

    fun updateTitle(title:String)

    fun onError()

}