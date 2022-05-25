package com.xianghe.ivy.ui.module.player.mvp.view.fragment.base;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;

import com.xianghe.ivy.mvp.IBaseConctact;

public abstract class MenuFragment<V extends IBaseConctact.IBaseView, P extends IBaseConctact.IBasePresenter<V>> extends PlayerFrameFragment<V, P> {

    public abstract void onMenuOpened();

    public abstract void onMenuClosed();

    protected void requestMenuClose() {
        DrawerLayout drawerLayout = findParentDrawer(getView());
        if (drawerLayout!=null){
            drawerLayout.closeDrawers();
        }
    }

    @Nullable
    private DrawerLayout findParentDrawer(View view) {
        if (view == null || view.getParent() == null) {
            return null;
        }

        ViewParent parent = view.getParent();
        if (parent instanceof DrawerLayout) {
            return (DrawerLayout) parent;
        } else if (parent instanceof ViewGroup) {
            return findParentDrawer((View) parent);
        } else {
            // should never happen.
            return null;
        }
    }
}
