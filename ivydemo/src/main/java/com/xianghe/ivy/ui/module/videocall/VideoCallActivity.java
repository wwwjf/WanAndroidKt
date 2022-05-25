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

    // loading 等待框
    private ViewStub mVcFrameRemoteVideoViewLoading;
    private ImageView mIvVcWaitBg;
    private CircleImageView mIvVcWaitAvatar;
    private ImageView mIvVcWaitAvatarLogo;
    private TextView mIvVcWaitUsername;
    private LoadingIndicatorView mIvVcWaitLoading;

    // permission
    public static final String REQUEST_CODE_PERMISSIONS_1100 = "1100";
    private PermissionManager.PermissionListener mPermissionListener;
    private boolean hasPermission = false; // 记录有无授权，决定退出是否是发送信息

    private PowerManager.WakeLock mWakeLock;// 保持屏幕常亮
    private int screenWidth;

    // 视频通话S
//    private VideoCallManager mVideoCallManager;
//    private VideoCallManager.Listener mCallListener;


    private NotificationManager mNotificationManager;

    // params
    private VideoCallBean mCallBean;// 拨打端 就是拨打端的数据（除了uid是接收方的信息），接收方就是全部拨打端的数据
    private boolean isOutGoing;// 是否是接收端

    // 拨打端  接收方的信息
    private String outGoingUserName = null; // 姓名
    private String outGoingAvatar = null; // 头像
    private int outGoingSex = -1;   // 性别

    // 计时器
    private Disposable timeTask;  // 启动计时 如果再规定时间未接通就关闭当前界面
    private Disposable timeCallTask; // 通话计时
    private Disposable timeRecordTask; // 录制计时
    private Disposable timeRecordTimTask; // 被录制计时


    private MovieShowListAdapter mMovieShowListAdapter;
    private ArrayList<MovieItemDb> mMovieItemDaoList;
    private MovieItemDbDao mMovieItemDbDao;

    private ServiceConnection mServiceConnection;
    /**
     * 正在保存录屏文件
     */
    private boolean isSavingRecord;

    /**
     * 自己是否正在录屏
     */
    private boolean isRecording;

    /**
     * 是否接收到退出聊天的消息
     */
    private boolean isGetComingBack;

    /**
     * 对方是否正在录屏
     */
    private boolean isOtherRecording;
    /**
     * 是否正在录屏的时候退出了聊天
     */
    private boolean isRecordingFinishActivity;

    /**
     * 音频文件名称（带文件格式）
     */
    private String mAudioName;

    private double mRecordTotalTime;
    /**
     * 数据库中时间戳相同的为一组视频
     */
    private long mTimeKey;

    // 视频合成弹框
    private LoadingDialog mLoadingDialog;

    private InnerReceiver mInnerReceiver;

    private ScreenRecordUtil.RecordListener recordListener = new ScreenRecordUtil.RecordListener() {
        @Override
        public void onStartRecord(String path) {
            KLog.e("=====onStartRecord:" + path);
            // 发送通知告知我已经开始录制视频了
            if (mCallBean != null) {
                EMManager.INSTANCE.sendTxtAttrSendMessage(EMMessageHelper.RECORD_START, mCallBean.getAccept_username(), mCallBean.getRoomID(), true);
            }
            if (mTimeKey == 0) {
                if (mMovieItemDaoList != null && mMovieItemDaoList.size() > 0) {
                    //已有录制的视频，获取已录制视频的时间戳
                    mTimeKey = mMovieItemDaoList.get(0).getKey();
                } else {
                    //生成一个新的时间戳
                    mTimeKey = System.currentTimeMillis();
                }
            }
            // 拨打端开始录像 显示录屏UI
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
            //声网api
//            mVideoCallManager.startAudioRecording(audioPath, Constants.AUDIO_RECORDING_QUALITY_HIGH);
            isRecording = true;
            //停止录屏后 合成音频、视频文件
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
                //修改为正在保存标志位
                isSavingRecord = true;
                // 发送通知告知我已经停止录制视频了
                if (mCallBean != null) {
                    EMManager.INSTANCE.sendTxtAttrSendMessage(EMMessageHelper.RECORD_END, mCallBean.getAccept_username(), mCallBean.getRoomID(), true);
                }
//                mVideoCallManager.stopAudioRecording();
                String recordVideoPath = ScreenRecordUtil.getScreenRecordFilePath();
                String recordAudioPath = IvyUtils.createVideoCallRecordDirs() + File.separator + mAudioName;
                isRecording = false;
                if (ScreenRecordService.STATUS_ERROR == status) {//录屏错误状态
                    //结束录屏UI
                    rfStatusRecordDiaTime(true, true, false);//状态异常，结束录屏
                    ToastUtil.showToast(VideoCallActivity.this, stopTip);
                    FileUtil.deleteSDFile(recordVideoPath);//视频文件路径
                    FileUtil.deleteSDFile(IvyUtils.createVideoCallRecordDirs() + File.separator + mAudioName);//音频文件路径
                    isSavingRecord = false;
                    if (isRecordingFinishActivity) {
                        //正在录屏时候退出了聊天
                        closeActivity();
                    }
                    return;
                } else if (ScreenRecordService.STATUS_TIME_FULL == status) {//录屏时间已满状态
                    //录制已到达3分钟
                    rfStatusRecordDiaTime(true, true, true);//时间已到3分钟，结束录屏
                    ToastUtil.showToast(VideoCallActivity.this, stopTip);
                    //继续执行以下保存操作
                }
                KLog.e("=====停止屏幕录制 onStopRecord:" + recordVideoPath + ",stopTip:" + stopTip + ",status:" + status);
                // 停止屏幕录制
                if (!isSaveRecordFile) {
                    //对方终止了屏幕录制，不保存文件
                    FileUtil.deleteSDFile(recordVideoPath);//视频文件路径
                    FileUtil.deleteSDFile(recordAudioPath);//音频文件路径
                    isSavingRecord = false;
                    return;
                }
                // 1、生成视频封面图片,获取视频时长
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
                    KLog.e("录屏文件保存失败");
                    FileUtil.deleteSDFile(recordVideoPath);//视频文件路径
                    FileUtil.deleteSDFile(recordAudioPath);//音频文件路径
                    if (isRecordingFinishActivity) {
                        //正在录屏时候退出了聊天
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
     * 合成音频、视频文件
     *
     * @param recordAudioFile
     * @param recordVideoFile
     */
    private void editAudioVideoFile(File recordAudioFile, File recordVideoFile) throws Exception {

        AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audio == null) {
            KLog.e("-----AudioManager为空");
            throw new Exception();
        }
        //视频时长
        String recordVideoPath = recordVideoFile.getAbsolutePath();
        String recordAudioPath = recordAudioFile.getAbsolutePath();
        String duration = IvyUtils.getMediaDuration(recordVideoPath);
        double videoDurationDouble = StringUtils.isEmpty(duration) ? 0 : Double.valueOf(duration) / 1000f;
        //视频时长有误差，取180s
        videoDurationDouble = videoDurationDouble > 180 ? 180 : videoDurationDouble;
        BigDecimal videoDuration = new BigDecimal(videoDurationDouble).setScale(1, RoundingMode.HALF_UP);
        //视频封面图片
        String videoPicPath = IvyUtils.getVideoFrame(IvyApp.getInstance().getApplicationContext(), recordVideoPath, 2);//视频封面
        MovieItemModel movieItemModel = new MovieItemModel(recordVideoPath, videoDuration, videoPicPath);
        movieItemModel.setVideoTime(videoDuration);
        movieItemModel.setFilPicPath(videoPicPath);

        // 2、合成视频、音频后数据库中生成一条对应的视频记录
        MovieItemDb movieItemDb = new MovieItemDb();
        movieItemDb.setUid(String.valueOf(UserInfoManager.getUid()));
        movieItemDb.setKey(mTimeKey);
        movieItemDb.setVideoTime(videoDuration.doubleValue());
        movieItemDb.setFilPicPath(videoPicPath);
        movieItemDb.setFrom(IvyConstants.VIDEO_EDIT);
        movieItemDb.setIsScreenRecord(1);
        movieItemDb.setVideo_from(true);//设置为true,视频发布界面返回到视频编辑时判断是否删除

        FFmpegUtils fFmpegUtils = new FFmpegUtils(recordVideoFile.getParent());
        //系统声音
        int currentVolume = audio.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
        KLog.e("系统音量=" + currentVolume);
        if (currentVolume == 0) {
            //小米手机设备获取声音来源
            currentVolume = audio.getStreamVolume(AudioManager.STREAM_SYSTEM);
        }
        if (currentVolume == 0) {
            //如果音量还为0，设置一个大于0的值
            currentVolume = 2;
        }
        if (recordAudioFile.length() == 0) {
            currentVolume = 0;
            KLog.e("声音文件大小为0，声音大小设置为" + currentVolume);

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
                    //转换后的视频路径
                    updateMovieItemDb(movieItemModel, movieItemDb, outputFilePath);
//                    FileUtil.fileScanVideo(this, mRecordFilePath, mRecordWidth, mRecordHeight, mRecordSeconds);
                    //删除转换前的视频文件、音频文件
                    KLog.e(TAG, "recordVideoPath=" + recordVideoPath);
                    KLog.e(TAG, "recordAudioPath=" + recordAudioPath);
                    FileUtil.deleteSDFile(recordVideoPath);//视频文件路径
                    FileUtil.deleteSDFile(recordAudioPath);//音频文件路径
                    FileUtil.fileScanVideo(VideoCallActivity.this, outputFilePath);
                    //取消loading框
                    if (mLoadingDialog != null) {
                        mLoadingDialog.cancel();
                        mLoadingDialog.setBackListener(null);
                    }
                    if (isRecordingFinishActivity) {
                        //正在录屏时候退出了聊天
                        closeActivity();
                    }
                    isSavingRecord = false;//完成文件保存
                });
    }

    private void updateMovieItemDb(MovieItemModel movieItemModel, MovieItemDb movieItemDb, String outputFilePath) {
        movieItemModel.setFilePath(outputFilePath);
        movieItemDb.setFilePath(outputFilePath);
        mMovieItemDaoList.add(movieItemDb);
        //插入数据库
        mMovieItemDbDao.insert(movieItemDb);
        mMovieShowListAdapter.addData(movieItemModel);
        //更新视频录制总时长
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
        //创建广播
        mInnerReceiver = new InnerReceiver();
        //动态注册广播
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
        if (!isOutGoing()) { // 拨打端
            setLocalPre(outGoingAvatar, outGoingSex);
        } else { // 接受端
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
                        .placeholder(R.mipmap.ic_my_head_default)   //加载成功之前占位图
                        .error(R.mipmap.ic_my_head_default))
                .into(mIvVcWaitAvatar);

        // bg
        if (avatar == null || TextUtils.isEmpty(avatar)) {
            mIvVcWaitBg.setBackground(new ColorDrawable(ContextCompat.getColor(this, R.color.color_999999)));
        } else {
            Glide.with(this)
                    .load(avatar)
                    .apply(new RequestOptions()
                            .placeholder(R.mipmap.ic_my_head_default)) //加载成功之前占位图
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
            case R.id.vc_iv_record: // 开启/结束录制
                if (XClickUtil.isFastDoubleClick(mVcIvRecord, 2000)) {
                    return;//2s内点击无效
                }
                rfStatusRecordDiaTime(mVcIvRecord.isSelected(), true, true);
                break;
            case R.id.vc_ll_record_time: // 结束对方录制屏幕
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
        // 控制必须授权
        if (!hasPermission && !mPermissionManager.isShowing()) {
            mPermissionManager.requestPermissions(this, mPermissionListener, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, REQUEST_CODE_PERMISSIONS_1100);
        }

        // 如果显示就取消通知提示
        if (mNotificationManager != null && isCallTime()) {
            NotificationUtils.dismissNotification(mNotificationManager, 2);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        KLog.d("onPause");

        // 加入了启动模式，保证一定是最顶层
        if (isCallTime()) {
            // 离开添加通知提示，正在通话中
            if (!isOutGoing()) { // 拨打端
                mNotificationManager = NotificationUtils.showNotification(IvyApp.getInstance().getApplicationContext(),
                        outGoingUserName,
                        getString(R.string.in_a_video_call),
                        2, new Intent(this, VideoCallActivity.class));
            } else { // 接受方
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
        // 释放资源
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
        // 关闭声音 (有时候异常了，就需要关闭声音)
//        if (!isOutGoing()) {
//            AudioSoundPoolManager.getInstance(VideoCallActivity.this).stopOutGoing();
//        }

        // 关闭等待动画
        mIvVcWaitLoading.hide();

        // 关闭计时器
        stopRecordTimTime();
        stopRecordTime(false);
        stopCallTime();
        stopTimeTask();

        // 关闭环信
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
                            if (!isOutGoing()) { // 拨打端
                                finishCurrentActivity(true);
                            } else { // 接受方
                                allBack(true); // 主动拒绝，发送消息并关闭
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
                                        // 停止声音的响动  关闭倒计时 开启声音录制
                                        stopTimeTask();
//                                    mVideoCallManager.onLocalAudioMuteClicked(true);
                                        rfStatusCallTime(true);
                                    } else {
                                        // 接受端 获取到远端视频就停止倒计时//
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
                                    // 关闭拨打端
                                    KLog.e(TAG, "----onRemoteUserLeft 关闭聊天 isRecording=" + isRecording + ",isRecordingFinishActivity=" + isRecordingFinishActivity);
                                    inComingBack();
                                }
                            };
                            mVideoCallManager.setListener(mCallListener);*/
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
//                            ToastUtil.showToast(VideoCallActivity.this, e.getMessage().toString());

                            if (!isOutGoing()) { // 拨打端
                                finishCurrentActivity(true);
                            } else { // 接受方
                                allBack(true); // 主动拒绝，发送消息并关闭
                            }

                            break;
                        }

                        if (!isOutGoing()) { // 拨打端
                            // 发送消息
                            EMManager.INSTANCE.sendTxtAttrSendMessage(mCallBean);
                            // 显示拨打铃声
//                            AudioSoundPoolManager.getInstance(VideoCallActivity.this).playOutGoing();
                            // 告诉后台等待的状态
                            requestVCmodify_video_chat_status(0, mCallBean.getRoomID(), null);
                            // 倒计时8s后就关闭当前
                            startTime(9);
                        } else {// 接收端
                            // 倒计时10s后就关闭当前
                            startTime(11);
                        }
                        break;
                }
            }

            @Override
            public void doAfterDeniedCancel(String[] permission, String tag) {
                super.doAfterDeniedCancel(permission, tag);
                KLog.d("doAfterDeniedCancel");
                if (!isOutGoing()) { // 拨打端
                    finishCurrentActivity(true);
                } else { // 接受方
                    allBack(true); // 主动拒绝，发送消息并关闭
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

    // --------------------------------------- 触摸滑动左右拍摄视频 start ----------------------------------------------
    private void initTouchListener() {
        // 设置左右屏幕对半分
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
        // 设置mMoveView居中
        mMoveView.post(() -> {
            ConstraintLayout.LayoutParams remote_params = (ConstraintLayout.LayoutParams)
                    mMoveView.getLayoutParams();
            remote_params.leftMargin = (screenWidth - mMoveView.getWidth()) / 2;
            remote_params.rightMargin = (screenWidth - mMoveView.getWidth()) / 2;
            mMoveView.setLayoutParams(remote_params);
        });
        // 触摸左右滑动的监听
        mMoveView.setOnTouchListener(new View.OnTouchListener() {
            private ConstraintLayout.LayoutParams remote_params;
            private ConstraintLayout.LayoutParams local_params;
            private ConstraintLayout.LayoutParams move_params;
            private int lastX;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 录制的时候不再响应触摸事件
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

                        //使用 layout 进行重新定位
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
    // --------------------------------------- 触摸滑动左右拍摄视频 end ----------------------------------------------

    // --------------------------------------- 计时器 start ----------------------------------------------

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
                        // 告诉后台超时的状态
                        requestVCmodify_video_chat_status(3, mCallBean.getRoomID(), null);
                        if (!isOutGoing()) { // 拨打端
                            // 仅仅是关闭当前界面
                            allBack(false);
                            ToastUtil.showToast(VideoCallActivity.this, getString(R.string.unable_to_access_the_video_invitation));
                        } else {
                            // 如果是未加入就关闭界面了
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
                        if (aLong < 60) { // 0s - 1分钟
                          /*  mVcTvVcTime.setText(String.format(getString(R.string.vc_0_0),
                                    aLong < 10 ? "0" + aLong : String.valueOf(aLong)));*/
                            int minute = (int) (aLong / 60);
                            int second = (int) (aLong % 60);
                            mVcTvVcTime.setText(String.format(getString(R.string.vc_0_0_1_1),
                                    minute < 10 ? "0" + minute : String.valueOf(minute),
                                    second < 10 ? "0" + second : String.valueOf(second)));
                        } else if (aLong < 3600) {  // 60*60 = 1分钟 - 1小时
                            int minute = (int) (aLong / 60);
                            int second = (int) (aLong % 60);
                            mVcTvVcTime.setText(String.format(getString(R.string.vc_0_0_1_1),
                                    minute < 10 ? "0" + minute : String.valueOf(minute),
                                    second < 10 ? "0" + second : String.valueOf(second)));
                        } else if (aLong < 86400) {   // 60 * 60 * 24 = 1小时 - 24小时
                            // 控制設置一次
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
                        } else { // >24小时，暂时设置退出，即最多通话一天
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
        return timeCallTask != null && !timeCallTask.isDisposed(); // 代表再通话中
    }

    /**
     * @param recordTotalTime 已录制视频总时长
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
                    // 最多录制180s
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
    // --------------------------------------- 计时器 end ----------------------------------------------

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

    // 是否是接收方
    public boolean isOutGoing() {
        return isOutGoing;
    }

    // ----------------------------------  关闭  start  ----------------------------------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mVcIvRecord.isSelected()) { // 正在录制屏幕
                ToastUtil.showToast(this, getString(R.string.perform_other_operations));
            } else {
                showDialogBack();
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_HOME) {
            KLog.e("-----按了Home键");
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            KLog.e("-----按了菜单键");
        } else if (keyCode == KeyEvent.KEYCODE_POWER) {
            KLog.e("-----按了电源键");
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
     * @param isActive 是否是主动关闭
     */
    private void allBack(boolean isActive) {
        if (isActive) {
            // 关闭信号，结束通话
            if (mCallBean != null) {
//                if (!isOutGoing()) { // 拨打端
//                } else { // 接收方的信息
//                }
                // 此处发送的 mCallBean  如果是接收方 就实际上是发送方的bean
                mCallBean.setRequestType(EMMessageHelper.CLOSE);
                EMManager.INSTANCE.sendTxtAttrSendMessage(mCallBean);
            }
        }

        // 离开房间
        /*if (mVideoCallManager != null) {
            mVideoCallManager.leaveChannel();
        }*/

        // 关闭操作
        if (!isOutGoing()) { // 拨打端
            outGoingBack();
        } else {// 接受端
            KLog.e(TAG, "----allback 关闭聊天 isRecording=" + isRecording + ",isRecordingFinishActivity=" + isRecordingFinishActivity);
            inComingBack();
        }
    }

    // 拨打端关闭
    private void outGoingBack() {
//        AudioSoundPoolManager.getInstance(VideoCallActivity.this).stopOutGoing();
        stopTimeTask();
        finishCurrentActivity(true);
    }

    // 接受端关闭
    private void inComingBack() {
        if (isGetComingBack){
            return;
        }
        isGetComingBack = true;
        stopTimeTask();
        if (isRecording) {
            //记录状态
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
        // 关闭所有的计时器 start
        stopCallTime();
        stopRecordTimTime();
        stopRecordTime(isSaveRecordFile);
        stopTimeTask();
        // 关闭所有的计时器 end

        // 关闭所有动画start
        if (mVcIvRecord.isSelected()) {
            mVcIvDiaRecordTime.clearAnimation();
        }
        if (mVcLlRecordTime.isSelected()) {
            mVcIvAcceptRecordTime.clearAnimation();
        }
        // 关闭所有动画end

        // 告诉后台结束的状态
        if (mCallBean != null) {
            requestVCmodify_video_chat_status(4, mCallBean.getRoomID(),
                    mVcTvVcTime == null || mVcTvVcTime.getText() == null || TextUtils.isEmpty(mVcTvVcTime.getText()) ?
                            null : mVcTvVcTime.getText().toString());
        }

        // 正在录屏时对方结束聊天，先保存录屏文件,到RecordListener中关闭
        if (isRecordingFinishActivity) {
            return;
        }
        closeActivity();

//        showLastActivityVideoCallHistory();
    }

    /**
     * 判断是否有录屏文件，有就跳到下一页，没有就关闭页面，回到视频聊天入口页
     */
    private void closeActivity() {
        // 有录屏文件
        if (mMovieShowListAdapter != null && mMovieShowListAdapter.getData().size() > 0) {
            Intent intent = new Intent(VideoCallActivity.this, VideoCallRecordActivity.class);
            ArrayList<MovieItemModel> data = (ArrayList<MovieItemModel>) mMovieShowListAdapter.getData();
            intent.putExtra(VideoCallRecordActivity.INTENT_KEY_ADAPTER_MODEL, data);
            intent.putExtra(VideoCallRecordActivity.INTENT_KEY_DB_MODEL, mMovieItemDaoList);
            startActivity(intent);
        }
        isRecordingFinishActivity = false;

        // 关闭环信


        finish();
    }


   /* private void showLastActivityVideoCallHistory() {
        // 上一个界面，弹出再此期间的拨打历史
        Activity activity = ActivityUtils.getLastSecondActivity();
        if (activity != null && activity instanceof BaseVideoCallActivity) {
            ((BaseVideoCallActivity) activity).showHistoryVideoCallResponseDialog();
        } else {// 一定要控制上一个activity 显示 历史信息
            EMMessageHelper.isShowVideoCallHistory = true;
        }
    }*/
    // ----------------------------------  关闭  end  ----------------------------------------


    // ----------------------------------  存储  start  ----------------------------------------
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

    // ----------------------------------  存储  end  ----------------------------------------
    // ----------------------------------  屏幕常亮  start  ----------------------------------------
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
    // ----------------------------------  屏幕常亮  end  ----------------------------------------

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
        // 仅切换摄像头
        mVcIvRecord.setVisibility(View.GONE);
        mVcIvSwitch.setVisibility(View.VISIBLE);
        // 未通话
        mVcTvVcTime.setVisibility(View.GONE);
        mVcTvVcTimeTip.setVisibility(View.GONE);
        // 拨打端未录像
        mVcTvDiaRecordTime.setVisibility(View.GONE);
        mVcIvDiaRecordTime.setVisibility(View.GONE);
        mVcTvDiaRecordTime.setText("00:00");
        // 接受端未录像
        mVcLlRecordTime.setVisibility(View.GONE);
        // 设置录像计时器文字显示
        mVcTvAcceptRecordTime.setVisibility(View.VISIBLE);
        mVcTvAcceptRecordTime.setText("00:00");
        // 设置录像的状态都为默认状态
        mVcIvRecord.setSelected(false);
        mVcLlRecordTime.setSelected(false);
    }

    /**
     * 开始通话，开启通话计时器
     *
     * @param isVisible
     */
    private void rfStatusCallTime(boolean isVisible) {
        if (isVisible) {
            // 关闭等待状态
            mVcFrameRemoteVideoViewLoading.setVisibility(View.GONE);
            mIvVcWaitLoading.hide();

            // 打开录制按钮
            mVcIvRecord.setVisibility(View.VISIBLE);

            mVcTvVcTime.setVisibility(View.VISIBLE);
            mVcTvVcTimeTip.setVisibility(View.VISIBLE);
            // 开启计时器
            startCallTime();
        } else {
            // 关闭录制按钮
            mVcIvRecord.setVisibility(View.GONE);

            mVcTvVcTime.setVisibility(View.GONE);
            mVcTvVcTimeTip.setVisibility(View.GONE);
            // 关闭计时器
            stopCallTime();
        }
    }

    /**
     * 拨打端 开始录制，开启录制计时器
     * 仅仅本地操作
     *
     * @param isVisible
     * @param isSendEM         是否发送环信信息告知对方
     * @param isSaveRecordFile 结束录屏是判断是否保存录屏文件（接收端终止了录屏不能保存）
     */
    private synchronized void rfStatusRecordDiaTime(boolean isVisible, boolean isSendEM, boolean isSaveRecordFile) {
        if (isVisible) {
            if (!mVcIvRecord.isSelected()) {// 防重复
                return;
            }
            mVcIvRecord.setSelected(false);
            mVcIvRecord.setBackgroundResource(R.drawable.btn_record_default_vc);

            // 打开录制按钮 返回按钮
            mVcIvBack.setVisibility(View.VISIBLE);
            mVcIvSwitch.setVisibility(View.VISIBLE);
            // 开启通话时间的显示
            mVcTvVcTime.setVisibility(View.VISIBLE);
            mVcTvVcTimeTip.setVisibility(View.VISIBLE);

            //显示录屏列表
            mVcRvDiaRecord.setVisibility(View.VISIBLE);

            // 拨打端关闭录像
            mVcTvDiaRecordTime.setVisibility(View.GONE);
            mVcIvDiaRecordTime.setVisibility(View.GONE);
            mVcTvDiaRecordTime.setText("00:00");

            // 关闭动画
            mVcIvDiaRecordTime.clearAnimation();

            //自己关闭录屏时 判断对方是否正在录屏，显示录屏提示
            if (isRecording && isOtherRecording) {
                mVcLlRecordTime.setVisibility(View.VISIBLE);
            } else {
                mVcLlRecordTime.setVisibility(View.INVISIBLE);
            }
            // 发送通知告知我已经停止录制视频了（在recordlistener中发送）
            /*if (isSendEM && mCallBean != null) {
                EMManager.INSTANCE.sendTxtAttrSendMessage(EMMessageHelper.RECORD_END, mCallBean.getAccept_username(), mCallBean.getRoomID(), true);
            }*/

            // 关闭录制视频  并关闭计时器
            stopRecordTime(isSaveRecordFile);


        } else {
            if (mVcIvRecord.isSelected()) {// 防重复
                return;
            }
            if (mRecordTotalTime >= 180) {
                showToast(getResources().getString(R.string.record_time_end_tip));
                return;
            }
            mVcIvRecord.setSelected(true);
            mVcIvRecord.setBackgroundResource(R.drawable.btn_record_press_vc);

            // 关闭视频切换按钮  返回按钮
            mVcIvBack.setVisibility(View.GONE);
            mVcIvSwitch.setVisibility(View.GONE);
            // 关闭通话时间的显示
            mVcTvVcTime.setVisibility(View.GONE);
            mVcTvVcTimeTip.setVisibility(View.GONE);

            //隐藏录屏列表
            mVcRvDiaRecord.setVisibility(View.GONE);

            if (!isRecording && isOtherRecording) {
                mVcLlRecordTime.setVisibility(View.INVISIBLE);
            }

            // 发送通知告知我已经开始录制视频了(需要放到recordListener start方法）
            /*if (isSendEM && mCallBean != null) {
                EMManager.INSTANCE.sendTxtAttrSendMessage(EMMessageHelper.RECORD_START, mCallBean.getAccept_username(), mCallBean.getRoomID(), true);
            }*/

            // 开启录制视频  并开始计时器
            startRecordTime(mRecordTotalTime);
        }
    }

    /**
     * 接受端 显示录制时间，或者被关闭录制时间
     */
    private void rfStatusRecordAcceptTime(boolean isVisible, long initialDelay, boolean isSendEM) {
        if (isVisible) {
            if (mVcLlRecordTime.isSelected()) {// 防重复
                return;
            }
            mVcLlRecordTime.setSelected(true);

            // 显示就可以设置点击时间
            mVcLlRecordTime.setVisibility(View.VISIBLE);

            // 开启动画
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

            // 开始倒计时
            startRecordTimTime(initialDelay);
        } else {
            if (!mVcLlRecordTime.isSelected()) {// 防重复
                return;
            }
            mVcLlRecordTime.setSelected(false);
            mVcLlRecordTime.setVisibility(View.GONE);

            // 关闭动画
            mVcIvAcceptRecordTime.clearAnimation();
            mVcTvAcceptRecordTime.setText("00:00");

            // 发送通知告知对方，我关闭了你的屏幕录制
            if (isSendEM && mCallBean != null) {
                EMManager.INSTANCE.sendTxtAttrSendMessage(EMMessageHelper.RECORD_END, mCallBean.getAccept_username(), mCallBean.getRoomID(), false);
            }

            // 关闭倒计时
            stopRecordTimTime();
        }
    }


    // ----------------------------------  UI  end  ----------------------------------------

    /**
     * 拨打端监听器，接受到接受段的回复信号 ，拒绝，接受，关闭
     */
    public void videoCallMessageCallBack(String requestType, String accept_userName, String roomID, long dialTime, boolean isMe) {
        // 是当前用户, 且是曾经具体的时间段发送的视频消息，才能操作当前界面的数据
        if (mCallBean != null && accept_userName.equals(mCallBean.getAccept_username()) && mCallBean.getRoomID().equals(roomID)) {
            switch (requestType) {
                case EMMessageHelper.REFUSE:
                    ToastUtil.showToast(this, getString(R.string.register_you_video));

                    // 关闭拨打端
                    outGoingBack();
                    break;
                case EMMessageHelper.REFUSE_BUSY:
                    ToastUtil.showToast(this, getString(R.string.the_other_party_is_busy));

                    // 关闭拨打端
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

                    // 关闭倒计时 关闭拨打声音
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
                    KLog.e(TAG, "----EMMessageHelper.CLOSE 关闭聊天 isRecording=" + isRecording + ",isRecordingFinishActivity=" + isRecordingFinishActivity);
                    // 关闭拨打端
                    inComingBack();//环信关闭视频聊天消息，关闭时会同时调用声网VideoCallManager.onRemoteUserLeft方法，导致调用两次closeActivity()
                    break;
                case EMMessageHelper.RECORD_START:   // 对方开始录制屏幕
                    isOtherRecording = true;
                    // 显示录制时间提示
                    rfStatusRecordAcceptTime(true,
                            (System.currentTimeMillis() - dialTime) / 1000 + 1, false);
                    break;
                case EMMessageHelper.RECORD_END:  // 可能是拨打端，可能是接受端
                    KLog.d("isMe: " + isMe);
                    if (isMe) { // 发送端关闭了自己的录制
                        isOtherRecording = false;
                        ToastUtil.showToast(this, getString(R.string.stop_record_screen_by_me));
                        rfStatusRecordAcceptTime(false, 0, false);
                    } else { // 接收端关闭了对方的录制
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
                // 开启动画
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
            rfStatusRecordDiaTime(true, false, false);//拒绝录屏 显示未录屏UI
        }
    }

    @Override
    public void deleteListener(int position) {
        //删除当前的缓存集合数据以及删除数据库数据
        if (mMovieShowListAdapter != null && mMovieItemDaoList != null && mMovieItemDaoList.size() > position) {

            MovieItemDb movieItemDb = mMovieItemDaoList.get(position);
            //移除数据库
            mMovieItemDbDao.delete(movieItemDb);
            mMovieShowListAdapter.deleteItem(position);
            mMovieItemDaoList.remove(movieItemDb);

            //重新计算录屏时间
            mRecordTotalTime = updateRecordTotalTime(mMovieShowListAdapter.getData());
            //删除文件
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
                            KLog.e("-------正在录屏回到桌面，结束录屏");
                            rfStatusRecordDiaTime(isRecording, true, true);
                        }
                    }
                }
            }
        }
    }
}
