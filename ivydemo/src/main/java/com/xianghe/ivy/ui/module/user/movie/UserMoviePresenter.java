package com.xianghe.ivy.ui.module.user.movie;

import android.os.Handler;
import android.os.SystemClock;

import com.blankj.utilcode.util.NetworkUtils;
import com.xianghe.ivy.app.IvyApp;
import com.xianghe.ivy.constant.Api;
import com.xianghe.ivy.constant.Global;
import com.xianghe.ivy.http.request.RequestError;
import com.xianghe.ivy.http.response.RespCode;
import com.xianghe.ivy.http.scheduler.SchedulerProvider;
import com.xianghe.ivy.manager.UserInfoManager;
import com.xianghe.ivy.model.CategoryMovieBean;
import com.xianghe.ivy.model.response.BaseResponse;
import com.xianghe.ivy.mvp.loadPager.BaseLoadingPresenter;
import com.xianghe.ivy.utils.KLog;

import java.util.List;


public class UserMoviePresenter extends BaseLoadingPresenter<UserMovieContract.View>
        implements UserMovieContract.Presenter {
    private UserMovieModel mMovieModel = new UserMovieModel();

    @Override
    public void requestData(long uid, int page) {
        UserMovieContract.View view = view();
        if (view == null) {
            return;
        }
        //判断是否有缓存
        long cacheUid;
        if (uid == 0 || uid == -1) {
            cacheUid = UserInfoManager.getUid();
        } else {
            cacheUid = uid;
        }

        //只取第一页缓存数据
        if (page == Api.Value.START_PAGE) {
            @SuppressWarnings("unchecked")
            BaseResponse<List<CategoryMovieBean>> cacheMovies = (BaseResponse<List<CategoryMovieBean>>) IvyApp.getInstance().getACache()
                    .getAsObject(Global.cache_key_userMovieResponse + cacheUid);
            if (cacheMovies != null) {
                KLog.e("---个人影片信息获取缓存数据");
                new Handler().postDelayed(() -> view.onRequestDataSuccess(cacheMovies, page),50);
//                return;
            }
        }

        add(mMovieModel.requestData(uid, page)
                .observeOn(SchedulerProvider.INSTANCE.ui())
                .subscribe(response -> {
                    if (page == Api.Value.START_PAGE) {
                        IvyApp.getInstance().getACache().put(Global.cache_key_userMovieResponse+cacheUid,response,2*60);
                        view.onRequestDataSuccess(response, page);
                    } else {
                        view.onLoadMoreDataSuccess(response, page);
                    }
                        },
                        throwable -> {
                            clossLoading();
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

    @Override
    public void deleteUserMovie(long movieId,int position) {
        UserMovieContract.View view = view();
        if (view == null) {
            return;
        }
        add(mMovieModel.deleteUserMovie(movieId)
                .observeOn(SchedulerProvider.INSTANCE.ui())
                .subscribe(response -> {view.onDeleteUserMovieSuccess(response,position);}, view::onDeleteUserMovieFailed));
    }
}
