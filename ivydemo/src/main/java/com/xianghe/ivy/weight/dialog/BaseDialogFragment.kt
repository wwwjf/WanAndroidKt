package com.xianghe.ivy.weight.dialog

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.annotation.StyleRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.xianghe.ivy.R

/**
 * @Author:Ycl
 * @Date:2017-09-14 10:38
 * @Desc:  dialog基类
 */

abstract class BaseDialogFragment : DialogFragment() {

    private var margin: Int = 0//左右边距
    private var width: Int = 0//宽度
    private var height: Int = 0//高度
    private var dimAmount = 0.5f//灰度深浅
    private var showBottom: Boolean = false//是否底部显示
    private var showRight: Boolean = false//是否右部显示
    private var outCancel = true//是否点击外部取消
    @StyleRes
    private var animStyle: Int = 0
    @StyleRes
    private var themeStyle: Int = 0//主题


    protected var layoutId: Int = 0

    abstract fun intLayoutId(): Int

    abstract fun convertView(holder: ViewHolder, dialog: BaseDialogFragment)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (themeStyle == 0) {
            setStyle(DialogFragment.STYLE_NO_TITLE, R.style.BaseDialogFragmentStyle)
        } else {
            setStyle(DialogFragment.STYLE_NO_TITLE, themeStyle)
        }
        layoutId = intLayoutId()

        //恢复保存的数据
        if (savedInstanceState != null) {
            margin = savedInstanceState.getInt(MARGIN)
            width = savedInstanceState.getInt(WIDTH)
            height = savedInstanceState.getInt(HEIGHT)
            dimAmount = savedInstanceState.getFloat(DIM)
            showBottom = savedInstanceState.getBoolean(BOTTOM)
            showRight = savedInstanceState.getBoolean(RIGHT)
            outCancel = savedInstanceState.getBoolean(CANCEL)
            animStyle = savedInstanceState.getInt(ANIM)
            themeStyle = savedInstanceState.getInt(THEME)
            layoutId = savedInstanceState.getInt(LAYOUT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(layoutId, container, false)
        convertView(ViewHolder.init(view), this)
        return view
    }

    override fun onStart() {
        super.onStart()
        initParams()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (outState != null) {
            outState.putInt(MARGIN, margin)
            outState.putInt(WIDTH, width)
            outState.putInt(HEIGHT, height)
            outState.putFloat(DIM, dimAmount)
            outState.putBoolean(BOTTOM, showBottom)
            outState.putBoolean(RIGHT, showRight)
            outState.putBoolean(CANCEL, outCancel)
            outState.putInt(ANIM, animStyle)
            outState.putInt(THEME, themeStyle)
            outState.putInt(LAYOUT, layoutId)
        }
    }

    private fun initParams() {
        val window = dialog?.window
        if (window != null) {
            val lp = window.attributes
            //调节灰色背景透明度[0-1]，默认0.5f
            lp.dimAmount = dimAmount
            //是否在底部显示
            if (showBottom) {
                lp.gravity = Gravity.BOTTOM
                if (animStyle == 0) {
                    animStyle = R.style.BottomAnimation
                }
            } else if (showRight) {
                lp.gravity = Gravity.RIGHT
                if (animStyle == 0) {
                    animStyle = R.style.RightAnimation
                }
            }

            //设置dialog宽度
            if (width == 0) {
                lp.width = getScreenWidth(context) - 2 * dp2px(context, margin.toFloat())
            } else if (width == -1) {
                lp.width = WindowManager.LayoutParams.WRAP_CONTENT
            } else if (width == -2) {
                lp.width = WindowManager.LayoutParams.MATCH_PARENT
            } else {
                lp.width = dp2px(context, width.toFloat())
            }

            //设置dialog高度
            if (height == 0) {
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            } else if (height == -2) {
                lp.height = WindowManager.LayoutParams.MATCH_PARENT
            } else {
                lp.height = dp2px(context, height.toFloat())
            }

            //设置dialog进入、退出的动画
            window.setWindowAnimations(animStyle)
            window.attributes = lp
        }
        isCancelable = outCancel
    }

    fun setMargin(margin: Int): BaseDialogFragment {
        this.margin = margin
        return this
    }

    fun setWidth(width: Int): BaseDialogFragment {
        this.width = width
        return this
    }

    fun setHeight(height: Int): BaseDialogFragment {
        this.height = height
        return this
    }

    fun setDimAmount(dimAmount: Float): BaseDialogFragment {
        this.dimAmount = dimAmount
        return this
    }

    fun setShowBottom(showBottom: Boolean): BaseDialogFragment {
        this.showBottom = showBottom
        return this
    }

    fun setShowRight(showRight: Boolean): BaseDialogFragment {
        this.showRight = showRight
        return this
    }

    fun setOutCancel(outCancel: Boolean): BaseDialogFragment {
        this.outCancel = outCancel
        return this
    }

    fun setAnimStyle(@StyleRes animStyle: Int): BaseDialogFragment {
        this.animStyle = animStyle
        return this
    }

    fun setThemeStyle(@StyleRes themeStyle: Int): BaseDialogFragment {
        this.themeStyle = themeStyle
        return this
    }

    fun show(manager: FragmentManager): BaseDialogFragment {
        super.show(manager, System.currentTimeMillis().toString())
        return this
    }

    fun dp2px(context: Context?, dipValue: Float): Int {
        if (context == null) {
            return 0
        }
        val scale = context.resources.displayMetrics.density
        return (dipValue * scale + 0.5f).toInt()
    }

    fun getScreenWidth(context: Context?): Int {
        if (context == null) {
            return 0
        }
        val displayMetrics = context.resources.displayMetrics
        return displayMetrics.widthPixels
    }

    companion object {
        private val MARGIN = "margin"
        private val WIDTH = "width"
        private val HEIGHT = "height"
        private val DIM = "dim_amount"
        private val BOTTOM = "show_bottom"
        private val RIGHT = "show_right"
        private val CANCEL = "out_cancel"
        private val ANIM = "anim_style"
        private val THEME = "theme_style"
        private val LAYOUT = "layout_id"
    }
}
