package com.xianghe.ivy.ui.module.main.mvp.mode;

import com.google.gson.reflect.TypeToken;
import com.luckongo.tthd.net.mapper.RespMapper;
import com.xianghe.ivy.constant.Api;
import com.xianghe.ivy.http.request.NetworkRequest;
import com.xianghe.ivy.model.ActivityDetailsBean;
import com.xianghe.ivy.model.response.BaseResponse;
import com.xianghe.ivy.ui.module.main.mvp.ActivityContentContract;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;

/**
 * @创建者 Allen
 * @创建时间 2019/3/28 15:03
 * @描述      活动内容model
 */
public class ActivityContentModel implements ActivityContentContract.Model {
    private static final String TAG = "ActivityContentModel";

    @Override
    public Observable<BaseResponse<ActivityDetailsBean>> getContentDetails(int activity_type) {
        Map<String, Object> params = new HashMap<>();
        params.put(Api.IActivityContent.activity_type, activity_type);
        return NetworkRequest.INSTANCE
                .postMapByJAVA(Api.Route.Index.ACTIVITY_INDEX,
                        params, new RespMapper(),
                        new TypeToken<BaseResponse<ActivityDetailsBean>>() {
                        }.getType());
    }
}
