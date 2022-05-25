package com.xianghe.ivy.network;

import com.google.gson.reflect.TypeToken;
import com.xianghe.ivy.app.IvyApp;
import com.xianghe.ivy.model.response.BaseResponse;
import com.xianghe.ivy.utils.KLog;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;

/**
 * 请求处理的通用模块
 */
public final class ServiceHelper {

    private static final String TAG = ServiceHelper.class.getSimpleName();

    private ServiceHelper() {
        throw new RuntimeException("ServiceHelper cannot be initialized!");
    }


    // 老代码的重新封装优化，仅提供一个对外的访问即可
    public static <T> void callObject(String url, HashMap<String, Object> params,
                                      OnRequestListener<T> listener) {
        Call<BaseResponse> call = IvyApp.getInstance().getIvyService().postMapMVC(url, params);
        callObject(call, listener);
    }

    public static <T> void callEntity(String url, HashMap<String, Object> params,
                                       Class<T> entityClass,
                                       OnRequestListener<T> listener) {
        Call<BaseResponse> call = IvyApp.getInstance().getIvyService().postMapMVC(url, params);
        callEntity(call, entityClass, listener);
    }

    private static <T> void callEntities(String url, HashMap<String, Object> params,
                                         Class<T> entityClass,
                                         OnRequestListener<List<T>> listener) {
        Call<BaseResponse> call = IvyApp.getInstance().getIvyService().postMapMVC(url, params);
        callEntities(call, entityClass, listener);
    }


    /**
     * 获取单个实体的处理操作
     *
     * @param call        请求
     * @param entityClass 实体类型
     * @param listener    监听器
     */
    private static <T> void callEntity(Call<BaseResponse> call, final Class<T> entityClass,
                                       final OnRequestListener<T> listener) {
        call.enqueue(new RequestCallBack() {
            @Override
            protected void onDataObtain(String jsonData) {
                KLog.d("onResponse: " + jsonData);
                T info = GsonHelper.convertEntity(jsonData, entityClass);
                if (info == null) {//{"ticket":"504BC5E6C-CE89-FECD-5C39-9B67033F0FB1","uid":"8272564","openid":"oIICa1aPy-RkF_fKDhs4YpKlnub4"}
                    if (listener != null) {
                        KLog.d("onFailure: object parse failed");
                        listener.onFailure("object parse failed");
                    }
                } else {
                    if (listener != null) {
                        listener.onResponse(info);
                    }
                }
            }

            @Override
            protected void onError(String errMsg) {
                KLog.d("onFailure: " + errMsg);
                if (listener != null) {
                    listener.onFailure(errMsg);
                }
            }
        });
    }

    /**
     * 获取复数实体的处理操作
     *
     * @param call        请求
     * @param entityClass 实体类型
     * @param listener    监听器
     */
    private static <T> void callEntities(Call<BaseResponse> call, final Class<T> entityClass,
                                         final OnRequestListener<List<T>> listener) {
        call.enqueue(new RequestCallBack() {
            @Override
            protected void onDataObtain(String jsonData) {
                KLog.d("onResponse: " + jsonData);
                List<T> infos = GsonHelper.convertEntities(jsonData, entityClass);
                if (infos == null) {
                    if (listener != null) {
                        KLog.d("onFailure: object parse null");
                        listener.onFailure("object parse null");
                    }
                } else {
                    if (listener != null) {
                        listener.onResponse(infos);
                    }
                }
            }

            @Override
            protected void onError(String errMsg) {
                KLog.d("onFailure: " + errMsg);
                if (listener != null) {
                    listener.onFailure(errMsg);
                }
            }
        });
    }

    private static <T> void callObject(Call<BaseResponse> call, final OnRequestListener<T> listener) {
        call.enqueue(new RequestCallBack() {
            @Override
            protected void onDataObtain(String jsonData) {
                KLog.d("onResponse: " + jsonData);
                T info = GsonHelper.getsGson().fromJson(jsonData, new TypeToken<T>() {}.getType());
                if (info == null) {//{"ticket":"504BC5E6C-CE89-FECD-5C39-9B67033F0FB1","uid":"8272564","openid":"oIICa1aPy-RkF_fKDhs4YpKlnub4"}
                    if (listener != null) {
                        KLog.d("onFailure: object parse null");
                        listener.onFailure("object parse null");
                    }
                } else {
                    if (listener != null) {
                        listener.onResponse(info);
                    }
                }
            }

            @Override
            protected void onError(String errMsg) {
                KLog.d("onFailure: " + errMsg);
                if (listener != null) {
                    listener.onFailure(errMsg);
                }
            }
        });
    }
}

