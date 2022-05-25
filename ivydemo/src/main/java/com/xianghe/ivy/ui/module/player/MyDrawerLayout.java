package com.xianghe.ivy.ui.module.player;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;

/**
 * 原生的 DrawerLayout 会默认吃掉点击事件， 该类主要重写 DrawerLayout 的 {@link DrawerLayout#onTouchEvent(MotionEvent)}.
 * 使其只有在 {@link MyDrawerLayout#isDrawerOpen()}, 有 drawer 打开的时候开会处理点击事件.
 * <p>
 * 简单重写只为实现UI需求, 正常交互使用仍有问题 (可以增加 drawerstate == DrawerLayout.STATE_IDLE 的判断, 但是涉及反射, 无需求不处理).
 */
public class MyDrawerLayout extends DrawerLayout {
    public MyDrawerLayout(@NonNull Context context) {
        super(context);
    }

    public MyDrawerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyDrawerLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(ev) && isDrawerOpen();
    }

    public boolean isDrawerOpen() {
        return isDrawerOpen(Gravity.LEFT) || isDrawerOpen(Gravity.TOP) || isDrawerOpen(Gravity.RIGHT) || isDrawerOpen(Gravity.BOTTOM);
    }

    @Override
    public boolean isDrawerOpen(@NonNull View drawer) {
        try {
            return super.isDrawerOpen(drawer);
        } catch (Exception e) {
            // do nothing
        }
        return false;
    }

    @Override
    public boolean isDrawerOpen(int drawerGravity) {
        try {
            return super.isDrawerOpen(drawerGravity);
        } catch (Exception e) {
            // do nothing
        }
        return false;
    }
}
