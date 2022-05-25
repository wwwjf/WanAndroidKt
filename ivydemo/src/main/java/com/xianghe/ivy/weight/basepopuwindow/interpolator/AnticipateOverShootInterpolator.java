package com.xianghe.ivy.weight.basepopuwindow.interpolator;

import android.view.animation.LinearInterpolator;

/**
 * Created by yanchunlan on 2016/1/28.
 */
public class AnticipateOverShootInterpolator extends LinearInterpolator {
    private float factor;

    public AnticipateOverShootInterpolator() {
        this.factor = (float) (2.0*1.5);
    }
    public AnticipateOverShootInterpolator(float factor) {
        this.factor = (float) (factor*1.5);
    }

    @Override
    public float getInterpolation(float input) {
        if (input < 0.5)
            return (float) (0.5 * a(input * 2.0, factor));
        else
           return (float) (0.5 * (o(input * 2.0 - 2.0, input) + 2.0));
    }

    private float a(double input,float factor){
        return (float) (input * input * ((factor + 1) * input - factor));
    }
    private float o(double input,float factor){
        return (float) (input * input * ((factor + 1) * input + factor));
    }
}
