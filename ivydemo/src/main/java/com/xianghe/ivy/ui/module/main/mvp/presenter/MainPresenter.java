package com.xianghe.ivy.ui.module.main.mvp.presenter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.blankj.utilcode.util.AppUtils;
import com.xianghe.ivy.constant.RxBusCode;
import com.xianghe.ivy.http.scheduler.SchedulerProvider;
import com.xianghe.ivy.manager.UserInfoManager;
import com.xianghe.ivy.model.QrCodeInfo;
import com.xianghe.ivy.model.VersionBean;
import com.xianghe.ivy.mvp.BasePresenter;
import com.xianghe.ivy.ui.module.main.mvp.MainContact;
import com.xianghe.ivy.ui.module.main.mvp.mode.MainCategoryMode;
import com.xianghe.ivy.utils.IvyUtils;
import com.xianghe.ivy.utils.KLog;
import com.xianghe.ivy.utils.LanguageUtil;

import gorden.rxbus2.RxBus;
import gorden.rxbus2.Subscribe;
import gorden.rxbus2.ThreadMode;

public class MainPresenter extends BasePresenter<MainContact.View> implements MainContact.Presenter {
    private static final String TAG = "MainPresenter";

    private final Context mContext;
    private MainCategoryMode mModel = new MainCategoryMode();

    private QrCodeInfo mQrCodeInfo;

    public MainPresenter(Context context) {
        mContext = context;
    }

    @Subscribe(code = RxBusCode.ACT_FOLLOW, threadMode = ThreadMode.MAIN)
    public void onReciveActionFollow() {
        KLog.i(TAG, "onReciveActionFollow");
        if (view().getCurrentPage() != MainContact.View.PAGE_FOLLOW) {
            view().showFollowBadge(true);
        }
        view().refreshPage(MainContact.View.PAGE_FOLLOW);
    }

    @Subscribe(code = RxBusCode.ACT_QYQLIST, threadMode = ThreadMode.MAIN)
    public void onReciveActionMoments() {
        KLog.i(TAG, "onReciveActionMoments");
        if (view().getCurrentPage() != MainContact.View.PAGE_MOMENTS) {
            view().showMomentsBadge(true);
        }
        view().refreshPage(MainContact.View.PAGE_MOMENTS);
    }

    @Subscribe(code = RxBusCode.ACT_USER_LOGIN, threadMode = ThreadMode.MAIN)
    public void onReciveLogin() {
        view().refreshPage(MainContact.View.PAGE_FOLLOW);
        view().refreshPage(MainContact.View.PAGE_MOMENTS);
        view().refreshPage(MainContact.View.PAGE_RECOMENED);
        if (LanguageUtil.isSimplifiedChinese(mContext)) {
            view().showShareQrCode();
            view().showMemberIcon();
            //查询用户是否会员
            getUserHomePage(UserInfoManager.getUid());
        } else {
            view().hideShareQrCode();
            view().hideMemberIcon();
        }
    }

    @Subscribe(code = RxBusCode.ACT_WECHAT_LOGIN_SUCCESS, threadMode = ThreadMode.MAIN)
    public void onReciveLoginWithWeChat(String userTag) {
        view().refreshPage(MainContact.View.PAGE_FOLLOW);
        view().refreshPage(MainContact.View.PAGE_MOMENTS);
        view().refreshPage(MainContact.View.PAGE_RECOMENED);
        if (LanguageUtil.isSimplifiedChinese(mContext)) {
            view().showShareQrCode();
            view().showMemberIcon();
            //查询用户是否会员
            getUserHomePage(UserInfoManager.getUid());
        } else {
            view().hideShareQrCode();
            view().hideMemberIcon();
        }
    }

    @Subscribe(code = RxBusCode.ACT_USER_LOGOUT, threadMode = ThreadMode.MAIN)
    public void onReciveLogout() {
        // 退出登录 关注和亲友圈是没数据的 直接清空体验极佳(注：推荐是可以请求到数据的，只能刷新一下了)
        view().clearPage(MainContact.View.PAGE_FOLLOW);
        view().clearPage(MainContact.View.PAGE_MOMENTS);
        view().clearPage(MainContact.View.PAGE_RECOMENED);
        view().refreshPage(MainContact.View.PAGE_RECOMENED);
        view().hideShareQrCode();
        view().hideMemberIcon();
    }

    /**
     * 亲友圈变化
     */
    @Subscribe(code = RxBusCode.ACT_RELATIVE_FRIEND, threadMode = ThreadMode.MAIN)
    public void onReciveActionRemoveMoments() {
        KLog.i(TAG, "onReciveActionMoments");
        if (view().getCurrentPage() != MainContact.View.PAGE_MOMENTS) {
            view().showMomentsBadge(true);
        }
        view().refreshPage(MainContact.View.PAGE_MOMENTS);
    }

    @Override
    public void startListenEvent() {
        RxBus.get().register(this);
    }

    @Override
    public void stopListenEvent() {
        RxBus.get().unRegister(this);
    }

    @Override
    public void checkVersion() {
        MainContact.View view = view();
        if (view == null) {
            return;
        }
        add(mModel.checkVersion()
                .observeOn(SchedulerProvider.INSTANCE.ui())
                .subscribe(response -> {
                            VersionBean data = response.getData();
                            if (TextUtils.isEmpty(data.getUrl())) {//1.0.0
                                return;
                            }
                            if (IvyUtils.compareVersion(data.getAsk_version(), AppUtils.getAppVersionName()) > 0) {
                                //强制升级
                                view.showUpdateView(data, true);

                            } else if (IvyUtils.compareVersion(data.getVersion(), AppUtils.getAppVersionName()) > 0) {
                                //提示升级
                                view.showUpdateView(data, false);
                            }
                        },
                        view::checkVersionFailed));
    }

//    @Override
//    public void checkQrCode() {
//        add(mModel.getShareQrCode()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<ResponseIndexShareQrCode>() {
//                    @Override
//                    public void accept(ResponseIndexShareQrCode response) throws Exception {
//                        if (response == null || view() == null) {
//                            return;
//                        }
//
//                        // 能获取到 qrcode 才显示分享二维码, 其他情况不显示
//                        if (response.getStatus() == BaseResponse.Status.OK) {
//                            mQrCodeInfo = response.getData();
//                            if (mQrCodeInfo != null && !TextUtils.isEmpty(mQrCodeInfo.getQrCode())) {
//                                return;
//                            }
//                        }
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        KLog.e(TAG, "throwable: " + throwable);
//                        if (view() != null) {
//                            view().showMsg(mContext.getString(R.string.common_network_error));
//                        }
//                    }
//                })
//        );
//    }


    @Override
    public void getInvitationCode() {

    }

    @Override
    public void getUserHomePage(long uid) {
        MainContact.View view = view();
        if (view == null){
            return;
        }
        add(mModel.getUserHomePage(uid)
                .observeOn(SchedulerProvider.INSTANCE.ui())
                .subscribe(response -> {
                    if (response == null || response.getData() == null) {
                        return;
                    }
                    if (response.getData().getMembership() == 1) {
                        UserInfoManager.putUserIsMember(true);
                        view.showMemberIcon();
                    }
                },throwable -> {
                    KLog.e("getUserHomePage error");
                }));
    }

    @Override
    public void jump2ShareQrCode(Context context) {
//        Intent intent = new Intent(context, NewInvitationActivity.class);
//        context.startActivity(intent);
    }
}
