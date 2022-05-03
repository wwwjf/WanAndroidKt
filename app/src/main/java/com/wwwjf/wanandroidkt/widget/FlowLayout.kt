package com.wwwjf.wanandroidkt.widget

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.wwwjf.wanandroidkt.utils.ScreenUtil
import kotlin.math.max

class FlowLayout : ViewGroup {

    private val TAG = FlowLayout::class.java.simpleName
    //item横向间距
    private val mHorizontalSpacing = ScreenUtil.dp2px(16f)

    //item纵向间距
    private val mVerticalSpacing = ScreenUtil.dp2px(8f)

    //所有view 一行一行存储
    private val allLineList = arrayListOf<ArrayList<View>>()

    //每一行行高
    private val lineHeights = arrayListOf<Int>()

    constructor(context: Context) : super(context)

    constructor(context: Context, attr: AttributeSet) : super(context, attr)

    constructor(context: Context, attr: AttributeSet, defStyle: Int) : super(
        context,
        attr,
        defStyle
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        Log.e(TAG, "------------onMeasure -----------")

        clearMeasureParams()
        val selfWidth = MeasureSpec.getSize(widthMeasureSpec)
        val selfHeight = MeasureSpec.getSize(heightMeasureSpec)
        Log.e(TAG,"selfWidth=${selfWidth},selfHeight${selfHeight}," +
                "widthMeasureSpec=${widthMeasureSpec},heightMeasureSpec=${heightMeasureSpec}"+
                "measuredWidth=${measuredWidth},measuredHeight=${measuredHeight}")
        var lineViewList = arrayListOf<View>()
        var lineWidthUsed = 0
        var lineHeight = 0

        var parentNeededWidth = 0 // measure过程中，子View要求的父ViewGroup的宽
        var parentNeededHeight = 0 // measure过程中，子View要求的父ViewGroup的高

        for (i in 0..childCount-1) {
            val childView = getChildAt(i)
            val childParams = childView.layoutParams

            if (childView.visibility != View.GONE) {

                val childWidth = getChildMeasureSpec(widthMeasureSpec, paddingLeft + paddingRight, childParams.width)
                val childHeight = getChildMeasureSpec(heightMeasureSpec, paddingTop + paddingBottom, childParams.height)
                childView.measure(childWidth, childHeight)

                val childMeasureWidth = childView.measuredWidth
                val childMeasureHeight = childView.measuredHeight

                if (childMeasureWidth + lineWidthUsed + mHorizontalSpacing > selfWidth) {
                    allLineList.add(lineViewList)
                    lineHeights.add(lineHeight)

                    parentNeededWidth = max(parentNeededWidth, lineWidthUsed + mHorizontalSpacing)
                    parentNeededHeight += lineHeight + mVerticalSpacing

                    lineViewList = arrayListOf()
                    lineWidthUsed = 0
                    lineHeight = 0
                }

                lineViewList.add(childView)
                lineWidthUsed += childMeasureWidth + mHorizontalSpacing
                lineHeight = max(lineHeight, childMeasureHeight)

                //最后一行
                if (i == childCount - 1) {
                    allLineList.add(lineViewList)
                    lineHeights.add(lineHeight)

                    parentNeededWidth = max(parentNeededWidth, lineWidthUsed + mHorizontalSpacing)
                    parentNeededHeight += lineHeight + mVerticalSpacing
                }
            }
        }

        val wideMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        val realWidth = if (wideMode == MeasureSpec.EXACTLY) selfWidth else parentNeededWidth
        val realHeight = selfHeight.takeIf { heightMode == MeasureSpec.EXACTLY }?:parentNeededHeight
        setMeasuredDimension(realWidth,realHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

        Log.e(TAG, "------------onLayout: -----------")
        var currentleft = paddingLeft
        var currentTop = paddingTop

        allLineList.forEachIndexed {
                index, arrayList ->
            val lineHeight = lineHeights.get(index)
            arrayList.forEach {
                val left = currentleft
                val top = currentTop
                val right = currentleft + it.measuredWidth
                val bottom = currentTop + it.measuredHeight
                it.layout(left, top, right, bottom)
                currentleft = right + mHorizontalSpacing
            }
            currentTop += lineHeight + mVerticalSpacing
            currentleft = paddingLeft
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        Log.e(TAG, "---------onDraw: --------------")
    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        Log.e(TAG, "---------dispatchDraw: --------------")

    }

    private fun clearMeasureParams() {
        allLineList.clear()
        lineHeights.clear()
    }
}