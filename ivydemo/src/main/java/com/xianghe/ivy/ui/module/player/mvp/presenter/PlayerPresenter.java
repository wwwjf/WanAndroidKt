package com.xianghe.ivy.ui.module.player.mvp.presenter;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;

import com.blankj.utilcode.util.AppUtils;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.xianghe.ivy.R;
import com.xianghe.ivy.app.IvyApp;
import com.xianghe.ivy.constant.Global;
import com.xianghe.ivy.constant.RxBusCode;
import com.xianghe.ivy.http.request.RequestError;
import com.xianghe.ivy.http.response.RespCode;
import com.xianghe.ivy.http.scheduler.SchedulerProvider;
import com.xianghe.ivy.manager.UserInfoManager;
import com.xianghe.ivy.model.CategoryMovieBean;
import com.xianghe.ivy.model.UserBean;
import com.xianghe.ivy.model.VersionBean;
import com.xianghe.ivy.model.response.BaseResponse;
import com.xianghe.ivy.model.response.ResponseIndexCategoryList;
import com.xianghe.ivy.mvp.BasePresenter;
import com.xianghe.ivy.ui.base.FloatingMenuActivity;
import com.xianghe.ivy.ui.module.main.mvp.MainContact;
import com.xianghe.ivy.ui.module.player.mvp.contact.PlayerContact;
import com.xianghe.ivy.ui.module.player.mvp.mode.PlayerMode;
import com.xianghe.ivy.ui.module.record.RecordActivity;
import com.xianghe.ivy.ui.module.user.UserActivity;
import com.xianghe.ivy.ui.module.user.UserContract;
import com.xianghe.ivy.ui.push.PushModel;
import com.xianghe.ivy.utils.IvyUtils;
import com.xianghe.ivy.utils.KLog;

import org.jetbrains.annotations.Contract;

import java.io.Serializable;
import java.util.List;

