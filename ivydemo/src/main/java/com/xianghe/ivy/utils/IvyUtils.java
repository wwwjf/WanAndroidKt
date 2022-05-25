package com.xianghe.ivy.utils;

import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.text.TextUtils;

import androidx.core.content.FileProvider;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.StringUtils;
import com.xianghe.ivy.BuildConfig;
import com.xianghe.ivy.R;
import com.xianghe.ivy.app.IvyApp;
import com.xianghe.ivy.manager.UserInfoManager;
import com.xianghe.ivy.model.UserBean;
import com.xianghe.ivy.ui.media.config.MediaConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * 工具类
 */
public class IvyUtils {
    public static final String TAG = IvyUtils.class.getSimpleName();

    /**
     * 获取系统信息作为user-agent
     *
     * @return
     */
    public static String getClientSystem() {

        String property = System.getProperty("http.agent");
        String appInfo = AppUtils.getAppName() + "/" + AppUtils.getAppVersionName();
        return property + " " + appInfo;
    }

    /**
     * 根据请求参数生成签名
     *
     * @return
     */
    public static String getSign(String paramString) {
//        type=hot&ClientSource=1&ClientSystem=Dalvik/2.1.0 (Linux; U; Android 5.0; Samsung Galaxy S6 - 5.0.0 - API 21 - 1440x2560 Build/LRX21M) i微影/1.0&Version=1.0
//        type=new&ClientSource=1&ClientSystem=Dalvik/2.1.0 (Linux; U; Android 8.0.0; ONEPLUS A3010 Build/OPR1.170623.032) i微影/1.0&Version=1.0
//        cb1a826dedbdb54fa943a5e6f077bb43
        KLog.i("LoadParamsInterceptor", "getSign: " + paramString);
        String s = md5(paramString);
        KLog.i("LoadParamsInterceptor", "getSign: " + s);

        return md5(paramString);
    }

