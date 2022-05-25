package com.xianghe.ivy.ui.module.videocall.service;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.ScreenUtils;
import com.xianghe.ivy.R;
import com.xianghe.ivy.utils.FileUtils;
import com.xianghe.ivy.utils.IvyUtils;
import com.xianghe.ivy.utils.KLog;
import com.xianghe.ivy.utils.ScreenRecordUtil;

import java.io.File;
import java.io.IOException;


public class ScreenRecordService extends Service implements Handler.Callback {

    private static final String TAG = ScreenRecordService.class.getSimpleName();
    private MediaProjectionManager mProjectionManager;
    private MediaProjection mMediaProjection;
    private MediaRecorder mMediaRecorder;
    private VirtualDisplay mVirtualDisplay;

    private boolean mIsRunning;
    private int mRecordWidth = ScreenUtils.getScreenWidth();
    private int mRecordHeight = ScreenUtils.getScreenHeight();
    private int mScreenDpi = ScreenUtils.getScreenDensityDpi();


    private int mResultCode;
    private Intent mResultData;

    //录屏文件的保存地址
    private String mRecordFilePath;

    private Handler mHandler;
    //已经录制多少秒了
    private int mRecordSeconds = 0;

    private static final int MSG_TYPE_COUNT_DOWN = 110;

    public static final int STATUS_NORMAL = 0;
    public static final int STATUS_ERROR = 1;
    public static final int STATUS_TIME_FULL = 2;

