package com.xianghe.ivy.http.mapper

/**
 * @Author:Ycl
 * @Date:2017-10-05 19:19
 * @Desc:
 */
interface HttpRespMapper {

    fun isSuccess(resp: Any?): Boolean

    fun getErrorMessage(resp: Any?): String

    fun getErrorCode(resp: Any?): Int

    fun getInfoCode(resp: Any?): Int
}