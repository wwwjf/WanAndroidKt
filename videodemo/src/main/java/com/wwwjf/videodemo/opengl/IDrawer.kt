package com.wwwjf.videodemo.opengl

import android.graphics.SurfaceTexture

interface IDrawer {

    fun setTextureID(id: Int)

    fun draw()

    fun setAlpha(alpha:Float){}

    //设置视频的原始宽高
    fun setVideoSize(videoW: Int, videoH: Int){}
    //设置OpenGL窗口宽高
    fun setWorldSize(worldW: Int, worldH: Int){}

    fun loadShader(shader: Int, fragmentShader: String): Int

    fun getVertexShader():String

    fun getFragmentShader():String

    //新增接口，用于提供SurfaceTexture
    fun getSurfaceTexture(cb: (st: SurfaceTexture)->Unit) {}

    fun release()

}