    @Override
    public IBinder onBind(Intent intent) {
        return new RecordBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();


        mIsRunning = false;
//        mMediaRecorder = new MediaRecorder();
        mHandler = new Handler(Looper.getMainLooper(), this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public boolean isReady() {
        return mMediaProjection != null && mResultData != null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void clearRecordElement() {
        clearAll();
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
        mResultData = null;
        mIsRunning = false;
    }

    public boolean ismIsRunning() {
        return mIsRunning;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setResultData(int resultCode, Intent resultData) {
        mResultCode = resultCode;
        mResultData = resultData;

        mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        if (mProjectionManager != null) {
            mMediaProjection = mProjectionManager.getMediaProjection(mResultCode, mResultData);
        }
    }

    /**
     *
     * @param recordTotalTime 已录制的视频总时长
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean startRecord(double recordTotalTime) {
        if (mIsRunning) {
            return false;
        }
//        ScreenRecordUtil.clearRecordElement();
        if (mMediaProjection == null) {
            mMediaProjection = mProjectionManager.getMediaProjection(mResultCode, mResultData);

        }

        try {
            setUpMediaRecorder(recordTotalTime);
            createVirtualDisplay();
            mMediaRecorder.start();

            ScreenRecordUtil.startRecord(mRecordFilePath);
            //最多录制三分钟
            mHandler.sendEmptyMessageDelayed(MSG_TYPE_COUNT_DOWN, 1000);

            mIsRunning = true;
        } catch (Exception e) {
            e.printStackTrace();
            mIsRunning = true;
            KLog.e("---屏幕录制异常，结束录屏");
            stopRecord("屏幕录制异常",STATUS_ERROR,false);
        }

//        Log.w("lala","startRecord ");
        return true;
    }

    /**
     *
     * @param tip
     * @param status 默认0-正常状态，其他值-异常状态
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean stopRecord(String tip,int status,boolean isSaveRecordFile) {
        KLog.e(TAG, "stopRecord: first  isSaveRecordFile="+isSaveRecordFile);

        if (!mIsRunning) {
            return false;
        }
        mIsRunning = false;
        KLog.e(TAG, "stopRecord  middle");

        try {
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            mMediaRecorder = null;
            mVirtualDisplay.release();
            mMediaProjection.stop();

            KLog.e(TAG, "stopRecord ");

        } catch (Exception e) {
            e.printStackTrace();
            mMediaRecorder.release();
            mMediaRecorder = null;
            KLog.e(TAG, "stopRecord  exception");

        } finally {
            mMediaProjection = null;
            mHandler.removeMessages(MSG_TYPE_COUNT_DOWN);
            ScreenRecordUtil.stopRecord(tip,status,isSaveRecordFile);
            KLog.e(TAG, "stopRecord  fileScanVideo");
            //系统图库更新(该视频文件没有把声音录制进去，不刷新系统图库）
//            FileUtil.fileScanVideo(this, mRecordFilePath, mRecordWidth, mRecordHeight, mRecordSeconds);
//        mRecordFilePath = null;
            mRecordSeconds = 0;
            return true;
        }
    }


    public void pauseRecord() {
        if (mMediaRecorder != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mMediaRecorder.pause();
            }
        }

    }

    public void resumeRecord() {
        if (mMediaRecorder != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mMediaRecorder.resume();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void createVirtualDisplay() {
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("MainScreen", mRecordWidth, mRecordHeight, mScreenDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC, mMediaRecorder.getSurface(), null, null);
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private void setUpMediaRecorder(double recordTotalTime) throws IOException {

        mRecordFilePath = IvyUtils.createVideoCallRecordDirs() + File.separator + System.currentTimeMillis() + ".mp4";
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
        mMediaRecorder = new MediaRecorder();
        //设置视频来源
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        //设置音频来源
//        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //输出的录屏文件格式
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        //视频编码器
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        //音频编码器
//        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
        //视频尺寸
        mMediaRecorder.setVideoSize(mRecordWidth, mRecordHeight);
        //设置录制的音频通道数
//        mMediaRecorder.setAudioChannels(1);
        //设置所录制的声音的采样率
//        mMediaRecorder.setAudioSamplingRate(8000);
        //比特率
        KLog.e("---VideoEncodingBitRate="+(mRecordWidth * mRecordHeight * 5));
        mMediaRecorder.setVideoEncodingBitRate((int) (mRecordWidth * mRecordHeight * 5));
        //视频帧率
        mMediaRecorder.setVideoFrameRate(20);
        //当前可录制最长时间
        int maxDuration = (int) (180*1000-recordTotalTime*1000);
        mMediaRecorder.setMaxDuration(maxDuration);
        //录制过程监听
        mMediaRecorder.setOnInfoListener((mr, what, extra) -> {
            if (what==MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                stopRecord(getString(R.string.record_time_end_tip),STATUS_TIME_FULL,true);

            }
        });
        //录屏文件路径
        mMediaRecorder.setOutputFile(mRecordFilePath);

        mMediaRecorder.prepare();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void clearAll() {
        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }
    }

    public String getRecordFilePath() {
        return mRecordFilePath;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {

            case MSG_TYPE_COUNT_DOWN: {

                String str = null;
                boolean enough = FileUtils.getSDFreeMemory() / (1024 * 1024) < 4;
                if (enough) {
                    //空间不足，停止录屏
                    str = getString(R.string.record_space_tip);
                    if (mRecordSeconds <1){
                        //小于1S不保存
                        stopRecord(str,STATUS_ERROR,false);
                    } else {
                        stopRecord(str, STATUS_NORMAL, true);
                    }
                    mRecordSeconds = 0;
                    break;
                }

                mRecordSeconds++;
                int minute = 0, second = 0;
                if (mRecordSeconds >= 60) {
                    minute = mRecordSeconds / 60;
                    second = mRecordSeconds % 60;
                } else {
                    second = mRecordSeconds;
                }
                ScreenRecordUtil.onRecording("0" + minute + ":" + (second < 10 ? "0" + second : second + ""));

                if (mRecordSeconds < 3 * 60-1) {//最开始延迟了1s，所以要减1
                    mHandler.sendEmptyMessageDelayed(MSG_TYPE_COUNT_DOWN, 1000);
                } else if (mRecordSeconds == 3 * 60-1) {
                    str = getString(R.string.record_time_end_tip);
                    stopRecord(str,STATUS_TIME_FULL,true);
                    mRecordSeconds = 0;
                }

                break;
            }
        }
        return true;
    }

    public class RecordBinder extends Binder {
        public ScreenRecordService getRecordService() {
            return ScreenRecordService.this;
        }
    }


}
