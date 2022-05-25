package com.xianghe.ivy.ui.module.record.cache_movie;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xianghe.ivy.R;
import com.xianghe.ivy.app.greendao.GreenDaoManager;
import com.xianghe.ivy.constant.RxBusCode;
import com.xianghe.ivy.entity.dao.MovieItemDbDao;
import com.xianghe.ivy.entity.db.MovieItemDb;
import com.xianghe.ivy.manager.UserInfoManager;
import com.xianghe.ivy.mvp.BaseActivity;
import com.xianghe.ivy.mvp.BaseVideoCallActivity;
import com.xianghe.ivy.weight.loading.VaryViewHelperController;
import com.xianghe.ivy.weight.photo_select.widget.DividerGridItemDecoration;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import gorden.rxbus2.Subscribe;
import gorden.rxbus2.ThreadMode;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CacheMovieActivity extends BaseVideoCallActivity {

    @BindView(R.id.rv_activity_cache_movie_recyclerView)
    RecyclerView mRecyclerView;

    private MovieItemDbDao mMovieItemDbDao;

    private VaryViewHelperController mVaryViewHelperController;

    private Disposable mSubscribe;

    private Unbinder mBind;

    private CacheMovieAdapter mCacheMovieAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache_movie);

        mBind = ButterKnife.bind(this);
        mVaryViewHelperController = new VaryViewHelperController(mRecyclerView);
        mVaryViewHelperController.showLoading();

        mMovieItemDbDao = GreenDaoManager.getInstance().getMovieItemDao();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @SuppressLint("CheckResult")
    private void initData() {
        mSubscribe = Observable.create((ObservableOnSubscribe<ArrayList<ArrayList<MovieItemDb>>>) e -> {
            ArrayList<ArrayList<MovieItemDb>> arrayLists = handlerData();
            e.onNext(arrayLists);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(arrayLists -> {
                    mVaryViewHelperController.restore();
                    setRecyclerViewData(arrayLists);
                }, throwable -> mVaryViewHelperController.showNetworkError(v -> initData()));
    }

    private void setRecyclerViewData(ArrayList<ArrayList<MovieItemDb>> arrayLists) {
        if (arrayLists == null){
            arrayLists = new ArrayList<>();
        }
        if(mCacheMovieAdapter == null){
            mCacheMovieAdapter = new CacheMovieAdapter(R.layout.adapter_cache_movie,arrayLists,this);
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 6));
            mRecyclerView.addItemDecoration(new DividerGridItemDecoration(this));
            mRecyclerView.setAdapter(mCacheMovieAdapter);
            mCacheMovieAdapter.setEmptyView(LayoutInflater.from(this).inflate(R.layout.view_pager_no_data, (ViewGroup) mRecyclerView.getParent(), false));
        }else{
            mCacheMovieAdapter.setNewData(arrayLists);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mSubscribe != null && !mSubscribe.isDisposed()){
            mSubscribe.dispose();
        }

        if (mBind != null){
            mBind.unbind();
        }
    }

    private ArrayList<ArrayList<MovieItemDb>> handlerData() {
        String uid = String.valueOf(UserInfoManager.getUid());
        //查询数据
        List<MovieItemDb> list = mMovieItemDbDao.queryBuilder()
                .orderDesc(MovieItemDbDao.Properties.Key)
                .where(MovieItemDbDao.Properties.Uid.eq(uid)).list();

        ArrayList<ArrayList<MovieItemDb>> cacheTotalList = new ArrayList<>();
        ArrayList<MovieItemDb> movieItemDbs = new ArrayList<>();

        LongSparseArray<List<MovieItemDb>> skuIdMap = new LongSparseArray<>();
        for (MovieItemDb movieItemDb : list) {
            List<MovieItemDb> tempList = skuIdMap.get(movieItemDb.getKey()); /*如果取不到数据,那么直接new一个空的ArrayList**/
            if (tempList == null) {
                tempList = new ArrayList<>();
                tempList.add(movieItemDb);
                skuIdMap.put(movieItemDb.getKey(), tempList);
            } else { /*某个sku之前已经存放过了,则直接追加数据到原来的List里**/
                tempList.add(movieItemDb);
            }
        }

        //遍历添加到集合中
        for (int j = 0; j < skuIdMap.size(); j++) {
            List<MovieItemDb> itemDbList = skuIdMap.get(skuIdMap.keyAt(j));
            //判断文件是否存在，如果不存在就在数据库中删除改条目
            for (int k = 0;k<itemDbList.size();k++){
                MovieItemDb movieItemDb = itemDbList.get(k);
                if (!TextUtils.isEmpty(movieItemDb.getFilePath())){
                    if (new File(movieItemDb.getFilePath()).exists()){
                        movieItemDbs.add(movieItemDb);
                    }else{
                        //移除该草稿
                        removeItemCache(movieItemDb);
                    }
                }else{
                    //移除该草稿
                    removeItemCache(movieItemDb);
                }
            }
            if (movieItemDbs.size() >0){
                cacheTotalList.add(0,movieItemDbs);
                movieItemDbs = new ArrayList<>();
            }
        }

        return cacheTotalList;
    }

    private void removeItemCache(MovieItemDb movieItemDb){
        if (mMovieItemDbDao != null) {
            if (movieItemDb!= null && !movieItemDb.isPicToMovie()){
                //删除封面图
                if (!TextUtils.isEmpty(movieItemDb.getFilPicPath())) {
                    File file = new File(movieItemDb.getFilPicPath());
                    if (file.exists()) {
                        file.delete();
                    }
                    mMovieItemDbDao.delete(movieItemDb);
                }
            }
        }
    }

    @OnClick({R.id.iv_activity_cache_movie_back,R.id.tv_activity_cache_movie_clear})
    void onViewClicked(View view){
        switch (view.getId()){
            case R.id.iv_activity_cache_movie_back:
                if (mCacheMovieAdapter != null && mCacheMovieAdapter.getData().size()==0){
                    setResult(RESULT_OK,getIntent().putExtra("isClear",true));
                }
                finish();
                break;
            case R.id.tv_activity_cache_movie_clear:
                if (mCacheMovieAdapter != null){
                    mCacheMovieAdapter.removeAll();
                }
                break;
        }
    }

    @Subscribe(code = RxBusCode.ACT_FINISH_VIDEO_EDIT, threadMode = ThreadMode.MAIN)
    public void actFinish(String isFinish) {
        if ("true".equals(isFinish) && !this.isFinishing()) {
            this.finish();
        }
    }
}
