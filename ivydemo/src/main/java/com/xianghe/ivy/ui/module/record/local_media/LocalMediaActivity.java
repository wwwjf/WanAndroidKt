package com.xianghe.ivy.ui.module.record.local_media;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.tangyx.video.ffmpeg.FFmpegRun;
import com.tangyx.video.ffmpeg.FFmpegUtils;
import com.xianghe.ivy.BuildConfig;
import com.xianghe.ivy.R;
import com.xianghe.ivy.app.greendao.GreenDaoManager;
import com.xianghe.ivy.constant.RxBusCode;
import com.xianghe.ivy.entity.dao.MovieItemDbDao;
import com.xianghe.ivy.entity.db.MovieItemDb;
import com.xianghe.ivy.manager.UserInfoManager;
import com.xianghe.ivy.model.ImageBean;
import com.xianghe.ivy.mvp.BaseVideoCallActivity;
import com.xianghe.ivy.ui.media.config.MediaConfig;
import com.xianghe.ivy.ui.module.preview_movie.PreviewMovieActivity;
import com.xianghe.ivy.ui.module.record.RecordActivity;
import com.xianghe.ivy.ui.module.record.model.MovieClipModel;
import com.xianghe.ivy.ui.module.record.model.MovieEditModel;
import com.xianghe.ivy.ui.module.record.model.MovieItemModel;
import com.xianghe.ivy.ui.module.videoedit.VideoEditActivity;
import com.xianghe.ivy.utils.FileUtill;
import com.xianghe.ivy.utils.IvyUtils;
import com.xianghe.ivy.weight.CustomProgress;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import gorden.rxbus2.Subscribe;
import gorden.rxbus2.ThreadMode;
import icepick.State;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.xianghe.ivy.app.IvyConstants.VIDEO_EDIT;
import static com.xianghe.ivy.model.ImageBean.NORMAL;
import static com.xianghe.ivy.ui.media.config.MediaConfig.VIDEO_ROTATE_FILE;
import static com.xianghe.ivy.ui.module.preview_movie.PreviewMovieActivity.LOCAL_CODE;
import static com.xianghe.ivy.ui.module.record.RecordActivity.LOCAL;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class LocalMediaActivity extends BaseVideoCallActivity {
    private static final String TAG = "LocalMediaActivity";

    private Unbinder mBind;

    @BindView(R.id.tv_activity_local_media_video)
    TextView mVideoText;

    @BindView(R.id.tv_activity_local_media_pic)
    TextView mPicText;

    @BindView(R.id.fl_activity_local_media_bottom_layout)
    FrameLayout mBottomLayout;

    @BindView(R.id.tv_activity_local_media_ensure)
    TextView mEnsureView;

    private LocalVideoFragment mLocalVideoFragment;

    private LocalPhotoFragment mLocalPhotoFragment;

    @State
    int mRotateInt, mSelectVideoCount;
    private double mSelectVideoTime;

    @State
    String mFileName;

    private FFmpegUtils mFFmpegUtils;
    private int mFrom;

    private CustomProgress mCustomProgress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_media);
        mBind = ButterKnife.bind(this);
        Intent intent = getIntent();
        try {
            mSelectVideoCount = intent.getIntExtra("selectVideoCount", 0);
            mFrom = intent.getIntExtra("from", 0);
            mSelectVideoTime = Double.valueOf(intent.getStringExtra("selectVideoTime"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        mLocalVideoFragment = new LocalVideoFragment();

        mLocalPhotoFragment = new LocalPhotoFragment();

        mCustomProgress = new CustomProgress(this);
        Bundle bundle = new Bundle();
        bundle.putInt("selectVideoCount", mSelectVideoCount);
        bundle.putDouble("selectVideoTime", mSelectVideoTime);
        mLocalVideoFragment.setArguments(bundle);
        initFragment(mLocalVideoFragment, mLocalPhotoFragment);
    }

    private void initFragment(Fragment showFragment, Fragment hideFragment) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        //判断fragment是否被添加过
        //判断隐藏的fragment是否被添加
        if (hideFragment.isAdded()) {
            fragmentTransaction.hide(hideFragment);
        }
        if (!showFragment.isAdded()) {
            fragmentTransaction.add(R.id.fl_activity_local_media_bottom_layout, showFragment).show(showFragment).commitAllowingStateLoss();
        } else {
            fragmentTransaction.show(showFragment).commitAllowingStateLoss();
        }
    }

    @OnClick({R.id.iv_activity_local_media_back, R.id.tv_activity_local_media_pic, R.id.tv_activity_local_media_video, R.id.tv_activity_local_media_ensure})
    void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_activity_local_media_video:
                //判断是否可以点击
                if (mLocalPhotoFragment != null && mLocalPhotoFragment.canChangeTab()) {
                    //让自己变成不可用，让另外一个变得可用
                    initFragment(mLocalVideoFragment, mLocalPhotoFragment);
                    changeTextEnable(false);
                }
                break;
            case R.id.tv_activity_local_media_pic:
                if (mLocalVideoFragment != null && mLocalVideoFragment.canChangeTab()) {
                    //让自己变成不可用，让另外一个变得可用
                    initFragment(mLocalPhotoFragment, mLocalVideoFragment);
                    changeTextEnable(true);
                }
                break;
            case R.id.iv_activity_local_media_back:
                finish();
                break;
            case R.id.tv_activity_local_media_ensure:
                ensureToRecord();
                break;
        }
    }

    private void ensureToRecord() {
        //转换视频然后跳转到拍摄界面
        //判断哪个有选择
        if (!mLocalVideoFragment.canChangeTab()) {
            //获取数据跳转到录制
            int[] i = new int[1];
            ArrayList<MovieItemModel> movieItemList = mLocalVideoFragment.getMovieItemList();
            ArrayList<MovieItemModel> copyMovieItemList = new ArrayList<>();
            //遍历查看视频是否横屏
            mCustomProgress.show(getString(R.string.loading), false, null);
            rotateVideo(movieItemList, i, copyMovieItemList);
        } else {
            // 处理照片逻辑
            ArrayList<String> imgs = (ArrayList<String>) mLocalPhotoFragment.getSelectedImgs();
            producePic(imgs);
        }
    }

    @SuppressLint("CheckResult")
    private void producePic(ArrayList<String> imags) {
        mCustomProgress.show(getString(R.string.loading), false, null);
        Observable.create((ObservableOnSubscribe<File>) e -> {
            ImageBean imageBean = new ImageBean();
            String videoFile = FileUtill.createSaveFile(LocalMediaActivity.this,FileUtill.getDefaultDir(LocalMediaActivity.this)+"/img");
            final String mererVideoPath = videoFile + "/" + String.format("img-output-%s.mp4", System.currentTimeMillis() + "");
            File file = new File(mererVideoPath);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            imageBean.setOutputPath(file.getAbsolutePath());
            imageBean.setImagePathList(imags);
            imageBean.setOutputType(NORMAL);
            try {
                FFmpegRun.setImageToNativeForEncodeMP4(imageBean);
            }catch (Exception e1){

            }finally {
                e.onNext(file);
            }


        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    String videoClipFile = FileUtill.createSaveFile(LocalMediaActivity.this,FileUtill.getDefaultDir(LocalMediaActivity.this)+"/clip");
                    if (mCustomProgress != null){
                        mCustomProgress.dismiss();
                    }
                    MovieItemDbDao movieItemDao = GreenDaoManager.getInstance().getMovieItemDao();
                    ArrayList<MovieItemDb> dbList = new ArrayList<>();
                    ArrayList<MovieItemModel> list = new ArrayList<>();
                    MovieItemDb movieItemDb = new MovieItemDb();
                    movieItemDb.setKey(System.currentTimeMillis());
                    movieItemDb.setFilPicPath(imags.get(0));
                    movieItemDb.setFilePath(o.getAbsolutePath());
                    movieItemDb.setVideoTime(Double.parseDouble(IvyUtils.getMediaDuration(o.getAbsolutePath()))/1000);
                    movieItemDb.setUid(UserInfoManager.getUid()+"");
                    movieItemDb.setVideo_from(true);
                    movieItemDb.setPicToMovie(true);
                    movieItemDb.setFrom(VIDEO_EDIT);
                    StringBuilder imgCache = new StringBuilder();
                    for (int i = 0;i<imags.size();i++){
                        imgCache.append(imags.get(i));
                        if (i != imags.size()-1){
                            imgCache.append(",");
                        }
                    }
                    movieItemDb.setPicList(imgCache.toString());
                    dbList.add(movieItemDb);


                    MovieItemModel movieItemModel = new MovieItemModel(o.getAbsolutePath(),BigDecimal.valueOf(Double.parseDouble(IvyUtils.getMediaDuration(o.getAbsolutePath()))/1000),imags.get(0));
                    list.add(movieItemModel);
                    Intent intent = new Intent(this, VideoEditActivity.class);
                    intent.putExtra("from", LOCAL);
                    intent.putExtra("pic",true);
                    intent.putExtra("edit_model", new MovieEditModel(videoClipFile, list));
                    intent.putExtra("record_model",dbList);
                    movieItemDao.insertInTx(dbList);
                    startActivity(intent);
                }, throwable -> {
                    if (mCustomProgress != null){
                        mCustomProgress.dismiss();
                    }
                });

    }

    private boolean getTotalMovieTime(ArrayList<MovieItemModel> movieItemList) {
        BigDecimal bigDecimal = BigDecimal.ZERO;
        for (MovieItemModel movieItemModel:movieItemList){
            bigDecimal = bigDecimal.add(movieItemModel.getVideoTime());
        }
        return bigDecimal.compareTo(BigDecimal.valueOf(180 - mSelectVideoTime))>0;
    }

    private void gotoRecord(ArrayList<MovieItemModel> movieItemList) {
        mFFmpegUtils = null;
        //完成了就跳转到拍摄界面
        //判断时间是不是大于180秒并且只有一个item
        Intent intent;
        if (getTotalMovieTime(movieItemList)){
            intent = new Intent(this,PreviewMovieActivity.class);
            String videoFile = FileUtill.createSaveFile(this,FileUtill.getDefaultDir(this)+"/clip");
            intent.putExtra("fromAct",PreviewMovieActivity.LOCAL);
            intent.putExtra("movie_model_list",new MovieClipModel(videoFile,movieItemList));
            startActivity(intent);
        }else{
            intent = new Intent(this, RecordActivity.class);
            if (mFrom == RecordActivity.CACHE){
                intent.putExtra("from",RecordActivity.CACHE);
            }else {
                intent.putExtra("from", LOCAL);
            }
            intent.putExtra("data", movieItemList);
            intent.putExtra("extra", mFileName);
            setResult(LOCAL_CODE,intent);
            finish();
        }
        if (mCustomProgress != null && mCustomProgress.isShowing()) {
            mCustomProgress.dismiss();
        }

    }

    private void rotateVideo(ArrayList<MovieItemModel> movieItemModels, final int[] i, ArrayList<MovieItemModel> copyMovieItemList) {
        MovieItemModel movieItemModel = movieItemModels.get(i[0]);
        //判断文件是否存在
        if (new File(movieItemModel.getFilePath()).exists()) {
            if (!isLand(movieItemModel.getFilePath())) {
                //获取旋转的视频
                if (mRotateInt == 0) {
                    createFFMPEG().rotateVideo(movieItemModel.getFilePath(), 180, outputFilePath -> {
                        //添加到复制的集合中
                        MovieItemModel model = new MovieItemModel();
                        model.setRotate(180);
                        model.setFilePath(outputFilePath);
                        model.setFilPicPath(movieItemModel.getFilPicPath());
                        model.setVideoTime(movieItemModel.getVideoTime());
                        model.setVideo_from(true);
                        copyMovieItemList.add(model);
                        if (i[0] == movieItemModels.size() - 1) {
                            //跳转到录制界面
                            gotoRecord(copyMovieItemList);
                        } else {
                            i[0] += 1;
                            rotateVideo(movieItemModels, i, copyMovieItemList);
                        }
                    });
                } else {
                    createFFMPEG().rotateVideo(movieItemModel.getFilePath(), 360 - mRotateInt, outputFilePath -> {
                        //删除第一个输出文件
                        MovieItemModel model = new MovieItemModel();
                        model.setRotate(360 - mRotateInt);
                        model.setFilePath(outputFilePath);
                        model.setFilPicPath(movieItemModel.getFilPicPath());
                        model.setVideoTime(movieItemModel.getVideoTime());
                        model.setVideo_from(true);
                        copyMovieItemList.add(model);
                        if (i[0] == movieItemModels.size() - 1) {
                            //跳转到录制界面
                            gotoRecord(copyMovieItemList);
                        } else {
                            i[0] += 1;
                            rotateVideo(movieItemModels, i, copyMovieItemList);
                        }
                    });
                }
            } else {
                copyMovieItemList.add(movieItemModel);
                if (i[0] == movieItemModels.size() - 1) {
                    //跳转到录制界面
                    gotoRecord(copyMovieItemList);
                } else {
                    i[0] += 1;
                    rotateVideo(movieItemModels, i, copyMovieItemList);
                }
            }
        } else {
            movieItemModels.remove(movieItemModel);
        }
    }

    private FFmpegUtils createFFMPEG() {
        if (mFFmpegUtils == null) {
            mFileName = FileUtill.createSaveFile(this, MediaConfig.VIDEO_XH_RECORDER) + VIDEO_ROTATE_FILE;
            mFileName = mFileName.replace("//", "/");
            mFFmpegUtils = new FFmpegUtils(mFileName);
        }
        return mFFmpegUtils;
    }

    public boolean isLand(String videoPath) {
        if (!TextUtils.isEmpty(videoPath) && new File(videoPath).exists()) {
            int widthInt;
            int heightInt;
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            //判断手机版本
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                try {
                    Uri contentUri = FileProvider.getUriForFile(this.getApplicationContext(), BuildConfig.APPLICATION_ID + ".fileprovider", new File(videoPath));
                    mmr.setDataSource(this, contentUri);
                }  catch ( Exception e) {
                    e.printStackTrace();
                }
            } else {
                mmr.setDataSource(videoPath);
            }
            String width = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);//宽
            String height = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);//高
            String rotation = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
            //判断如果是横屏的
            Log.e("获取到的视频的角度为===", rotation + ",,宽width=="+ width + ",,高height===" + height );
            try {
                mRotateInt = rotation == null ? 0 : Integer.parseInt(rotation);
                if (mRotateInt == 90 || mRotateInt == 270) {
                    widthInt = Integer.parseInt(height);
                    heightInt = Integer.parseInt(width);
                } else {
                    widthInt = Integer.parseInt(width);
                    heightInt = Integer.parseInt(height);
                }

                if (mRotateInt == 0 || mRotateInt == 180) {
                    if (widthInt > heightInt) {
                        return true;
                    }
                } else if (mRotateInt == 90 || mRotateInt == 270) {
                    if (widthInt > heightInt) {
                        return true;
                    }
                } else {
                    return false;
                }
            } catch (Exception e) {
                return false;
            } finally {
                mmr.release();
                mmr = null;
            }
        }
        return false;
    }

    public void changeEnsureBtn(boolean canEnsure) {
        mEnsureView.setEnabled(canEnsure);
    }


    private void changeTextEnable(boolean isEnable) {
        mVideoText.setEnabled(isEnable);
        mPicText.setEnabled(!isEnable);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBind != null) {
            mBind.unbind();
        }
    }

    @Subscribe(code = RxBusCode.ACT_VIDEO_PUSH_REFRESH_CACHE, threadMode = ThreadMode.MAIN)
    public void actCache() {
        finish();
    }

    @Subscribe(code = RxBusCode.ACT_VIDEO_PRE_REFRESH_CLIP, threadMode = ThreadMode.MAIN)
    public void actClip(Intent intent) {
        //删除当前的文件并关闭界面
        finish();
    }
}
