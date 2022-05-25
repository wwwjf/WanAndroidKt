package com.xianghe.ivy.ui.module.preview_movie;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.tangyx.video.ffmpeg.FFmpegUtils;
import com.wwwjf.mp4processor.cut.VideoCrop;
import com.xianghe.ivy.R;
import com.xianghe.ivy.constant.Global;
import com.xianghe.ivy.constant.RxBusCode;
import com.xianghe.ivy.mvp.BaseVideoCallActivity;
import com.xianghe.ivy.ui.module.record.RecordActivity;
import com.xianghe.ivy.ui.module.record.model.MovieClipModel;
import com.xianghe.ivy.ui.module.record.model.MovieItemModel;
import com.xianghe.ivy.utils.FileUtill;
import com.xianghe.ivy.utils.ViewUtil;
import com.xianghe.ivy.weight.ClipView;
import com.xianghe.ivy.weight.CustomProgress;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import gorden.rxbus2.RxBus;
import icepick.State;

import static com.xianghe.ivy.ui.media.config.MediaConfig.VIDEO_CILP_FILE;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class PreviewMovieActivity extends BaseVideoCallActivity implements ClipView.ClipListener {
    private static final String TAG = "PreviewMovieActivity";

    @BindView(R.id.tv_activity_preview_movie_title)
    TextView tvPreviewMovieTitle;
    @BindView(R.id.vp_activity_preview_recyclerView)
    RecyclerView mRecyclerView;                                                                     //所有播放的界面
    @BindView(R.id.cv_activity_preview_clipView)
    ClipView mClipView;

    private Unbinder mBind;

    private ArrayList<MovieItemModel> mMovieModels;

    private CustomProgress mCustomProgress;

    private String mVideoFileName;                                                                  //视频文件夹路径

    private final String fileName = "/xh_recorder/";

    private FFmpegUtils mFFmpegUtils;

    private VideoCrop mVideoCrop;

    public static final int INTENTBACKCODE = 10001;

    public static final int LOCAL_CODE = 100002;

    public static final String VIDEO_TIME = "video_time";

    public static final String VIDEO_PATH = "video_path";

    public static final String VIDEO_DIR_PATH = "video_dir_path";

    public static final String VIDEO_POSITION = "video_position";

    private String mFileName;

    private PreviewMovieAdapter movieAdapter;

    private int mFromAct;

    public static final int VIDEOEDIT = 1;
    public static final int VIDEORECORD = 3;

    public static final int LOCAL = 2;

    @State
    int mWidthInt;
    @State
    int mHeightInt;
    @State
    int mRotateInt;
    @State
    boolean isClip;
    @State
    MovieItemModel mMovieItemModel;

//    private ProgressDialog mProgressDialog;

    // 视频裁剪完成后黑屏处理
    private boolean isPause = false;
    private String cropVideoPath = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_movie);

        mBind = ButterKnife.bind(this);

        getIntentData();

        initListener();

