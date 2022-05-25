package com.xianghe.ivy.weight.download.breakpoint;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * 下载进度拦截器
 * Created by ${R.js} on 2018/3/22.
 */

public class DownloadInterceptor implements Interceptor {

    private DownloadProgressListener listener;
    private int code;

    public DownloadInterceptor(int code,DownloadProgressListener listener) {
        this.listener = listener;
        this.code = code;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());

        return originalResponse.newBuilder()
                .body(new DownloadResponseBody(originalResponse.body(), code,listener))
                .build();
    }
}
