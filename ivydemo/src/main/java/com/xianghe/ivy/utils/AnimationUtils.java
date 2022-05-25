package com.xianghe.ivy.utils;

import android.animation.Animator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
 * author:  ycl
 * date:  2018/12/6 14:21
 * desc:
 */
public class AnimationUtils {

    public static Animation createVerticalAnimation(float fromY, float toY, long durationMillis) {
        Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, fromY, Animation.RELATIVE_TO_SELF, toY);
        animation.setDuration(durationMillis);
        return animation;
    }

    public static void cancel(Animation animation) {
        if (animation != null) {
            animation.cancel();
        }
    }

    public static void cancel(Animator animator) {
        if (animator != null) {
            animator.cancel();
        }
    }
}
