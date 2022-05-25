package com.xianghe.ivy.ui.module.videoedit

import com.xianghe.ivy.R
import com.xianghe.ivy.app.IvyApp
import com.xianghe.ivy.mvp.loadPager.BaseLoadingPresenter
import com.xianghe.ivy.utils.KLog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * @Author: ycl
 * @Date: 2018/10/24 17:12
 * @Desc:
 */
class VideoEditPresenter : BaseLoadingPresenter<VideoEditContract.View>(), VideoEditContract.Presenter {

    private val TAG = "VideoEditPresenter"

    override fun loadBackgroundMusics(page: Int, pageSize: Int) {
        KLog.d(TAG, "获取背景音乐: page = " + page + ", pageSize = " + pageSize)
        //add(mModel.requestBackgroundMusicList())
        add(mModel.requestBackgroundMusicList(page, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    KLog.d(TAG, it.toString())
                    view()?.onLoadBackgroundMusics(it.data)
                }, {
                    view()?.onLoadBackgroundMusicsError();
                }))
    }
    override fun music_count(id: String) {
        KLog.d(TAG, "音乐应用统计: id = " + id )
        //add(mModel.requestBackgroundMusicList())
        add(mModel.requestMusic_count(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    KLog.d(TAG, "音乐应用统计: ok ")
                }, {
                    KLog.d(TAG, "音乐应用统计: error ")
                }))
    }

    override fun requestMusicTag() {
        showLoading()
        add(mModel.requestMusicTag()
                .subscribe({
                    clossLoading()
                    if (it.data.isEmpty()) {
                        onError(IvyApp.getInstance().getString(R.string.common_no_music))
                    } else {
                        view()?.onMusicTagListSuccess(it.data)
                    }
                }, {
                    clossLoading()
                    onError(it)
                }))
    }

    private val mModel by lazy { VideoEditModel() }
}
