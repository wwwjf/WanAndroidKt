package com.xianghe.ivy.ui.module.videocalledit;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wwwjf.base.utils.ToastUtil;
import com.xianghe.ivy.R;
import com.xianghe.ivy.app.greendao.GreenDaoManager;
import com.xianghe.ivy.constant.RxBusCode;
import com.xianghe.ivy.entity.dao.MovieItemDbDao;
import com.xianghe.ivy.entity.db.MovieItemDb;
import com.xianghe.ivy.mvp.BaseVideoCallActivity;
import com.xianghe.ivy.ui.media.base.IMediaPlayer;
import com.xianghe.ivy.ui.module.record.adapter.CustomItemTouchCallBack;
import com.xianghe.ivy.ui.module.record.adapter.MovieShowListAdapter;
import com.xianghe.ivy.ui.module.record.model.MovieEditModel;
import com.xianghe.ivy.ui.module.record.model.MovieItemModel;
import com.xianghe.ivy.ui.module.videocall.service.FileUtil;
import com.xianghe.ivy.ui.module.videoedit.VideoEditActivity;
import com.xianghe.ivy.ui.module.videoedit.VideoEditPlayUtils;
import com.xianghe.ivy.utils.KLog;
import com.xianghe.ivy.weight.CustomProgress;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gorden.rxbus2.Subscribe;
import gorden.rxbus2.ThreadMode;

/**
 * 视频聊天时的录屏数据
 */