//        initSmallVideo();

        onCreateState(savedInstanceState);
    }

    private void onCreateState(Bundle bundle) {
        if (bundle != null) {
            if (bundle.containsKey(Global.PARAMS)) {
                cropVideoPath = bundle.getString(Global.PARAMS);
            }
            if (bundle.containsKey(Global.PARAMS1)) {
                isPause = bundle.getBoolean(Global.PARAMS1);
            }
        }
    }

    private void initListener() {
        mClipView.setClipListener(this);
    }

    private void initRecyclerView(int videoHeight) {
        ViewPagerLinearLayoutManager layoutManager = new ViewPagerLinearLayoutManager(this, LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(layoutManager);
        movieAdapter = new PreviewMovieAdapter(R.layout.adapter_preview_movie, mMovieModels, this, videoHeight, mWidthInt, mHeightInt);
        movieAdapter.bindToRecyclerView(mRecyclerView);
        mRecyclerView.scrollToPosition(mMovieModels.indexOf(mMovieItemModel));
        layoutManager.setOnViewPagerListener(movieAdapter);
    }

    @Override
    protected void onResume() {
        if (movieAdapter != null) {
            movieAdapter.resumeVideo();
        }
        if (isPause) {
            isPause = false;
            // 执行裁剪完成未执行的操作
            if (cropVideoPath != null && !TextUtils.isEmpty(cropVideoPath)) {
                setBackData(cropVideoPath);
                cropVideoPath = null;
            }
        }
        super.onResume();
    }


    @Override
    protected void onPause() {
        if (movieAdapter != null) {
            movieAdapter.pauseVideo();
        }
        isPause = true;
        super.onPause();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null) {
            if (cropVideoPath != null && !TextUtils.isEmpty(cropVideoPath)) {
                outState.putString(Global.PARAMS, cropVideoPath);
            }
            outState.putBoolean(Global.PARAMS1, isPause);
        }
    }


    private void getIntentData() {
        mCustomProgress = new CustomProgress(this);
        MovieClipModel movieClipModel = (MovieClipModel) getIntent().getSerializableExtra("movie_model_list");
        int currentItemPosition = getIntent().getIntExtra("movie_model_position", 0);
        mFromAct = getIntent().getIntExtra("fromAct", 0);
        if (movieClipModel != null) {
            mVideoFileName = movieClipModel.getVideoFileName();
            mMovieModels = (ArrayList<MovieItemModel>) movieClipModel.getMovieItemModel();
            if (mMovieModels != null) {
                if(currentItemPosition>=0 && currentItemPosition < mMovieModels.size()) {
                    mMovieItemModel = mMovieModels.get(currentItemPosition);
                }else if(mMovieModels.size()>0 ){
                    mMovieItemModel = mMovieModels.get(0);
                }
                //如果不为空显示背景图片
                if (mMovieItemModel != null) {
                    initClipAndVideoData(false);
                }
            }
        }
    }

    private FFmpegUtils createFFMPEG() {
        if (mFFmpegUtils == null) {
            //判断视频文件夹是否存在
            if (!TextUtils.isEmpty(mVideoFileName)) {
                mFileName = mVideoFileName + VIDEO_CILP_FILE;
            } else {
                //如果文件不存在创建
                mFileName = createSaveFile() + VIDEO_CILP_FILE;
            }
            mFileName = mFileName.replace("//", "/");
            mFFmpegUtils = new FFmpegUtils(mFileName);
        }
        return mFFmpegUtils;
    }

    @OnClick({R.id.tv_activity_preview_movie_cancel, R.id.tv_activity_preview_movie_ensure})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_activity_preview_movie_cancel:
                //删除视频
                if(mFromAct == LOCAL) {
                    if (mMovieItemModel != null) {
                        deleteRotateFile(mMovieItemModel);
                    }
                }
                finish();
                break;
            case R.id.tv_activity_preview_movie_ensure:
                //剪辑视频
                clipVideo();
                break;
        }
    }

    private void deleteRotateFile(MovieItemModel itemModel) {
        if (itemModel != null){
            if (!itemModel.isVideo_from()){
                if (!TextUtils.isEmpty(itemModel.getFilePath())){
                    File file = new File(itemModel.getFilePath());
                    if (file.exists()){
                        file.delete();
                    }
                }
            }
        }

    }

    /**
     * 剪辑视频
     */
    private void clipVideo() {
        if (mClipView != null) {
            if (mMovieItemModel != null) {
                //判断是否有剪切

                if (BigDecimal.valueOf(mClipView.getEndTime()).subtract(BigDecimal.valueOf(mClipView.getStartTime())).compareTo(mMovieItemModel.getVideoTime()) == 0) {
                    isClip = false;
                    setBackData(mMovieItemModel.getFilePath());
                } else {
                    isClip = true;
                    mCustomProgress.show(getString(R.string.preview_cliping_movie_cliping), false, null);
                    //如果是本地视频
//                    if (mFromAct == 2) {
//                        videoCutMediaCodec();
//                    } else {
                        videoCutFFmpeg();
//                    }
                }
            }
        }
    }

    /**
     * 暂时不适应Codec 裁剪视频，太慢了
     */
    /*private void videoCutMediaCodec() {
        long startTime = BigDecimal.valueOf(mClipView.getStartTime()).multiply(BigDecimal.valueOf(1000)).setScale(0, BigDecimal.ROUND_HALF_UP).longValue();
        long dution = BigDecimal.valueOf(mClipView.getEndTime()).subtract(BigDecimal.valueOf(mClipView.getStartTime())).multiply(BigDecimal.valueOf(1000)).setScale(0, BigDecimal.ROUND_HALF_UP).longValue();
        long endTime = startTime + dution;


        //判断视频文件夹是否存在
        if (!TextUtils.isEmpty(mVideoFileName)) {
            mFileName = mVideoFileName + VIDEO_CILP_FILE;
        } else {
            //如果文件不存在创建
            mFileName = createSaveFile() + VIDEO_CILP_FILE;
        }
        FileUtill.creatDir2SDCard(mFileName);
        String ouputPath = mFileName + "/" + String.format("clipvideo-output-%s.mp4", System.currentTimeMillis() + "");
        KLog.d(this.getClass().getSimpleName(), "clipVideo: " + mMovieItemModel.getRotate());


        mVideoCrop = VideoUtils.startVideoCut(getBaseContext(),
                mMovieItemModel.getFilePath(),
                ouputPath,
                startTime * 1000,
                endTime * 1000,
                mMovieItemModel.getRotate() == 180 || mMovieItemModel.getRotate() == 360,
                new VideoCrop.OnEncoderListener() {
                    @Override
                    public void onStart() {
                        KLog.d("onStart");
                    }

                    @Override
                    public void onComplete(String outPutPath) {
                        KLog.d("onComplete  outPutPath :" + outPutPath);
                        if (outPutPath == null) {
                            if (mCustomProgress != null && mCustomProgress.isShowing()) {
                                mCustomProgress.dismiss();
                            }
                            return;
                        }
                        if (mCustomProgress != null && mCustomProgress.isShowing()) {
                            mCustomProgress.dismiss();
                        }
                        if (isPause) {// 如果已经黑屏，仅仅保存状态
                            cropVideoPath = outPutPath;
                            return;
                        }
                        //重新给数据赋值
                        setBackData(outPutPath);
                    }

                    @Override
                    public void onProgress(int onProgress) {
                        // KLog.d("onProgress  onProgress :" + onProgress);
                    }

                    @Override
                    public void onError(String msg) {
                        KLog.d("onError  msg :" + msg);
                        // 如果是编解码异常，就使用ffmpeg裁剪视频，二次处理方案
                        if (msg.equals(getApplicationContext().getResources().getString(com.example.mp4processor.R.string.vc_exception_video_cut))) {
                            runOnUiThread(() -> {
                                videoCutFFmpeg();
                            });
                            return;
                        }

                        if (mCustomProgress != null && mCustomProgress.isShowing()) {
                            mCustomProgress.dismiss();
                        }
                        runOnUiThread(() -> {
                            ToastUtil.showToast(getBaseContext(), msg);
                        });
                    }
                });
    }*/

    private void videoCutFFmpeg() {
        // ffmpeg 裁剪
        createFFMPEG().setOnFFmpegOutputListener(outputFilePath -> {
            //删除当前视频
            deleteRotateFile(mMovieItemModel);
            if (mCustomProgress != null && mCustomProgress.isShowing()) {
                mCustomProgress.dismiss();
            }
            if (isPause) {// 如果已经黑屏，仅仅保存状态
                cropVideoPath = outputFilePath;
                return;
            }
            //重新给数据赋值
            setBackData(outputFilePath);
        }).videoClip(mMovieItemModel.getFilePath()
                , BigDecimal.valueOf(mClipView.getStartTime()).multiply(BigDecimal.valueOf(1000)).setScale(0, BigDecimal.ROUND_HALF_UP).toString()
                , BigDecimal.valueOf(mClipView.getEndTime()).subtract(BigDecimal.valueOf(mClipView.getStartTime())).multiply(BigDecimal.valueOf(1000)).setScale(0, BigDecimal.ROUND_HALF_UP).toString());
    }

    /**
     * 设置返回的数据
     */
    private void setBackData(String outputFilePath) {
        if (mClipView != null) {
            //如果是本地视频
            if (mFromAct == LOCAL) {
                Intent intent = new Intent();
                intent.putExtra("from", RecordActivity.LOCAL);
                ArrayList<MovieItemModel> objects = new ArrayList<>();
                mMovieItemModel.setFilePath(outputFilePath);
                mMovieItemModel.setVideoTime(BigDecimal.valueOf(mClipView.getEndTime()).subtract(BigDecimal.valueOf(mClipView.getStartTime())));
                objects.add(mMovieItemModel);
                intent.putExtra("data", objects);
                RxBus.get().send(RxBusCode.ACT_VIDEO_PRE_REFRESH_CLIP, intent);
            } else if (mFromAct == VIDEOEDIT || mFromAct == VIDEORECORD ){
                Intent intent = new Intent();
                intent.putExtra(VIDEO_TIME, BigDecimal.valueOf(mClipView.getEndTime()).subtract(BigDecimal.valueOf(mClipView.getStartTime())));
                intent.putExtra(VIDEO_PATH, outputFilePath);
                intent.putExtra(VIDEO_DIR_PATH, mFileName);
                if (mMovieItemModel != null && mMovieModels != null) {
                    intent.putExtra(VIDEO_POSITION, mMovieModels.indexOf(mMovieItemModel));
                }
                setResult(INTENTBACKCODE, intent);
            }
            finish();
        }
    }

    private String createSaveFile() {
        String savePath;
        try {
            savePath = FileUtill.creatFile2SDCard(fileName);
        } catch (IOException e) {
            savePath = FileUtill.creatDir2SDCard(getFilesDir().getAbsolutePath() + fileName);
        }
        return savePath;
    }

    public void stopClip() {
        if (mClipView != null) {
            mClipView.onStopClip();
        }
    }

  /*
   不知为何返回事件监听不到
   @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("123", "onKeyDown: "+keyCode);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.d("123", "onKeyDown: ");
            if (mVideoCrop != null && !mVideoCrop.isComplete()) {
                Log.d("123", "onKeyDown: isComplete == false ");
                VideoUtils.stopVideoCut(mVideoCrop);
                Log.d("123", "onKeyDown: isComplete == "+mVideoCrop.isComplete());
                if (mCustomProgress != null && mCustomProgress.isShowing()) {
                    mCustomProgress.dismiss();
                }
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }*/

    @Override
    protected void onDestroy() {
        if (mBind != null) {
            mBind.unbind();
        }

        if (movieAdapter != null) {
            movieAdapter.destroyVideo();
        }

        if (mClipView != null) {
            mClipView.destroy();
        }

        if (mCustomProgress != null && mCustomProgress.isShowing()) {
            mCustomProgress.dismiss();
        }

        if (mMovieItemModel != null && isClip) {
            FileUtill.deleteFile(mMovieItemModel.getFilePath());
        }

        super.onDestroy();
    }


    public void resetClipView(int position) {
        if (mClipView != null) {
            mMovieItemModel = mMovieModels.get(position);
            initClipAndVideoData(true);
        }
    }

    private double recordTotalTime() {
        BigDecimal totalBigDecimal = new BigDecimal(BigInteger.ZERO);
        //遍历集合算总时间
        if (mMovieModels != null) {
            if (mMovieModels.size() > 0) {
                for (int i = 0; i < mMovieModels.size(); i++) {
                    totalBigDecimal = totalBigDecimal.add(mMovieModels.get(i).getVideoTime());
                }
                return totalBigDecimal.doubleValue();
            } else {
                return 0;
            }
        }
        return 0;
    }

    /**
     * 初始化界面跟video的数据
     *
     * @param needRefresh 是否需要刷新底部的滑动块
     */
    private void initClipAndVideoData(boolean needRefresh) {
        //初始化开始结束时间
        if (tvPreviewMovieTitle != null) {
            //显示视频条目
            tvPreviewMovieTitle.setText(String.format(getString(R.string.preview_movie_title), mMovieModels.indexOf(mMovieItemModel) + 1 + "", mMovieModels.size() + ""));
        }
        if (mFromAct == VIDEOEDIT) {            //视频编辑页面过来的
            mClipView.setTime(mMovieItemModel, mVideoFileName, mMovieModels.indexOf(mMovieItemModel), recordTotalTime(), 5);
        } else if (mFromAct == LOCAL) {
            mClipView.setTime(mMovieItemModel, mVideoFileName, mMovieModels.indexOf(mMovieItemModel), 5);
        }else if (mFromAct == VIDEORECORD){     //拍摄页面过来的
            mClipView.setTime(mMovieItemModel, mVideoFileName, mMovieModels.indexOf(mMovieItemModel), needRefresh);
        }else {
            mClipView.setTime(mMovieItemModel, mVideoFileName, mMovieModels.indexOf(mMovieItemModel), needRefresh);
        }
    }

    @Override
    public void onClipStopListener(double startTime, double endTime) {
        setVideoPositionPlay(startTime, endTime);
    }

    /**
     * 设置到指定位置播放
     */
    private void setVideoPositionPlay(double startTime, double endTime) {
        if (movieAdapter != null) {
            movieAdapter.playVideo(startTime * 1000, endTime * 1000);
        }
    }

    @Override
    public void onClipInflated() {
        //如果是本地的视频就判断宽高
        int videoHeight = mRecyclerView.getHeight() - ViewUtil.dp2px(PreviewMovieActivity.this, 14);
        initRecyclerView(videoHeight);
    }
