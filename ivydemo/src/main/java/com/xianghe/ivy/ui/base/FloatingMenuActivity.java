package com.xianghe.ivy.ui.base;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.xianghe.ivy.R;

import com.xianghe.ivy.mvp.BaseMVPActivity;
import com.xianghe.ivy.mvp.IBaseConctact;
import com.xianghe.ivy.utils.KLog;
import com.xianghe.ivy.utils.ViewUtil;
import com.xianghe.ivy.weight.circlemenu.CircleMenu;
import com.xianghe.ivy.weight.circlemenu.IMenu;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public abstract class FloatingMenuActivity<V extends IBaseConctact.IBaseView, P extends IBaseConctact.IBasePresenter<V>> extends DownloadActivity<V, P> {
    private static final String TAG = "FloatingMenuActivity";
    public static final String KEY_HIDE_MENU = "key_hide_menu";

    public ViewGroup mLayoutRoot;

    protected CircleMenu mMenu;
    private ImageView mBtnAction;
    private ImageView mBtnCamera;
    public ImageView mBtnMine;
    private ImageView mBtnHome;

    private final Handler mHandler = new Handler();
    private boolean isHideMenu = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLayoutRoot = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.activity_floating_menu, null, false);
        super.setContentView(mLayoutRoot);

        // init
        Intent intent = getIntent();
        if (intent != null) {
            isHideMenu = intent.getBooleanExtra(KEY_HIDE_MENU, true);
        }

        // findView
        mMenu = mLayoutRoot.findViewById(R.id.floating_menu);

        // 初始化view
        initFloatMenu();

        // 设置事件
        mBtnCamera.setOnClickListener(mMenuClickListener);
        mBtnMine.setOnClickListener(mMenuClickListener);

        mBtnHome.setOnClickListener(mMenuClickListener);

        if (!isHideMenu) {
            mMenu.setVisibility(View.VISIBLE);

        } else {
            mMenu.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        collepsedMenu(false);
    }

    @Override
    public void setContentView(int layoutResID) {
        View view = LayoutInflater.from(this).inflate(layoutResID, mLayoutRoot, false);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        mLayoutRoot.addView(view, 0, params);
    }

    @Override
    public void setContentView(View view) {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        mLayoutRoot.addView(view, 0, params);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        KLog.d(TAG, "setContentView");
        if (params == null) {
            params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
        }
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;

        mLayoutRoot.addView(view, 0, params);
    }

    protected void onClickMenuCamera(@NonNull IMenu menus) {
    }

    protected void onClickMenuMine(@NonNull IMenu menus) {
    }

    protected void onClickMenuHome(@NonNull IMenu menus) {
    }

    protected void onMenuExpandFinish(){

    }

    private void initFloatMenu() {
        mBtnAction = new ImageView(this);
        mBtnCamera = new ImageView(this);
        mBtnMine = new ImageView(this);
        mBtnHome = new ImageView(this);

        mBtnCamera.setId(R.id.btn_main_menu_camera);
        mBtnMine.setId(R.id.btn_main_menu_mine);
        mBtnHome.setId(R.id.btn_main_menu_home);

        mBtnAction.setImageResource(R.drawable.btn_icon_circle_menu_collected_selector);
        mBtnCamera.setImageResource(R.drawable.btn_icon_circle_menu_camera_selector);
        mBtnMine.setImageResource(R.drawable.btn_icon_circle_menu_mine_selector);
        mBtnHome.setImageResource(R.drawable.btn_icon_circle_menu_home_selector);

        int btnSizePx = ViewUtil.dp2px(this, 60);

        mMenu.setActionItem(new CircleMenu.Item(mBtnAction, btnSizePx, btnSizePx));
        mMenu.setItems(Arrays.asList(
                new CircleMenu.Item(mBtnHome, btnSizePx, btnSizePx),
                new CircleMenu.Item(mBtnMine, btnSizePx, btnSizePx),
                new CircleMenu.Item(mBtnCamera, btnSizePx, btnSizePx)));
        mMenu.setAnchor(CircleMenu.Anchor.RIGHT_BOTTOM);
        mMenu.setAnchorOffsetX(ViewUtil.dp2px(getBaseContext(), -50));
        mMenu.setAnchorOffsetY(ViewUtil.dp2px(getBaseContext(), -45));
        mMenu.setMenuRadiusMax(ViewUtil.dp2px(getBaseContext(), 125));
        mMenu.setItemRadius(ViewUtil.dp2px(getBaseContext(), 85));
        mMenu.setMenuRadiusMin(0);
        mMenu.setStartAngle(170);
        mMenu.setSweepAngle(170);
        mMenu.setMenuColor(0xA0000000);
    }

    public ImageView getBtnCamera() {
        return mBtnCamera;
    }

    public ImageView getBtnMine() {
        return mBtnMine;
    }

    protected void onDownLoadListDragging() {
        KLog.d(TAG, "onDownLoadListDragging");
        if (mMenu.getState() != IMenu.State.CLOSED) {
            mMenu.close(true);
        }
    }


    protected void onDownLoadListSetting() {
        KLog.d(TAG, "onDownLoadListSetting");
        if (mMenu.getState() != IMenu.State.CLOSED) {
            mMenu.close(true);
        }
    }

    private Runnable mMenuChangeRunnable = null;

    public IMenu.State getMenuState() {
        return mMenu == null ? IMenu.State.UNKNOWN : mMenu.getState();
    }

    public void expendedMenu(boolean animated) {
        if (isHideMenu) {
            return;
        }

        mHandler.removeCallbacks(mMenuChangeRunnable);
        mMenuChangeRunnable = new Runnable() {
            @Override
            public void run() {
                if (mMenu != null) {
                    mMenu.open(animated);
                    onMenuExpandFinish();
                }
            }
        };
        mHandler.postDelayed(mMenuChangeRunnable, 200);
    }

    public void collepsedMenu(boolean animated) {
        if (isHideMenu) {
            return;
        }

        mHandler.removeCallbacks(mMenuChangeRunnable);
        mMenuChangeRunnable = new Runnable() {
            @Override
            public void run() {
                mMenu.close(animated);
            }
        };
        mHandler.postDelayed(mMenuChangeRunnable, 200);
    }

    protected void onDownLoadListCollapsed() {
        KLog.d(TAG, "onDownLoadListCollapsed");
    }

    protected void onDownLoadListExpanded() {
        KLog.d(TAG, "onDownLoadListExpanded");
    }

    protected void onDownLoadListHidden() {
        KLog.d(TAG, "onDownLoadListHidden");
    }

    protected void onDownLoadListHalfExpanded() {
        KLog.d(TAG, "onDownLoadListHalfExpanded");
    }

    private View.OnClickListener mMenuClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_main_menu_camera:
                    onClickMenuCamera(mMenu);
                    break;
                case R.id.btn_main_menu_mine:
                    onClickMenuMine(mMenu);
                    break;
                case R.id.btn_main_menu_home:
                    onClickMenuHome(mMenu);
                    break;
            }
        }
    };
}
