package com.xianghe.ivy.weight.basepopuwindow.interpolator;

import android.view.animation.LinearInterpolator;

/**
 * Created by yanchunlan on 2016/1/28.
 */
public class SpringInterpolator extends LinearInterpolator {
    private float factor;

    public SpringInterpolator() {
        this.factor = .4f;
    }
    public SpringInterpolator(float factor) {
        this.factor = factor;
    }

    @Override
    public float getInterpolation(float input) {
        return (float) (Math.pow(2, -10 * input) * Math.sin((input - factor / 4) * (2 * Math.PI) / factor) + 1);
    }
}
