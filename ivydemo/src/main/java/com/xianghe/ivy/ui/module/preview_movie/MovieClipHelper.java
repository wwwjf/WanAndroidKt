package com.xianghe.ivy.ui.module.preview_movie;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.xianghe.ivy.BuildConfig;
import com.xianghe.ivy.R;
import com.xianghe.ivy.utils.FileUtill;
import com.xianghe.ivy.weight.CustomProgress;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.xianghe.ivy.ui.media.config.MediaConfig.VIDEO_CLIP_LIST_IMAGE;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

public class MovieClipHelper {

    private Context mContext;

    private int mCount;

    private long mTotalTime;

    private String mDefaultPicPath;

    private MovieClipResultListener mMovieClipResultListener;

    private MovieInfoListener mMovieInfoListener;

    private File mMediaStorageDir;

    private CustomProgress mCustomProgress;

    private String mClipTag = VIDEO_CLIP_LIST_IMAGE;

    private ArrayList<String> mFileList;

    private Disposable mDisposable;

    private MovieClipHelper(Context context, String saveFileName, int position, CustomProgress customProgress, long totalTime, String videoPath, String defaultPicPath, int count, MovieClipResultListener listener) {
        mContext = context;
        mTotalTime = totalTime;
        mDefaultPicPath = defaultPicPath;
        mCustomProgress = customProgress;
        mFileList = new ArrayList<>();
        mCount = count;
        mMovieClipResultListener = listener;
        if (TextUtils.isEmpty(saveFileName)) {
            mMediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), mClipTag + position);
        } else {
            mMediaStorageDir = new File(saveFileName, mClipTag + position);
        }

        getFrameBitmap(videoPath);
    }

    private MovieClipHelper(Context context, String saveFileName, int position, CustomProgress customProgress, long totalTime, String videoPath, String defaultPicPath, int count, MovieClipResultListener listener, MovieInfoListener infoListener) {
        mContext = context;
        mTotalTime = totalTime;
        mDefaultPicPath = defaultPicPath;
        mCustomProgress = customProgress;
        mFileList = new ArrayList<>();
        mCount = count;
        mMovieClipResultListener = listener;
        mMovieInfoListener = infoListener;
        if (TextUtils.isEmpty(saveFileName)) {
            mMediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), mClipTag + position);
        } else {
            mMediaStorageDir = new File(saveFileName, mClipTag + position);
        }

        getFrameBitmap(videoPath);
    }

    private File getOutputMediaFile(long position) {
        if (!mMediaStorageDir.exists()) {
            if (!mMediaStorageDir.mkdirs()) {
                return null;
            }
        }
        return new File(mMediaStorageDir, "IMG_" + position + ".jpg");
    }

    SimpleDateFormat sDateFormat = new SimpleDateFormat("yy:MM:dd HH:mm:ss");

    private void extractFrame(MediaMetadataRetriever metadataRetriever, long time) {
        Log.e("编辑视频前的系统时间time前==", time + "===" + sDateFormat.format(new Date(System.currentTimeMillis())));
//        Bitmap bitmap = metadataRetriever.getFrameAtTime((time * 1000 + 1L), MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
        Bitmap bitmap = metadataRetriever.getFrameAtTime(time * 1000 * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);

//        metadataRetriever.getFrameAtIndex()

        File videoFile = getOutputMediaFile(time);
        if (bitmap != null) {
            try {
                if (videoFile != null) {

                    if (videoFile.exists()) {
                        videoFile.delete();
                    }
                    FileOutputStream out = new FileOutputStream(videoFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 60, out);
                    out.flush();
                    out.close();
                }
            } catch (Exception e) {
                Log.d("MovieClipHelper", "e:" + e);
            } finally {
                if (!bitmap.isRecycled()) {
                    bitmap.recycle();
                    bitmap = null;
                }

                //如果文件不为空就添加到集合中
                if (videoFile != null) {
                    mFileList.add(videoFile.getAbsolutePath());
                }
                Log.e("编辑前的系统时间time后==", time + "=====" + sDateFormat.format(new Date(System.currentTimeMillis())));
            }
        }
    }

    public void onDestroy() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }

        if (mCustomProgress != null && mCustomProgress.isShowing()) {
            mCustomProgress.dismiss();
        }
    }

    @SuppressLint("CheckResult")
    private void getFrameBitmap(@NonNull String videoPath) {
        MediaMetadataRetriever mmr = getMediaRetriever(videoPath);
        if (mmr == null) return;
        String width = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);//宽
        String height = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);//高
        String rotation = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
        Log.e("编辑视频裁剪time===", mTotalTime + "====mCount===" + mCount);
        if ((mTotalTime > 0 && mCount > 0)) {
//            if ((mTotalTime > 0)) {
            Observable.create(e -> {
                //
                //判断是否有20s，获取的总时长不太准确
                //判断是否需要截取图片
                for (int i = 0; i < mTotalTime; i++) {
                    if (i == mCount) {
                        break;
                    } else {
                        extractFrame(mmr, i);
                    }
//                        extractFrame(mmr, i);
                }
                addPicToList();
                e.onNext(mFileList);
            })
                    .subscribeOn(Schedulers.io())
                    .doOnSubscribe(subscription -> {
                        if (mCustomProgress != null) {
                            mCustomProgress.show(mContext.getString(R.string.preview_cliping_movie), false, null);
                        }
                    })
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Object>() {


                        @Override
                        public void onSubscribe(Disposable d) {
                            mDisposable = d;
                        }

                        @SuppressWarnings("unchecked")
                        @Override
                        public void onNext(Object o) {
                            onSubNext((ArrayList<String>) o, mmr, width, height, rotation);
                        }

                        @Override
                        public void onError(Throwable e) {
                            onSubError(mmr);
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            //必须文件存在
            //判断默认图片是否存在
            if (TextUtils.isEmpty(mDefaultPicPath) || !new File(mDefaultPicPath).exists()) {
                Observable.create(e -> {
                    extractFrame(mmr, 0);
                    addPicToList();
                    e.onNext(mFileList);
                })
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe(subscription -> {
                            if (mCustomProgress != null) {
                                mCustomProgress.show(mContext.getString(R.string.preview_cliping_movie), false, null);
                            }
                        })
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Object>() {

                            @Override
                            public void onSubscribe(Disposable d) {
                                mDisposable = d;
                            }

                            @SuppressWarnings("unchecked")
                            @Override
                            public void onNext(Object o) {
                                onSubNext((ArrayList<String>) o, mmr, width, height, rotation);
                            }

                            @Override
                            public void onError(Throwable e) {
                                onSubError(mmr);
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            } else {
                addPicToList();
                if (mMovieClipResultListener != null) {
                    mMovieClipResultListener.onClipResult(mFileList, mMediaStorageDir.getAbsolutePath(), true, mDisposable);
                }
            }
        }
    }

    private MediaMetadataRetriever getMediaRetriever(String videoPath) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri contentUri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".fileprovider", new File(videoPath));
            if (contentUri != null)
                mmr.setDataSource(mContext, contentUri);
            else
                return null;
        } else {
            mmr.setDataSource(videoPath);
        }
        return mmr;
    }

    private void onSubNext(ArrayList<String> o, MediaMetadataRetriever mmr, String width, String height, String oritation) {
        if (mCustomProgress != null && mCustomProgress.isShowing()) {
            mCustomProgress.dismiss();
        }
        mmr.release();
        if (mMovieClipResultListener != null) {
            mMovieClipResultListener.onClipResult(o, mMediaStorageDir.getAbsolutePath(), false, mDisposable);
        }

        if (mMovieInfoListener != null) {
            mMovieInfoListener.onMovieInfo(width, height, oritation);
        }
    }

    private void onSubError(MediaMetadataRetriever mmr) {
        if (mCustomProgress != null && mCustomProgress.isShowing()) {
            mCustomProgress.dismiss();
        }
        mmr.release();
        //删除所有文件
        FileUtill.deleteFile(mMediaStorageDir);
        addPicToList();
        if (mMovieClipResultListener != null) {
            mMovieClipResultListener.onClipResult(mFileList, mMediaStorageDir.getAbsolutePath(), false, mDisposable);
        }
    }

    private void addPicToList() {
        int size = mFileList.size();
        //完成之后判断集合数量
        if (TextUtils.isEmpty(mDefaultPicPath) || !new File(mDefaultPicPath).exists()) {
            for (int j = mCount; j < 20; j++) {
                if (size != 0) {
                    mFileList.add(mFileList.get(0));
                } else {
                    mFileList.add(mDefaultPicPath);
                }
            }
        } else {
//            if (size > 0){
//                if (size >= 20){
//                    for (int j = 0; j <20; j++) {
//                        mFileList.add(mFileList.get(j));
//                    }
//                }else {
//                    for (int j = 0; j <20; j++) {
//                        if (j == size){
//                            mFileList.add(mFileList.get(size));
//                        }else {
//                            mFileList.add(mFileList.get(j));
//                        }
//                    }
//                }
//
//            }else {
//                for (int j = mCount;j<20;j++){
//                    mFileList.add(mDefaultPicPath);
//                }
//            }
            if (size == 0) {
                for (int j = mCount; j < 20; j++) {
                    mFileList.add(mDefaultPicPath);
                }
            } else {
                for (int j = size; j < 20; j++) {
                    mFileList.add(mFileList.get(0));
                }
            }
        }

        Log.e("编辑视频裁剪mFileList===", mFileList.size() + "====mCount===" + mCount);
    }

    public static class Builder {
        private Context mContext;

        private CustomProgress mCustomProgress;

        private String mVideoPath;

        private int mCount;

        private long mTotalTime;

        private int mPosition;

        private String mDefaultPicPath;

        private String mDirName;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder openProgressStyle() {
            mCustomProgress = new CustomProgress(mContext);
            return this;
        }

        public Builder setSaveFileDir(String dirName) {
            mDirName = dirName;
            return this;
        }

        public Builder setVideoPath(String path) {
            mVideoPath = path;
            return this;
        }

        public Builder setDefaultPicPath(String defaultPicPath) {
            mDefaultPicPath = defaultPicPath;
            return this;
        }

        public Builder setTotalTime(long totalTime) {
            mTotalTime = totalTime;
            return this;
        }

        public Builder setPicCount(int count) {
            mCount = count;
            return this;
        }

        public Builder setFilePosition(int position) {
            mPosition = position;
            return this;
        }

        public MovieClipHelper Build(MovieClipResultListener listener) {
            return new MovieClipHelper(mContext, mDirName, mPosition, mCustomProgress, mTotalTime, mVideoPath, mDefaultPicPath, mCount, listener);
        }

        public MovieClipHelper Build(MovieClipResultListener listener, MovieInfoListener infoListener) {
            return new MovieClipHelper(mContext, mDirName, mPosition, mCustomProgress, mTotalTime, mVideoPath, mDefaultPicPath, mCount, listener, infoListener);
        }
    }

    public interface MovieClipResultListener {
        void onClipResult(ArrayList<String> pathList, String fileDir, boolean isDefault, Disposable disposable);
    }

    public interface MovieInfoListener {
        void onMovieInfo(String width, String height, String oritation);
    }
}
