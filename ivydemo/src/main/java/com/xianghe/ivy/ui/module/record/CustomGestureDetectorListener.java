package com.xianghe.ivy.ui.module.record;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.tencent.ugc.TXUGCRecord;
import com.xianghe.ivy.R;

import java.util.Objects;


public class CustomGestureDetectorListener implements GestureDetector.OnGestureListener {

    private Context mContext;

    private TextView mTextView;

    private TXUGCRecord mTXUGCRecord;

    private int index;

    private Handler mHandler;

    private final DismissTask mTask;

    private float mScaleFactor;

    private int defaultZoom;

    private final ScaleGestureDetector mScaleGestureDetector;

    private GestureDetector mGestureDetector;

    private String[] mBeautyFilterTypeString;

    public CustomGestureDetectorListener(Context context, TextView textView, TXUGCRecord txugcRecord) {
        mContext = context;
        mTextView = textView;
        mTXUGCRecord = txugcRecord;
        mTXUGCRecord.setSpecialRatio(1);
        mHandler = new Handler(context.getMainLooper());
        mTask = new DismissTask();
        mBeautyFilterTypeString = new String[]{
                Objects.requireNonNull(mContext).getResources().getString(R.string.common_normal),
                Objects.requireNonNull(mContext).getResources().getString(R.string.common_white_orchid),
                Objects.requireNonNull(mContext).getResources().getString(R.string.common_pure),
                Objects.requireNonNull(mContext).getResources().getString(R.string.common_beautiful),
                Objects.requireNonNull(mContext).getResources().getString(R.string.common_cherry_red),
                Objects.requireNonNull(mContext).getResources().getString(R.string.common_nostalgic),
                Objects.requireNonNull(mContext).getResources().getString(R.string.common_japanese),
        };

        //创建手势监听
        ScaleGestureDetector.OnScaleGestureListener onScaleGestureListener = new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                if (detector.getCurrentSpan() > mScaleFactor) {
                    zoom(txugcRecord, true);
                } else {
                    zoom(txugcRecord, false);
                }
                mScaleFactor = detector.getCurrentSpan();
                return false;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                mScaleFactor = detector.getCurrentSpan();
                //返回true才能进入onScale回调
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                mScaleFactor = detector.getCurrentSpan();
            }
        };
        mScaleGestureDetector = new ScaleGestureDetector(context, onScaleGestureListener);
    }

    public void resetZoom() {
        defaultZoom = 0;
    }

    public void attach(MotionEvent event) {
        mScaleGestureDetector.onTouchEvent(event);
    }

    public void onGestureAttach(MotionEvent event) {
        if (mGestureDetector == null) {
            mGestureDetector = new GestureDetector(this);
        }
        mGestureDetector.onTouchEvent(event);
    }

    private void zoom(TXUGCRecord txugcRecord, boolean isLarger) {
        if (isLarger) {
            if (defaultZoom < txugcRecord.getMaxZoom()) {
                defaultZoom += 1;
            }
        } else {
            if (defaultZoom > 0) {
                defaultZoom -= 1;
            }
        }
        txugcRecord.setZoom(defaultZoom);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (e1 == null || e2 == null) {
            return false;
        }

        mHandler.removeCallbacksAndMessages(null);
        int distance = 50;
        if (e2.getX() - e1.getX() > distance && velocityX > 100) {
            //想右滑动
            if (index == 0) {
                index = mBeautyFilterTypeString.length - 1;
            } else {
                index -= 1;
            }
            if (mTXUGCRecord != null) {
                mTXUGCRecord.setFilter(getFilterBitmapByIndex(index));
                showTextAnim(false);
            }
        } else if (e1.getX() - e2.getX() > distance && velocityX < -100) {
            //像左滑动
            if (index == mBeautyFilterTypeString.length - 1) {
                index = 0;
            } else {
                index += 1;
            }
            if (mTXUGCRecord != null) {
                mTXUGCRecord.setFilter(getFilterBitmapByIndex(index));
                showTextAnim(true);
            }
        }
        mHandler.postDelayed(mTask, 1500);
        return false;
    }

    private void showTextAnim(boolean isRight) {
        Animation animation;
        if (isRight) {
            animation = AnimationUtils.loadAnimation(mContext, R.anim.anim_right_in);
        } else {
            animation = AnimationUtils.loadAnimation(mContext, R.anim.anim_left_in);
        }
        mTextView.setVisibility(View.VISIBLE);
        mTextView.setText(mBeautyFilterTypeString[index]);
        mTextView.startAnimation(animation);
    }

    private Bitmap getFilterBitmapByIndex(int index) {
        Bitmap bmp;
        switch (index) {
            case 1:
                bmp = decodeResource(mContext.getResources(), R.drawable.filter_bailan);
                break;
            case 2:
                bmp = decodeResource(mContext.getResources(), R.drawable.filter_chunzhen);
                break;
            case 3:
                bmp = decodeResource(mContext.getResources(), R.drawable.filter_weimei);
                break;
            case 4:
                bmp = decodeResource(mContext.getResources(), R.drawable.filter_yinghong);
                break;
            case 5:
                bmp = decodeResource(mContext.getResources(), R.drawable.filter_huaijiu);
                break;
            case 6:
                bmp = decodeResource(mContext.getResources(), R.drawable.filter_rixi);
                break;
            default:
                bmp = null;
                break;
        }
        return bmp;
    }

    private Bitmap decodeResource(Resources resources, int id) {
        TypedValue value = new TypedValue();
        resources.openRawResource(id, value);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inTargetDensity = value.density;
        return BitmapFactory.decodeResource(resources, id, opts);
    }


    class DismissTask implements Runnable {

        @Override
        public void run() {
            if (mTextView != null) {
                if (mTextView.getVisibility() == View.VISIBLE) {
                    mTextView.setVisibility(View.GONE);
                }
            }

            mHandler.removeCallbacks(this);
        }
    }
}