import gorden.rxbus2.RxBus;
import gorden.rxbus2.Subscribe;
import gorden.rxbus2.ThreadMode;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class PlayerPresenter extends BasePresenter<PlayerContact.IView> implements PlayerContact.IPresenter {
    private static final String TAG = "PlayerPresenter";

    private boolean isLoadMore = false;

    private Handler mHandler = new Handler();
    private Runnable mRunnable = null;

    private final Context mContext;

    private final PlayerContact.IMode mMode = new PlayerMode();
    private final NetworkConnectChangedReceiver mReceiver = new NetworkConnectChangedReceiver();
    private final UserRelationShipReceiver mRelationShipReceiver = new UserRelationShipReceiver();

    public PlayerPresenter(Context context) {
        mContext = context;
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
                                if (userBean!=null){
                                    view().updateMyPortrait(userBean.getAvatar());
                                }
                            }
                        }
                    }));
        }
    }

    @Subscribe(code = RxBusCode.ACT_USER_PUSH_RELATIVE, threadMode = ThreadMode.MAIN)
    public void onReciveEventAddFriend(PushModel pushModel) {
        KLog.d(TAG, " pushModel = " + pushModel);
        if (pushModel == null) {
            return;
        }

        if (view() != null) {
            view().onAddRelative(pushModel.getId());
        }
    }

    @Subscribe(code = RxBusCode.ACT_USER_LOGIN, threadMode = ThreadMode.MAIN)
    public void onReciveLogin() {
        if (view() != null) {
            view().refreshData();
        }
    }

    @Subscribe(code = RxBusCode.ACT_WECHAT_LOGIN_SUCCESS, threadMode = ThreadMode.MAIN)
    public void onReciveLoginWithWeChat(String userTag) {
        if (view() != null) {
            view().refreshData();
        }
    }

    @Subscribe(code = RxBusCode.ACT_USER_LOGOUT, threadMode = ThreadMode.MAIN)
    public void onReciveLogout() {
        if (view() != null) {
            view().refreshData();
        }
    }

    @SuppressLint("CheckResult")
    @Override
    public void refresh(final String category) {
        if (TextUtils.isEmpty(category)) {
            KLog.i(TAG, "取消刷新");

            if (view() != null) {
                view().finishRefresh(500);
            }
            return;
        }

        if (isLoadMore) {
            return;
        }

        add(mMode.getCategoryList(category, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseIndexCategoryList>() {
                    @Override
                    public void accept(ResponseIndexCategoryList response) throws Exception {
                        if (response != null && response.getStatus() == BaseResponse.Status.OK) {
                            if (view() != null) {
                                view().refreshCategoryList(response.getData());
                                view().finishRefresh(100);
                                view().finishLoadMore(100);
                            }
                        } else {
                            if (view() != null) {
                                view().showMsg(BaseResponse.infoCode2String(mContext, response.getInfoCode()));
                                view().finishRefresh(100);
                                view().finishLoadMore(100);
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        isLoadMore = false;
                        if (view() != null) {
                            view().showMsg(mContext.getString(R.string.common_net_error_please_try_again));
                            view().finishRefresh(100);
                            view().finishLoadMore(100);
                        }
                    }
                }));
    }

    @SuppressLint("CheckResult")
    @Override
    public void loadMoreCategoryList(boolean formUser, final String category, final int page) {
        if (TextUtils.isEmpty(category)) {
            KLog.i(TAG, "取消刷新");

            if (view() != null) {
                view().finishLoadMore(500);
            }
            return;
        }

        if (isLoadMore) {
            return;
        }
        KLog.e(TAG, "请求加载 category = " + category + ", page = " + page);

        isLoadMore = true;
        KLog.d();
        add(mMode.getCategoryList(category, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseIndexCategoryList>() {
                    @Override
                    public void accept(ResponseIndexCategoryList response) throws Exception {
                        isLoadMore = false;

                        if (response != null && response.getStatus() == BaseResponse.Status.OK) {
                            if (view() != null) {
                                view().addCategoryList(response.getData());
                                view().finishLoadMore(100);
                            }

                        } else {
                            if (view() != null) {
                                if (formUser) {
                                    view().showMsg(BaseResponse.infoCode2String(mContext, response.getInfoCode()));
                                }
                                view().finishLoadMore(100);
                            }
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        isLoadMore = false;
                        if (view() != null) {
                            if (formUser) {
                                view().showMsg(mContext.getString(R.string.common_net_error_please_try_again));
                            }
                            view().finishLoadMore(100);
                        }
                    }
                }));
    }

    @Override
    public void loadMoreUserMediaList(boolean formUser, long uid, int page) {
        if (isLoadMore) {
            return;
        }
        KLog.e(TAG, "请求加载 UserMedia： page = " + page);

        isLoadMore = true;
        add(mMode.getUserMediaList(uid, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseIndexCategoryList>() {
                    @Override
                    public void accept(ResponseIndexCategoryList response) throws Exception {
                        isLoadMore = false;

                        if (response != null && response.getStatus() == BaseResponse.Status.OK) {
                            if (view() != null) {
                                view().addCategoryList(response.getData());
                                view().finishLoadMore(100);

                            }
                        } else {
                            if (view() != null) {
                                if (formUser) {
                                    view().showMsg(BaseResponse.infoCode2String(mContext, response.getInfoCode()));
                                }
                                view().finishLoadMore(100);
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        isLoadMore = false;
                        if (view() != null) {
                            if (formUser) {
                                view().showMsg(mContext.getString(R.string.common_net_error_please_try_again));
                            }
                            view().finishLoadMore(100);
                        }
                    }
                }));
    }



    @Override
    public void loadMoreCollectMediaList(boolean formUser, int page) {
        if (isLoadMore) {
            return;
        }
        KLog.e(TAG, "请求加载 UserMedia： page = " + page);

        isLoadMore = true;
        add(mMode.getCollectMediaList(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseIndexCategoryList>() {
                    @Override
                    public void accept(ResponseIndexCategoryList response) throws Exception {
                        isLoadMore = false;

                        if (response != null && response.getStatus() == BaseResponse.Status.OK) {
                            if (view() != null) {
                                view().addCategoryList(response.getData());
                                view().finishLoadMore(100);
                            }

                        } else {
                            if (view() != null) {
                                if (formUser) {
                                    view().showMsg(BaseResponse.infoCode2String(mContext, response.getInfoCode()));
                                }
                                view().finishLoadMore(100);
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        isLoadMore = false;
                        if (view() != null) {
                            if (formUser) {
                                view().showMsg(mContext.getString(R.string.common_net_error_please_try_again));
                            }
                            view().finishLoadMore(100);
                        }
                    }
                }));
    }

    @Override
    public void followUser(long uid) {
        if (view() == null) {
            return;
        }

        if (!isLogin()) {
            view().showLoginConfirmDialog();
            return;
        }

        add(mMode.addFriend(uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BaseResponse>() {
                    @Override
                    public void accept(BaseResponse baseResponse) throws Exception {
                        KLog.d(TAG, "关注用户： " + baseResponse);
                        if (view() == null) {
                            return;
                        }

                        if (baseResponse != null && baseResponse.getStatus() == BaseResponse.Status.OK) {
                            view().onFollowUserResult(uid, true);

                            // 发送广播
                            Intent intent = new Intent();
                            intent.setAction(Global.USER_RELATIONSHIP_CHANGE);
                            intent.putExtra(Global.EXTRA_KEY_UID, uid);
                            intent.putExtra(Global.EXTRA_KEY_CASE, Global.CASE_FOLLOW);
                            mContext.sendBroadcast(intent);
                        } else {
                            view().onFollowUserResult(uid, false);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        KLog.d(TAG, "关注用户异常： " + throwable);
                        if (view() != null) {
                            view().showMsg(mContext.getString(R.string.common_net_error_please_try_again));
                            view().onFollowUserResult(uid, false);
                        }
                    }
                }));
    }

    @Override
    public void startListener() {
        RxBus.get().register(this);
        mReceiver.startReceiver(mContext);
        mRelationShipReceiver.startReceiver(mContext);
    }

    @Override
    public void stopListener() {
        RxBus.get().unRegister(this);
        mReceiver.stopReceiver(mContext);
        mRelationShipReceiver.stopReceiver(mContext);
    }

    @Override
    public void checkVersion() {
        PlayerContact.IView  view = view();
        if (view == null) {
            return;
        }
        add(mMode.checkVersion()
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

    @Override
    public void jump2UserInfoInfoNeed(Context context, long userId, float totalMoveX, float totalMoveY, int maxX, int maxY) {
        if (maxX != 0 && Math.abs(totalMoveX) > maxX / 4) {
            jump2UserInfo(context, userId, false);
        }
    }

    private long lastOpenUserInfoStamp;

    @Override
    public void jump2UserInfo(Context context, long userId, boolean withMenu) {
        if (!isLogin()) {
            view().showLoginConfirmDialog();
            return;
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastOpenUserInfoStamp < 800) {
            return;
        }
        lastOpenUserInfoStamp = currentTime;


        Intent intent = new Intent(context, UserActivity.class);
        intent.putExtra(FloatingMenuActivity.KEY_HIDE_MENU, !withMenu);
        Uri uri = Uri.parse("ivy://UserActivity?uid=" + userId);
        intent.setData(uri);
        context.startActivity(intent);
    }

    @Override
    public void requestData(long uid) {
        PlayerContact.IView view = view();
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
        add(mMode.requestData(uid)
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
    public void jump2Record(Context context) {
        /*if (!isLogin()) {
            view().showLoginConfirmDialog();
            return;
        }*/

       /* if (UploadTaskManager.getInstance().hasTasks()) {
            ToastUtils.showShort("当前有上传任务未完成，不能跳录制");
            return;
        }*/

        //跳转录制界面
        Intent intent = new Intent(context, RecordActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void stopMediaPlayer() {
        GSYVideoManager.releaseAllVideos();
    }

    @Override
    public void pauseMediaPlayer() {
        KLog.d(TAG, "pauseMediaPlayer");
        mHandler.removeCallbacks(mRunnable);
        mRunnable = new Runnable() {
            @Override
            public void run() {
                GSYVideoManager.onPause();
            }
        };
        mHandler.postDelayed(mRunnable, 200);
    }

    @Override
    public void resumeMediaPlayer() {
        KLog.i(TAG, "resumeMediaPlayer");
        mHandler.removeCallbacks(mRunnable);
        mRunnable = new Runnable() {
            @Override
            public void run() {
                GSYVideoManager.onResume();
            }
        };
        mHandler.postDelayed(mRunnable, 200);
    }

    private boolean isLogin() {
        return UserInfoManager.isLogin();
    }

    public class NetworkConnectChangedReceiver extends BroadcastReceiver {
        public void startReceiver(Context context) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            context.registerReceiver(this, filter);
        }

        public void stopReceiver(Context context) {
            context.unregisterReceiver(this);
        }

        private String getConnectionType(int type) {
            String connType = "";
            if (type == ConnectivityManager.TYPE_MOBILE) {
                connType = "3G网络数据";
            } else if (type == ConnectivityManager.TYPE_WIFI) {
                connType = "WIFI网络";
            }
            return connType;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager cmm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cmm == null) {
                delayNetworkNotFoundTip();
                return;
            }

            Network[] allNetworks = cmm.getAllNetworks();
            if (allNetworks == null || allNetworks.length <= 0) {
                delayNetworkNotFoundTip();
                return;
            }

            for (Network network : allNetworks) {
                NetworkInfo networkInfo = cmm.getNetworkInfo(network);
                if (network == null) {
                    continue;
                }
                KLog.d(TAG, networkInfo.getTypeName() + "  " + networkInfo.isConnected());
                if (networkInfo.isConnected()) {
                    // 有一个网络连接就说明网络可用...
                    mHandler.removeCallbacks(mToastRunnable);
                    return;
                }
            }
            // 走到最后都没网络可用, 提示用户网络不可用
            delayNetworkNotFoundTip();
        }
    }

    private void delayNetworkNotFoundTip() {
        mHandler.removeCallbacks(mToastRunnable);
        mHandler.postDelayed(mToastRunnable, 2000);
    }

    private Runnable mToastRunnable = new Runnable() {
        @Override
        public void run() {
            if (view() != null) {
                view().showMsg(mContext.getString(R.string.common_network_not_found));
                pauseMediaPlayer();
            }
        }
    };

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
