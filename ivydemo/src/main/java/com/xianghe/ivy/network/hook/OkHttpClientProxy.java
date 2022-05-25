package com.xianghe.ivy.network.hook;

import okhttp3.OkHttpClient;

/**
 * author:  ycl
 * date:  2018/11/22 18:24
 * desc: hook 掉okhttp的超时时间方法
 */
public class OkHttpClientProxy extends OkHttpClient {
    private int connectTimeout;
    private int readTimeout;
    private int writeTimeout;


    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public void setWriteTimeout(int writeTimeout) {
        this.writeTimeout = writeTimeout;
    }

    public int connectTimeoutMillis() {
        return connectTimeout;
    }

    public int readTimeoutMillis() {
        return readTimeout;
    }

    public int writeTimeoutMillis() {
        return writeTimeout;
    }

}
