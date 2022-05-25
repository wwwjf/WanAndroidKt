package com.xianghe.ivy.ui.module.pic2video;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tangyx.video.ffmpeg.FFmpegRun;
import com.xianghe.ivy.R;
import com.xianghe.ivy.adapter.recyclerview.IRecyclerItemClickListener;
import com.xianghe.ivy.model.ImageBean;
import com.xianghe.ivy.model.MusicBean;
import com.xianghe.ivy.ui.module.record.model.MovieEditModel;
import com.xianghe.ivy.ui.module.record.model.MovieItemModel;
import com.xianghe.ivy.ui.module.videoedit.VideoEditPlayUtils;
import com.xianghe.ivy.ui.module.videoedit.audio_record.VideoEditAudioRecordDialog;
import com.xianghe.ivy.ui.module.videoedit.sound.VESoundPopupWindow;
import com.xianghe.ivy.utils.IvyUtils;
import com.xianghe.ivy.utils.KLog;
import com.xianghe.ivy.weight.download.DownMusicFile;
import com.xianghe.ivy.weight.download.onDownloadProgressCallBack;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Pic2VideoActivity extends AppCompatActivity implements View.OnClickListener, MusicSelectedDialog.OnMusicSelectedListener, IRecyclerItemClickListener {
    private static final String TAG = "Pic2VideoActivity";
    private static final String KEY_IMG_PATHS = "key_img_paths";

    private String inputPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/aaaaaaaaaloosu/in.mp4";
    private String outputPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/aaaaaaaaaloosu/";

    private HandlerThread mBackgroundThread;
    private Handler mBackgroundHandler;

    private ArrayList<String> mImgPaths;

    private SurfaceView mSurfaceView;
    private View mBtnBack;
    private View mBtnVol;
    private View mBtnMusic;
    private View mBtnRecord;
    private View mBtnNext;
    private RecyclerView mViewList;

    private LinearLayoutManager mLayoutManager;
    private SwitchStyleAdapter mAdapter;
    private MusicSelectedDialog mMusicSelectedDialog;

    private LoadingDialog mLoadingDialog;

    private MovieEditModel mEditModel = new MovieEditModel(outputPath, null);
    private VideoEditPlayUtils mVideoEditUtils;

    public static Intent getStartIntent(Context context, List<String> imgPath) {
        ArrayList<String> tmp = new ArrayList<>();
        if (imgPath != null) {
            tmp.addAll(imgPath);
        }
        Intent intent = new Intent(context, Pic2VideoActivity.class);
        intent.putExtra(KEY_IMG_PATHS, tmp);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_2_video);
        init(savedInstanceState);
        findView(savedInstanceState);
        initView(savedInstanceState);
        initListener(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        KLog.d(TAG, "");
        if (mVideoEditUtils != null) {
            mVideoEditUtils.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        KLog.d(TAG, "");
        if (mVideoEditUtils != null) {
            mVideoEditUtils.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLoadingDialog.dismiss();
        mEncodeListener = null;
        if (mVideoEditUtils != null) {
            mVideoEditUtils.destroy();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mVideoEditUtils != null) {
            mVideoEditUtils.onSaveInstanceState(outState);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (mVideoEditUtils != null) {
            mVideoEditUtils.onRestoreInstanceState(savedInstanceState);
        }
    }

    private void init(Bundle savedInstanceState) {
        final Context ctx = this;
        final Intent intent = getIntent();

        Serializable imgs = intent.getSerializableExtra(KEY_IMG_PATHS);
        mImgPaths = (ArrayList<String>) imgs;

        List<SwitchModel> modelList = new ArrayList<>();
        modelList.add(new SwitchModel(SwitchModel.Type.NORMAL));
        modelList.add(new SwitchModel(SwitchModel.Type.QUICK));
        modelList.add(new SwitchModel(SwitchModel.Type.FADE));
        modelList.add(new SwitchModel(SwitchModel.Type.HORIZONTALSCROLLING));
        modelList.add(new SwitchModel(SwitchModel.Type.VERTICALSCROLLING));
        modelList.add(new SwitchModel(SwitchModel.Type.GRAY));

        mLayoutManager = new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false);
        mAdapter = new SwitchStyleAdapter(modelList);

        mBackgroundThread = new HandlerThread("pic_encode_thread");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());

        mLoadingDialog = new LoadingDialog(this);
    }

    private void findView(Bundle savedInstanceState) {
        mSurfaceView = findViewById(R.id.surface_view);

        mBtnBack = findViewById(R.id.btn_back);
        mBtnVol = findViewById(R.id.btn_vol);
        mBtnMusic = findViewById(R.id.btn_music);
        mBtnRecord = findViewById(R.id.btn_record);
        mBtnNext = findViewById(R.id.btn_next);

        mViewList = findViewById(R.id.view_list);
    }

    private void initView(Bundle savedInstanceState) {
        mViewList.setLayoutManager(mLayoutManager);
        mViewList.setAdapter(mAdapter);

        mVideoEditUtils = new VideoEditPlayUtils(mSurfaceView, mEditModel);
    }

    private void initListener(Bundle savedInstanceState) {
        mSurfaceView.getHolder().addCallback(surfaceCallback);

        mBtnBack.setOnClickListener(this);
        mBtnVol.setOnClickListener(this);
        mBtnMusic.setOnClickListener(this);
        mBtnRecord.setOnClickListener(this);
        mBtnNext.setOnClickListener(this);

        mAdapter.setItemClickListener(this);
    }

    private void onClickBtnBack() {
        finish();
    }

    private void onClickBtnVol() {
        VESoundPopupWindow soundPopupWindow = new VESoundPopupWindow(this, mVideoEditUtils,
                mVideoEditUtils.getMusicPath(),
                mVideoEditUtils.getRecordPath(), false);
        soundPopupWindow.showPopupWindow(mBtnVol);
    }

    private void onClickBtnMusic() {
        if (mMusicSelectedDialog == null) {
            mMusicSelectedDialog = new MusicSelectedDialog();
            mMusicSelectedDialog.setListener(this);
        }

        try {
            mMusicSelectedDialog.show(getSupportFragmentManager(), "123");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onClickBtnRecord() {
        VideoEditAudioRecordDialog recordDialog = new VideoEditAudioRecordDialog(this, mVideoEditUtils.getVideoTotalTime(), new VideoEditAudioRecordDialog.recordStatusListener() {
            @Override
            public void sureRecordListener(File file, String filePath) {
                KLog.i(TAG, "file = " + file);
                mVideoEditUtils.setRecordPath(filePath);// 保存录音文件路径
                mVideoEditUtils.openAllVolume();
                mVideoEditUtils.resetAllPlayer(); // 重播音视频

                mBtnVol.setVisibility(View.VISIBLE);
            }

            @Override
            public void startFirstRecordListener() {
                KLog.i(TAG, "");
                mVideoEditUtils.closeAllVolume();
            }

            @Override
            public void dismissDialogListener() {
                KLog.w(TAG, "");
                mVideoEditUtils.openAllVolume();
            }
        });
        recordDialog.show();
    }

    private void onClickBtnNext() {

    }

    @Override
    public void onMusicSelected(MusicSelectedDialog dialog, MusicBean music) {
        mLoadingDialog.show();

        String musicId = music.getId();
        String musicName = music.getMusic_name();
        String url = music.getMusic_link();
        DownMusicFile.getInstance().downloadFile(this, url, musicName, musicId, new onDownloadProgressCallBack() {
            @Override
            public void onSuccessDownload(String path) {
                mLoadingDialog.hide();
                KLog.i(TAG, "path = " + path);
                mVideoEditUtils.setMusicPath(path);
                mBtnVol.setVisibility(View.VISIBLE);
            }

            @Override
            public void onErrorDownload(Throwable t) {
                mLoadingDialog.hide();
                KLog.e(TAG, "t = " + t);
            }

            @Override
            public void onProgressDownload(int progress) {
                KLog.d(TAG, "progress = " + progress);
            }
        });
    }

    private SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            KLog.i(TAG, "holder = " + holder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            KLog.d(TAG, "holder = " + holder);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            KLog.e(TAG, "holder = " + holder);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                onClickBtnBack();
                break;
            case R.id.btn_vol:
                onClickBtnVol();
                break;
            case R.id.btn_music:
                onClickBtnMusic();
                break;
            case R.id.btn_record:
                onClickBtnRecord();
                break;
            case R.id.btn_next:
                onClickBtnNext();
                break;
        }
    }

    @Override
    public void onItemClick(RecyclerView parent, int position, RecyclerView.ViewHolder holder, View view) {
        int selectedItem = mAdapter.getSelectedItem();
        if (selectedItem == position) {
            return;
        }
        mLoadingDialog.show();
        mAdapter.setSelectedItem(position);

        SwitchModel switchModel = mAdapter.getItem(position);
        final int type = switchModel.getType();

        ImageBean imageBean = new ImageBean();
        File file = new File(inputPath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        imageBean.setOutputPath(file.getAbsolutePath());
        imageBean.setImagePathList(mImgPaths);
        imageBean.setOutputType(type);
        FFmpegRun.sEncodeListener = mEncodeListener;

        // FFmpegRun.setImageToNativeForEncodeMP4 及其耗时，一定跑在子线程
        mBackgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                FFmpegRun.setImageToNativeForEncodeMP4(imageBean);
            }
        });

    }

    private FFmpegRun.EncodeListener mEncodeListener = new FFmpegRun.EncodeListener() {
        @Override
        public void onProgress(long progress, long max) {
            KLog.v(TAG, "max = " + max + ", progress = " + progress);
        }

        @Override
        public void onEnd() {
            KLog.d(TAG, "onEnd");
            mLoadingDialog.hide();

            mSurfaceView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    final String moviePath = inputPath;
                    BigDecimal duration = BigDecimal.valueOf(Double.parseDouble(IvyUtils.getMediaDuration(moviePath)));

                    List<MovieItemModel> movieItems = new ArrayList<>();
                    movieItems.add(new MovieItemModel(moviePath, duration));

                    mEditModel.setMovieItemModels(movieItems);

                    VideoEditPlayUtils editUtil = mVideoEditUtils;
                    editUtil.resetAllPlayer();
                }
            }, 1000);

        }
    };
}
