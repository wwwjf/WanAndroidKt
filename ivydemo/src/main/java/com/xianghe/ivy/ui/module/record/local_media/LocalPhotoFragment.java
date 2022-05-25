package com.xianghe.ivy.ui.module.record.local_media;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xianghe.ivy.R;
import com.xianghe.ivy.mvp.BaseFragment;
import com.xianghe.ivy.ui.module.record.model.MovieItemModel;
import com.xianghe.ivy.weight.loading.VaryViewHelperController;
import com.xianghe.ivy.weight.photo_select.widget.DividerGridItemDecoration;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import icepick.State;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LocalPhotoFragment extends BaseFragment {

    @BindView(R.id.rv_fragment_local_media_recyclerView)
    RecyclerView mRecyclerView;

    private VaryViewHelperController mVaryViewHelperController;

    private Disposable mSubscribe;

    private LocalPhotoAdapter mLocalPhotoAdapter;

    private TextView mHeaderFirstText;

    //创建六个集合
    @State
    ArrayList<MovieItemModel> firstList;

    @State
    ArrayList<MovieItemModel> secondList;

    @State
    ArrayList<MovieItemModel> thirdList;

    @State
    ArrayList<MovieItemModel> fourthList;

    @State
    ArrayList<MovieItemModel> fifthList;

    @State
    ArrayList<MovieItemModel> sixthList;

    private SimpleDateFormat simpleDateFormat;

    private RecyclerView mMiddleRecyclerView;

    @State
    ArrayList<ArrayList<MovieItemModel>> mArrayLists;

    @Override
    protected int getChildView() {
        return R.layout.fragment_recyclerview;
    }

    @Override
    protected void initChildData(Bundle savedInstanceState) {
        mVaryViewHelperController = new VaryViewHelperController(mRecyclerView);

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        initData();
    }

    @SuppressLint("CheckResult")
    private void initData() {
        //判断是否完成了加载
        mSubscribe = Observable.create((ObservableOnSubscribe<ArrayList<MovieItemModel>>) e -> {
            ArrayList<MovieItemModel> localMedia = getLocalMedia();
            e.onNext(localMedia);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setAdapterData);
    }

    private void setAdapterData(ArrayList<MovieItemModel> localVideoBeans) {
        mVaryViewHelperController.restore();
        mLocalPhotoAdapter = new LocalPhotoAdapter(R.layout.item_local_photo, localVideoBeans);
        mLocalPhotoAdapter.bindToRecyclerView(mRecyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 6));
        mRecyclerView.addItemDecoration(new DividerGridItemDecoration(mContext));

        mRecyclerView.setAdapter(mLocalPhotoAdapter);

        //添加头部
        View HeadView = View.inflate(mContext, R.layout.item_header_photo, null);
        findHeaderView(HeadView);
        //添加头部
        mLocalPhotoAdapter.addHeaderView(HeadView);
    }

    private void findHeaderView(View headView) {
        mHeaderFirstText = headView.findViewById(R.id.tv_item_header_photo_first_text);
        mMiddleRecyclerView = headView.findViewById(R.id.rv_item_header_photo_recyclerView);
        //添加数据
        setCountText();

        setRecommendLayout();
    }

    private void setRecommendLayout() {
        //判断集合数量
        LocalMiddleAdapter localMiddleAdapter = new LocalMiddleAdapter(R.layout.view_item_photo_middle_layout, mArrayLists, mContext);

        mMiddleRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 6));

        mMiddleRecyclerView.addItemDecoration(new DividerGridItemDecoration(mContext));

        mMiddleRecyclerView.setAdapter(localMiddleAdapter);
    }

    private void setCountText() {
        if (mHeaderFirstText != null) {
            mHeaderFirstText.setText(String.format(getString(R.string.local_photo_movie), mArrayLists.size() + ""));
        }
    }

    private ArrayList<MovieItemModel> getLocalMedia() {
        //获取所有的图片
        String[] projection = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_TAKEN,
        };
        ArrayList<MovieItemModel> sysVideoList = new ArrayList<>();
        String where = MediaStore.Images.Media.MIME_TYPE + "=? or "
                + MediaStore.Images.Media.MIME_TYPE + "=? or "
                + MediaStore.Images.Media.MIME_TYPE + "=?";
        String[] whereArgs = {"image/jpeg", "image/png", "image/jpg"};
        // 视频其他信息的查询条件
        Cursor cursor = mContext.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, where, whereArgs, null);
        if (cursor == null) {
            return sysVideoList;
        }
        if (cursor.moveToFirst()) {
            do {
                Long time = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_TAKEN));
                String filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                //判断是否是mp4文件
                File file = new File(filePath);
                if (!file.exists()) {
                    continue;
                }

                MovieItemModel info = new MovieItemModel();
                info.setFilePath(filePath);
                info.setDate(time);
                //判断是否有图片
                sysVideoList.add(info);
                //判断时间是哪一天的
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        Collections.sort(sysVideoList, new Comparator<MovieItemModel>() {
            @Override
            public int compare(MovieItemModel o1, MovieItemModel o2) {
                if (o1.getDate()>o2.getDate()){
                    return -1;
                }
                if (o1.getDate()==o2.getDate()){
                    return 0;
                }
                return 1;
            }
        });
        mArrayLists = addToRecommend(sysVideoList);
        return sysVideoList;
    }

    private ArrayList<ArrayList<MovieItemModel>> addToRecommend(ArrayList<MovieItemModel> movieItemModels) {
        ArrayList<ArrayList<MovieItemModel>> totalList = new ArrayList<>();
        Map<String, ArrayList<MovieItemModel>> skuIdMap = new HashMap<>();
        for (MovieItemModel movieItemModel : movieItemModels) {
            ArrayList<MovieItemModel> tempList = skuIdMap.get(simpleDateFormat.format(movieItemModel.getDate())); /*如果取不到数据,那么直接new一个空的ArrayList**/
            if (tempList == null && totalList.size()<6) {
                tempList = new ArrayList<>();
                totalList.add(tempList);
                tempList.add(movieItemModel);
                skuIdMap.put(simpleDateFormat.format(movieItemModel.getDate()), tempList);
            } else { /*某个sku之前已经存放过了,则直接追加数据到原来的List里**/
                //小于十个
                if (tempList != null){
                    if (tempList.size() <10){
                        tempList.add(movieItemModel);
                    }
                }
            }
        }
        return totalList;
    }


    public boolean canChangeTab() {
        if (mLocalPhotoAdapter != null && mLocalPhotoAdapter.selectList != null) {
            return mLocalPhotoAdapter.selectList.size() == 0;
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSubscribe != null && !mSubscribe.isDisposed()) {
            mSubscribe.dispose();
        }
    }

    public List<String> getSelectedImgs() {
        ArrayList<String> imgs = new ArrayList<>();
        ArrayList<MovieItemModel> selectList = mLocalPhotoAdapter.selectList;
        if (selectList != null && selectList.size() > 0) {
            for (MovieItemModel model : selectList) {
                imgs.add(model.getFilePath());
            }
        }
        return imgs;
    }
}
