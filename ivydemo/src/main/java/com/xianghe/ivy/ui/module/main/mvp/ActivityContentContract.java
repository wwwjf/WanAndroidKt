package com.xianghe.ivy.ui.module.main.mvp;

import com.xianghe.ivy.model.ActivityDetailsBean;
import com.xianghe.ivy.model.response.BaseResponse;
import com.xianghe.ivy.mvp.loadPager.IBaseLoadingConctact;

import io.reactivex.Observable;

/**
 * @创建者 Allen
 * @创建时间 2019/3/28 14:24
 * @描述      活动内容介绍
 */
public interface ActivityContentContract {

    interface Model extends IBaseLoadingConctact.IBaseMode{
        Observable<BaseResponse<ActivityDetailsBean>> getContentDetails(int activity_type);
    }

    interface Presenter extends IBaseLoadingConctact.IBasePresenter<ActivityContentContract.View>{
        void getContentDetails(ActivityContentContract.View view, int activity_type);
    }

    interface View extends IBaseLoadingConctact.IBaseView {
        void setContentDetails(ActivityDetailsBean contentDetails);
        void onNetworkError(Throwable throwable);
        void onFailed(Throwable throwable);

    }
}
