//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xianghe.ivy.weight.floating_menu.animation;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.Animator.AnimatorListener;
import android.graphics.Point;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.xianghe.ivy.weight.floating_menu.FloatingMenu;

public class DefaultAnimationHandler extends MenuAnimationHandler {
    protected static final int START_DELAY = 80;
    protected static final int DURATION = 250;
    protected static final int LAG_BETWEEN_ITEMS = 20;
    private boolean animating;

    public DefaultAnimationHandler() {
        this.setAnimating(false);
    }

    public void animateMenuOpening(Point center) {
        super.animateMenuOpening(center);
        this.setAnimating(true);
        Animator lastAnimation = null;

        for (int i = 0; i < this.menu.getSubActionItems().size(); ++i) {
            ((FloatingMenu.Item) this.menu.getSubActionItems().get(i)).view.setScaleX(0.0F);
            ((FloatingMenu.Item) this.menu.getSubActionItems().get(i)).view.setScaleY(0.0F);
            ((FloatingMenu.Item) this.menu.getSubActionItems().get(i)).view.setAlpha(0.0F);
            PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, new float[]{(float) (((FloatingMenu.Item) this.menu.getSubActionItems().get(i)).x - center.x + ((FloatingMenu.Item) this.menu.getSubActionItems().get(i)).width / 2)});
            PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, new float[]{(float) (((FloatingMenu.Item) this.menu.getSubActionItems().get(i)).y - center.y + ((FloatingMenu.Item) this.menu.getSubActionItems().get(i)).height / 2)});
            PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, new float[]{720.0F});
            PropertyValuesHolder pvhsX = PropertyValuesHolder.ofFloat(View.SCALE_X, new float[]{1.0F});
            PropertyValuesHolder pvhsY = PropertyValuesHolder.ofFloat(View.SCALE_Y, new float[]{1.0F});
            PropertyValuesHolder pvhA = PropertyValuesHolder.ofFloat(View.ALPHA, new float[]{1.0F});
            ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(((FloatingMenu.Item) this.menu.getSubActionItems().get(i)).view, new PropertyValuesHolder[]{pvhX, pvhY, pvhR, pvhsX, pvhsY, pvhA});
            animation.setDuration(DURATION);
            animation.setInterpolator(new OvershootInterpolator(0.9F));
            animation.addListener(new DefaultAnimationHandler.SubActionItemAnimationListener((FloatingMenu.Item) this.menu.getSubActionItems().get(i), ActionType.OPENING));
            if (i == 0) {
                lastAnimation = animation;
            }

            //animation.setStartDelay((long)((this.menu.getSubActionItems().size() - i) * 20));
            animation.setStartDelay(i * START_DELAY);
            animation.start();
        }

        if (lastAnimation != null) {
            lastAnimation.addListener(new LastAnimationListener());
        }

    }

    public void animateMenuClosing(Point center) {
        super.animateMenuOpening(center);
        this.setAnimating(true);
        Animator lastAnimation = null;

        for (int i = 0; i < this.menu.getSubActionItems().size(); ++i) {
            PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, new float[]{(float) (-(((FloatingMenu.Item) this.menu.getSubActionItems().get(i)).x - center.x + ((FloatingMenu.Item) this.menu.getSubActionItems().get(i)).width / 2))});
            PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, new float[]{(float) (-(((FloatingMenu.Item) this.menu.getSubActionItems().get(i)).y - center.y + ((FloatingMenu.Item) this.menu.getSubActionItems().get(i)).height / 2))});
            PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, new float[]{-360.0F});
            PropertyValuesHolder pvhsX = PropertyValuesHolder.ofFloat(View.SCALE_X, new float[]{0.0F});
            PropertyValuesHolder pvhsY = PropertyValuesHolder.ofFloat(View.SCALE_Y, new float[]{0.0F});
            PropertyValuesHolder pvhA = PropertyValuesHolder.ofFloat(View.ALPHA, new float[]{0.0F});
            ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(((FloatingMenu.Item) this.menu.getSubActionItems().get(i)).view, new PropertyValuesHolder[]{pvhX, pvhY, pvhR, pvhsX, pvhsY, pvhA});
            animation.setDuration(DURATION);
            animation.setInterpolator(new AccelerateDecelerateInterpolator());
            animation.addListener(new DefaultAnimationHandler.SubActionItemAnimationListener((FloatingMenu.Item) this.menu.getSubActionItems().get(i), ActionType.CLOSING));
            if (i == 0) {
                lastAnimation = animation;
            }

            //animation.setStartDelay((long) ((this.menu.getSubActionItems().size() - i) * 20));
            animation.setStartDelay(i * START_DELAY);
            animation.start();
        }

        if (lastAnimation != null) {
            lastAnimation.addListener(new LastAnimationListener());
        }

    }

    public boolean isAnimating() {
        return this.animating;
    }

    protected void setAnimating(boolean animating) {
        this.animating = animating;
    }

    protected class SubActionItemAnimationListener implements AnimatorListener {
        private FloatingMenu.Item subActionItem;
        private ActionType actionType;

        public SubActionItemAnimationListener(FloatingMenu.Item subActionItem, ActionType actionType) {
            this.subActionItem = subActionItem;
            this.actionType = actionType;
        }

        public void onAnimationStart(Animator animation) {
        }

        public void onAnimationEnd(Animator animation) {
            DefaultAnimationHandler.this.restoreSubActionViewAfterAnimation(this.subActionItem, this.actionType);
        }

        public void onAnimationCancel(Animator animation) {
            DefaultAnimationHandler.this.restoreSubActionViewAfterAnimation(this.subActionItem, this.actionType);
        }

        public void onAnimationRepeat(Animator animation) {
        }
    }
}
