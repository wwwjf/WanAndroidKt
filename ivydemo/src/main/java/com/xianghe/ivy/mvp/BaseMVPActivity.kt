package com.xianghe.ivy.mvp

import android.os.Bundle

/**
 * @Author:  ycl
 * @Date:  2018/10/23 10:50
 * @Desc:
 */
abstract class BaseMVPActivity<V : com.xianghe.ivy.mvp.IBaseConctact.IBaseView, T : IBaseConctact.IBasePresenter<V>> : BaseVideoCallActivity() {
    @JvmField
    protected var mPresenter: T? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPresenter()
    }

    protected fun initPresenter() {
        mPresenter = createPresenter()
        mPresenter?.attachView(this as V)
    }

    override fun onDestroy() {
        mPresenter?.detachView()
        super.onDestroy()
    }

    abstract fun createPresenter(): T?

}
