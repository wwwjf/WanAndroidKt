package com.xianghe.ivy.weight.circlemenu;

import android.animation.Animator;
import android.view.View;

import androidx.annotation.NonNull;

public interface IMenu {

    public enum State {
        UNKNOWN,
        START_OPEN,     // 开始打开 menu
        START_CLOSE,    // 开始关闭 menu
        CLOSING,
        OPENING,
        OPENED,         // 打开完成 menu
        CLOSED,         // 关闭完成 menu
    }

    /**
     * 开启menu
     *
     * @param animated true 执行动画;
     *                 false 不执行动画
     */
    public void open(boolean animated);

    /**
     * 关闭menu
     *
     * @param animated true 执行动画;
     *                 false 不执行动画
     */
    public void close(boolean animated);


    public interface IAnimatedAdapter<M extends IMenu> {

        public Animator onLayout(@NonNull M menu);

        public Animator obtainOpenAnimator(@NonNull M menu);

        public Animator obtainCloseAnimator(@NonNull M menu);
    }

    /**
     * menu item class
     */
    public interface IItem {
        public int getWidht();

        public int getHeight();

        public View getView();
    }


    /**
     * menu state listener
     */
    public interface OnStateChangeListener {
        public void onMenuStateChange(IMenu menu, State state);
    }
}
