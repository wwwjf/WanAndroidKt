package com.xianghe.ivy.weight.clicklistener

import android.view.View
import java.util.*

/**
 * @Author:  Ycl
 * @Date:  2017-06-28 14:06
 * @Desc: 防止重复点击事件的回调
 */
abstract class NoDoubleClickListener : View.OnClickListener {
    private var lastClickTime: Long = 0

    override fun onClick(v: View) {
        val currentTime = Calendar.getInstance().timeInMillis
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime
            onNoDoubleClick(v)
        }
    }

    protected abstract fun onNoDoubleClick(v: View)

    companion object {
        val MIN_CLICK_DELAY_TIME = 1000
    }

}