package com.xianghe.ivy.weight.scrollview;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

import com.xianghe.ivy.utils.KLog;

/**
 * author:  ycl
 * date:  2018/11/14 17:17
 * desc:  可以监听滚动状态的scrollView
 */
public class ObservableScrollView extends HorizontalScrollView {

    public interface OnScrollListener {

        /**
         * The view is not scrolling. Note navigating the list using the trackball counts as
         * being in the idle state since these transitions are not animated.
         */
        int SCROLL_STATE_IDLE = 0;

        /**
         * The user is scrolling using touch, and their finger is still on the screen
         */
        int SCROLL_STATE_TOUCH_SCROLL = 1;

        /**
         * The user had previously been scrolling using touch and had performed a fling. The
         * animation is now coasting to a stop
         */
        int SCROLL_STATE_FLING = 2;

        void onScrollStateChanged(ObservableScrollView view, int scrollState);

        void onScroll(ObservableScrollView view, boolean isTouchScroll, int l, int t, int oldl, int oldt);
    }

    private static final boolean DEBUG = false;

    private static final int CHECK_SCROLL_STOP_DELAY_MILLIS = 80;
    private static final int MSG_SCROLL = 1;

    private boolean mIsTouched = false;
    private int mScrollState = OnScrollListener.SCROLL_STATE_IDLE;

    private OnScrollListener mOnScrollListener;

    private final Handler mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {

        private int mLastX = Integer.MIN_VALUE;

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == MSG_SCROLL) {
                final int scrollX = getScrollX();
                log("handleMessage, lastY = " + mLastX + ", x = " + scrollX);
                if (!mIsTouched && mLastX == scrollX) {
                    mLastX = Integer.MIN_VALUE;
                    setScrollState(OnScrollListener.SCROLL_STATE_IDLE);
                } else {
                    mLastX = scrollX;
                    restartCheckStopTiming();
                }
                return true;
            }
            return false;
        }
    });

    private void restartCheckStopTiming() {
        mHandler.removeMessages(MSG_SCROLL);
        mHandler.sendEmptyMessageDelayed(MSG_SCROLL, CHECK_SCROLL_STOP_DELAY_MILLIS);
    }

    public ObservableScrollView(Context context) {
        super(context);
    }

    public ObservableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ObservableScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        mOnScrollListener = onScrollListener;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        handleDownEvent(ev);
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        handleUpEvent(ev);
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        log(String.format("onScrollChanged, isTouched = %s, l: %d --> %d, t: %d --> %d", mIsTouched, oldl, l, oldt, t));
        if (mIsTouched) {
            setScrollState(OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
        } else {
            setScrollState(OnScrollListener.SCROLL_STATE_FLING);
            restartCheckStopTiming();
        }
        if (mOnScrollListener != null) {
            mOnScrollListener.onScroll(this, mIsTouched, l, t, oldl, oldt);
        }
    }

    private void handleDownEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                log("handleEvent, action = " + ev.getAction());
                mIsTouched = true;
                break;
        }
    }

    private void handleUpEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                log("handleEvent, action = " + ev.getAction());
                mIsTouched = false;
                restartCheckStopTiming();
                break;
        }
    }

    private void setScrollState(int state) {
        if (mScrollState != state) {
            log(String.format("---- onScrollStateChanged, state: %d --> %d", mScrollState, state));
            mScrollState = state;
            if (mOnScrollListener != null) {
                mOnScrollListener.onScrollStateChanged(this, state);
            }
        }
    }

    private void log(String obj) {
        if (DEBUG) {
            KLog.d(getClass().getSimpleName(), obj);
        }
    }
}
