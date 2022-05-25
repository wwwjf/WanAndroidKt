package com.xianghe.ivy.network.interceptor;

import android.text.TextUtils;

import com.blankj.utilcode.util.AppUtils;
import com.xianghe.ivy.app.IvyApp;
import com.xianghe.ivy.app.IvyConstants;
import com.xianghe.ivy.constant.Api;
import com.xianghe.ivy.manager.UserInfoManager;
import com.xianghe.ivy.utils.DeviceUtils;
import com.xianghe.ivy.utils.IvyUtils;
import com.xianghe.ivy.utils.KLog;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 添加接口公共参数
 */
public class LoadParamsInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        HttpUrl originalHttpUrl = originalRequest.url();
        HttpUrl.Builder builder = originalHttpUrl.newBuilder();

        String sign = IvyConstants.signValue;
        //用户token
        if (UserInfoManager.isLogin()) {
            builder.addQueryParameter("ticket", UserInfoManager.getTicket());
        }
        //Android客户端
        builder.addQueryParameter(Api.Key.Global.CLIENT_SOURCE, String.valueOf(Api.Value.ClientSource.ANDROID));
        //客户端系统版本
        builder.addQueryParameter(Api.Key.Global.CLIENT_SYSTEM, IvyUtils.getClientSystem());
//        builder.addQueryParameter(Api.Key.Global.CLIENT_SYSTEM, "Android");

        //设备Id
        builder.addQueryParameter(Api.Key.Global.MOBILE_UNIQUE_CODE, DeviceUtils.getDeviceInfo(IvyApp.getInstance().getApplicationContext()));
        //APP版本
        builder.addQueryParameter(Api.Key.Global.VERSION, AppUtils.getAppVersionName());
        KLog.e("params="+builder.build().query());
        //签名为空，生成签名
        StringBuilder stringBuilder = new StringBuilder();
        if (TextUtils.isEmpty(sign)) {
            Set<String> parameterNames = builder.build().queryParameterNames();
            builder.build();
            TreeMap<String, String> treeMap = new TreeMap<>();
            for (String parameterName : parameterNames) {
                List<String> parameterValues = builder.build().queryParameterValues(parameterName);
                for (String parameterValue : parameterValues) {
                    treeMap.put(parameterName, parameterValue);
                }
            }
            Iterator<Map.Entry<String, String>> it = treeMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> entry = it.next();
                stringBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
//            KLog.e("stringBuilder="+stringBuilder.substring(0,stringBuilder.length()-1));
            sign = IvyUtils.getSign(stringBuilder.substring(0,stringBuilder.length()-1));
//            KLog.e(, "intercept: sign=" + sign);
        }
        //请求数据签名
        builder.addQueryParameter("sign", sign);
        Request request = originalRequest.newBuilder()
                .url(builder.build())
                .method(originalRequest.method(), originalRequest.body())
                .build();

        return chain.proceed(request);
    }
}
