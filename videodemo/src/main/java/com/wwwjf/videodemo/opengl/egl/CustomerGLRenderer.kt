package com.wwwjf.videodemo.opengl.egl

import android.opengl.GLES20
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import com.wwwjf.videodemo.opengl.IDrawer
import com.wwwjf.videodemo.opengl.OpenGlTools
import java.lang.ref.WeakReference

class CustomerGLRenderer : SurfaceHolder.Callback {

    //OpenGL渲染线程
    private lateinit var mThread:RenderThread

    //页面上的SurfaceView弱引用
    private var mSurfaceView: WeakReference<SurfaceView>? = null

    //所有的绘制器
    private val mDrawers = mutableListOf<IDrawer>()
    
    /**
     * 设置SurfaceView
     */
    fun setSurface(surface: SurfaceView) {
        mSurfaceView = WeakReference(surface)
        surface.holder.addCallback(this)

        surface.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener{
            override fun onViewDetachedFromWindow(v: View?) {
                mThread.onSurfaceStop()
            }

            override fun onViewAttachedToWindow(v: View?) {
            }
        })
    }


    fun setRenderMode(renderMode: RenderMode) {

    }
    /**
     * 添加绘制器
     */
    fun addDrawer(drawer: IDrawer) {
        mDrawers.add(drawer)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        mThread = RenderThread(mSurfaceView?.get()?.holder?.surface,mDrawers)
        //启动渲染线程
        mThread.start()
        mThread.onSurfaceCreate()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        mThread.onSurfaceChange(width, height)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        mThread.onSurfaceDestroy()
    }

    /**
     * 渲染状态
     */
    enum class RenderState {
        NO_SURFACE, //没有有效的surface
        FRESH_SURFACE, //持有一个未初始化的新的surface
        SURFACE_CHANGE, //surface尺寸变化
        RENDERING, //初始化完毕，可以开始渲染
        SURFACE_DESTROY, //surface销毁
        STOP //停止绘制
    }

    enum class RenderMode {
        RENDER_CONTINUOUSLY,
        RENDER_WHEN_DIRTY
    }

}


