package com.wwwjf.glidedemo

import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.Headers
import java.net.URL

/**
 * 添加headers、查找本地缓存
 */
class CustomGlideUrl: GlideUrl {

    constructor(url:String):super(url)

    constructor(url: String,headers: Headers):super(url, headers)

    constructor(url:URL):super(url)

    constructor(url: URL,headers: Headers):super(url, headers)

    override fun getCacheKey(): String {
        return super.getCacheKey()
    }

    override fun getHeaders(): MutableMap<String, String> {
        return super.getHeaders()
    }

}