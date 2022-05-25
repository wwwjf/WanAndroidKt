package com.xianghe.ivy.manager.video;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.tangyx.video.ffmpeg.FFmpegUtils;
import com.wwwjf.mp4processor.av.Mp4Processor;
import com.wwwjf.mp4processor.utils.VideoUtils;
import com.xianghe.ivy.BuildConfig;
import com.xianghe.ivy.R;
import com.xianghe.ivy.app.IvyApp;
import com.xianghe.ivy.app.greendao.GreenDaoManager;
import com.xianghe.ivy.entity.dao.UploadTaskCacheDao;
import com.xianghe.ivy.entity.db.UploadTaskCache;
import com.xianghe.ivy.http.request.RequestError;
import com.xianghe.ivy.http.response.RespCode;
import com.xianghe.ivy.ui.media.config.MediaConfig;
import com.xianghe.ivy.utils.FileUtils;
import com.xianghe.ivy.utils.IvyUtils;
import com.xianghe.ivy.utils.KLog;

import java.io.File;
import java.util.List;


/**
 * 上传流程：
 *      默认的视频->
 *      1.  如果没有添加导演，演员信息，也需要添加logo,位置，概述的水印，水印添加完成，存储数据库hasWaterMark=true，防止后续中断，下次进入继续
 *      2.  水印添加完成，执行addHeaderInfo，movflags模式，（目的是把MP4的容器moov置前，后续的视频可以先加载视频流信息，快速被预览，提高加载速度）
 *      ，执行完成，hasAddHeaderInfo=true，防止后续中断之后，下次进入界面，直接执行后续的流程
 *      3.  视频处理完成，使用oss直接开始上传，待视频上传完成
 *      （具体上传完成，会回调界面的监听器，DownloadActivity是所有activity的基类，所以都会获取到监听器，具体监听器UploadManager.uploadListener，
 *      上传成功就上传界面recycleView旁边的列表的任务Item，并刷新，并执行删除视频文件操作，删除视频的第一帧图，删除视频上传断点记录oss文件操作）
 *
 *      备注：
 *         1> 被中断的视频，进入重新上传，直接根据数据库取出的数据字段判断，之前走在那个流程，继续执行上传操作，
 *          先判断hasAddHeaderInfo，在判断hasWaterMark，如果都没有，就继续添加水印
 *         2> 具体添加水印是使用，mediaCodec硬解码视频，获取到每一帧，将其绘制到OpenGLES里面去，在使用其多重纹理，在上面叠加水印的纹理，绘制在OpenGLES里面去，
 *         从OPenGLES里面取出数据，放置在编码器，一帧一帧处理完成，添加在视频轨道的合并器里面去，在解包装音频，放置在合并器，执行音视频合并，完成视频添加水印处理
 *         （漏洞，如果视频不支持硬解码，直接挂，待后续使用ffmpeg软解码弥补，时间不够啊，搞着就1周时间才搞出来，加上不是特别懂）
 *
 */
public class VideoTransUtils {
    private static final String TAG = "VideoTransUtils";
    private Mp4Processor mMp4Processor;
    private Object addWaterLock = new Object();  // 保证唯一
    private Object addHeadLock = new Object();  // 保证唯一
    private boolean cancleAddHeaderInfo = false; // 添加水印取消之后，无法执行取消操作，所以只有保持状态，待完成后取消
    private boolean cancleWaterMark = false; // 添加水印取消之后，不再执行complete方法

