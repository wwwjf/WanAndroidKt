package com.xianghe.ivy.weight;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatImageView;

import com.xianghe.ivy.utils.KLog;

public class ClickButton extends AppCompatImageView {
    private static final String TAG = "ClickButton";

    private AnimatorSet mSet;

    public ClickButton(Context context) {
        super(context);
    }

    public ClickButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ClickButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void performanceClick() {
        if (mSet != null) {
            mSet.cancel();
        }

        PropertyValuesHolder[] holders1 = {
                PropertyValuesHolder.ofFloat(View.SCALE_X, getScaleX(), .5f),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, getScaleY(), .5f),
        };
        ObjectAnimator animator1 = ObjectAnimator.ofPropertyValuesHolder(this, holders1);
        PropertyValuesHolder[] holders2 = {
                PropertyValuesHolder.ofFloat(View.SCALE_X, .5f, 1),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, .5f, 1),
        };
        ObjectAnimator animator2 = ObjectAnimator.ofPropertyValuesHolder(this, holders2);

        mSet = new AnimatorSet();
        mSet.addListener(mAnimatorListener);
        mSet.playSequentially(animator1, animator2);
        mSet.setDuration(200);
        mSet.start();
    }

    AnimatorListenerAdapter mAnimatorListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationStart(Animator animation) {
            KLog.w(TAG, "禁止点击");
            setEnabled(false);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            KLog.e(TAG, "可点击");
            setEnabled(true);
        }
    };
}