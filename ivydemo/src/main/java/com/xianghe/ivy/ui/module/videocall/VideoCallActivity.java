package com.xianghe.ivy.ui.module.videocall;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.StringUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.tangyx.video.ffmpeg.FFmpegUtils;
import com.xianghe.ivy.R;
import com.xianghe.ivy.app.IvyApp;
import com.xianghe.ivy.app.IvyConstants;
import com.xianghe.ivy.app.em.EMManager;
import com.xianghe.ivy.app.em.EMMessageHelper;
import com.xianghe.ivy.app.em.OnlinePreferenceManager;
import com.xianghe.ivy.app.greendao.GreenDaoManager;
import com.xianghe.ivy.constant.Global;
import com.xianghe.ivy.entity.dao.MovieItemDbDao;
import com.xianghe.ivy.entity.db.MovieItemDb;
import com.xianghe.ivy.manager.PermissionManager;
import com.xianghe.ivy.manager.UserInfoManager;
import com.xianghe.ivy.model.VideoCallBean;
import com.xianghe.ivy.mvp.BaseVideoCallActivity;
import com.xianghe.ivy.ui.module.record.adapter.CustomItemTouchCallBack;
import com.xianghe.ivy.ui.module.record.adapter.MovieShowListAdapter;
import com.xianghe.ivy.ui.module.record.model.MovieItemModel;
import com.xianghe.ivy.ui.module.videocall.service.FileUtil;
import com.xianghe.ivy.ui.module.videocall.service.ScreenRecordService;
import com.xianghe.ivy.ui.module.videocalledit.VideoCallRecordActivity;
import com.xianghe.ivy.utils.IvyUtils;
import com.xianghe.ivy.utils.KLog;
import com.xianghe.ivy.utils.NotificationUtils;
import com.xianghe.ivy.utils.ScreenRecordUtil;
import com.xianghe.ivy.utils.ToastUtil;
import com.xianghe.ivy.utils.XClickUtil;
import com.xianghe.ivy.weight.CustomProgress;
import com.xianghe.ivy.weight.dialog.LoadingDialog;
import com.xianghe.ivy.weight.indicator.LoadingIndicatorView;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class VideoCallActivity extends BaseVideoCallActivity implements View.OnClickListener,
        MovieShowListAdapter.onDeleteListener, View.OnTouchListener {
    private static final String TAG = "VideoCallActivity";

    private View mVcViewRoot;
    private FrameLayout mVcFrameRemoteVideoViewContainer;
    private FrameLayout mVcFrameLocalVideoViewContainer;
    private View mMoveView;
    private ImageView mVcIvBack;
    private ImageView mVcIvRecord;
    private ImageView mVcIvSwitch;
    private TextView mVcTvVcTime;
    private TextView mVcTvVcTimeTip;
    private TextView mVcTvDiaRecordTime;
    private ImageView mVcIvDiaRecordTime;
    private LinearLayout mVcLlRecordTime;
    private ImageView mVcIvAcceptRecordTime;
    private TextView mVcTvAcceptRecordTime;
    private RecyclerView mVcRvDiaRecord;

    // loading ?????????
    private ViewStub mVcFrameRemoteVideoViewLoading;
    private ImageView mIvVcWaitBg;
    private CircleImageView mIvVcWaitAvatar;
    private ImageView mIvVcWaitAvatarLogo;
    private TextView mIvVcWaitUsername;
    private LoadingIndicatorView mIvVcWaitLoading;

    // permission
    public static final String REQUEST_CODE_PERMISSIONS_1100 = "1100";
    private PermissionManager.PermissionListener mPermissionListener;
    private boolean hasPermission = false; // ??????????????????????????????????????????????????????

    private PowerManager.WakeLock mWakeLock;// ??????????????????
    private int screenWidth;

    // ????????????S
//    private VideoCallManager mVideoCallManager;
//    private VideoCallManager.Listener mCallListener;


    private NotificationManager mNotificationManager;

    // params
    private VideoCallBean mCallBean;// ????????? ?????????????????????????????????uid??????????????????????????????????????????????????????????????????
    private boolean isOutGoing;// ??????????????????

    // ?????????  ??????????????????
    private String outGoingUserName = null; // ??????
    private String outGoingAvatar = null; // ??????
    private int outGoingSex = -1;   // ??????

    // ?????????
    private Disposable timeTask;  // ???????????? ???????????????????????????????????????????????????
    private Disposable timeCallTask; // ????????????
    private Disposable timeRecordTask; // ????????????
    private Disposable timeRecordTimTask; // ???????????????


    private MovieShowListAdapter mMovieShowListAdapter;
    private ArrayList<MovieItemDb> mMovieItemDaoList;
    private MovieItemDbDao mMovieItemDbDao;

    private ServiceConnection mServiceConnection;
    /**
     * ????????????????????????
     */
    private boolean isSavingRecord;

    /**
     * ????????????????????????
     */
    private boolean isRecording;

    /**
     * ????????????????????????????????????
     */
    private boolean isGetComingBack;

    /**
     * ????????????????????????
     */
    private boolean isOtherRecording;
    /**
     * ??????????????????????????????????????????
     */
    private boolean isRecordingFinishActivity;

    /**
     * ???????????????????????????????????????
     */
    private String mAudioName;

    private double mRecordTotalTime;
    /**
     * ?????????????????????????????????????????????
     */
    private long mTimeKey;

    // ??????????????????
    private LoadingDialog mLoadingDialog;

    private InnerReceiver mInnerReceiver;

    private ScreenRecordUtil.RecordListener recordListener = new ScreenRecordUtil.RecordListener() {
        @Override
        public void onStartRecord(String path) {
            KLog.e("=====onStartRecord:" + path);
            // ????????????????????????????????????????????????
            if (mCallBean != null) {
                EMManager.INSTANCE.sendTxtAttrSendMessage(EMMessageHelper.RECORD_START, mCallBean.getAccept_username(), mCallBean.getRoomID(), true);
            }
            if (mTimeKey == 0) {
                if (mMovieItemDaoList != null && mMovieItemDaoList.size() > 0) {
                    //?????????????????????????????????????????????????????????
                    mTimeKey = mMovieItemDaoList.get(0).getKey();
                } else {
                    //???????????????????????????
                    mTimeKey = System.currentTimeMillis();
                }
            }
            // ????????????????????? ????????????UI
            mVcTvDiaRecordTime.setVisibility(View.VISIBLE);
            mVcTvDiaRecordTime.setText("00:00");
            mVcIvDiaRecordTime.setVisibility(View.VISIBLE);
            String audioPath = null;
            if (path.contains(".") && path.contains("/")) {
                String videoName = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
                mAudioName = System.currentTimeMillis() + "_" + videoName + ".aac";
                audioPath = IvyUtils.createVideoCallRecordDirs() + File.separator + mAudioName;
            }
            //audioPath=/storage/emulated/0/1553661932383_1553661932536.wav
            KLog.e("audioPath=" + audioPath);
            //??????api
//            mVideoCallManager.startAudioRecording(audioPath, Constants.AUDIO_RECORDING_QUALITY_HIGH);
            isRecording = true;
            //??????????????? ???????????????????????????
        }

        @Override
        public void onPauseRecord() {
            KLog.e("=====onPauseRecord");

        }

        @Override
        public void onResumeRecord() {
            KLog.e("=====onResumeRecord");

        }

        @Override
        public void onStopRecord(String stopTip, int status, boolean isSaveRecordFile) {
            if (!isSavingRecord) {
                //??????????????????????????????
                isSavingRecord = true;
                // ????????????????????????????????????????????????
                if (mCallBean != null) {
                    EMManager.INSTANCE.sendTxtAttrSendMessage(EMMessageHelper.RECORD_END, mCallBean.getAccept_username(), mCallBean.getRoomID(), true);
                }
//                mVideoCallManager.stopAudioRecording();
                String recordVideoPath = ScreenRecordUtil.getScreenRecordFilePath();
                String recordAudioPath = IvyUtils.createVideoCallRecordDirs() + File.separator + mAudioName;
                isRecording = false;
                if (ScreenRecordService.STATUS_ERROR == status) {//??????????????????
                    //????????????UI
                    rfStatusRecordDiaTime(true, true, false);//???????????????????????????
                    ToastUtil.showToast(VideoCallActivity.this, stopTip);
                    FileUtil.deleteSDFile(recordVideoPath);//??????????????????
                    FileUtil.deleteSDFile(IvyUtils.createVideoCallRecordDirs() + File.separator + mAudioName);//??????????????????
                    isSavingRecord = false;
                    if (isRecordingFinishActivity) {
                        //?????????????????????????????????
                        closeActivity();
                    }
                    return;
                } else if (ScreenRecordService.STATUS_TIME_FULL == status) {//????????????????????????
                    //???????????????3??????
                    rfStatusRecordDiaTime(true, true, true);//????????????3?????????????????????
                    ToastUtil.showToast(VideoCallActivity.this, stopTip);
                    //??????????????????????????????
                }
                KLog.e("=====?????????????????? onStopRecord:" + recordVideoPath + ",stopTip:" + stopTip + ",status:" + status);
                // ??????????????????
                if (!isSaveRecordFile) {
                    //?????????????????????????????????????????????
                    FileUtil.deleteSDFile(recordVideoPath);//??????????????????
                    FileUtil.deleteSDFile(recordAudioPath);//??????????????????
                    isSavingRecord = false;
                    return;
                }
                // 1???????????????????????????,??????????????????
                try {
                    File recordAudioFile = new File(recordAudioPath);
                    File recordVideoFile = new File(recordVideoPath);
                    if (recordAudioFile != null && recordAudioFile.exists() && recordAudioFile.length() != 0 &&
                            recordVideoFile != null && recordVideoFile.exists() && recordVideoFile.length() != 0) {

                        editAudioVideoFile(recordAudioFile, recordVideoFile);

                    } else {
                        throw new Exception();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    KLog.e("????????????????????????");
                    FileUtil.deleteSDFile(recordVideoPath);//??????????????????
                    FileUtil.deleteSDFile(recordAudioPath);//??????????????????
                    if (isRecordingFinishActivity) {
                        //?????????????????????????????????
                        closeActivity();
                    }
                    isSavingRecord = false;
                }

            }
        }

        @Override
        public void onRecording(String timeTip) {
            KLog.e("=====onRecording timeTip:" + timeTip);
            if (mVcTvDiaRecordTime.getVisibility() == View.VISIBLE) {
                mVcTvDiaRecordTime.setText(timeTip);
            }
        }
    };

    /**
     * ???????????????????????????
     *
     * @param recordAudioFile
     * @param recordVideoFile
     */
    private void editAudioVideoFile(File recordAudioFile, File recordVideoFile) throws Exception {

        AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audio == null) {
            KLog.e("-----AudioManager??????");
            throw new Exception();
        }
        //????????????
        String recordVideoPath = recordVideoFile.getAbsolutePath();
        String recordAudioPath = recordAudioFile.getAbsolutePath();
        String duration = IvyUtils.getMediaDuration(recordVideoPath);
        double videoDurationDouble = StringUtils.isEmpty(duration) ? 0 : Double.valueOf(duration) / 1000f;
        //???????????????????????????180s
        videoDurationDouble = videoDurationDouble > 180 ? 180 : videoDurationDouble;
        BigDecimal videoDuration = new BigDecimal(videoDurationDouble).setScale(1, RoundingMode.HALF_UP);
        //??????????????????
        String videoPicPath = IvyUtils.getVideoFrame(IvyApp.getInstance().getApplicationContext(), recordVideoPath, 2);//????????????
        MovieItemModel movieItemModel = new MovieItemModel(recordVideoPath, videoDuration, videoPicPath);
        movieItemModel.setVideoTime(videoDuration);
        movieItemModel.setFilPicPath(videoPicPath);

        // 2????????????????????????????????????????????????????????????????????????
        MovieItemDb movieItemDb = new MovieItemDb();
        movieItemDb.setUid(String.valueOf(UserInfoManager.getUid()));
        movieItemDb.setKey(mTimeKey);
        movieItemDb.setVideoTime(videoDuration.doubleValue());
        movieItemDb.setFilPicPath(videoPicPath);
        movieItemDb.setFrom(IvyConstants.VIDEO_EDIT);
        movieItemDb.setIsScreenRecord(1);
        movieItemDb.setVideo_from(true);//?????????true,????????????????????????????????????????????????????????????

        FFmpegUtils fFmpegUtils = new FFmpegUtils(recordVideoFile.getParent());
        //????????????
        int currentVolume = audio.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
        KLog.e("????????????=" + currentVolume);
        if (currentVolume == 0) {
            //????????????????????????????????????
            currentVolume = audio.getStreamVolume(AudioManager.STREAM_SYSTEM);
        }
        if (currentVolume == 0) {
            //??????????????????0?????????????????????0??????
            currentVolume = 2;
        }
        if (recordAudioFile.length() == 0) {
            currentVolume = 0;
            KLog.e("?????????????????????0????????????????????????" + currentVolume);

        }
        List<MovieItemModel> list = new ArrayList<>();
        list.add(movieItemModel);

        if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
            mLoadingDialog.setBackListener(() -> showToast(getString(R.string.vc_generate_recording_video)));
            mLoadingDialog.show();
        }
        fFmpegUtils.audioAndVideoSynthesis(list, 0, videoDurationDouble,
                recordAudioPath, currentVolume,
                null, 0, 0, 0, true,
                outputFilePath -> {
                    KLog.e("----outputFilePath=" + outputFilePath);
                    //????????????????????????
                    updateMovieItemDb(movieItemModel, movieItemDb, outputFilePath);
//                    FileUtil.fileScanVideo(this, mRecordFilePath, mRecordWidth, mRecordHeight, mRecordSeconds);
                    //?????????????????????????????????????????????
                    KLog.e(TAG, "recordVideoPath=" + recordVideoPath);
                    KLog.e(TAG, "recordAudioPath=" + recordAudioPath);
                    FileUtil.deleteSDFile(recordVideoPath);//??????????????????
                    FileUtil.deleteSDFile(recordAudioPath);//??????????????????
                    FileUtil.fileScanVideo(VideoCallActivity.this, outputFilePath);
                    //??????loading???
                    if (mLoadingDialog != null) {
                        mLoadingDialog.cancel();
                        mLoadingDialog.setBackListener(null);
                    }
                    if (isRecordingFinishActivity) {
                        //?????????????????????????????????
                        closeActivity();
                    }
                    isSavingRecord = false;//??????????????????
                });
    }

    private void updateMovieItemDb(MovieItemModel movieItemModel, MovieItemDb movieItemDb, String outputFilePath) {
        movieItemModel.setFilePath(outputFilePath);
        movieItemDb.setFilePath(outputFilePath);
        mMovieItemDaoList.add(movieItemDb);
        //???????????????
        mMovieItemDbDao.insert(movieItemDb);
        mMovieShowListAdapter.addData(movieItemModel);
        //???????????????????????????
        mRecordTotalTime = updateRecordTotalTime(mMovieShowListAdapter.getData());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        KLog.d("onCreate");
        hideSystemNavigationBar();
        onCreateContentView(savedInstanceState);
        setContentView(R.layout.activity_video_call);

        initPreData();
        initView();
        initTouchListener();
        initData();
        initPermission();
        initRecordAdapter();
        startScreenRecordService();
        keepScreenOn(this, true);
        IvyUtils.createVideoCallRecordDirs();
        //????????????
        mInnerReceiver = new InnerReceiver();
        //??????????????????
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(mInnerReceiver, intentFilter);
    }


    private void initView() {
        mVcViewRoot = findViewById(R.id.vc_csl_root);
        mVcFrameRemoteVideoViewContainer = (FrameLayout) findViewById(R.id.vc_frame_remote_video_view_container);
        mVcFrameLocalVideoViewContainer = (FrameLayout) findViewById(R.id.vc_frame_local_video_view_container);
        mMoveView = (View) findViewById(R.id.moveView);
        mVcIvBack = (ImageView) findViewById(R.id.vc_iv_back);
        mVcIvRecord = (ImageView) findViewById(R.id.vc_iv_record);
        mVcIvSwitch = (ImageView) findViewById(R.id.vc_iv_switch);
        mVcTvVcTime = (TextView) findViewById(R.id.vc_tv_vc_time);
        mVcTvVcTimeTip = (TextView) findViewById(R.id.vc_tv_vc_time_tip);
        mVcTvDiaRecordTime = (TextView) findViewById(R.id.vc_tv_dia_record_time);
        mVcIvDiaRecordTime = (ImageView) findViewById(R.id.vc_iv_dia_record_time);
        mVcLlRecordTime = (LinearLayout) findViewById(R.id.vc_ll_record_time);
        mVcIvAcceptRecordTime = (ImageView) findViewById(R.id.vc_iv_accept_record_time);
        mVcTvAcceptRecordTime = (TextView) findViewById(R.id.vc_tv_accept_record_time);
        mVcRvDiaRecord = (RecyclerView) findViewById(R.id.vc_rv_dia_record);

        mVcViewRoot.setOnTouchListener(this);
        mVcIvBack.setOnClickListener(this);
        mVcIvRecord.setOnClickListener(this);
        mVcIvSwitch.setOnClickListener(this);
        mVcTvDiaRecordTime.setOnClickListener(this);
        mVcIvDiaRecordTime.setOnClickListener(this);
        mVcIvAcceptRecordTime.setOnClickListener(this);
        mVcTvAcceptRecordTime.setOnClickListener(this);
        mVcRvDiaRecord.setOnClickListener(this);
        mVcLlRecordTime.setOnClickListener(this);

        // loading
        mVcFrameRemoteVideoViewLoading = (ViewStub) findViewById(R.id.vc_frame_remote_video_view_loading);
        View mViewStubLayout = mVcFrameRemoteVideoViewLoading.inflate();
        mIvVcWaitBg = (ImageView) mViewStubLayout.findViewById(R.id.iv_vc_wait_bg);
        mIvVcWaitAvatar = (CircleImageView) mViewStubLayout.findViewById(R.id.iv_vc_wait_avatar);
        mIvVcWaitAvatarLogo = (ImageView) mViewStubLayout.findViewById(R.id.iv_vc_wait_avatar_logo);
        mIvVcWaitUsername = (TextView) mViewStubLayout.findViewById(R.id.iv_vc_wait_username);
        mIvVcWaitLoading = (LoadingIndicatorView) mViewStubLayout.findViewById(R.id.iv_vc_wait_loading);

        mLoadingDialog = new LoadingDialog(this);
        mLoadingDialog.setBackListener(null);
        if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
            mLoadingDialog.cancel();
        }

        clearAllStatus();
    }

    private void initData() {
        if (!isOutGoing()) { // ?????????
            setLocalPre(outGoingAvatar, outGoingSex);
        } else { // ?????????
            if (mCallBean != null) {
                setLocalPre(mCallBean.getAvatar(), mCallBean.getSex());
            }
        }
        mMovieItemDbDao = GreenDaoManager.getInstance().getMovieItemDao();
    }

    private void setLocalPre(String avatar, int sex) {
        Glide.with(this)
                .load(avatar)
                .apply(new RequestOptions()
                        .placeholder(R.mipmap.ic_my_head_default)   //???????????????????????????
                        .error(R.mipmap.ic_my_head_default))
                .into(mIvVcWaitAvatar);

        // bg
        if (avatar == null || TextUtils.isEmpty(avatar)) {
            mIvVcWaitBg.setBackground(new ColorDrawable(ContextCompat.getColor(this, R.color.color_999999)));
        } else {
            Glide.with(this)
                    .load(avatar)
                    .apply(new RequestOptions()
                            .placeholder(R.mipmap.ic_my_head_default)) //???????????????????????????
                    .into(mIvVcWaitBg);
        }

        if (sex != 1) {
            mIvVcWaitAvatarLogo.setBackgroundResource(R.drawable.ic_famle_vc);
            if (!isOutGoing()) {
                mIvVcWaitUsername.setText(getString(R.string.she_is_on_the_road));
            } else {
                mIvVcWaitUsername.setText(getString(R.string.she_is_preparing));
            }
        } else {
            mIvVcWaitAvatarLogo.setBackgroundResource(R.drawable.icon_boy_vc);
            if (!isOutGoing()) {
                mIvVcWaitUsername.setText(getString(R.string.he_is_on_the_road));
            } else {
                mIvVcWaitUsername.setText(getString(R.string.he_is_preparing));
            }
        }
        mIvVcWaitLoading.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vc_iv_back:
                showDialogBack();
                break;
            case R.id.vc_iv_record: // ??????/????????????
                if (XClickUtil.isFastDoubleClick(mVcIvRecord, 2000)) {
                    return;//2s???????????????
                }
                rfStatusRecordDiaTime(mVcIvRecord.isSelected(), true, true);
                break;
            case R.id.vc_ll_record_time: // ????????????????????????
                rfStatusRecordAcceptTime(false, 0, true);
                break;
            case R.id.vc_iv_switch:
                /*if (mVideoCallManager != null) {
                    mVideoCallManager.onSwitchCameraClicked();
                }*/
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        KLog.d("onResume");
        hideSystemNavigationBar();
        // ??????????????????
        if (!hasPermission && !mPermissionManager.isShowing()) {
            mPermissionManager.requestPermissions(this, mPermissionListener, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, REQUEST_CODE_PERMISSIONS_1100);
        }

        // ?????????????????????????????????
        if (mNotificationManager != null && isCallTime()) {
            NotificationUtils.dismissNotification(mNotificationManager, 2);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        KLog.d("onPause");

        // ????????????????????????????????????????????????
        if (isCallTime()) {
            // ??????????????????????????????????????????
            if (!isOutGoing()) { // ?????????
                mNotificationManager = NotificationUtils.showNotification(IvyApp.getInstance().getApplicationContext(),
                        outGoingUserName,
                        getString(R.string.in_a_video_call),
                        2, new Intent(this, VideoCallActivity.class));
            } else { // ?????????
                mNotificationManager = NotificationUtils.showNotification(IvyApp.getInstance().getApplicationContext(),
                        mCallBean != null ? mCallBean.getUsername() : null,
                        getString(R.string.in_a_video_call),
                        2, new Intent(this, VideoCallActivity.class));
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        KLog.d("onNewIntent");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        KLog.d("onDestroy");
        releaseVideoCall();
        keepScreenOn(this, false);
        // ????????????
        if (mPermissionListener != null) {
            mPermissionListener = null;
        }
        hasPermission = false;

        mCallBean = null;
        isOutGoing = false;

        outGoingUserName = null;
        outGoingAvatar = null;
        outGoingSex = -1;

        mNotificationManager = null;
        if (mInnerReceiver != null) {
            unregisterReceiver(mInnerReceiver);
        }
    }

    private void releaseVideoCall() {
        // ???????????? (??????????????????????????????????????????)
//        if (!isOutGoing()) {
//            AudioSoundPoolManager.getInstance(VideoCallActivity.this).stopOutGoing();
//        }

        // ??????????????????
        mIvVcWaitLoading.hide();

        // ???????????????
        stopRecordTimTime();
        stopRecordTime(false);
        stopCallTime();
        stopTimeTask();

        // ????????????
        /*if (mCallListener != null) {
            mCallListener = null;
        }
        if (mVideoCallManager != null) {
            mVideoCallManager.onDestroy();
            mVideoCallManager = null;
        }*/

        if (mServiceConnection != null) {
            unbindService(mServiceConnection);
            mServiceConnection = null;
        }
        if (recordListener != null) {
            ScreenRecordUtil.removeRecordListener(recordListener);
            recordListener = null;
        }
    }

    private void initPermission() {
        mPermissionListener = new PermissionManager.SimplePermissionListener() {
            @Override
            public void doAfterGrand(String[] permission, String tag) {
                super.doAfterGrand(permission, tag);
                switch (tag) {
                    case REQUEST_CODE_PERMISSIONS_1100:
                        hasPermission = true;

                        if (mCallBean == null) {
                            if (!isOutGoing()) { // ?????????
                                finishCurrentActivity(true);
                            } else { // ?????????
                                allBack(true); // ????????????????????????????????????
                            }
                            return;
                        }
/*
                        mVideoCallManager = new VideoCallManager(VideoCallActivity.this,
                                mVcFrameRemoteVideoViewContainer,
                                mVcFrameLocalVideoViewContainer);*/
                        try {
                            /*mVideoCallManager.init(null,
                                    mCallBean.getRoomID(),
                                    null,
                                    (int) UserInfoManager.getUid());*/
//                            mVideoCallManager.onLocalAudioMuteClicked(true);
                            /*mCallListener = new VideoCallManager.Listener() {
                                @Override
                                public void onFirstRemoteVideoDecoded() {
                                    if (!isOutGoing()) {
                                        // ?????????????????????  ??????????????? ??????????????????
                                        stopTimeTask();
//                                    mVideoCallManager.onLocalAudioMuteClicked(true);
                                        rfStatusCallTime(true);
                                    } else {
                                        // ????????? ???????????????????????????????????????//
                                        stopTimeTask();
//                                    mVideoCallManager.onLocalAudioMuteClicked(true);
                                        rfStatusCallTime(true);
                                    }
                                    //
//                                mVideoCallManager.enableLocalAudio(true);
//                                mVideoCallManager.enableLocalVideo(true);
                                }

                                @Override
                                public void onRemoteUserLeft() {
                                    ToastUtil.showToast(VideoCallActivity.this, getString(R.string.stop_video_chat));
                                    // ???????????????
                                    KLog.e(TAG, "----onRemoteUserLeft ???????????? isRecording=" + isRecording + ",isRecordingFinishActivity=" + isRecordingFinishActivity);
                                    inComingBack();
                                }
                            };
                            mVideoCallManager.setListener(mCallListener);*/
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
//                            ToastUtil.showToast(VideoCallActivity.this, e.getMessage().toString());

                            if (!isOutGoing()) { // ?????????
                                finishCurrentActivity(true);
                            } else { // ?????????
                                allBack(true); // ????????????????????????????????????
                            }

                            break;
                        }

                        if (!isOutGoing()) { // ?????????
                            // ????????????
                            EMManager.INSTANCE.sendTxtAttrSendMessage(mCallBean);
                            // ??????????????????
//                            AudioSoundPoolManager.getInstance(VideoCallActivity.this).playOutGoing();
                            // ???????????????????????????
                            requestVCmodify_video_chat_status(0, mCallBean.getRoomID(), null);
                            // ?????????8s??????????????????
                            startTime(9);
                        } else {// ?????????
                            // ?????????10s??????????????????
                            startTime(11);
                        }
                        break;
                }
            }

            @Override
            public void doAfterDeniedCancel(String[] permission, String tag) {
                super.doAfterDeniedCancel(permission, tag);
                KLog.d("doAfterDeniedCancel");
                if (!isOutGoing()) { // ?????????
                    finishCurrentActivity(true);
                } else { // ?????????
                    allBack(true); // ????????????????????????????????????
                }
            }

            @Override
            public void doAfterDeniedEnsure(String[] permission, String tag) {
                super.doAfterDeniedEnsure(permission, tag);
                KLog.d("doAfterDeniedEnsure");
                if (mPermissionManager != null) {
                    mPermissionManager.requestPermissions(VideoCallActivity.this, this,
                            permission, tag, true);
                }
            }
        };
       /* mPermissionManager.requestPermissions(this, mPermissionListener, new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, REQUEST_CODE_PERMISSIONS_1100);*/
    }

    // --------------------------------------- ?????????????????????????????? start ----------------------------------------------
    private void initTouchListener() {
        // ???????????????????????????
        mVcFrameRemoteVideoViewContainer.post(() -> {
            ConstraintLayout.LayoutParams remote_params = (ConstraintLayout.LayoutParams)
                    mVcFrameRemoteVideoViewContainer.getLayoutParams();
            remote_params.rightMargin = mVcFrameRemoteVideoViewContainer.getWidth() / 2;
            remote_params.leftMargin = 0;
            mVcFrameRemoteVideoViewContainer.setLayoutParams(remote_params);
            screenWidth = mVcFrameRemoteVideoViewContainer.getWidth();
        });
        mVcFrameLocalVideoViewContainer.post(() -> {
            ConstraintLayout.LayoutParams remote_params = (ConstraintLayout.LayoutParams)
                    mVcFrameLocalVideoViewContainer.getLayoutParams();
            remote_params.leftMargin = mVcFrameLocalVideoViewContainer.getWidth() / 2;
            remote_params.rightMargin = 0;
            mVcFrameLocalVideoViewContainer.setLayoutParams(remote_params);
            screenWidth = mVcFrameLocalVideoViewContainer.getWidth();
        });
        // ??????mMoveView??????
        mMoveView.post(() -> {
            ConstraintLayout.LayoutParams remote_params = (ConstraintLayout.LayoutParams)
                    mMoveView.getLayoutParams();
            remote_params.leftMargin = (screenWidth - mMoveView.getWidth()) / 2;
            remote_params.rightMargin = (screenWidth - mMoveView.getWidth()) / 2;
            mMoveView.setLayoutParams(remote_params);
        });
        // ???????????????????????????
        mMoveView.setOnTouchListener(new View.OnTouchListener() {
            private ConstraintLayout.LayoutParams remote_params;
            private ConstraintLayout.LayoutParams local_params;
            private ConstraintLayout.LayoutParams move_params;
            private int lastX;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // ???????????????????????????????????????
                if (mVcIvRecord.isSelected()) {
                    return false;
                }
                int x = (int) event.getX();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        lastX = x;
                        remote_params = (ConstraintLayout.LayoutParams) mVcFrameRemoteVideoViewContainer.getLayoutParams();
                        local_params = (ConstraintLayout.LayoutParams) mVcFrameLocalVideoViewContainer.getLayoutParams();
                        move_params = (ConstraintLayout.LayoutParams) mMoveView.getLayoutParams();
                      /*  remote_params.rightMargin = move_params.rightMargin;
                        local_params.leftMargin = move_params.leftMargin;*/
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int offsetX = x - lastX;

                        //?????? layout ??????????????????
                        move_params.leftMargin += offsetX;
                        if (move_params.leftMargin < 0) {
                            move_params.leftMargin = 0;
                        } else if (move_params.leftMargin > screenWidth - mMoveView.getWidth()) {
                            move_params.leftMargin = screenWidth - mMoveView.getWidth();
                        }
                        move_params.rightMargin -= offsetX;
                        if (move_params.rightMargin < 0) {
                            move_params.rightMargin = 0;
                        } else if (move_params.rightMargin > screenWidth - mMoveView.getWidth()) {
                            move_params.rightMargin = screenWidth - mMoveView.getWidth();
                        }
                        mMoveView.setLayoutParams(move_params);

                        // left
                        remote_params.rightMargin -= offsetX;
                        if (remote_params.rightMargin < 0) {
                            remote_params.rightMargin = 0;
                        } else if (remote_params.rightMargin > screenWidth) {
                            remote_params.rightMargin = screenWidth;
                        }
                        remote_params.leftMargin = 0;
                        mVcFrameRemoteVideoViewContainer.setLayoutParams(remote_params);

                        // right
                        local_params.leftMargin += offsetX;
                        if (local_params.leftMargin < 0) {
                            local_params.leftMargin = 0;
                        } else if (local_params.leftMargin > screenWidth) {
                            local_params.leftMargin = screenWidth;
                        }
                        local_params.rightMargin = 0;
                        mVcFrameLocalVideoViewContainer.setLayoutParams(local_params);
                        break;
                }
                return true;
            }
        });
    }
    // --------------------------------------- ?????????????????????????????? end ----------------------------------------------

    // --------------------------------------- ????????? start ----------------------------------------------

    private void startTime(int count) {
        stopTimeTask();
        timeTask = Observable.intervalRange(0, count, 0, 1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        KLog.d("accept" + aLong);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        KLog.i("throwable " + throwable);
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        KLog.i("complete"); // complete
                        // ???????????????????????????
                        requestVCmodify_video_chat_status(3, mCallBean.getRoomID(), null);
                        if (!isOutGoing()) { // ?????????
                            // ???????????????????????????
                            allBack(false);
                            ToastUtil.showToast(VideoCallActivity.this, getString(R.string.unable_to_access_the_video_invitation));
                        } else {
                            // ????????????????????????????????????
                            allBack(true);
                        }
                    }
                }, new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        KLog.i("Disposable");
                        timeTask = disposable;
                    }
                });
    }

    public boolean stopTimeTask() {
        if (timeTask != null && !timeTask.isDisposed()) {
            timeTask.dispose();
            timeTask = null;
            return true;
        }
        return false;
    }

    private void startCallTime() {
        stopCallTime();
        timeCallTask = Observable.interval(0, 1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
//                    KLog.d("accept" + aLong);
                    if (mVcTvVcTime.getVisibility() == View.VISIBLE) {
                        if (aLong < 60) { // 0s - 1??????
                          /*  mVcTvVcTime.setText(String.format(getString(R.string.vc_0_0),
                                    aLong < 10 ? "0" + aLong : String.valueOf(aLong)));*/
                            int minute = (int) (aLong / 60);
                            int second = (int) (aLong % 60);
                            mVcTvVcTime.setText(String.format(getString(R.string.vc_0_0_1_1),
                                    minute < 10 ? "0" + minute : String.valueOf(minute),
                                    second < 10 ? "0" + second : String.valueOf(second)));
                        } else if (aLong < 3600) {  // 60*60 = 1?????? - 1??????
                            int minute = (int) (aLong / 60);
                            int second = (int) (aLong % 60);
                            mVcTvVcTime.setText(String.format(getString(R.string.vc_0_0_1_1),
                                    minute < 10 ? "0" + minute : String.valueOf(minute),
                                    second < 10 ? "0" + second : String.valueOf(second)));
                        } else if (aLong < 86400) {   // 60 * 60 * 24 = 1?????? - 24??????
                            // ??????????????????
                            if (mVcTvVcTimeTip != null && !mVcTvVcTimeTip.isSelected()) {
                                ViewGroup.LayoutParams params = mVcTvVcTimeTip.getLayoutParams();
                                if (params instanceof ConstraintLayout.LayoutParams) {
                                    ConstraintLayout.LayoutParams p = (ConstraintLayout.LayoutParams) params;
                                    p.rightMargin = p.rightMargin + getResources().getDimensionPixelSize(R.dimen.dp_20);
                                    mVcTvVcTimeTip.setLayoutParams(p);
                                }
                                mVcTvVcTimeTip.setSelected(true);
                            }

                            int hour = (int) (aLong / 3600);
                            int minute = (int) (aLong % 3600 / 60);
                            int second = (int) (aLong % 3600 % 60);
                            mVcTvVcTime.setText(String.format(getString(R.string.vc_0_0_1_1_2_2),
                                    hour < 10 ? "0" + hour : String.valueOf(hour),
                                    minute < 10 ? "0" + minute : String.valueOf(minute),
                                    second < 10 ? "0" + second : String.valueOf(second)));
                        } else { // >24???????????????????????????????????????????????????
                            allBack(true);
                        }
                    }
                });
    }

    public void stopCallTime() {
        if (timeCallTask != null && !timeCallTask.isDisposed()) {
            timeCallTask.dispose();
            timeCallTask = null;
        }
    }


    private boolean isCallTime() {
        return timeCallTask != null && !timeCallTask.isDisposed(); // ??????????????????
    }

    /**
     * @param recordTotalTime ????????????????????????
     */
    private void startRecordTime(double recordTotalTime) {
//        stopRecordTime();
        ScreenRecordUtil.clearRecordElement();
        if (!ScreenRecordUtil.startScreenRecord(this, IvyConstants.REQUEST_CODE_SCREEN_RECORD, recordTotalTime)) {
            showToast(getResources().getString(R.string.record_time_end_tip));
            rfStatusRecordDiaTime(true, false, false);
        }

    }

    public void stopRecordTime(boolean isSaveRecordFile) {
//        if (timeRecordTask != null && !timeRecordTask.isDisposed()) {
//            timeRecordTask.dispose();
//            timeRecordTask = null;
//        }
        if (ScreenRecordUtil.isCurrentRecording()) {
            ScreenRecordUtil.stopScreenRecord(this, isSaveRecordFile);
        }
    }

    private void startRecordTimTime(long initialDelay) {
        stopRecordTimTime();
        KLog.d("initialDelay: " + initialDelay);
        timeRecordTimTask = Observable.interval(0, 1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
//                    KLog.d("accept" + aLong);
                    aLong += initialDelay;
                    // ????????????180s
                    if (aLong > 180) {
                        stopRecordTimTime();
                        return;
                    }
                    if (mVcTvAcceptRecordTime.getVisibility() != View.GONE) {
                        int minute = (int) (aLong / 60);
                        int millers = (int) (aLong % 60);
                        mVcTvAcceptRecordTime.setText(String.format(getString(R.string.vc_0_0_1_1),
                                minute < 10 ? "0" + minute : String.valueOf(minute),
                                millers < 10 ? "0" + millers : String.valueOf(millers)));
                    }
                });
    }

    public void stopRecordTimTime() {
        if (timeRecordTimTask != null && !timeRecordTimTask.isDisposed()) {
            timeRecordTimTask.dispose();
            timeRecordTimTask = null;
        }
    }
    // --------------------------------------- ????????? end ----------------------------------------------

    private void initPreData() {
        mCallBean = getIntent().hasExtra(Global.PARAMS) ? getIntent().getParcelableExtra(Global.PARAMS) : null;
        isOutGoing = getIntent().hasExtra(Global.PARAMS1) ? getIntent().getBooleanExtra(Global.PARAMS1, false) : false;

        outGoingAvatar = getIntent().hasExtra(Global.PARAMS2) ? getIntent().getStringExtra(Global.PARAMS2) : null;
        outGoingSex = getIntent().hasExtra(Global.PARAMS3) ? getIntent().getIntExtra(Global.PARAMS3, -1) : -1;
        outGoingUserName = getIntent().hasExtra(Global.PARAMS4) ? getIntent().getStringExtra(Global.PARAMS4) : null;
        KLog.d("isOutGoing: " + isOutGoing + " outGoingUserName " + outGoingUserName +
                " outGoingAvatar " + outGoingAvatar + " outGoingSex " + outGoingSex +
                " \n mCallBean: " + mCallBean.toString());
    }

    public String getAccept_userName() {
        return mCallBean != null ? mCallBean.getAccept_username() : "";
    }

    // ??????????????????
    public boolean isOutGoing() {
        return isOutGoing;
    }

    // ----------------------------------  ??????  start  ----------------------------------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mVcIvRecord.isSelected()) { // ??????????????????
                ToastUtil.showToast(this, getString(R.string.perform_other_operations));
            } else {
                showDialogBack();
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_HOME) {
            KLog.e("-----??????Home???");
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            KLog.e("-----???????????????");
        } else if (keyCode == KeyEvent.KEYCODE_POWER) {
            KLog.e("-----???????????????");
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showDialogBack() {
        if (mCustomProgress == null) {
            mCustomProgress = new CustomProgress(this);
        }
        mCustomProgress.show(getString(R.string.vc_sure_stop_chat),
                getString(R.string.common_tips_title),
                getString(R.string.common_cancel),
                getString(R.string.common_ensure),
                v -> mCustomProgress.dismiss(), v -> {
                    mCustomProgress.dismiss();
                    allBack(true);
                }, true, dialog -> mCustomProgress.dismiss());
    }

    /**
     * @param isActive ?????????????????????
     */
    private void allBack(boolean isActive) {
        if (isActive) {
            // ???????????????????????????
            if (mCallBean != null) {
//                if (!isOutGoing()) { // ?????????
//                } else { // ??????????????????
//                }
                // ??????????????? mCallBean  ?????????????????? ???????????????????????????bean
                mCallBean.setRequestType(EMMessageHelper.CLOSE);
                EMManager.INSTANCE.sendTxtAttrSendMessage(mCallBean);
            }
        }

        // ????????????
        /*if (mVideoCallManager != null) {
            mVideoCallManager.leaveChannel();
        }*/

        // ????????????
        if (!isOutGoing()) { // ?????????
            outGoingBack();
        } else {// ?????????
            KLog.e(TAG, "----allback ???????????? isRecording=" + isRecording + ",isRecordingFinishActivity=" + isRecordingFinishActivity);
            inComingBack();
        }
    }

    // ???????????????
    private void outGoingBack() {
//        AudioSoundPoolManager.getInstance(VideoCallActivity.this).stopOutGoing();
        stopTimeTask();
        finishCurrentActivity(true);
    }

    // ???????????????
    private void inComingBack() {
        if (isGetComingBack){
            return;
        }
        isGetComingBack = true;
        stopTimeTask();
        if (isRecording) {
            //????????????
            isRecordingFinishActivity = true;
            finishCurrentActivity(true);
        } else {
            finishCurrentActivity(false);
        }
    }

    private synchronized void finishCurrentActivity(boolean isSaveRecordFile) {
        if (VideoCallActivity.this.isDestroyed()) {
            return;
        }
        // ???????????????????????? start
        stopCallTime();
        stopRecordTimTime();
        stopRecordTime(isSaveRecordFile);
        stopTimeTask();
        // ???????????????????????? end

        // ??????????????????start
        if (mVcIvRecord.isSelected()) {
            mVcIvDiaRecordTime.clearAnimation();
        }
        if (mVcLlRecordTime.isSelected()) {
            mVcIvAcceptRecordTime.clearAnimation();
        }
        // ??????????????????end

        // ???????????????????????????
        if (mCallBean != null) {
            requestVCmodify_video_chat_status(4, mCallBean.getRoomID(),
                    mVcTvVcTime == null || mVcTvVcTime.getText() == null || TextUtils.isEmpty(mVcTvVcTime.getText()) ?
                            null : mVcTvVcTime.getText().toString());
        }

        // ?????????????????????????????????????????????????????????,???RecordListener?????????
        if (isRecordingFinishActivity) {
            return;
        }
        closeActivity();

//        showLastActivityVideoCallHistory();
    }

    /**
     * ?????????????????????????????????????????????????????????????????????????????????????????????????????????
     */
    private void closeActivity() {
        // ???????????????
        if (mMovieShowListAdapter != null && mMovieShowListAdapter.getData().size() > 0) {
            Intent intent = new Intent(VideoCallActivity.this, VideoCallRecordActivity.class);
            ArrayList<MovieItemModel> data = (ArrayList<MovieItemModel>) mMovieShowListAdapter.getData();
            intent.putExtra(VideoCallRecordActivity.INTENT_KEY_ADAPTER_MODEL, data);
            intent.putExtra(VideoCallRecordActivity.INTENT_KEY_DB_MODEL, mMovieItemDaoList);
            startActivity(intent);
        }
        isRecordingFinishActivity = false;

        // ????????????


        finish();
    }


   /* private void showLastActivityVideoCallHistory() {
        // ???????????????????????????????????????????????????
        Activity activity = ActivityUtils.getLastSecondActivity();
        if (activity != null && activity instanceof BaseVideoCallActivity) {
            ((BaseVideoCallActivity) activity).showHistoryVideoCallResponseDialog();
        } else {// ????????????????????????activity ?????? ????????????
            EMMessageHelper.isShowVideoCallHistory = true;
        }
    }*/
    // ----------------------------------  ??????  end  ----------------------------------------


    // ----------------------------------  ??????  start  ----------------------------------------
    public void onCreateContentView(@Nullable Bundle bundle) {
        if (bundle != null) {
            if (bundle.containsKey(Global.PARAMS)) {
                mCallBean = bundle.getParcelable(Global.PARAMS);
            }
            if (bundle.containsKey(Global.PARAMS1)) {
                isOutGoing = bundle.getBoolean(Global.PARAMS1);
            }
            if (bundle.containsKey(Global.PARAMS2)) {
                hasPermission = bundle.getBoolean(Global.PARAMS2);
            }
            if (bundle.containsKey(Global.PARAMS3)) {
                outGoingUserName = bundle.getString(Global.PARAMS3);
            }
            if (bundle.containsKey(Global.PARAMS4)) {
                outGoingAvatar = bundle.getString(Global.PARAMS4);
            }
            if (bundle.containsKey(Global.PARAMS5)) {
                outGoingSex = bundle.getInt(Global.PARAMS5);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null) {
            if (mCallBean != null) {
                outState.putParcelable(Global.PARAMS, mCallBean);
            }
            outState.putBoolean(Global.PARAMS1, isOutGoing());
            outState.putBoolean(Global.PARAMS2, hasPermission);
            outState.putString(Global.PARAMS3, outGoingUserName);
            outState.putString(Global.PARAMS4, outGoingAvatar);
            outState.putInt(Global.PARAMS5, outGoingSex);
        }
    }

    // ----------------------------------  ??????  end  ----------------------------------------
    // ----------------------------------  ????????????  start  ----------------------------------------
    @SuppressLint("InvalidWakeLockTag")
    public void keepScreenOn(Context context, boolean on) {
        if (on) {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
                    PowerManager.ON_AFTER_RELEASE, "==KeepScreenOn==");
            mWakeLock.acquire();
        } else {
            if (mWakeLock != null) {
                mWakeLock.release();
                mWakeLock = null;
            }
        }
    }
    // ----------------------------------  ????????????  end  ----------------------------------------

    // ----------------------------------  UI  start  ----------------------------------------

    private void initRecordAdapter() {
        ArrayList<MovieItemModel> movieItemModels = new ArrayList<>();
        mMovieItemDaoList = new ArrayList<>();
        mMovieShowListAdapter = new MovieShowListAdapter(R.layout.adapter_movie_show_list, movieItemModels, false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mVcRvDiaRecord.setLayoutManager(linearLayoutManager);
        mVcRvDiaRecord.setAdapter(mMovieShowListAdapter);
        CustomItemTouchCallBack callBack = new CustomItemTouchCallBack(mMovieShowListAdapter.getData(), mMovieShowListAdapter, this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callBack);
        itemTouchHelper.attachToRecyclerView(mVcRvDiaRecord);
        mMovieShowListAdapter.setDeleteListener(this);
    }

    private void clearAllStatus() {
        // ??????????????????
        mVcIvRecord.setVisibility(View.GONE);
        mVcIvSwitch.setVisibility(View.VISIBLE);
        // ?????????
        mVcTvVcTime.setVisibility(View.GONE);
        mVcTvVcTimeTip.setVisibility(View.GONE);
        // ??????????????????
        mVcTvDiaRecordTime.setVisibility(View.GONE);
        mVcIvDiaRecordTime.setVisibility(View.GONE);
        mVcTvDiaRecordTime.setText("00:00");
        // ??????????????????
        mVcLlRecordTime.setVisibility(View.GONE);
        // ?????????????????????????????????
        mVcTvAcceptRecordTime.setVisibility(View.VISIBLE);
        mVcTvAcceptRecordTime.setText("00:00");
        // ???????????????????????????????????????
        mVcIvRecord.setSelected(false);
        mVcLlRecordTime.setSelected(false);
    }

    /**
     * ????????????????????????????????????
     *
     * @param isVisible
     */
    private void rfStatusCallTime(boolean isVisible) {
        if (isVisible) {
            // ??????????????????
            mVcFrameRemoteVideoViewLoading.setVisibility(View.GONE);
            mIvVcWaitLoading.hide();

            // ??????????????????
            mVcIvRecord.setVisibility(View.VISIBLE);

            mVcTvVcTime.setVisibility(View.VISIBLE);
            mVcTvVcTimeTip.setVisibility(View.VISIBLE);
            // ???????????????
            startCallTime();
        } else {
            // ??????????????????
            mVcIvRecord.setVisibility(View.GONE);

            mVcTvVcTime.setVisibility(View.GONE);
            mVcTvVcTimeTip.setVisibility(View.GONE);
            // ???????????????
            stopCallTime();
        }
    }

    /**
     * ????????? ????????????????????????????????????
     * ??????????????????
     *
     * @param isVisible
     * @param isSendEM         ????????????????????????????????????
     * @param isSaveRecordFile ???????????????????????????????????????????????????????????????????????????????????????
     */
    private synchronized void rfStatusRecordDiaTime(boolean isVisible, boolean isSendEM, boolean isSaveRecordFile) {
        if (isVisible) {
            if (!mVcIvRecord.isSelected()) {// ?????????
                return;
            }
            mVcIvRecord.setSelected(false);
            mVcIvRecord.setBackgroundResource(R.drawable.btn_record_default_vc);

            // ?????????????????? ????????????
            mVcIvBack.setVisibility(View.VISIBLE);
            mVcIvSwitch.setVisibility(View.VISIBLE);
            // ???????????????????????????
            mVcTvVcTime.setVisibility(View.VISIBLE);
            mVcTvVcTimeTip.setVisibility(View.VISIBLE);

            //??????????????????
            mVcRvDiaRecord.setVisibility(View.VISIBLE);

            // ?????????????????????
            mVcTvDiaRecordTime.setVisibility(View.GONE);
            mVcIvDiaRecordTime.setVisibility(View.GONE);
            mVcTvDiaRecordTime.setText("00:00");

            // ????????????
            mVcIvDiaRecordTime.clearAnimation();

            //????????????????????? ???????????????????????????????????????????????????
            if (isRecording && isOtherRecording) {
                mVcLlRecordTime.setVisibility(View.VISIBLE);
            } else {
                mVcLlRecordTime.setVisibility(View.INVISIBLE);
            }
            // ??????????????????????????????????????????????????????recordlistener????????????
            /*if (isSendEM && mCallBean != null) {
                EMManager.INSTANCE.sendTxtAttrSendMessage(EMMessageHelper.RECORD_END, mCallBean.getAccept_username(), mCallBean.getRoomID(), true);
            }*/

            // ??????????????????  ??????????????????
            stopRecordTime(isSaveRecordFile);


        } else {
            if (mVcIvRecord.isSelected()) {// ?????????
                return;
            }
            if (mRecordTotalTime >= 180) {
                showToast(getResources().getString(R.string.record_time_end_tip));
                return;
            }
            mVcIvRecord.setSelected(true);
            mVcIvRecord.setBackgroundResource(R.drawable.btn_record_press_vc);

            // ????????????????????????  ????????????
            mVcIvBack.setVisibility(View.GONE);
            mVcIvSwitch.setVisibility(View.GONE);
            // ???????????????????????????
            mVcTvVcTime.setVisibility(View.GONE);
            mVcTvVcTimeTip.setVisibility(View.GONE);

            //??????????????????
            mVcRvDiaRecord.setVisibility(View.GONE);

            if (!isRecording && isOtherRecording) {
                mVcLlRecordTime.setVisibility(View.INVISIBLE);
            }

            // ????????????????????????????????????????????????(????????????recordListener start?????????
            /*if (isSendEM && mCallBean != null) {
                EMManager.INSTANCE.sendTxtAttrSendMessage(EMMessageHelper.RECORD_START, mCallBean.getAccept_username(), mCallBean.getRoomID(), true);
            }*/

            // ??????????????????  ??????????????????
            startRecordTime(mRecordTotalTime);
        }
    }

    /**
     * ????????? ????????????????????????????????????????????????
     */
    private void rfStatusRecordAcceptTime(boolean isVisible, long initialDelay, boolean isSendEM) {
        if (isVisible) {
            if (mVcLlRecordTime.isSelected()) {// ?????????
                return;
            }
            mVcLlRecordTime.setSelected(true);

            // ?????????????????????????????????
            mVcLlRecordTime.setVisibility(View.VISIBLE);

            // ????????????
            AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
            alphaAnimation.setDuration(500);
            alphaAnimation.setRepeatCount(Animation.INFINITE);
            alphaAnimation.setRepeatMode(Animation.REVERSE);
            mVcIvAcceptRecordTime.startAnimation(alphaAnimation);

            mVcTvAcceptRecordTime.setText("00:00");
            if (isRecording && isOtherRecording) {
                mVcLlRecordTime.setVisibility(View.INVISIBLE);
            } else {
                mVcLlRecordTime.setVisibility(View.VISIBLE);
            }

            // ???????????????
            startRecordTimTime(initialDelay);
        } else {
            if (!mVcLlRecordTime.isSelected()) {// ?????????
                return;
            }
            mVcLlRecordTime.setSelected(false);
            mVcLlRecordTime.setVisibility(View.GONE);

            // ????????????
            mVcIvAcceptRecordTime.clearAnimation();
            mVcTvAcceptRecordTime.setText("00:00");

            // ?????????????????????????????????????????????????????????
            if (isSendEM && mCallBean != null) {
                EMManager.INSTANCE.sendTxtAttrSendMessage(EMMessageHelper.RECORD_END, mCallBean.getAccept_username(), mCallBean.getRoomID(), false);
            }

            // ???????????????
            stopRecordTimTime();
        }
    }


    // ----------------------------------  UI  end  ----------------------------------------

    /**
     * ?????????????????????????????????????????????????????? ???????????????????????????
     */
    public void videoCallMessageCallBack(String requestType, String accept_userName, String roomID, long dialTime, boolean isMe) {
        // ???????????????, ???????????????????????????????????????????????????????????????????????????????????????
        if (mCallBean != null && accept_userName.equals(mCallBean.getAccept_username()) && mCallBean.getRoomID().equals(roomID)) {
            switch (requestType) {
                case EMMessageHelper.REFUSE:
                    ToastUtil.showToast(this, getString(R.string.register_you_video));

                    // ???????????????
                    outGoingBack();
                    break;
                case EMMessageHelper.REFUSE_BUSY:
                    ToastUtil.showToast(this, getString(R.string.the_other_party_is_busy));

                    // ???????????????
                    outGoingBack();
                    break;
                case EMMessageHelper.ACCEPT:
                    if (mVcFrameRemoteVideoViewLoading.getVisibility() == View.VISIBLE) {
                        if (outGoingSex != 1) {
                            mIvVcWaitUsername.setText(getString(R.string.she_is_preparing));
                        } else {
                            mIvVcWaitUsername.setText(getString(R.string.he_is_preparing));
                        }
                    }

                    // ??????????????? ??????????????????
                    if (stopTimeTask()) {
                        OnlinePreferenceManager.getInstance().putString(roomID);
                    }
//                    AudioSoundPoolManager.getInstance(VideoCallActivity.this).stopOutGoing();
                    break;
                case EMMessageHelper.CLOSE:
                    if (VideoCallActivity.this.isDestroyed()) {
                        return;
                    }

                    ToastUtil.showToast(this, getString(R.string.stop_video_chat));
                    KLog.e(TAG, "----EMMessageHelper.CLOSE ???????????? isRecording=" + isRecording + ",isRecordingFinishActivity=" + isRecordingFinishActivity);
                    // ???????????????
                    inComingBack();//???????????????????????????????????????????????????????????????VideoCallManager.onRemoteUserLeft???????????????????????????closeActivity()
                    break;
                case EMMessageHelper.RECORD_START:   // ????????????????????????
                    isOtherRecording = true;
                    // ????????????????????????
                    rfStatusRecordAcceptTime(true,
                            (System.currentTimeMillis() - dialTime) / 1000 + 1, false);
                    break;
                case EMMessageHelper.RECORD_END:  // ???????????????????????????????????????
                    KLog.d("isMe: " + isMe);
                    if (isMe) { // ?????????????????????????????????
                        isOtherRecording = false;
                        ToastUtil.showToast(this, getString(R.string.stop_record_screen_by_me));
                        rfStatusRecordAcceptTime(false, 0, false);
                    } else { // ?????????????????????????????????
                        ToastUtil.showToast(this, getString(R.string.stop_record_screen));
                        rfStatusRecordDiaTime(true, false, false);
                    }
                    break;
            }
        }
    }

    private void startScreenRecordService() {
        ScreenRecordUtil.removeRecordListener(recordListener);
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                ScreenRecordService.RecordBinder recordBinder = (ScreenRecordService.RecordBinder) service;
                ScreenRecordService screenRecordService = recordBinder.getRecordService();
                ScreenRecordUtil.setScreenService(screenRecordService);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };

        Intent intent = new Intent(this, ScreenRecordService.class);
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);

        ScreenRecordUtil.addRecordListener(recordListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        hideSystemNavigationBar();
        if (requestCode == IvyConstants.REQUEST_CODE_SCREEN_RECORD && resultCode == Activity.RESULT_OK) {
            try {
                ScreenRecordUtil.setUpData(resultCode, data, mRecordTotalTime);
                // ????????????
                AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
                alphaAnimation.setDuration(500);
                alphaAnimation.setRepeatCount(Animation.INFINITE);
                alphaAnimation.setRepeatMode(Animation.REVERSE);
                mVcIvDiaRecordTime.startAnimation(alphaAnimation);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (hasPermission) {
            ToastUtil.showToast(this, getString(R.string.screen_record_reject));
            rfStatusRecordDiaTime(true, false, false);//???????????? ???????????????UI
        }
    }

    @Override
    public void deleteListener(int position) {
        //????????????????????????????????????????????????????????????
        if (mMovieShowListAdapter != null && mMovieItemDaoList != null && mMovieItemDaoList.size() > position) {

            MovieItemDb movieItemDb = mMovieItemDaoList.get(position);
            //???????????????
            mMovieItemDbDao.delete(movieItemDb);
            mMovieShowListAdapter.deleteItem(position);
            mMovieItemDaoList.remove(movieItemDb);

            //????????????????????????
            mRecordTotalTime = updateRecordTotalTime(mMovieShowListAdapter.getData());
            //????????????
            if (movieItemDb != null &&
                    !TextUtils.isEmpty(movieItemDb.getFilePath()) &&
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
                FileUtil.fileDeleteScanVideo(VideoCallActivity.this, filePath);
                FileUtil.fileDeleteScanImage(VideoCallActivity.this,picFilePath);
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        KLog.e("---onTouch----");
        if (isRecording) {
            showToast(getString(R.string.perform_other_operations));
            return false;
        }
        return false;
    }

    private void showToast(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        ToastUtil.showToastCenter(VideoCallActivity.this, msg);
    }

    private double updateRecordTotalTime(List<MovieItemModel> recordList) {

        BigDecimal totalTime = new BigDecimal(BigInteger.ZERO);
        for (MovieItemModel movieItemModel : recordList) {
            totalTime = totalTime.add(movieItemModel.getVideoTime());
        }
        return totalTime.doubleValue();
    }

    private void hideSystemNavigationBar() {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            View view = this.getWindow().getDecorView();
            view.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    class InnerReceiver extends BroadcastReceiver {

        final String SYSTEM_DIALOG_REASON_KEY = "reason";

        final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";

        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (reason != null) {
                    if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY) || reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                        KLog.e("-------reason=" + reason);
                        if (isRecording) {
                            KLog.e("-------???????????????????????????????????????");
                            rfStatusRecordDiaTime(isRecording, true, true);
                        }
                    }
                }
            }
        }
    }
}
