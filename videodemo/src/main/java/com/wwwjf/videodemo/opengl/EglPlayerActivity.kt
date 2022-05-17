package com.wwwjf.videodemo.opengl

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.view.Surface
import android.view.SurfaceView
import com.wwwjf.videodemo.R
import com.wwwjf.videodemo.databinding.ActivityEglPlayerBinding
import com.wwwjf.videodemo.media.decoder.AudioDecoder
import com.wwwjf.videodemo.media.decoder.VideoDecoder
import com.wwwjf.videodemo.opengl.drawer.VideoDrawer
import com.wwwjf.videodemo.opengl.egl.CustomerGLRenderer
import java.util.concurrent.Executors

class EglPlayerActivity : AppCompatActivity() {
    private lateinit var viewbinding:ActivityEglPlayerBinding
    val path = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath}/Camera/8a554b2ce6a3d0ee34d534602429885d.mp4"
    val path2 = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath}/Camera/d61027d389ca66658ae3aff7a253103d.mp4"

    private val threadPool = Executors.newFixedThreadPool(10)
    private lateinit var mRenderer:CustomerGLRenderer
    private lateinit var surfaceView: SurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewbinding = ActivityEglPlayerBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_egl_player)
        surfaceView = findViewById<SurfaceView>(R.id.surfaceView)
        initFirstVideo()
        initSecondVideo()
        setRenderSurface()
    }

    private fun initFirstVideo() {
        mRenderer = CustomerGLRenderer()
        val drawer = VideoDrawer()
        drawer.setVideoSize(1920, 1080)
        drawer.getSurfaceTexture {
            initPlayer(path, Surface(it), true)
        }
        mRenderer.addDrawer(drawer)
    }

    private fun initSecondVideo() {
        val drawer = VideoDrawer()
        drawer.setAlpha(0.5f)
        drawer.setVideoSize(1920, 1080)
        drawer.getSurfaceTexture {
            initPlayer(path2, Surface(it), false)
        }
        mRenderer.addDrawer(drawer)

        Handler().postDelayed({
            drawer.scale(0.5f, 0.5f)
        }, 1000)
    }

    private fun initPlayer(path: String, sf: Surface, withSound: Boolean) {
        val videoDecoder = VideoDecoder(path, null, sf)
        threadPool.execute(videoDecoder)
        videoDecoder.goOn()

        if (withSound) {
            val audioDecoder = AudioDecoder(path)
            threadPool.execute(audioDecoder)
            audioDecoder.goOn()
        }
    }

    private fun setRenderSurface() {
        mRenderer.setSurface(surfaceView)
        mRenderer.setRenderMode(CustomerGLRenderer.RenderMode.RENDER_CONTINUOUSLY)
    }

}