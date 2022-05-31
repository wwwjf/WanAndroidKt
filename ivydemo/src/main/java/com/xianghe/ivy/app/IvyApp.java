package com.xianghe.ivy.app;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;

import com.blankj.utilcode.util.Utils;
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager;
import com.xianghe.ivy.app.em.EMManager;
import com.xianghe.ivy.app.greendao.GreenDaoManager;
import com.xianghe.ivy.app.umeng.UmengManager;
import com.xianghe.ivy.model.ContactBean;
import com.xianghe.ivy.model.FriendBean;
import com.xianghe.ivy.network.IvyService;
import com.xianghe.ivy.network.RetrofitClient;
import com.xianghe.ivy.utils.ACache;
import com.xianghe.ivy.utils.KLog;
import com.xianghe.ivy.utils.LanguageUtil;
import com.xianghe.ivy.utils.SizeAdapterUtils;

import java.util.ArrayList;
import java.util.Locale;

import io.reactivex.plugins.RxJavaPlugins;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;


public class IvyApp extends Application implements Application.ActivityLifecycleCallbacks {

    private static IvyApp mInstance;

    private Handler mHandler;

    private IvyService ivyService;

    private LocationManager mLocationManager;


    public ArrayList<FriendBean> Contactslist = new ArrayList<>();//通讯录列表
    public ArrayList<ContactBean> mContactList = new ArrayList<>();

    private boolean isBackGround;

    /**
     * 注册邀请码
     */
    public String inviteCode = "";



    @Override
    public void onCreate() {
        super.onCreate();
//        initHotfix();
        registerParams();
        registerUmeng();
        registerActivityLifecycleCallbacks(this);
        Utils.init(this);
        registerEM();
        GreenDaoManager.getInstance().init();
        SizeAdapterUtils.INSTANCE.init(this);
        IjkPlayerManager.setLogLevel(IjkMediaPlayer.IJK_LOG_SILENT);
        RxJavaPlugins.setErrorHandler(e -> {
            KLog.e("----------", "Throwable:" + e.getMessage());
            e.printStackTrace();
        });

        //复制assets文件ic_launcher.png到SD卡中，分享时使用
        /*File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/ic_launcher.png");
        boolean exists = file.exists();
        if (!exists) {
            FileUtil.copyFilesFromAssets(this, "ic_launcher.png", file.getAbsolutePath());
        }*/
        final String accountID = "162848";
        ArrayList<String> hosts = new ArrayList<>();
        hosts.add("ivyoss.i-weiying.com");
        hosts.add("ivytest.i-weiying.com");

        String language = LanguageUtil.getLastSettingLanguage(this);
        if (language != null) {
            if ("zh".equals(language)) {
                LanguageUtil.changeLanguage(this, Locale.SIMPLIFIED_CHINESE);
            } else {
                LanguageUtil.changeLanguage(this, Locale.ENGLISH);
            }
        } else {
            // 使用系统默认语言
        }
    }


    public boolean isMainProcess() {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return getApplicationInfo().packageName.equals(appProcess.processName);
            }
        }
        return false;
    }

    private void registerEM() {
        EMManager.INSTANCE.init(this);
    }

    private void registerUmeng() {
        UmengManager.Companion.getInstance().init();
    }

    private void registerParams() {
        mInstance = this;
        mHandler = new Handler(Looper.getMainLooper());
        ivyService = RetrofitClient.getInstance().create(IvyService.class);
    }

    public static IvyApp getInstance() {
        return mInstance;
    }

    public IvyService getIvyService() {
        return ivyService;
    }

    public Handler getHandler() {
        return mHandler;
    }

    /**
     * 全局缓存接口数据
     *
     * @return
     */
    public ACache getACache() {
        return ACache.get(this);
    }

    private WindowManager.LayoutParams windowManagerParams = new WindowManager.LayoutParams();

    public WindowManager.LayoutParams getWindowManagerParams() {
        return windowManagerParams;
    }


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        //判断应用是否进入后台再到前台

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
