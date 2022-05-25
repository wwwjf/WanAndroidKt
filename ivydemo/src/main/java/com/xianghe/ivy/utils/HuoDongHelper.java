package com.xianghe.ivy.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.xianghe.ivy.constant.Api;
import com.xianghe.ivy.http.request.NetworkRequest;
import com.xianghe.ivy.model.HuoDongInfoBean;
import com.xianghe.ivy.model.HuoDongListBean;
import com.xianghe.ivy.model.response.BaseResponse;
import com.xianghe.ivy.network.GsonHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class HuoDongHelper {
    private static final String TAG = "HuoDongHelper";
    private static final String SP_NAME = TAG;
    private static final String KEY_HUO_DONGS = "key_huo_dongs";
    private static final long MIN_LOAD_DURATION = 30 * 60 * 1000;   // 30 分钟

    private volatile static HuoDongHelper mInstance = null;
    private final Context mContext;
    private final Gson mGson;

    private long mLastSuccessLoad = 0;

    public static HuoDongHelper get(Context context) {
        if (mInstance == null) {
            synchronized (HuoDongHelper.class) {
                if (mInstance == null) {
                    mInstance = new HuoDongHelper(context);
                }
            }
        }
        return mInstance;
    }

    private HuoDongHelper(Context context) {
        mContext = context.getApplicationContext();
        mGson = new Gson();
    }

    private void saveHuoDongs(List<HuoDongInfoBean> huoDongs) {
        String json = mGson.toJson(huoDongs);
        mContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit()
                .putString(KEY_HUO_DONGS, json)
                .apply();
    }


    @Nullable
    public List<HuoDongInfoBean> getHuoDongs() {
        loadHuoDongsFormNetIfNeed();
        String json = mContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
                .getString(KEY_HUO_DONGS, null);

        if (!TextUtils.isEmpty(json)) {
            return mGson.fromJson(json, new TypeToken<List<HuoDongInfoBean>>() {
            }.getType());
        } else {
            return null;
        }
    }

    @SuppressLint("CheckResult")
    private void loadHuoDongsFormNetIfNeed() {
//        long diffDuration = System.currentTimeMillis() - mLastSuccessLoad;
//        if (diffDuration < MIN_LOAD_DURATION) {
//            KLog.i(TAG, "小于最小有效时间, 不向服务器请求.");
//            return;
//        }

        Map<String, Object> params = new HashMap<>();
        NetworkRequest.INSTANCE.postMap(Api.Route.Index.ACTIVITY_INDEX, params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<JsonElement, BaseResponse<HuoDongListBean>>() {
                    @Override
                    public BaseResponse<HuoDongListBean> apply(JsonElement jsonElement) throws Exception {
                        return GsonHelper.getsGson().fromJson(jsonElement, new TypeToken<BaseResponse<HuoDongListBean>>() {
                        }.getType());
                    }
                })
                .subscribe(new Consumer<BaseResponse<HuoDongListBean>>() {
                    @Override
                    public void accept(BaseResponse<HuoDongListBean> response) throws Exception {
                        if (response.getStatus() == BaseResponse.Status.OK) {
                            try {
                                saveHuoDongs(response.getData().getList());
                                mLastSuccessLoad = System.currentTimeMillis();
                            } catch (Exception e) {
                                e.printStackTrace();
                                // 调用层级太多了，用 try catch 处理 空指针.
                            }
                        }
                    }
                });
    }
}
