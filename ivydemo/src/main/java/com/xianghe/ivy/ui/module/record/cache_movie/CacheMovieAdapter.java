package com.xianghe.ivy.ui.module.record.cache_movie;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.xianghe.ivy.R;
import com.xianghe.ivy.adapter.adapter.BaseQuickAdapter;
import com.xianghe.ivy.adapter.adapter.BaseViewHolder;
import com.xianghe.ivy.app.greendao.GreenDaoManager;
import com.xianghe.ivy.entity.dao.MovieItemDbDao;
import com.xianghe.ivy.entity.db.MovieItemDb;
import com.xianghe.ivy.ui.module.record.RecordActivity;
import com.xianghe.ivy.ui.module.record.model.MovieEditModel;
import com.xianghe.ivy.ui.module.record.model.MovieItemModel;
import com.xianghe.ivy.ui.module.videoedit.VideoEditActivity;
import com.xianghe.ivy.ui.module.videopush.VideoPushActivity;
import com.xianghe.ivy.utils.FileUtill;
import com.xianghe.ivy.weight.CustomProgress;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.xianghe.ivy.app.IvyConstants.VIDEO_EDIT;
import static com.xianghe.ivy.app.IvyConstants.VIDEO_PUSH;
import static com.xianghe.ivy.app.IvyConstants.VIDEO_RECORD;

import androidx.annotation.Nullable;

public class CacheMovieAdapter extends BaseQuickAdapter<ArrayList<MovieItemDb>,BaseViewHolder> implements BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.OnItemChildClickListener {

    private SimpleDateFormat mSimpleDateFormat;

    private MovieItemDbDao mMovieItemDbDao;

    private CustomProgress mCustomProgress;

    private CustomProgress mLoadingProgress;

    private Context mContext;

