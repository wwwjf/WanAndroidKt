package com.wwwjf.videodemo.opengl.egl

import android.opengl.GLES20
import com.wwwjf.videodemo.opengl.IDrawer
import com.wwwjf.videodemo.opengl.OpenGlTools

class RenderThread(private val mSurface: Any?, private val mDrawers: MutableList<IDrawer>) :
    Thread() {

    // 渲染状态
    private var mState = CustomerGLRenderer.RenderState.NO_SURFACE

    private var mEGLSurface: EGLSurfaceHolder? = null

    // 是否绑定了EGLSurface
    private var mHaveBindEGLContext = false

    //是否已经新建过EGL上下文，用于判断是否需要生产新的纹理ID
    private var mNeverCreateEglContext = true

    private var mWidth = 0
    private var mHeight = 0

    private val mWaitLock = Object()

//------------第1部分：线程等待与解锁-----------------

    private fun holdOn() {
        synchronized(mWaitLock) {
            mWaitLock.wait()
        }
    }

    private fun notifyGo() {
        synchronized(mWaitLock) {
            mWaitLock.notify()
        }
    }

//------------第2部分：Surface声明周期转发函数------------

    fun onSurfaceCreate() {
        mState = CustomerGLRenderer.RenderState.FRESH_SURFACE
        notifyGo()
    }

    fun onSurfaceChange(width: Int, height: Int) {
        mWidth = width
        mHeight = height
        mState = CustomerGLRenderer.RenderState.SURFACE_CHANGE
        notifyGo()
    }

    fun onSurfaceDestroy() {
        mState = CustomerGLRenderer.RenderState.SURFACE_DESTROY
        notifyGo()
    }

    fun onSurfaceStop() {
        mState = CustomerGLRenderer.RenderState.STOP
        notifyGo()
    }

//------------第3部分：OpenGL渲染循环------------

    override fun run() {
        // 【1】初始化EGL
        initEGL()
        while (true) {
            when (mState) {
                CustomerGLRenderer.RenderState.FRESH_SURFACE -> {
                    //【2】使用surface初始化EGLSurface，并绑定上下文
                    createEGLSurfaceFirst(mSurface)
                    holdOn()
                }
                CustomerGLRenderer.RenderState.SURFACE_CHANGE -> {
                    createEGLSurfaceFirst(mSurface)
                    //【3】初始化OpenGL世界坐标系宽高
                    GLES20.glViewport(0, 0, mWidth, mHeight)
                    configWordSize()
                    mState = CustomerGLRenderer.RenderState.RENDERING
                }
                CustomerGLRenderer.RenderState.RENDERING -> {
                    //【4】进入循环渲染
                    render()
                }
                CustomerGLRenderer.RenderState.SURFACE_DESTROY -> {
                    //【5】销毁EGLSurface，并解绑上下文
                    destroyEGLSurface()
                    mState = CustomerGLRenderer.RenderState.NO_SURFACE
                }
                CustomerGLRenderer.RenderState.STOP -> {
                    //【6】释放所有资源
                    releaseEGL()
                    return
                }
                else -> {
                    holdOn()
                }
            }
            sleep(20)
        }
    }

//------------第4部分：EGL相关操作------------

    private fun initEGL() {
        mEGLSurface = EGLSurfaceHolder()
        mEGLSurface?.init(null, EGL_RECORDABLE_ANDROID)
    }

    private fun createEGLSurfaceFirst(surface: Any?) {
        if (!mHaveBindEGLContext) {
            mHaveBindEGLContext = true
            createEGLSurface(surface)
            if (mNeverCreateEglContext) {
                mNeverCreateEglContext = false
                generateTextureID()
            }
        }
    }

    private fun createEGLSurface(surface: Any?) {
        mEGLSurface?.createEGLSurface(surface)
        mEGLSurface?.makeCurrent()
    }

    private fun destroyEGLSurface() {
        mEGLSurface?.destroyEGLSurface()
        mHaveBindEGLContext = false
    }

    private fun releaseEGL() {
        mEGLSurface?.release()
    }

//------------第5部分：OpenGL ES相关操作-------------

    private fun generateTextureID() {
        val textureIds = OpenGlTools.createTextureIds(mDrawers.size)
        for ((idx, drawer) in mDrawers.withIndex()) {
            drawer.setTextureID(textureIds[idx])
        }
    }

    private fun configWordSize() {
        mDrawers.forEach { it.setWorldSize(mWidth, mHeight) }
    }

    private fun render() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        mDrawers.forEach { it.draw() }
        mEGLSurface?.swapBuffers()
    }
}
