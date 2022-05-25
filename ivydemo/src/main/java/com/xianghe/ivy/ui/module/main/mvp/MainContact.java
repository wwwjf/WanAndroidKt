package com.xianghe.ivy.ui.module.main.mvp;

import android.content.Context;

import com.xianghe.ivy.model.UserBean;
import com.xianghe.ivy.model.VersionBean;
import com.xianghe.ivy.model.response.BaseResponse;
import com.xianghe.ivy.model.response.ResponseIndexCategoryList;
import com.xianghe.ivy.model.response.ResponseIndexShareQrCode;
import com.xianghe.ivy.mvp.IBaseConctact;

import io.reactivex.Observable;


public interface MainContact extends IBaseConctact {
    public interface View extends IBaseConctact.IBaseView {

        public static final int PAGE_FOLLOW = 0;
        public static final int PAGE_RECOMENED = 1;
        public static final int PAGE_MOMENTS = 2;

        public void showFollowBadge(boolean show);

        public void showRecommendBadge(boolean show);

        public void showMomentsBadge(boolean show);

        public int getCurrentPage();

        public void refreshPage(int page);

        /**
         * 显示更新提示
         *
         * @param versionBean   更新数据
         * @param isForceUpdate 是否强制升级
         */
        public void showUpdateView(VersionBean versionBean, boolean isForceUpdate);

        public void checkVersionFailed(Throwable throwable);

        public void clearPage(int pageMoments);

        public void showShareQrCode();

        public void hideShareQrCode();

        public void showMsg(String msg);

        public void getInvitationFail(Throwable throwable);

        void showMemberIcon();
        void hideMemberIcon();
    }

    public interface Presenter extends IBaseConctact.IBasePresenter<View> {
        public void startListenEvent();

        public void stopListenEvent();

        /**
         * 版本检测
         */
        public void checkVersion();

        //public void checkQrCode();

        public void jump2ShareQrCode(Context context);

        void getInvitationCode();

        void getUserHomePage(long uid);
    }

    public interface IMode extends IBaseConctact.IBaseMode {
        public Observable<ResponseIndexCategoryList> getCategoryList(String category, int page);

        /**
         * 版本检测
         *
         * @return
         */
        public Observable<BaseResponse<VersionBean>> checkVersion();

        public Observable<ResponseIndexShareQrCode> getShareQrCode();

        public Observable<BaseResponse<UserBean>> getUserInfo();

        /**
         * 获取已登录用户个人信息
         * @param uid
         * @return
         */
        Observable<BaseResponse<UserBean>> getUserHomePage(long uid);
    }
}
