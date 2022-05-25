package com.xianghe.ivy.ui.module.player.widget;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

import com.xianghe.ivy.utils.KLog;


public class NetWatcher {
    private static final String TAG = "NetWatcher";

    private final int MSG_REMOVE_TIMEOUT_COUNTER = 1;
    private final int MSG_TIMEOUT_COUNTER = 2;

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REMOVE_TIMEOUT_COUNTER:
                    removeTimeoutCounter();
                    break;
                case MSG_TIMEOUT_COUNTER:
                    onTimeout();
                    break;
            }
        }
    };

    private Listener mListener;

    public void startTimeoutCounter() {
        mHandler.removeMessages(MSG_REMOVE_TIMEOUT_COUNTER);
        if (!mHandler.hasMessages(MSG_TIMEOUT_COUNTER)) {
            mHandler.sendEmptyMessageDelayed(MSG_TIMEOUT_COUNTER, 30000);
        }
    }

    public void cancelTimeoutCounter() {
        mHandler.sendEmptyMessageDelayed(MSG_REMOVE_TIMEOUT_COUNTER, 1000);
    }

    private void removeTimeoutCounter() {
        KLog.e(TAG, "取消超时计时器");
        mHandler.removeMessages(MSG_TIMEOUT_COUNTER);
    }


    private void onTimeout() {
        KLog.e(TAG, "超时回调");
        if (mListener != null) {
            mListener.onTimeout();
        }
    }

    public Listener getListener() {
        return mListener;
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public interface Listener {
        public void onTimeout();
    }
}
