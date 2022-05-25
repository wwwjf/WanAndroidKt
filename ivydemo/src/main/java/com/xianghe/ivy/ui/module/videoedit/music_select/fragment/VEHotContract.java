package com.xianghe.ivy.ui.module.videoedit.music_select.fragment;

import com.xianghe.ivy.model.MusicBean;
import com.xianghe.ivy.model.response.BaseResponse;
import com.xianghe.ivy.mvp.loadPager.IBaseLoadingConctact;

import java.util.List;

import io.reactivex.Observable;

/**
 * @Author: ycl
 * @Date: 2018/10/24 17:05
 * @Desc:
 */
public interface VEHotContract {

    interface Model extends IBaseLoadingConctact.IBaseMode {
        Observable<BaseResponse<List<MusicBean>>> requestData(int type);
    }

    interface View extends IBaseLoadingConctact.IBaseView {
        void onMusicSuccess(List<MusicBean> d);
    }

    interface Presenter extends IBaseLoadingConctact.IBasePresenter<VEHotContract.View> {
        void requestData(int type);
    }

}
