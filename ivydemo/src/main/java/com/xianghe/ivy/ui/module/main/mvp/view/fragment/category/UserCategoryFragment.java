package com.xianghe.ivy.ui.module.main.mvp.view.fragment.category;

import com.xianghe.ivy.model.CategoryMovieBean;

import java.util.List;

public abstract class UserCategoryFragment extends AbsCategoryFragment {
    @Override
    public void onShow() {

    }

    @Override
    public void refreshCategoryList(List<CategoryMovieBean> movies, boolean formUser) {
        super.refreshCategoryList(movies, formUser);
        mViewList.scrollToPosition(0);
        mAdapter.setFocus(0);
    }
}
