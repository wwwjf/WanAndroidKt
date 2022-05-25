package com.xianghe.ivy.http.request

import android.text.TextUtils
import com.blankj.utilcode.util.AppUtils
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.luckongo.tthd.net.mapper.RespMapper
import com.xianghe.ivy.R
import com.xianghe.ivy.app.IvyApp
import com.xianghe.ivy.app.IvyConstants
import com.xianghe.ivy.constant.Api
import com.xianghe.ivy.http.errorhandler.ErrorHandler
import com.xianghe.ivy.http.mapper.HttpRespMapper
import com.xianghe.ivy.http.response.RespCode
import com.xianghe.ivy.http.scheduler.SchedulerProvider
import com.xianghe.ivy.manager.UserInfoManager
import com.xianghe.ivy.network.GsonHelper
import com.xianghe.ivy.utils.DeviceUtils
import com.xianghe.ivy.utils.IvyUtils
import com.xianghe.ivy.utils.LanguageUtil
import com.xianghe.ivy.utils.NetworkUtil
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import java.lang.reflect.Type

/**
 * @Author:  ycl
 * @Date:  2018/10/22 17:08
 * @Desc:
 */
object NetworkRequest : INetworkRequest {

    private val mGson by lazy { GsonHelper.getsGson() }
    private val mRequestApi by lazy { IvyApp.getInstance().ivyService }
    private val mFullUrlPattern = Regex("^http[s]?:")

    // -----------------------------  输出 start ---------------------------------
    inline fun <reified D> get(url: String, params: Map<String, String> = hashMapOf(),
                               respMapper: HttpRespMapper= RespMapper(),
                               type: Type = object : TypeToken<D>() {}.type)
            : Observable<D> {
//        val type = object : TypeToken<D>() {}.type
        return getRequest(url, params)
                .subscribeOn(SchedulerProvider.io())
                .observeOn(SchedulerProvider.computation())
                .map {
                    parseJsonElement<D>(it, type)
                }
                .flatMap {
                    if (respMapper.isSuccess(it)) {
                        Observable.just(it as D)
                    } else {
                        Observable.error(RequestError(respMapper.getErrorCode(it),
                                respMapper.getErrorMessage(it),respMapper.getInfoCode(it)))
                    }
                }
                .observeOn(SchedulerProvider.ui())
                .onErrorResumeNext(ErrorHandler())
    }

    inline fun downLoadGet(url: String): Observable<ResponseBody>? {
        return getdownLoadRequest(url)
                .subscribeOn(SchedulerProvider.io())
                .observeOn(SchedulerProvider.io())
    }

    inline fun <reified D> postMap(url: String, params: Map<String, Any>,
                                   respMapper: HttpRespMapper,
                                   type: Type = object : TypeToken<D>() {}.type)
            : Observable<D> {
        //没网络主动抛出，程序决定是否处理
        if (!NetworkUtil.isNetworkConnected(IvyApp.getInstance().applicationContext)) {
            return Observable.error(RequestError(RespCode.CODE_901, IvyApp.getInstance().resources.getString(R.string.common_network_error)))
        }
        return postMap(url, params)
                .subscribeOn(SchedulerProvider.io())
                .observeOn(SchedulerProvider.computation())
                .map { it ->
                    try {
                        parseJsonElement<D>(it, type)
                    } catch (e: Exception) {
                        e.printStackTrace()
//                        GsonHelper.getsGson().fromJson(it, BaseResponse<String>::class.java)
                    }

                }
                .flatMap {
                    if (respMapper.isSuccess(it)) {
                        Observable.just(it as D)
                    } else {
                        Observable.error(RequestError(respMapper.getErrorCode(it),
                                respMapper.getErrorMessage(it),respMapper.getInfoCode(it)))
                    }
                }
                .observeOn(SchedulerProvider.ui())
                .onErrorResumeNext(ErrorHandler())
    }

