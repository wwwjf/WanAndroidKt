package com.xianghe.ivy.ui.media.play.video;

import android.media.MediaPlayer;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.blankj.utilcode.util.ScreenUtils;
import com.xianghe.ivy.model.SizeBean;
import com.xianghe.ivy.ui.media.base.IMediaPlayer;
import com.xianghe.ivy.ui.module.record.model.MovieEditModel;
import com.xianghe.ivy.ui.module.record.model.MovieItemModel;
import com.xianghe.ivy.utils.KLog;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: ycl
 * @Date: 2018/10/31 19:02
 * @Desc:
 */
public class VideoPlayerUtils implements IMediaPlayer, SurfaceHolder.Callback {
    private static final String TAG = "VideoPlayerUtils";

    private MediaPlayer mMediaPlayer;
    private MovieEditModel mMovieEditModel = null;
    private String mVoideoPath = null;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mHolder;

    // 缓存计算的宽高
    private Map<String, SizeBean> mCache = new ConcurrentHashMap<String, SizeBean>();
    private String lastKey = "";
    private int mSurfaceWidth;
    private int mSurfaceHeight;
    private boolean isSizeAdapter = false; // 是否开启适配宽高模式

    private float _Volume = 0.7f;
    private int _currentStatus = Status.STATUS_NULL;
    private boolean isPlaying = false;

    private int index = 0;

    private OnPlayNextListener mOnPlayNextListener;

    //使用Map，而不使用固定变量，方便以后扩展Status
    private SparseArray<OnStatusChangedListener> listenerMap = new SparseArray<>();


    public VideoPlayerUtils(SurfaceView surfaceView, MovieEditModel editModel, float fVal) {
        this.mMovieEditModel = editModel;
        this.mSurfaceView = surfaceView;
        this.mHolder = surfaceView.getHolder();
        this._Volume = fVal;
        initHolder();
    }

    public VideoPlayerUtils(SurfaceView surfaceView, MovieEditModel editModel, float fVal, OnPlayNextListener onPlayNextListener) {
        this.mMovieEditModel = editModel;
        this.mSurfaceView = surfaceView;
        this.mHolder = surfaceView.getHolder();
        this._Volume = fVal;
        mOnPlayNextListener = onPlayNextListener;
        initHolder();
    }

    public VideoPlayerUtils(SurfaceView surfaceView, String voideoPath, float fVal, OnPlayNextListener onPlayNextListener) {
        this.mSurfaceView = surfaceView;
        this.mHolder = surfaceView.getHolder();
        this.mVoideoPath = voideoPath;
        this._Volume = fVal;
        mOnPlayNextListener = onPlayNextListener;
        initHolder();
    }

    public VideoPlayerUtils(SurfaceView surfaceView, String voideoPath, float fVal) {
        this.mSurfaceView = surfaceView;
        this.mHolder = surfaceView.getHolder();
        this.mVoideoPath = voideoPath;
        this._Volume = fVal;
        initHolder();
    }


    public List<MovieItemModel> getListMovieItemModel() {
        if (mMovieEditModel != null) {
            return mMovieEditModel.getMovieItemModels();
        }
        return null;
    }

    public String getVideoFileName() {
        if (mMovieEditModel != null) {
            return mMovieEditModel.getVideoFileName();
        }
        return null;
    }


    public MovieEditModel getMovieEditModel() {
        return mMovieEditModel;
    }

    public String getFirstFilPicPath() {
        if (mMovieEditModel != null) {
            List<MovieItemModel> movieItemModels = mMovieEditModel.getMovieItemModels();
            if (movieItemModels != null && !movieItemModels.isEmpty()) {
                for (MovieItemModel model : movieItemModels) {
                    if (model.getFilPicPath() != null) {
                        return model.getFilPicPath();
                    }
                }
            }
        }
        return null;
    }

    public double getVideoTotalTime() {
        //遍历集合算总时间
        if (mMovieEditModel != null) {
            List<MovieItemModel> movieItemModels = mMovieEditModel.getMovieItemModels();
            if (movieItemModels != null && !movieItemModels.isEmpty()) {
                BigDecimal totalBigDecimal = new BigDecimal(BigInteger.ZERO);
                for (int i = 0; i < movieItemModels.size(); i++) {
                    totalBigDecimal = totalBigDecimal.add(movieItemModels.get(i).getVideoTime());
                }
                return isSizeAdapter ? totalBigDecimal.divide(BigDecimal.valueOf(1000), 3, BigDecimal.ROUND_HALF_UP).doubleValue() : totalBigDecimal.doubleValue();
            } else {
                return 0;
            }
        }
        return 0;
    }

    public void setSizeAdapter(boolean sizeAdapter) {
        isSizeAdapter = sizeAdapter;
    }

    private void initHolder() {
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mHolder.addCallback(this);
    }

