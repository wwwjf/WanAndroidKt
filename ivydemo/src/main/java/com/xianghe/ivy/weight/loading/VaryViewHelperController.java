package com.xianghe.ivy.weight.loading;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.xianghe.ivy.R;

public class VaryViewHelperController {

    private IVaryViewHelper helper;

    private boolean isShowing;

    public VaryViewHelperController(View view) {
        this(new VaryViewHelper(view));
    }

    public VaryViewHelperController(IVaryViewHelper helper) {
        super();
        this.helper = helper;
    }

    public void showNetworkError(View.OnClickListener onClickListener) {
        View layout = helper.inflate(R.layout.view_pager_error);
        if (null != onClickListener) {
            layout.setOnClickListener(onClickListener);
        }
        helper.showLayout(layout);
    }

    public void showNetworkError(View.OnClickListener onClickListener,String content) {
        View layout = helper.inflate(R.layout.view_pager_error);
        TextView againBtn = layout.findViewById(R.id.tv_view_pager_error_content);
        if (!TextUtils.isEmpty(content)){
            againBtn.setText(content);
        }
        if (null != onClickListener) {
            layout.setOnClickListener(onClickListener);
        }
        helper.showLayout(layout);
    }

    public void showNetworkError(View.OnClickListener onClickListener,int errorResId) {
        View layout = helper.inflate(errorResId);
        if (null != onClickListener) {
            layout.setOnClickListener(onClickListener);
        }
        helper.showLayout(layout);
    }

    public void showLoading() {
        View layout = helper.inflate(R.layout.view_pager_loading);
        helper.showLayout(layout);
    }

    public void showLoading(int layoutRes) {
        View layout = helper.inflate(layoutRes);
        helper.showLayout(layout);
    }

    public void restore() {
        helper.restoreView();
    }
}
