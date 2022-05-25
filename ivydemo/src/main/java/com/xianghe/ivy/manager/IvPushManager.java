package com.xianghe.ivy.manager;

import static com.xianghe.ivy.app.IvyConstants.COMMONSIGNVALUE;
import static com.xianghe.ivy.constant.Api.Push.PUSHURL;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Handler;

import com.blankj.utilcode.util.AppUtils;
import com.xianghe.ivy.app.IvyApp;
import com.xianghe.ivy.constant.Api;
import com.xianghe.ivy.network.IvyService;
import com.xianghe.ivy.utils.DeviceUtils;
import com.xianghe.ivy.utils.IvyUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class IvPushManager {

    private Handler mHandler;

    private Context mContext;

    private PushTask mPushTask;

    public IvPushManager(Context context){
        mContext = context.getApplicationContext();
        mHandler = new Handler();
    }


    public void setTagAndCommit(){
        //判断语言
        String deviceId = getDeviceId();
        int pushType;

    }

    public void setTagAndLogin(){
        //判断语言
        //判断是否登录
    }

    public String getTag(){
        //判断是否登录
        String deviceId = getDeviceId();

        return deviceId;
    }

    @SuppressLint("CheckResult")
    public void commitTag(String deviceId,int pushType){
        //请求服务器接口
        IvyService ivyService = IvyApp.getInstance().getIvyService();
        String unique_code = getDeviceId();
        Map<String,Object> params = new HashMap<>();
        params.put("ticket",UserInfoManager.getTicket());
        //Android客户端
        params.put(Api.Key.Global.CLIENT_SOURCE, Api.Value.ClientSource.ANDROID+"");
        //客户端系统版本
        params.put(Api.Key.Global.CLIENT_SYSTEM, IvyUtils.getClientSystem());
        //APP版本
        params.put(Api.Key.Global.VERSION, AppUtils.getAppVersionName());
        params.put("registration_id",deviceId);
        params.put("mobile_unique_code",unique_code);
        params.put("push_channel",pushType);
        params.put("sign",COMMONSIGNVALUE);
        if (mPushTask == null){
            mPushTask = new PushTask(ivyService,params,deviceId,pushType);
        }
        mPushTask.run();
    }

    @SuppressLint("CheckResult")
    private void requestPushWay(IvyService ivyService, String deviceId,int pushType,Map<String,Object> params){
        ivyService.postPushWay(PUSHURL,params).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(pushBean -> {
            if (mPushTask != null){
                mPushTask.stop();
            }
        }, throwable -> {
            if (mPushTask == null){
                mPushTask = new PushTask(ivyService,params,deviceId,pushType);
            }
            mPushTask.start();
        });
    }

    @SuppressLint({"MissingPermission", "HardwareIds"})
    private String getDeviceId(){
        return DeviceUtils.getDeviceInfo(IvyApp.getInstance().getApplicationContext());
    }

    private class PushTask implements Runnable{

        private String deviceId;

        private int pushType;

        private IvyService ivyService;

        private Map<String,Object> params;

        public PushTask(IvyService ivyService,Map<String,Object> params,String deviceId,int pushType){
            this.deviceId = deviceId;
            this.pushType = pushType;
            this.ivyService = ivyService;
            this.params = params;
        }

        @Override
        public void run() {
            requestPushWay(ivyService,deviceId,pushType,params);
        }

        public void start(){
            mHandler.postDelayed(this,10*1000);
        }

        public void stop(){
            mHandler.removeCallbacksAndMessages(null);
        }
    }
}
