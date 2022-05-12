package com.wwwjf.common.business.mvp;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.wwwjf.base.BaseConstant;
import com.wwwjf.base.CustomProgress;
import com.wwwjf.base.KLog;
import com.wwwjf.base.PermissionManager;
import com.wwwjf.base.utils.ResourceValuesUtil;
import com.wwwjf.base.utils.StringUtil;
import com.wwwjf.base.utils.ToastUtil;
import com.wwwjf.common.R;
import com.wwwjf.common.business.ViewInject;
import com.wwwjf.mvp.view.LifeCycleMvpFragment;


public abstract class BaseFragment extends LifeCycleMvpFragment {

    public Context mContext;
    protected PermissionManager mPermissionManager;
    protected CustomProgress mCustomProgress;

    /*static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }*/
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        initText();
    }

    public abstract void initText();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View mView;

        ViewInject annotation = this.getClass().getAnnotation(ViewInject.class);
        int contentViewId;
        if (annotation != null) {
            contentViewId = annotation.contentViewId();
        } else {
            contentViewId = getLayoutId();
        }
        if (contentViewId <= 0) {
            throw new RuntimeException("contentViewId 为 0");
        }

        mView = initFragmentView(inflater, contentViewId);
        bindView(mView);
        if (savedInstanceState != null) {
            String params1 = savedInstanceState.getString(BaseConstant.IntentKey.PARAMS_1);
            KLog.e("====params1:" + params1);
        }
        mCustomProgress = new CustomProgress(mContext);
        onCreateBaseFragment();

        return mView;

    }

    private int getLayoutId() {
        return -1;
    }

    private View initFragmentView(LayoutInflater inflater, int contentLayoutId) {
        View view;
        int viewId;
        if (!isNeedRefresh()) {//无刷新

            if (isNeedToolbar()) {
                viewId = R.layout.activity_base_no_refresh_toolbar;
            } else {
                viewId = R.layout.activity_base_no_refresh_no_toolbar;
            }
        } else {//有刷新

            if (isNeedToolbar()) {
                viewId = R.layout.activity_base_refresh_toolbar;
            } else {
                viewId = R.layout.activity_base_refresh_no_toolbar;
            }

        }
        view = inflater.inflate(viewId, null);

        if (isNeedRefresh()) {
            SwipeRefreshLayout refreshLayout = view.findViewById(R.id.swipeRefresh_base);
            refreshLayout.setColorSchemeColors(ContextCompat.getColor(mContext, R.color.color_3B55E6));
            refreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
            refreshLayout.setProgressViewEndTarget(true, 200);
        }
        FrameLayout content = view.findViewById(R.id.fl_base_content);
        inflater.inflate(contentLayoutId, content);

        return view;
    }


    public boolean isUserLogin() {
        /*if (UserInfoManager.isLogin()) {
            return true;
        }
        requireActivity().startActivityForResult(new Intent(getActivity(), LoginActivity.class), EPayConstant.ActivityCode.REQUEST_CODE_10002);
        return false;*/
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mPermissionManager.handleRequestPermissionsResult(mContext, requestCode, permissions, grantResults);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPermissionManager = new PermissionManager(this);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden){
            return;
        }
        initText();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    public void showLoading() {

        if (mCustomProgress != null && !mCustomProgress.isShowing()) {
            mCustomProgress.show(ResourceValuesUtil.getString(mContext, "epay.loading"), true, null);
        }
    }

    public void disMissLoading() {
        if (mCustomProgress != null && mCustomProgress.isShowing()) {
            mCustomProgress.cancel();
        }
    }

    public void showToast(String msg) {
        if (StringUtil.isTrimEmpty(msg)) {
            return;
        }
        ToastUtil.show(mContext, msg);
    }

    @Override
    public void onDestroy() {
        if (mPermissionManager != null) {
            mPermissionManager = null;
        }
        super.onDestroy();
    }

    private void bindView(View view) {
//        ButterKnife.bind(this, view);
    }

    protected boolean isNeedToolbar() {
        return false;
    }

    protected boolean isNeedRefresh() {
        return false;
    }

    //模板方法设计模式
    public abstract void onCreateBaseFragment();
}