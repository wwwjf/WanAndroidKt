package com.wwwjf.videodemo.opengl

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.view.Surface
import com.wwwjf.videodemo.media.decoder.AudioDecoder
import com.wwwjf.videodemo.media.decoder.VideoDecoder
import com.wwwjf.videodemo.R
import com.wwwjf.videodemo.opengl.drawer.BitmapDrawer
import com.wwwjf.videodemo.opengl.drawer.SoulVideoDrawer
import com.wwwjf.videodemo.opengl.drawer.TriangleDrawer
import com.wwwjf.videodemo.opengl.drawer.VideoDrawer
import java.util.concurrent.Executors

class OpenGlActivity : AppCompatActivity() {
    private val TAG = OpenGlActivity::class.java.simpleName
    val path = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath}/Camera/8a554b2ce6a3d0ee34d534602429885d.mp4"
//    val path2 = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath}/Camera/85d49cd0468fba88ecf47d0622310345.mp4"
    val path2 = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath}/Camera/d61027d389ca66658ae3aff7a253103d.mp4"
//    val path3 = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath}/Camera/share_9bab908bd86f1697778a917f64591440.mp4"
    val path3 = "https://v.douyin.com/FHDWmAU"
    lateinit var drawer: IDrawer
    lateinit var glSurfaceView: DefGLSurfaceView
    var videoDecoder: VideoDecoder? = null
    var audioDecoder:AudioDecoder? = null
    val threadPool = Executors.newFixedThreadPool(10)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_gl)
        glSurfaceView = findViewById(R.id.glSurfaceView)
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
            3->{
                drawer = VideoDrawer()
                initMultiVideoDrawer()
            }
            4->{
                drawer = SoulVideoDrawer()
                initSoulDrawer()
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
            initPlayer(path,Surface(it))
        }

        glSurfaceView.setEGLContextClientVersion(2)
        val render = SimpleRender()
        render.addDrawer(drawer)
        glSurfaceView.setRenderer(render)

    }


    private fun initMultiVideoDrawer() {
        val render = SimpleRender()
        val drawer1 = VideoDrawer()
        drawer1.setVideoSize(1920,1080)
        drawer1.getSurfaceTexture {
            initPlayer(path,Surface(it),true)
        }
        render.addDrawer(drawer1)
        val drawer2 = VideoDrawer()
        drawer2.setAlpha(0.2f)
        drawer2.setVideoSize(1080,1920)
        drawer2.getSurfaceTexture {
            initPlayer(path2, Surface(it),false)
        }
        render.addDrawer(drawer2)
        glSurfaceView.addDrawer(drawer2)

        glSurfaceView.setEGLContextClientVersion(2)
        glSurfaceView.setRenderer(render)

        Handler().postDelayed ({
            drawer2.scale(0.5f,0.5f)
        },100)

    }


    private fun initSoulDrawer() {
        val render = SimpleRender()
        drawer.setVideoSize(1080,1920)
        drawer.getSurfaceTexture {
            initPlayer(path3, Surface(it))
        }
        render.addDrawer(drawer)
        glSurfaceView.setEGLContextClientVersion(2)
        glSurfaceView.setRenderer(render)

    }

    private fun initPlayer(path:String, sf: Surface,withSound:Boolean=true) {

        videoDecoder = VideoDecoder(path, null, sf)
        threadPool.execute(videoDecoder)
        videoDecoder?.goOn()

        if (withSound) {
            audioDecoder = AudioDecoder(path)
            threadPool.execute(audioDecoder)
            audioDecoder?.goOn()
        }
    }

    override fun onDestroy() {
        drawer.release()
        videoDecoder?.stop()
        audioDecoder?.stop()
        super.onDestroy()

    }
}