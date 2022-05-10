package com.wwwjf.videodemo.opengl

interface IDrawer {

    fun setTextureID(id: Int)

    fun draw()

    fun setWorldSize(width: Int, height: Int)

    fun release()

}
