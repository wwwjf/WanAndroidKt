package com.tangyx.video.ffmpeg;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.xianghe.ivy.model.ImageBean;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 依赖备注：
 *      1>
 *      xw_ffmpeg: 是2.x版本的ffmoeg
 *      ffmpeginvoke: 是修改其头文件，main函数编译之后的so文件
 *      代码是xy根据tangyx的 https://www.jianshu.com/p/dfe9404e001a ，完成so的编译，如需再编译，请自行更换
 *      2>
 *      ffmpeg:是2.x版本的ffmpeg
 *      mediaeditor:是图片合成视频功能的代码编译生成的so
 *      此功能是cl完成，因为存在c代码内存泄漏问题，合成的视频多次切换特效导致OOM，cl又离职，导致后续暂无人维护其代码，修复其bug，导致此功能暂时隐藏
 *      功能入口：LocalMediaActivity的xml里面暂时设置gone
 *      图片合成视频及视频特效-源代码  ----->   共享盘 --->   Z:\临时\交接文档-Android-ycl
 *
 */

public class FFmpegRun {
    static {
        // --- ffmpeg 2.X 命令调用 start ---
        System.loadLibrary("xw_ffmpeg");
        System.loadLibrary("ffmpeginvoke");
        // --- ffmpeg 2.X 命令调用 end ---

        // --- 图片合成视频 start 暂时隐藏，未使用 ---
        System.loadLibrary("ffmpeg");
        System.loadLibrary("mediaeditor");
        // --- 图片合成视频 end 暂时隐藏，未使用 ---
    }

    private static final int MSG_WHAT_PROGRESS = 1;
    private static final int MSG_WHAT_END = 2;

    private static Handler sMainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_WHAT_PROGRESS:
                    if (sEncodeListener != null) {
                        sEncodeListener.onProgress(msg.arg1, msg.arg2);
                    }
                    break;
                case MSG_WHAT_END:
                    if (sEncodeListener != null) {
                        sEncodeListener.onEnd();
                    }
                    break;
            }
        }
    };

    @SuppressLint("CheckResult")
    public static Disposable executeRx(String[] commands, final FFmpegRunListener fFmpegRunListener) {
        //判断手机版本
        //保存图片
        //图片压缩
        if (fFmpegRunListener != null) {
            fFmpegRunListener.onStart();
        }
        return Observable.create((ObservableOnSubscribe<Integer>) e -> {
            int run = run(commands);
            e.onNext(run);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(integer -> {
                    if (fFmpegRunListener != null) {
                        fFmpegRunListener.onEnd(integer);
                    }
                });
    }

    public static Disposable execute(String[] commands, final FFmpegRunListener fFmpegRunListener) {
        return executeRx(commands, fFmpegRunListener);
    }

    /**
     * ffmpeg 命令调用
     * @param commands
     * @return
     */
    public native static int run(String[] commands);

    public interface FFmpegRunListener {
        void onStart();

        void onEnd(int result);

    }

    /**
     * 图片合成视频
     * @param imageBean
     */
    public native static void setImageToNativeForEncodeMP4(ImageBean imageBean);

    public static void nativeProgressCallback(int current, int max) {
        Message msg = sMainHandler.obtainMessage();
        msg.what = MSG_WHAT_PROGRESS;
        msg.arg1 = current;
        msg.arg2 = max;
        sMainHandler.sendMessage(msg);
    }

    public static void onEnd() {
        Message msg = sMainHandler.obtainMessage(MSG_WHAT_END);
        sMainHandler.sendMessage(msg);
    }

    public static EncodeListener sEncodeListener;

    public static interface EncodeListener {

        public void onProgress(long progress, long max);

        public void onEnd();
    }
}
