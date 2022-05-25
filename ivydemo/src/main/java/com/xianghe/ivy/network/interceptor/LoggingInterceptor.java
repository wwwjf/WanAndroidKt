package com.xianghe.ivy.network.interceptor;

import com.xianghe.ivy.utils.KLog;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 日志打印
 */
public class LoggingInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {

        Response response = chain.proceed(chain.request());
        okhttp3.MediaType mediaType = response.body().contentType();
        String content = response.body().string();
         KLog.e( "response body:" + content);
//        KLog.json(content);
        return response.newBuilder().body(okhttp3.ResponseBody.create(mediaType, content)).build();

    }
}
