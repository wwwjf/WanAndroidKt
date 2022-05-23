package com.wwwjf.ffmpegdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import kotlin.concurrent.thread

class RepackActivity : AppCompatActivity() {

    private var ffRepack: Int = 0
    val srcPath = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath}/Camera/8a554b2ce6a3d0ee34d534602429885d.mp4"
    val destPath = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath}/Camera/8a554b2ce6a3d0ee34d534602429885d_repack.mp4"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repack)

        ffRepack = createRepack(srcPath, destPath)
    }

    fun onStartClick(view: View) {
        if (ffRepack != 0) {
            thread {
                startRepack(ffRepack)
            }
        }
    }

    private external fun createRepack(srcPath: String, destPath: String): Int

    private external fun startRepack(repack: Int)

}