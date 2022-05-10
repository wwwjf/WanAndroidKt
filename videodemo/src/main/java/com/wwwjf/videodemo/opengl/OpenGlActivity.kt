package com.wwwjf.videodemo.opengl

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Surface
import androidx.core.os.EnvironmentCompat
import com.cxp.learningvideo.media.decoder.AudioDecoder
import com.cxp.learningvideo.media.decoder.VideoDecoder
import com.wwwjf.videodemo.R
import java.util.concurrent.Executors

class OpenGlActivity : AppCompatActivity() {
    private val TAG = OpenGlActivity::class.java.simpleName
    val path = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath}/Camera/8a554b2ce6a3d0ee34d534602429885d.mp4"
    lateinit var drawer: IDrawer
    lateinit var glSurfaceView: GLSurfaceView
    var videoDecoder: VideoDecoder? = null
    var audioDecoder:AudioDecoder? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_gl)
        glSurfaceView = findViewById<GLSurfaceView>(R.id.glSurfaceView)
        when (intent.getIntExtra("type", 0)) {
            0 -> {
                drawer = TriangleDrawer()
                initDrawer(drawer)
            }
            1->{
                drawer = BitmapDrawer(BitmapFactory.decodeResource(this.resources, R.drawable.ic_launcher))
                initDrawer(drawer)
            }
            2->{
                drawer = VideoDrawer()
                initVideoDrawer()
            }
        }
    }


    private fun initDrawer(drawer: IDrawer) {
        glSurfaceView.setEGLContextClientVersion(2)
        val render = SimpleRender()
        render.addDrawer(drawer)
        glSurfaceView.setRenderer(render)
    }

    private fun initVideoDrawer() {
        //设置视频宽高
        drawer.setVideoSize(1920, 1080)

        drawer.getSurfaceTexture {
            //使用SurfaceTexture初始化一个Surface，并传递给MediaCodec使用
            initPlayer(Surface(it))
        }

        glSurfaceView.setEGLContextClientVersion(2)
        val render = SimpleRender()
        render.addDrawer(drawer)
        glSurfaceView.setRenderer(render)

    }

    private fun initPlayer(sf: Surface) {
        val threadPool = Executors.newFixedThreadPool(10)

        videoDecoder = VideoDecoder(path, null, sf)
        threadPool.execute(videoDecoder)

        audioDecoder = AudioDecoder(path)
        threadPool.execute(audioDecoder)


        videoDecoder?.goOn()
        audioDecoder?.goOn()
    }

    override fun onDestroy() {
        drawer.release()
        videoDecoder?.stop()
        audioDecoder?.stop()
        super.onDestroy()

    }
}