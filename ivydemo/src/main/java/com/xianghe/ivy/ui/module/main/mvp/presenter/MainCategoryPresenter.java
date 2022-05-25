package com.xianghe.ivy.ui.module.main.mvp.presenter;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.xianghe.ivy.R;
import com.xianghe.ivy.app.IvyApp;
import com.xianghe.ivy.constant.Global;
import com.xianghe.ivy.constant.RxBusCode;
import com.xianghe.ivy.model.CategoryMovieBean;
import com.xianghe.ivy.model.UserBean;
import com.xianghe.ivy.model.evnetbus.MovieUpdateEvent;
import com.xianghe.ivy.model.response.BaseResponse;
import com.xianghe.ivy.model.response.ResponseIndexCategoryList;
import com.xianghe.ivy.mvp.BasePresenter;
import com.xianghe.ivy.ui.module.main.mvp.MainCategoryContact;
import com.xianghe.ivy.ui.module.main.mvp.MainContact;
import com.xianghe.ivy.ui.module.main.mvp.mode.MainCategoryMode;
import com.xianghe.ivy.ui.push.PushModel;
import com.xianghe.ivy.utils.KLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.util.List;

import gorden.rxbus2.RxBus;
import gorden.rxbus2.ThreadMode;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainCategoryPresenter extends BasePresenter<MainCategoryContact.View> implements MainCategoryContact.Presenter {
    private static final String TAG = "MainCategoryPresenter";

    private final Context mAppContext;

    private boolean isLoadingData = false;

    private MainContact.IMode mMode = new MainCategoryMode();

    private UserRelationShipReceiver mRelationShipReceiver = new UserRelationShipReceiver();

    public MainCategoryPresenter(Context context) {
        mAppContext = context.getApplicationContext();
    }

    @gorden.rxbus2.Subscribe(code = RxBusCode.ACT_USER_PUSH_RELATIVE, threadMode = ThreadMode.MAIN)
    public void onReciveEventAddFriend(PushModel pushModel) {
        KLog.d(TAG, " pushModel = " + pushModel);
        if (pushModel == null) {
            return;
        }

        if (view() != null) {
            view().onAddFriend(pushModel.getId());
        }
    }

    @gorden.rxbus2.Subscribe(code = RxBusCode.ACT_MAIN_USER_PROFILE_UPDATE_AVATAR, threadMode = ThreadMode.MAIN)
    public void onMyPortraitUpdate(String newUrl) {
        if (view() != null) {
            add(mMode.getUserInfo()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<BaseResponse<UserBean>>() {
                        @Override
                        public void accept(BaseResponse<UserBean> response) throws Exception {
                            if (response != null && response.getStatus() == BaseResponse.Status.OK) {
                                UserBean userBean = response.getData();
                                if (userBean != null) {
                                    view().updateMyPortrait(userBean.getAvatar());
                                }
                            }
                        }
                    }));
        }
    }

    @Subscribe()
    public void onReceiveEventMovieUpdate(MovieUpdateEvent event) {
        KLog.w(TAG, "onReceiveEventMovieUpdate:");
        if (event == null) {
            return;
        }
        CategoryMovieBean movie = event.getMovieBean();
        if (movie != null) {
            if (view() != null) {
                view().updateMovie((CategoryMovieBean) movie);
            }
        }
    }

    @Override
    public void receiveBroadcast() {
        EventBus.getDefault().register(this);
        RxBus.get().register(this);
        mRelationShipReceiver.startReceiver(mAppContext);
    }

    @Override
    public void unReceiveBroadcast() {
        EventBus.getDefault().unregister(this);
        RxBus.get().unRegister(this);
        mRelationShipReceiver.stopReceiver(mAppContext);
    }

    @SuppressLint("CheckResult")
    public void refreshCategoryList(final String category, boolean formUser) {
        if (isLoadingData) {
            return;
        }

        isLoadingData = true;
        view().showLoading(true);

        add(mMode.getCategoryList(category, formUser ? -1 : 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseIndexCategoryList>() {
                    @Override
                    public void accept(ResponseIndexCategoryList response) throws Exception {
                        KLog.w(TAG, "response: " + response);
                        isLoadingData = false;

                        if (view() != null) {
                            view().finishLoadMore(0);
                            view().showLoading(false);
                            if (response != null && response.getStatus() == BaseResponse.Status.OK) {
                                view().refreshCategoryList(response.getData(), formUser);
                                view().showLoading(false);
                            } else {
                                view().clearData();
                                view().showDateEmpty();
                                view().showLoading(false);
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        KLog.e(TAG, "throwable: " + throwable);
                        isLoadingData = false;

                        if (view() != null) {
                            view().finishLoadMore(0);
                            view().showNetWorkError();
                            view().showLoading(false);
                        }
                    }
                }));
    }

    @SuppressLint("CheckResult")
    public void loadMoreCategoryList(final String category, int page, boolean fromUser) {
        if (isLoadingData) {
            return;
        }
        KLog.e(TAG, "请求 loadMoreCategoryList: category = " + category + ", page = " + page);

        isLoadingData = true;

        add(mMode.getCategoryList(category, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseIndexCategoryList>() {
                    @Override
                    public void accept(ResponseIndexCategoryList response) throws Exception {
                        KLog.w(TAG, "response: " + response);
                        isLoadingData = false;
                        if (view() == null) {
                            return;
                        }

                        // 隐藏 loading
                        view().finishLoadMore(0);


                        if (response != null && response.getStatus() == BaseResponse.Status.OK) {
                            // 请求成功
                            List<CategoryMovieBean> data = response.getData();
                            view().loadMoreCategoryList(page, data);

                            // 用户操作时，返回数据为空，提示用户
                            if (fromUser && (data == null || data.size() == 0)) {
                                view().showErrorMsg(mAppContext.getString(R.string.view_no_more_media_data));
                            }
                        } else {
                            // 请求失败
                            if (page == 1) {
                                view().loadMoreCategoryList(page, response.getData());
                                view().showDateEmpty();
                            }

                            // 用户操作时，提示用户错误信息
                            if (fromUser) {
                                view().showErrorMsg(BaseResponse.infoCode2String(mAppContext, response.getInfoCode()));
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        KLog.e(TAG, "throwable: " + throwable);
                        isLoadingData = false;

                        if (view() != null) {
                            view().finishLoadMore(0);

                            if (page == 1) {
                                view().showNetWorkError();
                            } else {
                                view().showErrorMsg(IvyApp.getInstance().getResources().getString(R.string.common_network_error));
                            }
                        }
                    }
                }));
    }


    private class UserRelationShipReceiver extends BroadcastReceiver {

        public void startReceiver(Context context) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Global.USER_RELATIONSHIP_CHANGE);
            context.registerReceiver(this, filter);
        }

        public void stopReceiver(Context context) {
            context.unregisterReceiver(this);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            long uid = intent.getLongExtra(Global.EXTRA_KEY_UID, 0);
            int caseWhat = intent.getIntExtra(Global.EXTRA_KEY_CASE, Global.CASE_UNKOWN);
            if (view() != null) {
                view().onUserRelationShipChange(uid, caseWhat);
            }
        }
    }
}
