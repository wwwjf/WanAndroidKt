package com.wwwjf.videodemo

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.wwwjf.base.PermissionManager
import com.wwwjf.videodemo.opengl.EglPlayerActivity
import com.wwwjf.videodemo.opengl.OpenGlActivity

class VideoDemoActivity : AppCompatActivity() {
    private val TAG = VideoDemoActivity::class.java.simpleName
    private lateinit var mPermissionManager:PermissionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_demo)
        mPermissionManager = PermissionManager(this)
        mPermissionManager.requestPermissions(this,object :
            PermissionManager.SimplePermissionListener() {
            override fun doAfterGrand(permission: Array<out String>?, tag: String?) {
                Log.e(TAG, "doAfterGrand: 权限授权成功")
            }},
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA),
            "video_tag")

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            mPermissionManager.handleRequestPermissionsResult(this,requestCode,permissions, grantResults)
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun cameraSurfaceView(view: View) {
        startActivity(Intent(this, SurfaceViewActivity::class.java))
    }

    fun cameraTextureView(view: View) {
        startActivity(Intent(this, TextureViewActivity::class.java))
    }

    fun mediaExtractorMuxer(view: View) {
        startActivity(Intent(this, ExtractorMuxerActivity::class.java))
    }

    fun mediaCodec(view: View) {
        startActivity(Intent(this,MediaCodecActivity::class.java))
    }

    fun camera(view: View) {
        startActivity(Intent(this,CameraActivity::class.java))
    }
    fun openGL1(view: View) {
        val intent = Intent(this,OpenGlActivity::class.java)
        startActivity(intent)
    }
    fun openGL2(view: View) {
        val intent = Intent(this,OpenGlActivity::class.java)
        intent.putExtra("type",1)
        startActivity(intent)
    }
    fun openGL3(view: View) {
        val intent = Intent(this,OpenGlActivity::class.java)
        intent.putExtra("type",2)
        startActivity(intent)
    }
    fun openGL4(view: View) {
        val intent = Intent(this,OpenGlActivity::class.java)
        intent.putExtra("type",3)
        startActivity(intent)
    }
    fun openGL5(view: View) {
        val intent = Intent(this,EglPlayerActivity::class.java)
        intent.putExtra("type",3)
        startActivity(intent)
    }
    fun openGL6(view: View) {
        val intent = Intent(this,OpenGlActivity::class.java)
        intent.putExtra("type",4)
        startActivity(intent)
    }
    fun openGL7(view: View) {
        val intent = Intent(this,SynthesizerActivity::class.java)
        intent.putExtra("type",4)
        startActivity(intent)
    }
}