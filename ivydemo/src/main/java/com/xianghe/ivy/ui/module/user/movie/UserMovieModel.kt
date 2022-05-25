package com.xianghe.ivy.ui.module.user.movie

import com.luckongo.tthd.net.mapper.RespMapper
import com.xianghe.ivy.constant.Api
import com.xianghe.ivy.http.request.NetworkRequest
import com.xianghe.ivy.model.CategoryMovieBean
import com.xianghe.ivy.model.response.BaseResponse
import io.reactivex.Observable

class UserMovieModel : UserMovieContract.Model {

    override fun requestData(uid: Long, page: Int): Observable<BaseResponse<List<CategoryMovieBean>>> {
        return NetworkRequest.postMap(Api.Route.User.MEDIALIST,
                mapOf("uid" to uid,
                        "page" to page),
                respMapper = RespMapper())
    }

    override fun deleteUserMovie(movieId: Long): Observable<BaseResponse<String>> {

        return NetworkRequest.postMap(Api.Route.User.DELETE_USERMOVIE,
                mapOf("media_id" to movieId),
                respMapper = RespMapper())
    }
}