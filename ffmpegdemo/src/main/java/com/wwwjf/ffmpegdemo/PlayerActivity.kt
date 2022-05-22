package com.wwwjf.ffmpegdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.Surface
import android.view.SurfaceHolder
import android.view.View
import com.wwwjf.ffmpegdemo.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity() {
    private var player:Int?=null
    lateinit var viewBinding:ActivityPlayerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        viewBinding.surfaceViewActivityFfmpeg.holder.addCallback(object :SurfaceHolder.Callback{
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
                pause(player!!)
            }
        })
    }


    private fun playerVideo(holder: SurfaceHolder) {
        val path = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath}/Camera/8a554b2ce6a3d0ee34d534602429885d.mp4"
        player = createPlayer(path, holder.surface)
        player?.let {
            play(it)
        }

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

    private external fun createPlayer(path: String, surface: Surface): Int

    private external fun play(player: Int)

    private external fun pause(player: Int)

    private external fun stop(player: Int)
}