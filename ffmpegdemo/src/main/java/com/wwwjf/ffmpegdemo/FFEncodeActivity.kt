package com.wwwjf.ffmpegdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast

class FFEncodeActivity : AppCompatActivity() {
    private var ffEncoder: Int=0
    val srcPath = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath}/Camera/8a554b2ce6a3d0ee34d534602429885d.mp4"
    val destPath = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath}/Camera/8a554b2ce6a3d0ee34d534602429885d_encode.mp4"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ffencode)
        ffEncoder = initEncoder(srcPath, destPath)
}

fun onStartClick(view: View) {
    if (ffEncoder != 0) {
        startEncoder(ffEncoder)
        Toast.makeText(this, "开始编码", Toast.LENGTH_SHORT).show()
    }
}

override fun onDestroy() {
    if (ffEncoder > 0) {
        releaseEncoder(ffEncoder)
    }
    super.onDestroy()
}

private external fun initEncoder(srcPath: String, destPath: String): Int

private external fun startEncoder(encoder: Int)

private external fun releaseEncoder(encoder: Int)

}