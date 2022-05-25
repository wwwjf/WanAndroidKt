package com.xianghe.ivy.mvp.loadPager

import com.xianghe.ivy.mvp.BasePresenter


abstract class BaseLoadingPresenter<T : IBaseLoadingConctact.IBaseView> : BasePresenter<T>() {
    fun showLoading() {
        view()?.showLoading()
    }

    fun clossLoading() {
        view()?.dismissLoading()
    }

    fun <E> onError(e: E?) {
        view()?.onError(e)
    }

    fun <D> onSuccess(d: D?) {
        view()?.onSuccess(d)
    }
}