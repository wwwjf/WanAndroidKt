package com.xianghe.ivy.ui.module.welcom;

import static com.xianghe.ivy.constant.Api.REQUEST_CODE;
import static com.xianghe.ivy.ui.module.main.mvp.view.activity.MainActivity.LIST_DATA;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.xianghe.ivy.R;
import com.xianghe.ivy.app.IvyConstants;
import com.xianghe.ivy.app.em.EMManager;
import com.xianghe.ivy.constant.Api;
import com.xianghe.ivy.manager.PermissionManager;
import com.xianghe.ivy.manager.UserInfoManager;
import com.xianghe.ivy.model.CategoryMovieBean;
import com.xianghe.ivy.model.HuoDongInfoBean;
import com.xianghe.ivy.mvp.loadPager.BaseMVPCustomLoadingActivity;
import com.xianghe.ivy.ui.module.player.PlayerActivity;
import com.xianghe.ivy.ui.module.player.mvp.contact.PlayerContact;
import com.xianghe.ivy.utils.HuoDongHelper;
import com.xianghe.ivy.utils.KLog;
import com.xianghe.ivy.utils.LanguageUtil;
import com.xianghe.ivy.utils.NetworkUtil;
import com.xianghe.ivy.utils.ToastUtil;
import com.xianghe.ivy.weight.CustomProgress;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import gorden.rxbus2.RxBus;
import icepick.State;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

public class XWLauncherActivity extends BaseMVPCustomLoadingActivity<XWLauncherContact.View, XWLauncherPresenter> implements XWLauncherContact.View {
    private static final String TAG = "XWLauncherActivity";
    private static final String SP_NAME = "show_huo_dong";
    private static final String KEY_SHOW_HUO_DONG_PIC = "key_show_huo_dong_pic";

    //跳转判断
    private boolean markA = false;
    private boolean markB = false;
    /**
     * 从分享的视频中启动APP
     */
    public static final String SHARE_ACTION_OPEN_VIDEO = "open_video";
    public static final String SHARE_PLAYER_DATA = "playerData";

    private MainPermissionListener mPermissionListener;

    private boolean isPermission;

    private boolean isFinish;

    @State
    ArrayList<CategoryMovieBean> mCategoryMovieBeans;
    private TextView mBtnSkip;

    private static final int COUNTDOWN = 5;
    private String mAction = "";
    //网络状态标志
    private boolean networkState = true;
    /**
     * 是否正在请求视频详情信息
     */
    private boolean isRequestMediaDetail;
    private PlayerContact.IView.Data mPlayerData;

    private void setPermissions() {
        //判断权限
        mPermissionManager.requestPermissions(this, mPermissionListener, new String[]{
                android.Manifest.permission.READ_CONTACTS,
                android.Manifest.permission.READ_PHONE_STATE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_WIFI_STATE
        }, TAG, true);
    }

