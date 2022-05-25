package com.xianghe.ivy.ui.module.videoedit.music_select.fragment;

import com.xianghe.ivy.http.scheduler.SchedulerProvider;
import com.xianghe.ivy.model.MusicList1;
import com.xianghe.ivy.mvp.loadPager.BaseLoadingPresenter;

/**
 * @Author: ycl
 * @Date: 2018/10/24 17:12
 * @Desc:
 */
public class VESearchPresenter extends BaseLoadingPresenter<VESearchContract.View>
        implements VESearchContract.Presenter {

    private final VESearchModel mModel = new VESearchModel();

    @Override
    public void requestData(String key,int page,int pageSize) {
        showLoading();
        add(mModel.requestData(key,page,pageSize)
                .observeOn(SchedulerProvider.INSTANCE.ui())
                .subscribe(bean -> {
                    clossLoading();
                    MusicList1 data = bean.getData();
                    if (data != null) {
                        view().onMusicSuccess(data);
                    } else {
                        onError(null);
                    }
                }, throwable -> {
                    clossLoading();
                    onError(throwable);
                }));
    }
}
