package com.xianghe.ivy.ui.module.player.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;

import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.utils.CommonUtil;
import com.shuyu.gsyvideoplayer.utils.Debuger;
import com.xianghe.ivy.R;
import com.xianghe.ivy.utils.DLLog;
import com.xianghe.ivy.utils.IjkMediaPlayerUtil;
import com.xianghe.ivy.utils.KLog;
import com.xianghe.ivy.utils.NetworkUtil;
import com.xianghe.ivy.utils.TimeFormatUtils;
import com.xianghe.ivy.utils.ToastUtil;
import com.xianghe.ivy.utils.ViewUtil;

import static com.xianghe.ivy.utils.TimeFormatUtils.getCurrentTime;


/**
 * 针对需求对 UE 做修改
 */
public class IvyVideoView extends IvyVideoWidgetView {
    private static final String TAG = "IvyVideoView";

    private RectF mCenterRect = new RectF();
    private RectF mRightRect = new RectF();

    public boolean hasLoadTure = true;

    public boolean isFirstLoad = true;

    public boolean isFirstLoadTwo = true;

    //缓冲完成
    public static final int CURRENT_STATE_PLAYING_BUFFERING_END = 8;

    public IvyVideoView(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public IvyVideoView(Context context) {
        super(context);
    }

    public IvyVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
    }


    @Override
    public void onError(int what, int extra) {
        if (what == -192) {
            ToastUtil.showToast(getContext(), getResources().getString(R.string.common_network_error));
            //判断状态
            if (mCurrentState == CURRENT_STATE_PREPAREING) {
                release();
            } else {
                if (getGSYVideoManager() != null) {
                    setStateAndUi(CURRENT_STATE_PAUSE);
                    mCurrentPosition = getGSYVideoManager().getCurrentPosition();
                    getGSYVideoManager().pause();
                }
            }
            changeUiToPauseShow();
        } else {
            super.onError(what, extra);
        }
    }


    @Override
    protected void startDismissControlViewTimer() {
    }

    @Override
    protected void cancelDismissControlViewTimer() {
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        ((GSYVideoManager) getGSYVideoManager()).setTimeOut(30 * 1000, true);
    }

    @Override
    protected boolean isShowNetConfirm() {
        return false;
    }

    public int getmCurrentState(){
        return mCurrentState;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int horizontalSize = w / 5;
        int verticalSize = h / 5;
        mCenterRect.set(horizontalSize, verticalSize,
                w - horizontalSize, h - verticalSize);

        mRightRect.set(w - w / 10, 0, w, h);

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        float x = ev.getX();
        float y = ev.getY();

        if (mRightRect.contains(x, y)) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    protected void touchSurfaceMove(float deltaX, float deltaY, float x, float y) {
        KLog.e(TAG, "");
        if (mCenterRect.contains(mDownX, mDownY)) {
            if (Math.abs(deltaX) > Math.abs(deltaY)) {
                // 左右滑动
                if (deltaX < 0) {
                    KLog.d(TAG, "←");
                    // 从 右向左 滑
                    if (mGestureListener != null) {
                        mGestureListener.onRight2LeftFling(this, deltaX, deltaY, x - mDownX, y - mDownY);
                    }
                } else {
                    // 从 左向右 滑
                    KLog.d(TAG, "→");
                    if (mGestureListener != null) {
                        mGestureListener.onLeft2RightFling(this, deltaX, deltaY, x - mDownX, y - mDownY);
                    }
                }
            }
        } else if (mRightRect.contains(mDownX, mDownY)) {
            int curHeight = CommonUtil.getCurrentScreenLand((Activity) getActivityContext()) ? mScreenWidth : mScreenHeight;

            deltaY = -deltaY;
            int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int deltaV = (int) (max * deltaY * 3 / curHeight);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mGestureDownVolume + deltaV, 0);
            int volumePercent = (int) (mGestureDownVolume * 100 / max + deltaY * 3 * 100 / curHeight);

            showVolumeDialog(-deltaY, volumePercent);
        } else {
            super.touchSurfaceMove(deltaX, deltaY, x, y);
        }
    }

    /**
     * 点击触摸显示和隐藏逻辑
     */
    @Override
    public void onClickUiToggle() {
  //  protected void onClickUiToggle() {
        if (mCurrentState == CURRENT_STATE_PLAYING) {
            clickStartIcon();

        } else if (mCurrentState == CURRENT_STATE_PAUSE) {
            clickStartIcon();

        } else if (mCurrentState == CURRENT_STATE_PREPAREING) {
            release();
            changeUiToPauseShow();

        } else {

            super.onClickUiToggle();
        }
    }

    protected void clickStartIcon() {
        if (mCurrentState == CURRENT_STATE_PAUSE) {
            if (!NetworkUtil.isNetworkConnected(getContext())) {
                ToastUtil.showToast(getContext(), getContext().getResources().getString(R.string.common_network_error));
                return;
            }
        }
        super.clickStartIcon();
    }

    @Override
    protected void changeUiToPlayingShow() {
        KLog.d(TAG, "changeUiToPlayingShow");
        super.changeUiToPlayingShow();
        if (!hasLoadTure && !isFirstLoadTwo) {
            setViewShowState(mLoadingProgressBar, VISIBLE);
        }

        if (!isFirstLoad && isFirstLoadTwo) {
            isFirstLoadTwo = false;
        }
        isFirstLoad = false;
    }