    private void startVideo() {
        final CustomVideoView vv = this.findViewById(R.id.videoView);
        vv.setVisibility(View.VISIBLE);
        final String uri = "android.resource://" + getPackageName() + "/" + R.raw.launcher;
        /*主要代码起始位置*/
        vv.setVideoURI(Uri.parse(uri));
        vv.start();
        vv.setOnPreparedListener(mp -> {
            mp.start();
            mp.setLooping(false);
        });
        vv.setOnCompletionListener(mp -> {
            isFinish = true;
            vv.stopPlayback();
            if (!XWLauncherActivity.this.isFinishing() && isPermission && !isRequestMediaDetail) {
                jump2Main();
                //跳转个人界面
//                Intent intent = new Intent(this, RecordActivity.class);
//                startActivity(intent);
//                finish();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        KLog.e(TAG, "=========");
    }

    @Override
    public void initData(@org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        dismissLoading();
        Intent intent = getIntent();
        if (!this.isTaskRoot()) {
            if (intent != null) {
                String action = intent.getAction();
                if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
                    finish();
                    return;
                }
            }
        }

        // --------- 清除本地登陆信息 start -----------------
        // 第一次安装app/登陆 如果无此字段，就清除本地登陆信息
        if (UserInfoManager.isLogin()) {
            if (!UserInfoManager.getBoolean(IvyConstants.SP_CLEAR_LOGIN)) {
                UserInfoManager.putBoolean(IvyConstants.SP_CLEAR_LOGIN, true);
                UserInfoManager.exitUser(); // 退出登陆
            }
        } else {
            if (!UserInfoManager.getBoolean(IvyConstants.SP_CLEAR_LOGIN)) {
                UserInfoManager.putBoolean(IvyConstants.SP_CLEAR_LOGIN, true);
            }
        }
        // 有字段了，且返回值为true,就不处理
        // --------- 清除本地登陆信息 end -----------------

        Log.e("123456789","" + UserInfoManager.isLogin());

       if( NetworkUtil.isNetworkConnected(XWLauncherActivity.this)){
           //请求接口
           mPresenter.refreshCategoryList(Api.Value.CategoryType.RECOMMEND);
           markB = true;
       }else{

           //CategoryMovieBean c = new CategoryMovieBean();

          // mCategoryMovieBeans =
           networkState = false;
           markB = true;

         //  showNetworkConnected();
           Log.e("987987","网络状态不可用");
       }


        mPermissionListener = new MainPermissionListener();

        // 全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_xwlauncher);

        boolean showHuoDongPic = false;
        if (LanguageUtil.isSimplifiedChinese(this)) {
            List<HuoDongInfoBean> huoDongs = HuoDongHelper.get(this).getHuoDongs();
            if (huoDongs != null && huoDongs.size() > 0) {
                final long currentTimeMillis = System.currentTimeMillis();
                for (HuoDongInfoBean huoDong : huoDongs) {
                    if (huoDong.getActivityType() == HuoDongInfoBean.ACTIVITY_TYPE_JI_ZHAN) {
                        if (huoDong.getStartTimeByMillisecond() < currentTimeMillis && huoDong.getEndTimeByMillisecond() > currentTimeMillis) {
                            showHuoDongPic = true;
                        }
                        break;
                    }
                }
            }
        }

        if (showHuoDongPic) {
            View btnJoin = findViewById(R.id.btn_join);
            btnJoin.setOnClickListener(mOnClickListener);

            mBtnSkip = findViewById(R.id.btn_skip);
            mBtnSkip.setOnClickListener(mOnClickListener);
            Observable.interval(1, 1, TimeUnit.SECONDS).take(COUNTDOWN)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(new Function<Long, Long>() {
                        @Override
                        public Long apply(Long aLong) throws Exception {
                            return COUNTDOWN - aLong - 1;
                        }
                    })
                    .subscribe(new Observer<Long>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            KLog.i(TAG, "d = " + d);
                            View layoutHuoDong = findViewById(R.id.layout_huo_dong);
                            layoutHuoDong.setBackgroundResource(R.mipmap.bg_pic);
                            layoutHuoDong.setVisibility(View.VISIBLE);
                            layoutHuoDong.setOnClickListener(mOnClickListener);
                        }

                        @Override
                        public void onNext(Long value) {
                            KLog.d(TAG, Thread.currentThread().getName() + " aLong = " + value);
                            mBtnSkip.setText(value + "s " + getString(R.string.common_skip));
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            //jump2Main();
                            isFinish = true;
                            if (!XWLauncherActivity.this.isFinishing() && isPermission) {
                                jump2Main();
                            }
                        }

                        @Override
                        public void onComplete() {
                            KLog.i(TAG, "");
                            isFinish = true;

                            if (!XWLauncherActivity.this.isFinishing() && isPermission && !isRequestMediaDetail) {
                                jump2Main();
                            }
                        }
                    });
        } else {
            startVideo();
        }
        setPermissions();
    }

    @Override
    public void initListener() {

    }

    @NotNull
    @Override
    public Object onCreateContentView(@Nullable Bundle bundle) {
        intMainData();
        return R.layout.activity_xwlauncher;
    }

    @Nullable
    @Override
    public XWLauncherPresenter createPresenter() {
        return new XWLauncherPresenter();
    }

    @Override
    public void getDataSuccess(List<CategoryMovieBean> data) {
        mCategoryMovieBeans = (ArrayList<CategoryMovieBean>) data;

        Log.e("8888","@@@@@3@@@");
        if(mCategoryMovieBeans == null || mCategoryMovieBeans.size() <= 0){
            ToastUtil.showToast(this,"获取视频失败！");
            showLogin();
           // return;
        }
        Log.e("8888","@@@@@4@@@");
        Log.e("123456--",  mCategoryMovieBeans.get(1) + "--" );
        PlayerContact.IView.Data playerData = new PlayerContact.IView.Data();
        playerData.setIndex(0);
        playerData.setMovies(mCategoryMovieBeans);
        playerData.setLoadType(PlayerContact.IView.Data.LOAD_TYPE_CATEGORY);
        playerData.setPage(0);
        playerData.setCategory("recommend");
        playerData.setHideMenu(false);
        markB = true;
       if(markA){
            getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit()
                    .putBoolean(KEY_SHOW_HUO_DONG_PIC, true)
                    .apply();
            startActivity(PlayerActivity.getStartIntent(this,playerData));
            finish();
        }






    }

