package com.xianghe.ivy.app.umeng

import android.content.Context


/**
 * @Author:  Ycl
 * @Date:  2017-07-06 10:40
 * @Desc:  友盟统计工具类
 */

class UmengManager private constructor() {
    private var isOpenUm: Boolean = false // 控制测试环境不init友盟
//    private var isOpenUm: Boolean = true

    companion object {
        //计算页面停留时长的
//        private var mPageName = ""
//        private var startTime: Long = 0

        val instance: UmengManager
            get() = SingleHelper.instance
    }

    private object SingleHelper {
        var instance = UmengManager()
    }

    fun init() {
        if (isOpenUm) return
    }

    fun closeActivityUmeng() {
        if (isOpenUm) return
        //禁止默认的页面统计方式，这样将不会再自动统计Activity。
    }

    fun onPause(context: Context) {
        if (isOpenUm) return
    }

    fun onResume(context: Context) {
        if (isOpenUm) return
    }

    fun onPageStart(pageName: String) {
        if (isOpenUm) return
    }

    fun onPageEnd(pageName: String) {
        if (isOpenUm) return
    }

    fun onKillProcess(context: Context) {
        if (isOpenUm) return
    }

    /**
     * 登录统计
     */
    fun onProfileSignIn(userID: String?, type: String? = null) {
        if (isOpenUm || userID.isNullOrEmpty()) return

    }

    /**
     * 发送事件  map 不需要传递
     */
    fun sendEvent(eventName: String, map: Map<String, Any>? = hashMapOf()) {
        if (isOpenUm) return
//        MobclickAgent.onEvent(BaseApplication.mApplicationContext, eventName, map)
    }

    /**
     *  统计发生次数
     */
    private fun countEvent(eventId: String) {
        if (isOpenUm) return
    }

    /**
     * 计算事件
     */
    private fun durationEvent(eventId: String, map: Map<String, String>, duration: Int) {
        if (isOpenUm) return
    }

}