    // 提供给java调用的方法
    fun <D> postFieldMap(url: String, params: Map<String, Any>,
                         respMapper: HttpRespMapper,
                         type: Type = object : TypeToken<D>() {}.type): Observable<D> {
        //没网络主动抛出，程序决定是否处理
        if (!NetworkUtil.isNetworkConnected(IvyApp.getInstance().applicationContext)) {
            return Observable.error(RequestError(RespCode.CODE_901, IvyApp.getInstance().resources.getString(R.string.common_network_error)))
        }
        return postFieldMap(url, params)
                .subscribeOn(SchedulerProvider.io())
                .observeOn(SchedulerProvider.computation())
                .map { it ->
                    try {
                        parseJsonElement<D>(it, type)
                    } catch (e: Exception) {
                        e.printStackTrace()
//                        GsonHelper.getsGson().fromJson(it, BaseResponse<String>::class.java)
                    }
                }
                .flatMap {
                    if (respMapper.isSuccess(it)) {
                        Observable.just(it as D)
                    } else {
                        Observable.error(RequestError(respMapper.getErrorCode(it),
                                respMapper.getErrorMessage(it),respMapper.getInfoCode(it)))
                    }
                }
                .observeOn(SchedulerProvider.ui())
                .onErrorResumeNext(ErrorHandler())
    }

    // 提供给java调用的方法
    fun <D> postMapByJAVA(url: String, params: Map<String, Any>,
                          respMapper: HttpRespMapper,
                          type: Type = object : TypeToken<D>() {}.type): Observable<D> {
        //没网络主动抛出，程序决定是否处理
        if (!NetworkUtil.isNetworkConnected(IvyApp.getInstance().applicationContext)) {
            return Observable.error(RequestError(RespCode.CODE_901, IvyApp.getInstance().resources.getString(R.string.common_network_error)))
        }
        return postMap(url, params)
                .subscribeOn(SchedulerProvider.io())
                .observeOn(SchedulerProvider.computation())
                .map { it ->
                    try {
                        parseJsonElement<D>(it, type)
                    } catch (e: Exception) {
                        e.printStackTrace()
//                        GsonHelper.getsGson().fromJson(it, BaseResponse<String>::class.java)
                    }
                }
                .flatMap {
                    if (respMapper.isSuccess(it)) {
                        Observable.just(it as D)
                    } else {
                        Observable.error(RequestError(respMapper.getErrorCode(it),
                                respMapper.getErrorMessage(it),respMapper.getInfoCode(it)))
                    }
                }
                .observeOn(SchedulerProvider.ui())
                .onErrorResumeNext(ErrorHandler())
    }

    // 提供给java调用的方法
    fun <D> postMultipartByJAVA(url: String, params: Map<String, RequestBody>,
                                file: MultipartBody.Part,respMapper: HttpRespMapper,
                                type: Type = object : TypeToken<D>() {}.type): Observable<D> {
        //没网络主动抛出，程序决定是否处理
        if (!NetworkUtil.isNetworkConnected(IvyApp.getInstance().applicationContext)) {
            return Observable.error(RequestError(RespCode.CODE_901, IvyApp.getInstance().resources.getString(R.string.common_network_error)))
        }
        return postMultiPart(url, params,file)
                .subscribeOn(SchedulerProvider.io())
                .observeOn(SchedulerProvider.computation())
                .map { it ->
                    try {
                        parseJsonElement<D>(it, type)
                    } catch (e: Exception) {
                        e.printStackTrace()
//                        GsonHelper.getsGson().fromJson(it, BaseResponse<String>::class.java)
                    }
                }
                .flatMap {
                    if (respMapper.isSuccess(it)) {
                        Observable.just(it as D)
                    } else {
                        Observable.error(RequestError(respMapper.getErrorCode(it),
                                respMapper.getErrorMessage(it),respMapper.getInfoCode(it)))
                    }
                }
                .observeOn(SchedulerProvider.ui())
                .onErrorResumeNext(ErrorHandler())
    }

    // -----------------------------  输出 end ---------------------------------

    override fun postString(url: String, params: Map<String, Any>): Observable<String> =
            postRequest(url, params)
                    .observeOn(SchedulerProvider.computation())
                    .map { mGson.toJson(it) }
                    .observeOn(SchedulerProvider.ui())
                    .onErrorResumeNext(ErrorHandler())

    override fun getString(url: String, params: Map<String, Any>): Observable<String> =
            getRequest(url, params)
                    .observeOn(SchedulerProvider.computation())
                    .map { mGson.toJson(it) }
                    .observeOn(SchedulerProvider.ui())
                    .onErrorResumeNext(ErrorHandler())

