package com.xianghe.ivy.ui.module.welcom;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.luckongo.tthd.net.mapper.RespMapper;
import com.xianghe.ivy.constant.Api;
import com.xianghe.ivy.http.request.NetworkRequest;
import com.xianghe.ivy.model.MediaDetailBean;
import com.xianghe.ivy.model.response.BaseResponse;
import com.xianghe.ivy.model.response.ResponseIndexCategoryList;
import com.xianghe.ivy.network.GsonHelper;
import com.xianghe.ivy.utils.KLog;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

public class XWLauncherModel implements XWLauncherContact.Model {
    private static final String TAG = "XWLauncherModel";

    @Override
    public Observable<ResponseIndexCategoryList> getCategoryList(String category, int page) {
        Map<String, Object> params = new HashMap<>();
        params.put(Api.Key.CategoryType, category);
        params.put(Api.Key.PAGE, page);
//        return NetworkRequest.INSTANCE.postMap(Api.Route.Index.CATEGORY_LIST, params)
//                .map(jsonElement -> GsonHelper.getsGson().fromJson(jsonElement, ResponseIndexCategoryList.class));
        return NetworkRequest.INSTANCE.postMap(Api.Route.Index.CATEGORY_LIST, params)
                .map(new Function<JsonElement, ResponseIndexCategoryList>() {
                    @Override
                    public ResponseIndexCategoryList apply(JsonElement jsonElement) throws Exception {
                        KLog.d(TAG, jsonElement + "");
                        return GsonHelper.getsGson().fromJson(jsonElement, ResponseIndexCategoryList.class);
                    }
                });
    }

    @Override
    public Observable<BaseResponse<MediaDetailBean>> getMediaDetail(int mediaId) {
        Map<String, Object> params = new HashMap<>();
        params.put(Api.Key.MEDIA_ID, mediaId);
        params.put(Api.Key.ACTION_TYPE, Api.Value.ActionType.BACKWARD);
        return NetworkRequest.INSTANCE.postMapByJAVA(Api.Route.Index.MEDIA_MEDIA_DETAIL,
                params, new RespMapper(),
                new TypeToken<BaseResponse<MediaDetailBean>>() {
                }.getType());
    }
}