    @Override
    public void onRequestMovieDetailSuccess(PlayerContact.IView.Data data) {
        isRequestMediaDetail = false;
        mPlayerData = data;
        if (!XWLauncherActivity.this.isFinishing() && isPermission && isFinish) {
            jump2Main();
        }
    }

    @Override
    public void onRequestMovieDetailFailed(Throwable throwable) {
        isFinish = true;
        mPlayerData = null;
        //请求视频详情失败，跳到首页
        if (!XWLauncherActivity.this.isFinishing() && isPermission) {
            jump2Main();
        }
    }

    private class MainPermissionListener implements PermissionManager.PermissionListener {

        @Override
        public void doAfterGrand(String[] permission, String tag) {
            if (TAG.equals(tag)) {

                isPermission = true;

                if (!XWLauncherActivity.this.isFinishing() && isFinish && !isRequestMediaDetail) {
                    jump2Main();
                }
            }
        }

        @Override
        public void doAfterDeniedCancel(String[] permission, String tag) {
            finish();
        }

        @Override
        public void doAfterDeniedEnsure(String[] permission, String tag) {
            //判断权限
            mPermissionManager.requestPermissions(XWLauncherActivity.this, mPermissionListener, new String[]{
                    android.Manifest.permission.READ_CONTACTS,
                    android.Manifest.permission.READ_PHONE_STATE,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_WIFI_STATE
            }, TAG, true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            setPermissions();
        }
    }

    private void jump2Main() {
        jump2Main(false, false);
    }

    private void jump2Main(boolean showDuoDong, boolean join) {
        getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit()
                .putBoolean(KEY_SHOW_HUO_DONG_PIC, true)
                .apply();
        //Intent intent = MainActivity.getStartIntent(this, true, /*显示活动*/showDuoDong,/*参与活动*/join);
        markA = true;
        if(markB){

            PlayerContact.IView.Data playerData = new PlayerContact.IView.Data();
            playerData.setIndex(0);
            if(networkState){
                playerData.setMovies(mCategoryMovieBeans);
            }else{
                playerData.setNetworkState(false);
             //   playerData.setMovies(null);
            }

            playerData.setLoadType(PlayerContact.IView.Data.LOAD_TYPE_CATEGORY);
            playerData.setPage(0);
            playerData.setCategory("recommend");
            playerData.setHideMenu(false);
            Intent intent =  PlayerActivity.getStartIntent(this,playerData);
            intent.putExtra(LIST_DATA, mCategoryMovieBeans);

            //从分享的视频进入视频播放页的数据
            intent.putExtra(SHARE_ACTION_OPEN_VIDEO, SHARE_ACTION_OPEN_VIDEO.equals(mAction));
            intent.putExtra(SHARE_PLAYER_DATA, mPlayerData);


            startActivity(intent);
            finish();
        }

    }

    private void onClickBtnSkip(View v) {
        isFinish = true;
        if (!XWLauncherActivity.this.isFinishing() && isPermission) {
            jump2Main();
        }
    }

    private void onClickJoin(View v) {
        isFinish = true;
        if (!XWLauncherActivity.this.isFinishing() && isPermission) {
            jump2Main(false, true);
        }
    }

    private void onClickLayoutHuoDong(View v) {
        isFinish = true;
        if (!XWLauncherActivity.this.isFinishing() && isPermission) {
            jump2Main(true, false);
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_skip:
                    onClickBtnSkip(v);
                    break;
                case R.id.btn_join:
                    onClickJoin(v);
                    break;
                case R.id.layout_huo_dong:
                    onClickLayoutHuoDong(v);
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public boolean isNum(String str) {
        String reg = "[0-9]+";
        return str.matches(reg);
    }

    ////////////20190531主页入口更换移植///////////////
    public void intMainData(){
        RxBus.get().register(this);
        if (UserInfoManager.isLogin()) {
            //判断是否登陆
            // 登录过，就拉取环信的信息
            Log.e("8888","@@@@@@@@");
            EMManager.INSTANCE.login(UserInfoManager.getHX_userName(), UserInfoManager.getHX_password(), 0);// 登录过的用户每次进入刷新环信
        }
    }

    private void showNetworkConnected(){
        mCustomProgress = new CustomProgress(XWLauncherActivity.this);
        mCustomProgress.show(getResources().getString(R.string.common_network_connect_no_dailog),
                getString(R.string.common_tips_title),
                getString(R.string.common_ensure),
                view -> {
                  XWLauncherActivity.this.finish();
//                        http://download.i-weiying.com/apk/release/ivy.apk
                    mCustomProgress.cancel();

                },
                false, null);
    }


}