    override fun postRequest(url: String, params: Map<String, Any>): Observable<JsonElement> =
            (if (url.contains(mFullUrlPattern)) {
                mRequestApi.postMapByFullUrl(url, createParams(params))
            } else {
                mRequestApi.postMap(url, createParams(params))
            })
                    .subscribeOn(SchedulerProvider.io())
                    .onErrorResumeNext(ErrorHandler())

    override fun getRequest(url: String, params: Map<String, Any>): Observable<JsonElement> =
            (if (url.contains(mFullUrlPattern)) {
                mRequestApi.getByFullUrl(url, createParams(params))
            } else {
                mRequestApi.get(url, createParams(params))
            })
                    .subscribeOn(SchedulerProvider.io())
                    .observeOn(SchedulerProvider.ui())
                    .onErrorResumeNext(ErrorHandler())

    fun postMap(url: String, params: Map<String, Any>)
            : Observable<JsonElement> {
        return (if (url.contains(mFullUrlPattern)) {
            mRequestApi.postMapByFullUrl(url, createParams(params))
        } else {
            mRequestApi.postMap(url, createParams(params))
        })
                .subscribeOn(SchedulerProvider.io())
                .onErrorResumeNext(ErrorHandler())
    }

    fun postFieldMap(url: String, params: Map<String, Any>): Observable<JsonElement> {
        return mRequestApi.postFieldMap(url,params).subscribeOn(SchedulerProvider.io())
                .onErrorResumeNext(ErrorHandler())
    }

    fun postMultiPart(url: String, params: Map<String, RequestBody>,file: MultipartBody.Part)
            : Observable<JsonElement> {
        return (if (url.contains(mFullUrlPattern)) {
            if(file == null){
                mRequestApi.postMultipartByFullUrl(url,params);
            }else{
                mRequestApi.postMultipartByFullUrl(url, params,file)
            }
        } else {
            if (file == null){
                mRequestApi.postMultipart(url,params);
            }else{
                mRequestApi.postMultipart(url, params,file)
            }
        })
                .subscribeOn(SchedulerProvider.io())
                .onErrorResumeNext(ErrorHandler())
    }

    fun getdownLoadRequest(url: String)
            : Observable<ResponseBody>{
        return mRequestApi.getDownLoad(url)
    }


    override fun <T> parseString(json: String, type: Type): T = mGson.fromJson(json, type)
    override fun <T> parseJsonElement(json: JsonElement, type: Type): T = mGson.fromJson(json, type)
    private fun createParams(map: Map<String, Any>): Map<String, Any> = map.toSortedMap().apply {
        //用户token
        if (UserInfoManager.isLogin()) {
            this.put("ticket", UserInfoManager.getTicket())
        }
        //Android客户端
        this.put(Api.Key.Global.CLIENT_SOURCE, Api.Value.ClientSource.ANDROID.toString())
        //客户端系统版本
        this.put(Api.Key.Global.CLIENT_SYSTEM, IvyUtils.getClientSystem())
        // 临时设置的设备Id
        this.put(Api.Key.Global.MOBILE_UNIQUE_CODE, DeviceUtils.getDeviceInfo(IvyApp.getInstance().applicationContext))
        //APP版本
        this.put(Api.Key.Global.VERSION, AppUtils.getAppVersionName())
        //客户端语言
        var language = Api.Value.ClientLanguage.ZH_CN
        if (!LanguageUtil.isSimplifiedChinese(IvyApp.getInstance())) {
            language = Api.Value.ClientLanguage.EN_US
        }
        this.put(Api.Key.Global.CLIENT_LANGUAGE, language)
        //签名为空，生成签名
        var sign = IvyConstants.signValue
        if (TextUtils.isEmpty(sign)) {
            val stringBuilder = StringBuilder()
            val it = this.entries.iterator()
            while (it.hasNext()) {
                val entry = it.next()
                stringBuilder.append(entry.key).append("=").append(entry.value).append("&")
            }
            //            KLog.e("stringBuilder="+stringBuilder.substring(0,stringBuilder.length()-1));
            sign = IvyUtils.getSign(stringBuilder.substring(0, stringBuilder.length - 1))
            //            KLog.e(, "intercept: sign=" + sign);
        }
        //请求数据签名
        this.put("sign", sign)
    }

}