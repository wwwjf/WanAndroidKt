package com.xianghe.ivy.model;

import java.io.Serializable;

public class DownLoadBean implements Serializable {

    public static final int STATE_IDEL = 0;

    public static final int STATE_DOWNLOADING = 1;
    public static final int STATE_DOWNLOA_PAUSE = 2;
    public static final int STATE_DOWNLOAD_COMPELET = 3;
    public static final int STATE_DOWNLOAD_FAILED = 4;

    public static final int STATE_UPLOADING = 5;
    public static final int STATE_UPWNLOA_PAUSE = 6;
    public static final int STATE_UPLOAD_COMPELET = 7;
    public static final int STATE_UPLOAD_FAILED = 8;

    private CategoryMovieBean mMovieBean;

    private int state = STATE_IDEL;

    public CategoryMovieBean getMovieBean() {
        return mMovieBean;
    }

    public void setMovieBean(CategoryMovieBean movieBean) {
        mMovieBean = movieBean;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
