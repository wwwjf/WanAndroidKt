package com.xianghe.ivy.mvp

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.lang.ref.WeakReference


abstract class BasePresenter<T : com.xianghe.ivy.mvp.IBaseConctact.IBaseView> : com.xianghe.ivy.mvp.IBaseConctact.IBasePresenter<T> {
    /**
     * 持有UI接口的弱引用
     */
    private var mView: WeakReference<T>? = null
        private set

    private val compositeDisposable by lazy { CompositeDisposable() }

    override fun attachView(mRootView: T) {
        this.mView = WeakReference(mRootView)
    }

    /**
     * 解绑
     */
    override fun detachView() {
        mView?.clear()
        mView = null

        //保证activity结束时取消所有正在执行的订阅
        removeAll()
    }

    private val isViewAttached: Boolean
        get() = mView != null

    fun checkViewAttached() {
        if (!isViewAttached) throw MvpViewNotAttachedException()
    }

    protected fun removeAll() {
        if (compositeDisposable != null && !compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
            compositeDisposable.clear()
        }
    }

    fun add(disposable: Disposable) {
        if (disposable != null) {
            compositeDisposable.add(disposable)
        }
    }


    fun view(): T? {
        return mView?.get() ?: null
    }

    private class MvpViewNotAttachedException internal constructor() : RuntimeException("Please call IPresenter.attachView(IBaseView) before" + " requesting data to the IPresenter")
}