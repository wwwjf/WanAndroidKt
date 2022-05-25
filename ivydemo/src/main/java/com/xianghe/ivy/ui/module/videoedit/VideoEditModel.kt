package com.xianghe.ivy.ui.module.videoedit

import com.luckongo.tthd.net.mapper.RespMapper
import com.xianghe.ivy.constant.Api
import com.xianghe.ivy.constant.URL
import com.xianghe.ivy.http.request.NetworkRequest
import com.xianghe.ivy.model.MusicList
import com.xianghe.ivy.model.MusicTagBean
import com.xianghe.ivy.model.response.BaseResponse
import io.reactivex.Observable

/**
 * @Author:  ycl
 * @Date:  2018/10/24 17:36
 * @Desc:
 */
class VideoEditModel : VideoEditContract.Model {
    override fun requestBackgroundMusicList(page: Int, pageSize: Int): Observable<BaseResponse<MusicList>> {
        return NetworkRequest.postMap(Api.Route.Index.BACKGROUND_MUSIC_LIST,
                mapOf(Api.Key.PAGE to page, Api.Key.PAGE_SIZE to pageSize),
                respMapper = RespMapper())
    }

    override fun requestMusic_count(id: String): Observable<BaseResponse<*>> {
        return NetworkRequest.postMap(Api.Route.Index.MUSIC_COUNT,
                mapOf("id" to id),
                respMapper = RespMapper())
    }

    override fun requestMusicTag(): Observable<BaseResponse<List<MusicTagBean>>> {
        return NetworkRequest.postMap(
                URL.MUSIC_TYPE, mapOf(),
                respMapper = RespMapper())
    }
}