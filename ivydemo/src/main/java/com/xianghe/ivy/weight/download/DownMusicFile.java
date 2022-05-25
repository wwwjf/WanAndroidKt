package com.xianghe.ivy.weight.download;

import android.app.Activity;

import com.xianghe.ivy.R;
import com.xianghe.ivy.app.IvyApp;
import com.xianghe.ivy.ui.media.config.MediaConfig;
import com.xianghe.ivy.utils.FileUtils;
import com.xianghe.ivy.utils.KLog;
import com.xianghe.ivy.utils.NetworkUtil;
import com.xianghe.ivy.weight.download.breakpoint.DownloadProgressListener;

import java.io.File;
import java.io.IOException;

import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 作者：created by huangjiang on 2018/9/14  21:38
 * 邮箱：504512336@qq.com
 * 描述：网络音乐下载
 */
public class DownMusicFile {

    private static DownMusicFile sDownMusicFile;

    private onDownloadProgressCallBack callBack;
    private Activity activity;
    private String url;
    private File downloadFile;
    private Call<ResponseBody> call;

    public static DownMusicFile getInstance() {
        if (sDownMusicFile == null) {
            synchronized (DownMusicFile.class) {
                if (sDownMusicFile == null) {
                    sDownMusicFile = new DownMusicFile();
                }
            }
        }
        return sDownMusicFile;
    }

    public void downloadFile(Activity activity, String musicLink, String musicName, String musicId, onDownloadProgressCallBack callBack) {
        KLog.i("musicId: " + musicId + " musicName: " + musicName + " musicLink: " + musicLink);
        SaveDownMusicFile downMusicFile = SaveDownMusicFile.getInstance();
        if (!downMusicFile.hasFile()) {
            if (callBack != null) {
                callBack.onErrorDownload(new Throwable(activity.getResources().getString(R.string.mp3_file_save_failed)));
            }
            return;
        }
        String musicPath = downMusicFile.readMusicFile(SaveDownMusicFile.getKey(musicName, musicId));
        if (null == musicPath || musicPath.equals("")) {
            KLog.e(musicLink);
            //音乐文件不存在时，重新下载
            loadFile(activity, musicLink, musicName, musicId, callBack);
        } else {
            File file = new File(musicPath);
            //音乐文件不存在时，重新下载
            if (!file.exists()||file.length()==0 ){
                loadFile(activity, musicLink, musicName, musicId, callBack);
            } else {
                if (callBack != null) {
                    callBack.onSuccessDownload(musicPath);
                }
            }
        }
    }



    private void loadFile(Activity activity, String url, String musicName, String musicId, onDownloadProgressCallBack callBack) {
        if (!NetworkUtil.isNetworkConnected(IvyApp.getInstance().getApplicationContext())) {
            if (callBack != null) {
                callBack.onErrorDownload(new Throwable(activity.getResources().getString(R.string.common_network_error)));
            }
            return;
        }
        this.activity = activity;
        this.url = url;
        this.callBack = callBack;
        this.downloadFile = FileUtils.createTempFile(MediaConfig.AUDIO_DIR_DOWNLOAD_MUSIC, musicName + MediaConfig.AUDIO_MP3);
        if (downloadFile != null) {
            KLog.i("downloadFile: " + downloadFile.getAbsolutePath());
            request(musicName, musicId);
        } else {
            KLog.i("downloadFile: " + null);
            if (callBack != null) {
                callBack.onErrorDownload(new Throwable(activity.getResources().getString(R.string.mp3_file_create_failed)));
            }
        }
    }

    private void request(String musicName, String musicId) {
        DownloadService downLoadService = RetrofitMananger.createService(new DownloadProgressListener() {
            @Override
            public void progress(long read, long contentLength, boolean done, int type) {
                KLog.d("done: " + done + " progress: " + (int) (read * 100 / contentLength));
                if (type == 0) {
                    if (done) {
                        // 保存文件
                        SaveDownMusicFile.getInstance().saveMusicToFile(SaveDownMusicFile.getKey(musicName, musicId), downloadFile.getPath());
                        // 返回回调
                        if (activity != null && !activity.isFinishing()) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (callBack != null) {
                                        callBack.onSuccessDownload(downloadFile.getPath());
                                    }
                                }
                            });
                        }
                    } else {
                        if (activity != null && !activity.isFinishing()) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (callBack != null) {
                                        callBack.onProgressDownload((int) (read * 100 / contentLength));
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
        call = downLoadService.downloadLargeFile(url);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                KLog.d("response: " + response);
                if (response.isSuccessful()) {
                    BufferedSink sink = null;
                    //下载文件到本地
                    try {
                        sink = Okio.buffer(Okio.sink(downloadFile));
                        sink.writeAll(response.body().source());
                    } catch (Exception e) {
                        e.printStackTrace();
                        KLog.e("e:" + e);
                        if ("Socket closed".equals(e.getMessage())) {
                            onFailure(null, new Throwable("取消下载"));
                        } else {
                            onFailure(null, new Throwable("网络较差，下载失败"));
                        }
                    } finally {
                        try {
                            if (sink != null) sink.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            KLog.e("e:" + e);
                        }
                    }
                    KLog.d("response: complete");
                } else {
                    KLog.d("DownloadActivity", "==responseCode==" + response.code() + "");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                KLog.d("下载失败", t.getMessage());
                deleteFile();
                if (activity != null && !activity.isFinishing()) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (callBack != null) {
                                callBack.onErrorDownload(t);
                            }
                        }
                    });
                }
            }
        });
    }


    public void cancle() {
        if (call != null && !call.isCanceled()) {
            call.cancel();
           /* if (activity != null) {
                activity = null;
            }
            if (callBack != null) {
                callBack = null;
            }*/
        }
    }

    public void cancleAnddeleteFile() {
        if (call != null && !call.isCanceled()) {
            call.cancel();
           /* if (activity != null) {
                activity = null;
            }
            if (callBack != null) {
                callBack = null;
            }*/
            deleteFile();
        }
    }

    private void deleteFile() {
        if (downloadFile != null) {
            FileUtils.deleteTempFile(downloadFile);
//            downloadFile = null;
        }
    }
}