    /**
     * 双击暂停/播放
     * 如果不需要，重载为空方法即可
     */
    @Override
    protected void touchDoubleUp(MotionEvent e) {
        final ImageView imageView = new ImageView(getContext());

        int size = ViewUtil.dp2px(getContext(), 50);
        imageView.setImageResource(R.mipmap.icon_like);
        imageView.setX(e.getX() - size / 2);
        imageView.setY(e.getY() - size / 2);
        imageView.setRotation((float) (-20 + Math.random() * 40));

        LayoutParams layoutParams = new LayoutParams(size, size);
        addView(imageView, layoutParams);

        PropertyValuesHolder[] showProperty = {
                PropertyValuesHolder.ofFloat(View.SCALE_X, 0.5f, 1.2f, 1f),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.5f, 1.2f, 1f),
        };
        ObjectAnimator showAnimator = ObjectAnimator.ofPropertyValuesHolder(imageView, showProperty);

        PropertyValuesHolder[] hideProperty = {
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.5f),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.5f),
                PropertyValuesHolder.ofFloat(View.ALPHA, 1f, 0f),
                PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, imageView.getY(), imageView.getY() - size * 2)
        };
        final ObjectAnimator hideAnimator = ObjectAnimator.ofPropertyValuesHolder(imageView, hideProperty);
        hideAnimator.setStartDelay(500);
        hideAnimator.start();

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setTarget(imageView);
        animatorSet.playSequentially(showAnimator, hideAnimator);
        animatorSet.start();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ViewParent viewParent = imageView.getParent();
                if (viewParent instanceof ViewGroup) {
                    ((ViewGroup) viewParent).removeView(imageView);
                }
            }
        });

        if (mGestureListener != null) {
            mGestureListener.onDoubleTap(this, e);
        }
    }

    private GestureListener mGestureListener;

    public GestureListener getGestureListener() {
        return mGestureListener;
    }

    public void setGestureListener(GestureListener gestureListener) {
        mGestureListener = gestureListener;
    }

    public interface GestureListener {
        public void onRight2LeftFling(View ivyVideoView, float deltaX, float deltaY, float totalMoveX, float totalMoveY);

        public void onLeft2RightFling(View ivyVideoView, float deltaX, float deltaY, float totalMoveX, float totalMoveY);

        public void onDoubleTap(View ivyVideoView, MotionEvent event);
    }

    @Override
    public void onInfo(int what, int extra) {
        //DLLog.d("IvyVideoView", "onInfo: " + IjkMediaPlayerUtil.infoToString(what) + "  " + TimeFormatUtils.getCurrentTime());
        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
            //DLLog.d("IvyVideoView", "xw暂停播放开始缓冲更多数据" + TimeFormatUtils.getCurrentTime());
            hasLoadTure = false;
            mBackUpPlayingBufferState = mCurrentState;
            //避免在onPrepared之前就进入了buffering，导致一只loading
            if (mHadPlay && mCurrentState != CURRENT_STATE_PREPAREING && mCurrentState > 0)
                setStateAndUi(CURRENT_STATE_PLAYING_BUFFERING_START);

        } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
            //DLLog.d("IvyVideoView", "xw缓冲了足够的数据重新开始播放" + getCurrentTime());
            hasLoadTure = true;
            if (mBackUpPlayingBufferState != -1) {
                if (mBackUpPlayingBufferState == CURRENT_STATE_PLAYING_BUFFERING_START) {
                    mBackUpPlayingBufferState = CURRENT_STATE_PLAYING_BUFFERING_START;
                }
                if (mHadPlay && mCurrentState != CURRENT_STATE_PREPAREING && mCurrentState > 0)
                    setStateAndUi(CURRENT_STATE_PLAYING_BUFFERING_END);

                mBackUpPlayingBufferState = -1;
            }
        } else if (what == getGSYVideoManager().getRotateInfoFlag()) {
            mRotate = extra;
            Debuger.printfLog("Video Rotate Info " + extra);
            if (mTextureView != null)
                mTextureView.setRotation(mRotate);
        } else if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
            //DLLog.d("IvyVideoView", "xw开始渲染第一帧" + getCurrentTime());
        }
    }

    @Override
    public void onPrepared() {
        super.onPrepared();
        DLLog.d("IvyVideoView", "onPrepared : " + TimeFormatUtils.getCurrentTime());
    }

    /**
     * 处理控制显示
     *
     * @param state
     */
    protected void resolveUIState(int state) {
        switch (state) {
            case CURRENT_STATE_NORMAL:
                changeUiToNormal();
                cancelDismissControlViewTimer();
                break;
            case CURRENT_STATE_PREPAREING:
                changeUiToPreparingShow();
                startDismissControlViewTimer();
                break;
            case CURRENT_STATE_PLAYING:
                changeUiToPlayingShow();
                startDismissControlViewTimer();
                break;
            case CURRENT_STATE_PAUSE:
                changeUiToPauseShow();
                cancelDismissControlViewTimer();
                break;
            case CURRENT_STATE_ERROR:
                changeUiToError();
                break;
            case CURRENT_STATE_AUTO_COMPLETE:
                changeUiToCompleteShow();
                cancelDismissControlViewTimer();
                break;
            case CURRENT_STATE_PLAYING_BUFFERING_START:
                changeUiToPlayingBufferingShow();
                break;
            case CURRENT_STATE_PLAYING_BUFFERING_END:
                if (mLoadingProgressBar.getVisibility() == VISIBLE) {
                    setViewShowState(mLoadingProgressBar, INVISIBLE);
                }
                mCurrentState = CURRENT_STATE_PLAYING;
                break;
        }
    }
}
