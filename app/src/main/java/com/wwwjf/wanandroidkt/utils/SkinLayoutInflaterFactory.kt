package com.wwwjf.wanandroidkt.utils

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View

class SkinLayoutInflaterFactory:LayoutInflater.Factory2 {
    override fun onCreateView(
        parent: View?,
        name: String,
        context: Context,
        attrs: AttributeSet
    ): View? {
        return null
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {

        return null
    }
}