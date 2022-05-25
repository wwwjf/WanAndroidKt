package com.xianghe.ivy.ui.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xianghe.ivy.R;
import com.xianghe.ivy.mvp.BaseMVPActivity;
import com.xianghe.ivy.mvp.IBaseConctact;
import com.xianghe.ivy.weight.behavior.DrawerSheetBehavior;


/**
 * 确定基本的UI架构
 *
 * @param <V>
 * @param <P>
 */
public abstract class DownloadWeightActivity<V extends IBaseConctact.IBaseView, P extends IBaseConctact.IBasePresenter<V>> extends BaseMVPActivity<V, P> {
    private ViewGroup mLayoutContent = null;
    private View mLayoutDownloadDrawer;
    private View mDownloadMenuBtn;

    protected DrawerSheetBehavior mDrawerBehavior;

    /**
     * sheet 开关点击
     */
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (mDrawerBehavior.getState()) {
                case DrawerSheetBehavior.STATE_COLLAPSED:
                    mDrawerBehavior.setState(DrawerSheetBehavior.STATE_EXPANDED);
                    break;
                default:
                    mDrawerBehavior.setState(DrawerSheetBehavior.STATE_COLLAPSED);
                    break;
            }
        }
    };

    /**
     * sheet 状态回调
     */
    private DrawerSheetBehavior.BottomSheetCallback sheetCallback = new DrawerSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View var1, int state) {
            if (DrawerSheetBehavior.STATE_EXPANDED == state) {
                mDownloadMenuBtn.setBackgroundResource(R.drawable.download_menu_toggle_state_expanded_background);
                onDownLoadListExpanded();
            } else if (DrawerSheetBehavior.STATE_COLLAPSED == state) {
                mDownloadMenuBtn.setBackgroundResource(R.drawable.download_menu_toggle_state_collected_background);
                onDownLoadListCollapsed();
            } else if (DrawerSheetBehavior.STATE_DRAGGING == state) {
                onDownLoadListDragging();
            } else if (DrawerSheetBehavior.STATE_SETTLING == state) {
                onDownLoadListSetting();
            } else if (DrawerSheetBehavior.STATE_HIDDEN == state) {
                onDownLoadListHidden();
            } else if (DrawerSheetBehavior.STATE_HALF_EXPANDED == state) {
                onDownLoadListHalfExpanded();
            }
        }

        @Override
        public void onSlide(@NonNull View var1, float var2) {

        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_download_weight);
        mLayoutContent = findViewById(R.id.layout_download_weight_content);
        mLayoutDownloadDrawer = findViewById(R.id.layout_download_drawer);
        mDownloadMenuBtn = findViewById(R.id.download_menu_btn);

        mDrawerBehavior = DrawerSheetBehavior.from(mLayoutDownloadDrawer);

        mDrawerBehavior.setBottomSheetCallback(sheetCallback);
        mDownloadMenuBtn.setOnClickListener(mOnClickListener);
    }

    @Override
    public void setContentView(int layoutResID) {
        View view = LayoutInflater.from(this).inflate(layoutResID, mLayoutContent, false);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        mLayoutContent.addView(view, 0, params);
    }

    @Override
    public void setContentView(View view) {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        mLayoutContent.addView(view, 0, params);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        if (params == null) {
            params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
        }
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;

        mLayoutContent.addView(view, 0, params);
    }

    protected abstract void onDownLoadListCollapsed();

    protected abstract void onDownLoadListExpanded();

    protected abstract void onDownLoadListDragging();

    protected abstract void onDownLoadListSetting();

    protected abstract void onDownLoadListHidden();

    protected abstract void onDownLoadListHalfExpanded();
}
