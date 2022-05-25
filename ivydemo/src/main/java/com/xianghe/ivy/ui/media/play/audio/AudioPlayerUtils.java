package com.xianghe.ivy.ui.media.play.audio;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.SparseArray;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;

import com.xianghe.ivy.ui.media.base.IMediaPlayer;
import com.xianghe.ivy.utils.KLog;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * @Author: ycl
 * @Date: 2018/10/30 16:24
 * @Desc:
 */
public class AudioPlayerUtils implements IMediaPlayer {
    private MediaPlayer mMediaPlayer;
    private float _Volume = 0.7f;
    private int _currentStatus = Status.STATUS_NULL;
    private boolean isPlaying = false;// 防止再停止之后，又准备完成，开始播放音乐

    private int startPosition = 0;
    // 控制不再播放音乐
    private boolean unEnablePlaying = false;


    //使用Map，而不使用固定变量，方便以后扩展Status
    private SparseArray<OnStatusChangedListener> listenerMap = new SparseArray<>();

    public AudioPlayerUtils(@FloatRange(from = 0.0f, to = 1.0f) float fVal) {
        this._Volume = fVal;
        init();
    }

    private void init() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
//                KLog.d("");
                setCurrentStatus(Status.STATUS_READY, mp.getDuration());
                // 因为准备是异步的，所以需要再此处判断，如果关闭了就不再启动
                if (isPlaying) {
                    mp.seekTo(startPosition);
                    mp.start();
                    startTimeTask();
                    setCurrentStatus(Status.STATUS_PLAYING, null);
                }
            }
        });

        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
//                KLog.d("");
                stop();
                setCurrentStatus(Status.STATUS_ERROR, null);
                return false;
            }
        });
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
//                KLog.d("");
                setCurrentStatus(Status.STATUS_COMPLETE, null);
            }
        });
        mMediaPlayer.setVolume(_Volume, _Volume);
    }


    // ------------------------------- 计时器相关 start -------------------------
    private int endPosition = 0;
    private boolean isOpenTimeTask = false; // 是否开启计时器，控制音频在endPosition停止
    private Disposable timeTask;

    // 第一次创建之后，后面不在创建，一直倒计时，在onresume,onpause 会重复创建，目的是为了稀屏之后，不在倒计时
    private void startTimeTask() {
        if (isOpenTimeTask && timeTask == null) {
            timeTask = Observable.interval(0, 1 * 10, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(aLong -> {
                        if (isOpenTimeTask && mMediaPlayer != null) {
                            int progress = mMediaPlayer.getCurrentPosition();
//                            KLog.d("accept" + aLong + "progress: " + progress);
                            if (progress >= endPosition) {
                                // 此监听，后续会不断重播音频
                                setCurrentStatus(Status.STATUS_STOP_START, null);
                            }
                        }
                    });
        }
    }

    private void stopTimeTask() {
        if (timeTask != null && !timeTask.isDisposed()) {
            timeTask.dispose();
            timeTask = null;
        }
    }

    // 存在截取的时间，就开启倒计时，没有就不要倒计时，并且不需要关闭，所以openTimeTask 判断很有意义
    public void setOpenTimeTask(boolean openTimeTask) {
//        KLog.i("openTimeTask: " + openTimeTask);
        isOpenTimeTask = openTimeTask;
    }

    public boolean isOpenTimeTask() {
        return isOpenTimeTask;
    }

    public boolean isUnEnablePlaying() {
        return unEnablePlaying;
    }
    // ------------------------------- 计时器相关 end -------------------------


    public int getCurrentPosition() {
        return mMediaPlayer != null ? mMediaPlayer.getCurrentPosition() : 0;
    }

    public void setSeekPosition(int startPosition, int endPosition) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        unEnablePlaying = (endPosition - startPosition == 0);
        if (unEnablePlaying) {
            // 等于0停止计时器，音乐
            stopTimeTask();
            stop();
        }
    }

    public void setLooping() {
        if (mMediaPlayer != null) {
            mMediaPlayer.setLooping(true);
        }
    }

    // 重置播放音乐的起点,并关闭计时器监听
    public void resetSeekPosition() {
        this.startPosition = 0;
        this.endPosition = 0; // 如果endpoint==0就不开启计时器
        this.isOpenTimeTask = false;
    }

    /**
     * 播放音频文件
     */
    @Override
    public void play(String path) {
        //等于0就拦截
        if (unEnablePlaying) {
            return;
        }
        if (path == null || path.equals("")) {
            return;
        }
        KLog.d("path: " + path);
        if (mMediaPlayer == null) return;
        try {
            stop();
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.prepareAsync();
            isPlaying = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 设置音量
     */
    public void setVolume(@FloatRange(from = 0.0f, to = 1.0f) float fVal) {
        _Volume = fVal;
        if (mMediaPlayer != null) {
            mMediaPlayer.setVolume(fVal, fVal);
        }
    }

    public float get_Volume() {
        return _Volume;
    }


    public void closeVolume() {
        if (mMediaPlayer != null) {
            mMediaPlayer.setVolume(0f, 0f);
        }
    }

    public void openVolume() {
        if (mMediaPlayer != null) {
            mMediaPlayer.setVolume(_Volume, _Volume);
        }
    }


    /**
     * 恢复播放
     */
    @Override
    public void resume() {
        KLog.d("");
        if (mMediaPlayer == null) return;
        if (_currentStatus == Status.STATUS_PAUSE) {
            setCurrentStatus(Status.STATUS_PLAYING, null);
            /* mMediaPlayer.seekTo(startPosition);*/
            mMediaPlayer.start();
            startTimeTask();
        }
        isPlaying = true;
    }

    /**
     * 暂停
     */
    @Override
    public void pause() {
        KLog.d("");
        if (mMediaPlayer == null) return;
        if (mMediaPlayer.isPlaying()) {
            setCurrentStatus(Status.STATUS_PAUSE, null);
            mMediaPlayer.pause();
            stopTimeTask();
        }
        isPlaying = false;
    }

    /**
     * stop
     */
    @Override
    public void stop() {
        KLog.d("");
        if (mMediaPlayer == null) return;
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            setCurrentStatus(Status.STATUS_STOP, null);
        }
        isPlaying = false;
    }

    @Override
    public void destroy() {
        if (mMediaPlayer == null) return;
        stopTimeTask();
        stop();

        mMediaPlayer.setOnCompletionListener(null);
        mMediaPlayer.setOnErrorListener(null);
        mMediaPlayer.setOnPreparedListener(null);

        mMediaPlayer.release();
        mMediaPlayer = null;

        if (listenerMap != null) {
            listenerMap.clear();
        }

        isPlaying = false;
    }

    /**
     * 设置当前状态，同时调用相应的回调（如果有）
     */
    private void setCurrentStatus(int nVal, Object obj) {
        _currentStatus = nVal;
//        if (listenerMap.indexOfKey(nVal) > 0) {
        OnStatusChangedListener listener = listenerMap.get(nVal);
        if (listener != null) {
            listener.onStatusChanged(this, nVal, obj);
        }
//        }
    }

    public AudioPlayerUtils setStatusChangedListener(@NonNull int nStatus, OnStatusChangedListener listener) {
        listenerMap.put(nStatus, listener);
        return this;
    }
}
