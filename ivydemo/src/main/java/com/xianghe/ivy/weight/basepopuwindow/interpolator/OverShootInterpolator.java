package com.xianghe.ivy.weight.basepopuwindow.interpolator;

import android.view.animation.LinearInterpolator;

/**
 * Created by yanchunlan on 2016/1/28.
 */
public class OverShootInterpolator extends LinearInterpolator {
    private float factor;

    public OverShootInterpolator() {
        this.factor = 2.0f;
    }

    public OverShootInterpolator(float factor) {
        this.factor = factor;
    }

    @Override
    public float getInterpolation(float input) {

        input -= 1.0;
        return (float) (input * input * ((factor + 1) * input + factor) + 1.0);
    }
}
