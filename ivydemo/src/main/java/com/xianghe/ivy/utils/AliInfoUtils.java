package com.xianghe.ivy.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.xianghe.ivy.R;
import com.xianghe.ivy.app.IvyApp;

import java.util.Map;

/**
 * @创建者 Allen
 * @创建时间 2019/4/25 16:10
 * @描述      支付宝登录获取用户信息工具类
 */
public class AliInfoUtils {
    public static final int SDK_AUTH_FLAG = 2233;

    private static AliInfoUtils instance;
    public static Context  mContext;
    private AliInfoHandler mHandler;

    private AliInfoUtils() {
    }


    /**
     * 检测是否安装支付宝
     * @param context
     * @return
     */
    public static boolean checkAliPayInstalled(Context context) {

        Uri uri = Uri.parse("alipays://platformapi/startApp");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        ComponentName componentName = intent.resolveActivity(context.getPackageManager());
        return componentName != null;
    }

    public synchronized static AliInfoUtils getInstance(Context context) {
        if (instance == null) {
            instance = new AliInfoUtils();
        }
        mContext = context;
        return instance;
    }

    /**
     * 支付宝账户授权业务示例
     */
    public void authV2(Activity activity) {
        if (mHandler == null) {
            mHandler = new AliInfoHandler();
        }
        String millis = String.valueOf(System.currentTimeMillis());
        if (TextUtils.isEmpty(IvyAppID.ALI_PID) || TextUtils.isEmpty(IvyAppID.ALI_APPID)
                || (TextUtils.isEmpty(IvyAppID.RSA2_PRAVITE_KEY))
                || TextUtils.isEmpty(millis)) {
            ToastUtil.showToast(activity, activity.getString(R.string.error_auth_missing_partner_appid_rsa_private_target_id));
            return;
        }

        /*
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * authInfo 的获取必须来自服务端；
         */
        boolean rsa2 = (IvyAppID.RSA2_PRAVITE_KEY.length() > 0);
        Map<String, String> authInfoMap = OrderInfoUtil2_0.buildAuthInfoMap(IvyAppID.ALI_PID, IvyAppID.ALI_APPID, millis, rsa2);
        String info = OrderInfoUtil2_0.buildOrderParam(authInfoMap);

        String privateKey = IvyAppID.RSA2_PRAVITE_KEY;
        String sign = OrderInfoUtil2_0.getSign(authInfoMap, privateKey, rsa2);
        final String authInfo = info + "&" + sign;

    }

    private static class AliInfoHandler extends Handler {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_AUTH_FLAG: {
                    @SuppressWarnings("unchecked")
                    AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
                    String resultStatus = authResult.getResultStatus();
                    // 判断resultStatus 为“9000”且result_code
                    // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
                    if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
                        // 获取alipay_open_id，调支付时作为参数extern_token 的value
                        // 传入，则支付账户为该授权账户
                        String authCode = authResult.getAuthCode();
                        mAuthListener.authSuccessListener(authCode, authResult.getUserId());
                        ToastUtil.showToast(IvyApp.getInstance(), IvyApp.getInstance().getApplicationContext().getString(R.string.auth_success));
                    } else {
                        // 其他状态值则为授权失败
                        ToastUtil.showToast(IvyApp.getInstance(), IvyApp.getInstance().getApplicationContext().getString(R.string.auth_failed));
                    }
                    break;
                }
            }
        }
    }

    public void cloesHandler(){
        if (mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }

    private static AuthListener mAuthListener;
    public void setAuthListener(AuthListener listener){
        mAuthListener = listener;
    }
    /**
     * 定义一个授权后的接口
     */
    public interface AuthListener{
        void authSuccessListener(String authCode, String userId);
    }
}
