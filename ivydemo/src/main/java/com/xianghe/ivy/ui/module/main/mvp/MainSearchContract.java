package com.xianghe.ivy.ui.module.main.mvp;

import com.xianghe.ivy.model.MainSearchBean;
import com.xianghe.ivy.model.MediaDetailBean;
import com.xianghe.ivy.model.response.BaseResponse;
import com.xianghe.ivy.mvp.loadPager.IBaseLoadingConctact;
import com.xianghe.ivy.ui.module.player.mvp.contact.PlayerContact;

import io.reactivex.Observable;

public interface MainSearchContract {

    interface Model extends IBaseLoadingConctact.IBaseMode {

        /**
         * 综合搜索
         * @param type 0-综合搜索，1-用户搜索，2-影片搜索
         * @param keyWord 关键字
         * @param page 第几页
         * @param pageSize 一页条数
         * @return
         */
        Observable<BaseResponse<MainSearchBean>> searchData(int type, String keyWord, int page, int pageSize);

        /**
         * 请求视频详情（带评论）
         * @param mediaId 视频id
         * @return
         */
        Observable<BaseResponse<MediaDetailBean>> getMediaDetail(int mediaId);
        /**
         * 关注用户
         * @param uid uid
         * @return
         */
        Observable<BaseResponse<String>> followUser(long uid);
    }

    interface View extends IBaseLoadingConctact.IBaseView {

        void onNetworkError(Throwable throwable);

        void onFailed(Throwable throwable);

        void onSearchSuccess(BaseResponse<MainSearchBean> response,int page);

        void onRequestMovieDetailSuccess(PlayerContact.IView.Data data, int position);

        void onFollowUserSuccess(BaseResponse<String> response,int position);

        void showToast(String msg);
    }

    interface Presenter extends IBaseLoadingConctact.IBasePresenter<MainSearchContract.View> {

        void searchData(int type, String keyWord, int page, int pageSize);

        void requestMovieDetail(int mediaId,int position);

        void followUser(long uid,int position);
    }
}
