package com.wwwjf.common.autoservice

import android.content.Context
import androidx.fragment.app.Fragment

interface IWebViewActivityService {
    fun startWebViewActivity(context: Context, url: String, title: String, showActionBar:Boolean = true)
}