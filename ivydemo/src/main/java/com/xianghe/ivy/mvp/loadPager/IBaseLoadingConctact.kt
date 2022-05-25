package com.xianghe.ivy.mvp.loadPager

import com.xianghe.ivy.mvp.IBaseConctact

/**
 * @Author:  ycl
 * @Date:  2018/10/22 16:38
 * @Desc:
 */
interface IBaseLoadingConctact {
    interface IBaseMode : IBaseConctact.IBaseMode {
    }

    interface IBaseView : IBaseConctact.IBaseView {
        fun <E> onError(e: E?)

        fun <D> onSuccess(d: D?)

        fun showLoading()

        fun dismissLoading()
    }

    interface IBasePresenter<in V : IBaseView> : IBaseConctact.IBasePresenter<V> {
    }


}