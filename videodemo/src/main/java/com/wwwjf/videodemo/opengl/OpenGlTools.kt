package com.wwwjf.videodemo.opengl

import android.opengl.GLES20

object OpenGlTools {
    fun createTextureIds(count: Int): IntArray {
        val texture = IntArray(count)
        GLES20.glGenTextures(count,texture,0)
        return texture
    }

}
