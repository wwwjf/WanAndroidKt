package com.xianghe.ivy.ui.module.player.mvp.presenter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.StringUtils;
import com.google.gson.JsonElement;
import com.google.gson.internal.LinkedTreeMap;
import com.xianghe.ivy.BuildConfig;
import com.xianghe.ivy.R;
import com.xianghe.ivy.constant.Api;
import com.xianghe.ivy.http.request.NetworkRequest;
import com.xianghe.ivy.http.scheduler.SchedulerProvider;
import com.xianghe.ivy.manager.UserInfoManager;
import com.xianghe.ivy.manager.download.IMovieDownloadManager;
import com.xianghe.ivy.manager.download.MovieDownloadManager;
import com.xianghe.ivy.model.CategoryMovieBean;
import com.xianghe.ivy.model.MediaReportTypeBean;
import com.xianghe.ivy.model.ShareDescriptionBean;
import com.xianghe.ivy.model.VideoTypeBean;
import com.xianghe.ivy.model.response.BaseResponse;
import com.xianghe.ivy.model.response.MediaReportTypeResponse;
import com.xianghe.ivy.mvp.BasePresenter;
import com.xianghe.ivy.network.GsonHelper;
import com.xianghe.ivy.ui.module.player.mvp.contact.ShareContact;
import com.xianghe.ivy.ui.module.player.mvp.mode.ShareMode;
import com.xianghe.ivy.utils.KLog;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class SharePresenter extends BasePresenter<ShareContact.View> implements ShareContact.Presenter {
    private static final String TAG = "SharePresenter";

    private final ShareContact.IMode mMode = new ShareMode();
    private final Context mContext;

    public SharePresenter(Context context) {
        mContext = context;
    }

    @Override
    public void requestShareInfo() {
        ShareContact.View view = view();
        if (view == null){
            return;
        }
        add(mMode.requestShareDescriptionInfo()
                .observeOn(SchedulerProvider.INSTANCE.ui())
                .subscribeOn(SchedulerProvider.INSTANCE.io()).subscribe(response -> {
                    ShareDescriptionBean shareDescription = response.getData();
                    //保存
                    SPUtils spShare = SPUtils.getInstance("ShareDescription");
                    spShare.put("title",shareDescription.getTitle());
                    spShare.put("description",shareDescription.getDescription());
                },throwable -> KLog.e("请求分享文案接口出错")));
    }

    @Override
    public void mediaReport(CategoryMovieBean movieBean, int type, CharSequence others) {
        add(mMode.mediaReport(movieBean, type, others.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BaseResponse>() {
                    @Override
                    public void accept(BaseResponse baseResponse) throws Exception {
                        if (view() != null) {
                            if (baseResponse.getStatus() == BaseResponse.Status.OK) {
                                view().showMsg(mContext.getString(R.string.dialog_report_success), 1);
                            } else {
                                view().showMsg(mContext.getString(R.string.dialog_report_failed), 2);
                                KLog.e(TAG, baseResponse.getStatus());
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (view() != null) {
                            view().showMsg(mContext.getString(R.string.dialog_report_failed), 2);
                            KLog.e(TAG, throwable.getMessage());
                        }
                    }
                }));
    }

    @Override
    public void downloadManager(Context ctx, CategoryMovieBean movie) {
        if (!isLogin()) {
            view().showLoginConfirmDialog();
            return;
        }

        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            view().showRequestPermissionDialog(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
            return;
        }

        IMovieDownloadManager downloadManager = MovieDownloadManager.getInstance(ctx);

        if (downloadManager.contains(movie)) {
            view().showMsg(ctx.getString(R.string.share_movie_already_download), 0);
            return;
        }
        downloadManager.addTask(movie);
        downloadManager.startTask(movie);
    }

    @Override
    public void showShare(Context ctx, String platform, CategoryMovieBean movie) {

    }

    public static String platform2Channel(String platform) {
        return "";
    }

    @Override
    public boolean isLogin() {
        return UserInfoManager.isLogin();
    }

    @Override
    public void reportMedia() {
        if (!isLogin()) {
            view().showLoginConfirmDialog();
            return;
        }

        add(mMode.getReportType()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MediaReportTypeResponse>() {
                    @Override
                    public void accept(MediaReportTypeResponse response) throws Exception {
                        if (view() == null) {
                            return;
                        }
                        MediaReportTypeBean reportTypes = response.getData();
                        if (response != null && response.getStatus() == BaseResponse.Status.OK && reportTypes != null) {
                            Map<Integer, String> items = new LinkedTreeMap<>();
                            if (reportTypes.getVideoType() != null) {
                                for (int i = 0; i < reportTypes.getVideoType().size(); i++) {
                                    VideoTypeBean reportType = reportTypes.getVideoType().get(i);
                                    String str = BaseResponse.infoCode2String(mContext, reportType.getTypeCode());
                                    if (!TextUtils.isEmpty(str)) {
                                        items.put(reportType.getId(), str);
                                    }
                                }

                            }
                            view().showReportDialog(items);
                        } else {
                            KLog.d(TAG, "请求错误, 取消举报. response =" + response);
                            view().showMsg(BaseResponse.infoCode2String(mContext, response.getInfoCode()));
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (view() == null) {
                            return;
                        }
                        view().showMsg(mContext.getString(R.string.common_network_error));
                    }
                }));
    }

    @Override
    public void forwardMedia(long mediaId, String content) {
        if (!isLogin()) {
            view().showLoginConfirmDialog();
            return;
        }
        add(mMode.mediaForwarding(mediaId, content)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BaseResponse>() {
                    @Override
                    public void accept(BaseResponse response) throws Exception {
                        if (view() == null) {
                            return;
                        }
                        if (response != null && response.getStatus() == BaseResponse.Status.OK) {
                            view().showMsg(mContext.getString(R.string.share_forward_success));
                            view().onForwardMedia(mediaId, true);
                            //setUserAction(mediaId);
                        } else {
                            view().showMsg(BaseResponse.infoCode2String(mContext, response.getInfoCode()));
                            view().onForwardMedia(mediaId, false);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (view() == null) {
                            return;
                        }
                        view().showMsg(mContext.getString(R.string.common_network_error));
                    }
                })
        );
    }


    private void setUserAction(long movieId) {
        HashMap<String, Object> params = new HashMap<>();
        params.put(Api.Key.MEDIA_ID, movieId);
        params.put(Api.Key.ACTION, 1);
        NetworkRequest.INSTANCE.postMap(Api.Action.USER_ACTION, params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JsonElement>() {
                    @Override
                    public void accept(JsonElement jsonElement) throws Exception {
                        BaseResponse response = GsonHelper.getsGson().fromJson(jsonElement, BaseResponse.class);
                        if (response.getStatus() == 1) {
                            KLog.d("分享统计成功");
                        } else {
                            KLog.d("分享统计失败");
                        }

                    }
                });
    }
}
