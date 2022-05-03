package com.wwwjf.wanandroidkt.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class CustomRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    private var mLastX:Float? = 0.0f
    private var mLastY:Float? = 0.0f

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val x = ev?.run { getX() }
        val y = ev?.run { getY() }
        when(ev?.action){
            MotionEvent.ACTION_DOWN ->parent.requestDisallowInterceptTouchEvent(true)
            MotionEvent.ACTION_MOVE ->{
                val deltX = mLastX?.let { x?.minus(it) }
                val deltY = mLastY?.let { y?.minus(it) }
                if (deltX != null && deltY != null && deltX < deltY){
                    parent.requestDisallowInterceptTouchEvent(false)
                }
            }
        }
        mLastX = x
        mLastY = y
        return super.dispatchTouchEvent(ev)
    }
    override fun onInterceptTouchEvent(e: MotionEvent?): Boolean {
        return super.onInterceptTouchEvent(e)
    }

    override fun onTouchEvent(e: MotionEvent?): Boolean {
        return super.onTouchEvent(e)
    }
}