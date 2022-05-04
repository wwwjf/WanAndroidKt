package com.wwwjf.videodemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class VideoDemoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_demo)
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
}