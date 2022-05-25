package com.xianghe.ivy.ui.module.videoedit;

import android.os.Bundle;
import android.view.SurfaceView;

import androidx.annotation.FloatRange;

import com.xianghe.ivy.constant.Global;
import com.xianghe.ivy.ui.media.base.IMediaPlayer;
import com.xianghe.ivy.ui.media.play.audio.AudioPlayerUtils;
import com.xianghe.ivy.ui.media.play.video.VideoPlayerUtils;
import com.xianghe.ivy.ui.module.record.model.MovieEditModel;
import com.xianghe.ivy.ui.module.record.model.MovieItemModel;
import com.xianghe.ivy.utils.KLog;

import java.util.List;

/**
 * @Author: ycl
 * @Date: 2018/11/6 10:16
 * @Desc:
 */
public class VideoEditPlayUtils {

    // 网络音频播放
    private AudioPlayerUtils mAudioNetMusicPlayerUtils;
    // 本地音频播放
    private AudioPlayerUtils mAudioLocalMusicPlayerUtils;
    // 录音音频播放
    private AudioPlayerUtils mAudioRecordPlayerUtils;
    // 视频播放
    private VideoPlayerUtils mVideoPlayerUtils;

    // 缓存本地录制的音频路径
    private String mRecordPath = null;
    // 音乐下载ok后存储路径
    private String mMusicPath = null;
    // 是否关闭声音了，避免重复打开
    private boolean mIsCloseVolume = false;
    // 音频时间小于视频时间 , 根据此判断是否开启音乐结束就循环
    private boolean mIsMusicLessVideoTime = false;
    // 如果是本地视频页面跳转过来不需要显示视频剪辑
    private boolean mSizeAdapter = false;


    public VideoEditPlayUtils(SurfaceView holder, MovieEditModel editModel) {
        mAudioNetMusicPlayerUtils = new AudioPlayerUtils(0.7f);
        mAudioLocalMusicPlayerUtils = new AudioPlayerUtils(0.5f);
//        mAudioLocalMusicPlayerUtils.setLooping();// 当音频结束的时候，需要循环
        mAudioRecordPlayerUtils = new AudioPlayerUtils(1.0f);
        mVideoPlayerUtils = new VideoPlayerUtils(holder, editModel, 0.5f);
        initListener();
    }

    public VideoEditPlayUtils(SurfaceView holder, MovieEditModel editModel, IMediaPlayer.OnPlayNextListener onPlayNextListener) {
        mAudioNetMusicPlayerUtils = new AudioPlayerUtils(0.7f);
        mAudioLocalMusicPlayerUtils = new AudioPlayerUtils(0.5f);
//        mAudioLocalMusicPlayerUtils.setLooping();// 当音频结束的时候，需要循环
        mAudioRecordPlayerUtils = new AudioPlayerUtils(1.0f);
        mVideoPlayerUtils = new VideoPlayerUtils(holder, editModel, 0.5f,onPlayNextListener);
        initListener();
    }


    private void initListener() {
        // 对视频状态进行监听
        if (mVideoPlayerUtils != null)
            mVideoPlayerUtils.setStatusChangedListener(IMediaPlayer.Status.STATUS_COMPLETE, (playerUtils, status, msg) -> {
                switch (status) {
                    // 视频完成之后，循环，音乐也需要重新播放
                    case AudioPlayerUtils.Status.STATUS_COMPLETE:
                        if (msg != null && msg instanceof Boolean && ((boolean) msg)) {
                            KLog.d("Status.STATUS_COMPLETE  循环播放音乐，本地录音");

                            // 重新播放音乐
                            playLocalAudioPlayer();
                        }
                        break;
                }
            });
        if (mAudioLocalMusicPlayerUtils != null) {
            mAudioLocalMusicPlayerUtils.setStatusChangedListener(IMediaPlayer.Status.STATUS_STOP_START, (playerUtils, status, other) -> {
                switch (status) {
                    // 截取一段音乐，播放结束就需要停止并重新播放，目的是控制循环
                    case AudioPlayerUtils.Status.STATUS_STOP_START:
                        KLog.d("Status.STATUS_STOP_START 裁剪音乐了，循环播放本地音乐");
                        // 重新播放音乐
                        audioLocalMusicPlay(mMusicPath);
                        break;
                }
            }).setStatusChangedListener(IMediaPlayer.Status.STATUS_COMPLETE, (playerUtils, status, other) -> {
                switch (status) {
                    // 播放完成，循环播放
                    case AudioPlayerUtils.Status.STATUS_COMPLETE:
                        // 只有音频小于视频长度，才重新播放音乐，默认不开启循环
                        if (mIsMusicLessVideoTime) {
                            KLog.d("Status.STATUS_COMPLETE 音乐播放完了，循环播放本地音乐");
                            audioLocalMusicPlay(mMusicPath);
                        }
                        break;
                }
            });
        }
    }


