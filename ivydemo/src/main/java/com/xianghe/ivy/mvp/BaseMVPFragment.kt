package com.xianghe.ivy.mvp

import android.os.Bundle

/**
 * @Author:  ycl
 * @Date:  2018/10/23 11:07
 * @Desc:
 */
abstract class BaseMVPFragment<V : com.xianghe.ivy.mvp.IBaseConctact.IBaseView, T : IBaseConctact.IBasePresenter<V>> : BaseFragment() {

    @JvmField
    protected var mPresenter: T? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPresenter()
    }

    private fun initPresenter() {
        mPresenter = createPresenter()
        mPresenter?.attachView(this as V)
    }

    override fun onDestroy() {
        mPresenter?.detachView()
        super.onDestroy()
    }

    abstract fun createPresenter(): T?

    open fun <D> onSuccess(d: D?) {
    }

    open fun <E> onError(e: E?) {
    }
}