    public CacheMovieAdapter(int layoutResId, @Nullable List<ArrayList<MovieItemDb>> data, Context context) {
        super(layoutResId, data);
        mContext = context;
        setOnItemClickListener(this);
        setOnItemChildClickListener(this);

        mMovieItemDbDao = GreenDaoManager.getInstance().getMovieItemDao();
        mCustomProgress = new CustomProgress(mContext);
        mLoadingProgress = new CustomProgress(mContext);

        mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.getDefault());
    }

    @Override
    protected void convert(BaseViewHolder helper, ArrayList<MovieItemDb> item) {
        if (item.size()>0) {
            MovieItemDb movieItemDb = item.get(0);
            if (movieItemDb != null) {
                helper.setFileImage(mContext, R.id.iv_item_cache_movie_icon, movieItemDb.getFilePath())
                        .setText(R.id.tv_item_cache_movie_time, getCurrentMovieTime(movieItemDb))
                        .addOnClickListener(R.id.iv_item_cache_movie_delete);

                //显示时间
                int time = getCurrentMovieDuration(item);
                String minute;
                if (time / 60 > 0) {
                    if (time / 60 >= 10) {
                        minute = time / 60 + ":";
                    } else {
                        minute = "0" + time / 60 + ":";
                    }
                } else {
                    minute = "00:";
                }
                helper.setText(R.id.tv_item_cache_movie_duration, minute + (time % 60 >= 10 ? time % 60 : "0" + time % 60));
            }
        }
    }

    private int getCurrentMovieDuration(ArrayList<MovieItemDb> item) {
        //求总时间
        BigDecimal bigDecimal = BigDecimal.ZERO;
        //判断如果是发布界面的就显示最后一个item的时间
        MovieItemDb movieItem = item.get(item.size()-1);
        if (movieItem.getFrom() == VIDEO_PUSH){
            bigDecimal = bigDecimal.add(BigDecimal.valueOf(movieItem.getVideoTime()));
        }else{
            for (MovieItemDb movieItemDb:item){
                bigDecimal = bigDecimal.add(BigDecimal.valueOf(movieItemDb.getVideoTime()));
            }
        }
        return bigDecimal.intValue();
    }

    private String getCurrentMovieTime(MovieItemDb movieItemDb) {
        return mSimpleDateFormat.format(movieItemDb.getKey());
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        ArrayList<MovieItemDb> movieItemDbArrayList = mData.get(position);
        if (movieItemDbArrayList != null && movieItemDbArrayList.size()>0){
            //根据标记跳转界面
            MovieItemDb movieItemDb = movieItemDbArrayList.get(0);
            //跳转到录制界面
            Intent intent;
            if (movieItemDb.getFrom() == VIDEO_RECORD){
                intent = new Intent(mContext,RecordActivity.class);
                intent.putExtra("record_model",movieItemDbArrayList);
                intent.putExtra("from",RecordActivity.CACHE);
            }else if (movieItemDb.getFrom() == VIDEO_EDIT){
                intent = new Intent(mContext,VideoEditActivity.class);
                intent.putExtra("record_model",movieItemDbArrayList);
                intent.putExtra("edit_model",getEditMode(movieItemDbArrayList));
                intent.putExtra("from",RecordActivity.CACHE);
                intent.putExtra("pic",movieItemDbArrayList.get(0).getPicToMovie());
            }else{
                intent = new Intent(mContext,VideoPushActivity.class);
                intent.putExtra("record_model",movieItemDbArrayList.get(movieItemDbArrayList.size()-1));
//                Log.e("草稿箱合成视频后的文件=vieItemDb==", movieItemDbArrayList.get(movieItemDbArrayList.size()-1).getFilePath()+
//                        "===MovieItemDb=="+ getBeforeListModel(movieItemDbArrayList).get(0).getFilePath());
                intent.putExtra("list_model",getBeforeListModel(movieItemDbArrayList));
                intent.putExtra("from",RecordActivity.CACHE);
                intent.putExtra("pic",movieItemDbArrayList.get(0).getPicToMovie());
            }
            mContext.startActivity(intent);
        }
    }

    private ArrayList<MovieItemDb> getBeforeListModel(ArrayList<MovieItemDb> movieItemDbArrayList) {
        ArrayList<MovieItemDb> list = new ArrayList<>();
        for (int i = 0;i<movieItemDbArrayList.size()-1;i++){
            list.add(movieItemDbArrayList.get(i));
        }
        return list;
    }

    private MovieEditModel getEditMode(ArrayList<MovieItemDb> movieItemDbArrayList) {
        String videoFile = FileUtill.createSaveFile(mContext, FileUtill.getDefaultDir(mContext) + "/clip");
        if (movieItemDbArrayList != null && movieItemDbArrayList.size()>0) {
            ArrayList<MovieItemModel> movieItemModels = initRecordList(new ArrayList<>(), movieItemDbArrayList);
            return new MovieEditModel(videoFile, movieItemModels);
        }
        return new MovieEditModel(videoFile,new ArrayList<>());
    }

    private ArrayList<MovieItemModel> initRecordList(ArrayList<MovieItemModel> movieItemModels, ArrayList<MovieItemDb> movieItemDaoList) {
        if (movieItemDaoList != null && movieItemDaoList.size()>0){
            for (MovieItemDb movieItemDb:movieItemDaoList){
                movieItemModels.add(new MovieItemModel(movieItemDb.getFilePath(),BigDecimal.valueOf(movieItemDb.getVideoTime()),movieItemDb.getFilPicPath()));
            }
        }
        return movieItemModels;
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        //删除数据并且删除数据库数据
        ArrayList<MovieItemDb> movieItemDbs = mData.get(position);
        if (movieItemDbs != null && movieItemDbs.size()>0){
            removeCacheAndFile(adapter,movieItemDbs,position);
        }
    }

    private void removeCacheAndFile(BaseQuickAdapter adapter, ArrayList<MovieItemDb> movieItemDbs, int position){
        mCustomProgress.show(mContext.getString(R.string.cache_movie_delete_tip), mContext.getString(R.string.common_tips_title),
                mContext.getString(R.string.common_ensure), mContext.getString(R.string.common_cancel), v -> {
                    mCustomProgress.dismiss();
                    mLoadingProgress.show(mContext.getString(R.string.clearing),false,null);
                    clearFile(adapter,movieItemDbs,position);
                }, v -> mCustomProgress.dismiss()
                ,false,null);
    }

    public void removeAll(){
        mCustomProgress.show(mContext.getString(R.string.cache_movie_delete_tip), mContext.getString(R.string.common_tips_title),
                mContext.getString(R.string.common_ensure), mContext.getString(R.string.common_cancel), v -> {
                    mCustomProgress.dismiss();
                    mLoadingProgress.show(mContext.getString(R.string.clearing),false,null);
                    clearAllFile((ArrayList<ArrayList<MovieItemDb>>) mData);
                }, v -> mCustomProgress.dismiss()
                ,false,null);
    }

    @SuppressLint("CheckResult")
    private void clearFile(BaseQuickAdapter adapter,ArrayList<MovieItemDb> movieItemDbs,int position){
        Observable.create(e -> {
            if (mMovieItemDbDao != null){
                mMovieItemDbDao.deleteInTx(movieItemDbs);
            }
            deleteFile(movieItemDbs);
            e.onNext(null);
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(o -> {
            dismissLoading();
            adapter.remove(position);
        }, throwable -> {
            dismissLoading();
            adapter.remove(position);
        });
    }

    @SuppressLint("CheckResult")
    private void clearAllFile(ArrayList<ArrayList<MovieItemDb>> movieItemDbs){
        Observable.create(e -> {
            if (movieItemDbs != null && movieItemDbs.size()>0){
                for (ArrayList<MovieItemDb> movieItemDbArrayList:movieItemDbs){
                    if (mMovieItemDbDao != null){
                        mMovieItemDbDao.deleteInTx(movieItemDbArrayList);
                    }

                    deleteFile(movieItemDbArrayList);
                }
            }
            e.onNext(null);
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(o -> {
            dismissLoading();
            setNewData(new ArrayList<>());
        }, throwable -> {
            dismissLoading();
            setNewData(new ArrayList<>());
        });
    }


    private void dismissLoading() {
        if (mLoadingProgress != null){
            mLoadingProgress.dismiss();
        }
    }

    private void deleteFile(ArrayList<MovieItemDb> movieItemDbs) {
        if (movieItemDbs != null && movieItemDbs.size()>0) {
            //不删除本地视频
            for (MovieItemDb movieItemDb : movieItemDbs) {
                if (!movieItemDb.getVideo_from()){
                    String filePath = movieItemDb.getFilePath();
                    if (!TextUtils.isEmpty(filePath)) {
                        File file = new File(filePath);
                        if (file.exists()) {
                            file.delete();
                        }
                    }

                    if (!movieItemDb.getPicToMovie()){
                        String filPicPath = movieItemDb.getFilPicPath();
                        if (!TextUtils.isEmpty(filPicPath)) {
                            File picFile = new File(filPicPath);
                            if (picFile.exists()) {
                                picFile.delete();
                            }
                        }
                    }
                }
            }
        }
    }
}
