package com.xianghe.ivy.ui.module.videoedit.music_select.fragment;

import com.xianghe.ivy.http.scheduler.SchedulerProvider;
import com.xianghe.ivy.model.MusicBean;
import com.xianghe.ivy.model.response.BaseResponse;
import com.xianghe.ivy.mvp.loadPager.BaseLoadingPresenter;

import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * @Author: ycl
 * @Date: 2018/10/24 17:12
 * @Desc:
 */
public class VEHotPresenter extends BaseLoadingPresenter<VEHotContract.View>
        implements VEHotContract.Presenter {

    private VEHotModel mModel = new VEHotModel();

    @Override
    public void requestData(int type) {
        if (type == -1) {
            onError("type:" + type);
            return;
        }
        showLoading();
        add(mModel.requestData(type)
                .observeOn(SchedulerProvider.INSTANCE.ui())
                .subscribe(new Consumer<BaseResponse<List<MusicBean>>>() {
                    @Override
                    public void accept(BaseResponse<List<MusicBean>> bean) throws Exception {
                        clossLoading();
                        List<MusicBean> data = bean.getData();
                        if (data != null&&!data.isEmpty()) {
                            view().onMusicSuccess(data);
                        } else {
                            onError(null);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        clossLoading();
                        onError(throwable);
                    }
                }));
    }
}