    public void start(UploadTaskCache cache) {

        // 不是走视频逻辑(上传图片或者其他)，cache 为null



        // 已经添加水印了，添加头部信息之后，再开始上传
        if (cache.isHasWaterMark()) {
            cancleAddHeaderInfo = false;
//            addHeaderInfo(cache, uploadUtils);
            return;
        }

        File f = new File(cache.getFilePath());
        if (f==null||!f.exists() || f.length() == 0) {
//            setCacheStates(cache, UploadManager.Status.ERROR);
//            sendHandlerMessage(uploadUtils, UploadManager.uploadStatus.ERROR,
//                    new RequestError(RespCode.CODE_614, IvyApp.getInstance().getResources().getString(R.string.file_no_exist)));
            return;
        }

        // 还没有水印的添加水印之后，在开始上传
        cancleWaterMark = false;
        synchronized (addWaterLock) {
            File outfile = FileUtils.createTempFile(MediaConfig.VIDEO_XH_RECORDER_PROCESS, System.currentTimeMillis() + MediaConfig.VIDEO_NAME);
            if (outfile != null) {
//                setCacheStates(cache, UploadManager.Status.VIDEO_START);
                try {
                    KLog.d(TAG, "start:  video trans start");
                    mMp4Processor = VideoUtils.start(cache.getFilePath(),
                            outfile.getAbsolutePath(),
                            R.mipmap.logo,
                            R.drawable.icon_movie_water_mark,
                            R.mipmap.icon_place_stroke,
                            cache.getDirector() == null || TextUtils.isEmpty(cache.getDirector()) ? null : cache.getDirector().split(","),
                            cache.getPlayer() == null || TextUtils.isEmpty(cache.getPlayer()) ? null : cache.getPlayer().split(","),
                            cache.getDesc(),
                            cache.getTitle(),
                            cache.getLocation(),
                            IvyApp.getInstance().getApplicationContext(),
                            new Mp4Processor.OnProgressListener() {
                                @Override
                                public void onProgress(long max, long current) {
                                    KLog.d(TAG, "onProgress:  max: " + max + " current: " + current);

//                                    setCacheStates(cache, UploadManager.Status.VIDEO_RUNNING);
                                    cache.setOffset(current);
                                    cache.setTotelLength(max);
//                                    sendHandlerMessage(uploadUtils, UploadManager.uploadStatus.PROGRESS, cache);
                                }

                                @Override
                                public void onComplete(String path) {
                                    KLog.d(TAG, "onComplete: video trans complete：   path: " + path);

                                    if (cancleWaterMark) {
                                        KLog.d(TAG, "onComplete: cancleWaterMark == true  已经退出了，不再执行 " );
                                        return;
                                    }

                                    File f = new File(path);
                                    if (f==null||!f.exists() || f.length() == 0) {
//                                        setCacheStates(cache, UploadManager.Status.ERROR);
//                                        sendHandlerMessage(uploadUtils, UploadManager.uploadStatus.ERROR, new RequestError(RespCode.CODE_614, IvyApp.getInstance().getResources().getString(R.string.upload_this_media_no_support)));
                                        return;
                                    }

//                                    setCacheStates(cache, UploadManager.Status.VIDEO_COMPLETED);
                                    cache.setOffset(0);
                                    cache.setTotelLength(0);
//                                    sendHandlerMessage(uploadUtils, UploadManager.uploadStatus.PROGRESS, path); // 此处不是完成，只是进度达到了100%

                                    KLog.d(TAG, "onComplete: update before： " + cache.toString());

                                    // --------------------------  添加水印成功，设置成功状态，并把添加水印成功的目录设置到上传目录 start  -------------------------
                                    // 设置装换成功状态
                                    cache.setHasWaterMark(true);

                                    // 修改上传文件是装换之后的文件, outPath 变成是原来的文件
                                    String tempPath = cache.getFilePath();
                                    cache.setFilePath(path);
                                    cache.setOutFilePath(tempPath);

                                    /*if (uploadUtils.getBuilder() != null) {
                                        uploadUtils.getBuilder().setFilePath(path);
                                    }*/

                                    KLog.d(TAG, "onComplete: update after： " + cache.toString());
                                    // 存储到本地去
                                    UploadTaskCacheDao dao = GreenDaoManager.getInstance().getUploadTaskCacheDao();
                                   /* if (BuildConfig.DEBUG) {
                                        List<UploadTaskCache> caches1 = dao.loadAll();
                                        for (UploadTaskCache c : caches1) {
                                            KLog.d(TAG, "onComplete: db save before： " + c.toString());
                                        }
                                    }*/
                                    dao.update(cache);
                                   /* if (BuildConfig.DEBUG) {
                                        List<UploadTaskCache> caches2 = dao.loadAll();
                                        for (UploadTaskCache c : caches2) {
                                            KLog.d(TAG, "onComplete:  db save after： " + c.toString());
                                        }
                                    }*/
                                    // --------------------------  添加水印成功，设置成功状态，并把添加水印成功的目录设置到上传目录 end  -------------------------

                                    // 开始添加头部信息
//                                    addHeaderInfo(cache, uploadUtils);
                                }

                                @Override
                                public void onError() {
                                    KLog.d(TAG, "onError ");
                                    /*if (cache.getState() != UploadManager.Status.ERROR) {
                                        setCacheStates(cache, UploadManager.Status.ERROR);
                                        sendHandlerMessage(uploadUtils, UploadManager.uploadStatus.ERROR, new RequestError(RespCode.CODE_614, IvyApp.getInstance().getResources().getString(R.string.upload_this_media_no_support)));
                                    }*/
                                }
                            });

                    // ---------------------  存储outFilePath ，防止添加视频的时候，删除了输出的文件 ---------------------
                    cache.setOutFilePath(mMp4Processor.getOutputPath());

                    KLog.d(TAG, "start:  setOutFilePath update after： " + cache.toString());
                    // 存储到本地去
                    UploadTaskCacheDao dao = GreenDaoManager.getInstance().getUploadTaskCacheDao();
                    if (BuildConfig.DEBUG) {
                        List<UploadTaskCache> caches1 = dao.loadAll();
                        for (UploadTaskCache c : caches1) {
                            KLog.d(TAG, "start:  setOutFilePath  db save before： " + c.toString());
                        }
                    }
                    dao.update(cache);
                    if (BuildConfig.DEBUG) {
                        List<UploadTaskCache> caches2 = dao.loadAll();
                        for (UploadTaskCache c : caches2) {
                            KLog.d(TAG, "start:  setOutFilePath   db save after： " + c.toString());
                        }
                    }
                    // -------------------------------------------------------------------------------------------------
                } catch (Exception e) {
                    e.printStackTrace();

//                    setCacheStates(cache, UploadManager.Status.ERROR);
//                    sendHandlerMessage(uploadUtils, UploadManager.uploadStatus.ERROR, new RequestError(RespCode.CODE_611, IvyApp.getInstance().getResources().getString(R.string.media_conversion_failed)));
                } catch (Error error) {
                    error.printStackTrace();

//                    setCacheStates(cache, UploadManager.Status.ERROR);
//                    sendHandlerMessage(uploadUtils, UploadManager.uploadStatus.ERROR, new RequestError(RespCode.CODE_611, IvyApp.getInstance().getResources().getString(R.string.media_conversion_failed)));
                }
            } else {
//                setCacheStates(cache, UploadManager.Status.ERROR);
//                sendHandlerMessage(uploadUtils, UploadManager.uploadStatus.ERROR, new RequestError(RespCode.CODE_611, IvyApp.getInstance().getResources().getString(R.string.media_conversion_failed)));
            }
        }
    }


