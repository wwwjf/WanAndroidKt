package com.wwwjf.webview

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.google.auto.service.AutoService
import com.wwwjf.common.autoservice.IWebViewActivityService
import com.wwwjf.common.autoservice.IWebViewFragmentService
import com.wwwjf.webview.utils.Constant

@AutoService(IWebViewActivityService::class,IWebViewFragmentService::class)
class WebViewServiceImpl : IWebViewActivityService,IWebViewFragmentService {
    override fun startWebViewActivity(context: Context, url: String,
        title: String, showActionBar: Boolean) {
        val intent = Intent(context, WebViewActivity::class.java)
        intent.putExtra(Constant.URL, url)
        intent.putExtra(Constant.TITLE, title)
        intent.putExtra(Constant.SHOW_ACTIONBAR,showActionBar)
        context.startActivity(intent)
    }

    override fun startWebViewFragmentActivity(context: Context, url: String,
        title: String, showActionBar: Boolean) {
        val intent = Intent(context,ContainerActivity::class.java)
        intent.putExtra(Constant.URL,url)
        intent.putExtra(Constant.TITLE,title)
        context.startActivity(intent)
    }

    override fun getWebViewFragment(url: String): Fragment = WebViewFragment.newInstance(url)
}