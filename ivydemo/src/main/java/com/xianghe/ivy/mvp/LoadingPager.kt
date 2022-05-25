package com.xianghe.ivy.mvp

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.annotation.RequiresApi
import com.xianghe.ivy.R

/**
 * @Author:Ycl
 * @Date:2017-03-09 15:06
 * @Desc:
 */
class LoadingPager : FrameLayout {
    companion object {
        val STATE_NONE = -1
        val STATE_LOADING = 0
        val STATE_EMPTY = 1
        val STATE_ERROR = 2
        val STATE_SUCCESS = 3
    }

    private lateinit var mLoadingView: View
    private lateinit var mErrorView: View
    private lateinit var mEmptyView: View
    private lateinit var mSuccessView: View
    private var mLoadingCallback: ((View) -> Unit)? = null
    private var mClickCallback: ((View) -> Unit)? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        initViewStub(context, attrs)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int)
            : super(context, attrs, defStyleAttr, defStyleRes) {
        initViewStub(context, attrs)
    }

    private fun initViewStub(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoadingPager)
        typedArray.run {
            val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT)
            mLoadingView = ViewStub(context,
                    getResourceId(R.styleable.LoadingPager_pager_loading, 0))
            addView(mLoadingView, params)
            mErrorView = ViewStub(context,
                    getResourceId(R.styleable.LoadingPager_pager_error, 0))
            addView(mErrorView, params)
            mEmptyView = ViewStub(context,
                    getResourceId(R.styleable.LoadingPager_pager_empty, 0))
            addView(mEmptyView, params)
            mSuccessView = ViewStub(context,
                    getResourceId(R.styleable.LoadingPager_pager_success, 0))
            addView(mSuccessView, params)
        }
        typedArray.recycle()
        showLoading()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
    }

    fun loadData() {
        mLoadingCallback?.invoke(this)
    }

    fun setSuccessView(view: View) {
        removeView(mSuccessView)
        mSuccessView = view
        (mSuccessView.parent as? ViewGroup)?.run {
            removeView(mSuccessView)
        }
        addView(mSuccessView)
    }

    fun setSuccessView(@LayoutRes layoutResource: Int) {
        val mSuccessViewStub = mSuccessView
        (mSuccessViewStub as? ViewStub)?.run {
            this.layoutResource = layoutResource
            mSuccessView = this.inflate()
            showLoading()
        }
    }

    fun setLoadingView(view: View) {
        removeView(mLoadingView)
        mLoadingView = view
        (mLoadingView.parent as? ViewGroup)?.run {
            removeView(mLoadingView)
        }
        addView(mLoadingView)
    }

    fun setLoadingView(@LayoutRes layoutResource: Int) {
        val mSuccessViewStub = mLoadingView
        (mSuccessViewStub as? ViewStub)?.run {
            this.layoutResource = layoutResource
            mLoadingView = this.inflate()
            showLoading()
        }
    }

    fun setEmptyView(view: View) {
        removeView(mEmptyView)
        mEmptyView = view
        (mEmptyView.parent as? ViewGroup)?.run {
            removeView(mEmptyView)
        }
        addView(mEmptyView)
    }

    fun setEmptyView(@LayoutRes layoutResource: Int) {
        val mSuccessViewStub = mEmptyView
        (mSuccessViewStub as? ViewStub)?.run {
            this.layoutResource = layoutResource
            mEmptyView = this.inflate()
            showLoading()
        }
    }

    fun setErrorView(view: View) {
        removeView(mErrorView)
        mErrorView = view
        (mErrorView.parent as? ViewGroup)?.run {
            removeView(mErrorView)
        }
        addView(mErrorView)
    }

    fun setErrorView(@LayoutRes layoutResource: Int) {
        val mSuccessViewStub = mErrorView
        (mSuccessViewStub as? ViewStub)?.run {
            this.layoutResource = layoutResource
            mErrorView = this.inflate()
            showLoading()
        }
    }

    fun setLoadingCallback(sameToOther: Boolean = true, callback: (View) -> Unit) {
        mLoadingCallback = callback
        if (sameToOther) mClickCallback = callback
    }

    fun setStateViewClickCallback(callback: (View) -> Unit) {
        mClickCallback = callback
    }

    fun refreshUIByState(state: Int) {
        if (state == STATE_NONE || state == STATE_LOADING) {
            if (isInflated(mLoadingView).not()) {
                (mLoadingView as? ViewStub)?.run {
                    mLoadingView = inflate()
                }
            }
            mLoadingView.visible()
            bringChildToFront(mLoadingView)
        } else {
            if (isInflated(mLoadingView))
                mLoadingView.gone()
        }

        if (state == STATE_EMPTY) {
            if (isInflated(mEmptyView).not()) {
                (mEmptyView as? ViewStub)?.run {
                    mEmptyView = inflate()
                    mEmptyView.setOnClickListener(mClickCallback)
                }
            }
            mEmptyView.visible()
            bringChildToFront(mEmptyView)
        } else {
            if (isInflated(mEmptyView))
                mEmptyView.gone()
        }

        if (state == STATE_ERROR) {
            if (isInflated(mErrorView).not()) {
                (mErrorView as? ViewStub)?.run {
                    mErrorView = inflate()
                    mErrorView.setOnClickListener(mClickCallback)
                }
            }
            mErrorView.visible()
            bringChildToFront(mErrorView)
        } else {
            if (isInflated(mErrorView))
                mErrorView.gone()
        }

        if (state == STATE_SUCCESS) {
            if (isInflated(mSuccessView).not()) {
                (mSuccessView as? ViewStub)?.run {
                    mSuccessView = inflate()
                }
            }
            //二次检查，防止要activity中使用时successView是Fragment
            val successView = mSuccessView
            if (successView !is ViewStub ||
                    (successView is ViewStub && successView.layoutResource != 0
                            && successView.parent != null)) {
                successView.visible()
                bringChildToFront(successView)
            }
        } else if (state != STATE_LOADING) {
            if (isInflated(mSuccessView))
                mSuccessView.gone()
        }
    }

    fun showLoading() {
        refreshUIByState(STATE_LOADING)
    }

    fun showError() {
        refreshUIByState(STATE_ERROR)
    }

    fun showEmpty() {
        refreshUIByState(STATE_EMPTY)
    }

    fun showSuccess() {
        refreshUIByState(STATE_SUCCESS)
    }

    private fun checkStatus(): Boolean {
        return checkViewStatus(mLoadingView) && checkViewStatus(mErrorView) &&
                checkViewStatus(mEmptyView) && checkViewStatus(mSuccessView)
    }

    private fun checkViewStatus(view: View): Boolean {
        return (view !is ViewStub) || (view is ViewStub &&
                (view.parent != null && view.layoutResource != 0))
    }

    private fun isInflated(view: View): Boolean {
        //非ViewStub（表示已经inflate了）或者parent为空（已经inflate或者不能inflate）
        return (view !is ViewStub) ||
                (view is ViewStub && (view.parent == null || view.layoutResource == 0))
    }

    private fun View.gone() {
        if (this.visibility != View.GONE) {
            this.visibility = View.GONE
        }
    }

    private fun View.visible() {
        if (this.visibility != View.VISIBLE) {
            this.visibility = View.VISIBLE
        }
    }

    private fun View.invisible() {
        if (this.visibility != View.INVISIBLE) {
            this.visibility = View.INVISIBLE
        }
    }

    fun getSuccessView(): View {
        return mSuccessView
    }
}