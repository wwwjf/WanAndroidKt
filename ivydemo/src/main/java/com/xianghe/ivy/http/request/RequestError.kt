package com.xianghe.ivy.http.request

/**
 * @Author:  ycl
 * @Date:  2018/10/22 17:23
 * @Desc:
 */
class RequestError : Throwable {
    var state: Int
    var info_code: Int = -1
    override var message: String

    constructor(state: Int, message: String) : super(message) {
        this.state = state
        this.message = message
    }

    constructor(state: Int, message: String, info_code: Int) : super(message) {
        this.state = state
        this.message = message
        this.info_code = info_code
    }

    override fun toString(): String {
        return "${javaClass.simpleName}{state=$state,info_code=$info_code,message=$message}"
    }
}