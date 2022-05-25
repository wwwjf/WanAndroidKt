package com.xianghe.ivy.ui.module.main.mvp.mode;

import com.google.gson.reflect.TypeToken;
import com.luckongo.tthd.net.mapper.RespMapper;
import com.xianghe.ivy.constant.Api;
import com.xianghe.ivy.http.request.NetworkRequest;
import com.xianghe.ivy.model.MainSearchBean;
import com.xianghe.ivy.model.MediaDetailBean;
import com.xianghe.ivy.model.response.BaseResponse;
import com.xianghe.ivy.ui.module.main.mvp.MainSearchContract;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;

public class MainSearchModel implements MainSearchContract.Model {
    @Override
    public Observable<BaseResponse<MainSearchBean>> searchData(int type, String keyWord,
                                                               int page, int pageSize) {
        Map<String, Object> params = new HashMap<>();
        params.put("type", type);
        params.put("keyword", keyWord);
        params.put("page", page);
        params.put("pagesize", pageSize);
        return NetworkRequest.INSTANCE
                .postMapByJAVA(Api.Route.Search.ESGET_LIST,
                        params, new RespMapper(),
                        new TypeToken<BaseResponse<MainSearchBean>>() {
                        }.getType());
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

    @Override
    public Observable<BaseResponse<String>> followUser(long uid) {
        Map<String, Object> params = new HashMap<>();
        params.put("friend_id",uid);
        return NetworkRequest.INSTANCE.postMapByJAVA(Api.Route.User.FOLLOWUSER,
                params, new RespMapper(),
                new TypeToken<BaseResponse<String>>() {}.getType());
    }
}
