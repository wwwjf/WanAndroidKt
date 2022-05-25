package com.xianghe.ivy.mvp

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.birdsport.cphome.extension.adaptActivity
import com.birdsport.cphome.extension.fullscreen
import com.birdsport.cphome.extension.isTranslucentStatusBar
import com.xianghe.ivy.R
import com.xianghe.ivy.app.umeng.UmengManager
import com.xianghe.ivy.constant.GlobalVariables
import com.xianghe.ivy.manager.PermissionManager
import com.xianghe.ivy.manager.UserInfoManager
import com.xianghe.ivy.manager.download.MovieDownloadManager
import com.xianghe.ivy.utils.KLog
import com.xianghe.ivy.weight.CustomProgress
import gorden.rxbus2.RxBus

/**
 * @Author:  ycl
 * @Date:  2018/10/25 11:49
 * @Desc:
 */
abstract class BaseActivity : AppCompatActivity() {
    @JvmField
    protected var mPermissionManager: PermissionManager? = PermissionManager(this)
    @JvmField
    protected var mCustomProgress: CustomProgress? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adaptActivity(isNeedSizeAdapter())
        isTranslucentStatusBar(isTranslucent())
        fullscreen(isFullScreen())
        if (isNeedRxBus()) {
            KLog.d("BaseActivity", this.localClassName + "register");
            RxBus.get().register(this)
        }
    }


    override fun onResume() {
        super.onResume()
        adaptActivity(isNeedSizeAdapter())
        if (isNeedUmeng()) UmengManager.instance.onResume(this)
        UmengManager.instance.onPageStart(umPageName())
        showStartTasKConfirmDialogIfNeed()
    }

    override fun onPause() {
        super.onPause()
        if (isNeedUmeng()) UmengManager.instance.onPause(this)
        UmengManager.instance.onPageEnd(umPageName())
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isNeedRxBus()) {
            KLog.d("BaseActivity", this.localClassName + "Unregister");
            RxBus.get().unRegister(this)
        }
        if (mCustomProgress != null) {
            if (mCustomProgress?.isShowing == true) mCustomProgress?.dismiss()
            mCustomProgress = null
        }
        if (mPermissionManager != null) {
            mPermissionManager=null
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mPermissionManager?.handleRequestPermissionsResult(this, requestCode, permissions, grantResults)
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    //是否沉浸
    open fun isTranslucent() = false

    // 是否满屏
    open fun isFullScreen() = true

    //是否注册rxBus
    open fun isNeedRxBus() = true

    //是否使用适配方案
    open fun isNeedSizeAdapter() = false


    // ------------ umeng start ----------------
    fun umPageName() = this.javaClass.simpleName

    //此处如果有fragment有统计，则不需要在activity里面添加统计，避免统计重复
    open fun isNeedUmeng() = true
    // ------------ umeng end ----------------


    //  ------------  login start ----------------
    open fun checkLogin(sureLoginRunnable: Runnable) {
        if (UserInfoManager.isLogin()) {
            sureLoginRunnable.run()
        } else {
            showLogin()
        }
    }

    open fun showLogin() {
        if (mCustomProgress == null) {
            mCustomProgress = CustomProgress(this)
        }
        mCustomProgress?.show(getString(R.string.common_tips_no_login),
                getString(R.string.common_tips_title),
                getString(R.string.common_cancel),
                getString(R.string.common_ensure),
                { mCustomProgress?.cancel() },
                {
//                    startActivity(Intent(this,LoginActivity::class.java))
                    mCustomProgress?.cancel()
                },
                true, null)
    }
    //  ------------  login end ----------------


    //  ------------ 上传下载提示 start -----------

    private fun showStartTasKConfirmDialogIfNeed() {
        // 控制只弹一次
        if (!GlobalVariables.showUploadDialog) {
            return
        }
        GlobalVariables.showUploadDialog = false
        // 检测登录才弹
        if (!UserInfoManager.isLogin()) {
            return
        }

        val downloadManager = MovieDownloadManager.getInstance(this)
//        val uploadTaskManager = UploadTaskManager.getInstance()
        val uploadTaskManager = null

        val hasDownloadTasks = downloadManager.hasTasks()
//        val hasUploadTasks = uploadTaskManager.hasTasks()
        val hasUploadTasks = false

        var msg: String? = null
        if (hasDownloadTasks && hasUploadTasks) {
            msg = getString(R.string.common_upload_download)
        } else if (hasDownloadTasks && !hasUploadTasks) {
            msg = getString(R.string.common_download)
        } else if (!hasDownloadTasks && hasUploadTasks) {
            msg = getString(R.string.common_upload)
        } else {
            return
        }
        if (mCustomProgress == null) {
            mCustomProgress = CustomProgress(this)
        }
        mCustomProgress?.show(String.format(this.resources.getString(R.string.common_download_upload_tips), msg),
                getString(R.string.common_tips_title),
                getString(R.string.common_cancel),
                String.format(getString(R.string.common_continue_tips), msg),
                View.OnClickListener {
                    downloadManager.removeAllTask()
//                    uploadTaskManager.removeAllTask()
                    mCustomProgress?.dismiss()
                }, View.OnClickListener {
            downloadManager.isStartAllUploadTask = true// 打开开关，能够自动下载
            downloadManager.startAll(false)
//            uploadTaskManager.isStartAllUploadTask = true// 打开开关，能够自动下载
//            uploadTaskManager.startAll()
            mCustomProgress?.dismiss()
        }, false, null)
    }
    //  ------------ 上传下载提示 end -----------


    //  ------------ 点击界面空白区域收起键盘 start-----------
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (isShouldHideInput(v, ev)) {

                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm?.hideSoftInputFromWindow(v!!.windowToken, 0)
            }
            return super.dispatchTouchEvent(ev)
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        return if (window.superDispatchTouchEvent(ev)) {
            true
        } else onTouchEvent(ev)
    }


    private fun isShouldHideInput(v: View?, event: MotionEvent): Boolean {
        if (v != null && v is EditText) {
            val leftTop = intArrayOf(0, 0)
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop)
            val left = leftTop[0]
            val top = leftTop[1]

            val bottom = top + v.height
            val right = left + v.width
            return !(event.x > left && event.x < right
                    && event.y > top && event.y < bottom)
        }
        return false
    }
    //  ------------ 点击界面空白区域收起键盘 end-----------
}