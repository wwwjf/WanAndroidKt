package com.xianghe.ivy.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.provider.ContactsContract;
import android.view.WindowManager;

import com.blankj.utilcode.util.Utils;
import com.google.gson.JsonElement;
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager;
import com.xianghe.ivy.app.em.EMManager;
import com.xianghe.ivy.app.greendao.GreenDaoManager;
import com.xianghe.ivy.app.umeng.UmengManager;
import com.xianghe.ivy.constant.Api;
import com.xianghe.ivy.http.request.NetworkRequest;
import com.xianghe.ivy.manager.UserInfoManager;
import com.xianghe.ivy.model.ContactBean;
import com.xianghe.ivy.model.FriendBean;
import com.xianghe.ivy.model.response.BaseResponse;
import com.xianghe.ivy.model.response.GetTokenResponse;
import com.xianghe.ivy.model.response.ResponseIndexCategoryList;
import com.xianghe.ivy.network.GsonHelper;
import com.xianghe.ivy.network.IvyService;
import com.xianghe.ivy.network.RetrofitClient;
import com.xianghe.ivy.utils.ACache;
import com.xianghe.ivy.utils.KLog;
import com.xianghe.ivy.utils.LanguageUtil;
import com.xianghe.ivy.utils.SizeAdapterUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
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


    @SuppressLint("CheckResult")
    public void getToken() {
        Map<String, Object> params = new HashMap<>();
        NetworkRequest.INSTANCE.postMap(Api.Route.Index.INDEX_GETTOKEN, params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(jsonElement -> {
                    //判断是否在后台了
                    if (isBackGround) {
                        stopTimeHandler();
                    } else {
                        GetTokenResponse response = GsonHelper.getsGson().fromJson(jsonElement, GetTokenResponse.class);
                        if (response != null && response.getStatus() == BaseResponse.Status.OK) {
//                            expires_Time = response.getData()
                            UserInfoManager.saveOSSToken(response.getData());
                            setTimeHandlerDelayed(response.getData().getExpires_in() * 1000);

//                            OssUrl.init(getApplicationContext());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        setTimeHandlerDelayed(10 * 1000);
                    }
                });
    }

    public void upLoadPushPlace(HashMap<String, Object> params) {

        NetworkRequest.INSTANCE.postMap(Api.Route.Index.INDEX_PUSHPLACE, params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JsonElement>() {
                    @Override
                    public void accept(JsonElement jsonElement) throws Exception {
                        ResponseIndexCategoryList response = GsonHelper.getsGson().fromJson(jsonElement, ResponseIndexCategoryList.class);

                        if (response != null && response.getStatus() == BaseResponse.Status.OK) {

                        } else {

                        }
                    }
                });
    }

    private void initLocation() {
        //判断语言

    }

    private Handler timeHandler = new Handler();
    private Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            //要做的事情
            KLog.e("定时刷新");
            getToken();
        }
    };

    public void setTimeHandlerDelayed(long delayTime) {
        KLog.i("delayTime: " + delayTime);
        timeHandler.removeCallbacks(timeRunnable);
        timeHandler.postDelayed(timeRunnable, delayTime);//根据失效执行一次runnable.
    }

    public void stopTimeHandler() {
        if (timeHandler != null && timeRunnable != null) {
            timeHandler.removeCallbacks(timeRunnable);
        }
    }

    public void readContacts() {
        Contactslist.clear();
        mContactList.clear();
        Cursor cursor = null;
        try {
            //cursor指针 query询问 contract协议 kinds种类
            cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            if (cursor != null) {

                while (cursor.moveToNext()) {
                    String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll(" ", "");

                    FriendBean bean = new FriendBean();
                    bean.setName(displayName);
                    bean.setMobile(number);
                    Contactslist.add(bean);
                    ContactBean contactBean = new ContactBean();
                    contactBean.setContactName(displayName);
                    contactBean.setContactPhone(number);
                    mContactList.add(contactBean);
                }
                KLog.i("读取通讯录成功" + cursor.getPosition());
            }
        } catch (Exception e) {
            e.printStackTrace();
            KLog.i("读取通讯录失败" + e.getLocalizedMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void getLocationAndContacts() {
        initLocation();
        new Thread(() -> {
            KLog.i("读取通讯录");
            readContacts();
        }).start();
        //获取通讯录地址
    }

    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    //判断应用是否在后台
    public boolean isRunningForeground() {
        ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            List<ActivityManager.RunningAppProcessInfo> appProcessInfos = activityManager.getRunningAppProcesses();
            // 枚举进程
            for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessInfos) {
                if (appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    if (appProcessInfo.processName.equals(this.getApplicationInfo().processName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        String language = LanguageUtil.getLastSettingLanguage(activity);
        if (language != null) {
            if ("zh".equals(language)) {
                LanguageUtil.changeLanguage(activity, Locale.SIMPLIFIED_CHINESE);
            } else {
                LanguageUtil.changeLanguage(activity, Locale.ENGLISH);
            }
        } else {
            // 使用系统默认语言
        }

        if (!activity.getClass().getSimpleName().equalsIgnoreCase("XWLauncherActivity")) {
            isBackGround = false;
            //请求token
            getToken();
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
        //判断应用是否进入后台再到前台
        if (isBackGround) {
            //请求token
            getToken();
            isBackGround = false;
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        //判断应用是否都在后台了
        if (!isRunningForeground()) {
            isBackGround = true;
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
