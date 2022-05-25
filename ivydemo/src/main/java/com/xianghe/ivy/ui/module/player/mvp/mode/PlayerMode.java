package com.xianghe.ivy.ui.module.player.mvp.mode;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.luckongo.tthd.net.mapper.RespMapper;
import com.xianghe.ivy.constant.Api;
import com.xianghe.ivy.http.request.NetworkRequest;
import com.xianghe.ivy.model.UserBean;
import com.xianghe.ivy.model.VersionBean;
import com.xianghe.ivy.model.response.BaseResponse;
import com.xianghe.ivy.model.response.ResponseIndexCategoryList;
import com.xianghe.ivy.network.GsonHelper;
import com.xianghe.ivy.ui.module.player.mvp.contact.PlayerContact;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

public class PlayerMode implements PlayerContact.IMode {
    @Override
    public Observable<ResponseIndexCategoryList> getCategoryList(String categoryType, int page) {
        Map<String, Object> params = new HashMap<>();
        params.put(Api.Key.CategoryType, categoryType);
        params.put(Api.Key.PAGE, page);
        return NetworkRequest.INSTANCE.postMap(Api.Route.Index.CATEGORY_LIST, params)
                .map(new Function<JsonElement, ResponseIndexCategoryList>() {
                    @Override
                    public ResponseIndexCategoryList apply(JsonElement jsonElement) throws Exception {
                        return GsonHelper.getsGson().fromJson(jsonElement, ResponseIndexCategoryList.class);
                    }
                });
    }

    @Override
    public Observable<ResponseIndexCategoryList> getUserMediaList(long uid, int page) {
        Map<String, Object> params = new HashMap<>();
        params.put(Api.Key.UID, uid);
        params.put(Api.Key.PAGE, page);
        return NetworkRequest.INSTANCE.postMap(Api.Route.User.MEDIALIST, params)
                .map(new Function<JsonElement, ResponseIndexCategoryList>() {
                    @Override
                    public ResponseIndexCategoryList apply(JsonElement jsonElement) throws Exception {
                        return GsonHelper.getsGson().fromJson(jsonElement, ResponseIndexCategoryList.class);
                    }
                });
    }

    @Override
    public Observable<ResponseIndexCategoryList> getCollectMediaList(int page) {
        Map<String, Object> params = new HashMap<>();
        params.put(Api.Key.PAGE, page);
        return NetworkRequest.INSTANCE.postMap(Api.Route.User.COLLECTIONLIST, params)
                .map(new Function<JsonElement, ResponseIndexCategoryList>() {
                    @Override
                    public ResponseIndexCategoryList apply(JsonElement jsonElement) throws Exception {
                        return GsonHelper.getsGson().fromJson(jsonElement, ResponseIndexCategoryList.class);
                    }
                });
    }

    @Override
    public Observable<BaseResponse> addFriend(long uid) {
        Map<String, Object> params = new HashMap<>();
        params.put(Api.Key.FRIEND_ID, uid);
        return NetworkRequest.INSTANCE.postMap(Api.Route.User.FOLLOWUSER, params)
                .map(new Function<JsonElement, BaseResponse>() {
                    @Override
                    public BaseResponse apply(JsonElement jsonElement) throws Exception {
                        return GsonHelper.getsGson().fromJson(jsonElement, BaseResponse.class);
                    }
                });
    }

    @Override
    public Observable<BaseResponse<UserBean>> getUserInfo() {
        return NetworkRequest.INSTANCE.postMapByJAVA(Api.Route.User.USERINFO,
                new HashMap<>(),
                new RespMapper(),
                new TypeToken<BaseResponse<UserBean>>() {
                }.getType());
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
    public Observable<BaseResponse<UserBean>> requestData(long uid) {
        Map<String, Object> map = new HashMap<>();
        map.put("uid", uid);
        return NetworkRequest.INSTANCE.postMapByJAVA(
                Api.Route.User.HOMEPAGE,
                map,
                new RespMapper(),
                new TypeToken<BaseResponse<UserBean>>() {
                }.getType());
    }


}
