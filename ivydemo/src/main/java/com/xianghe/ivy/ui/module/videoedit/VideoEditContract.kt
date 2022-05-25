package com.xianghe.ivy.ui.module.videoedit

import com.xianghe.ivy.model.MusicList
import com.xianghe.ivy.model.MusicTagBean
import com.xianghe.ivy.model.response.BaseResponse
import com.xianghe.ivy.mvp.loadPager.IBaseLoadingConctact
import io.reactivex.Observable

/**
 * @Author: ycl
 * @Date: 2018/10/25 9:47
 * @Desc:
 */
class VideoEditContract {
    interface Model : IBaseLoadingConctact.IBaseMode {
        fun requestMusicTag(): Observable<BaseResponse<List<MusicTagBean>>>
        fun requestBackgroundMusicList(page: Int, pageSize: Int): Observable<BaseResponse<MusicList>>
        fun requestMusic_count(id: String): Observable<BaseResponse<*>>
    }

    interface View : IBaseLoadingConctact.IBaseView {
        fun onMusicTagListSuccess(list: List<MusicTagBean>)
        fun onLoadBackgroundMusics(data: MusicList?)
        fun onLoadBackgroundMusicsError();
    }

    interface Presenter : IBaseLoadingConctact.IBasePresenter<VideoEditContract.View> {
        fun requestMusicTag()

        fun loadBackgroundMusics(page: Int, pageSize: Int);
        fun music_count(id: String)
    }
}
