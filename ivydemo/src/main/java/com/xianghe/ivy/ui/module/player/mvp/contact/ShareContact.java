package com.xianghe.ivy.ui.module.player.mvp.contact;

import android.content.Context;

import com.xianghe.ivy.model.CategoryMovieBean;
import com.xianghe.ivy.model.ShareDescriptionBean;
import com.xianghe.ivy.model.response.BaseResponse;
import com.xianghe.ivy.model.response.MediaReportTypeResponse;
import com.xianghe.ivy.mvp.IBaseConctact;

import java.util.Map;

import io.reactivex.Observable;

public interface ShareContact extends IBaseConctact {
    public interface View extends IBaseConctact.IBaseView {

        public void showMsg(String msg);

        /**
         * 显示举报结果对话框
         *
         * @param msg      msg
         * @param iconType 0 消息icon;
         *                 1 成功icon;
         *                 2 失败icon;
         */
        public void showMsg(String msg, int iconType);

        public void showRequestPermissionDialog(String[] permissions);

        public void showLoginConfirmDialog();

        public void showReportDialog(Map<Integer, String> items);

        public void onShareResult(String name, boolean result);

        public void onForwardMedia(long mediaId, boolean success);

    }

    public interface Presenter extends IBaseConctact.IBasePresenter<View> {

        void requestShareInfo();

        public void mediaReport(CategoryMovieBean movie, int type, CharSequence others);

        public void downloadManager(Context ctx, CategoryMovieBean movie);

        public void showShare(Context ctx, String platform, CategoryMovieBean movie);

        public boolean isLogin();

        public void reportMedia();

        public void forwardMedia(long mediaId, String content);
    }

    public interface IMode extends IBaseConctact.IBaseMode {
        public Observable<BaseResponse> mediaReport(CategoryMovieBean movieBean, int type, String content);

        public Observable<MediaReportTypeResponse> getReportType();

        public Observable<BaseResponse> mediaForwarding(long mediaId, String content);

        Observable<BaseResponse<ShareDescriptionBean>> requestShareDescriptionInfo();
    }
}
