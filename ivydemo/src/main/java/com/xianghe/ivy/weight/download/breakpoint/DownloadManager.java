package com.xianghe.ivy.weight.download.breakpoint;

import android.os.Environment;

import com.xianghe.ivy.BuildConfig;
import com.xianghe.ivy.app.URLS;
import com.xianghe.ivy.utils.KLog;
import com.xianghe.ivy.weight.download.DownloadProgressInterceptor;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 下载管理
 * Created by ${R.js} on 2018/3/22.
 */

public class DownloadManager implements DownloadProgressListener {

    private DownloadInfo info;
    private ProgressListener progressObserver;
    private File outFile;
    private Disposable subscribe;
    private DownLoadService service;
    private long currentRead;
    private static DownloadManager manager;

    private DownloadManager() {
        info = new DownloadInfo();
        outFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "yaoshi.apk");
        info.setSavePath(outFile.getAbsolutePath());
    }

    public void setSavePath(File file) {
        info.setSavePath(file.getPath());
    }


    /**
     * 获取单例引用
     *
     * @return
     */
    public static DownloadManager getInstance() {
        if (manager == null) {
            synchronized (DownloadManager.class) {
                if (manager == null) {
                    manager = new DownloadManager();
                }
            }
        }
        return manager;
    }

    @Override
    public void progress(long read, final long contentLength, final boolean done,int type) {
        KLog.e("progress : ", "read = " + read + "contentLength = " + contentLength);
        // 该方法仍然是在子线程，如果想要调用进度回调，需要切换到主线程，否则的话，会在子线程更新UI，直接错误
        // 如果断电续传，重新请求的文件大小是从断点处到最后的大小，不是整个文件的大小，info中的存储的总长度是
        // 整个文件的大小，所以某一时刻总文件的大小可能会大于从某个断点处请求的文件的总大小。此时read的大小为
        // 之前读取的加上现在读取的
//        if (info.getContentLength() > contentLength) {
//            read = read + (info.getContentLength() - contentLength);
//        } else {
        info.setContentLength(contentLength);
//        }
        info.setReadLength(read);

        Observable.just(1).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                if (progressObserver != null) {
//                    progressObserver.progressChanged(info.getReadLength(), info.getContentLength(), done);
                }
            }
        });
    }

    /**
     * 开始下载
     *
     * @param url
     */
    public void start(String url) {
        info.setUrl(url);
//        final DownloadInterceptor interceptor = new DownloadInterceptor(this);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(8, TimeUnit.SECONDS);
//        builder.addInterceptor(interceptor);
        builder.addInterceptor(new DownloadProgressInterceptor(1,this));
        Retrofit retrofit = new Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(URLS.BASE_URL)
                .build();
        if (service == null) {
            service = retrofit.create(DownLoadService.class);
            info.setService(service);
        } else {
            service = info.getService();
        }

        downLoad();
    }

    /**
     * 开始下载
     */
    private void downLoad() {
        KLog.e("下载：", info.toString());
        subscribe = service.download("bytes=" + info.getReadLength() + "-", info.getUrl())
                /*指定线程*/
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .retryWhen(new RetryWhenNetworkException())
                /* 读取下载写入文件，并把ResponseBody转成DownInfo */
                .map(new Function<ResponseBody, ResponseBody>() {
                    @Override
                    public ResponseBody apply(ResponseBody responseBody) throws Exception {
                        BufferedSink sink = null;
                        //下载文件到本地
                        try {
                            File file = new File(info.getSavePath());
                            sink = Okio.buffer(Okio.sink(file));
                            sink.writeAll(responseBody.source());
                        } catch (Exception e) {
                            if (e != null) {
                                e.printStackTrace();
                            }
                        } finally {
                            try {
                                if (sink != null) sink.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

//                        try {
//                            //写入文件
//                            FileUtil.writeCache(responseBody, new File(info.getSavePath()), info);
//                        } catch (IOException e) {
//                            progressObserver.err();
//                            KLog.e("异常:", e.toString());
//                        }
                        return responseBody;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        KLog.e("下载", "onNext");

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        KLog.e("下载", "onError" + throwable.toString());
                        progressObserver.err();
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        KLog.e("下载", "onCompleted");
                    }
                }, new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {

                    }
                });
    }

    /**
     * 暂停下载
     */
    public void pause() {
        KLog.e("pause");
        if (subscribe != null && !subscribe.isDisposed()) {
            subscribe.dispose();
        }
    }

    /**
     * 继续下载
     */
    public void reStart() {
        downLoad();
    }

    /**
     * 进度监听
     */
    public interface ProgressListener {
//        void progressChanged(long read, long contentLength, boolean done);

        void err();
    }

    public void setProgressListener(ProgressListener progressObserver) {
        this.progressObserver = progressObserver;
    }
}
