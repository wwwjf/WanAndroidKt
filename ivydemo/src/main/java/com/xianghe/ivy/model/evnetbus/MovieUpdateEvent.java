package com.xianghe.ivy.model.evnetbus;


import androidx.annotation.Nullable;

import com.xianghe.ivy.model.CategoryMovieBean;

public class MovieUpdateEvent {
    private CategoryMovieBean mMovieBean;

    public MovieUpdateEvent() {
    }

    public MovieUpdateEvent(CategoryMovieBean movieBean) {
        mMovieBean = movieBean;
    }

    @Nullable
    public CategoryMovieBean getMovieBean() {
        return mMovieBean;
    }

    public void setMovieBean(CategoryMovieBean movieBean) {
        mMovieBean = movieBean;
    }
}
