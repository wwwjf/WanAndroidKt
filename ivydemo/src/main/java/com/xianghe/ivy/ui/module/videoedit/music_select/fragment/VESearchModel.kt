package com.xianghe.ivy.ui.module.videoedit.music_select.fragment

import com.luckongo.tthd.net.mapper.RespMapper
import com.xianghe.ivy.constant.URL
import com.xianghe.ivy.http.request.NetworkRequest
import com.xianghe.ivy.model.MusicList1
import com.xianghe.ivy.model.response.BaseResponse
import io.reactivex.Observable

/**
 * @Author:  ycl
 * @Date:  2018/10/24 17:36
 * @Desc:
 */
class VESearchModel : VESearchContract.Model {
    override fun requestData(key:String,page:Int,pageSize: Int): Observable<BaseResponse<MusicList1>> {
        return NetworkRequest.postMap(
                URL.MUSIC_SEARCH, mapOf("keyword" to key,"page" to page , "pagesize" to pageSize),
                respMapper = RespMapper())
    }
}