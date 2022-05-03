package com.wwwjf.wanandroidkt.utils

import android.content.res.Resources
import android.util.TypedValue

object ScreenUtil {

    fun dp2px(dp: Float):Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
            Resources.getSystem().displayMetrics).toInt()
    }
}