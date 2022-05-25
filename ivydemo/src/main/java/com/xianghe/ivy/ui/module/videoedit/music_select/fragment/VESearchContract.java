package com.xianghe.ivy.ui.module.videoedit.music_select.fragment;

import com.xianghe.ivy.model.MusicList1;
import com.xianghe.ivy.model.response.BaseResponse;
import com.xianghe.ivy.mvp.loadPager.IBaseLoadingConctact;

import io.reactivex.Observable;

/**
 * @Author: ycl
 * @Date: 2018/10/24 17:05
 * @Desc:
 */
public interface VESearchContract {

    interface Model extends IBaseLoadingConctact.IBaseMode {
        Observable<BaseResponse<MusicList1>> requestData(String key, int page, int pageSize);
    }

    interface View extends IBaseLoadingConctact.IBaseView {
        void onMusicSuccess(MusicList1 d);
    }

    interface Presenter extends IBaseLoadingConctact.IBasePresenter<VESearchContract.View> {
        void requestData(String key,int page, int pageSize);
    }

}
