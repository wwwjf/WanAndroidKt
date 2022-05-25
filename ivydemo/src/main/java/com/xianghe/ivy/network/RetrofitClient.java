package com.xianghe.ivy.network;

import com.xianghe.ivy.BuildConfig;
import com.xianghe.ivy.app.IvyApp;
import com.xianghe.ivy.app.URLS;
import com.xianghe.ivy.network.interceptor.CacheInterceptor;
import com.xianghe.ivy.utils.HttpLogger;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static volatile RetrofitClient mRetrofitClient;
    private Retrofit mRetrofit;
    private OkHttpClient mOkHttpClient;

    private RetrofitClient() {
        //设置Http缓存
        Cache cache = new Cache(new File(IvyApp.getInstance().getCacheDir(), "HttpCache"),
                1024 * 1024 * 10);
        mOkHttpClient = new OkHttpClient.Builder()
                .cache(cache)
//                .addInterceptor(new LoggingInterceptor())
                .addInterceptor(getHttpLoggingInterceptor())
                /*.addNetworkInterceptor(new LoadParamsInterceptor())*/
                .addNetworkInterceptor(new CacheInterceptor())
                .retryOnConnectionFailure(false)//不循环请求
                .connectTimeout(15L, TimeUnit.SECONDS)
                .writeTimeout(15L, TimeUnit.SECONDS)
                .readTimeout(15L, TimeUnit.SECONDS)
                .build();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(URLS.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(GsonHelper.getsGson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(mOkHttpClient)
                .build();
    }


    // 待验证
    public void setOkHttpClientTimeOut(long time) {


    }

    public static RetrofitClient getInstance() {
        if (mRetrofitClient == null) {
            synchronized (RetrofitClient.class) {
                if (null == mRetrofitClient) {
                    mRetrofitClient = new RetrofitClient();
                }
            }
        }
        return mRetrofitClient;
    }


    private HttpLoggingInterceptor getHttpLoggingInterceptor() {
        if (BuildConfig.DEBUG) {
            return new HttpLoggingInterceptor(new HttpLogger()).setLevel(HttpLoggingInterceptor.Level.BODY).setLevel(HttpLoggingInterceptor.Level.HEADERS);
        } else {
            return new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE);
        }
    }

    public <T> T create(Class<T> service) {
        return mRetrofit.create(service);
    }
}
