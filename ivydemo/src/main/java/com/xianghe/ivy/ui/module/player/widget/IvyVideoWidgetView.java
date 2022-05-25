package com.xianghe.ivy.ui.module.player.widget;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewParent;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.shuyu.gsyvideoplayer.utils.CommonUtil;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.xianghe.ivy.R;
import com.xianghe.ivy.utils.Formatter;
import com.xianghe.ivy.utils.KLog;


/**
 * 只改变UI不跟变UE
 */
public class IvyVideoWidgetView extends StandardGSYVideoPlayer {
    private static final String TAG = "IvyVideoWidgetView";

    private View mIvCover;

    //private View mLayoutBottomPanel;
    private View mLayoutError;

    //private ImageView mIvState;
    private int mStartSeekProgress;

    public IvyVideoWidgetView(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public IvyVideoWidgetView(Context context) {
        super(context);
    }

    public IvyVideoWidgetView(Context context, AttributeSet attrs) {
        super(context, attrs);
        findView(context, attrs);
    }

    private void findView(Context context, AttributeSet attrs) {
        mIvCover = findViewById(R.id.iv_cover);

        //mLayoutBottomPanel = findViewById(R.id.layout_bottom_panel);
        //mIvState = findViewById(R.id.iv_state);

        mLayoutError = findViewById(R.id.layout_error);

        if (mThumbImageViewLayout != null &&
                (mCurrentState == -1 || mCurrentState == CURRENT_STATE_NORMAL || mCurrentState == CURRENT_STATE_ERROR)) {
            mThumbImageViewLayout.setVisibility(VISIBLE);
        }
    }

    /**
     * 整体自定义布局
     */
    @Override
    public int getLayoutId() {
        return R.layout.view_ivy_video_widget;
    }

    /**
     * 进度布局
     */
    @Override
    protected int getProgressDialogLayoutId() {
        return R.layout.dialog_video_progress;
    }

    @Override
    protected int getVolumeLayoutId() {
        return R.layout.dialog_ivy_video_view_volume;
    }

    @Override
    public void startPlayLogic() {
        super.startPlayLogic();
    }

    /**
     * 开始按键
     */
    protected void updateStartImage() {
        if (mStartButton instanceof ImageView) {
            ImageView imageView = (ImageView) mStartButton;
            if (mCurrentState == CURRENT_STATE_PLAYING) {
                imageView.setImageResource(0);  //不显示暂停按钮样式
                //mIvState.setImageResource(R.mipmap.icon_player_state_stop);
                mLayoutError.setVisibility(GONE);
            } else if (mCurrentState == CURRENT_STATE_ERROR) {
                //imageView.setImageResource(com.shuyu.gsyvideoplayer.R.drawable.video_click_error_selector);
                imageView.setImageResource(0);  //不显示暂停按钮样式
                mLayoutError.setVisibility(VISIBLE);
            } else {
                imageView.setImageResource(R.drawable.drawable_ripple_play);
                //mIvState.setImageResource(R.mipmap.icon_zanting);
                mLayoutError.setVisibility(GONE);
            }
        }
    }

    @Override
    protected void changeUiToPreparingShow() {
        KLog.d(TAG, "changeUiToPreparingShow");

        setViewShowState(mTopContainer, VISIBLE);
        setViewShowState(mStartButton, INVISIBLE);
        showLoading(true);
        setViewShowState(mIvCover, INVISIBLE);
        setViewShowState(mBottomProgressBar, INVISIBLE);
        setViewShowState(mLockScreen, GONE);
    }

    @Override
    protected void changeUiToPlayingShow() {
        KLog.d(TAG, "changeUiToPlayingShow");

        setViewShowState(mTopContainer, VISIBLE);
        setViewShowState(mStartButton, INVISIBLE);
        showLoading(false);
        setViewShowState(mIvCover, INVISIBLE);
        setViewShowState(mBottomProgressBar, INVISIBLE);
        setViewShowState(mLockScreen, (mIfCurrentIsFullscreen && mNeedLockFull) ? VISIBLE : GONE);

        updateStartImage();
    }

    @Override
    protected void changeUiToPauseShow() {
        KLog.d(TAG, "changeUiToPauseShow");

        setViewShowState(mTopContainer, VISIBLE);
        setViewShowState(mStartButton, VISIBLE);
        showLoading(false);
        setViewShowState(mIvCover, INVISIBLE);
        setViewShowState(mBottomProgressBar, INVISIBLE);
        setViewShowState(mLockScreen, (mIfCurrentIsFullscreen && mNeedLockFull) ? VISIBLE : GONE);

        updateStartImage();
        updatePauseCover();
    }

    @Override
    protected void changeUiToPlayingBufferingShow() {
        KLog.d(TAG, "changeUiToPlayingBufferingShow");

        setViewShowState(mTopContainer, VISIBLE);
        setViewShowState(mStartButton, INVISIBLE);
        showLoading(true);
        setViewShowState(mIvCover, INVISIBLE);
        setViewShowState(mBottomProgressBar, INVISIBLE);
        setViewShowState(mLockScreen, GONE);

    }

    @Override
    protected void changeUiToCompleteShow() {
        KLog.d(TAG, "changeUiToCompleteShow");

        setViewShowState(mTopContainer, VISIBLE);
        setViewShowState(mStartButton, VISIBLE);
        showLoading(false);
        setViewShowState(mIvCover, VISIBLE);
        setViewShowState(mBottomProgressBar, INVISIBLE);
        setViewShowState(mLockScreen, (mIfCurrentIsFullscreen && mNeedLockFull) ? VISIBLE : GONE);

        updateStartImage();
    }


    @Override
    protected void hideAllWidget() {
        KLog.d(TAG, "hideAllWidget");

        showLoading(false);
        setViewShowState(mTopContainer, INVISIBLE);
        setViewShowState(mBottomProgressBar, VISIBLE);
        setViewShowState(mStartButton, INVISIBLE);
    }

    @Override
    protected void changeUiToNormal() {
        KLog.d(TAG, "changeUiToNormal");

        setViewShowState(mTopContainer, VISIBLE);
        setViewShowState(mStartButton, INVISIBLE);
        showLoading(false);
        setViewShowState(mIvCover, VISIBLE);
        setViewShowState(mBottomProgressBar, INVISIBLE);
        setViewShowState(mLockScreen, (mIfCurrentIsFullscreen && mNeedLockFull) ? VISIBLE : GONE);

        updateStartImage();

    }

    @Override
    protected void changeUiToError() {
        KLog.d(TAG, "changeUiToError");

        setViewShowState(mTopContainer, INVISIBLE);
        setViewShowState(mStartButton, VISIBLE);
        showLoading(false);
        setViewShowState(mIvCover, INVISIBLE);
        setViewShowState(mBottomProgressBar, INVISIBLE);
        setViewShowState(mLockScreen, (mIfCurrentIsFullscreen && mNeedLockFull) ? VISIBLE : GONE);

        updateStartImage();
    }

    @Override
    protected void changeUiToPrepareingClear() {
        KLog.d(TAG, "changeUiToPrepareingClear");

        setViewShowState(mTopContainer, INVISIBLE);
        setViewShowState(mStartButton, INVISIBLE);
        showLoading(false);
        setViewShowState(mIvCover, INVISIBLE);
        setViewShowState(mBottomProgressBar, INVISIBLE);
        setViewShowState(mLockScreen, GONE);
    }

    @Override
    protected void changeUiToPlayingBufferingClear() {
        KLog.d(TAG, "changeUiToPlayingBufferingClear");

        setViewShowState(mTopContainer, INVISIBLE);
        setViewShowState(mStartButton, INVISIBLE);
        showLoading(true);
        setViewShowState(mIvCover, INVISIBLE);
        setViewShowState(mBottomProgressBar, VISIBLE);
        setViewShowState(mLockScreen, GONE);

        updateStartImage();
    }

    @Override
    protected void changeUiToClear() {
        KLog.d(TAG, "changeUiToClear");

        setViewShowState(mTopContainer, INVISIBLE);
        setViewShowState(mStartButton, INVISIBLE);
        showLoading(false);
        setViewShowState(mIvCover, INVISIBLE);
        setViewShowState(mBottomProgressBar, INVISIBLE);
        setViewShowState(mLockScreen, GONE);
    }

    @Override
    protected void changeUiToCompleteClear() {
        KLog.d(TAG, "changeUiToCompleteClear");

        setViewShowState(mTopContainer, INVISIBLE);
        setViewShowState(mStartButton, VISIBLE);
        showLoading(false);
        setViewShowState(mIvCover, VISIBLE);
        setViewShowState(mBottomProgressBar, VISIBLE);
        setViewShowState(mLockScreen, (mIfCurrentIsFullscreen && mNeedLockFull) ? VISIBLE : GONE);

        updateStartImage();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = ev.getX() - mDownX;
                float dy = ev.getY() - mDownY;
                if (Math.abs(dx) < Math.abs(dy) && Math.abs(dy) > 50) {
                    // 垂直滑动
                    getParent().requestDisallowInterceptTouchEvent(false);

                } else {
                    // 水平滑动
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        KLog.d(TAG, "onTouchEvent");
        return super.onTouchEvent(event);
    }

    private MotionEvent mDownEvnet;

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mDownEvnet = MotionEvent.obtain(event);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            mDownEvnet = null;
        }

        if (mDownEvnet != null && mDownEvnet.getPointerId(0) != event.getPointerId(0)) {
            return false;
        }

        int id = v.getId();
        float x = event.getX();
        float y = event.getY();

        if (mIfCurrentIsFullscreen && mLockCurScreen && mNeedLockFull) {
            onClickUiToggle();
            startDismissControlViewTimer();
            return true;
        }

        if (id == com.shuyu.gsyvideoplayer.R.id.fullscreen) {
            return false;
        }

        if (id == com.shuyu.gsyvideoplayer.R.id.surface_container) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN:
                    touchSurfaceDown(x, y);

                    break;
                case MotionEvent.ACTION_MOVE:
                    float deltaX = x - mDownX;
                    float deltaY = y - mDownY;
                    float absDeltaX = Math.abs(deltaX);
                    float absDeltaY = Math.abs(deltaY);

                    if ((mIfCurrentIsFullscreen && mIsTouchWigetFull)
                            || (mIsTouchWiget && !mIfCurrentIsFullscreen)) {
                        if (!mChangePosition && !mChangeVolume && !mBrightness) {
                            touchSurfaceMoveFullLogic(absDeltaX, absDeltaY);
                        }
                    }
                    touchSurfaceMove(deltaX, deltaY, x, y);

                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:

                    startDismissControlViewTimer();

                    touchSurfaceUp();

                    startProgressTimer();

                    //不要和隐藏虚拟按键后，滑出虚拟按键冲突
                    if (mHideKey && mShowVKey) {
                        return true;
                    }
                    break;
            }
            gestureDetector.onTouchEvent(event);
        } else if (id == com.shuyu.gsyvideoplayer.R.id.progress) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    cancelDismissControlViewTimer();
                case MotionEvent.ACTION_MOVE:
                    cancelProgressTimer();
                    ViewParent vpdown = getParent();
                    while (vpdown != null) {
                        vpdown.requestDisallowInterceptTouchEvent(true);
                        vpdown = vpdown.getParent();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    startDismissControlViewTimer();
                    startProgressTimer();
                    ViewParent vpup = getParent();
                    while (vpup != null) {
                        vpup.requestDisallowInterceptTouchEvent(false);
                        vpup = vpup.getParent();
                    }
                    mBrightnessData = -1f;
                    break;
            }
        }

        return false;
    }

    protected void touchSurfaceMove(float deltaX, float deltaY, float x, float y) {
        super.touchSurfaceMove(deltaX, deltaY, y);
    }

    /**
     * 双击{@link StandardGSYVideoPlayer#touchDoubleUp()} 方法 MotionEvent 数据信息丢失了, 着了处理一下.
     */
    protected GestureDetector gestureDetector = new GestureDetector(getContext().getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            touchDoubleUp(e);
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (!mChangePosition && !mChangeVolume && !mBrightness) {
                onClickUiToggle();
            }
            return super.onSingleTapConfirmed(e);
        }
    });

    protected void touchDoubleUp(MotionEvent e) {
        touchDoubleUp();
    }

    @Override
    protected void showProgressDialog(float deltaX, String seekTime, int seekTimePosition, String totalTime, int totalTimeDuration) {
        setViewShowState(mStartButton, INVISIBLE);
        KLog.i(TAG, "deltaX = " + deltaX);
        if (deltaX >= 0) {
            //mIvState.setImageResource(R.drawable.icon_player_state_seek_forward);
        } else {
            //mIvState.setImageResource(R.drawable.icon_player_state_seek_backward);
        }
        super.showProgressDialog(deltaX, seekTime, seekTimePosition, totalTime, totalTimeDuration);
    }

    @Override
    protected void dismissProgressDialog() {
        setViewShowState(mStartButton, VISIBLE);
        super.dismissProgressDialog();
    }


    private boolean isSeekProgress = false;

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isSeekProgress = true;

        mStartSeekProgress = seekBar.getProgress();
        setViewShowState(mStartButton, INVISIBLE);

        super.onStartTrackingTouch(seekBar);
        View localView = LayoutInflater.from(getActivityContext()).inflate(getProgressDialogLayoutId(), null);
        if (localView.findViewById(getProgressDialogProgressId()) instanceof ProgressBar) {
            mDialogProgressBar = ((ProgressBar) localView.findViewById(getProgressDialogProgressId()));
            if (mDialogProgressBarDrawable != null) {
                mDialogProgressBar.setProgressDrawable(mDialogProgressBarDrawable);
            }
        }
        if (localView.findViewById(getProgressDialogCurrentDurationTextId()) instanceof TextView) {
            mDialogSeekTime = ((TextView) localView.findViewById(getProgressDialogCurrentDurationTextId()));
        }
        if (localView.findViewById(getProgressDialogAllDurationTextId()) instanceof TextView) {
            mDialogTotalTime = ((TextView) localView.findViewById(getProgressDialogAllDurationTextId()));
        }
        if (localView.findViewById(getProgressDialogImageId()) instanceof ImageView) {
            mDialogIcon = ((ImageView) localView.findViewById(getProgressDialogImageId()));
        }
        mProgressDialog = new Dialog(getActivityContext(), com.shuyu.gsyvideoplayer.R.style.video_style_dialog_progress);
        mProgressDialog.setContentView(localView);
        mProgressDialog.getWindow().addFlags(Window.FEATURE_ACTION_BAR);
        mProgressDialog.getWindow().addFlags(32);
        mProgressDialog.getWindow().addFlags(16);
        mProgressDialog.getWindow().setLayout(getWidth(), getHeight());
        mProgressDialog.show();

        cancelProgressTimer();
    }


    /***
     * 拖动进度条
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        isSeekProgress = false;

        setViewShowState(mStartButton, VISIBLE);
        super.onStopTrackingTouch(seekBar);
        startProgressTimer();
        mProgressDialog.dismiss();
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        super.onProgressChanged(seekBar, progress, fromUser);
        if (fromUser) {
            int position = (int) (getDuration() * 1f * progress / seekBar.getMax());
            if (mDialogSeekTime != null) {
                mDialogSeekTime.setText(CommonUtil.stringForTime(position));
            }

            if (progress >= mStartSeekProgress) {
                //mIvState.setImageResource(R.drawable.icon_player_state_seek_forward);
            } else {
                //mIvState.setImageResource(R.drawable.icon_player_state_seek_backward);
            }

            mCurrentTimeTextView.setText(CommonUtil.stringForTime(position));
        }
    }

    @Override
    protected void setProgressAndTime(int progress, int secProgress, int currentTime, int totalTime) {

        if (mGSYVideoProgressListener != null && mCurrentState == CURRENT_STATE_PLAYING) {
            mGSYVideoProgressListener.onProgress(progress, secProgress, currentTime, totalTime);
        }

        if (mProgressBar == null || mTotalTimeTextView == null || mCurrentTimeTextView == null) {
            return;
        }

        if (!mTouchingProgressBar) {
            if (progress != 0) mProgressBar.setProgress(progress);
        }
        if (getGSYVideoManager().getBufferedPercentage() > 0) {
            secProgress = getGSYVideoManager().getBufferedPercentage();
        }
        if (secProgress > 94) secProgress = 100;
        setSecondaryProgress(secProgress);

        mTotalTimeTextView.setText(CommonUtil.stringForTime(totalTime));
        if (!isSeekProgress) {
            if (currentTime > 0)
                mCurrentTimeTextView.setText(CommonUtil.stringForTime(currentTime));
        }

        if (mBottomProgressBar != null) {
            if (progress != 0) mBottomProgressBar.setProgress(progress);
            setSecondaryProgress(secProgress);
        }
    }

    /******************* 下方两个重载方法，在播放开始前不屏蔽封面，不需要可屏蔽 ********************/
    @Override
    public void onSurfaceUpdated(Surface surface) {
        super.onSurfaceUpdated(surface);
        if (mIvCover != null && mIvCover.getVisibility() == VISIBLE) {
            mIvCover.setVisibility(INVISIBLE);
        }
    }

    @Override
    protected void setViewShowState(View view, int visibility) {
        if (view == mIvCover && visibility != VISIBLE) {
            return;
        }
        super.setViewShowState(view, visibility);
    }

    @Override
    public void onBufferingUpdate(int percent) {
        super.onBufferingUpdate(percent);

    }

    public void showLoading(boolean show) {
        if (show) {
            mLoadingProgressBar.setVisibility(View.VISIBLE);
            mHandler.sendEmptyMessage(MSG_UPDATE_INFO);
        } else {
            mLoadingProgressBar.setVisibility(View.INVISIBLE);
            mHandler.removeMessages(MSG_UPDATE_INFO);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeMessages(MSG_UPDATE_INFO);
    }

    private final int MSG_UPDATE_INFO = 666;


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            TextView tvSpeed = findViewById(R.id.tv_network_speed);
            long speed = getGSYVideoManager().getNetSpeed();
            Formatter.BytesResult result = Formatter.formatBytes(speed);
            tvSpeed.setText(result.value + "" + result.units + "/s");
            KLog.d(TAG, "显示网络速度 = " + speed);
            removeMessages(MSG_UPDATE_INFO);
            sendEmptyMessageDelayed(MSG_UPDATE_INFO, 1000);
        }
    };
}
