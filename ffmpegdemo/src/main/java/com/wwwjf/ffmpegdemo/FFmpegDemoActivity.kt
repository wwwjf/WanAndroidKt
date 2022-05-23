package com.wwwjf.ffmpegdemo

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import android.view.View
import com.wwwjf.base.PermissionManager
import com.wwwjf.ffmpegdemo.databinding.ActivityFfmpegDemoBinding

class FFmpegDemoActivity : AppCompatActivity() {
    private lateinit var mPermissionManager: PermissionManager
    private val TAG:String = FFmpegDemoActivity::class.java.simpleName
    lateinit var viewBinding: ActivityFfmpegDemoBinding


    companion object{
        init {
            System.loadLibrary("native-lib")
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityFfmpegDemoBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        Log.e(TAG, "onCreate: ${helloFromJNI()}\n${ffmpegInfo()}")
        initPermission()



    }

    private fun initPermission() {

        mPermissionManager = PermissionManager(this)
        mPermissionManager.requestPermissions(this,object :
            PermissionManager.SimplePermissionListener() {
            override fun doAfterGrand(permission: Array<out String>?, tag: String?) {
                Log.e(TAG, "doAfterGrand: 权限授权成功")
            }},
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE),
            "video_tag")
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            mPermissionManager.handleRequestPermissionsResult(this,requestCode,permissions, grantResults)
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    fun player(view: View) {
        startActivity(Intent(this,PlayerActivity::class.java))
    }
    fun openglPlayer(view: View) {
        startActivity(Intent(this,OpenGLPlayerActivity::class.java))
    }
    fun repack(view: View) {
        startActivity(Intent(this,RepackActivity::class.java))
    }
    fun encode(view: View) {
        startActivity(Intent(this,FFEncodeActivity::class.java))
    }

    private external fun helloFromJNI():String

    private external fun ffmpegInfo(): String
}