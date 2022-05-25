package com.xianghe.ivy.ui.module.main.mvp.view.fragment.category;


import android.os.Bundle;

import com.xianghe.ivy.R;
import com.xianghe.ivy.constant.Api;
import com.xianghe.ivy.model.CategoryMovieBean;

import java.util.ArrayList;
import java.util.List;

import static com.xianghe.ivy.ui.module.main.mvp.view.activity.MainActivity.LIST_DATA;


public class MainRecommendFragment extends AbsCategoryFragment {

    @Override
    public String getCategory() {
        return Api.Value.CategoryType.RECOMMEND;
    }

    @Override
    protected String getDateEmptyText() {
        return getString(R.string.index_categroy_data_empty);
    }

    @Override
    protected int getDataEmptyIcon() {
        return R.mipmap.img_tongyong200;
    }

    @Override
    public void onShow() {
        // 不处理
    }

    @Override
    public void refreshData() {
        // 第一次 refreshData 时。
        // LauncherActivity 预请求的数据，如果存在直接使用。
        // 没有泽正常请求
        Bundle arguments = getArguments();
        if (arguments != null) {
            ArrayList<CategoryMovieBean> categoryMovieBeans = (ArrayList<CategoryMovieBean>) arguments.getSerializable(LIST_DATA);
            if (categoryMovieBeans == null) {
                super.refreshData();
            }
            setArguments(null);
        } else {
            super.refreshData();
        }
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null) {
            ArrayList<CategoryMovieBean> categoryMovieBeans = (ArrayList<CategoryMovieBean>) arguments.getSerializable(LIST_DATA);
            if (categoryMovieBeans != null) {
                mAdapter.setDatas(categoryMovieBeans);
            }
        }
    }

    @Override
    public void refreshCategoryList(List<CategoryMovieBean> movies, boolean formeUser) {
        try {
            super.refreshCategoryList(movies, formeUser);
            int centerPosition = mLayoutManager.getCurrentPosition();
            int itemCount = mAdapter.getItemCount();

            if (!formeUser) {
                if (itemCount > 2 && centerPosition == 0) {
                    mLayoutManager.scrollToPosition(2);
                    mAdapter.setFocus(2);
                } else if (centerPosition > itemCount) {
                    mLayoutManager.scrollToPosition(2);
                    mAdapter.setFocus(2);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
