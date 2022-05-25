package com.xianghe.ivy.utils

import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import com.xianghe.ivy.BuildConfig
import java.util.concurrent.ConcurrentHashMap

/**
 * author: ycl
 * date: 2018-09-05 16:51
 * desc: 今日头条适配方案
 */
object SizeAdapterUtils {

    private const val dpWidth = 720f // 默认定义的dp值
    private var mInitScaledDensity: Float = 0f
    private var mInitDensity: Float = 0f
    private var mInitScreenWidth: Int = 0 // 缓存需要根据宽度控制

    private val mCache: MutableMap<String, DisplayMetricsInfo>

    init {
        mCache = ConcurrentHashMap()
    }

    fun init(application: Application) {
        val displayMetrics = Resources.getSystem().displayMetrics

        mInitDensity = displayMetrics.density
        mInitScaledDensity = displayMetrics.scaledDensity
        mInitScreenWidth = getScreenWidth(application)

        // 字体大小改变需要动态设置的值 mInitScaledDensity
        application.registerComponentCallbacks(object : ComponentCallbacks {
            // 系统设置的监听
            override fun onConfigurationChanged(newConfig: Configuration) {
                if (newConfig != null) {
                    if (newConfig.fontScale > 0) {
                        mInitScaledDensity = Resources.getSystem().displayMetrics.scaledDensity
                    }
                    mInitScreenWidth = getScreenWidth(application)
                }
            }


            override fun onLowMemory() {

            }
        })
    }

    //Activity 中的 setContentView(View) 一定要在 super.onCreate(Bundle); 之后执行
    fun adaptActivity(application: Application, activity: Activity?) {
        val appDisplayMetrics = application.resources.displayMetrics

        val key = dpWidth.toString() + "|" + mInitScaledDensity + "|" + mInitScreenWidth
        val displayMetricsInfo = mCache[key]
        ld("key: $key  displayMetricsInfo： ${displayMetricsInfo == null}")


        // 得到设计图的density，densityDpi
        val targetDensity: Float
        val targetDensityDpi: Int
        val targetScaleDensity: Float

        if (displayMetricsInfo == null) {
            // 得到设计图的density，densityDpi
            targetDensity = appDisplayMetrics.widthPixels / dpWidth
            targetDensityDpi = (targetDensity * 160).toInt()
            targetScaleDensity = targetDensity * (mInitScaledDensity / mInitDensity)
            mCache[key] = DisplayMetricsInfo(targetDensity, targetDensityDpi, targetScaleDensity)
        } else {
            targetDensity = displayMetricsInfo.density
            targetDensityDpi = displayMetricsInfo.densityDpi
            targetScaleDensity = displayMetricsInfo.scaledDensity
        }
        ld("targetDensity: $targetDensity targetDensityDpi: $targetDensityDpi targetScaleDensity: $targetScaleDensity")

        // 替换application
        setDensity(appDisplayMetrics, targetDensity, targetDensityDpi, targetScaleDensity)
        // 兼容MIUI
        setDensity(getMetricsOnMiui(application.resources), targetDensity, targetDensityDpi, targetScaleDensity)

        if (activity == null) {
            return
        }
        // 替换activity
        setDensity(activity.resources.displayMetrics, targetDensity, targetDensityDpi, targetScaleDensity)
        // 兼容MIUI
        setDensity(getMetricsOnMiui(activity.resources), targetDensity, targetDensityDpi, targetScaleDensity)
    }


    private fun setDensity(displayMetrics: DisplayMetrics?, ds: Float, dsp: Int, scaleDs: Float) =
            displayMetrics?.run {
                density = ds
                densityDpi = dsp
                scaledDensity = scaleDs
            }


    private fun getMetricsOnMiui(resources: Resources): DisplayMetrics? {
        if ("MiuiResources" == resources.javaClass.simpleName || "XResources" == resources.javaClass.simpleName) {
            return try {
                val field = Resources::class.java.getDeclaredField("mTmpMetrics")
                field.isAccessible = true
                field.get(resources) as DisplayMetrics
            } catch (e: Exception) {
                null
            }
        }
        return null
    }

    private fun getScreenWidth(context: Context): Int {
        val w = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val d = w.defaultDisplay
        val metrics = DisplayMetrics()
        d.getMetrics(metrics)
        return metrics.widthPixels
    }

    private fun ld(msg: String) {
        if (BuildConfig.DEBUG) {
            KLog.d(javaClass.simpleName, msg)
        }
    }

    data class DisplayMetricsInfo(val density: Float, val densityDpi: Int, val scaledDensity: Float)
}
