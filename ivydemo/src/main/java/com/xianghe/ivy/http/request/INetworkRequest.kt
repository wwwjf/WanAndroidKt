package com.xianghe.ivy.http.request
import com.google.gson.JsonElement
import io.reactivex.Observable
import java.lang.reflect.Type
/**
 * @Author:  ycl
 * @Date:  2018/10/22 17:04
 * @Desc:
 */
interface INetworkRequest {

    fun postString(url: String, params: Map<String, Any>): Observable<String>

    fun getString(url: String, params: Map<String, Any> = hashMapOf()): Observable<String>

    fun postRequest(url: String, params: Map<String, Any>): Observable<JsonElement>

    fun getRequest(url: String, params: Map<String, Any>): Observable<JsonElement>

    fun <T> parseString(json: String, type: Type): T

    fun <T> parseJsonElement(json: JsonElement, type: Type): T
}