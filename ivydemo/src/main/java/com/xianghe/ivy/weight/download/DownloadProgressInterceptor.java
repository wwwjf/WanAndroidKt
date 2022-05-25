package com.xianghe.ivy.weight.download;


import com.xianghe.ivy.weight.download.breakpoint.DownloadProgressListener;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
*作者：created by huangjiang on 2018/10/11  下午5:25
*邮箱：504512336@qq.com
*描述：拦截器
*/
public class DownloadProgressInterceptor implements Interceptor {
    private DownloadProgressInterceptor listener;
    private int code;
    private DownloadProgressListener mDownloadProgressListener;
    public DownloadProgressInterceptor(int code,DownloadProgressListener downloadProgressListener)
    {
        this.code = code;
        this.mDownloadProgressListener = downloadProgressListener;
    }

    @Override
    public Response intercept(Chain chain) throws IOException
    {
        Response originalResponse = chain.proceed(chain.request());
        return originalResponse.newBuilder()
                .body(new DownloadProgressResponseBody(originalResponse.body(),code)
                        .setDownloadProgressListener(mDownloadProgressListener))
                .build();
    }
}
