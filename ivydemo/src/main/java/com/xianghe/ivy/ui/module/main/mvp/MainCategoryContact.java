package com.xianghe.ivy.ui.module.main.mvp;

import com.xianghe.ivy.model.CategoryMovieBean;
import com.xianghe.ivy.model.UserBean;
import com.xianghe.ivy.mvp.IBaseConctact;

import java.util.List;

public interface MainCategoryContact extends IBaseConctact {

    public interface View extends IBaseConctact.IBaseView {

        public void onShow();

        public void clearData();

        public void refreshData();

        public void refreshCategoryList(List<CategoryMovieBean> movies, boolean formUser);

        public void loadMoreCategoryList(int page, List<CategoryMovieBean> movies);

        public void showErrorMsg(String msg);

        public void finishLoadMore(long delay);

        public void showDateEmpty();

        public void showNetWorkError();

        public void showLoading(boolean show);

        public void updateMovie(CategoryMovieBean movie);

        public void onAddFriend(long uid);

        public void onUserRelationShipChange(long uid, int caseWhat);

        public void updateMyPortrait(String newUrl);
    }

    public interface Presenter extends IBaseConctact.IBasePresenter<View> {

        public void receiveBroadcast();

        public void unReceiveBroadcast();

        /**
         * 刷新数据
         *
         * @param category {@link com.xianghe.ivy.constant.Api.Value.CategoryType#NEW}          最新;
         *                 {@link com.xianghe.ivy.constant.Api.Value.CategoryType#HOT}          热门;
         *                 {@link com.xianghe.ivy.constant.Api.Value.CategoryType#FOLLOW}       关注;
         *                 {@link com.xianghe.ivy.constant.Api.Value.CategoryType#FRIEND}       亲友圈;
         *                 {@link com.xianghe.ivy.constant.Api.Value.CategoryType#RECOMMEND}    推荐;
         */
        public void refreshCategoryList(final String category, boolean formUser);

        /**
         * 加载更多数据
         *
         * @param category {@link com.xianghe.ivy.constant.Api.Value.CategoryType#NEW}          最新;
         *                 {@link com.xianghe.ivy.constant.Api.Value.CategoryType#HOT}          热门;
         *                 {@link com.xianghe.ivy.constant.Api.Value.CategoryType#FOLLOW}       关注;
         *                 {@link com.xianghe.ivy.constant.Api.Value.CategoryType#FRIEND}       亲友圈;
         *                 {@link com.xianghe.ivy.constant.Api.Value.CategoryType#RECOMMEND}    推荐;
         * @param page     第几页
         * @param fromUser false 自动加载机制调用， true 用户主动加载(e.g 左滑加载更多)
         */
        public void loadMoreCategoryList(String category, int page, boolean fromUser);

    }
}