public class VideoCallRecordActivity extends BaseVideoCallActivity
        implements MovieShowListAdapter.onDeleteListener {

    public static final String TAG = VideoCallRecordActivity.class.getSimpleName();
    public static final String INTENT_KEY_ADAPTER_MODEL = "adapterList";
    public static final String INTENT_KEY_DB_MODEL = "dbList";
    public static final String INTENT_KEY_MOVIE_EDIT_MODEL = "recordEditList";
    public static final int VIDEO_CALL_RECORD = 3;
    @BindView(R.id.vcr_iv_back)
    ImageView mIvBack;
    @BindView(R.id.vcr_tv_save)
    TextView mTvSave;
    @BindView(R.id.vcr_tv_publish)
    TextView mTvPublish;
    @BindView(R.id.vcr_rv_record_list)
    RecyclerView mRvRecordList;
    @BindView(R.id.vcr_surfaceView)
    SurfaceView mSurfaceView;

    private MovieItemDbDao mMovieItemDao;
    private ArrayList<MovieItemModel> mMovieItemModelList;
    private ArrayList<MovieItemDb> mMovieItemDaoList;
    private MovieShowListAdapter mMovieShowListAdapter;
    /**
     * 传给发布编辑页处理的数据
     */
    private MovieEditModel mMovieEditModel;
    private VideoEditPlayUtils mVideoEditPlayUtils;
    private double mTotalTime;
    /**
     * 退出时是否需要删除数据录屏记录
     */
    private boolean mIsIgnoreDeleteData;

    private CustomItemTouchCallBack.TouchStatus mTouchStatus;
    /**
     * 在被剪辑，置位，删除之后都需要更新上一个页面的数据
     */
    private boolean needRefreshLastData = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call_record);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        mMovieItemModelList = (ArrayList<MovieItemModel>) intent.getSerializableExtra(INTENT_KEY_ADAPTER_MODEL);
        mMovieItemDaoList = (ArrayList<MovieItemDb>) intent.getSerializableExtra(INTENT_KEY_DB_MODEL);
        intent.getSerializableExtra(INTENT_KEY_MOVIE_EDIT_MODEL);
        mMovieItemDao = GreenDaoManager.getInstance().getMovieItemDao();

        if (mMovieItemModelList == null) {
            mMovieItemModelList = new ArrayList<>();
        }
        if (mMovieItemDaoList == null){
            mMovieItemDaoList = new ArrayList<>();
        }
        mMovieEditModel = new MovieEditModel(null, mMovieItemModelList);
        mVideoEditPlayUtils = new VideoEditPlayUtils(mSurfaceView, mMovieEditModel,listener);
        if (mCustomProgress == null) {
            mCustomProgress = new CustomProgress(this);
        }
        initRecordAdapter();
        mTotalTime = calculateRecordTime(mMovieShowListAdapter.getData());
        KLog.e("----mTotalTime="+mTotalTime);
        if (mTotalTime < 5){
            mTvPublish.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 计算视频总时长
     * @param movieItemModelList
     * @return
     */
    private double calculateRecordTime(List<MovieItemModel> movieItemModelList) {
        BigDecimal totalBigDecimal = new BigDecimal(BigInteger.ZERO);
        for (MovieItemModel movieItemModel : movieItemModelList) {
            totalBigDecimal = totalBigDecimal.add(movieItemModel.getVideoTime());
        }
        return totalBigDecimal.doubleValue();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mVideoEditPlayUtils != null) {
            mVideoEditPlayUtils.resume();
        }
    }

    private IMediaPlayer.OnPlayNextListener listener = new IMediaPlayer.OnPlayNextListener() {
        @Override
        public void onNextPlay(int position) {
            if (mTouchStatus != CustomItemTouchCallBack.TouchStatus.DOWN){
                if (mMovieShowListAdapter.getData().size() == 0){
                    return;
                }
                mMovieShowListAdapter.notifyAll(position);
            }
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mVideoEditPlayUtils != null) {
            mVideoEditPlayUtils.onSaveInstanceState(outState);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (mVideoEditPlayUtils != null) {
            mVideoEditPlayUtils.onRestoreInstanceState(savedInstanceState);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoEditPlayUtils != null) {
            mVideoEditPlayUtils.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVideoEditPlayUtils != null) {
            mVideoEditPlayUtils.destroy();
        }

    }

    @OnClick({R.id.vcr_iv_back, R.id.vcr_tv_save, R.id.vcr_tv_publish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.vcr_iv_back:
                exitEditRecord();
                break;
            case R.id.vcr_tv_save:
                mIsIgnoreDeleteData = true;
                //删除数据库记录
                mMovieItemDao.deleteInTx(mMovieItemDaoList);
                showToast(getString(R.string.save_success));
                finish();
                break;
            case R.id.vcr_tv_publish:
                mMovieEditModel = new MovieEditModel(null,mMovieShowListAdapter.getData());
                Intent intent = new Intent(VideoCallRecordActivity.this,VideoEditActivity.class);
                intent.putExtra("edit_model",mMovieEditModel);
                intent.putExtra("record_model",mMovieItemDaoList);
                intent.putExtra("from",VIDEO_CALL_RECORD);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    /**
     * 退出提示
     */
    private void exitEditRecord() {
        if (mIsIgnoreDeleteData){
            finish();
            return;
        }
        mCustomProgress.show(getString(R.string.screen_record_back),
                getString(R.string.common_tips_title),
                getString(R.string.common_cancel),
                getString(R.string.common_confirm),
                v -> mCustomProgress.cancel(),
                v -> {
                    deleteAll();
                    //关闭页面
                    finish();
                }, false, null);
    }


    private void showToast(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        ToastUtil.show(VideoCallRecordActivity.this, msg);
    }

    private void initRecordAdapter() {
        mMovieShowListAdapter = new MovieShowListAdapter(R.layout.adapter_movie_show_list, mMovieItemModelList,true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRvRecordList.setLayoutManager(linearLayoutManager);
        mRvRecordList.setAdapter(mMovieShowListAdapter);
        CustomItemTouchCallBack callBack = new CustomItemTouchCallBack(mMovieShowListAdapter.getData(), mMovieShowListAdapter, this);
        callBack.setListener(() -> {
            needRefreshLastData = true;
            if (mVideoEditPlayUtils != null) {
                mVideoEditPlayUtils.resetAllPlayer(); // 重播音视频
            }
        });
        callBack.setOnTouchStatuListener(state -> mTouchStatus = state);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callBack);
        itemTouchHelper.attachToRecyclerView(mRvRecordList);

        mMovieShowListAdapter.setDeleteListener(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            exitEditRecord();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void deleteAll() {
        if (mMovieShowListAdapter != null && mMovieItemDaoList != null){
            //删除数据库记录
            mMovieItemDao.deleteInTx(mMovieItemDaoList);
            //删除本地文件
            for (MovieItemDb movieItemDb : mMovieItemDaoList) {
                if (movieItemDb != null &&
                        !TextUtils.isEmpty(movieItemDb.getFilePath())&&
                        !TextUtils.isEmpty(movieItemDb.getFilPicPath())) {
                    File file = new File(movieItemDb.getFilePath());
                    File picFile = new File(movieItemDb.getFilPicPath());
                    String filePath = file.getAbsolutePath();
                    String picFilePath = picFile.getAbsolutePath();
                    if (file.exists()) {
                        file.delete();
                    }

                    if (picFile.exists()) {
                        picFile.delete();
                    }
                    FileUtil.fileDeleteScanVideo(VideoCallRecordActivity.this, filePath);
                    FileUtil.fileDeleteScanImage(VideoCallRecordActivity.this,picFilePath);
                }
            }
        }
    }

    @Override
    public void deleteListener(int position) {
        // 删除数据库录屏记录，
        // 删除本地录屏视频，
        // 删除本地录屏封面图片

        //删除当前的缓存集合数据以及删除数据库数据
        if (mMovieShowListAdapter != null && mMovieItemDaoList != null && mMovieItemDaoList.size() > position) {

            MovieItemDb movieItemDb = mMovieItemDaoList.get(position);
            //移除数据库
            mMovieItemDao.delete(movieItemDb);
            mMovieShowListAdapter.deleteItem(position);
            mMovieItemDaoList.remove(movieItemDb);

            //计算视频总时长
            mTotalTime = calculateRecordTime(mMovieShowListAdapter.getData());
            if (mTotalTime < 5){
                mTvPublish.setVisibility(View.INVISIBLE);
            }
            //删除文件
            if (movieItemDb != null && !TextUtils.isEmpty(movieItemDb.getFilePath())) {
                File file = new File(movieItemDb.getFilePath());
                File picFile = new File(movieItemDb.getFilPicPath());
                String filePath = file.getAbsolutePath();
                String picFilePath = picFile.getAbsolutePath();
                if (file.exists()) {
                    file.delete();
                }

                if (picFile.exists()) {
                    picFile.delete();
                }
                FileUtil.fileDeleteScanVideo(VideoCallRecordActivity.this,filePath);
                FileUtil.fileDeleteScanImage(VideoCallRecordActivity.this,picFilePath);
            }
        }

        needRefreshLastData = true;
        if (mVideoEditPlayUtils != null) {
            mVideoEditPlayUtils.resetAllPlayer(); // 重播音视频
        }
        if (mMovieShowListAdapter != null && mMovieShowListAdapter.getData().size() == 0){
            finish();
        }
    }

    @Subscribe(code = RxBusCode.ACT_FINISH_VIDEO_EDIT, threadMode = ThreadMode.MAIN)
    public void actFinish(String isFinish) {
        if ("true".equals(isFinish) && !this.isFinishing()) {
            //删除文件
            deleteAll();
            finish();
        }
    }

    @Subscribe(code = RxBusCode.ACT_VIDEO_RECORD_REFRESH_BY_EDIT, threadMode = ThreadMode.MAIN)
    public void actRefreshAdapter(Intent intent) {
        if (mMovieShowListAdapter != null && intent.hasExtra("edit_model")) {
            Serializable data = intent.getSerializableExtra("edit_model");
            if (data instanceof MovieEditModel) {
                List<MovieItemModel> movieItemModels = ((MovieEditModel) data).getMovieItemModels();
                if (movieItemModels.size()==0){
                    //视频编辑页面已删除所有视频片段，直接关闭
                    finish();
                    return;
                }
                mMovieItemModelList.clear();
                mMovieItemModelList.addAll(movieItemModels);
                mMovieShowListAdapter.setNewData(movieItemModels);
                mTotalTime = calculateRecordTime(mMovieShowListAdapter.getData());
                if (mTotalTime < 5){
                    mTvPublish.setVisibility(View.INVISIBLE);
                    finish();
                    return;
                }
            }
        }

        if (intent.hasExtra("record_model")){
            Serializable data = intent.getSerializableExtra("record_model");
            if (data instanceof ArrayList){
                mMovieItemDaoList.clear();
                mMovieItemDaoList.addAll((ArrayList<MovieItemDb>) data);
                if (mMovieItemDaoList.size()==0) {
                    //视频编辑页面已删除所有视频片段，直接关闭
                    finish();
                    return;
                }
                for (MovieItemDb movieItemDb : mMovieItemDaoList) {
                    movieItemDb.setMusicPath("");
                    movieItemDb.setMusicName("");
                    movieItemDb.setMusicId("");
                    movieItemDb.setVoicePath("");
                    //修改数据库
                    mMovieItemDao.update(movieItemDb);
                }
            }
        }
    }

    @Subscribe(code = RxBusCode.ACT_VIDEO_EDIT_CACHE, threadMode = ThreadMode.MAIN)
    public void actSaveVideoAdapter() {
        finish();
    }

    @Subscribe(code = RxBusCode.ACT_VIDEO_PUSH_REFRESH_CACHE, threadMode = ThreadMode.MAIN)
    public void actPushSaveVideoAdapter() {
        //视频发布 保存到草稿后关闭页面
        finish();
    }



}
