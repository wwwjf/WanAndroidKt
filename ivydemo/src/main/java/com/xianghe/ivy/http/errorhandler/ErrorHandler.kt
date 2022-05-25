package com.xianghe.ivy.http.errorhandler

import com.blankj.utilcode.util.ActivityUtils
import com.xianghe.ivy.R
import com.xianghe.ivy.app.IvyApp
import com.xianghe.ivy.http.request.RequestError
import com.xianghe.ivy.http.response.RespCode
import com.xianghe.ivy.manager.UserInfoManager
import com.xianghe.ivy.mvp.BaseActivity
import com.xianghe.ivy.ui.module.welcom.XWLauncherActivity
import com.xianghe.ivy.utils.KLog
import io.reactivex.Observable
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.functions.Function
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException


/**
 * @Author:  ycl
 * @Date:  2018/10/22 17:17
 * @Desc:
 */
class ErrorHandler<D> : Function<Throwable, Observable<D>> {
    override fun apply(e: Throwable): Observable<D> {
        when (e) {
            is RequestError -> {
                KLog.i("${javaClass.simpleName}.Error" +
                        " Error code:--->${e.state}, Error message:--> ${e.message}")
                e.printStackTrace()
                if (e.state == RespCode.CODE_1000) {
                    UserInfoManager.exitUser()
                    // 弹出登录界面
                    (ActivityUtils.getTopActivity() as? BaseActivity)?.run {
                        if (this.umPageName() != XWLauncherActivity::class.java.simpleName) {
                            showLogin()
                        }
                    }
                    return Observable.error(RequestError(e.state,
                            IvyApp.getInstance().resources.getString(R.string. common_other_device_login),e.info_code))
                }
                return Observable.error(e)
            }
            is HttpException -> {
                KLog.i("${javaClass.simpleName}.Error" +
                        " Error code:--->${e.code()}, Error message:--> ${e.message}")
                e.printStackTrace()
                return Observable.error(
                        RequestError(e.code(), e.message()))
            }
            is SocketTimeoutException, is TimeoutException -> {
                return Observable.error(
                        RequestError(RespCode.CODE_901, IvyApp.getInstance().resources.getString(R.string.common_network_connect_timeout)))
            }
            is ConnectException, is UnknownHostException -> {
                return Observable.error(
                        RequestError(RespCode.CODE_901, IvyApp.getInstance().resources.getString(R.string.common_network_connect_no)))
            }
            is UndeliverableException -> {
                return Observable.error(
                        RequestError(RespCode.CODE_901, IvyApp.getInstance().resources.getString(R.string.common_network_connect_poor)))
            }
            is SocketException -> {
                return Observable.error(e)
            }
            else -> return Observable.error(e)
        }
    }
}
