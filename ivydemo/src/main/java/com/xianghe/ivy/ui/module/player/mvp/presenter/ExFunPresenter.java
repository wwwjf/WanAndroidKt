package com.xianghe.ivy.ui.module.player.mvp.presenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import com.blankj.utilcode.util.AppUtils;
import com.google.gson.JsonElement;
import com.xianghe.ivy.R;
import com.xianghe.ivy.constant.Api;
import com.xianghe.ivy.http.request.NetworkRequest;
import com.xianghe.ivy.manager.UserInfoManager;
import com.xianghe.ivy.model.response.BaseResponse;
import com.xianghe.ivy.mvp.BasePresenter;
import com.xianghe.ivy.network.GsonHelper;
import com.xianghe.ivy.ui.module.player.mvp.contact.ExFunContact;
import com.xianghe.ivy.ui.module.player.mvp.mode.ExFunMode;
import com.xianghe.ivy.utils.IvyUtils;
import com.xianghe.ivy.utils.KLog;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ExFunPresenter extends BasePresenter<ExFunContact.IView> implements ExFunContact.Presenter {
    private static final String TAG = "ExFunPresenter";

    private final Context mContext;
    private final ExFunContact.IMode mMode = new ExFunMode();

    public ExFunPresenter(Context context) {
        mContext = context;
    }

    @SuppressLint("CheckResult")
    @Override
    public void follow(long mediaId, boolean follow) {
        if (follow) {
            add(mMode.collection(mediaId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<BaseResponse>() {
                        @Override
                        public void accept(BaseResponse response) throws Exception {
                            KLog.d(TAG, "收藏：" + response);
                            if (view() == null) {
                                return;
                            }
                            if (response != null && response.getStatus() == BaseResponse.Status.OK) {
                                view().followResult(mediaId, follow, true);
                            } else {
                                view().followResult(mediaId, follow, false);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            KLog.w(TAG, "收藏失败：" + throwable);
                            if (view() != null) {
                                view().followResult(mediaId, follow, false);
                                view().showMsg(mContext.getString(R.string.common_net_error_please_try_again));
                            }
                        }
                    }));
        } else {
            add(mMode.collectionCancel(mediaId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<BaseResponse>() {
                        @Override
                        public void accept(BaseResponse response) throws Exception {
                            KLog.d(TAG, "取消收藏：" + response);
                            if (view() == null) {
                                return;
                            }
                            if (response != null && response.getStatus() == BaseResponse.Status.OK) {
                                view().followResult(mediaId, follow, true);
                            } else {
                                view().followResult(mediaId, follow, false);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            KLog.w(TAG, "取消收藏失败：" + throwable);
                            if (view() != null) {
                                view().followResult(mediaId, follow, false);
                                view().showMsg(mContext.getString(R.string.common_net_error_please_try_again));
                            }
                        }
                    }));
        }
    }

    @SuppressLint("CheckResult")
    @Override
    public void praise(long mediaId, boolean praise) {
        if (praise) {
            add(mMode.praise(mediaId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<BaseResponse>() {
                        @Override
                        public void accept(BaseResponse response) throws Exception {
                            KLog.d(TAG, "点赞：" + response);
                            if (view() == null) {
                                return;
                            }

                            if (response != null & response.getStatus() == BaseResponse.Status.OK) {
                                view().praiseResult(mediaId, praise, true);
                            } else {
                                view().praiseResult(mediaId, praise, false);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (view() != null) {
                                view().praiseResult(mediaId, praise, false);
                                view().showMsg(mContext.getString(R.string.common_net_error_please_try_again));
                            }
                        }
                    }));
        } else {
            add(mMode.praiseCancel(mediaId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<BaseResponse>() {
                        @Override
                        public void accept(BaseResponse response) throws Exception {
                            KLog.d(TAG, "取消点赞：" + response);
                            if (response != null & response.getStatus() == BaseResponse.Status.OK) {
                                view().praiseResult(mediaId, praise, true);
                            } else {
                                view().praiseResult(mediaId, praise, false);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (view() != null) {
                                view().praiseResult(mediaId, praise, false);
                                view().showMsg(mContext.getString(R.string.common_net_error_please_try_again));
                            }
                        }
                    }));
        }
    }

    @Override
    public boolean isLogin() {
        return UserInfoManager.isLogin();
    }

    @Override
    public void jump2Login(Context context) {
//        context.startActivity(new Intent(context, LoginActivity.class));
    }
}
