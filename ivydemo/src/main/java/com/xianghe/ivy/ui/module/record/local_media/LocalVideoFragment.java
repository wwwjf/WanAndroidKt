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
                    //判断是否完成了加载
                    setAdapterData(movieItemModels, isLoadMore);
                });
    }

    private void setAdapterData(ArrayList<MovieItemModel> localVideoBeans, boolean isLoadMore) {
        if (isLoadMore) {
            //判断微信的是否解析完
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
        //判断视频是否大于count
        if (localVideoBeans.size() >= count) {
            mLocalMediaAdapter.setOnLoadMoreListener(this, mRecyclerView);
        }
    }
    }

    private ArrayList<MovieItemModel> getLocalMedia() {
        //获取所有的视频文件
        ArrayList<MovieItemModel> sysVideoList = new ArrayList<>();
        String[] thumbColumns = {MediaStore.Video.Thumbnails.DATA,
                MediaStore.Video.Thumbnails.VIDEO_ID};
//        // MediaStore.Video.Thumbnails.DATA:视频缩略图的文件路径
//        // 视频其他信息的查询条件
        String[] mediaColumns = {MediaStore.Video.Media.DATA, MediaStore.Video.Media.DURATION, MediaStore.Video.Media._ID};
        //指定查询条件 MediaStore.Video.Media.DURATION + ">0" 过滤掉0秒的无用视频
        String where = MediaStore.Images.Media.MIME_TYPE + "=? and " + MediaStore.Video.Media.DURATION + ">0";
        // 指定查询条件中的参数
        String[] whereArgs = {"video/mp4"};
//        分页查询,每页显示count=24条,startIndex从0开始
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
                //判断是否是mp4文件
                File file = new File(filePath);
                if (!file.exists()) {
                    continue;
                }

                //判断文件是否有音轨
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
                //判断是否有图片
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
        //判断是否有加载更多
        if (mLocalMediaAdapter != null) {
            startIndex ++;
            initData(true);
        }
    }
}