    // 添加头部信息
    private void addHeaderInfo(UploadTaskCache cache) {
        if (cancleAddHeaderInfo) {
            KLog.d(TAG, "addHeaderInfo: cancleAddHeaderInfo == true 已经退出了，不再执行 " );
            return;
        }
        File f = new File(cache.getFilePath());
        if (f==null||!f.exists() || f.length() == 0) {
            KLog.d(TAG, "addHeaderInfo: inputFile == null " );
//            setCacheStates(cache, UploadManager.Status.ERROR);
//            sendHandlerMessage(uploadUtils, UploadManager.uploadStatus.ERROR, new RequestError(RespCode.CODE_614,
//                    IvyApp.getInstance().getResources().getString(R.string.upload_this_media_no_support)));
            return;
        }
        // 检测视频添加水印有无失败，是否ok，如果是错误的数据就不再上传，弹出错误提示
        if (!IvyUtils.isOKVideo(cache.getFilePath())) {
            KLog.d(TAG, "addHeaderInfo: isOKVideo is null " );
//            setCacheStates(cache, UploadManager.Status.ERROR);
//            sendHandlerMessage(uploadUtils, UploadManager.uploadStatus.ERROR, new RequestError(RespCode.CODE_614,
//                    IvyApp.getInstance().getResources().getString(R.string.upload_this_media_no_support)));
            return;
        }


//        if (cache != null && cache.getState() == UploadManager.Status.START) {
//            return;
//        }

//        setCacheStates(cache, UploadManager.Status.START);
//        cache.setOffset(0);
//        cache.setTotelLength(0);
//        sendHandlerMessage(uploadUtils, UploadManager.uploadStatus.PROGRESS, path); // 此处不是完成，只是进度达到了100%

        synchronized (addHeadLock) {
            FFmpegUtils fmpegUtils = new FFmpegUtils(FileUtils.createTempDir(MediaConfig.VIDEO_XH_RECORDER_SYNTH));
            fmpegUtils.setOnFFmpegOutputListener(outputFilePath -> {
                if (!cancleAddHeaderInfo) { // 未取消

                    File f1 = new File(outputFilePath);
                    if (f1==null||!f1.exists() || f1.length() == 0) {
//                        setCacheStates(cache, UploadManager.Status.ERROR);
//                        sendHandlerMessage(uploadUtils, UploadManager.uploadStatus.ERROR, new RequestError(RespCode.CODE_614,
//                                IvyApp.getInstance().getResources().getString(R.string.upload_this_media_no_support)));
                        return;
                    }
                    KLog.d(TAG, "addHeaderInfo: update before： " + cache.toString());

                    // --------------------------  添加头部信息成功，设置成功状态，并把添加头部信息成功的目录设置到上传目录 start  -------------------------
                    // 设置装换成功状态
                    cache.setHasAddHeaderInfo(true);

                    // 修改上传文件是装换之后的文件, outPath 变成是原来的文件
                    String tempPath = cache.getFilePath();
                    cache.setFilePath(outputFilePath);
                    cache.setOutFilePath(tempPath);

                    /*if (uploadUtils.getBuilder() != null) {
                        uploadUtils.getBuilder().setFilePath(outputFilePath);
                    }*/

                    KLog.d(TAG, "addHeaderInfo: update after： " + cache.toString());
                    // 存储到本地去
                    UploadTaskCacheDao dao = GreenDaoManager.getInstance().getUploadTaskCacheDao();
                   /* if (BuildConfig.DEBUG) {
                        List<UploadTaskCache> caches1 = dao.loadAll();
                        for (UploadTaskCache c : caches1) {
                            KLog.d(TAG, "addHeaderInfo: db save before： " + c.toString());
                        }
                    }*/
                    dao.update(cache);
                   /* if (BuildConfig.DEBUG) {
                        List<UploadTaskCache> caches2 = dao.loadAll();
                        for (UploadTaskCache c : caches2) {
                            KLog.d(TAG, "addHeaderInfo:  db save after： " + c.toString());
                        }
                    }*/
                    // --------------------------  添加头部信息成功，设置成功状态，并把添加头部信息成功的目录设置到上传目录 end  -------------------------

                    // 开始上传
//                    uploadUtils.start();
                }
            }).addHeaderInfo(cache.getFilePath());
        }
    }


