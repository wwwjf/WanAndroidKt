package com.ghoome.g1.util

import android.content.Context
import android.util.TypedValue
import com.xianghe.ivy.app.IvyApp

/**
 * 常用单位转换的辅助类
 */
class DensityUtils private constructor() {
    init {
        /** cannot be instantiated  */
        throw UnsupportedOperationException("cannot be instantiated")
    }

    companion object {

        /**
         * dp转px

         * @param context
         * *
         * @param dpVal
         * *
         * @return
         */
        fun dp2px(context: Context = IvyApp.getInstance().applicationContext, dpVal: Float): Int {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    dpVal, context.resources.displayMetrics).toInt()
        }

        /**
         * sp转px

         * @param context
         * *
         * @param spVal
         * *
         * @return
         */
        fun sp2px(context: Context = IvyApp.getInstance().applicationContext, spVal: Float): Int {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                    spVal, context.resources.displayMetrics).toInt()
        }


        /**
         * px转dp

         * @param context
         * *
         * @param pxVal
         * *
         * @return
         */
        fun px2dp(context: Context = IvyApp.getInstance().applicationContext, pxVal: Float): Float {
            val scale = context.resources.displayMetrics.density
            return pxVal / scale
        }

        /**
         * px转sp
         * @param context
         * *
         * @param pxVal
         * *
         * @return
         */
        fun px2sp(context: Context = IvyApp.getInstance().applicationContext, pxVal: Float): Float {
            return pxVal / context.resources.displayMetrics.scaledDensity
        }

        fun getSize(context: Context = IvyApp.getInstance().applicationContext, id: Int): Int {
            return context.resources.getDimensionPixelSize(id)
        }


        fun getScreenWidth(context: Context = IvyApp.getInstance().applicationContext): Int {
            return context.resources.displayMetrics.widthPixels
        }

        fun getScreenHeight(context: Context = IvyApp.getInstance().applicationContext): Int {
            return context.resources.displayMetrics.heightPixels
        }


    }
}