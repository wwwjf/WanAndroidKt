package com.xianghe.ivy.ui.module.player.mvp.contact;

import android.content.Context;

import com.xianghe.ivy.model.CategoryMovieBean;
import com.xianghe.ivy.model.response.BaseResponse;
import com.xianghe.ivy.mvp.IBaseConctact;

import io.reactivex.Observable;

public interface ExFunContact extends IBaseConctact {
    @SuppressWarnings("UnnecessaryInterfaceModifier")
    public interface IView extends IBaseConctact.IBaseView {

        public enum Meuns {
            COMMENT,    // 评论
            SHARE,      // 分享
        }


        /**
         * 更新界面显示数据
         *
         * @param movie movie
         */
        public void updateUI(CategoryMovieBean movie);


        /**
         * 菜单是否已经打开
         *
         * @return true 分享菜单 或 评论菜单 已打开; false 没有菜单打开
         */
        public boolean isMenuOpen();


        /**
         * 打开菜单
         *
         * @param menus {@link Meuns#COMMENT} 评论菜单
         *              {@link Meuns#SHARE} 分享菜单
         */
        public void openMenu(Meuns menus);


        /**
         * 关闭菜单
         */
        public void closeMenu();


        /**
         * 提示信息
         *
         * @param msg msg
         */
        public void showMsg(String msg);

        /**
         * 关注视频结果
         *
         * @param mediaId 视频id.
         * @param follow  true 关注; false 取消关注.
         * @param success true 操作成功; false 操作失败.
         */
        public void followResult(long mediaId, boolean follow, boolean success);

        /**
         * 点赞视频结果
         *
         * @param mediaId 视频id.
         * @param praise  true 点赞; false 取消点赞.
         * @param success true 操作成功; false 操作失败.
         */
        public void praiseResult(long mediaId, boolean praise, boolean success);
    }

    @SuppressWarnings("UnnecessaryInterfaceModifier")
    public interface Presenter extends IBaseConctact.IBasePresenter<IView> {

        /**
         * 收藏视频
         *
         * @param mediaId 视频id
         * @param follow  true 收藏;
         *                false 取消收藏;
         */
        public void follow(long mediaId, boolean follow);


        /**
         * 点赞视频
         *
         * @param mediaId 视频id
         * @param praise  true 点赞;
         *                false 取消点赞;
         */
        public void praise(long mediaId, boolean praise);


        /**
         * 判断是否已经登录
         *
         * @return true 已登录; false 未登录
         */
        public boolean isLogin();


        /**
         * 跳转登录界面
         *
         * @param context context
         */
        public void jump2Login(Context context);
    }


    @SuppressWarnings("UnnecessaryInterfaceModifier")
    public interface IMode extends IBaseConctact.IBaseMode {
        public Observable<BaseResponse> collection(long mediaId);

        public Observable<BaseResponse> collectionCancel(long mediaId);

        public Observable<BaseResponse> praise(long mediaId);

        public Observable<BaseResponse> praiseCancel(long mediaId);
    }
}
