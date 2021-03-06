package com.xianghe.ivy.ui.module.record.local_media;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xianghe.ivy.R;
import com.xianghe.ivy.adapter.adapter.BaseQuickAdapter;
import com.xianghe.ivy.mvp.BaseFragment;
import com.xianghe.ivy.ui.media.config.MediaConfig;
import com.xianghe.ivy.ui.module.record.model.MovieItemModel;
import com.xianghe.ivy.utils.FileUtill;
import com.xianghe.ivy.utils.IvyUtils;
import com.xianghe.ivy.weight.loading.VaryViewHelperController;
import com.xianghe.ivy.weight.photo_select.widget.DividerGridItemDecoration;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import icepick.State;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LocalVideoFragment extends BaseFragment implements BaseQuickAdapter.RequestLoadMoreListener {

    @BindView(R.id.rv_fragment_local_media_recyclerView)
    RecyclerView mRecyclerView;

    private VaryViewHelperController mVaryViewHelperController;

    private Disposable mSubscribe;

    private int count = 24,mSelectVideoCount;
    private double mSelectVideoTime;

    @State
    int startIndex = 0;

    private LocalMediaAdapter mLocalMediaAdapter;

    public LocalVideoFragment(){}

    @Override
    protected int getChildView() {
        return R.layout.fragment_recyclerview;
    }

    @Override
    protected void initChildData(Bundle savedInstanceState) {
        mVaryViewHelperController = new VaryViewHelperController(mRecyclerView);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mSelectVideoCount= bundle.getInt("selectVideoCount");
            mSelectVideoTime= bundle.getDouble("selectVideoTime");
        }
        initData(false);
    }

    @SuppressLint("CheckResult")
    private void initData(boolean isLoadMore) {
        if (!isLoadMore) {
            mVaryViewHelperController.showLoading();
        }
        mSubscribe = Observable.create((ObservableOnSubscribe<ArrayList<MovieItemModel>>) e -> {
            ArrayList<MovieItemModel> localMedia = getLocalMedia();
            Collections.reverse(localMedia);
            e.onNext(localMedia);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movieItemModels -> {
                    //???????????????????????????
                    setAdapterData(movieItemModels, isLoadMore);
                });
    }

    private void setAdapterData(ArrayList<MovieItemModel> localVideoBeans, boolean isLoadMore) {
        if (isLoadMore) {
            //??????????????????????????????
            if (localVideoBeans.size() >= count) {
                mLocalMediaAdapter.loadMoreComplete();
            } else {
                mLocalMediaAdapter.loadMoreEnd(false);
            }
            mLocalMediaAdapter.addData(localVideoBeans);
        } else {
        if (mLocalMediaAdapter == null) {
            mVaryViewHelperController.restore();
            mLocalMediaAdapter = new LocalMediaAdapter(R.layout.item_local_media, localVideoBeans, FileUtill.createSaveFile(mContext, MediaConfig.VIDEO_XH_RECORDER), mSelectVideoCount, mSelectVideoTime);
            mLocalMediaAdapter.bindToRecyclerView(mRecyclerView);
            mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 6));
            mRecyclerView.addItemDecoration(new DividerGridItemDecoration(mContext));
            mRecyclerView.setAdapter(mLocalMediaAdapter);

//        }
        } else {
            mLocalMediaAdapter.addData(localVideoBeans);
        }
        //????????????????????????count
        if (localVideoBeans.size() >= count) {
            mLocalMediaAdapter.setOnLoadMoreListener(this, mRecyclerView);
        }
    }
    }

    private ArrayList<MovieItemModel> getLocalMedia() {
        //???????????????????????????
        ArrayList<MovieItemModel> sysVideoList = new ArrayList<>();
        String[] thumbColumns = {MediaStore.Video.Thumbnails.DATA,
                MediaStore.Video.Thumbnails.VIDEO_ID};
//        // MediaStore.Video.Thumbnails.DATA:??????????????????????????????
//        // ?????????????????????????????????
        String[] mediaColumns = {MediaStore.Video.Media.DATA, MediaStore.Video.Media.DURATION, MediaStore.Video.Media._ID};
        //?????????????????? MediaStore.Video.Media.DURATION + ">0" ?????????0??????????????????
        String where = MediaStore.Images.Media.MIME_TYPE + "=? and " + MediaStore.Video.Media.DURATION + ">0";
        // ??????????????????????????????
        String[] whereArgs = {"video/mp4"};
//        ????????????,????????????count=24???,startIndex???0??????
        String orderString = MediaStore.Images.Media._ID + " ASC LIMIT " + count + " OFFSET " + startIndex * count;
        Cursor cursor = mContext.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, mediaColumns, where, whereArgs, orderString);
        if (cursor == null) {
            return sysVideoList;
        }
        if (cursor.moveToFirst()) {
            do {
                int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                int videoId = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                String filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                //???????????????mp4??????
                File file = new File(filePath);
                if (!file.exists()) {
                    continue;
                }

                //???????????????????????????
                String mediaHasAudio = IvyUtils.getMediaHasAudio(filePath);
                MovieItemModel info = new MovieItemModel();
                info.setFilePath(filePath);
                info.setVideoTime(BigDecimal.valueOf(duration).divide(BigDecimal.valueOf(1000), 1, BigDecimal.ROUND_DOWN));
                Cursor thumbCursor = mContext.getContentResolver().
                        query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                                thumbColumns, MediaStore.Video.Thumbnails.VIDEO_ID + "=" + videoId,
                                null, null);
                if (thumbCursor != null && thumbCursor.moveToFirst()) {
                    info.setFilPicPath(thumbCursor.getString(thumbCursor .getColumnIndex(MediaStore.Video.Thumbnails.DATA)));
                    thumbCursor.close();
                }
                info.setVideo_from(true);
                //?????????????????????
                sysVideoList.add(0,info);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return sysVideoList;
    }

    @SuppressLint("CheckResult")
    private void getOkVideo(String filePath, int duration, int videoId, ArrayList<MovieItemModel> sysVideoList){
        String[] thumbColumns = {MediaStore.Video.Thumbnails.DATA,
                MediaStore.Video.Thumbnails.VIDEO_ID};
        Observable.create((ObservableOnSubscribe<String>) e -> {
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    if (TextUtils.isEmpty(s)) {
                        return;
                    }

                    setAdapterData(sysVideoList, false);
                });
    }

    public boolean canChangeTab() {
        if (mLocalMediaAdapter != null && mLocalMediaAdapter.selectList != null){
            return mLocalMediaAdapter.selectList.size() == 0;
        }
        return true;
    }

    public ArrayList<MovieItemModel> getMovieItemList() {
        return mLocalMediaAdapter.selectList;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSubscribe != null && !mSubscribe.isDisposed()) {
            mSubscribe.dispose();
        }
    }

    @Override
    public void onLoadMoreRequested() {
        //???????????????????????????
        if (mLocalMediaAdapter != null) {
            startIndex ++;
            initData(true);
        }
    }
}
