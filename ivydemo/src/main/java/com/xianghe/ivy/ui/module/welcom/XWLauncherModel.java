package com.xianghe.ivy.ui.module.welcom;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.luckongo.tthd.net.mapper.RespMapper;
import com.xianghe.ivy.constant.Api;
import com.xianghe.ivy.http.request.NetworkRequest;
import com.xianghe.ivy.model.CategoryMovieBean;
import com.xianghe.ivy.model.MediaDetailBean;
import com.xianghe.ivy.model.response.BaseResponse;
import com.xianghe.ivy.model.response.ResponseIndexCategoryList;
import com.xianghe.ivy.network.GsonHelper;
import com.xianghe.ivy.utils.KLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        /*return NetworkRequest.INSTANCE.postMap(Api.Route.Index.CATEGORY_LIST, params)
                .map(new Function<JsonElement, ResponseIndexCategoryList>() {
                    @Override
                    public ResponseIndexCategoryList apply(JsonElement jsonElement) throws Exception {
                        KLog.d(TAG, jsonElement + "");
                        return GsonHelper.getsGson().fromJson(jsonElement, ResponseIndexCategoryList.class);
                    }
                });*/
        ResponseIndexCategoryList response = new ResponseIndexCategoryList();

        response.setStatus(BaseResponse.Status.OK);
        response.setInfoCode(10000);
        List<CategoryMovieBean> list = new ArrayList<>();
        CategoryMovieBean bean = new CategoryMovieBean();
        bean.setMedia("https://v95-p.douyinvod.com/6106f948c93449b04a4b282a7b257ad5/62970ca9/video/tos/cn/tos-cn-ve-15c001-alinc2/7fe7a9c9482743e08e88b2ba22aa62db/?a=1128&ch=0&cr=0&dr=0&cd=0%7C0%7C0%7C0&cv=1&br=538&bt=538&btag=80000&cs=0&ds=4&ft=blh3-IQQqUnXfmoZmo0OW_EklpPixBGcK~_39eFROAfJO12&mime_type=video_mp4&qs=0&rc=Zzc1ZThkMzVpZmg7ODM6O0BpM21yOjw6ZnZmZDMzNGkzM0AzNjNgXjMuNmAxXl9jMjQzYSNrNjNxcjRnaC9gLS1kLWFzcw%3D%3D&l=20220601135113010208039083420C73DD");
//        bean.setMedia("http://baikevideo.cdn.bcebos.com/media/mda-OgkOlOML8EqV0Umy/a751ec0436d8be9bb294617882aab136.mp4");
//        bean.setMedia("http://baikevideo.cdn.bcebos.com/media/mda-OgkOlOML8EqV0Umy/551a71b9d71e489a4999423bb00212c8.m3u8");
        bean.setCover("https://p3-sign.douyinpic.com/tos-cn-p-0015/0ddd5ad211d3419ea4049795faac9bba_1652871530~c5_300x400.jpeg?x-expires=1655269200&x-signature=Ei0L6qJU9uRWUoQ4GgW%2BYK4Sa3E%3D&from=4257465056_large&s=PackSourceEnum_DOUYIN_REFLOW&se=false&sc=cover&l=202206011351130102101050330E0C368D");
//        bean.setCover("http://bkimg.cdn.bcebos.com/smart/d439b6003af33a8769b6e1d6cf5c10385243b527-bkimg-process,v_1,rw_16,rh_9,maxl_640,pad_1?x-bce-process=image/format,f_auto");
        list.add(bean);
        list.add(bean);
        list.add(bean);
        list.add(bean);
        list.add(bean);
        list.add(bean);
        response.setData(list);
        return Observable.just(response);
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
