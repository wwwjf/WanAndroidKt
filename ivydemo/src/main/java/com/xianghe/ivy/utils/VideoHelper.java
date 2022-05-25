package com.xianghe.ivy.utils;

import static com.xianghe.ivy.ui.media.config.MediaConfig.VIDEO_FRAME_IMAGE;
import static com.xianghe.ivy.ui.media.config.MediaConfig.VIDEO_LOCAL_IMAGE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import androidx.core.content.FileProvider;

import com.xianghe.ivy.BuildConfig;
import com.xianghe.ivy.ui.media.config.MediaConfig;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class VideoHelper {

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private static String mPicFileName = VIDEO_FRAME_IMAGE;

    private static String mMainPicFileName = VIDEO_LOCAL_IMAGE;

    private static String saveFile;

    public static File getOutputMediaFile(String fileName, int type) {
        File mediaStorageDir = new File(fileName,mPicFileName);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getAbsolutePath(),"IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getAbsolutePath(),"VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }
        return mediaFile;
    }

    public static File getOutputMediaFile(String file, int type, long position) {
        File mediaStorageDir = new File(file,mPicFileName);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getAbsolutePath(),"IMG_" + position + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getAbsolutePath(),"VID_" + position + ".mp4");
        } else {
            return null;
        }
        return mediaFile;
    }

    public static File getOutputMediaFile(Context context) {
        //判断本地的文件是否存在
        if (TextUtils.isEmpty(saveFile)){
            saveFile = createSaveFile(context);
        }
        File mediaStorageDir = new File(saveFile,mMainPicFileName);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        return new File(mediaStorageDir.getAbsolutePath(),"IMG_" + timeStamp + ".jpg");
    }

    private static String createSaveFile(Context context) {
        String fileName = MediaConfig.VIDEO_XH_RECORDER;
        //判断文件是否存在
        File file = new File(fileName);
        if (file.exists()){
            return file.getAbsolutePath();
        }else{
            String savePath;
            try {
                savePath = FileUtill.creatFile2SDCard(fileName);
            } catch (IOException e) {
                savePath = FileUtill.creatDir2SDCard(context.getFilesDir().getAbsolutePath() + fileName);
            }
            return savePath;
        }
    }

    /**
     * 获取视频的第一帧图片
     */
    public static Disposable getImageForVideoAsync(Context context, String filePath, String videoPath, OnLoadVideoImageListener listener, long position) {
        //判断图片是否已经存在了
        File outputMediaFile = getOutputMediaFile(filePath, MEDIA_TYPE_IMAGE, position);
        if (outputMediaFile != null && outputMediaFile.isFile() && outputMediaFile.exists()) {
            listener.onLoadImage(outputMediaFile);
        } else {
            LoadVideoImage task = new LoadVideoImage(listener, context, filePath);
            return task.loadImageView(videoPath);
        }
        return null;
    }


    private static class LoadVideoImage{

        private OnLoadVideoImageListener listener;

        @SuppressLint("StaticFieldLeak")
        private Context mContext;

        private String filePath;

        private LoadVideoImage(OnLoadVideoImageListener listener, Context context, String filePath){
            this.listener = listener;
            this.filePath = filePath;
            mContext = context;
        }

        @SuppressLint("CheckResult")
        private Disposable loadImageView(String imagePath){
            //判断手机版本
            //保存图片
            //图片压缩
           return Observable.create((ObservableOnSubscribe<File>) e -> {
               MediaMetadataRetriever mmr = new MediaMetadataRetriever();
               String path = imagePath;
               //判断手机版本
               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                   Uri contentUri = FileProvider.getUriForFile(mContext.getApplicationContext(), BuildConfig.APPLICATION_ID + ".fileprovider", new File(path));
                   mmr.setDataSource(mContext, contentUri);
               } else {
                   mmr.setDataSource(path);
               }
               Bitmap bitmap = mmr.getFrameAtTime(); //保存图片
               File f = getOutputMediaFile(filePath, MEDIA_TYPE_IMAGE);
               try {
                   if (f != null) {
                       if (f.exists()) {
                           f.delete();
                       }
                       //图片压缩
                       BitmapUtil.saveImageToFile(bitmap, f);
                   }
               } finally {
                   if (bitmap != null && !bitmap.isRecycled()) {
                       bitmap.recycle();
                   }
                   mmr.release();
               }
               e.onNext(f);
           }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(file -> {
                        if (listener != null) {
                            listener.onLoadImage(file);
                        }
                    });
        }
    }

    public interface OnLoadVideoImageListener {
        void onLoadImage(File file);
    }
}
