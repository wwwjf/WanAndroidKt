package com.xianghe.ivy.ui.module.record;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
import static com.tencent.rtmp.TXLiveConstants.RENDER_ROTATION_PORTRAIT;
import static com.tencent.rtmp.TXLiveConstants.VIDEO_ANGLE_HOME_LEFT;
import static com.tencent.rtmp.TXLiveConstants.VIDEO_ANGLE_HOME_RIGHT;
import static com.xianghe.ivy.app.IvyConstants.VIDEO_EDIT;
import static com.xianghe.ivy.app.IvyConstants.VIDEO_RECORD;
import static com.xianghe.ivy.ui.module.preview_movie.PreviewMovieActivity.INTENTBACKCODE;
import static com.xianghe.ivy.ui.module.preview_movie.PreviewMovieActivity.LOCAL_CODE;
import static com.xianghe.ivy.ui.module.preview_movie.PreviewMovieActivity.VIDEO_PATH;
import static com.xianghe.ivy.ui.module.preview_movie.PreviewMovieActivity.VIDEO_POSITION;
import static com.xianghe.ivy.ui.module.preview_movie.PreviewMovieActivity.VIDEO_TIME;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tencent.rtmp.ui.TXCloudVideoView;
import com.tencent.ugc.TXRecordCommon;
import com.tencent.ugc.TXUGCRecord;
import com.xianghe.ivy.R;
import com.xianghe.ivy.app.greendao.GreenDaoManager;
import com.xianghe.ivy.constant.RxBusCode;
import com.xianghe.ivy.entity.dao.MovieItemDbDao;
import com.xianghe.ivy.entity.db.MovieItemDb;
import com.xianghe.ivy.manager.NoDoubleClickListenerManager;
import com.xianghe.ivy.manager.PermissionManager;
import com.xianghe.ivy.manager.UserInfoManager;
import com.xianghe.ivy.mvp.BaseVideoCallActivity;
import com.xianghe.ivy.ui.module.record.adapter.CustomItemTouchCallBack;
import com.xianghe.ivy.ui.module.record.adapter.MovieShowListAdapter;
import com.xianghe.ivy.ui.module.record.cache_movie.CacheMovieActivity;
import com.xianghe.ivy.ui.module.record.local_media.LocalMediaActivity;
import com.xianghe.ivy.ui.module.record.model.MovieEditModel;
import com.xianghe.ivy.ui.module.record.model.MovieItemModel;
import com.xianghe.ivy.ui.module.videoedit.VideoEditActivity;
import com.xianghe.ivy.ui.module.welcom.CustomVideoView;
import com.xianghe.ivy.utils.ConUtil;
import com.xianghe.ivy.utils.FileUtill;
import com.xianghe.ivy.utils.KLog;
import com.xianghe.ivy.utils.ToastUtil;
import com.xianghe.ivy.utils.VideoHelper;
import com.xianghe.ivy.weight.CircleProgressBar;
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
import butterknife.Unbinder;
import gorden.rxbus2.Subscribe;
import gorden.rxbus2.ThreadMode;
import icepick.State;
import io.reactivex.disposables.Disposable;

@SuppressWarnings({"unchecked", "ResultOfMethodCallIgnored"})
public class RecordActivity extends BaseVideoCallActivity implements MovieShowListAdapter.onDeleteListener {
    private static final String TAG = "RecordActivity";

    @BindView(R.id.activity_shoot_surface_view)
    TXCloudVideoView mSurfaceView;
    @BindView(R.id.iv_activity_shoot_shoot)
    ImageView ivShootShoot;                                                                         //点击录制的按钮
    @BindView(R.id.iv_activity_shoot_back)
    ImageView ivShootBack;                                                                          //返回的按钮
    @BindView(R.id.iv_activity_shoot_cancel)
    ImageView ivShootCancel;                                                                        //取消录制的按钮
    @BindView(R.id.rv_activity_shoot_recyclerView)
    RecyclerView rvShootRecyclerView;
    @BindView(R.id.vpb_activity_shoot_progress)
    CircleProgressBar rpbShootProgress;                                                             //记录时间的progress
    @BindView(R.id.iv_activity_shoot_record)
    ImageView ivShootRecord;                                                                        //正在录制的按钮
    @BindView(R.id.fl_activity_shoot_record_layout)
    FrameLayout flShootRecordLayout;                                                                //录制的布局
    @BindView(R.id.tv_activity_shoot_next)
    TextView tvShootNext;                                                                           //下一步的按钮
    @BindView(R.id.iv_activity_change_camera)
    ImageView ivShootCamera;                                                                        //拍照按钮
    @BindView(R.id.tv_activity_shoot_record_time)
    TextView tvShootRecordTime;                                                                     //记录时间的text
    @BindView(R.id.tv_activity_shoot_filter_type)
    TextView mFilterTypeText;                                                                       //滤镜类型
    @BindView(R.id.view_activity_shoot_recyclerView_bg)
    View mRecyclerBg;
    @BindView(R.id.tv_activity_shoot_local)
    TextView mLocalText;                                                                            //本地上传按钮
    @BindView(R.id.rl_activity_shoot_root_view)
    RelativeLayout mShootLayout;                                                                    //录制的基类布局
    @BindView(R.id.tv_activity_shoot_cache)
    TextView mCacheText;                                                                            //缓存的按钮
    @BindView(R.id.tv_activity_shoot_save)
    TextView mSaveCacheText;                                                                        //存为草稿

    private ArrayList<MovieItemDb> mMovieItemDaoList;

    private MovieItemDbDao mMovieItemDbDao;

    private Unbinder mBind;

    private MovieShowListAdapter mMovieShowListAdapter;

    private Handler mHandler;

    private AlbumOrientationEventListener mAlbumOrientationEventListener;

    private boolean isFrontCamera = false;

    private CustomProgress mCustomProgress;

    private final double mRecordTotalTime = 180;

    private boolean isRecording;                                                                    //是否正在录制

    private String mVideoFile;                                                                      //视频文件夹

    private TXUGCRecord mTXCameraRecord;

    private final TXRecordCommon.TXUGCCustomConfig mCustomConfig = new TXRecordCommon.TXUGCCustomConfig();

    private double currentTime;

    private CustomGestureDetectorListener mCustomGestureDetectorListener;

    @State
    boolean needRestPreview = true;

    @State
    int mOrientation;

    @State
    long timeKey;

    private RecordPermissionListener mRecordPermissionListener;