    // ----------------------------- 音乐播放 start  -----------------------------------

    public void audioRecordPlay(String path) {
        if (mAudioRecordPlayerUtils != null) {
            mAudioRecordPlayerUtils.play(path);
        }
    }

    public void audioRecordStop() {
        if (mAudioRecordPlayerUtils != null) {
            mAudioRecordPlayerUtils.stop();
        }
    }

    public void audioNetMusicPlay(String path) {
        if (mAudioNetMusicPlayerUtils != null) {
            mAudioNetMusicPlayerUtils.play(path);
        }
    }

    public void audioNetMusicStop() {
        if (mAudioNetMusicPlayerUtils != null) {
            mAudioNetMusicPlayerUtils.stop();
        }
    }

    public void audioLocalMusicPlay(String path) {
        if (mAudioLocalMusicPlayerUtils != null) {
            mAudioLocalMusicPlayerUtils.play(path);
        }
    }

    public void audioLocalMusicStop() {
        if (mAudioLocalMusicPlayerUtils != null) {
            mAudioLocalMusicPlayerUtils.stop();
        }
    }


    // 重播音视频
    public void resetAllPlayer() {
        if (mVideoPlayerUtils != null) {
            mVideoPlayerUtils.reSetPlay();
        }
        playLocalAudioPlayer();
    }

    // 播放本地音乐，录音
    public void playLocalAudioPlayer() {
        if (mMusicPath != null && !mMusicPath.isEmpty()) {
            audioLocalMusicPlay(mMusicPath);
        }
        if (mRecordPath != null && !mRecordPath.isEmpty()) {
            audioRecordPlay(mRecordPath);
        }
    }

    public void closeAllVolume() {
        if (mIsCloseVolume) {
            return;
        }
        if (!mIsCloseVolume) {
            mIsCloseVolume = true;
        }
        if (mVideoPlayerUtils != null) {
            mVideoPlayerUtils.closeVolume();
        }
        if (mAudioLocalMusicPlayerUtils != null) {
            mAudioLocalMusicPlayerUtils.closeVolume();
        }
        if (mAudioRecordPlayerUtils != null) {
            mAudioRecordPlayerUtils.closeVolume();
        }
    }

    public void openAllVolume() {
        if (!mIsCloseVolume) {
            return;
        }
        if (mIsCloseVolume) {
            mIsCloseVolume = false;
        }
        if (mVideoPlayerUtils != null) {
            mVideoPlayerUtils.openVolume();
        }
        if (mAudioLocalMusicPlayerUtils != null) {
            mAudioLocalMusicPlayerUtils.openVolume();
        }
        if (mAudioRecordPlayerUtils != null) {
            mAudioRecordPlayerUtils.openVolume();
        }
    }
    // ----------------------------- 音乐播放 end  -----------------------------------


    public void resume() {
        //  -------  恢复音频播放  start  --------
        if (mAudioNetMusicPlayerUtils != null) {
            mAudioNetMusicPlayerUtils.resume();
        }
        if (mAudioLocalMusicPlayerUtils != null) {
            mAudioLocalMusicPlayerUtils.resume();
        }
        if (mAudioRecordPlayerUtils != null) {
            mAudioRecordPlayerUtils.resume();
        }
        if (mVideoPlayerUtils != null) {
            mVideoPlayerUtils.resume();
        }
        //  -------  回复音频播放  start  --------
    }

    public void pause() {
        //  -------  关闭音频播放  start  --------
        if (mAudioNetMusicPlayerUtils != null) {
            mAudioNetMusicPlayerUtils.pause();
        }
        if (mAudioLocalMusicPlayerUtils != null) {
            mAudioLocalMusicPlayerUtils.pause();
        }
        if (mAudioRecordPlayerUtils != null) {
            mAudioRecordPlayerUtils.pause();
        }
        if (mVideoPlayerUtils != null) {
            mVideoPlayerUtils.pause();
        }
        //  -------  关闭音频播放  end  --------
    }

    public void destroy() {
        //  -------  关闭音频播放  start  --------
        if (mAudioNetMusicPlayerUtils != null) {
            mAudioNetMusicPlayerUtils.destroy();
        }
        if (mAudioLocalMusicPlayerUtils != null) {
            mAudioLocalMusicPlayerUtils.destroy();
        }
        if (mAudioRecordPlayerUtils != null) {
            mAudioRecordPlayerUtils.destroy();
        }
        if (mVideoPlayerUtils != null) {
            mVideoPlayerUtils.destroy();
        }
        //  -------  关闭音频播放  end  --------
    }

