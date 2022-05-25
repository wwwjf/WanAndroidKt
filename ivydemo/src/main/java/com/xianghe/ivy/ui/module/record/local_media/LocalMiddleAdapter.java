package com.xianghe.ivy.ui.module.record.local_media;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.tangyx.video.ffmpeg.FFmpegRun;
import com.xianghe.ivy.R;
import com.xianghe.ivy.adapter.adapter.BaseQuickAdapter;
import com.xianghe.ivy.adapter.adapter.BaseViewHolder;
import com.xianghe.ivy.app.greendao.GreenDaoManager;
import com.xianghe.ivy.entity.dao.MovieItemDbDao;
import com.xianghe.ivy.entity.db.MovieItemDb;
import com.xianghe.ivy.manager.UserInfoManager;
import com.xianghe.ivy.model.ImageBean;
import com.xianghe.ivy.ui.module.record.model.MovieEditModel;
import com.xianghe.ivy.ui.module.record.model.MovieItemModel;
import com.xianghe.ivy.ui.module.videoedit.VideoEditActivity;
import com.xianghe.ivy.utils.FileUtill;
import com.xianghe.ivy.utils.IvyUtils;
import com.xianghe.ivy.weight.CustomProgress;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.xianghe.ivy.app.IvyConstants.VIDEO_EDIT;
import static com.xianghe.ivy.model.ImageBean.NORMAL;
import static com.xianghe.ivy.ui.module.record.RecordActivity.LOCAL;

import androidx.annotation.Nullable;

public class LocalMiddleAdapter extends BaseQuickAdapter<ArrayList<MovieItemModel>,BaseViewHolder> implements BaseQuickAdapter.OnItemClickListener {

    private SimpleDateFormat simpleDateFormat;

    private CustomProgress mCustomProgress;

    private Context mContext;

    public LocalMiddleAdapter(int layoutResId, @Nullable List<ArrayList<MovieItemModel>> data, Context context) {
        super(layoutResId, data);
        setOnItemClickListener(this);
        mContext = context;

        mCustomProgress = new CustomProgress(context);

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
    }

    @Override
    protected void convert(BaseViewHolder helper, ArrayList<MovieItemModel> item) {
        helper.setFileImage(mContext,R.id.iv_view_item_local_photo_icon,item.get(0).getFilePath())
                .setText(R.id.tv_view_item_local_photo_time,simpleDateFormat.format(item.get(0).getDate())+"");
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        //跳转到编辑页面
        ArrayList<String> imgList = new ArrayList<>();
        for (MovieItemModel movieItemModel : mData.get(position)){
            imgList.add(movieItemModel.getFilePath());
        }
        producePic(imgList);
    }

    @SuppressLint("CheckResult")
    private void producePic(ArrayList<String> imags) {
        mCustomProgress.show(mContext.getString(R.string.loading), false, null);
        Observable.create((ObservableOnSubscribe<File>) e -> {
            ImageBean imageBean = new ImageBean();
            String videoFile = FileUtill.createSaveFile(mContext,FileUtill.getDefaultDir(mContext)+"/img");
            final String mererVideoPath = videoFile + "/" + String.format("img-output-%s.mp4", System.currentTimeMillis() + "");
            File file = new File(mererVideoPath);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            imageBean.setOutputPath(file.getAbsolutePath());
            imageBean.setImagePathList(imags);
            imageBean.setOutputType(NORMAL);
            FFmpegRun.setImageToNativeForEncodeMP4(imageBean);
            e.onNext(file);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    String videoClipFile = FileUtill.createSaveFile(mContext,FileUtill.getDefaultDir(mContext)+"/clip");
                    if (mCustomProgress != null){
                        mCustomProgress.dismiss();
                    }
                    MovieItemDbDao movieItemDao = GreenDaoManager.getInstance().getMovieItemDao();
                    ArrayList<MovieItemDb> dbList = new ArrayList<>();
                    ArrayList<MovieItemModel> list = new ArrayList<>();
                    MovieItemDb movieItemDb = new MovieItemDb();
                    movieItemDb.setKey(System.currentTimeMillis());
                    movieItemDb.setFilPicPath(imags.get(0));
                    movieItemDb.setFilePath(o.getAbsolutePath());
                    movieItemDb.setVideoTime(Double.parseDouble(IvyUtils.getMediaDuration(o.getAbsolutePath()))/1000);
                    movieItemDb.setUid(UserInfoManager.getUid()+"");
                    movieItemDb.setPicToMovie(true);
                    movieItemDb.setFrom(VIDEO_EDIT);
                    StringBuilder imgCache = new StringBuilder();
                    for (int i = 0;i<imags.size();i++){
                        imgCache.append(imags);
                        if (i != imags.size()-1){
                            imgCache.append(",");
                        }
                    }
                    movieItemDb.setPicList(imgCache.toString());
                    dbList.add(movieItemDb);


                    MovieItemModel movieItemModel = new MovieItemModel(o.getAbsolutePath(),BigDecimal.valueOf(Double.parseDouble(IvyUtils.getMediaDuration(o.getAbsolutePath()))/1000),imags.get(0));
                    list.add(movieItemModel);
                    Intent intent = new Intent(mContext, VideoEditActivity.class);
                    intent.putExtra("pic_res",imags);
                    intent.putExtra("from",LOCAL);
                    intent.putExtra("pic",true);
                    intent.putExtra("edit_model", new MovieEditModel(videoClipFile, list));
                    intent.putExtra("record_model",dbList);
                    movieItemDao.insertInTx(dbList);
                    mContext.startActivity(intent);
                }, throwable -> {
                    if (mCustomProgress != null){
                        mCustomProgress.dismiss();
                    }
                });

    }
}