    /**
     * @param string
     * @return
     */
    public static String md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("md5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * @param context
     * @param platform
     */
    public static void shareApp(Context context, String platform) {
        //String url = context.getResources().getString(R.string.download_url);

    }

    public static String platform2Channel(String platform) {
        return  "";
    }

    /**
     * 根据content 获取真实路径
     * Gets the content:// URI from the given corresponding path to a file
     *
     * @param context
     * @param imageFile
     * @return content Uri
     */
    public static Uri getVideoContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/video/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    /**
     * Gets the corresponding path to a file from the given content:// URI
     *
     * @param selectedVideoUri The content:// URI to find the file path from
     * @param contentResolver  The content resolver to use to perform the query.
     * @return the file path as a string
     */
    public static String getFilePathFromContentUri(Uri selectedVideoUri,
                                                   ContentResolver contentResolver) {
        String filePath;
        String[] filePathColumn = {MediaStore.MediaColumns.DATA};

        Cursor cursor = contentResolver.query(selectedVideoUri, filePathColumn, null, null, null);

        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }

    /**
     * 发送邀请短信
     *
     * @param context
     * @param phoneNumber
     * @param message
     */
    public static void sendInviteSms(Context context, String phoneNumber, String message) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phoneNumber));
        intent.putExtra("sms_body", message);
        context.startActivity(intent);
    }

    public static void sendInviteSmsDirectly(Context context, String phoneNum, String message, PendingIntent sentIntent) {
        SmsManager smsManager = SmsManager.getDefault();
        ArrayList<String> list = smsManager.divideMessage(message);
        for (int i = 0; i < list.size(); i++) {
            try {
                smsManager.sendTextMessage(phoneNum, null, list.get(i), sentIntent, null);
            }catch (Exception e){//有可能有权限问题
                break;
            }
        }
    }

    /**
     * 去除URL中多余的斜杆
     *
     * @param url
     * @return
     */
    public static String removeExtraSlashOfUrl(String url) {
        return url.replaceAll("(?<!(http:|https:))/+", "/");
    }

    /**
     * 获取视频某一帧图片
     *
     * @param videoPath
     * @param frameAt
     * @return
     */
    public static String getVideoFrame(Context context, String videoPath, int frameAt) {
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri contentUri = FileProvider.getUriForFile(context,
                        BuildConfig.APPLICATION_ID + ".fileprovider", new File(videoPath));
                media.setDataSource(context, contentUri);
            } else {
                media.setDataSource(videoPath);
            }
            Bitmap bitmap = media.getFrameAtTime(frameAt, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
            if (bitmap == null) {
                return null;
            }
            File file = FileUtils.createTempFile(MediaConfig.VIDEO_XH_RECORDER_SYNTH,
                    System.currentTimeMillis() + ".jpg");
            FileOutputStream outStream = null;
            try {
                outStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outStream);
                outStream.flush();
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                    bitmap = null;
                }
                if (outStream!=null) {
                    outStream = null;
                }
            }
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            media.release();
            media = null;
        }
    }

    public static String getMediaHasAudio(String path) {
        String hasAudio;
        MediaMetadataRetriever mmr = null;
        try {
            mmr = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri contentUri = FileProvider.getUriForFile(IvyApp.getInstance().getApplicationContext(),
                        BuildConfig.APPLICATION_ID + ".fileprovider", new File(path));
                mmr.setDataSource(IvyApp.getInstance().getApplicationContext(), contentUri);
            } else {
                mmr.setDataSource(path);
            }
            mmr.setDataSource(path);
            hasAudio = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO);
        } catch (Exception e) {
            hasAudio = "";
        } finally {
            if (mmr != null) {
                mmr.release();
                mmr = null;
            }
        }
        return hasAudio;
    }

    public static boolean hasMusicTrack(String videoPath) throws IOException {
        MediaExtractor extractor = new MediaExtractor();
        extractor.setDataSource(videoPath);
        int count = extractor.getTrackCount();
        for (int i = 0; i < count; i++) {
            MediaFormat format = extractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith("audio")) {
                extractor.release();
                return true;
            }
        }
        extractor.release();
        return false;
    }

    /**
     * 获取视频或音频时长
     *
     * @param path 播放时长单位为毫秒
     * @return
     */
    public static String getMediaDuration(String path) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        String duration = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri contentUri = FileProvider.getUriForFile(IvyApp.getInstance().getApplicationContext(),
                        BuildConfig.APPLICATION_ID + ".fileprovider", new File(path));
                if (contentUri != null) {
                    mmr.setDataSource(IvyApp.getInstance().getApplicationContext(), contentUri);
                }
            } else {
                mmr.setDataSource(path);
            }
            duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mmr.release();
            mmr = null;
        }
        return duration;
    }

    public static boolean isOKVideo(String path) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        String hasVideo = null;
        String width = null;
        String height = null;
        try {
            //判断手机版本
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri contentUri = FileProvider.getUriForFile(IvyApp.getInstance().getApplicationContext(),
                        BuildConfig.APPLICATION_ID + ".fileprovider", new File(path));
                mmr.setDataSource(IvyApp.getInstance(), contentUri);
            } else {
                mmr.setDataSource(path);
            }
            hasVideo = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO);//有无视频
            width = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);//宽
            height = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);//高
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mmr.release();
            mmr = null;
        }
        return hasVideo != null && width != null && height != null;
    }

    public static int getVideoBitrate(String path) {
        if (path == null) {
            return 0;
        }
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        String BITRATE = null;
        try {
            //判断手机版本
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri contentUri = FileProvider.getUriForFile(IvyApp.getInstance().getApplicationContext(),
                        BuildConfig.APPLICATION_ID + ".fileprovider", new File(path));
                mmr.setDataSource(IvyApp.getInstance(), contentUri);
            } else {
                mmr.setDataSource(path);
            }
            BITRATE = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);//宽

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mmr.release();
            mmr = null;
        }
        return BITRATE == null ? 0 : Integer.parseInt(BITRATE);
    }


    public static int getMediaDuration2(String path) {
        int duration = 0;
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            duration = mediaPlayer.getDuration();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        return duration;
    }

    public static String[] getMediaWidthHeight(String uri) {
        String[] info = new String[2];
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try {
            if (uri != null) {
                HashMap<String, String> headers = null;
                if (headers == null) {
                    headers = new HashMap<String, String>();
                    headers.put("User-Agent", "Mozilla/5.0 (Linux; U; Android 4.4.2; zh-CN; MW-KW-001 Build/JRO03C) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 UCBrowser/1.0.0.001 U4/0.8.0 Mobile Safari/533.1");
                }
                mmr.setDataSource(uri, headers);
            } else {
                //mmr.setDataSource(mFD, mOffset, mLength);
            }

            String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);//时长(毫秒)
            String width = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);//宽
            String height = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);//高
            info[0] = width;
            info[1] = height;
        } catch (Exception ex) {
            KLog.e("TAG", "MediaMetadataRetriever exception " + ex);
        } finally {
            mmr.release();
        }
        return info;
    }

    /**
     * 获取视频宽高
     *
     * @param uri
     * @param listener
     */
    public static void getMediaWidthHeight(String uri, OnLoadVideoWidthHeightListener listener) {
        LoadVideoTask task = new LoadVideoTask(listener);
        task.execute(uri);
    }

    /**
     * 检测是否完善资料  [头像、昵称，生日，性别，个性签名]
     *
     * @param userBean 已登录的用户信息
     * @param uid      已登录的id
     * @return false-未完善资料，true-已完善资料或非第一次视频通话
     */
    public static boolean checkFinishUserInfo(UserBean userBean, long uid) {
        if (userBean == null) {
            return false;
        }

        //是否跳过个人信息检测
        boolean isIgnoreUserInfo = SPUtils.getInstance("UserInfoIsFinish")
                .getBoolean("isIgnore_" + UserInfoManager.getUid(), false);
        if (isIgnoreUserInfo) {
            return true;
        }

        //是否完善资料
        boolean userInfoIsFinish = SPUtils.getInstance("UserInfoIsFinish")
                .getBoolean(String.valueOf(UserInfoManager.getUid()), false);
        if (userInfoIsFinish) {
            return true;
        }

        //以uid作为key保存信息
        SPUtils.getInstance("UserInfoIsFinish").put(String.valueOf(UserInfoManager.getUid()), true);

        //头像
        if (StringUtils.isEmpty(userBean.getAvatar())) {
            return false;
        }

        //昵称
        if (StringUtils.isEmpty(userBean.getName())) {
            return false;
        }

        //生日
        if (StringUtils.isEmpty(userBean.getBirthday())) {
            return false;
        }

        //性别
        if (userBean.getSex() == -1) {
            return false;
        }

        //签名
        if (StringUtils.isEmpty(userBean.getSignature())) {
            return false;
        }

        return true;
    }

    public static class LoadVideoTask extends AsyncTask<String, Integer, String[]> {
        private OnLoadVideoWidthHeightListener listener;

        public LoadVideoTask(OnLoadVideoWidthHeightListener listener) {
            this.listener = listener;
        }

        @Override
        protected String[] doInBackground(String... params) {

            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            String path = params[0];
            try {
                if (path.startsWith("http")) {
                    //获取网络视频第一帧图片
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("User-Agent", IvyUtils.getClientSystem());
                    mmr.setDataSource(path, headers);
                } else
                    //本地视频
                    mmr.setDataSource(path);
                String width = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);//宽
                String height = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);//高
                String rotationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION); // 视频旋转方向
                String[] result = new String[3];
                result[0] = width;
                result[1] = height;
                result[2] = rotationStr;
                return result;
            } catch (RuntimeException e) {
                e.printStackTrace();
            } finally {
                mmr.release();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            super.onPostExecute(result);
            if (listener != null) {
                listener.getWidthHeight(result);
            }
        }
    }

    public interface OnLoadVideoWidthHeightListener {
        void getWidthHeight(String[] widthHeight);
    }

    /**
     * 比较版本号的大小,前者大则返回一个正数,后者大返回一个负数,相等则返回0
     *
     * @param version1
     * @param version2
     * @return
     */
    public static int compareVersion(String version1, String version2) {
//        if (version1 == null || version2 == null) {
//            throw new Exception("compareVersion error:illegal params.");
//        }
        String[] versionArray1 = version1.split("\\.");//注意此处为正则匹配，不能用"."；
        String[] versionArray2 = version2.split("\\.");
        int idx = 0;
        int minLength = Math.min(versionArray1.length, versionArray2.length);//取最小长度值
        int diff = 0;
        while (idx < minLength
                && (diff = versionArray1[idx].length() - versionArray2[idx].length()) == 0//先比较长度
                && (diff = versionArray1[idx].compareTo(versionArray2[idx])) == 0) {//再比较字符
            ++idx;
        }
        //如果已经分出大小，则直接返回，如果未分出大小，则再比较位数，有子版本的为大；
        diff = (diff != 0) ? diff : versionArray1.length - versionArray2.length;
        return diff;
    }

    public String savaBitmap(String imgName, byte[] bytes) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String filePath = null;
            FileOutputStream fos = null;
            try {
                filePath = Environment.getExternalStorageDirectory().getCanonicalPath() + "/MyImg";
                File imgDir = new File(filePath);
                if (!imgDir.exists()) {
                    imgDir.mkdirs();
                }
                imgName = filePath + "/" + imgName;
                fos = new FileOutputStream(imgName);
                fos.write(bytes);
                return filePath;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static boolean isLetter(char letter) {
        return letter >= 'A' && letter <= 'Z' || letter >= 'a' && letter <= 'z';
    }

    /**
     * 创建录屏文件保存目录
     * /storage/emulated/0/xh_recorder/videoCall_record
     */
    public static String createVideoCallRecordDirs() {
        //录屏文件保存路径
        String videoCallRecordPath = Environment.getExternalStorageDirectory().getAbsolutePath() + MediaConfig.VIDEO_XH_RECORDER + MediaConfig.VIDEO_CALL_RECORD;
        File videoCallRecordDir = new File(videoCallRecordPath);
        if (videoCallRecordDir.exists()) {
            return videoCallRecordPath;
        }
        KLog.e("----文件夹不存在，创建文件夹:" + videoCallRecordPath);
        if (videoCallRecordDir.mkdirs()) {
            return videoCallRecordPath;
        }
        KLog.e("----文件夹创建失败,返回根目录:" + videoCallRecordPath);
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

}