    public void onRestoreInstanceState(Bundle bundle) {
        if (bundle != null) {
            if (bundle.containsKey(Global.PARAMS)) {
                mRecordPath = bundle.getString(Global.PARAMS);
            }
            if (bundle.containsKey(Global.PARAMS1)) {
                mMusicPath = bundle.getString(Global.PARAMS1);
            }
            if (bundle.containsKey(Global.PARAMS2)) {
                mIsCloseVolume = bundle.getBoolean(Global.PARAMS2, false);
            }
            if (bundle.containsKey(Global.PARAMS3)) {
                mIsMusicLessVideoTime = bundle.getBoolean(Global.PARAMS3, false);
            }
            if (bundle.containsKey(Global.PARAMS4)) {
                mSizeAdapter = bundle.getBoolean(Global.PARAMS4, false);
            }
        }
    }

    public void onSaveInstanceState(Bundle bundle) {
        if (bundle != null) {
            bundle.putString(Global.PARAMS, mRecordPath);
            bundle.putString(Global.PARAMS1, mMusicPath);
            bundle.putBoolean(Global.PARAMS2, mIsCloseVolume);
            bundle.putBoolean(Global.PARAMS3, mIsMusicLessVideoTime);
            bundle.putBoolean(Global.PARAMS4, mSizeAdapter);
        }
    }

    public void setMusicSeekPos(int startTime, int endTime, int videoDuration) {
        if (mAudioLocalMusicPlayerUtils != null) {
            // 设置开始，结束时间，并判断是否开启计时器
            mAudioLocalMusicPlayerUtils.setSeekPosition(startTime, endTime);
            int durtime = endTime - startTime;
            mAudioLocalMusicPlayerUtils.setOpenTimeTask(durtime > 0 && durtime < videoDuration);// 存在截取就开启计时器  ,注意此处如果是等于，就是没有裁剪，就不需要开启计时器
        }
    }

    public void resetMusicSeekPos() {
        if (mAudioLocalMusicPlayerUtils != null) mAudioLocalMusicPlayerUtils.resetSeekPosition();
    }


    public void setVideoVolume(@FloatRange(from = 0.0f, to = 1.0f) float fVal) {
        if (mVideoPlayerUtils != null) {
            mVideoPlayerUtils.setVolume(fVal);
        }
    }

    public void setMusicVolume(@FloatRange(from = 0.0f, to = 1.0f) float fVal) {
        if (mAudioLocalMusicPlayerUtils != null) {
            mAudioLocalMusicPlayerUtils.setVolume(fVal);
        }
    }

    public void setNetMusicVolume(@FloatRange(from = 0.0f, to = 1.0f) float fVal) {
        if (mAudioNetMusicPlayerUtils != null) {
            mAudioNetMusicPlayerUtils.setVolume(fVal);
        }
    }

    public void setRecordVolume(@FloatRange(from = 0.0f, to = 1.0f) float fVal) {
        if (mAudioRecordPlayerUtils != null) {
            mAudioRecordPlayerUtils.setVolume(fVal);
        }
    }

    public float getVideoVolume() {
        return mVideoPlayerUtils != null ? mVideoPlayerUtils.get_Volume() : 0f;
    }

    public float getMusicVolume() {
        return mAudioLocalMusicPlayerUtils != null ? mAudioLocalMusicPlayerUtils.get_Volume() : 0f;
    }

    public float getRecordVolume() {
        return mAudioRecordPlayerUtils != null ? mAudioRecordPlayerUtils.get_Volume() : 0f;
    }

    public double getVideoTotalTime() {
        return mVideoPlayerUtils != null ? mVideoPlayerUtils.getVideoTotalTime() : 0L;
    }


    public List<MovieItemModel> getListMovieItemModel() {
        return mVideoPlayerUtils != null ? mVideoPlayerUtils.getListMovieItemModel() : null;
    }

    public String getVideoFileName() {
        return mVideoPlayerUtils != null ? mVideoPlayerUtils.getVideoFileName() : null;
    }

    public MovieEditModel getMovieEditModel() {
        return mVideoPlayerUtils != null ? mVideoPlayerUtils.getMovieEditModel() : null;
    }

    public String getFirstFilPicPath() {
        return mVideoPlayerUtils != null ? mVideoPlayerUtils.getFirstFilPicPath() : null;
    }

    public String getRecordPath() {
        return mRecordPath;
    }

    public void setRecordPath(String recordPath) {
        mRecordPath = recordPath;
    }

    public String getMusicPath() {
        return mMusicPath;
    }

    public void setMusicPath(String musicPath) {
        mMusicPath = musicPath;
    }

    public boolean isUnEnablePlaying() {
        return mAudioLocalMusicPlayerUtils != null ? mAudioLocalMusicPlayerUtils.isUnEnablePlaying() : false;
    }

    public void setMusicLessVideoTime(boolean musicLessVideoTime) {
        mIsMusicLessVideoTime = musicLessVideoTime;
    }

    public void setVideoSizeAdapter(boolean sizeAdapter) {
        this.mSizeAdapter = sizeAdapter;
        if (mVideoPlayerUtils != null) mVideoPlayerUtils.setSizeAdapter(sizeAdapter);
    }

    public boolean isSizeAdapter() {
        return mSizeAdapter;
    }
}
