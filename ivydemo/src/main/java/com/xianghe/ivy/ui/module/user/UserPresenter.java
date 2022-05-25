package com.xianghe.ivy.ui.module.user;

import android.text.TextUtils;

import com.google.gson.internal.LinkedTreeMap;
import com.xianghe.ivy.app.IvyApp;
import com.xianghe.ivy.constant.Global;
import com.xianghe.ivy.http.request.RequestError;
import com.xianghe.ivy.http.response.RespCode;
import com.xianghe.ivy.http.scheduler.SchedulerProvider;
import com.xianghe.ivy.manager.UserInfoManager;
import com.xianghe.ivy.model.MediaReportTypeBean;
import com.xianghe.ivy.model.UserBean;
import com.xianghe.ivy.model.response.BaseResponse;
import com.xianghe.ivy.mvp.loadPager.BaseLoadingPresenter;
import com.xianghe.ivy.utils.KLog;

import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class UserPresenter extends BaseLoadingPresenter<UserContract.View>
        implements UserContract.Presenter {
    private static final String TAG = "UserPresenter";

    private UserModel mUserModel = new UserModel();

    @Override
    public void requestData(long uid) {
        UserContract.View view = view();
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
        @SuppressWarnings("unchecked")
        BaseResponse<UserBean> userBeanBaseResponse = (BaseResponse<UserBean>) IvyApp.getInstance().getACache()
                .getAsObject(Global.cache_key_userBeanResponse + cacheUid);
        if (userBeanBaseResponse != null) {
            KLog.e("----个人主页信息获取缓存数据");
            view.onRequestDataSuccess(userBeanBaseResponse);
//            return;
        }
        long finalCacheUid = cacheUid;
        add(mUserModel.requestData(uid)
                .observeOn(SchedulerProvider.INSTANCE.ui())
                .subscribe(response -> {
                    IvyApp.getInstance().getACache().put(Global.cache_key_userBeanResponse + finalCacheUid, response);
                    view.onRequestDataSuccess(response);
                }, throwable -> {
                    if (throwable instanceof RequestError) {
                        int code = ((RequestError) throwable).getState();
                        if (code == RespCode.CODE_901) {
                            //无网络
                            view.onNetworkError(throwable);
                        } else {
                            view.onRequestFailed(throwable);
                        }
                    } else {
                        view.onRequestFailed(throwable);
                    }

                }));
    }

    @Override
    public void reportUser(long uid, int reportType, String content, int type) {
        UserContract.View view = view();
        if (view == null) {
            return;
        }
        add(mUserModel.reportUser(uid, reportType, content, type)
                .observeOn(SchedulerProvider.INSTANCE.ui()).subscribe(view::onReportUserSuccess,
                        throwable -> view.onReportUserFailed(throwable.getMessage())));
    }

    @Override
    public void blacklistUser(long uid, int isBlacklist) {
        UserContract.View view = view();
        if (view == null) {
            return;
        }
        int status;
        if (isBlacklist == 1) {//已在黑名单中
            status = 2;
        } else {//未在黑名单中
            status = 1;
        }
        add(mUserModel.blacklistUser(uid, status)
                .observeOn(SchedulerProvider.INSTANCE.ui()).subscribe(view::onBlacklistSuccess,
                        view::onBlacklistFailed));
    }

    @Override
    public void followUser(long friendId) {
        UserContract.View view = view();
        if (view == null) {
            return;
        }
        add(mUserModel.followUser(friendId).observeOn(SchedulerProvider.INSTANCE.ui()).subscribe(
                view::onFollowUserSuccess, view::onRequestFailed
        ));
    }

    @Override
    public void unFollowUser(long friendId) {
        UserContract.View view = view();
        if (view == null) {
            return;
        }
        add(mUserModel.unFollowUser(friendId).observeOn(SchedulerProvider.INSTANCE.ui()).subscribe(
                view::onUnFollowUserSuccess, view::onRequestFailed
        ));
    }

    @Override
    public void applyRelativeFriend(long uid) {
        UserContract.View view = view();
        if (view == null) {
            return;
        }
        add(mUserModel.applyRelativeFriend(uid).observeOn(SchedulerProvider.INSTANCE.ui()).subscribe(
                view::onApplyRelativeFriendSuccess, view::onRequestFailed
        ));
    }

    @Override
    public void deleteRelativeFriend(long uid) {
        UserContract.View view = view();
        if (view == null) {
            return;
        }
        add(mUserModel.deleteRelativeFriend(uid).observeOn(SchedulerProvider.INSTANCE.ui()).subscribe(
                view::onDeleteRelativeFriendSuccess, view::onRequestFailed
        ));
    }

    @Override
    public void showReportDialog() {
        UserContract.View view = view();
        if (view == null) {
            return;
        }
        add(mUserModel.requestReportType()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response != null && response.getStatus() == BaseResponse.Status.OK && response.getData() != null) {
                        // 请求成功
                        MediaReportTypeBean reportTypes = response.getData();
                        if (reportTypes != null) {
                            Map<Integer, String> items = new LinkedTreeMap<>();
                            if (reportTypes.getPeopleType() != null) {
                                for (MediaReportTypeBean.ReportType reportType : reportTypes.getPeopleType()) {
                                    String typeCodeString = BaseResponse.infoCode2String(IvyApp.getInstance().getApplicationContext(), reportType.getTypeCode());
                                    items.put(reportType.getId(),
                                            TextUtils.isEmpty(typeCodeString) ? "" : typeCodeString);
                                }
                            }
                            view.showReportDialog(items);
                        }
                    } else {
                        KLog.w(TAG, "请求错误取消举报 response = " + response);
                    }
                }, view::onRequestFailed));
    }
}
