package com.xianghe.ivy.weight.download;

import com.google.gson.Gson;
import com.xianghe.ivy.network.GsonHelper;
import com.xianghe.ivy.weight.download.breakpoint.DownloadProgressListener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
*作者：created by huangjiang on 2018/10/11  下午5:27
*邮箱：504512336@qq.com
*描述： 下载 Retrofit 实例
*/
public class RetrofitMananger {
    public static DownloadService createService(DownloadProgressListener downloadProgressListener)
    {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
//        ExecutorService executorService = Executors.newSingleThreadExecutor();
        //特别注意下载大文件时千万不要打开下面注释代码 否则会导致OOM
        //.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()//
                .readTimeout(10, TimeUnit.SECONDS)//
                .connectTimeout(10, TimeUnit.SECONDS)//
                .addInterceptor(new DownloadProgressInterceptor(0,downloadProgressListener))
                .build();

        Gson gson = GsonHelper.getsGson();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://f5.market.xiaomi.com/download/AppStore/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .callbackExecutor(executorService) //默认CallBack回调在主线程进行,当设置下载大文件时需设置注解@Stream 不加这句话会报android.os.NetworkOnMainThreadException
                .build();

        DownloadService apiService = retrofit.create(DownloadService.class);

        return apiService;
    }
}
