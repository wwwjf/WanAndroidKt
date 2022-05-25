package com.xianghe.ivy.ui.module.user.movie;

import com.xianghe.ivy.model.CategoryMovieBean;
import com.xianghe.ivy.model.response.BaseResponse;
import com.xianghe.ivy.mvp.loadPager.IBaseLoadingConctact;

import java.util.List;

import io.reactivex.Observable;

public interface UserMovieContract {

    interface Model extends IBaseLoadingConctact.IBaseMode {
        Observable<BaseResponse<List<CategoryMovieBean>>> requestData(long uid, int page);

        Observable<BaseResponse<String>> deleteUserMovie(long movieId);
    }

    interface View extends IBaseLoadingConctact.IBaseView {

        void onFailed(Throwable throwable);

        void onRequestDataSuccess(BaseResponse<List<CategoryMovieBean>> d,int page);

        void onLoadMoreDataSuccess(BaseResponse<List<CategoryMovieBean>> response,int page);

        void onNetworkError(Throwable throwable);

        void onDeleteUserMovieSuccess(BaseResponse<String> response,int position);

        void onDeleteUserMovieFailed(Throwable throwable);
    }

    interface Presenter extends IBaseLoadingConctact.IBasePresenter<View> {
        void requestData(long uid, int page);

        void deleteUserMovie(long movieId,int position);
    }
}
