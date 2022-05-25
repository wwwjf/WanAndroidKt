package com.xianghe.ivy.ui.module.videoedit.music_select.fragment

import com.luckongo.tthd.net.mapper.RespMapper
import com.xianghe.ivy.constant.RequestKey
import com.xianghe.ivy.constant.URL
import com.xianghe.ivy.http.request.NetworkRequest
import com.xianghe.ivy.model.MusicBean
import com.xianghe.ivy.model.response.BaseResponse
import io.reactivex.Observable

/**
 * @Author:  ycl
 * @Date:  2018/10/24 17:36
 * @Desc:
 */
class VEHotModel : VEHotContract.Model {
    override fun requestData(type: Int): Observable<BaseResponse<List<MusicBean>>> {
        return NetworkRequest.postMap(
                URL.MUSICLIST, mapOf(RequestKey.TYPE to type),
                respMapper = RespMapper())
    }
}