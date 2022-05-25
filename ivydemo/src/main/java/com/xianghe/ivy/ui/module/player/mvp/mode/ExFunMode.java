package com.xianghe.ivy.ui.module.player.mvp.mode;


import com.google.gson.JsonElement;
import com.xianghe.ivy.constant.Api;
import com.xianghe.ivy.http.request.NetworkRequest;
import com.xianghe.ivy.model.response.BaseResponse;
import com.xianghe.ivy.network.GsonHelper;
import com.xianghe.ivy.ui.module.player.mvp.contact.ExFunContact;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

public class ExFunMode implements ExFunContact.IMode {
    private static final String TAG = "ExFunMode";

    /**
     * 收藏
     *
     * @param mediaId mediaId
     */
    @Override
    public Observable<BaseResponse> collection(long mediaId) {
        Map<String, Object> params = new HashMap<>();
        params.put(Api.Key.MEDIA_ID, mediaId);

        return NetworkRequest.INSTANCE.postMap(Api.Route.Mine.Collection.MEDIA, params)
                .map(new Function<JsonElement, BaseResponse>() {
                    @Override
                    public BaseResponse apply(JsonElement jsonElement) throws Exception {
                        return GsonHelper.getsGson().fromJson(jsonElement, BaseResponse.class);
                    }
                });
    }

    /**
     * 取消收藏
     *
     * @param mediaId mediaId
     */
    @Override
    public Observable<BaseResponse> collectionCancel(long mediaId) {
        Map<String, Object> params = new HashMap<>();
        params.put(Api.Key.MEDIA_ID, mediaId);

        return NetworkRequest.INSTANCE.postMap(Api.Route.Mine.Collection.DEL_MEDIA, params)
                .map(new Function<JsonElement, BaseResponse>() {
                    @Override
                    public BaseResponse apply(JsonElement jsonElement) throws Exception {
                        return GsonHelper.getsGson().fromJson(jsonElement, BaseResponse.class);
                    }
                });
    }

    /**
     * 点赞
     *
     * @param mediaId mediaId
     */
    @Override
    public Observable<BaseResponse> praise(long mediaId) {
        Map<String, Object> params = new HashMap<>();
        params.put(Api.Key.MEDIA_ID, mediaId);

        return NetworkRequest.INSTANCE.postMap(Api.Route.Index.MEDIA_PRAISE, params)
                .map(new Function<JsonElement, BaseResponse>() {
                    @Override
                    public BaseResponse apply(JsonElement jsonElement) throws Exception {
                        return GsonHelper.getsGson().fromJson(jsonElement, BaseResponse.class);
                    }
                });
    }

    /**
     * 取消点赞
     *
     * @param mediaId mediaId
     */
    @Override
    public Observable<BaseResponse> praiseCancel(long mediaId) {
        Map<String, Object> params = new HashMap<>();
        params.put(Api.Key.MEDIA_ID, mediaId);

        return NetworkRequest.INSTANCE.postMap(Api.Route.Index.MEDIA_CANCEL_PRAISE, params)
                .map(new Function<JsonElement, BaseResponse>() {
                    @Override
                    public BaseResponse apply(JsonElement jsonElement) throws Exception {
                        return GsonHelper.getsGson().fromJson(jsonElement, BaseResponse.class);
                    }
                });
    }
}
