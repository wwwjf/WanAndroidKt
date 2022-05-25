package com.xianghe.ivy.mvp

/**
 * @Author:  ycl
 * @Date:  2018/10/22 16:38
 * @Desc:
 */
interface IBaseConctact {
    interface IBaseMode {
    }

    interface IBaseView {
    }

    interface IBasePresenter<in V : IBaseView> {

        fun attachView(mRootView: V)

        fun detachView()
    }
}