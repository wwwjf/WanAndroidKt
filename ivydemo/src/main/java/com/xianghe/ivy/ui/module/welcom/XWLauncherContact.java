package com.xianghe.ivy.ui.module.welcom;

import com.xianghe.ivy.model.CategoryMovieBean;
import com.xianghe.ivy.model.MediaDetailBean;
import com.xianghe.ivy.model.response.BaseResponse;
import com.xianghe.ivy.model.response.ResponseIndexCategoryList;
import com.xianghe.ivy.mvp.loadPager.IBaseLoadingConctact;
import com.xianghe.ivy.ui.module.player.mvp.contact.PlayerContact;

import java.util.List;

import io.reactivex.Observable;

public class XWLauncherContact {

    interface Model extends IBaseLoadingConctact.IBaseMode{
        Observable<ResponseIndexCategoryList> getCategoryList(String category, int page);
        /**
         * 请求视频详情（带评论）
         * @param mediaId 视频id
         * @return
         */
        Observable<BaseResponse<MediaDetailBean>> getMediaDetail(int mediaId);
    }

    interface View extends IBaseLoadingConctact.IBaseView{
        void getDataSuccess(List<CategoryMovieBean> data);

        void onRequestMovieDetailSuccess(PlayerContact.IView.Data data);

        void onRequestMovieDetailFailed(Throwable throwable);
    }

    interface Presenter extends IBaseLoadingConctact.IBasePresenter<View> {
        /**
         * 刷新数据
         *
         * @param category {@link com.xianghe.ivy.constant.Api.Value.CategoryType#NEW}          最新;
         *                 {@link com.xianghe.ivy.constant.Api.Value.CategoryType#HOT}          热门;
         *                 {@link com.xianghe.ivy.constant.Api.Value.CategoryType#FOLLOW}       关注;
         *                 {@link com.xianghe.ivy.constant.Api.Value.CategoryType#FRIEND}       亲友圈;
         *                 {@link com.xianghe.ivy.constant.Api.Value.CategoryType#RECOMMEND}    推荐;
         */
        void refreshCategoryList(final String category);

        void requestMovieDetail(int mediaId);
    }
}
