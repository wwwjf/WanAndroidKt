package com.wwwjf.ffmpegdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.Surface
import android.view.SurfaceHolder
import android.view.View
import com.wwwjf.ffmpegdemo.databinding.ActivityOpenGlplayerBinding
import com.wwwjf.ffmpegdemo.databinding.ActivityPlayerBinding

class OpenGLPlayerActivity : AppCompatActivity() {

    private var player:Int?=null
    lateinit var viewBinding: ActivityOpenGlplayerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityOpenGlplayerBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        viewBinding.surfaceViewActivityOpengl.holder.addCallback(object : SurfaceHolder.Callback{
            override fun surfaceCreated(holder: SurfaceHolder) {
                playerVideo(holder)
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                stop(player!!)
            }
        })
    }

    private fun playerVideo(holder: SurfaceHolder) {
        val path = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath}/Camera/8a554b2ce6a3d0ee34d534602429885d_repack.mp4"
        player = createGLPlayer(path, holder.surface)
        player?.let {
            playOrPause(it)
        }

    }
    fun playOrPause(view: View) {
        playOrPause(player!!)
    }

    private external fun createGLPlayer(path: String, surface: Surface): Int
    private external fun playOrPause(player: Int)
    private external fun stop(player: Int)

}