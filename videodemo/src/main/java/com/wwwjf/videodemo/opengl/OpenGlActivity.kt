package com.wwwjf.videodemo.opengl

import android.graphics.BitmapFactory
import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wwwjf.videodemo.R

class OpenGlActivity : AppCompatActivity() {
    lateinit var drawer: IDrawer
    lateinit var glSurfaceView: GLSurfaceView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_gl)
        glSurfaceView = findViewById<GLSurfaceView>(R.id.glSurfaceView)
        drawer = if (intent.getIntExtra("type",0)==0){
            TriangleDrawer()
        } else {
            BitmapDrawer(BitmapFactory.decodeResource(this.resources, R.drawable.ic_launcher))
        }
        initDrawer(drawer)
    }

    private fun initDrawer(drawer: IDrawer) {
        glSurfaceView.setEGLContextClientVersion(2)
        val render = SimpleRender()
        render.addDrawer(drawer)
        glSurfaceView.setRenderer(render)
    }

    override fun onDestroy() {
        drawer.release()
        super.onDestroy()

    }
}