    private final String PERMISSIONTAG = "PERMISSIONTAG";

    @State
    boolean isFirstRecord = true;

    @State
    boolean hasChangeRecord = true;

    @State
    String mFileName;

    @State
    int mFrom;

    private boolean isVertical;

    private boolean isHorizontal = true;

    private VideoFramePicListener mVideoFramePicListener;

    private Disposable mImageForVideoAsync;

    public static final int LOCAL = 1;

    public static final int CACHE = 2;
    private CustomItemTouchCallBack mCallBack;
    private boolean goToCache;
    private boolean isCancelRecord;
    private String picPath;
    private Context context;

    private TextView tv_shoot_guidance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_record);

        initData();

        initView();

        initListener();

        initAdapter();

        context = this;
        mRecordPermissionListener = new RecordPermissionListener();
        mTXCameraRecord = TXUGCRecord.getInstance(this.getApplicationContext());
        mCustomGestureDetectorListener = new CustomGestureDetectorListener(this, mFilterTypeText, mTXCameraRecord);
        actRefreshAdapter(getIntent());
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        refreshClipAdapter(intent);
    }

    private void initTimeKey() {
        if (timeKey == 0) {
            timeKey = System.currentTimeMillis();
        }
    }

    private void initListener() {
        ivShootCancel.setOnClickListener(new NoDoubleClickListener());
        ivShootCamera.setOnClickListener(new NoDoubleClickListener());
        ivShootShoot.setOnClickListener(new NoDoubleClickListener());
        ivShootRecord.setOnClickListener(new NoDoubleClickListener());
        mLocalText.setOnClickListener(new NoDoubleClickListener());
        mCacheText.setOnClickListener(new NoDoubleClickListener());
        mSaveCacheText.setOnClickListener(new NoDoubleClickListener());
    }

    private void initData() {
        mBind = ButterKnife.bind(this);

        mHandler = new Handler(getMainLooper());

        mCustomProgress = new CustomProgress(this);

        mAlbumOrientationEventListener = new AlbumOrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL);

