package com.wwwjf.ffmpegdemo

import android.Manifest
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
    private var player:Int?=null

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
        viewBinding.surfaceViewActivityFfmpeg.holder.addCallback(object :SurfaceHolder.Callback{
            override fun surfaceCreated(holder: SurfaceHolder) {
                initPermission(holder)
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
            }
        })


    }

    private fun initPermission(holder: SurfaceHolder) {

        mPermissionManager = PermissionManager(this)
        mPermissionManager.requestPermissions(this,object :
            PermissionManager.SimplePermissionListener() {
            override fun doAfterGrand(permission: Array<out String>?, tag: String?) {
                Log.e(TAG, "doAfterGrand: 权限授权成功")
                playerVideo(holder)
            }},
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE),
            "video_tag")
    }

    private fun playerVideo(holder: SurfaceHolder) {
        val path = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath}/Camera/8a554b2ce6a3d0ee34d534602429885d.mp4"
        player = createPlayer(path, holder.surface)
        player?.let {
            play(it)
        }

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
    fun play(view: View) {
        player?.let {
            play(it)
        }
    }
    fun pause(view: View) {
        player?.let {
            pause(it)
        }
    }

    private external fun helloFromJNI():String

    private external fun ffmpegInfo(): String

    private external fun createPlayer(path: String, surface: Surface): Int

    private external fun play(player: Int)

    private external fun pause(player: Int)

}