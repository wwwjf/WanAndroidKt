package com.xianghe.ivy.ui.module.user;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.luckongo.tthd.net.mapper.RespMapper;
import com.xianghe.ivy.constant.Api;
import com.xianghe.ivy.http.request.NetworkRequest;
import com.xianghe.ivy.model.MediaReportTypeBean;
import com.xianghe.ivy.model.UserBean;
import com.xianghe.ivy.model.response.BaseResponse;
import com.xianghe.ivy.model.response.MediaReportTypeResponse;
import com.xianghe.ivy.model.response.ResponseIndexCategoryList;
import com.xianghe.ivy.network.GsonHelper;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

/**
 * @Author: ycl
 * @Date: 2018/11/6 12:58
 * @Desc:
 */
public class UserModel implements UserContract.Model {

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

    @Override
    public Observable<BaseResponse<String>> reportUser(long uid, int reportType, String content, int type) {
        Map<String, Object> params = new HashMap<>();
        params.put("report_obj_id", uid);
        params.put("report_type", reportType);
        params.put("content", content);
        params.put("type", type);
        return NetworkRequest.INSTANCE.postMapByJAVA(
                Api.Route.User.USERREPORT,
                params, new RespMapper(),
                new TypeToken<BaseResponse<String>>() {
                }.getType());
    }

    @Override
    public Observable<BaseResponse<String>> blacklistUser(long uid, int status) {
        Map<String,Object> params = new HashMap<>();
        params.put("friend_uid",uid);
        params.put("status",status);
        return NetworkRequest.INSTANCE.postMapByJAVA(
                Api.Route.User.FRIEND_BLACK,
                params,new RespMapper(),
                new TypeToken<BaseResponse<String>>(){}.getType()
        );
    }

    @Override
    public Observable<BaseResponse<String>> followUser(long friendId) {
        Map<String, Object> params = new HashMap<>();
        params.put("friend_id", friendId);
        return NetworkRequest.INSTANCE.postMapByJAVA(
                Api.Route.User.FOLLOWUSER, params,
                new RespMapper(),
                new TypeToken<BaseResponse<String>>() {
                }.getType());
    }

    @Override
    public Observable<BaseResponse<String>> unFollowUser(long friendId) {
        Map<String, Object> params = new HashMap<>();
        params.put("friend_id", friendId);
        return NetworkRequest.INSTANCE.postMapByJAVA(
                Api.Route.User.UNFOLLOWUSER,
                params, new RespMapper(),
                new TypeToken<BaseResponse<String>>() {
                }.getType());
    }

    @Override
    public Observable<BaseResponse<String>> applyRelativeFriend(long uid) {
        Map<String, Object> params = new HashMap<>();
        params.put("uid", uid);
        return NetworkRequest.INSTANCE.postMapByJAVA(
                Api.Route.User.APPLYRELATIVEFRIEND, params,
                new RespMapper(),
                new TypeToken<BaseResponse<String>>() {
                }.getType());
    }

    @Override
    public Observable<BaseResponse<String>> deleteRelativeFriend(long uid) {
        Map<String, Object> params = new HashMap<>();
        params.put("friend_uid", uid);
        return NetworkRequest.INSTANCE.postMapByJAVA(
                Api.Route.User.DELETERELATIVEFRIEND, params,
                new RespMapper(),
                new TypeToken<BaseResponse<String>>() {
                }.getType());
    }

    @Override
    public Observable<MediaReportTypeResponse> requestReportType() {
        return NetworkRequest.INSTANCE.postMap(Api.Route.Index.INDEX_MEDIA_REPORT_TYPE, new HashMap<>())
                .map(new Function<JsonElement, MediaReportTypeResponse>() {
                    @Override
                    public MediaReportTypeResponse apply(JsonElement jsonElement) throws Exception {
                        return GsonHelper.getsGson().fromJson(jsonElement, MediaReportTypeResponse.class);
                    }
                });
    }

}
