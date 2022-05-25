package com.xianghe.ivy.ui.module.videoedit;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.ActivityUtils;
import com.tangyx.video.ffmpeg.FFmpegRun;
import com.tangyx.video.ffmpeg.FFmpegUtils;
import com.xianghe.ivy.R;
import com.xianghe.ivy.app.greendao.GreenDaoManager;
import com.xianghe.ivy.constant.RxBusCode;
import com.xianghe.ivy.entity.dao.MovieItemDbDao;
import com.xianghe.ivy.entity.db.MovieItemDb;
import com.xianghe.ivy.manager.PermissionManager;
import com.xianghe.ivy.manager.UserInfoManager;
import com.xianghe.ivy.model.ImageBean;
import com.xianghe.ivy.model.MusicBean2;
import com.xianghe.ivy.model.MusicList;
import com.xianghe.ivy.model.MusicTagBean;
import com.xianghe.ivy.mvp.loadPager.BaseMVPLoadingActivity;
import com.xianghe.ivy.ui.media.base.IMediaPlayer;
import com.xianghe.ivy.ui.media.config.MediaConfig;
import com.xianghe.ivy.ui.module.pic2video.SwitchModel;
import com.xianghe.ivy.ui.module.pic2video.SwitchStyleAdapter;
import com.xianghe.ivy.ui.module.preview_movie.PreviewMovieActivity;
import com.xianghe.ivy.ui.module.record.RecordActivity;
import com.xianghe.ivy.ui.module.record.adapter.CustomItemTouchCallBack;
import com.xianghe.ivy.ui.module.record.adapter.MovieShowListAdapter;
import com.xianghe.ivy.ui.module.record.model.MovieEditModel;
import com.xianghe.ivy.ui.module.record.model.MovieItemModel;
import com.xianghe.ivy.ui.module.videocall.service.FileUtil;
import com.xianghe.ivy.ui.module.videocalledit.VideoCallRecordActivity;
import com.xianghe.ivy.ui.module.videoedit.audio_record.VideoEditAudioRecordDialog;
import com.xianghe.ivy.ui.module.videoedit.music_select.VideoEditMusicShowView;
import com.xianghe.ivy.ui.module.videoedit.sound.VESoundPopupWindow;
import com.xianghe.ivy.ui.module.videopush.VideoPushActivity;
import com.xianghe.ivy.utils.AnimationUtils;
import com.xianghe.ivy.utils.FileUtill;
import com.xianghe.ivy.utils.FileUtils;
import com.xianghe.ivy.utils.IvyUtils;
import com.xianghe.ivy.utils.KLog;
import com.xianghe.ivy.utils.NoDoubleClickUtils;
import com.xianghe.ivy.utils.ToastUtil;
import com.xianghe.ivy.weight.CustomProgress;
import com.xianghe.ivy.weight.dialog.LoadingDialog;
import com.xianghe.ivy.weight.download.DownMusicFile;
import com.xianghe.ivy.weight.download.onDownloadProgressCallBack;
import com.xianghe.ivy.weight.scrollview.ObservableScrollView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gorden.rxbus2.RxBus;
import gorden.rxbus2.Subscribe;
import gorden.rxbus2.ThreadMode;
import icepick.State;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.xianghe.ivy.app.IvyConstants.VIDEO_EDIT;
import static com.xianghe.ivy.app.IvyConstants.VIDEO_PUSH;
import static com.xianghe.ivy.app.IvyConstants.VIDEO_RECORD;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class VideoEditActivity extends BaseMVPLoadingActivity<VideoEditContract.View, VideoEditPresenter>
        implements VideoEditContract.View, View.OnClickListener {
    private static final String TAG = "VideoEditActivity";

    private SurfaceView mVeSurfaceView;
    private ImageView mVeIvBack;
    private TextView mVeTvPush;
    private ImageView mVeIvRecord;
    private ImageView mVeIvMusic;
    private ImageView mVeIvSound;
    private TextView mVeTvMusicName;
    private RecyclerView rvShootRecyclerView;
    private VideoEditMusicShowView mRightMusicLayout;

    // 录音弹框
    private VideoEditAudioRecordDialog mAudioRecordDialog;
    // 音乐弹框
//    private ViewEditMusicShowDialog mMusicSelectDialog;
    // 退出弹框
    private CustomProgress mCustomProgress;
    // 视频合成弹框
    private LoadingDialog mLoadingDialog;

    // ------------------------ permission start  -----------------------
    public static final String REQUEST_CODE_PERMISSIONS_1001 = "1001";
    public static final String REQUEST_CODE_PERMISSIONS_1002 = "1002";
    public static final String REQUEST_CODE_PERMISSIONS_1003 = "1003";
    public static final String REQUEST_CODE_PERMISSIONS_1004 = "1004";
    private PermissionManager.PermissionListener mPermissionListener;

    // 需要申请的运行时权限
    private String[] recordNeedPermissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
    };
    // 需要申请的运行时权限
    private String[] musicNeedPermissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };
    private String[] locationNeedPermissions = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_WIFI_STATE
    };
    // ------------------------ permission end  -----------------------

    // 音视频播放工具类
    public VideoEditPlayUtils mVideoEditPlayUtils;
    private FFmpegUtils mFFmpegUtils;

    // params
    private boolean isHideCurrentUi = false;

    // 视频裁剪
    private MovieShowListAdapter mMovieShowListAdapter;
    private boolean needRefreshLastData = false;// 在被剪辑，置位，删除之后都需要更新上一个页面的数据


    // ------- 音频裁剪 start -----------
    private TextView tvPreviewMovieTitle;
    private View mClipBgLeft;
    private View mClipBgRight;
    private View mLeftMovieBg;
    private View mRightMovieBg;
    private RelativeLayout mClipLayout;
    private TextView mClipTime;
    private TextView mSaveCacheText;

    private int mClipTotalWidth;
    private double mStartTime = 0.0;
    private double mEndTime;

    private ObservableScrollView mScrollView;
    private LinearLayout layout;
    private FrameLayout mVideoEditMusicLayout;

    private boolean isClipTotalOpen = false;
    private long musicDuration;//音频总时长
    private double totalTime;//音频时间小于视频时间 / s
    private int srcollWidth;//音频剪切srcollview总长度
    private double mChangetmX = 0;//滑动srcollview长度
    // ------- 音频裁剪 end -----------


    //新添加数据库对象以及数据库数据
    private ArrayList<MovieItemDb> mMovieItemDaoList;

    private MovieItemDbDao mMovieItemDbDao;

    @State
    long timeKey;

    @State
    boolean hasChangeRecord = true;

    @State
    int mFrom;


    private RecyclerView mStyleRecyclerView;

    @State
    boolean isPic;

    private SwitchStyleAdapter mSwitchStyleAdapter;

    @State
    MovieEditModel mPreData;

    private Disposable mSubscribe;

    @State
    MovieItemDb mMovieItemDb;

    @State
    ArrayList<MusicBean2> mMusicBgBean;


    @State
    int mItemType = -1001;

    private CustomItemTouchCallBack.TouchStatus mTouchStatus;

    @Nullable
    @Override
    public VideoEditPresenter createPresenter() {
        return new VideoEditPresenter();
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        dismissLoading();
        init();
        initView();
        initUtils();
        initAdapter();
        initCacheMusic();
        initLeftTouch();
        initRightTouch();
    }

    private void init() {
        mMovieItemDbDao = GreenDaoManager.getInstance().getMovieItemDao();
        Intent intent = getIntent();
        Serializable db_model = intent.getSerializableExtra("record_model");
        mFrom = intent.getIntExtra("from", 0);
        isPic = intent.getBooleanExtra("pic", false);
        if (db_model instanceof ArrayList) {
            mMovieItemDaoList = (ArrayList<MovieItemDb>) db_model;
            if (isPic && mMovieItemDaoList.size() > 0) {
                mMovieItemDb = mMovieItemDaoList.get(0);
                //判断是否有中括号
                clearMark();
            }
        }

    }

    private void clearMark() {
        if (mMovieItemDb != null) {
            String picList = mMovieItemDb.getPicList();
            if (!TextUtils.isEmpty(picList)) {
                if (picList.contains("[") || picList.contains("]")) {
                    picList = picList.replace("[", "");
                    picList = picList.replace("]", "");
                    //重新添加
                    mMovieItemDb.setPicList(picList);
                }
            }
        }
    }

    private void initTimeKey() {
        if (timeKey == 0) {
            timeKey = System.currentTimeMillis();
        }
    }

    private MovieEditModel getPreData() {
        Intent intent = getIntent();
        if (intent.hasExtra("edit_model")) {
            Serializable data = intent.getSerializableExtra("edit_model");
            if (data instanceof MovieEditModel) {
                // new MovieEditModel(mVideoFile,picFileName,mMovieShowListAdapter.getData()))
                // /storage/emulated/0/Pictures/image
                // /storage/emulated/0/xh_recorder
                return ((MovieEditModel) data);
            }
        }
        return null;
    }

    private void initUtils() {
        mPreData = getPreData();
        mVideoEditPlayUtils = new VideoEditPlayUtils(mVeSurfaceView, mPreData, listener);
        mCustomProgress = new CustomProgress(this);
        mLoadingDialog = new LoadingDialog(this);
        mFFmpegUtils = new FFmpegUtils(FileUtils.createTempDir(MediaConfig.VIDEO_XH_RECORDER_SYNTH));
        mPermissionListener = new PermissionManager.SimplePermissionListener() {
            @Override
            public void doAfterGrand(String[] permission, String tag) {
                super.doAfterGrand(permission, tag);
                switch (tag) {
                    // 录音回调
                    case REQUEST_CODE_PERMISSIONS_1001:
                        showRecordDialog();
                        break;
                    // 本地音乐获取回调
                    case REQUEST_CODE_PERMISSIONS_1002:
                        mPresenter.requestMusicTag();
                        break;
                    // 位置获取回调
                    case REQUEST_CODE_PERMISSIONS_1003:
                        videoAndAudioSynthesis();
                        break;
                    // 需要获取音乐权限，读取文件
                    case REQUEST_CODE_PERMISSIONS_1004:
//                        initAdapter();
//                        initCacheMusic();
                        break;
                }
            }

            @Override
            public void doAfterDeniedEnsure(String[] permission, String tag) {
                super.doAfterDeniedEnsure(permission, tag);
                if (mPermissionManager != null) {
                    mPermissionManager.requestPermissions(VideoEditActivity.this, this,
                            permission, tag, true);
                }
            }
        };
    }

    @Override
    public void initListener() {
    }

    @NotNull
    @Override
    public Object onCreateContentView(@Nullable Bundle bundle) {
        if (mVideoEditPlayUtils != null) mVideoEditPlayUtils.onRestoreInstanceState(bundle);
        return R.layout.activity_video_edit;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mVideoEditPlayUtils != null) mVideoEditPlayUtils.onSaveInstanceState(outState);
    }

    private void initView() {
        mVeSurfaceView = (SurfaceView) findViewById(R.id.ve_surfaceview);
        mVeIvBack = (ImageView) findViewById(R.id.ve_iv_back);
        mVeTvPush = (TextView) findViewById(R.id.ve_tv_push);
        mVeIvRecord = (ImageView) findViewById(R.id.ve_iv_record);
        mVeIvMusic = (ImageView) findViewById(R.id.ve_iv_music);
        mVeIvSound = (ImageView) findViewById(R.id.ve_iv_sound);
        mVeTvMusicName = (TextView) findViewById(R.id.ve_tv_music_name);
        rvShootRecyclerView = (RecyclerView) findViewById(R.id.rv_activity_shoot_recyclerView);
        mStyleRecyclerView = findViewById(R.id.rv_activity_view_edit_style);
        mSaveCacheText = findViewById(R.id.ve_activity_save_cache);

        //音频剪辑
        tvPreviewMovieTitle = (TextView) findViewById(R.id.tv_activity_preview_movie_title);
        mClipBgLeft = (View) findViewById(R.id.view_activity_preview_movie_left_clip);
        mClipBgRight = (View) findViewById(R.id.view_activity_preview_movie_right_clip);
        mLeftMovieBg = (View) findViewById(R.id.view_activity_preview_movie_left_movie_bg);
        mRightMovieBg = (View) findViewById(R.id.view_activity_preview_movie_right_movie_bg);
        mClipLayout = (RelativeLayout) findViewById(R.id.rl_preview_movie_clip_layout);
        mClipTime = (TextView) findViewById(R.id.tv_preview_movie_clip_time);
        mScrollView = (ObservableScrollView) findViewById(R.id.layout_scrollview);
        layout = (LinearLayout) findViewById(R.id.layout);
        mVideoEditMusicLayout = (FrameLayout) findViewById(R.id.video_edit_music);
        mRightMusicLayout = findViewById(R.id.ll_activity_view_edit_music);


        mVeIvBack.setOnClickListener(this);
        mVeTvPush.setOnClickListener(this);
        mVeIvRecord.setOnClickListener(this);
        mVeIvMusic.setOnClickListener(this);
        mVeIvSound.setOnClickListener(this);
        mVeSurfaceView.setOnClickListener(this);
        mSaveCacheText.setOnClickListener(this);
        mVeIvSound.setTag(false); // false代表隐藏ui

        mScrollView.setOnScrollListener(new ObservableScrollView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(ObservableScrollView view, int scrollState) {
                switch (scrollState) {
                    case SCROLL_STATE_IDLE:
                        // 滚动停止之后需要重新播放音乐
                        setAudioPositionPlay();
                        break;
                }
            }

            @Override
            public void onScroll(ObservableScrollView view, boolean isTouchScroll, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                double pxOffer = musicDuration * 1.0f / srcollWidth;
                mChangetmX = pxOffer * scrollX;
            }
        });
    }

    @Override
    public void onClick(View v) {
        NoDoubleClickUtils.isDoubleClick(() -> {
            switch (v.getId()) {
                case R.id.ve_iv_back:
                    showBackNotice();
                    break;
                case R.id.ve_tv_push:
                    if (recordTotalTime() >= 5) {
                        mPermissionManager.requestPermissions(VideoEditActivity.this, mPermissionListener, locationNeedPermissions, REQUEST_CODE_PERMISSIONS_1003);
                    } else {
                        if (mCustomProgress != null) {
                            mCustomProgress.show("", v1 -> mCustomProgress.dismiss(), true, dialog -> mCustomProgress.dismiss(), new Handler(getMainLooper()));
                        }
                    }
                    break;
                case R.id.ve_iv_record:
                    mPermissionManager.requestPermissions(VideoEditActivity.this, mPermissionListener, recordNeedPermissions, REQUEST_CODE_PERMISSIONS_1001);
                    break;
                case R.id.ve_iv_music:
                    checkLogin(() -> {
                        mPermissionManager.requestPermissions(VideoEditActivity.this, mPermissionListener, musicNeedPermissions, REQUEST_CODE_PERMISSIONS_1002);
                    });
                    break;
                case R.id.ve_iv_sound:
                    if (mVideoEditPlayUtils == null) {
                        return;
                    }
                    VESoundPopupWindow soundPopupWindow = new VESoundPopupWindow(VideoEditActivity.this, mVideoEditPlayUtils,
                            mVideoEditPlayUtils.getMusicPath(),
                            mVideoEditPlayUtils.getRecordPath(), isPic);
                    soundPopupWindow.showPopupWindow(mVeIvSound);
                    break;
                case R.id.ve_surfaceview:
                    if (mRightMusicLayout.isVisible()) {
                        mRightMusicLayout.dismiss();
                    }
                    break;
                case R.id.ve_activity_save_cache:
                    changeCacheStatus(VIDEO_EDIT);
                    RxBus.get().send(RxBusCode.ACT_VIDEO_EDIT_CACHE);
                    List<Activity> activityList = ActivityUtils.getActivityList();
                    if (activityList != null && !activityList.isEmpty()) {
                        for (Activity act : activityList) {
                            if (!act.isDestroyed() && act.getClass().getSimpleName().equals(RecordActivity.class.getSimpleName()) ||
                                    act.getClass().getSimpleName().equals(VideoCallRecordActivity.class.getSimpleName())) {
                                act.finish();
                            }
                        }
                    }
                    finish();
                    break;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mVideoEditPlayUtils != null) {
            mVideoEditPlayUtils.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoEditPlayUtils != null) {
            mVideoEditPlayUtils.pause();
        }
        // 处理熄灭屏幕关闭录音
        if (mAudioRecordDialog != null && mAudioRecordDialog.isShowing()) {
            mAudioRecordDialog.pauseRecord();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVideoEditPlayUtils != null) {
            mVideoEditPlayUtils.destroy();
//            // 删除录音文件夹 取文件前缀 得到文件夹名字，删除
//            String recordPath = mVideoEditPlayUtils.getRecordPath();
//            if (recordPath != null && !TextUtils.isEmpty(recordPath)) {
//                int index = recordPath.lastIndexOf(MediaConfig.AUDIO_DIR);
//                if (index > 0) {
//                    recordPath = recordPath.substring(0, index + MediaConfig.AUDIO_DIR.length());
//                    KLog.i("recordPath:" + recordPath);
//                    FileUtils.deleteTempFileDir(recordPath);
//                }
//            }
        }
        dismissCustomDialog();
        loadingDialogDismiss();
        if (mAudioRecordDialog != null && mAudioRecordDialog.isShowing()) {
            mAudioRecordDialog.dismiss();
        }
        dismissMusicDialog();
        DownMusicFile.getInstance().cancle();
        // 资源删除

        if (mSubscribe != null && !mSubscribe.isDisposed()) {
            mSubscribe.dispose();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showBackNotice();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void showBackNotice() {
        // 什么都没加就直接退出去
//        if (mVideoEditPlayUtils != null
//                && mVideoEditPlayUtils.getRecordPath() == null
//                && mVideoEditPlayUtils.getMusicPath() == null) {
        // 更新上一页的数据
        //判断是否有音乐
        //判断是哪个界面进来的
        if (mRightMusicLayout.isVisible()) {
            mRightMusicLayout.dismiss();
        } else {
            backRefresh();
        }

//            return;
//        }
       /* if (mCustomProgress != null) {
            mCustomProgress.show(getString(R.string.video_edit_back), getString(R.string.common_notice_title), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCustomProgress.dismiss();
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCustomProgress.dismiss();
                    // 更新上一页的数据
                    backRefresh();
                    finish();
                }
            }, true, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    mCustomProgress.dismiss();
                }
            });
        }*/
    }

    public void startScrollText() {
        if (!TextUtils.isEmpty(mVeTvMusicName.getText())) {
            mVeTvMusicName.requestFocus();
            mVeTvMusicName.setSelected(true);
        }
    }

    public void stopScrollText() {
        mVeTvMusicName.setSelected(false);
    }


    public void hideCurrentUi() {
        if (!isHideCurrentUi) {
            isHideCurrentUi = true;
            Animation animation = AnimationUtils.createVerticalAnimation(0f, -1.5f, 200);
            mVeTvPush.startAnimation(animation);
            mVeIvRecord.startAnimation(animation);
            mVeIvMusic.startAnimation(animation);

            mVeTvPush.setVisibility(View.GONE);
            mVeIvRecord.setVisibility(View.GONE);
            mVeIvMusic.setVisibility(View.GONE);

            if (mVeIvSound.getTag() != null && mVeIvSound.getTag() instanceof Boolean && (boolean) mVeIvSound.getTag()) {
                mVeIvSound.startAnimation(animation);
                mVeIvSound.setVisibility(View.GONE);
            }
        }
    }

    public void showCurrentUi() {
        if (isHideCurrentUi) {
            isHideCurrentUi = false;
            Animation animation = AnimationUtils.createVerticalAnimation(-1.5f, 0f, 200);
            mVeTvPush.startAnimation(animation);
            mVeIvRecord.startAnimation(animation);
            mVeIvMusic.startAnimation(animation);

            mVeTvPush.setVisibility(View.VISIBLE);
            mVeIvRecord.setVisibility(View.VISIBLE);
            mVeIvMusic.setVisibility(View.VISIBLE);

            if (mVeIvSound.getTag() != null && mVeIvSound.getTag() instanceof Boolean && (boolean) mVeIvSound.getTag()) {
                mVeIvSound.startAnimation(animation);
                mVeIvSound.setVisibility(View.VISIBLE);
            }
        }
    }

    private void showLoadMusicBack() {
        if (mCustomProgress != null) {
            mCustomProgress.show(getString(R.string.video_edit_load_music_back),
                    getString(R.string.common_tips_title),
                    getString(R.string.common_cancel),
                    getString(R.string.common_ensure),
                    v -> mCustomProgress.dismiss(), v -> {
                        mCustomProgress.dismiss();
                        DownMusicFile.getInstance().cancleAnddeleteFile();
                    }, true, dialog -> mCustomProgress.dismiss());
        }
    }


    private void dismissCustomDialog() {
        if (mCustomProgress != null && mCustomProgress.isShowing()) {
            mCustomProgress.dismiss();
        }
    }

    // ----------------------------- 录音 start  -----------------------------------
    private void showRecordDialog() {
        if (mVideoEditPlayUtils == null) {
            return;
        }
        mAudioRecordDialog = new VideoEditAudioRecordDialog(this,
                mVideoEditPlayUtils.getVideoTotalTime(),
                new VideoEditAudioRecordDialog.recordStatusListener() {
                    @Override
                    public void sureRecordListener(File file, String filePath) {
                        //设置录音的数据
                        setCacheVoice(filePath);

                        // 保存当前录音文件,并重新播放音视频
                        if (mVideoEditPlayUtils != null) {
                            mVideoEditPlayUtils.setRecordPath(filePath);// 保存录音文件路径
                            mVideoEditPlayUtils.openAllVolume();
                            mVideoEditPlayUtils.resetAllPlayer(); // 重播音视频
                        }
                        // 控制音乐按钮显示
                        mVeIvSound.setTag(true);

                        // 录音成功之后不再编辑视频
                        rvShootRecyclerView.setVisibility(View.GONE);
                    }

                    @Override
                    public void startFirstRecordListener() {
                        // 停止界面音乐，文字滚动
                        stopScrollText();
                        if (mVideoEditPlayUtils != null) {
                            mVideoEditPlayUtils.closeAllVolume();
                        }
                    }

                    @Override
                    public void dismissDialogListener() {
                        showCurrentUi();

                        // 恢复所有音乐，文字滚动
                        startScrollText();
                        if (mVideoEditPlayUtils != null) {
                            mVideoEditPlayUtils.openAllVolume();
                        }
                    }
                });
        mAudioRecordDialog.show();
        hideCurrentUi();
    }

    private void setCacheVoice(String filePath) {
        if (mMovieItemDbDao != null && mMovieItemDaoList != null && mMovieItemDaoList.size() > 0) {
            for (MovieItemDb movieItemDb : mMovieItemDaoList) {
                movieItemDb.setVoicePath(filePath);
                mMovieItemDbDao.update(movieItemDb);
            }
        }
    }

    private void initCacheMusic() {
        /*添加音乐和录音的播放*/
        if (mMovieItemDaoList != null && mMovieItemDaoList.size() > 0) {
            MovieItemDb movieItemDb = mMovieItemDaoList.get(0);
            if (!TextUtils.isEmpty(movieItemDb.getVoicePath())) {
                File file = new File(movieItemDb.getVoicePath());
                if (file.exists()) {
                    mVideoEditPlayUtils.setRecordPath(movieItemDb.getVoicePath());// 保存录音文件路径
                    mVideoEditPlayUtils.openAllVolume();
                    mVideoEditPlayUtils.resetAllPlayer(); // 重播音视频

                    if (mVeIvSound != null) {
                        mVeIvSound.setVisibility(View.VISIBLE);
                    }

                    if (rvShootRecyclerView != null) {
                        // 录音成功之后不再编辑视频
                        rvShootRecyclerView.setVisibility(View.GONE);
                    }
                }
            }

            if (!TextUtils.isEmpty(movieItemDb.getMusicPath())) {
                File file = new File(movieItemDb.getMusicPath());
                if (file.exists()) {
                    // 控制音乐按钮显示
                    if (mVeIvSound != null) {
                        mVeIvSound.setVisibility(View.VISIBLE);
                    }

                    if (mVideoEditPlayUtils != null) {
                        boolean show = false;
                        // 是否是图片转视频
//                        if (mMovieItemDb != null) {
//                            show = mMovieItemDb.getPicToMovie();
//                        }
                        openMusicAndClip(movieItemDb.getMusicPath(), !show);
                    }

                    if (mVeTvMusicName != null) {
                        // 设置文字，开启文字滚动
                        mVeTvMusicName.setText(movieItemDb.getMusicName());
                        mVeTvMusicName.setTag(movieItemDb.getMusicId());
                        startScrollText();
                    }
                }
            }
        }else {
            Toast.makeText(this,"未找到内存卡",Toast.LENGTH_SHORT).show();
        }
    }

    // 读取音乐
    private boolean hasMusicPath(MovieItemDb movieItemDb) {
        if (TextUtils.isEmpty(movieItemDb.getMusicPath())) {
            return false;
        }
        File file = new File(movieItemDb.getMusicPath());
//        try {
//            String filePath = URLDecoder.decode(movieItemDb.getMusicPath(),"UTF-8");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        // 处理部分音乐读取失败的异常，读取失败就去本地存储库里面找有无音乐
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
//            // 根据id去查找music
//            SaveDownMusicFile downMusicFile = SaveDownMusicFile.getInstance();
//            if (!downMusicFile.hasFile()) {
//                return false;
//            }
//            String musicPath = downMusicFile.readMusicFile(SaveDownMusicFile.getKey(movieItemDb.getMusicName(), movieItemDb.getMusicId()));
//            if (null == musicPath || musicPath.equals("")) {
//                return false;
//            } else {
//                File f = new File(musicPath);
//                //音乐文件不存在时，重新下载
//                if (!f.exists() || f.length() == 0) {
//                    return false;
//                } else {
//                    movieItemDb.setMusicPath(musicPath);
//                    return true;
//                }
//            }
//        }
    }
    // ----------------------------- 录音 end  -----------------------------------


    // ----------------------------- 音乐 start  -----------------------------------

    private LoadingDialog.onBackCallBackListener mBackListener = new LoadingDialog.onBackCallBackListener() {
        @Override
        public void onBackCallBack() {
            showLoadMusicBack();
        }
    };

    @Override
    public void onMusicTagListSuccess(@NotNull List<? extends MusicTagBean> list) {
//        mMusicSelectDialog = new VideoEditMusicSelectDialog((List<MusicTagBean>) list);
//        mMusicSelectDialog.show(getSupportFragmentManager());
        mRightMusicLayout.show((ArrayList<MusicTagBean>) list);
        hideCurrentUi();
    }

    private void dismissMusicDialog() {
        showCurrentUi();
        if (mRightMusicLayout != null && mRightMusicLayout.isVisible()) {
            mRightMusicLayout.dismiss();
        }
    }

    // 添加按钮，下载音乐的回调
    public void loadMusic(String musicID, String musicName, String musicLink, boolean needClipLayout) {
       /* CircleDialog.Builder downLoadBuilder = new CircleDialog.Builder();
        downLoadBuilder.setTitle("下载中")
                .setProgressText("已经下载")
                .setWidth(0.4f)
                .setCanceledOnTouchOutside(false)
                .setCancelable(true)
                .setNegative("取消", v -> {
                    DownMusicFile.getInstance().cancle();
                })
                .show(getSupportFragmentManager());*/

        if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
            mLoadingDialog.setBackListener(mBackListener);
            mLoadingDialog.show();
        }

        DownMusicFile.getInstance().downloadFile(this, musicLink, musicName, musicID, new onDownloadProgressCallBack() {
            @Override
            public void onSuccessDownload(String path) {
                KLog.i(TAG, "MusicPath = " + path);
               /* DialogFragment dialogFragment = downLoadBuilder.setProgressText("下载完成").create();
                if (dialogFragment != null) {
                    dialogFragment.dismiss();
                }*/
                dismissCustomDialog(); // 关闭返回弹框

                loadingDialogDismiss();
                // 控制音乐按钮显示
                mVeIvSound.setTag(true);

                //更新数据库数据
                upDataDaoMusic(path, musicName, musicID);

                if (mVideoEditPlayUtils != null) {
                    openMusicAndClip(path, needClipLayout);
                }

                // 关闭音乐选择弹框
                dismissMusicDialog();

                // 设置文字，开启文字滚动
                mVeTvMusicName.setText(musicName);
                mVeTvMusicName.setTag(musicID);
                startScrollText();
            }

            @Override
            public void onErrorDownload(Throwable t) {
                t.printStackTrace();
               /* DialogFragment dialogFragment = downLoadBuilder.setProgressText("下载失败").create();
                if (dialogFragment != null) {
                    dialogFragment.dismiss();
                }*/
                dismissCustomDialog(); // 关闭返回弹框

                loadingDialogDismiss();
                onError(t);
            }

            @Override
            public void onProgressDownload(int progress) {
                KLog.v(TAG, "progress = " + progress);
                /* downLoadBuilder.setProgress(100, progress).create();*/
            }
        });
    }

    private void openMusicAndClip(String path, boolean needClipLayout) {
        mVideoEditPlayUtils.audioNetMusicStop(); // 关闭网络音乐播放
        mVideoEditPlayUtils.setMusicPath(path);  // 保存网络文件路径
        mVideoEditPlayUtils.openAllVolume(); // 打开声音
        mVideoEditPlayUtils.resetMusicSeekPos(); // 重置播放音乐的起点,并关闭计时器监听
        mVideoEditPlayUtils.resetAllPlayer(); // 重播音视频
        // 添加到音频裁剪轨道里面去
        if (mVideoEditPlayUtils.getMusicPath() != null && !TextUtils.isEmpty(mVideoEditPlayUtils.getMusicPath())) {
            String mediaDuration = IvyUtils.getMediaDuration(mVideoEditPlayUtils.getMusicPath());
            if (mediaDuration == null) {
                // 如果未获取到时长，就从mediaPlayer里面获取总时长
                musicDuration = IvyUtils.getMediaDuration2(mVideoEditPlayUtils.getMusicPath());
            } else {
                musicDuration = Long.valueOf(mediaDuration).longValue();
            }
            double videoTotalTime = mVideoEditPlayUtils.getVideoTotalTime();
            if (musicDuration < videoTotalTime * 1000) {
                totalTime = musicDuration / 1000.0f;
                mVideoEditPlayUtils.setMusicLessVideoTime(true);
            } else {
                totalTime = videoTotalTime;
                mVideoEditPlayUtils.setMusicLessVideoTime(false);
            }

            if (!isClipTotalOpen) {
                getClipTotalWidth(musicDuration, needClipLayout);
            } else {
                resetClipView(musicDuration, needClipLayout);
            }
        }
    }

    private void upDataDaoMusic(String path, String musicName, String musicID) {
        if (mMovieItemDbDao == null) {
            mMovieItemDbDao = GreenDaoManager.getInstance().getMovieItemDao();
        }
        if (mMovieItemDaoList != null && mMovieItemDaoList.size() > 0) {
            for (int i=0; i < mMovieItemDaoList.size(); i++){
                MovieItemDb movieItemDb = mMovieItemDaoList.get(i);
                Log.e("音乐路径path===", path + ",,,音乐名称name==" + musicName);
                movieItemDb.setMusicPath(path);
                movieItemDb.setMusicName(musicName);
                movieItemDb.setMusicId(musicID);
                mMovieItemDbDao.update(movieItemDb);
            }
//            for (MovieItemDb movieItemDb : mMovieItemDaoList) {
//                Log.e("音乐路径path===", path + ",,,音乐名称name==" + musicName);
//                movieItemDb.setMusicPath(path);
//                movieItemDb.setMusicName(musicName);
//                movieItemDb.setMusicId(musicID);
//                mMovieItemDbDao.update(movieItemDb);
//            }
        }
    }

    // ----------------------------- 音乐 end  -----------------------------------


    // ----------------------------- 视频合成 start  -----------------------------------

    private void loadingDialogDismiss() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    private void videoAndAudioSynthesis() {
        if (mLoadingDialog == null) {
            return;
        }
        if (mVideoEditPlayUtils == null) {
            return;
        }
        if (mFFmpegUtils == null) {
            return;
        }
        mLoadingDialog.setBackListener(null);
        mLoadingDialog.show();
        try {
            mFFmpegUtils.audioAndVideoSynthesis(mVideoEditPlayUtils.getListMovieItemModel(),
                    (int) (mVideoEditPlayUtils.getVideoVolume() * 100),
                    mVideoEditPlayUtils.getVideoTotalTime(),
                    mVideoEditPlayUtils.getRecordPath(),
                    (int) (mVideoEditPlayUtils.getRecordVolume() * 100),
                    mVideoEditPlayUtils.isUnEnablePlaying() ? null : mVideoEditPlayUtils.getMusicPath(),// 开始结束等于0，就不传递音频路径
                    (int) (mVideoEditPlayUtils.getMusicVolume() * 100),
                    (mChangetmX + mStartTime),
                    (mEndTime - mStartTime), isPic,
                    outputFilePath -> {
                        KLog.i("outputFilePath: " + outputFilePath);
                        loadingDialogDismiss();
                        if (VideoEditActivity.this.isFinishing()) {
                            return;
                        }
                        if (outputFilePath == null) {
                            ToastUtil.showToast(this, getString(R.string.video_synthesis_failed));
                            return;
                        }
                        // 音乐应用统计 无需返回
                        if (mVeTvMusicName.getTag() != null && mVeTvMusicName.getTag() instanceof String) {
                            String musicId = (String) mVeTvMusicName.getTag();
                            if (mPresenter != null) mPresenter.music_count(musicId);
                        }

                        Intent intent = new Intent(this, VideoPushActivity.class);
                        String firstPic = mVideoEditPlayUtils.getFirstFilPicPath();
                        //添加到草稿箱中
                        saveToCache(outputFilePath, firstPic, intent);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
            );
        } catch (Exception e) {
            e.printStackTrace();
            loadingDialogDismiss();
        }
    }

    private void saveToCache(String outputFilePath, String firstPic, Intent intent) {
        if (mMovieItemDaoList == null || mMovieItemDaoList.isEmpty()) {
            return;
        }
        initTimeKey();

        //遍历删除所有的视频文件和数据库数据
        if (mMovieItemDbDao == null) {
            mMovieItemDbDao = GreenDaoManager.getInstance().getMovieItemDao();
        }
        //修改所有的时间
        for (int i = 0; i < mMovieItemDaoList.size(); i++) {
            MovieItemDb movieItemDb = mMovieItemDaoList.get(i);
            movieItemDb.setKey(timeKey);
            movieItemDb.setFrom(VIDEO_PUSH);
            mMovieItemDbDao.update(movieItemDb);
        }

        //添加新的文件
        MovieItemDb movieItemDb = new MovieItemDb();
        movieItemDb.setUid(UserInfoManager.getUid() + "");
        if (mMovieItemDb != null) {
            movieItemDb.setPicList(mMovieItemDb.getPicList());
        }
        movieItemDb.setFrom(VIDEO_PUSH);
        movieItemDb.setKey(timeKey);
        movieItemDb.setVideoTime(Double.parseDouble(IvyUtils.getMediaDuration(outputFilePath)) / 1000);
        movieItemDb.setFilePath(outputFilePath);
        movieItemDb.setFilPicPath(firstPic);
        movieItemDb.setIsScreenRecord(mMovieItemDaoList.get(0).getIsScreenRecord());
        //判断是否有本地视频
        for (MovieItemDb movieItem : mMovieItemDaoList) {
            if (!movieItem.getVideo_from()) {
                movieItemDb.setVideo_from(true);
                break;
            }
        }

        intent.putExtra("record_model", movieItemDb);

        mMovieItemDbDao.insert(movieItemDb);
    }

    // ----------------------------- 视频合成 end  -----------------------------------

    // ------------ finish current activity  start  -----------------
    @Override
    public boolean isNeedRxBus() {
        return true;
    }

    @Subscribe(code = RxBusCode.ACT_FINISH_VIDEO_EDIT, threadMode = ThreadMode.MAIN)
    public void actFinish(String isFinish) {
        if ("true".equals(isFinish) && !this.isFinishing()) {
            //删除文件
            deleteCacheFile(mMovieItemDaoList, false, false);
            mMovieItemDbDao.deleteInTx(mMovieItemDaoList);
            this.finish();
        }
    }
    // ------------ finish current activity  end  -----------------


    // ------------ 视频裁剪  start  -----------------

    private void initAdapter() {
        // 如果是本地视频页面跳转过来不需要显示视频剪辑
        Intent intent = getIntent();
        if (intent.hasExtra("mFromAct") && intent.getBooleanExtra("mFromAct", false)) {
            mVideoEditPlayUtils.setVideoSizeAdapter(true); // 本地视频需要视频宽高比
            rvShootRecyclerView.setVisibility(View.GONE);
            return;
        }
        mVideoEditPlayUtils.setVideoSizeAdapter(false); // 本地视频需要视频宽高比
        // 否则需要显示
        rvShootRecyclerView.setVisibility(View.VISIBLE);
        mMovieShowListAdapter = new MovieShowListAdapter(R.layout.adapter_movie_show_list, mVideoEditPlayUtils.getListMovieItemModel(), null, mVideoEditPlayUtils.getVideoFileName(), true);
        mMovieShowListAdapter.setDeleteListener((int pos) -> {
            // 如果减去当前item还是大于5s，就直接删除并更新标识，否则弹框提示，删除就更新标识
            if (mMovieShowListAdapter.recordTotalTimeExpertPos(pos) >= 5) {


                needRefreshLastData = true;
                mVideoEditPlayUtils.resetAllPlayer(); // 重播音视频

                deleteItemAndUpdate(pos);
                return;
            }
            if (mCustomProgress != null) {
                String tips = getString(R.string.video_edit_delete);
                if (mMovieItemDaoList.get(pos).getIsScreenRecord() == 1) {
                    tips = getString(R.string.video_call_record_delete);
                }
                mCustomProgress.show(tips,
                        getString(R.string.common_tips_title),
                        getString(R.string.common_cancel),
                        getString(R.string.common_ensure),
                        v -> mCustomProgress.dismiss(),
                        v -> {
                            mCustomProgress.dismiss();
                            // 删除数据并设置更新上一个页面的数据

                            needRefreshLastData = true;
                            deleteItemAndUpdate(pos);

                            // 更新上一页的数据
                            backRefresh();
                        },
                        true,
                        dialog -> mCustomProgress.dismiss());
            }
        });
        if (isPic) {
            rvShootRecyclerView.setVisibility(View.GONE);
            mStyleRecyclerView.setVisibility(View.VISIBLE);
        } else {
            rvShootRecyclerView.setVisibility(View.VISIBLE);
            mStyleRecyclerView.setVisibility(View.GONE);
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvShootRecyclerView.setLayoutManager(linearLayoutManager);
        rvShootRecyclerView.setAdapter(mMovieShowListAdapter);
        CustomItemTouchCallBack callBack = new CustomItemTouchCallBack(mMovieShowListAdapter.getData(), mMovieShowListAdapter, this);
        callBack.setListener(() -> {
            needRefreshLastData = true;
            mVideoEditPlayUtils.resetAllPlayer(); // 重播音视频
        });
        callBack.setOnTouchStatuListener(new CustomItemTouchCallBack.OnTouchStatuListener() {
            @Override
            public void touchStatu(CustomItemTouchCallBack.TouchStatus state) {
                mTouchStatus = state;
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callBack);
        itemTouchHelper.attachToRecyclerView(rvShootRecyclerView);

        if (isPic) {
            initStyleRecycler();
        }
    }

    private void initStyleRecycler() {
        List<SwitchModel> modelList = new ArrayList<>();
        modelList.add(new SwitchModel(SwitchModel.Type.NORMAL));
        modelList.add(new SwitchModel(SwitchModel.Type.QUICK));
        modelList.add(new SwitchModel(SwitchModel.Type.FADE));
        modelList.add(new SwitchModel(SwitchModel.Type.HORIZONTALSCROLLING));
        modelList.add(new SwitchModel(SwitchModel.Type.VERTICALSCROLLING));
        modelList.add(new SwitchModel(SwitchModel.Type.GRAY));

        mStyleRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mSwitchStyleAdapter = new SwitchStyleAdapter(modelList);
        mSwitchStyleAdapter.setSelectedItem(0);
        mSwitchStyleAdapter.setItemClickListener((parent, position, holder, view) -> {
            mItemType = mSwitchStyleAdapter.getItem(position).getType();
            //判断是否有数据
            int selectedItem = mSwitchStyleAdapter.getSelectedItem();
            if (selectedItem == position) {
                return;
            }

            if (mRightMusicLayout.isVisible()) {
                mRightMusicLayout.dismiss();
            }

            mSwitchStyleAdapter.setSelectedItem(position);

            picToMovie(mItemType);
        });
        mStyleRecyclerView.setAdapter(mSwitchStyleAdapter);
        //判断是否有音乐
        if (mMovieItemDb != null) {
            mItemType = mMovieItemDb.getTempletType();
            setSelectItem();
            String musicPath = mMovieItemDb.getMusicPath();
            if (!TextUtils.isEmpty(musicPath)) {
                File file = new File(musicPath);
                if (!file.exists()) {
                    requestBackMusic();
                }
            } else {
                requestBackMusic();
            }
        } else {
            requestBackMusic();
        }
    }

    private void requestBackMusic() {
        if (mCustomProgress != null && !mCustomProgress.isShowing()) {
            mCustomProgress.show(getString(R.string.loading), false, null);
        }
        mPresenter.loadBackgroundMusics(1, 100);
    }

    private void setSelectItem() {
        if (mSwitchStyleAdapter != null && mSwitchStyleAdapter.getDatas() != null && mSwitchStyleAdapter.getDatas().size() > 0) {
            List<SwitchModel> datas = mSwitchStyleAdapter.getDatas();
            for (int i = 0; i < datas.size(); i++) {
                SwitchModel switchModel = datas.get(i);
                if (switchModel.getType() == mItemType) {
                    mSwitchStyleAdapter.setSelectedItem(i);
                    break;
                }
            }
        }
    }

    @SuppressLint("CheckResult")
    private void picToMovie(int mItemType) {
        if (mCustomProgress != null && !mCustomProgress.isShowing()) {
            mCustomProgress.show(getString(R.string.loading), false, null);
        }
        //添加到模型中
        mSubscribe = Observable.create((ObservableOnSubscribe<String>) e -> {
            ImageBean imageBean = new ImageBean();
            File file = new File(createPicFile());

            imageBean.setOutputPath(file.getAbsolutePath());
            imageBean.setImagePathList(createPics());
            imageBean.setOutputType(mItemType);
            FFmpegRun.setImageToNativeForEncodeMP4(imageBean);
            e.onNext(file.getAbsolutePath());
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    if (mCustomProgress != null) {
                        mCustomProgress.dismiss();
                    }
                    //添加到模型中
                    if (mPreData != null && mPreData.getMovieItemModels() != null && mPreData.getMovieItemModels().size() > 0) {
                        MovieItemModel movieItemModel = mPreData.getMovieItemModels().get(0);
                        movieItemModel.setFilePath(s);
                        movieItemModel.setVideoTime(BigDecimal.valueOf(Double.parseDouble(IvyUtils.getMediaDuration(s)) / 1000));

                        //保存到数据库
                        if (mMovieItemDb != null) {
                            //删除以前的视频
                            if (!TextUtils.isEmpty(mMovieItemDb.getFilePath())) {
                                File file = new File(mMovieItemDb.getFilePath());
                                if (file.exists()) {
                                    file.delete();
                                }
                            }
                            mMovieItemDb.setFilePath(s);
                            mMovieItemDb.setTempletType(mItemType);
                            mMovieItemDb.setVideoTime(Double.parseDouble(IvyUtils.getMediaDuration(s)) / 1000);
                            //重新更新
                            if (mMovieItemDbDao != null) {
                                mMovieItemDbDao.update(mMovieItemDb);
                            }
                        }
                    }
                    //判断是否有数据
                    if (mMusicBgBean == null || mMusicBgBean.size() == 0) {
                        requestBackMusic();
                    } else {
                        for (int i = 0; i < mMusicBgBean.size(); i++) {
                            MusicBean2 musicBean2 = mMusicBgBean.get(i);
                            if (mItemType == musicBean2.getTemplateId()) {
                                loadMusic(musicBean2.getId() + "", musicBean2.getMusicName(), musicBean2.getMusicLike(), false);
                                break;
                            }
                        }
                    }
                }, throwable -> {
                    if (mCustomProgress != null) {
                        mCustomProgress.dismiss();
                    }
                });
    }

    private ArrayList<String> createPics() {
        if (mMovieItemDb != null) {
            if (!TextUtils.isEmpty(mMovieItemDb.getPicList())) {
                if (mMovieItemDb.getPicList().contains(",")) {
                    String[] split = mMovieItemDb.getPicList().split(",");
                    return new ArrayList<>(Arrays.asList(split));
                } else {
                    ArrayList<String> list = new ArrayList<>();
                    list.add(mMovieItemDb.getPicList());
                    return list;
                }
            }
        }
        return new ArrayList<>();
    }

    private String createPicFile() {
        String videoFile = FileUtill.createSaveFile(this, FileUtill.getDefaultDir(this) + "/img");
        return videoFile + "/" + String.format("img-output-%s.mp4", System.currentTimeMillis() + "");
    }

    private void deleteItemAndUpdate(int pos) {
        initTimeKey();
        changeCacheKey();
        //删除数据并且刷新数据库
        if (mMovieItemDaoList != null && mMovieItemDaoList.size() > pos && mMovieItemDbDao != null) {
            MovieItemDb movieItemDb = mMovieItemDaoList.get(pos);
            mMovieItemDbDao.delete(movieItemDb);
            mMovieItemDaoList.remove(movieItemDb);
            mMovieShowListAdapter.deleteItem(pos);

            deleteCacheFile(movieItemDb, true, true);
            mVideoEditPlayUtils.resetAllPlayer();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //处理剪辑回来的视频
        if (resultCode == PreviewMovieActivity.INTENTBACKCODE && MovieShowListAdapter.request_code == requestCode) {
            refreshAdapter(data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void changeCacheKey() {
        //判断是不是第一次修改
        if (hasChangeRecord) {
            //先遍历集合修改所有的key
            if (mMovieItemDaoList != null && mMovieItemDaoList.size() > 0) {
                for (MovieItemDb movieItemDb : mMovieItemDaoList) {
                    movieItemDb.setKey(timeKey);
                }
                //更新数据库
                mMovieItemDbDao.updateInTx(mMovieItemDaoList);
            }
            hasChangeRecord = false;
        }
    }

    private void refreshAdapter(Intent data) {
        if (data != null) {
            //刷新数据
            int position = data.getIntExtra(PreviewMovieActivity.VIDEO_POSITION, -1);
            BigDecimal videoTime = (BigDecimal) data.getSerializableExtra(PreviewMovieActivity.VIDEO_TIME);
            String video_path = data.getStringExtra(PreviewMovieActivity.VIDEO_PATH);
            if (mMovieShowListAdapter != null && position >= 0 && mMovieShowListAdapter.getData().size() > position) {
                //初始化时间
                initTimeKey();

                changeCacheKey();

                MovieItemModel movieItemModel = mMovieShowListAdapter.getData().get(position);
                //获取原来的文件地址删除
                String filePath = movieItemModel.getFilePath();
                if (!TextUtils.isEmpty(filePath)) {
                    if (!filePath.endsWith(video_path)) {
                        deleteCacheFile(movieItemModel, false, false);

                        movieItemModel.setVideoTime(videoTime);
                        movieItemModel.setFilePath(video_path);
                        mMovieShowListAdapter.notifyItemChanged(position);

                        //添加重新修改数据库数据
                        if (mMovieItemDaoList != null && mMovieItemDaoList.size() > position && mMovieItemDbDao != null) {
                            changeCacheData(position, videoTime, video_path);
                        }
                    }
                }

                needRefreshLastData = true;
                mVideoEditPlayUtils.resetAllPlayer(); // 重播音视频
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

    private void backRefresh() {
        if (isPic) {
            //删除数据
            if (mFrom != RecordActivity.CACHE) {
                //弹框
                showBackDialog(getString(R.string.record_abandon_movie));
            } else {
                //修改标记
                if (mMovieItemDb != null) {
                    mMovieItemDb.setFrom(VIDEO_EDIT);
                    mMovieItemDbDao.update(mMovieItemDb);
                }
                finish();
            }
        } else {
            if (needRefreshLastData) {
                Intent intent = new Intent();
                intent.putExtra("edit_model", new MovieEditModel(null, mMovieShowListAdapter.getData()));
                intent.putExtra("record_model", mMovieItemDaoList);
                if (mFrom == RecordActivity.CACHE  && (mMovieItemDb == null || (mMovieItemDb != null && mMovieItemDb.getIsScreenRecord() != 1))) {
                    intent.putExtra("from", mFrom);
                    intent.setClass(this, RecordActivity.class);
                    startActivity(intent);
                }
                RxBus.get().send(RxBusCode.ACT_VIDEO_RECORD_REFRESH_BY_EDIT, intent);
                finish();
            } else {
                if (mFrom == RecordActivity.CACHE) {
                    if (mMovieItemDaoList != null &&
                            mMovieItemDaoList.size() > 0 &&
                            mMovieItemDaoList.get(0).getIsScreenRecord() == 1) {
                        //录屏视频
                        changeCacheStatus(VIDEO_EDIT);
                        finish();
                        return;
                    }
                    //进入录制界面，修改数据库数据
                    Intent intent = new Intent(this, RecordActivity.class);
                    intent.putExtra("record_model", mMovieItemDaoList);
                    intent.putExtra("from", mFrom);
                    startActivity(intent);
                } else if (mFrom == VideoCallRecordActivity.VIDEO_CALL_RECORD) {

                } else {
                    changeCacheStatus(VIDEO_RECORD);
                }
                finish();
            }
        }
    }

    private void showBackDialog(String content) {
        if (mCustomProgress != null) {
            mCustomProgress.showLeftEnsure(content, getString(R.string.common_notice_title), "", "",
                    v -> mCustomProgress.dismiss(), v -> {
                        mCustomProgress.dismiss();
                        finish();
                        //删除数据库数据
                        if (mMovieItemDbDao != null) {
                            mMovieItemDbDao.deleteInTx(mMovieItemDaoList);
                        }
                        //删除文件
                        if (mMovieItemDb != null) {
                            deleteCacheFile(mMovieItemDb, false, true);
                            mMovieItemDbDao.delete(mMovieItemDb);
                        }
                    }, true, dialog -> mCustomProgress.dismiss());
        }
    }

    /**
     * 修改数据库中视频数据
     *
     * @param from 下次从草稿箱进入的页面
     */
    private void changeCacheStatus(int from) {
        if (mMovieItemDaoList != null && mMovieItemDaoList.size() > 0) {
            for (int i = 0; i < mMovieItemDaoList.size(); i++) {
                MovieItemDb movieItemDb = mMovieItemDaoList.get(i);
//                movieItemDb.setVoicePath("");
//                movieItemDb.setMusicPath("");
//                movieItemDb.setMusicName("");
//                Log.e("保存音乐的路径path===", movieItemDb.getMusicPath() + ",,,,,音乐名==" + movieItemDb.getMusicName());
                movieItemDb.setFrom(from);
                //更新数据
                if (mMovieItemDbDao == null) {
                    mMovieItemDbDao = GreenDaoManager.getInstance().getMovieItemDao();
                }
                mMovieItemDbDao.update(movieItemDb);
            }
        }
    }

    // ------------ 视频裁剪  end  -----------------


    ////////////////////// //音频剪辑控件/////////////////////////

    private void initSrcollView(int width) {
        if (mScrollView != null) {
            mScrollView.fullScroll(View.FOCUS_UP);
        }
        layout.removeAllViews();
        Drawable drawable = getResources().getDrawable(R.drawable.repeat_bg);
        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                width,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        imageView.setLayoutParams(params);
        imageView.setBackground(drawable);
        layout.addView(imageView, params);
    }

    private void getClipTotalWidth(long duration, boolean needClipLayout) {
        initClipAndVideoData();
        if (needClipLayout) {
            mVideoEditMusicLayout.setVisibility(View.VISIBLE);
        } else {
            mVideoEditMusicLayout.setVisibility(View.GONE);
        }
        rvShootRecyclerView.setVisibility(View.GONE);
        mClipLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                                                        @Override
                                                                        public void onGlobalLayout() {
                                                                            isClipTotalOpen = true;
                                                                            mClipTotalWidth = mClipLayout.getWidth();
                                                                            int leftClipWidth = mClipBgLeft.getWidth();
                                                                            int rightClipWidth = mClipBgRight.getWidth();

                                                                            double offer = mClipTotalWidth / (totalTime * 1000.0f);
                                                                            srcollWidth = (int) (offer * duration) - leftClipWidth - rightClipWidth;
                                                                            if (srcollWidth < mClipTotalWidth - leftClipWidth - rightClipWidth) {
                                                                                srcollWidth = mClipTotalWidth - leftClipWidth - rightClipWidth;
                                                                            }
                                                                            initSrcollView(srcollWidth);

                                                                            //设置layout的宽度
                                                                            mClipLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                                                        }
                                                                    }
        );
    }

    private void initLeftTouch() {
        mClipBgLeft.setOnTouchListener(new View.OnTouchListener() {

            private RelativeLayout.LayoutParams mTimeLayoutParams;
            private int mSecondMiddleWidth;
            private int mRightClipWidth;
            private int mLeftClipWidth;
            private RelativeLayout.LayoutParams mRightLayoutParams;
            private float mLeftDownX;
            private RelativeLayout.LayoutParams mLeftClipParams;
            private FrameLayout.LayoutParams mLeftMovieParams;

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //获取左右两个滑动控件的宽度
                        mLeftClipWidth = mClipBgLeft.getWidth();
                        mRightClipWidth = mClipBgRight.getWidth();
                        //获取一秒对应的宽度
                        BigDecimal secondMiddleWidthDecimal = getSecondMiddleWidth(mLeftClipWidth, mRightClipWidth, 0);
                        if (secondMiddleWidthDecimal != null) {
                            mSecondMiddleWidth = secondMiddleWidthDecimal.intValue();
                        }
                        mLeftClipParams = (RelativeLayout.LayoutParams) mClipBgLeft.getLayoutParams();
                        mRightLayoutParams = (RelativeLayout.LayoutParams) mClipBgRight.getLayoutParams();
                        mLeftMovieParams = (FrameLayout.LayoutParams) mLeftMovieBg.getLayoutParams();
                        mTimeLayoutParams = (RelativeLayout.LayoutParams) mClipTime.getLayoutParams();
                        mLeftDownX = event.getX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //计算时间
                        mLeftClipParams.leftMargin += (int) (event.getX() - mLeftDownX);
                        if (mLeftClipParams.leftMargin < 0) {
                            mLeftClipParams.leftMargin = 0;
                        }

                        //计算左两边的时间值
                        //根据右边的时间计算左边最多能滑动多少
                        int maxLeftMargin = calCanScrollWidth(mLeftClipWidth, mRightClipWidth, mSecondMiddleWidth, mRightLayoutParams.rightMargin).intValue();

                        if (mLeftClipParams.leftMargin >= maxLeftMargin) {
                            mLeftClipParams.leftMargin = maxLeftMargin;
                        }

                        //滑动时间
                        mTimeLayoutParams.leftMargin = mLeftClipParams.leftMargin;
                        mClipTime.setLayoutParams(mTimeLayoutParams);

                        //计算左边的时间
                        calLeftCurrentTime(mLeftClipWidth, mRightClipWidth, mLeftClipParams.leftMargin, mRightLayoutParams.rightMargin);

                        mClipBgLeft.setLayoutParams(mLeftClipParams);

                        //让左边的剪切view显示
                        mLeftMovieParams.width = mLeftClipParams.leftMargin;
                        if (mLeftMovieParams.width < 0) {
                            mLeftMovieParams.width = 0;
                        }
                        mLeftMovieBg.setLayoutParams(mLeftMovieParams);
                        break;
                    case MotionEvent.ACTION_UP:
                        //设置播放的位置
                        setAudioPositionPlay();
                        break;
                }
                return true;
            }
        });
    }

    /**
     * 计算当前中间的时间
     */
    private void calLeftCurrentTime(int leftClipWidth, int rightClipWidth, int leftClipMargin, int rightClipMargin) {
        BigDecimal middleWidth = calCanScrollWidth(leftClipWidth, rightClipWidth, leftClipMargin, rightClipMargin);
        BigDecimal secondMiddleWidth = getSecondMiddleWidth(leftClipWidth, rightClipWidth, 3);

        if (secondMiddleWidth != null) {
            BigDecimal currentTime = middleWidth.divide(secondMiddleWidth, 1, BigDecimal.ROUND_HALF_UP);
            mClipTime.setText(String.format(getString(R.string.common_second), currentTime));
            //获取右边的时间,加上宽度遮罩的大小
            BigDecimal rightLeaveTime = BigDecimal.valueOf(rightClipMargin).divide(secondMiddleWidth, 1, BigDecimal.ROUND_HALF_UP);
            //用总时间减去右边和中间的就是左边的时间
            mStartTime = BigDecimal.valueOf(totalTime).subtract(rightLeaveTime).subtract(currentTime).doubleValue() * 1000;
/*
            KLog.i("mStartTime: " + mStartTime + " currentTime:" + currentTime + " rightLeaveTime: " + rightLeaveTime + " totalTime:" + totalTime);
*/
        }
    }

    /**
     * 计算右边的时间
     */
    private void calRightCurrentTime(int leftClipWidth, int rightClipWidth, int leftClipMargin, int rightClipMargin) {
        BigDecimal middleWidth = calCanScrollWidth(leftClipWidth, rightClipWidth, leftClipMargin, rightClipMargin);
        BigDecimal secondMiddleWidth = getSecondMiddleWidth(leftClipWidth, rightClipWidth, 3);


        if (secondMiddleWidth != null) {
            BigDecimal currentTime = middleWidth.divide(secondMiddleWidth, 1, BigDecimal.ROUND_HALF_UP);
            mClipTime.setText(String.format(getString(R.string.common_second), currentTime));
            //获取左边的时间，右边的偏移量加上宽度遮罩的大小
            BigDecimal rightLeaveTime = BigDecimal.valueOf(leftClipMargin).divide(secondMiddleWidth, 1, BigDecimal.ROUND_HALF_UP);
            //用总时间减去左边和中间的就是左边的时间
            mEndTime = BigDecimal.valueOf(totalTime).subtract(BigDecimal.valueOf(totalTime).subtract(rightLeaveTime).subtract(currentTime)).doubleValue() * 1000;
/*
            KLog.i("mEndTime: " + mEndTime + " currentTime:" + currentTime + " rightLeaveTime: " + rightLeaveTime + " totalTime:" + totalTime);
*/
        }
    }

    /**
     * 计算能够滑动多少距离
     */
    private BigDecimal calCanScrollWidth(int leftClipWidth, int rightClipWidth, int middleWidth, int clipMargin) {
        //根据左边右边中间的宽度计算左边最大的margin
        return BigDecimal.valueOf(mClipTotalWidth)
                .subtract(BigDecimal.valueOf(leftClipWidth))
                .subtract(BigDecimal.valueOf(rightClipWidth))
                .subtract(BigDecimal.valueOf(middleWidth))
                .subtract(BigDecimal.valueOf(clipMargin));
    }


    private BigDecimal getSecondMiddleWidth(int leftClipWidth, int rightClipWidth, int scale) {
        if (mVideoEditPlayUtils != null) {
            return BigDecimal.valueOf(mClipTotalWidth).
                    subtract(BigDecimal.valueOf(leftClipWidth)).
                    subtract(BigDecimal.valueOf(rightClipWidth))
                    .divide(BigDecimal.valueOf(totalTime), scale, BigDecimal.ROUND_HALF_UP);
        }
        return null;
    }


    private void initRightTouch() {
        mClipBgRight.setOnTouchListener(new View.OnTouchListener() {
            private RelativeLayout.LayoutParams mClipTimeLayoutParams;
            private RelativeLayout.LayoutParams mLeftLayoutParams;
            private int mSecondMiddleWidth;
            private int mRightClipWidth;
            private int mLeftClipWidth;
            private float mRightDownX;
            private RelativeLayout.LayoutParams mRightClipParams;
            private FrameLayout.LayoutParams mRightMovieParams;

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //获取左右两个滑动控件的宽度
                        //获取左右两个滑动控件的宽度
                        mLeftClipWidth = mClipBgLeft.getWidth();
                        mRightClipWidth = mClipBgRight.getWidth();
                        BigDecimal secondMiddleWidthDecimal = getSecondMiddleWidth(mLeftClipWidth, mRightClipWidth, 0);
                        if (secondMiddleWidthDecimal != null) {
                            mSecondMiddleWidth = secondMiddleWidthDecimal.intValue();
                        }
                        mRightDownX = event.getX();
                        mLeftLayoutParams = (RelativeLayout.LayoutParams) mClipBgLeft.getLayoutParams();
                        mRightClipParams = (RelativeLayout.LayoutParams) mClipBgRight.getLayoutParams();
                        mRightMovieParams = (FrameLayout.LayoutParams) mRightMovieBg.getLayoutParams();
                        mClipTimeLayoutParams = (RelativeLayout.LayoutParams) mClipTime.getLayoutParams();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mRightClipParams.rightMargin += mRightDownX - event.getX();
                        if (mRightClipParams.rightMargin < 0) {
                            mRightClipParams.rightMargin = 0;
                        }

                        //计算左两边的时间值
                        //根据右边的时间计算左边最多能滑动多少
                        int maxRightMargin = calCanScrollWidth(mLeftClipWidth, mRightClipWidth, mSecondMiddleWidth, mLeftLayoutParams.leftMargin).intValue();
                        if (mRightClipParams.rightMargin >= maxRightMargin) {
                            mRightClipParams.rightMargin = maxRightMargin;
                        }
                        mClipBgRight.setLayoutParams(mRightClipParams);

                        //改变textView的宽度就能让文字移动了
                        mClipTimeLayoutParams.rightMargin = mRightClipParams.rightMargin;
                        mClipTime.setLayoutParams(mClipTimeLayoutParams);

                        //计算右边的时间
                        calRightCurrentTime(mLeftClipWidth, mRightClipWidth, mLeftLayoutParams.leftMargin, mRightClipParams.rightMargin);
                        //显示右边的view
                        mRightMovieParams.width = mRightClipParams.rightMargin;
                        if (mRightMovieParams.width < 0) {
                            mRightMovieParams.width = 0;
                        }
                        mRightMovieBg.setLayoutParams(mRightMovieParams);
                        break;
                    case MotionEvent.ACTION_UP:
                        //设置播放的位置
                        setAudioPositionPlay();
                        break;
                }
                return true;
            }
        });
    }

    private void setAudioPositionPlay() {
        if (mVideoEditPlayUtils != null) {
            mVideoEditPlayUtils.setMusicSeekPos((int) (mChangetmX + mStartTime), (int) (mChangetmX + mEndTime), (int) (mVideoEditPlayUtils.getVideoTotalTime() * 1000));
            mVideoEditPlayUtils.resetAllPlayer(); // 重播音视频
        }
    }

    public void resetClipView(long duration, boolean needClipLayout) {
//        mMovieItemModel = mMovieModels.get(position);

        RelativeLayout.LayoutParams leftLayoutParams = (RelativeLayout.LayoutParams) mClipBgLeft.getLayoutParams();
        RelativeLayout.LayoutParams rightLayoutParams = (RelativeLayout.LayoutParams) mClipBgRight.getLayoutParams();
        FrameLayout.LayoutParams leftMoveLayoutParams = (FrameLayout.LayoutParams) mLeftMovieBg.getLayoutParams();
        FrameLayout.LayoutParams rightMoveLayoutParams = (FrameLayout.LayoutParams) mRightMovieBg.getLayoutParams();
        RelativeLayout.LayoutParams timeLayoutParams = (RelativeLayout.LayoutParams) mClipTime.getLayoutParams();
        leftLayoutParams.leftMargin = 0;
        rightLayoutParams.rightMargin = 0;
        leftMoveLayoutParams.width = 0;
        rightMoveLayoutParams.width = 0;
        //设置左边右边的view的位置
        mClipBgLeft.setLayoutParams(leftLayoutParams);
        mClipBgRight.setLayoutParams(rightLayoutParams);
        mLeftMovieBg.setLayoutParams(leftMoveLayoutParams);
        mRightMovieBg.setLayoutParams(rightMoveLayoutParams);

        //让文字居中
        timeLayoutParams.leftMargin = 0;
        timeLayoutParams.rightMargin = 0;
        mClipTime.setLayoutParams(timeLayoutParams);
        getClipTotalWidth(duration, needClipLayout);

    }

    /**
     * 初始化界面跟video的数据
     */
    private void initClipAndVideoData() {
        //初始化开始结束时间
        mStartTime = 0.0;
        mEndTime = totalTime * 1000.0f;
        mChangetmX = 0;
//        mClipTime.setText(String.format(getString(R.string.common_second), mMovieItemModel.getVideoTime()));
    }


    @Subscribe(code = RxBusCode.ACT_VIDEO_PUSH_REFRESH_BACK, threadMode = ThreadMode.MAIN)
    public void actRefreshAdapter(Intent intent) {
        //获取数据
        if (intent.hasExtra("record_model")) {
            Serializable record_model = intent.getSerializableExtra("record_model");
            if (record_model instanceof MovieItemDb) {
                MovieItemDb movieItemDb = (MovieItemDb) record_model;
                //删除数据库数据
                if (mMovieItemDbDao == null) {
                    mMovieItemDbDao = GreenDaoManager.getInstance().getMovieItemDao();
                }
                //删除文件
                deleteCacheFile(movieItemDb, false, false);
                mMovieItemDbDao.delete(movieItemDb);

                //修改标志位
                //修改所有的时间
                if (mMovieItemDaoList != null && mMovieItemDaoList.size() > 0) {
                    for (int i = 0; i < mMovieItemDaoList.size(); i++) {
                        MovieItemDb editMovieItemDb = mMovieItemDaoList.get(i);
                        movieItemDb.setFrom(VIDEO_EDIT);
                        mMovieItemDbDao.update(editMovieItemDb);
                    }
                }
            }
        }
    }

    @Subscribe(code = RxBusCode.ACT_VIDEO_PUSH_REFRESH_CACHE, threadMode = ThreadMode.MAIN)
    public void actCache() {
        //删除当前的文件并关闭界面
        finish();
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
                        if (!filePath.contains("videoCall_record")) {
                            file.delete();
                        }
                        FileUtil.fileDeleteScanVideo(VideoEditActivity.this,filePath);
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
                            deleteCacheFile(itemO, needDelete, true);
                        }
                    } else {
                        deleteCacheFile(itemO, needDelete, true);
                    }
                }
            }
        }
    }

    private IMediaPlayer.OnPlayNextListener listener = new IMediaPlayer.OnPlayNextListener() {
        @Override
        public void onNextPlay(int position) {
            if (mTouchStatus != CustomItemTouchCallBack.TouchStatus.DOWN) {
                if (mMovieShowListAdapter != null){
                    mMovieShowListAdapter.notifyAll(position);
                }
            }
        }
    };

    @Override
    public void onLoadBackgroundMusics(@Nullable MusicList data) {
        if (data != null) {
            if (mCustomProgress != null && mCustomProgress.isShowing()) {
                mCustomProgress.dismiss();
            }
            mMusicBgBean = data.getList();
            if (mMusicBgBean != null && mMusicBgBean.size() > 0) {
                MusicBean2 musicBean2;
                if (mItemType == -1001 || mItemType == 0) {
                    musicBean2 = mMusicBgBean.get(0);
                    loadMusic(musicBean2.getId() + "", musicBean2.getMusicName(), musicBean2.getMusicLike(), false);
                } else {
                    for (int i = 0; i < mMusicBgBean.size(); i++) {
                        musicBean2 = mMusicBgBean.get(i);
                        if (mItemType == musicBean2.getTemplateId()) {
                            loadMusic(musicBean2.getId() + "", musicBean2.getMusicName(), musicBean2.getMusicLike(), false);
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onLoadBackgroundMusicsError() {
        if (mCustomProgress != null && mCustomProgress.isShowing()) {
            mCustomProgress.dismiss();
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
}