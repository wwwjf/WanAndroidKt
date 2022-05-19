package com.wwwjf.ffmpegdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.wwwjf.ffmpegdemo.databinding.ActivityFfmpegDemoBinding

class FFmpegDemoActivity : AppCompatActivity() {
    private val TAG:String = FFmpegDemoActivity::class.java.simpleName
    lateinit var viewBinding: ActivityFfmpegDemoBinding

    companion object{
        init {
            System.loadLibrary("ffmpegdemo")
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityFfmpegDemoBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        Log.e(TAG, "onCreate: ${helloFromJNI()}")
        viewBinding.tvContent.text = helloFromJNI()

    }

    private external fun helloFromJNI():String

    private external fun ffmpegInfo(): String
}