//        mVideoFile = FileUtill.createSaveFile(this, FileUtill.getDefaultDir(this) + "/clip");
        mVideoFile = getExternalMediaDirs()[0].getAbsolutePath();
        FileUtill.createSaveFile(this,"TXUGC");

        mMovieItemDbDao = GreenDaoManager.getInstance().getMovieItemDao();
    }

    private void checkOrientation() {
        if (mAlbumOrientationEventListener.canDetectOrientation()) {
            mAlbumOrientationEventListener.enable();
        }
    }

    private void initAdapter() {
        //判断是否是从本地过来的
        ArrayList<MovieItemModel> movieItemModels = new ArrayList<>();
        mFrom = getIntent().getIntExtra("from", 0);
        mFileName = getIntent().getStringExtra("extra");
        mMovieItemDaoList = new ArrayList<>();
        if (mFrom == LOCAL) {
            Serializable data = getIntent().getSerializableExtra("data");
            if (data instanceof ArrayList) {
                movieItemModels.addAll((ArrayList) data);
                //添加到草稿箱中
                initLocalDaoList(movieItemModels);
            }
        } else if (mFrom == CACHE) {
            Serializable record_model = getIntent().getSerializableExtra("record_model");
            if (record_model instanceof ArrayList) {
                mMovieItemDaoList = (ArrayList<MovieItemDb>) record_model;
                initRecordList(movieItemModels, mMovieItemDaoList);
            }
        }

        //判断是否有数据有数据就让下一步显示。本地隐藏
        if (movieItemModels.size() > 0) {
            //修改界面
            timeKey = mMovieItemDaoList.get(0).getKey();
            if (mCacheText != null && mCacheText.getVisibility() == View.VISIBLE) {
                mCacheText.setVisibility(View.GONE);
            }

            if (tvShootNext != null && tvShootNext.getVisibility() == View.GONE) {
                tvShootNext.setVisibility(View.VISIBLE);
            }
        }

        if (mMovieShowListAdapter == null) {
            mMovieShowListAdapter = new MovieShowListAdapter(R.layout.adapter_movie_show_list, movieItemModels, tvShootNext, mLocalText, mCacheText, mSaveCacheText, mVideoFile, false);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            rvShootRecyclerView.setLayoutManager(linearLayoutManager);
            rvShootRecyclerView.setAdapter(mMovieShowListAdapter);
            mCallBack = new CustomItemTouchCallBack(mMovieShowListAdapter.getData(), mMovieShowListAdapter, this);
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mCallBack);
            itemTouchHelper.attachToRecyclerView(rvShootRecyclerView);
            mMovieShowListAdapter.setDeleteListener(this);
        } else {
            mMovieShowListAdapter.notifyDataSetChanged();
        }
    }

    private void initLocalDaoList(ArrayList<MovieItemModel> movieItemModels) {
        initTimeKey();
        for (MovieItemModel movieItemModel : movieItemModels) {
            MovieItemDb movieItemDb = new MovieItemDb();
            movieItemDb.setUid(UserInfoManager.getUid() + "");
            movieItemDb.setFrom(VIDEO_RECORD);
            movieItemDb.setKey(timeKey);
            movieItemDb.setRotate(movieItemModel.getRotate());
            movieItemDb.setVideoTime(movieItemModel.getVideoTime().doubleValue());
            movieItemDb.setFilePath(movieItemModel.getFilePath());
            movieItemDb.setFilPicPath(movieItemModel.getFilPicPath());
            movieItemDb.setVideo_from(true);
            mMovieItemDaoList.add(movieItemDb);
        }
        //添加到数据库
        mMovieItemDbDao.insertOrReplaceInTx(mMovieItemDaoList);
    }

    private void initRecordList(ArrayList<MovieItemModel> movieItemModels, ArrayList<MovieItemDb> movieItemDaoList) {
        if (movieItemDaoList != null && movieItemDaoList.size() > 0) {
            for (MovieItemDb movieItemDb : movieItemDaoList) {
                movieItemModels.add(new MovieItemModel(movieItemDb.getFilePath(), BigDecimal.valueOf(movieItemDb.getVideoTime()), movieItemDb.getFilPicPath(), movieItemDb.getVideo_from()));
            }
        }
    }

    private void initView() {
        tv_shoot_guidance = findViewById(R.id.tv_activity_shoot_guidance);
        tv_shoot_guidance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomizeDialog();
            }
        });
        mSurfaceView.enableHardwareDecode(true);
    }

    @Override
    protected void onStart() {
        goToCache = false;
        //请求权限
        if (mPermissionManager != null)
            mPermissionManager.requestPermissions(this, mRecordPermissionListener, new String[]{
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.RECORD_AUDIO
            }, PERMISSIONTAG, true);
        super.onStart();
    }

    private void startCamera() {
        checkOrientation();
        //判断方向
        if (mOrientation == 270 || mOrientation == 0) {
            if (getRequestedOrientation() != SCREEN_ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
            startCameraPreview(VIDEO_ANGLE_HOME_RIGHT);
        } else {
            if (getRequestedOrientation() != SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
            }
            startCameraPreview(VIDEO_ANGLE_HOME_LEFT);
        }
        needRestPreview = false;
    }

    @Override
    public void acceptDialogCallBack() {
        // 接受了视频通话就需要存草稿
        stopRecordAndRefreshStats();
    }


    @Override
    protected void onStop() {

        stopCameraPreView();

        super.onStop();
    }


    private void startCameraPreview(int orientation) {
        mTXCameraRecord.setHomeOrientation(orientation);
        mTXCameraRecord.setRenderRotation(RENDER_ROTATION_PORTRAIT);
        mTXCameraRecord.setVideoRecordListener(recordListener);

        mCustomConfig.videoBitrate = 4800;// 600 ~ 4800
        mCustomConfig.videoResolution = TXRecordCommon.VIDEO_RESOLUTION_720_1280;
        mCustomConfig.isFront = isFrontCamera;
        mCustomConfig.maxDuration = 9999000;
        mCustomConfig.videoFps = 20;
        mCustomConfig.videoGop = 1;
        mCustomConfig.needEdit = true;
        mTXCameraRecord.startCameraCustomPreview(mCustomConfig, mSurfaceView);
        mTXCameraRecord.setZoom(1);
        mTXCameraRecord.setRecordSpeed(TXRecordCommon.RECORD_SPEED_NORMAL);//录制速度
        mTXCameraRecord.setAspectRatio(TXRecordCommon.VIDEO_ASPECT_RATIO_9_16);//视频比例
        //        style - 美颜风格.三种美颜风格：0 ：光滑 1：自然 2：朦胧
        //        beautyDepth - : 美颜级别取值范围 0 ~ 9； 0 表示关闭 1 ~ 9值越大 效果越明显。
        //        whiteningDepth - : 美白级别取值范围 0 ~ 9； 0 表示关闭 1 ~ 9值越大 效果越明显。
        //        ruddyDepth - : 红润级别取值范围 0 ~ 9； 0 表示关闭 1 ~ 9值越大 效果越明显。
        mTXCameraRecord.setBeautyDepth(1, 5, 6, 0);
        mTXCameraRecord.setFaceScaleLevel(0);//设置瘦脸效果
        mTXCameraRecord.setEyeScaleLevel(0);//设置大眼效果
        mTXCameraRecord.setFaceShortLevel(0);
        mTXCameraRecord.setFaceVLevel(0);
        mTXCameraRecord.setChinLevel(0);
        mTXCameraRecord.setNoseSlimLevel(0);
    }


    private void stopCameraPreView() {
        mTXCameraRecord.stopCameraPreview();
        //判断是否有手势
        mCustomGestureDetectorListener.resetZoom();
        //判断时间
        mAlbumOrientationEventListener.disable();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mFilterTypeText != null && mTXCameraRecord != null) {
            if (event.getPointerCount() == 2) {
                mCustomGestureDetectorListener.attach(event);
            }

            //判断是否是在录制
            if (event.getPointerCount() == 1 && !isRecording) {
                mCustomGestureDetectorListener.onGestureAttach(event);
            }
        }
        return super.onTouchEvent(event);
    }


    @Override
    protected void onDestroy() {
        if (mBind != null) {
            mBind.unbind();
        }
        if (mCustomProgress != null) {
            mCustomProgress.dismiss();
        }

        if (mAlbumOrientationEventListener != null) {
            mAlbumOrientationEventListener.disable();
            mAlbumOrientationEventListener = null;
        }
        if (mTXCameraRecord != null) {
            mTXCameraRecord.stopCameraPreview();
            mTXCameraRecord.stopBGM();
            mTXCameraRecord.release();
        }

        if (mImageForVideoAsync != null && !mImageForVideoAsync.isDisposed()) {
            mImageForVideoAsync.dispose();
        }
        super.onDestroy();
    }

    @OnClick({R.id.tv_activity_shoot_next, R.id.iv_activity_shoot_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_activity_shoot_next:
                //判断时间是否大于5秒
                next();
                break;
            case R.id.iv_activity_shoot_back:                                                       //返回按钮
                //判断是否有录制
                showBackNotice();
                break;
        }
    }

    private void changeCamera() {
        isFrontCamera = !isFrontCamera;
        if (mTXCameraRecord != null) {
            mTXCameraRecord.switchCamera(isFrontCamera);
        }
    }

    /**
     * 滑动到最后一个item
     */
    private void moveRecyclerToPosition() {
        // 第一个可见位置
        if (rvShootRecyclerView != null) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) rvShootRecyclerView.getLayoutManager();
            if (layoutManager != null) {
                // 最后一个可见位置
                int lastItem = layoutManager.findLastVisibleItemPosition();
                //判断最后一个view是否小于可见的view
                if (lastItem != -1) {
                    if (mMovieShowListAdapter.getData().size() >= lastItem) {
                        // 如果跳转位置在第一个可见位置之前，就smoothScrollToPosition可以直接跳转
                        rvShootRecyclerView.smoothScrollToPosition(mMovieShowListAdapter.getData().size() - 1);
                    }
                }
            }
        }
    }

    private void setCacheStatus(int status) {
        //判断是不是从缓存过来的遍历集合改变标记
        if (mMovieItemDaoList != null && mMovieItemDaoList.size() > 0) {
            for (MovieItemDb movieItemDb : mMovieItemDaoList) {
//                movieItemDb.setMusicPath("");
//                movieItemDb.setMusicName("");
//                movieItemDb.setMusicId("");
//                movieItemDb.setVoicePath("");
                movieItemDb.setFrom(status);
                //修改数据库
                mMovieItemDbDao.update(movieItemDb);
            }
        }
    }


    /**
     * 拍摄完成下一步
     */
    private void next() {
        if (recordTotalTime() >= 5) {
            //保存状态
            setCacheStatus(VIDEO_EDIT);
            Intent intent = new Intent(this, VideoEditActivity.class);
            intent.putExtra("edit_model", new MovieEditModel(mVideoFile, mMovieShowListAdapter.getData()));
            intent.putExtra("record_model", mMovieItemDaoList);
            startActivity(intent);
        } else {
            if (mCustomProgress != null) {
                mCustomProgress.show("", v -> mCustomProgress.dismiss(), true, dialog -> mCustomProgress.dismiss(), mHandler);
            }
        }
    }

    /**
     * 退出提示
     */
    private void showBackNotice() {
        //判断是不是草稿箱进来的
        //判断是不是本地进来的
        if (isRecording)
            stopRecordAndRefreshStats();
        Log.e("拍摄页的mFrom==", mFrom + "");
        if (mFrom == LOCAL) {
            //判断额外的文件在不在
            if (!TextUtils.isEmpty(mFileName)) {
                File file = new File(mFileName);
                if (file.exists()) {
                    FileUtill.deleteFile(file);
                }
            }
            showBackDialog(getString(R.string.record_abandon_movie));
        } else if (mFrom == CACHE) {
            setCacheStatus(VIDEO_RECORD);
            finish();
        } else {
            showBackDialog(getString(R.string.record_abandon_movie));
        }
    }

    private void showBackDialog(String content) {
        if (mMovieShowListAdapter != null) {
            if (mMovieShowListAdapter.getData().size() > 0) {
                if (mCustomProgress != null) {
                    mCustomProgress.showLeftEnsure(content, getString(R.string.common_tips_title), getResources().getString(R.string.immediate_exit), getResources().getString(R.string.video_push_cache),
                            v -> {
                                mCustomProgress.dismiss();
                                finish();
                            }, v -> {
                                mCustomProgress.dismiss();
                                finish();
                                //删除数据库数据
                                if (mMovieItemDbDao != null) {
                                    mMovieItemDbDao.deleteInTx(mMovieItemDaoList);
                                }
                                //删除文件
                                deleteCacheFile(mMovieItemDaoList, true, false);
                            }, true, dialog -> mCustomProgress.dismiss());
                }
            } else {
                finish();
            }
        } else {
            finish();
        }
    }


    private double recordTotalTime() {
        BigDecimal totalBigDecimal = new BigDecimal(BigInteger.ZERO);
        //遍历集合算总时间
        if (mMovieShowListAdapter != null) {
            List<MovieItemModel> movieItemModels = mMovieShowListAdapter.getData();
            if (movieItemModels.size() > 0) {
                for (int i = 0; i < movieItemModels.size(); i++) {
                    totalBigDecimal = totalBigDecimal.add(movieItemModels.get(i).getVideoTime());
                }
                return totalBigDecimal.doubleValue();
            } else {
                return 0;
            }
        }
        return 0;
    }

    private boolean recordMovieMaximize() {
        //判断总时间如果是180秒就提示
        //判断是否小于50段
        if (hasFinishSize() && recordTotalTime() < mRecordTotalTime) {
            return true;
        } else {
            if (mCustomProgress != null) {
                mCustomProgress.show(getString(R.string.record_dialog_record_publish), v -> mCustomProgress.dismiss(), false, null, mHandler);
            }
            return false;
        }
    }

    private boolean hasFinishSize() {
        if (mMovieShowListAdapter != null) {
            return mMovieShowListAdapter.getData().size() < 50;
        }
        return false;
    }

    /**
     * 文字出现隐藏动画
     */
    private void setTimeAnimation(View showView, int animRes, int visibility) {
        showView.setVisibility(visibility);
        Animation animation = AnimationUtils.loadAnimation(this, animRes);
        showView.startAnimation(animation);
    }

    @Override
    public void onBackPressed() {
        showBackNotice();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //处理剪辑回来的视频
        if (resultCode == INTENTBACKCODE && 111 == requestCode) {
            refreshAdapter(data);
        }
        if (requestCode == 1 && resultCode == LOCAL_CODE) {
            refreshClipAdapter(data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void refreshClipAdapter(@Nullable Intent data) {
        if (data != null) {
            mFrom = data.getIntExtra("from", 0);
            mFileName = getIntent().getStringExtra("extra");
            Serializable serializable = data.getSerializableExtra("data");
            if (serializable instanceof ArrayList) {
                ArrayList<MovieItemModel> movieItemModels = new ArrayList<>((ArrayList) serializable);
                //添加到草稿箱中
                initLocalDaoList(movieItemModels);
                mMovieShowListAdapter.addData(movieItemModels);

                //判断是否有数据有数据就让下一步显示。本地隐藏
                if (movieItemModels.size() > 0) {
                    //修改界面
                    if (mLocalText != null && mLocalText.getVisibility() == View.GONE) {
                        mLocalText.setVisibility(View.VISIBLE);
                    }
                    if (mCacheText != null && mCacheText.getVisibility() == View.VISIBLE) {
                        mCacheText.setVisibility(View.GONE);
                    }

                    if (tvShootNext != null && tvShootNext.getVisibility() == View.GONE) {
                        tvShootNext.setVisibility(View.VISIBLE);
                    }

                    if (mSaveCacheText.getVisibility() == View.GONE) {
                        mSaveCacheText.setVisibility(View.VISIBLE);
                    }
                }

                if (mCallBack != null) {
                    mCallBack.setDatas(mMovieShowListAdapter.getData());
                }
            }
        }
    }

    private void refreshAdapter(Intent data) {
        if (data != null) {
            //刷新数据
            int position = data.getIntExtra(VIDEO_POSITION, -1);
            BigDecimal videoTime = (BigDecimal) data.getSerializableExtra(VIDEO_TIME);
            String video_path = data.getStringExtra(VIDEO_PATH);
            if (mMovieShowListAdapter != null && position >= 0) {
                initTimeKey();

                //判断是否需要修改key
                changeCacheKey();

                //修改当前界面显示的数据
                changeUiVideoData(position, videoTime, video_path);
            }
        }
    }

    private void changeCacheData(int position, BigDecimal videoTime, String video_path) {
        MovieItemDb movieItemDb = mMovieItemDaoList.get(position);
        if (movieItemDb != null) {
            movieItemDb.setFilePath(video_path);
            movieItemDb.setVideoTime(videoTime.doubleValue());

            //修改数据库中数据
            mMovieItemDbDao.update(movieItemDb);
        }
    }

    private void changeUiVideoData(int position, BigDecimal videoTime, String video_path) {
        MovieItemModel movieItemModel = mMovieShowListAdapter.getData().get(position);
        //获取原来的文件地址删除
        String filePath = movieItemModel.getFilePath();
        if (!TextUtils.isEmpty(filePath)) {
            //判断地址是不是一样的是一样的就不删除
            if (!filePath.endsWith(video_path)) {
                //是本地视频就不能删文件
                if (!movieItemModel.isVideo_from()) {
                    File file = new File(filePath);
                    if (file.exists()) {
                        file.delete();
                    }
                }

                //添加新的地址
                movieItemModel.setVideoTime(videoTime);
                movieItemModel.setFilePath(video_path);
                mMovieShowListAdapter.notifyItemChanged(position);

                //修改数据库的数据
                changeCacheData(position, videoTime, video_path);
            }
        }
    }

    private void recordProgress(long time) {
        KLog.d(TAG, "record time: " + time);
        currentTime = formatTime(time);
        tvShootRecordTime.setText(String.format(getString(R.string.common_second), currentTime + ""));
        rpbShootProgress.setProgress((int) Math.ceil(currentTime));
        if (currentTime >= BigDecimal.valueOf(mRecordTotalTime).subtract(BigDecimal.valueOf(recordTotalTime())).doubleValue()) {
            stopRecordAndRefreshStats();//总时间超过180S  停止录像
        } else {
            if (currentTime > 0.2)
                ivShootCancel.setEnabled(true);
            if (currentTime > 1) {
                ivShootRecord.setEnabled(true);
            }
        }

    }

    private double formatTime(long time) {
        return BigDecimal.valueOf((time - TXUGCRecord.getInstance(this).getPartsManager().getDuration()) / 1000f).setScale(1, BigDecimal.ROUND_DOWN).doubleValue();
    }

    private void recordSuccessSaveFile(String lastFilePath) {
        MovieItemModel movieItemModel = new MovieItemModel(lastFilePath, BigDecimal.valueOf(currentTime));
        if (picPath != null) {
            refreshRecordList(movieItemModel, picPath);
            picPath = null;
        } else {
            if (mVideoFramePicListener == null) {
                mVideoFramePicListener = new VideoFramePicListener(lastFilePath, movieItemModel);
            } else {
                mVideoFramePicListener.setFilePathAndMovieItemModel(movieItemModel, lastFilePath);
            }
            mImageForVideoAsync = VideoHelper.getImageForVideoAsync(this, mVideoFile, lastFilePath, mVideoFramePicListener, System.currentTimeMillis());
        }
    }

    @Override
    public void deleteListener(int position) {
        //删除当前的缓存集合数据以及删除数据库数据
        if (mMovieShowListAdapter != null && mMovieItemDaoList != null && mMovieItemDaoList.size() > position) {
            initTimeKey();

            changeCacheKey();

            MovieItemDb movieItemDb = mMovieItemDaoList.get(position);
            //移除数据库
            mMovieItemDbDao.delete(movieItemDb);
            mMovieShowListAdapter.deleteItem(position);
            mMovieItemDaoList.remove(movieItemDb);
            //删除文件
            //判断是不是本地的
            deleteCacheFile(movieItemDb, true, true);
        }
    }

    /*
     * 录制回调
     */
    private final TXRecordCommon.ITXVideoRecordListener recordListener = new TXRecordCommon.ITXVideoRecordListener() {

        @Override
        public void onRecordEvent(int event, Bundle bundle) {//录屏事件回调
            KLog.e(TAG, "record event onRecordEvent: " + event);
            switch (event) {
                case TXRecordCommon.EVT_ID_PAUSE://不处理，在RecordComplete中处理
                    break;
                case TXRecordCommon.EVT_ID_RESUME://没有做多段录制，无此事件
                    break;
                case TXRecordCommon.EVT_CAMERA_CANNOT_USE://打开之前做判断了，不会有此事件
                    break;
                case TXRecordCommon.EVT_MIC_CANNOT_USE://打开之前做判断了，不会有此事件
                    break;

            }
//            if (event == TXRecordCommon.EVT_CAMERA_CANNOT_USE) {
//                //让按钮不可点击
//                ivShootCamera.setEnabled(false);
//                ivShootShoot.setEnabled(false);
//                mLocalText.setEnabled(false);
//                mCacheText.setEnabled(false);
//                ToastUtil.showToastCenter(context, getResources().getString(R.string.common_camera_no_permission));
//            } else if (event == TXRecordCommon.EVT_MIC_CANNOT_USE) {
//                //让按钮不可点击
//                ivShootCamera.setEnabled(false);
//                ivShootShoot.setEnabled(false);
//                mLocalText.setEnabled(false);
//                mCacheText.setEnabled(false);
//                ToastUtil.showToastCenter(context, "麦克风无法使用");
//            }
        }

        @Override
        public void onRecordProgress(long time) {
            KLog.d(TAG, "record progress:" + time + "   isRecording" + isRecording);
            if (isRecording) {
                recordProgress(time);
            }
        }

        @Override
        public void onRecordComplete(TXRecordCommon.TXRecordResult txRecordResult) {
            KLog.d(TAG, "record complete");
            if (txRecordResult.retCode >= 0) {
                // 录制成功， 视频文件在result.videoPath中,
                mTXCameraRecord.getPartsManager().deleteAllParts();
                if (isCancelRecord) {
                    File file = new File(txRecordResult.videoPath);
                    if (file.exists()) {
                        file.delete();
                    }
                    isCancelRecord = false;
                } else {
                    recordSuccessSaveFile(txRecordResult.videoPath);
                }
            } else {
                // 错误处理，错误码定义请参见TXRecordCommon中“录制结果回调错误码定义”
                //TODO
                ToastUtil.showToast(context, "录制失败");
            }
            recordStopedToRefreshView();
            if (mCallBack != null) {
                mCallBack.setDatas(mMovieShowListAdapter.getData());
            }
        }
    };

    private void startRecordAndRefreshStats() {
        if (isRecording) {
            return;
        }
        mTXCameraRecord = TXUGCRecord.getInstance(getApplicationContext());
        if (!recordMovieMaximize()) {
            return;
        }
        long currentTime = System.currentTimeMillis();
        String pathVideo = mVideoFile + File.separator + "VID_" + currentTime + ".mp4";
        String pathPic = mVideoFile + File.separator + "IMG_" + currentTime + ".jpg";
        int result = mTXCameraRecord.startRecord(pathVideo, null);//cover图片基本获取不了，所以下方用截图接口获取
        if (result != TXRecordCommon.START_RECORD_OK) {
            if (result == TXRecordCommon.START_RECORD_ERR_NOT_INIT) {
                ToastUtil.showToastCenter(this, getString(R.string.record_err_not_init));
            } else if (result == TXRecordCommon.START_RECORD_ERR_IS_IN_RECORDING) {
                ToastUtil.showToastCenter(this, getString(R.string.record_err_is_in_recording));
            } else if (result == TXRecordCommon.START_RECORD_ERR_VIDEO_PATH_IS_EMPTY) {
                ToastUtil.showToastCenter(this, getString(R.string.record_err_video_path_is_empty));
            } else if (result == TXRecordCommon.START_RECORD_ERR_API_IS_LOWER_THAN_18) {
                ToastUtil.showToastCenter(this, getString(R.string.record_err_is_lower_than_18));
            }
            return;
        }
        isRecording = true;
        //开始录制就让他不可用
        ivShootShoot.setVisibility(View.GONE);
        //开始录制让recycler不能操作
        mRecyclerBg.setVisibility(View.VISIBLE);
        mTXCameraRecord.snapshot(bitmap -> {
            if (ConUtil.saveBitmap(bitmap, pathPic)) {
                picPath = pathPic;
            }
        });
        //重置时间
        tvShootRecordTime.setText(String.format(getString(R.string.common_second), "0.0"));
        //标记正在录制
        rpbShootProgress.setMax(BigDecimal.valueOf(180).subtract(BigDecimal.valueOf(recordTotalTime())).intValue());
        rpbShootProgress.setProgress(0);

        ivShootRecord.setEnabled(false);//在录制进度大于1s的时候才可点击停止，确保录制时长大于1S
        flShootRecordLayout.setVisibility(View.VISIBLE);
        setTimeAnimation(tvShootRecordTime, R.anim.anim_translate_in, View.VISIBLE);
        ivShootBack.setVisibility(View.GONE);
        ivShootCancel.setEnabled(false);
        ivShootCancel.setVisibility(View.VISIBLE);
        tvShootNext.setVisibility(View.GONE);
        ivShootCamera.setVisibility(View.GONE);
        mSaveCacheText.setVisibility(View.GONE);
        mLocalText.setVisibility(View.GONE);
        mCacheText.setVisibility(View.GONE);

    }


    /*
     * 停止录像并刷新界面
     */
    private void stopRecordAndRefreshStats() {
        if (isRecording) {
            isRecording = false;
            mTXCameraRecord.stopRecord();
        }
    }

    /*
     * 录像停止返回结果后，刷新界面
     */
    private void recordStopedToRefreshView() {
        //选择和录制的视频总时长为0的时候，显示 草稿箱,否则不显示
        //选择和录制总时长大于0的时候，显示 存为草稿
        if (recordTotalTime() <= 0) {
            mCacheText.setVisibility(View.VISIBLE);
            mSaveCacheText.setVisibility(View.GONE);
        } else {
            mCacheText.setVisibility(View.GONE);
            mSaveCacheText.setVisibility(View.VISIBLE);
            tvShootNext.setVisibility(View.VISIBLE);
        }
        mRecyclerBg.setVisibility(View.GONE);
        ivShootBack.setVisibility(View.VISIBLE);
        ivShootCancel.setVisibility(View.GONE);

        //如果没有达到最大视频上线，显示本地视频 按钮
        mLocalText.setVisibility(View.VISIBLE);
        //隐藏 停止录制按钮和录制进度
        flShootRecordLayout.setVisibility(View.GONE);
        //显示 开始录制按钮
        ivShootShoot.setVisibility(View.VISIBLE);
        setTimeAnimation(tvShootRecordTime, R.anim.anim_translate_out, View.GONE);
        ivShootCamera.setVisibility(View.VISIBLE);
    }


    private class NoDoubleClickListener extends NoDoubleClickListenerManager {

        @Override
        public void onNoDoubleClick(View v) {
            switch (v.getId()) {
                case R.id.iv_activity_shoot_cancel:
                    stopRecordAndRefreshStats();
                    isCancelRecord = true;
//                    showLocalBtn(recordTotalTime() <= 0, true);
//                    stopRecord(recordTotalTime() > 0, true, true);
                    break;
                case R.id.iv_activity_shoot_record://stop
                    stopRecordAndRefreshStats();
//                    //添加视频到列表中
//                    //获取背景图添加到集合中
//                    //让录制按钮不可用
//                    ivShootCancel.setEnabled(false);
//                    ivShootShoot.setEnabled(false);
//                    showLocalBtn(false, true);
//                    stopRecord(true, false, false);
                    break;
                case R.id.iv_activity_shoot_shoot://start
                    ivShootShoot.setEnabled(false);//防止短时间内重复点击
                    startRecordAndRefreshStats();
                    ivShootShoot.setEnabled(true);
//                if (recordMovieMaximize()) {
//                    showLocalBtn(false, false);
//                    //让取消先不能点击
//                    ivShootCancel.setEnabled(false);
//                    startRecord();
//                } else {
//                    showLocalBtn(false, true);
//                    mSaveCacheText.setVisibility(View.VISIBLE);
//                }
                    break;
                case R.id.iv_activity_change_camera:
                    changeCamera();
                    break;
                case R.id.tv_activity_shoot_local:
                    if (recordMovieMaximize()) {
                        Intent intent = new Intent(RecordActivity.this, LocalMediaActivity.class);
                        if (mMovieShowListAdapter != null) {
                            intent.putExtra("selectVideoCount", mMovieShowListAdapter.getData().size());
                            intent.putExtra("selectVideoTime", recordTotalTime() + "");
                            intent.putExtra("from", mFrom);
                        }
                        startActivityForResult(intent, 1);
                    }
                    break;
                case R.id.tv_activity_shoot_cache:
                    goToCache = true;
                    Intent cacheIntent = new Intent(RecordActivity.this, CacheMovieActivity.class);
                    startActivity(cacheIntent);
                    break;
                case R.id.tv_activity_shoot_save:
                    finish();
                    break;
            }
        }
    }


    @SuppressWarnings("SameParameterValue")
    private class AlbumOrientationEventListener extends OrientationEventListener {
        private AlbumOrientationEventListener(Context context, int rate) {
            super(context, rate);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
                return;
            }

            //录制标签设置加大范围
            //如果是暂停状态就只记录现在屏幕的方向
            if (needRestPreview) {
                if (orientation >= 0 && orientation < 180) {
                    mOrientation = 90;
                } else if (orientation >= 180 && orientation <= 360) {
                    mOrientation = 270;
                }
                return;
            } else {
                if (orientation > 60 && orientation <= 120) {
                    mOrientation = 90;
                    showHorizontalAnimation();
                } else if (orientation > 230 && orientation <= 300) {
                    mOrientation = 270;
                    showHorizontalAnimation();
                } else if (orientation > 120 && orientation <= 230) {
                    mOrientation = 180;
                    showVerticalAnimation();
                    return;
                } else if (orientation > 300 && orientation <= 360 || (orientation > 0 && orientation <= 60)) {
                    mOrientation = 0;
                    showVerticalAnimation();
                    return;
                }
            }

            //判断范围
            if (mOrientation == 90) {
                //反横屏
                //判断现在是不是反横屏
                if (getRequestedOrientation() != SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                    mTXCameraRecord.stopCameraPreview();
                    startCameraPreview(VIDEO_ANGLE_HOME_LEFT);
                }
            } else if (mOrientation == 270) {
                //正横屏
                //判断是不是正横屏
                if (getRequestedOrientation() != SCREEN_ORIENTATION_LANDSCAPE) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    mTXCameraRecord.stopCameraPreview();
                    startCameraPreview(VIDEO_ANGLE_HOME_RIGHT);
                }
            }
        }

        private void showVerticalAnimation() {
            if (isHorizontal) {
                if (ivShootShoot != null) {
                    ivShootShoot.setEnabled(false);
                }
                isHorizontal = false;
                //动画
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mShootLayout, "rotation", 0, -90);
                objectAnimator.setDuration(500);
                objectAnimator.start();
                objectAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (ivShootShoot != null) {
                            ivShootShoot.setEnabled(true);
                        }
                        isVertical = true;
                        //让提示文字出现
                        if (tvShootRecordTime != null) {
                            if (tvShootRecordTime.getVisibility() == View.GONE) {
                                setTimeAnimation(tvShootRecordTime, R.anim.anim_translate_in, View.VISIBLE);
                                tvShootRecordTime.setText(getString(R.string.record_horizontal_tip));
                            }
                        }
                    }
                });
            }
        }

        private void showHorizontalAnimation() {
            if (isVertical) {
                //录制不可点击
                if (ivShootShoot != null) {
                    ivShootShoot.setEnabled(false);
                }
                isVertical = false;
                //动画
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mShootLayout, "rotation", -90, 0);
                objectAnimator.setDuration(500);
                objectAnimator.start();
                objectAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (ivShootShoot != null) {
                            ivShootShoot.setEnabled(true);
                        }
                        isHorizontal = true;
                        //让提示文字出现
                        if (tvShootRecordTime != null) {
                            if (tvShootRecordTime.getVisibility() == View.GONE) {
                                setTimeAnimation(tvShootRecordTime, R.anim.anim_translate_out, View.VISIBLE);
                            } else {
                                if (!isRecording)
                                    setTimeAnimation(tvShootRecordTime, R.anim.anim_translate_out, View.GONE);
                            }
                        }
                    }
                });
            }
        }
    }

    private void refreshRecordList(MovieItemModel mMovieItemModel, String picPath) {
        mMovieItemModel.setFilPicPath(picPath);
        mMovieShowListAdapter.addData(mMovieItemModel);
        putToCache(mMovieItemModel);
//        //正在录制就不改变状态了
//        if (!isRecording) {
//            //显示按钮
//            changeBackIconAndState(ivShootCancel, ivShootBack, true);//显示按钮
//            //然录制按钮可以点击
//            ivShootShoot.setEnabled(true);
//        }
        //滑动到最后一个
        moveRecyclerToPosition();
    }

    private class VideoFramePicListener implements VideoHelper.OnLoadVideoImageListener {

        private MovieItemModel mMovieItemModel;


        private VideoFramePicListener(String file, MovieItemModel movieItemModel) {
            mMovieItemModel = movieItemModel;
            mMovieItemModel.setFilePath(file);
        }

        private void setFilePathAndMovieItemModel(MovieItemModel movieItemModel, String videoFile) {
            mMovieItemModel = movieItemModel;
            mMovieItemModel.setFilePath(videoFile);
        }

        @Override
        public void onLoadImage(File file) {
            refreshRecordList(mMovieItemModel, file.getAbsolutePath());
        }
    }


    private void putToCache(MovieItemModel movieItemModel) {
        initTimeKey();

        //判断是否是草稿箱进来的，去修改key
        changeCacheKey();

        //添加缓存到集合以及数据库
        if (movieItemModel != null) {
            MovieItemDb movieItemDb = new MovieItemDb();
            movieItemDb.setUid(UserInfoManager.getUid() + "");
            movieItemDb.setKey(timeKey);
            movieItemDb.setFrom(VIDEO_RECORD);
            movieItemDb.setVideoTime(movieItemModel.getVideoTime().doubleValue());
            movieItemDb.setFilPicPath(movieItemModel.getFilPicPath());
            movieItemDb.setFilePath(movieItemModel.getFilePath());
            mMovieItemDaoList.add(movieItemDb);
            //插入数据库
            mMovieItemDbDao.insert(movieItemDb);
        }
    }

    private void changeCacheKey() {
        //判断是不是第一次修改
        if (hasChangeRecord) {
            //先遍历集合修改所有的key
            if (mMovieItemDaoList != null && mMovieItemDaoList.size() > 0) {
                for (MovieItemDb movieItemDb : mMovieItemDaoList) {
                    movieItemDb.setKey(timeKey);
                    //更新数据库
                    mMovieItemDbDao.update(movieItemDb);
                }
            }
            hasChangeRecord = false;
        }
    }


    private class RecordPermissionListener implements PermissionManager.PermissionListener {

        @Override
        public void doAfterGrand(String[] permission, String tag) {
            startCamera();
        }

        @Override
        public void doAfterDeniedCancel(String[] permission, String tag) {
            finish();
        }

        @Override
        public void doAfterDeniedEnsure(String[] permission, String tag) {
            if (mPermissionManager != null)
                mPermissionManager.requestPermissions(RecordActivity.this, mRecordPermissionListener, permission, PERMISSIONTAG, true);
        }

    }


    // ------------ finish current activity  start  -----------------
    @Override
    public boolean isNeedRxBus() {
        return true;
    }

    @SuppressWarnings("unused")
    @Subscribe(code = RxBusCode.ACT_FINISH_VIDEO_EDIT, threadMode = ThreadMode.MAIN)
    public void actFinish(String isFinish) {
        if ("true".equals(isFinish) && !this.isFinishing()) {
            this.finish();
            //删除
            if (mMovieItemDbDao != null) {
                mMovieItemDbDao.deleteInTx(mMovieItemDaoList);
            }

            deleteCacheFile(mMovieItemDaoList, true, false);
        }
    }

    private void deleteCacheFile(Object o, boolean needDelete, boolean deleteFirst) {
        if (o instanceof MovieItemDb) {
            //判断是否是本地视频
            if (!((MovieItemDb) o).getVideo_from()) {
                String filePath = ((MovieItemDb) o).getFilePath();
                //因为封面图是当前第一个视频的图片所以不能删除
                if (!TextUtils.isEmpty(filePath)) {
                    File file = new File(filePath);

                    if (file.exists()) {
                        file.delete();
                    }
                }

                if (needDelete) {
                    String filPicPath = ((MovieItemDb) o).getFilPicPath();
                    if (!TextUtils.isEmpty(filPicPath)) {
                        File file = new File(filPicPath);

                        if (file.exists()) {
                            file.delete();
                        }
                    }
                }
            }
        } else if (o instanceof ArrayList) {
            //获取第一个数据再判断
            for (int i = 0; i < ((ArrayList) o).size(); i++) {
                Object itemO = ((ArrayList) o).get(i);
                if (itemO instanceof MovieItemDb) {
                    if (i == 0) {
                        if (deleteFirst) {
                            deleteCacheFile(itemO, false, true);
                        } else {
                            deleteCacheFile(itemO, true, true);
                        }
                    } else {
                        deleteCacheFile(itemO, true, true);
                    }
                }
            }
        }
    }

    @SuppressWarnings("unused")
    @Subscribe(code = RxBusCode.ACT_VIDEO_RECORD_REFRESH_BY_EDIT, threadMode = ThreadMode.MAIN)
    public void actRefreshAdapter(Intent intent) {
        if (mMovieShowListAdapter != null && intent.hasExtra("edit_model")) {
            Serializable data = intent.getSerializableExtra("edit_model");
            if (data instanceof MovieEditModel) {
                List<MovieItemModel> movieItemModels = ((MovieEditModel) data).getMovieItemModels();
                if (movieItemModels.size() == 0) {
                    //修改界面
                    if (mLocalText != null && mLocalText.getVisibility() == View.GONE) {
                        mLocalText.setVisibility(View.VISIBLE);
                    }
                    if (mCacheText != null && mCacheText.getVisibility() == View.GONE) {
                        mCacheText.setVisibility(View.VISIBLE);
                    }
                    if (tvShootNext != null && tvShootNext.getVisibility() == View.VISIBLE) {
                        tvShootNext.setVisibility(View.GONE);
                    }
                    if (mSaveCacheText != null && mSaveCacheText.getVisibility() == View.VISIBLE) {
                        mSaveCacheText.setVisibility(View.GONE);
                    }
                }
                mMovieShowListAdapter.setNewData(movieItemModels);
            }
        }

        if (intent.hasExtra("record_model")) {
            Serializable data = intent.getSerializableExtra("record_model");
            if (data instanceof ArrayList) {
                mMovieItemDaoList = (ArrayList<MovieItemDb>) data;
                //重新设置草稿箱的标记
                setCacheStatus(VIDEO_RECORD);
            }
        }
    }

    @SuppressWarnings("unused")
    @Subscribe(code = RxBusCode.ACT_VIDEO_PRE_REFRESH_CLIP, threadMode = ThreadMode.MAIN)
    public void actClip(Intent intent) {
        //删除当前的文件并关闭界面
        if (goToCache) return;
        refreshClipAdapter(intent);
    }

    @SuppressWarnings("unused")
    @Subscribe(code = RxBusCode.ACT_FINISH_VIDEO_RECORD, threadMode = ThreadMode.MAIN)
    public void actPush() {
        finish();
    }

    @SuppressWarnings("unused")
    @Subscribe(code = RxBusCode.ACT_VIDEO_PUSH_REFRESH_CACHE, threadMode = ThreadMode.MAIN)
    public void actCache() {
        finish();
    }

    // ------------ finish current activity  end  -----------------
    @SuppressWarnings("unused")
    @Subscribe(code = RxBusCode.ACT_VIDEO_EDIT_CACHE, threadMode = ThreadMode.MAIN)
    public void actEditCache() {
        KLog.d("actEditCache");
        finish();
    }


    private void showCustomizeDialog() {
        /* @setView 装入自定义View ==> R.layout.dialog_customize
         * 由于dialog_customize.xml只放置了一个EditView，因此和图8一样
         * dialog_customize.xml可自定义更复杂的View
         */
        Dialog dialog = new Dialog(this);


        final View dialogView = LayoutInflater.from(RecordActivity.this)
                .inflate(R.layout.dialog_video_guidance, null);

      // customizeDialog.setTitle("我是一个自定义Dialog");

        dialog.setContentView(dialogView);
        dialog.show();
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = (int)(display.getWidth() * 0.78); //设置宽度
        lp.height = (int)(display.getHeight() * 0.83); //设置高度
        dialog.getWindow().setAttributes(lp);
        startVideo(dialogView);


    }

    private void startVideo(View v) {
        final CustomVideoView vv = v.findViewById(R.id.video_view_1);

        /*vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START)
                            vv.setBackgroundColor(Color.TRANSPARENT);
                        return true;
                    }
                });*/

        View btn_finish = v.findViewById(R.id.btn_finish);
        final String uri = "android.resource://" + getPackageName() + "/" + R.raw.v_guide;
        /*主要代码起始位置*/
        vv.setVideoURI(Uri.parse(uri));
        vv.start();
        vv.setOnPreparedListener(mp -> {
            mp.start();
            mp.setLooping(false);
            Handler myHandler = new Handler();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            vv.setBackgroundColor(Color.TRANSPARENT);
                        }
                    },500);
                }
            }).start();


        });
        vv.setOnCompletionListener(mp -> {

            vv.stopPlayback();
            btn_finish.setVisibility(View.VISIBLE);
        });
    }

}

