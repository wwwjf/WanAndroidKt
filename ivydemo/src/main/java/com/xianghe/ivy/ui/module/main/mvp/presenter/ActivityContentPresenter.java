package com.xianghe.ivy.ui.module.main.mvp.presenter;

import android.app.Activity;

import com.xianghe.ivy.http.request.RequestError;
import com.xianghe.ivy.http.response.RespCode;
import com.xianghe.ivy.mvp.loadPager.BaseLoadingPresenter;
import com.xianghe.ivy.ui.module.main.mvp.ActivityContentContract;
import com.xianghe.ivy.ui.module.main.mvp.mode.ActivityContentModel;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @创建者 Allen
 * @创建时间 2019/3/28 15:17
 * @描述      活动内容
 */
public class ActivityContentPresenter extends BaseLoadingPresenter<ActivityContentContract.View> implements ActivityContentContract.Presenter {

    private ActivityContentModel mContentModel;
    private Activity mContext;

    public ActivityContentPresenter(Activity context) {
        mContext = context;
        mContentModel = new ActivityContentModel();
    }

    @Override
    public void getContentDetails(ActivityContentContract.View view, int activity_type) {
        if (view == null) {
            return;
        }
        if (activity_type == 0) {
            return;
        }
        add(mContentModel.getContentDetails(activity_type)
        .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    view.setContentDetails( response.getData());
                }, throwable -> {
                    if (throwable instanceof RequestError) {
                        int code = ((RequestError) throwable).getState();
                        if (code == RespCode.CODE_901) {
                            //无网络
                            view.onNetworkError(throwable);
                        } else {
                            view.onFailed(throwable);
                        }
                    } else {
                        view.onFailed(throwable);
                    }
                }));
    }

}