    public void cancel(UploadTaskCache cache) {
        if (cache == null) {
//            uploadUtils.cancel();
            return;
        }

        if (cache.isHasAddHeaderInfo()) {
//            uploadUtils.cancel();
            return;
        }

        if (cache.isHasWaterMark()) {
            synchronized (addHeadLock) {
                cancleAddHeaderInfo = true;
                // 为了防止取消 添加头部失败，已经执行了，就执行这个取消指令
//                uploadUtils.cancel();

//                setCacheStates(cache, UploadManager.Status.CANCEL);
//                sendHandlerMessage(uploadUtils, UploadManager.uploadStatus.ERROR, new RequestError(RespCode.CODE_612, IvyApp.getInstance().getResources().getString(R.string.conversion_cancel)));
            }
            return;
        }
        KLog.d(TAG, "cancel: video trans cancel");
        synchronized (addWaterLock) {
            if (mMp4Processor != null) {
                cancleWaterMark = true;
                try {
                    VideoUtils.stop(mMp4Processor);

//                    setCacheStates(cache, UploadManager.Status.CANCEL);
//                    sendHandlerMessage(uploadUtils, UploadManager.uploadStatus.ERROR, new RequestError(RespCode.CODE_612, IvyApp.getInstance().getResources().getString(R.string.conversion_cancel)));
                } catch (Exception e) {
                    e.printStackTrace();

//                    setCacheStates(cache, UploadManager.Status.ERROR);
//                    sendHandlerMessage(uploadUtils, UploadManager.uploadStatus.ERROR, new RequestError(RespCode.CODE_613, IvyApp.getInstance().getResources().getString(R.string.cancel_failed)));
                } catch (Error error) {
                    error.printStackTrace();

//                    setCacheStates(cache, UploadManager.Status.ERROR);
//                    sendHandlerMessage(uploadUtils, UploadManager.uploadStatus.ERROR, new RequestError(RespCode.CODE_613, IvyApp.getInstance().getResources().getString(R.string.cancel_failed)));
                }
            }
        }
    }

    private void sendHandlerMessage(int what, Object obj) {
//        if (uploadUtils.getHandler() != null) {
//            Handler h = uploadUtils.getHandler();
//            Message msg = h.obtainMessage(what);
//            msg.obj = obj;
//            h.sendMessage(msg);
//        }
    }

    private void setCacheStates(UploadTaskCache cache) {
//        if (cache != null) cache.setState(state);
    }
}
