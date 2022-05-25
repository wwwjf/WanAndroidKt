package com.xianghe.ivy.mvp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.xianghe.ivy.app.IvyApp;
import com.xianghe.ivy.app.umeng.UmengManager;
import com.xianghe.ivy.manager.PermissionManager;
import com.xianghe.ivy.utils.SizeAdapterUtils;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @Author: ycl
 * @Date: 2018/10/25 14:30
 * @Desc: 空实现，给你们具体实现
 */
public class BaseFragment extends Fragment {

    protected PermissionManager mPermissionManager;

    protected Activity mContext;

    protected View mRootView;

    private Unbinder mBind;

    @Override
    public void onAttach(Context context) {
        mContext = (Activity) context;
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPermissionManager = new PermissionManager(this);
        UmengManager.Companion.getInstance().closeActivityUmeng();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //由于某些原因, 屏幕旋转后 Fragment 的重建, 所以需要手动调用一次
        if (isNeedSizeAdapter())
            SizeAdapterUtils.INSTANCE.adaptActivity(IvyApp.getInstance(), getActivity());
        if (mRootView == null) {
            mRootView = inflater.inflate(getChildView(), container, false);
            mBind = ButterKnife.bind(this, mRootView);
            initChildData(savedInstanceState);
        } else {
            ViewGroup parent = (ViewGroup) mRootView.getParent();
            if (parent != null) {
                parent.removeView(mRootView);
            }
        }
        return mRootView;
    }

    protected void initChildData(Bundle savedInstanceState) {

    }

    protected int getChildView() {
        return 0;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isNeedUmeng()) {
            UmengManager.Companion.getInstance().onResume(getActivity());
        }
        UmengManager.Companion.getInstance().onPageStart(umPageName());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (mPermissionManager != null) {
            mPermissionManager.handleRequestPermissionsResult(mContext, requestCode, permissions, grantResults);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onDestroy() {
        if (mBind != null) {
            mBind.unbind();
        }
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isNeedUmeng()) {
            UmengManager.Companion.getInstance().onPause(getActivity());
        }
        UmengManager.Companion.getInstance().onPageEnd(umPageName());
    }


    // ------------ umeng start ----------------
    protected boolean isNeedUmeng() {
        return false;
    }

    protected String umPageName() {
        return this.getClass().getSimpleName();
    }
    // ------------ umeng end ----------------


    //是否使用适配方案
    protected boolean isNeedSizeAdapter() {
        return false;
    }
}
