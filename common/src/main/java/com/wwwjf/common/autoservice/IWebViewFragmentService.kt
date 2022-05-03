package com.wwwjf.common.autoservice

import android.content.Context
import androidx.fragment.app.Fragment

interface IWebViewFragmentService {

    fun startWebViewFragmentActivity(context: Context, url: String, title: String, showActionBar:Boolean = true)

    fun getWebViewFragment(url:String): Fragment
}