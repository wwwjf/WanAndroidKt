package com.xianghe.ivy.ui.module.user;

import com.xianghe.ivy.model.MediaReportTypeBean;
import com.xianghe.ivy.model.UserBean;
import com.xianghe.ivy.model.response.BaseResponse;
import com.xianghe.ivy.model.response.MediaReportTypeResponse;
import com.xianghe.ivy.model.response.ResponseIndexCategoryList;
import com.xianghe.ivy.mvp.loadPager.IBaseLoadingConctact;

import java.util.Map;

import io.reactivex.Observable;


public interface UserContract {
    interface Model extends IBaseLoadingConctact.IBaseMode {
        /**
         * 请求用户主页信息
         * @param uid
         * @return
         */
        Observable<BaseResponse<UserBean>> requestData(long uid);

        /**
         * 举报用户
         * @param uid 用户id
         * @param reportType 举报的类型 0=色情低俗 1=政治敏感词 2=违法犯罪 3= 侵犯版权 4=侮辱谩骂
         * @param content 其他问题
         * @param type 举报对象类型：1 举报用户
         * @return
         */
        Observable<BaseResponse<String>> reportUser(long uid,int reportType,String content,int type);

        /**
         * 加入、解除黑名单
         * @param uid 用户id
         * @param status 1：加入黑名单， 2：解除黑名单
         * @return
         */
        Observable<BaseResponse<String>> blacklistUser(long uid,int status);
        /**
         * 关注用户
         * @param friendId id
         * @return
         */
        Observable<BaseResponse<String>> followUser(long friendId);

        /**
         * 取消关注用户
         * @param friendId id
         * @return
         */
        Observable<BaseResponse<String>> unFollowUser(long friendId);

        /**
         * 申请加入亲友圈
         * @param uid uid
         * @return
         */
        Observable<BaseResponse<String>> applyRelativeFriend(long uid);

        /**
         * 删除亲友
         * @param uid uid
         * @return
         */
        Observable<BaseResponse<String>> deleteRelativeFriend(long uid);

        /**
         * 请求举报类型信息
         * @return
         */
        Observable<MediaReportTypeResponse> requestReportType();
    }

    interface View extends IBaseLoadingConctact.IBaseView {
        void onRequestDataSuccess(BaseResponse<UserBean> d);

        void onReportUserSuccess(BaseResponse<String> d);

        void onReportUserFailed(String errorMsg);

        void onBlacklistSuccess(BaseResponse<String> response);

        void onBlacklistFailed(Throwable throwable);

        void showReportDialog(Map<Integer, String> items);

        void onFollowUserSuccess(BaseResponse<String> response);

        void onUnFollowUserSuccess(BaseResponse<String> response);

        void onApplyRelativeFriendSuccess(BaseResponse<String> response);

        void onRequestFailed(Throwable throwable);

        void onDeleteRelativeFriendSuccess(BaseResponse<String> response);

        /**
         * 网络异常
         * @param throwable
         */
        void onNetworkError(Throwable throwable);

    }

    interface Presenter extends IBaseLoadingConctact.IBasePresenter<View> {
        void requestData(long uid);

        void reportUser(long uid,int reportType,String content,int type);

        void blacklistUser(long uid,int isBlacklist);

        void followUser(long friendId);

        void unFollowUser(long friendId);

        void applyRelativeFriend(long uid);

        void deleteRelativeFriend(long uid);

        void showReportDialog();
    }
}
