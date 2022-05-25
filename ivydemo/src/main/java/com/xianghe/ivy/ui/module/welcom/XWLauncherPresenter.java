package com.xianghe.ivy.ui.module.welcom;

import android.util.Log;

import com.xianghe.ivy.http.scheduler.SchedulerProvider;
import com.xianghe.ivy.model.CategoryMovieBean;
import com.xianghe.ivy.model.MediaDetailBean;
import com.xianghe.ivy.model.response.BaseResponse;
import com.xianghe.ivy.model.response.ResponseIndexCategoryList;
import com.xianghe.ivy.mvp.BasePresenter;
import com.xianghe.ivy.ui.module.player.mvp.contact.PlayerContact;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class XWLauncherPresenter extends BasePresenter<XWLauncherContact.View> implements XWLauncherContact.Presenter{


    private XWLauncherModel mXWLauncherModel = new XWLauncherModel();

    @Override
    public void refreshCategoryList(String category) {
        XWLauncherContact.View view = view();
        if (view == null){
            return;
        }
        add(mXWLauncherModel.getCategoryList(category, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (view() != null) {
                        if (response != null && response.getStatus() == BaseResponse.Status.OK) {

                            view.getDataSuccess(response.getData());
                        }
                    }
                }));
    }

    @Override
    public void requestMovieDetail(int mediaId) {
        XWLauncherContact.View view = view();
        if (view == null){
            return;
        }
        add(mXWLauncherModel.getMediaDetail(mediaId)
                .subscribeOn(SchedulerProvider.INSTANCE.io())
                .observeOn(SchedulerProvider.INSTANCE.computation())
                .subscribe(response -> view.onRequestMovieDetailSuccess(transferData(response,mediaId)),
                        view::onRequestMovieDetailFailed));
    }

    /**
     * 数据转换
     *
     * @param response 接口返回数据
     * @return
     */
    private PlayerContact.IView.Data transferData(BaseResponse<MediaDetailBean> response, int mediaId) {
        MediaDetailBean mediaDetail = response.getData();
        CategoryMovieBean movieBean = new CategoryMovieBean();
        movieBean.setId(mediaId);
        movieBean.setTitle(mediaDetail.getTitle());
        movieBean.setMedia(mediaDetail.getMedia());
        movieBean.setComments(mediaDetail.getComments() == null ? 0 : mediaDetail.getComments().getCommentsTotal());
        movieBean.setStar(mediaDetail.getStar());
        movieBean.setIsAttention(mediaDetail.getIsAttention());
        movieBean.setIsStar(mediaDetail.getIsMediaStar());
        movieBean.setIsLike(mediaDetail.getIsAuthorStar());
        movieBean.setUid(mediaDetail.getUid());
        movieBean.setIsFriend(mediaDetail.getIsFriend());
        movieBean.setCreatedAt(mediaDetail.getCreatedAt());
        movieBean.setAddress(mediaDetail.getAddress());
        movieBean.setCover(mediaDetail.getCover());
        movieBean.setVideoLength(mediaDetail.getVideoLength());
        movieBean.setUsername(mediaDetail.getUsername());
        movieBean.setAvatar(mediaDetail.getAvatar());
        movieBean.setShare(mediaDetail.getShare());
        movieBean.setLike(mediaDetail.getLike());
        movieBean.setIsAttention(mediaDetail.getIsAttention());
        List<CategoryMovieBean> movies = new ArrayList<>();
        movies.add(movieBean);

        PlayerContact.IView.Data data = new PlayerContact.IView.Data();
        data.setMovies(movies);
        data.setIndex(0);
        return data;
    }
}
