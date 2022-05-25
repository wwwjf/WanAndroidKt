package com.xianghe.ivy.ui.module.player.mvp.view.fragment.base;

import com.xianghe.ivy.model.CategoryMovieBean;
import com.xianghe.ivy.mvp.BaseMVPFragment;
import com.xianghe.ivy.mvp.IBaseConctact;

public abstract class PlayerFrameFragment<V extends IBaseConctact.IBaseView, P extends IBaseConctact.IBasePresenter<V>> extends BaseMVPFragment<V, P> {

    protected CategoryMovieBean mMovieBean;

    public void onMovieChange(CategoryMovieBean movie) {
        mMovieBean = movie;
    }

    public CategoryMovieBean getMovie() {
        return mMovieBean;
    }
}
