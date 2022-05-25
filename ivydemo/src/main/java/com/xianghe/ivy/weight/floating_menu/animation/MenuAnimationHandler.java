//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xianghe.ivy.weight.floating_menu.animation;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.graphics.Point;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;

import com.xianghe.ivy.weight.floating_menu.FloatingMenu;

public abstract class MenuAnimationHandler {
    protected FloatingMenu menu;

    public MenuAnimationHandler() {
    }

    public void setMenu(FloatingMenu menu) {
        this.menu = menu;
    }

    public void animateMenuOpening(Point center) {
        if (this.menu == null) {
            throw new NullPointerException("MenuAnimationHandler cannot animate without a valid FloatingMenu.");
        }
    }

    public void animateMenuClosing(Point center) {
        if (this.menu == null) {
            throw new NullPointerException("MenuAnimationHandler cannot animate without a valid FloatingMenu.");
        }
    }

    protected void restoreSubActionViewAfterAnimation(FloatingMenu.Item subActionItem, MenuAnimationHandler.ActionType actionType) {
        LayoutParams params = (LayoutParams) subActionItem.view.getLayoutParams();
        if (params == null) {
            return;
        }
        subActionItem.view.setTranslationX(0.0F);
        subActionItem.view.setTranslationY(0.0F);
        subActionItem.view.setRotation(0.0F);
        subActionItem.view.setScaleX(1.0F);
        subActionItem.view.setScaleY(1.0F);
        subActionItem.view.setAlpha(1.0F);
        if (actionType == MenuAnimationHandler.ActionType.OPENING) {
            params.setMargins(subActionItem.x, subActionItem.y, 0, 0);
            subActionItem.view.setLayoutParams(params);
        } else if (actionType == MenuAnimationHandler.ActionType.CLOSING) {
            Point center = this.menu.getActionViewCenter();
            params.setMargins(center.x - subActionItem.width / 2, center.y - subActionItem.height / 2, 0, 0);
            subActionItem.view.setLayoutParams(params);
            ((ViewGroup) this.menu.getActivityContentView()).removeView(subActionItem.view);
        }

    }

    public abstract boolean isAnimating();

    protected abstract void setAnimating(boolean var1);

    public class LastAnimationListener implements AnimatorListener {
        public LastAnimationListener() {
        }

        public void onAnimationStart(Animator animation) {
            MenuAnimationHandler.this.setAnimating(true);
        }

        public void onAnimationEnd(Animator animation) {
            MenuAnimationHandler.this.setAnimating(false);
        }

        public void onAnimationCancel(Animator animation) {
            MenuAnimationHandler.this.setAnimating(false);
        }

        public void onAnimationRepeat(Animator animation) {
            MenuAnimationHandler.this.setAnimating(true);
        }
    }

    protected static enum ActionType {
        OPENING,
        CLOSING;

        private ActionType() {
        }
    }
}
