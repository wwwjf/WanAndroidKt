package com.xianghe.ivy.ui.module.main.mvp.presenter;

import com.blankj.utilcode.util.StringUtils;
import com.xianghe.ivy.http.request.RequestError;
import com.xianghe.ivy.http.response.RespCode;
import com.xianghe.ivy.http.scheduler.SchedulerProvider;
import com.xianghe.ivy.model.CategoryMovieBean;
import com.xianghe.ivy.model.MainSearchBean;
import com.xianghe.ivy.model.MediaDetailBean;
import com.xianghe.ivy.model.response.BaseResponse;
import com.xianghe.ivy.mvp.loadPager.BaseLoadingPresenter;
import com.xianghe.ivy.ui.module.main.mvp.MainSearchContract;
import com.xianghe.ivy.ui.module.main.mvp.mode.MainSearchModel;
import com.xianghe.ivy.ui.module.player.mvp.contact.PlayerContact;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainSearchPresenter extends BaseLoadingPresenter<MainSearchContract.View>
        implements MainSearchContract.Presenter {
    private MainSearchModel mModel = new MainSearchModel();

    @Override
    public void searchData(int type, String keyWord, int page, int pageSize) {
        MainSearchContract.View view = view();
        if (view == null) {
            return;
        }
        if (StringUtils.isEmpty(keyWord)) {
            return;
        }

        add(mModel.searchData(type, keyWord, page, pageSize)
                .observeOn(SchedulerProvider.INSTANCE.ui())
                .subscribe(new Consumer<BaseResponse<MainSearchBean>>() {
                    @Override
                    public void accept(BaseResponse<MainSearchBean> response) throws Exception {
                        MainSearchPresenter.this.clossLoading();
                        view.onSearchSuccess(response, page);
                    }
                }, throwable -> {
                    clossLoading();
                    if (throwable instanceof RequestError) {
                        int code = ((RequestError) throwable).getState();
                        if (code == RespCode.CODE_901) {
                            //无网络
                            view.onNetworkError(throwable);
                        } else {
                            view.onFailed(throwable);
                        }
                    } else {
                        view.onFailed(throwable);
                    }
                }));
    }

    @Override
    public void requestMovieDetail(int mediaId, int position) {
        MainSearchContract.View view = view();
        if (view == null) {
            return;
        }
        showLoading();
        add(mModel.getMediaDetail(mediaId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    clossLoading();
                    view.onRequestMovieDetailSuccess(transferData(response,mediaId), position);
                }, throwable -> {
                    clossLoading();
                    view.onFailed(throwable);
                }));
    }

    @Override
    public void followUser(long uid, int position) {
        MainSearchContract.View view = view();
        if (view == null) {
            return;
        }
        showLoading();
        add(mModel.followUser(uid)
                .subscribeOn(SchedulerProvider.INSTANCE.io())
                .observeOn(SchedulerProvider.INSTANCE.ui())
                .subscribe(response -> {
                    clossLoading();
                    view.onFollowUserSuccess(response, position);
                }, throwable -> {
                    clossLoading();
                    view.onFailed(throwable);
                }));
    }

    /**
     * 数据转换
     *
     * @param response 接口返回数据
     * @return
     */
    private PlayerContact.IView.Data transferData(BaseResponse<MediaDetailBean> response,int mediaId) {
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
