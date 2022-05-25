package com.xianghe.ivy.ui.module.player.mvp.mode;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.luckongo.tthd.net.mapper.RespMapper;
import com.xianghe.ivy.constant.Api;
import com.xianghe.ivy.http.request.NetworkRequest;
import com.xianghe.ivy.model.CategoryMovieBean;
import com.xianghe.ivy.model.ShareDescriptionBean;
import com.xianghe.ivy.model.response.BaseResponse;
import com.xianghe.ivy.model.response.MediaReportTypeResponse;
import com.xianghe.ivy.network.GsonHelper;
import com.xianghe.ivy.ui.module.player.mvp.contact.ShareContact;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

public class ShareMode implements ShareContact.IMode {
    @Override
    public Observable<BaseResponse> mediaReport(CategoryMovieBean movieBean, int type, String content) {
        Map<String, Object> params = new HashMap<>();
        params.put(Api.Key.MEDIA_ID, movieBean.getId());
        params.put(Api.Key.ID, type);
        params.put(Api.Key.CONTENT, content);

        return NetworkRequest.INSTANCE.postMap(Api.Route.Index.INDEX_MEDIA_REPORT, params)
                .map(new Function<JsonElement, BaseResponse>() {
                    @Override
                    public BaseResponse apply(JsonElement jsonElement) throws Exception {
                        return GsonHelper.getsGson().fromJson(jsonElement, BaseResponse.class);
                    }
                });
    }

    @Override
    public Observable<MediaReportTypeResponse> getReportType() {
        return NetworkRequest.INSTANCE.postMap(Api.Route.Index.INDEX_MEDIA_REPORT_TYPE, new HashMap<>())
                .map(new Function<JsonElement, MediaReportTypeResponse>() {
                    @Override
                    public MediaReportTypeResponse apply(JsonElement jsonElement) throws Exception {
                        return GsonHelper.getsGson().fromJson(jsonElement, MediaReportTypeResponse.class);
                    }
                });
    }

    @Override
    public Observable<BaseResponse> mediaForwarding(long mediaId, String content) {
        HashMap<String, Object> params = new HashMap<>();
        params.put(Api.Key.MEDIA_ID, mediaId);
        params.put(Api.Key.CONTENT, content);
        return NetworkRequest.INSTANCE.postMap(Api.Route.Index.MEDIA_FORWARDING, params)
                .map(new Function<JsonElement, BaseResponse>() {
                    @Override
                    public BaseResponse apply(JsonElement jsonElement) throws Exception {
                        return GsonHelper.getsGson().fromJson(jsonElement, BaseResponse.class);
                    }
                });
    }

    @Override
    public Observable<BaseResponse<ShareDescriptionBean>> requestShareDescriptionInfo() {
        return NetworkRequest.INSTANCE.postMapByJAVA(Api.Route.Index.SHARE_DESCRIPTION,
                new HashMap<>(),new RespMapper(),
                new TypeToken<BaseResponse<ShareDescriptionBean>>(){}.getType());
    }
}
