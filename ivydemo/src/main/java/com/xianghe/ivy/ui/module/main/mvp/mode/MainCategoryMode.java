package com.xianghe.ivy.ui.module.main.mvp.mode;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.luckongo.tthd.net.mapper.RespMapper;
import com.xianghe.ivy.constant.Api;
import com.xianghe.ivy.http.request.NetworkRequest;
import com.xianghe.ivy.model.UserBean;
import com.xianghe.ivy.model.VersionBean;
import com.xianghe.ivy.model.response.BaseResponse;
import com.xianghe.ivy.model.response.ResponseIndexCategoryList;
import com.xianghe.ivy.model.response.ResponseIndexShareQrCode;
import com.xianghe.ivy.network.GsonHelper;
import com.xianghe.ivy.ui.module.main.mvp.MainContact;
import com.xianghe.ivy.utils.KLog;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.functions.Function;


public class MainCategoryMode implements MainContact.IMode {
    private static final String TAG = "MainCategoryMode";

    @Override
    public Observable<ResponseIndexCategoryList> getCategoryList(String category, int page) {
        Map<String, Object> params = new HashMap<>();
        params.put(Api.Key.CategoryType, category);
        params.put(Api.Key.PAGE, page);
        return NetworkRequest.INSTANCE.postMap(Api.Route.Index.CATEGORY_LIST, params)
                .map(new Function<JsonElement, ResponseIndexCategoryList>() {
                    @Override
                    public ResponseIndexCategoryList apply(JsonElement jsonElement) throws Exception {
                        KLog.d(TAG, "category = " + category + ", page = " + page + ", jsonElement = " + jsonElement);
                        return GsonHelper.getsGson().fromJson(jsonElement, ResponseIndexCategoryList.class);
                    }
                });
    }

    @Override
    public Observable<BaseResponse<VersionBean>> checkVersion() {

        return NetworkRequest.INSTANCE.postMapByJAVA(Api.Route.Index.INDEX_VERSION_CHECK,
                new HashMap<>(),
                new RespMapper(),
                new TypeToken<BaseResponse<VersionBean>>() {
                }.getType());
    }

    @Override
    public Observable<ResponseIndexShareQrCode> getShareQrCode() {
        Map<String, Object> params = new HashMap<>();
        return NetworkRequest.INSTANCE.postMap(Api.Route.Index.SHARE_QRCODE, params)
                .map(new Function<JsonElement, ResponseIndexShareQrCode>() {
                    @Override
                    public ResponseIndexShareQrCode apply(JsonElement jsonElement) throws Exception {
                        KLog.d(TAG, "json = " + jsonElement);
                        return GsonHelper.getsGson().fromJson(jsonElement, ResponseIndexShareQrCode.class);
                    }
                });
    }

    @Override
    public Observable<BaseResponse<UserBean>> getUserInfo() {
        return NetworkRequest.INSTANCE.postMapByJAVA(Api.Route.User.USERINFO,
                new HashMap<>(),
                new RespMapper(),
                new TypeToken<BaseResponse<UserBean>>() {}.getType());
    }

    @Override
    public Observable<BaseResponse<UserBean>> getUserHomePage(long uid) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("uid",uid);
        return NetworkRequest.INSTANCE.postMapByJAVA(
                Api.Route.User.HOMEPAGE,
                params,
                new RespMapper(),
                new TypeToken<BaseResponse<UserBean>>() {}.getType());
    }
}
