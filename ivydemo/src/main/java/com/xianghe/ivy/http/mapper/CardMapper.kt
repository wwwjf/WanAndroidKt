package com.luckongo.tthd.net.mapper

import com.xianghe.ivy.http.mapper.HttpRespMapper
import com.xianghe.ivy.http.response.RespCode
import com.xianghe.ivy.model.response.BaseResponse


/**
 * @Author:  Ycl
 * @Date:  2018-03-21 15:37
 * @Desc:
 */
class CardMapper : HttpRespMapper {
    override fun getInfoCode(resp: Any?): Int {
        return -1
    }

    override fun isSuccess(resp: Any?): Boolean {
        return resp != null
    }

    override fun getErrorMessage(resp: Any?): String {
        return  "Http Request : UnknownError"
    }

    override fun getErrorCode(resp: Any?): Int {
        return  -1
    }
}