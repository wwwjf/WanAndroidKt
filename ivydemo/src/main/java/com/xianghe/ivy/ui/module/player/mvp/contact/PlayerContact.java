package com.xianghe.ivy.ui.module.player.mvp.contact;

import android.content.Context;

import com.xianghe.ivy.model.CategoryMovieBean;
import com.xianghe.ivy.model.UserBean;
import com.xianghe.ivy.model.VersionBean;
import com.xianghe.ivy.model.response.BaseResponse;
import com.xianghe.ivy.model.response.ResponseIndexCategoryList;
import com.xianghe.ivy.mvp.IBaseConctact;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

public interface PlayerContact extends IBaseConctact {
    public interface IView extends IBaseConctact.IBaseView {


        public class Data implements Serializable {
            public static final int INVALID_INDEX = -1;
            public static final int INVALID_PAGE = 0;

            public static final int LOAD_TYPE_CATEGORY = 0;
            public static final int LOAD_TYPE_USER_MEDIA = 1;
            public static final int LOAD_TYPE_COLLECTION = 2;


            //  网络状态标志
            private boolean networkState = true;
            // 悬浮菜单
            private boolean isHideMenu = true;

            // 视频列表相关
            private ArrayList<CategoryMovieBean> movies = new ArrayList<>();    //视频列表
            private int index = INVALID_INDEX;  // 视频开始的index

            // 评论相关
            private long startCommentId = -1;

            // 请求相关
            private String category;
            private int page = INVALID_PAGE;
            private int loadType = LOAD_TYPE_CATEGORY;
            private long uid;


            /**
             * 点击头像关闭当前页面还是开启一个新的页面,true-关闭当前页面，默认false
             */
            private boolean isMine;

            public ArrayList<CategoryMovieBean> getMovies() {
                return movies;
            }

            public boolean isHideMenu() {
                return isHideMenu;
            }

            public void setHideMenu(boolean hideMenu) {
                isHideMenu = hideMenu;
            }


            public void setNetworkState(boolean NetworkState) {
                networkState = NetworkState;
            }
            public void setMovies(List<CategoryMovieBean> movies) {
                if (movies != null) {
                    this.movies.addAll(movies);
                }
            }

            public int getIndex() {
                return index;
            }

            public void setIndex(int index) {
                this.index = index;
            }

            public long getStartCommentId() {
                return startCommentId;
            }

            public void setStartCommentId(long startCommentId) {
                this.startCommentId = startCommentId;
            }

            public String getCategory() {
                return category;
            }

            public void setCategory(String category) {
                this.category = category;
            }

            public int getPage() {
                return page;
            }

            public void setPage(int page) {
                this.page = page;
            }

            public boolean isMine() {
                return isMine;
            }

            public void setMine(boolean mine) {
                isMine = mine;
            }

            public int getLoadType() {
                return loadType;
            }

            public void setLoadType(int loadType) {
                this.loadType = loadType;
            }

            public long getUid() {
                return uid;
            }

            public void setUid(long uid) {
                this.uid = uid;
            }

            @Override
            public String toString() {
                return "Data{" +
                        "index=" + index +
                        ", movies=" + movies +
                        ", category='" + category + '\'' +
                        ", page=" + page +
                        '}';
            }
        }

        public void refreshData();

        public void finishRefresh(long delay);

        public void finishLoadMore(long delay);

        public void refreshCategoryList(List<CategoryMovieBean> movies);

        public void addCategoryList(List<CategoryMovieBean> movies);

        public void showMsg(CharSequence text);

        public void showLoginConfirmDialog();

        /**
         * 关注用户结果
         *
         * @param uid     用户id
         * @param success true 成功; false 失败;
         */
        public void onFollowUserResult(long uid, boolean success);

        /**
         * 有亲友添加
         *
         * @param uid 亲友id
         */
        public void onAddRelative(long uid);

        public void onUserRelationShipChange(long uid, int caseWhat);

        void updateMyPortrait(String newUrl);

        /**
         * 显示更新提示
         *
         * @param versionBean   更新数据
         * @param isForceUpdate 是否强制升级
         */
        public void showUpdateView(VersionBean versionBean, boolean isForceUpdate);
        public void checkVersionFailed(Throwable throwable);

        /**
         * 获取用户信息
         * @param d
         */
        void onRequestDataSuccess(BaseResponse<UserBean> d);

        /**
         * 网络异常
         * @param throwable
         */
        void onNetworkError(Throwable throwable);

        void onRequestFailed(Throwable throwable);
    }

    public interface IPresenter extends IBaseConctact.IBasePresenter<IView> {
        public void refresh(final String category);

        public void loadMoreCategoryList(boolean formUser, final String category, final int page);

        public void loadMoreUserMediaList(boolean formUser, long uid, int page);

        public void loadMoreCollectMediaList(boolean formUser, int page);

        public void stopMediaPlayer();

        public void pauseMediaPlayer();

        public void resumeMediaPlayer();

        public void jump2UserInfoInfoNeed(Context context, long userId, float totalMoveX, float totalMoveY, int maxX, int maxY);

        public void jump2UserInfo(Context context, long userId, boolean withMenu);
        //获取用户信息
        public void requestData(long uid);

        public void jump2Record(Context context);

        public void followUser(long uid);

        public void startListener();

        public void stopListener();
        /**
         * 版本检测
         */
        public void checkVersion();

    }

    public interface IMode extends IBaseConctact.IBaseMode {

        /**
         * 过去首页列表
         *
         * @param categoryType {@link com.xianghe.ivy.constant.Api.Value.CategoryType#NEW}          最新;
         *                     {@link com.xianghe.ivy.constant.Api.Value.CategoryType#HOT}          热门;
         *                     {@link com.xianghe.ivy.constant.Api.Value.CategoryType#FOLLOW}       关注;
         *                     {@link com.xianghe.ivy.constant.Api.Value.CategoryType#FRIEND}       亲友;
         *                     {@link com.xianghe.ivy.constant.Api.Value.CategoryType#RECOMMEND}    推荐;
         * @param page         page
         */
        public Observable<ResponseIndexCategoryList> getCategoryList(String categoryType, int page);

        public Observable<ResponseIndexCategoryList> getUserMediaList(long uid, int page);

        public Observable<ResponseIndexCategoryList> getCollectMediaList(int page);

        /**
         * 关注用户(添加朋友)
         *
         * @param uid uid
         */
        public Observable<BaseResponse> addFriend(long uid);

        public Observable<BaseResponse<UserBean>> getUserInfo();

        /**
         * 版本检测
         *
         * @return
         */
        public Observable<BaseResponse<VersionBean>> checkVersion();

        /**
         * 请求用户主页信息
         * @param uid
         * @return
         */
        Observable<BaseResponse<UserBean>> requestData(long uid);

    }
}