    private void initMediaPlayer() {
        KLog.d(TAG, "initMediaPlayer");
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
//                KLog.d("");
                setCurrentStatus(Status.STATUS_READY, mp.getDuration());
                // 因为准备是异步的，所以需要再此处判断，如果关闭了就不再启动
                if (mMediaPlayer != null && isPlaying) {
//                    mMediaPlayer.seekTo(position);
                    mMediaPlayer.setDisplay(mHolder);
                    mMediaPlayer.start();
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
                if (mMovieEditModel != null) {
                    setCurrentStatus(Status.STATUS_COMPLETE,
                            index >= mMovieEditModel.getMovieItemModels().size());// 代表是否是到最后了，到最后了其他音乐也需要重置

                    play(nextVideoPath());
                } else if (mVoideoPath != null) {
                    play(mVoideoPath);
                }
            }
        });
        mMediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                if (isSizeAdapter) {
                    KLog.d(TAG, "width: " + width + " height: " + height + " mSurfaceWidth: " + mSurfaceWidth + " mSurfaceHeight: " + mSurfaceHeight);
                    String key = width + "|" + height;
                    if (!lastKey.equals(key)) { // 记录上一个key，防重复
                        lastKey = key;
                        SizeBean sizeBean = mCache.get(key);
                        if (sizeBean == null) {
                            float max = Math.max((float) width / (float) mSurfaceWidth, (float) height / (float) mSurfaceHeight);
                            width = (int) Math.ceil((float) width / max);
                            height = (int) Math.ceil((float) height / max);
                            mCache.put(key, new SizeBean(width, height));
                        }
                        if (mSurfaceView != null) {
                            ViewGroup.LayoutParams params = mSurfaceView.getLayoutParams();
                            if (params != null && params instanceof ConstraintLayout.LayoutParams) {
                                ConstraintLayout.LayoutParams p = (ConstraintLayout.LayoutParams) params;
                                p.width = mCache.get(key).getWidth();
                                p.height = mCache.get(key).getHeight();
                                mSurfaceView.setLayoutParams(p);
                            }
                        }
                    }
                }
            }
        });

        mMediaPlayer.setVolume(_Volume, _Volume);

    }

    // 遍历获取视频文件
    private String nextVideoPath() {
        if (mMovieEditModel != null) {
            List<MovieItemModel> movieItemModels = mMovieEditModel.getMovieItemModels();
            if (movieItemModels == null) {
                return null;
            }
            if (index >= movieItemModels.size()) {
                index = 0;
            }
            if (mOnPlayNextListener != null) {
                mOnPlayNextListener.onNextPlay(index);
            }
            if (movieItemModels.size()==0){
                return null;
            }
            return movieItemModels.get(index++).getFilePath();
        } else if (mVoideoPath != null) {
            return mVoideoPath;
        }
        return null;
    }


    // 从新开始播放
    public void reSetPlay() {
        KLog.d(TAG, "");
        index = 0;
        stop();
        play(nextVideoPath());
    }

    @Override
    public void play(String path) {
        if (path == null || path.equals("")) {
            return;
        }
        KLog.d(TAG, "path: " + path);
        if (mMediaPlayer == null) {
            KLog.w(TAG, "media player in null return");
            return;
        }
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.prepareAsync();
            isPlaying = true;
        } catch (IOException e) {
            KLog.e(TAG, "e: " + e);
            e.printStackTrace();
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

    @Override
    public void resume() {
        KLog.d(TAG, "");
        if (mMediaPlayer == null) return;
        if (_currentStatus == Status.STATUS_PAUSE) {
            setCurrentStatus(Status.STATUS_PLAYING, null);
            mMediaPlayer.start();
        }
        isPlaying = true;
    }

    @Override
    public void pause() {
        KLog.d(TAG, "");
        if (mMediaPlayer == null) return;
        if (mMediaPlayer.isPlaying()) {
            setCurrentStatus(Status.STATUS_PAUSE, null);
            mMediaPlayer.pause();
        }
        isPlaying = false;
    }

    @Override
    public void stop() {
        KLog.d(TAG, "");
        if (mMediaPlayer == null) return;
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            setCurrentStatus(Status.STATUS_STOP, null);
        }
        isPlaying = false;
    }

    @Override
    public void destroy() {
        KLog.d(TAG, "");
        if (mMediaPlayer != null) {
            stop();
            mMediaPlayer.setOnCompletionListener(null);
            mMediaPlayer.setOnErrorListener(null);
            mMediaPlayer.setOnPreparedListener(null);
            mMediaPlayer.setOnVideoSizeChangedListener(null);

            mMediaPlayer.release();

            KLog.i(TAG, " mMediaPlayer = null");
            mMediaPlayer = null;
        }
        if (mCache != null) {
            mCache.clear();
        }
        if (listenerMap != null) {
            listenerMap.clear();
        }
        if (mHolder != null) {
            mHolder.removeCallback(this);
            mHolder = null;
        }
        isPlaying = false;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        KLog.d(TAG, "");
//        stop();
        // destroy之后需要重建一次
        if (mMediaPlayer == null) {
            mSurfaceWidth = ScreenUtils.getScreenWidth();
            mSurfaceHeight = ScreenUtils.getScreenHeight();
            initMediaPlayer();
        }

        setCurrentStatus(Status.STATUS_COMPLETE, true); // 代表开始播放视频了
        play(nextVideoPath());
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        KLog.d(TAG, "");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        KLog.d(TAG, "");

        // 仅仅是释放播放器资源
        if (mMediaPlayer != null) {
//            mMediaPlayer.setOnCompletionListener(null);
//            mMediaPlayer.setOnErrorListener(null);
//            mMediaPlayer.setOnPreparedListener(null);
//            mMediaPlayer.setOnVideoSizeChangedListener(null);

            mMediaPlayer.stop();
            setCurrentStatus(Status.STATUS_STOP, null);
            mMediaPlayer.release();
            KLog.i(TAG, " mMediaPlayer = null");
            mMediaPlayer = null;
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

    public VideoPlayerUtils setStatusChangedListener(@NonNull int nStatus, OnStatusChangedListener listener) {
        listenerMap.put(nStatus, listener);
        return this;
    }

}