//    //视频使用系统压缩转码
//    public void videoCompressio(String inputPath,String sRate,String scale,String bitrate){
//        int iRate = 0;
//        float fScale=0;
//        if (!TextUtils.isEmpty(sRate)) {
//            iRate = Integer.valueOf(sRate);
//        }
//        if (!TextUtils.isEmpty(scale)) {
//            fScale = Float.valueOf(scale);
//        }
//
//
//        BaseMediaBitrateConfig compressMode = new CBRMode(166, Integer.valueOf(bitrate));
//        compressMode.setVelocity(BaseMediaBitrateConfig.Velocity.ULTRAFAST);
//
//        LocalMediaConfig.Buidler buidler = new LocalMediaConfig.Buidler();
//        final LocalMediaConfig config = buidler
//                .setVideoPath(inputPath)
//                .captureThumbnailsTime(1)
//                .doH264Compress(compressMode)
//                .setFramerate(iRate)
//                .setScale(fScale)
//                .build();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.e("HongLi", "开始压缩");
//                        showProgress("", "转码中...", -1);
//                    }
//                });
//                OnlyCompressOverBean onlyCompressOverBean = new LocalMediaCompress(config).startCompress();
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
////                        Log.e("HongLi","结束压缩");
//                        hideProgress();
//                        String videopath = onlyCompressOverBean.getVideoPath();
//                        KLog.d("videopath = "+ videopath);
//
//                        int videoHeight = mRecyclerView.getHeight() - ViewUtil.dp2px(PreviewMovieActivity.this,14);
//                        mMovieItemModel.setFilePath(videopath);
//                        initRecyclerView(videoHeight);
//                    }
//                });
//
////                startActivity(intent);
////                MovieItemModel.setFilePath(videopath);
////                initRecyclerView(videoHeight);
//            }
//        }).start();
//    }
//    private void showProgress(String title, String message, int theme) {
//        if (mProgressDialog == null) {
//            if (theme > 0)
//                mProgressDialog = new ProgressDialog(this, theme);
//            else
//                mProgressDialog = new ProgressDialog(this);
//            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//            mProgressDialog.setCanceledOnTouchOutside(false);// 不能取消
//            mProgressDialog.setCancelable(false);
//            mProgressDialog.setIndeterminate(true);// 设置进度条是否不明确
//        }
//
//        if (!StringUtils.isEmpty(title))
//            mProgressDialog.setTitle(title);
//        mProgressDialog.setMessage(message);
//        mProgressDialog.show();
//    }
//
//    private void hideProgress() {
//        if (mProgressDialog != null) {
//            mProgressDialog.dismiss();
//        }
//    }
//    public static void initSmallVideo() {
//        // 设置拍摄视频缓存路径
//        File dcim = Environment
//                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
//        if (DeviceUtils.isZte()) {
//            if (dcim.exists()) {
//                JianXiCamera.setVideoCachePath(dcim + "/xw/");
//            } else {
//                JianXiCamera.setVideoCachePath(dcim.getPath().replace("/sdcard/",
//                        "/sdcard-ext/")
//                        + "/xw/");
//            }
//        } else {
//            JianXiCamera.setVideoCachePath(dcim + "/xw/");
//        }
//        // 初始化拍摄
//        JianXiCamera.initialize(false, null);
//    }
}
