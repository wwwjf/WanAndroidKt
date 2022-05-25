package com.xianghe.ivy.network;

import com.blankj.utilcode.util.ActivityUtils;
import com.xianghe.ivy.manager.UserInfoManager;
import com.xianghe.ivy.model.response.BaseResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 对CallBack进行封装
 */
public abstract class RequestCallBack implements Callback<BaseResponse> {

    private static final String TAG = RequestCallBack.class.getSimpleName();

    // 请求成功是的状态值
    private static final int RESPONSE_SUCC = 1;
    // 请求失败是的状态值
    private static final int RESPONSE_FAIL = 0;
    // 登录过期
    private static final int RESPONSE_EXPIRED = 1000;

    @Override
    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
        BaseResponse body = response.body();
        if (response.code() != 200){
            onError("errorCode:"+response.code());
        } else {
            handleResponse(body);
        }
    }

    @Override
    public void onFailure(Call<BaseResponse> call, Throwable t) {
        t.printStackTrace();
        onError("network error");
    }

    /**
     * 处理应答
     * @param response 应答实体
     */
    private void handleResponse(BaseResponse response) {
        try {
            if (response == null){
                onError("response is null");
            } else if (response.getStatus() == -1){
                onError("response status  is null");
            } else if (response.getStatus()==RESPONSE_FAIL){
                onError(response.getInfo());
            } else if (response.getStatus()==RESPONSE_SUCC) {
                // 请求成功才处理数据
                onDataObtain(GsonHelper.object2JsonStr(response.getData()));
            } else if (response.getStatus()==RESPONSE_EXPIRED){
                //登录过期，清除登录信息
                UserInfoManager.exitUser();
//                ActivityUtils.startActivity(LoginActivity.class);
                onError(response.getInfo());
//                EventBus.getDefault().post(new MessageEvent("exitUser"));
            } else {
                onError("new status value");
            }
        } catch (Exception e) {
            if (e.getMessage() == null) {
                onError("thread exiting with uncaught exception");
            } else {
                onError(e.getMessage());
            }
        }
    }

    /**
     * 获取json数据
     * @param jsonData json字符串
     * @return
     */
    protected abstract void onDataObtain(String jsonData);

    /**
     * 获取错误数据
     * @param errMsg 错误数据
     */
    protected abstract void onError(String errMsg);
}

