package com.xianghe.ivy.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.widget.Toast;

import com.xianghe.ivy.R;
import com.xianghe.ivy.ui.module.videocall.service.ScreenRecordService;

import java.util.ArrayList;
import java.util.List;

/**
 * 屏幕录制工具类
 */
public class ScreenRecordUtil {


    private static ScreenRecordService s_ScreenRecordService;

    private static List<RecordListener> s_RecordListener = new ArrayList<>();

    private static List<OnPageRecordListener> s_PageRecordListener = new ArrayList<>();

    //true,录制结束的提示语正在显示
    public static boolean s_IsRecordingTipShowing = false;

    /**
     * 录屏功能 5.0+ 的手机才能使用
     * @return
     */
    public static boolean isScreenRecordEnable(){

        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ;

    }


    public static void setScreenService(ScreenRecordService screenService){
        s_ScreenRecordService = screenService;
    }

    public static void clear(){
        if ( isScreenRecordEnable() && s_ScreenRecordService != null){
            s_ScreenRecordService.clearAll();
            s_ScreenRecordService = null;

        }

        if (s_RecordListener != null && s_RecordListener.size() > 0){
            s_RecordListener.clear();
        }

        if (s_PageRecordListener != null && s_PageRecordListener.size() > 0 ){
            s_PageRecordListener.clear();
        }
    }

    /**
     * 开始录制
     * @param activity
     * @param requestCode
     * @param recordTotalTime 已录制总时长
     * @return true-开始录制，false-录制已到最长时间
     */
    public static boolean startScreenRecord(Activity activity, int requestCode, double recordTotalTime) {

        if (recordTotalTime >=180){
            return false;
        }
        if (isScreenRecordEnable()){

            if (s_ScreenRecordService != null && !s_ScreenRecordService.ismIsRunning()){

                if (!s_ScreenRecordService.isReady()){

                    MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) activity.
                            getSystemService(Context.MEDIA_PROJECTION_SERVICE);
                    if (mediaProjectionManager != null){
                        Intent intent = mediaProjectionManager.createScreenCaptureIntent();
                        PackageManager packageManager = activity.getPackageManager();
                        if (packageManager.resolveActivity(intent,PackageManager.MATCH_DEFAULT_ONLY) != null){
                            //存在录屏授权的Activity
                            activity.startActivityForResult(intent,requestCode);
                        }else {
                            Toast.makeText(activity,R.string.can_not_record_tip,Toast.LENGTH_SHORT).show();
                        }
                    }

                } else {
                    s_ScreenRecordService.startRecord(recordTotalTime);

                }

            }
        }
        return true;

    }

    /**
     * 获取用户允许录屏后，设置必要的数据
     * @param resultCode
     * @param resultData
     */
    public static void setUpData(int resultCode,Intent resultData,double recordTotalTime) throws Exception{

        if (isScreenRecordEnable()){

            if (s_ScreenRecordService != null && !s_ScreenRecordService.ismIsRunning()){
                s_ScreenRecordService.setResultData(resultCode,resultData);
                s_ScreenRecordService.startRecord(recordTotalTime);

            }

        }
    }

    /**
     * 停止录制
     */
    public static void stopScreenRecord(Context context,boolean isSaveRecordFile){
        if (isScreenRecordEnable()){
            if (s_ScreenRecordService != null && s_ScreenRecordService.ismIsRunning()){
                String str = context.getString(R.string.record_video_tip);
                s_ScreenRecordService.stopRecord(str,ScreenRecordService.STATUS_NORMAL,isSaveRecordFile);
            }
        }
    }

    /**
     * 获取录制后的文件地址
     * @return
     */
    public static String getScreenRecordFilePath(){

        if (isScreenRecordEnable() && s_ScreenRecordService!= null) {
            return s_ScreenRecordService.getRecordFilePath();
        }
        return null;

    }

    /**
     * 判断当前是否在录制
     * @return
     */
    public static boolean isCurrentRecording(){
        if (isScreenRecordEnable() && s_ScreenRecordService!= null) {
            return s_ScreenRecordService.ismIsRunning();
        }
        return false;
    }

    /**
     * true,录制结束的提示语正在显示
     * @return
     */
    public static boolean isRecodingTipShow(){
        return s_IsRecordingTipShowing;
    }

    public static void setRecordingStatus(boolean isShow){
        s_IsRecordingTipShowing = isShow;
    }


    /**
     * 系统正在录屏，app 录屏会有冲突，清理掉一些数据
     */
    public static void clearRecordElement(){

        if (isScreenRecordEnable()){
            if (s_ScreenRecordService != null ){
                s_ScreenRecordService.clearRecordElement();
            }
        }
    }

    public static void addRecordListener(RecordListener listener){

        if (listener != null && !s_RecordListener.contains(listener)){
            s_RecordListener.add(listener);
        }

    }

    public static void removeRecordListener(RecordListener listener){
        if (listener != null && s_RecordListener.contains(listener)){
            s_RecordListener.remove(listener);
        }
    }

    public static void addPageRecordListener( OnPageRecordListener listener){

        if (listener != null && !s_PageRecordListener.contains(listener)){
            s_PageRecordListener.add(listener);
        }
    }

    public static void removePageRecordListener( OnPageRecordListener listener){

        if (listener != null && s_PageRecordListener.contains(listener)){
            s_PageRecordListener.remove(listener);
        }
    }

    public static void onPageRecordStart(){
        if (s_PageRecordListener!= null && s_PageRecordListener.size() > 0 ){
            for (OnPageRecordListener listener : s_PageRecordListener){
                listener.onStartRecord();
            }
        }
    }


    public static void onPageRecordStop(){
        if (s_PageRecordListener!= null && s_PageRecordListener.size() > 0 ){
            for (OnPageRecordListener listener : s_PageRecordListener){
                listener.onStopRecord();
            }
        }
    }

    public static void onPageBeforeShowAnim(){
        if (s_PageRecordListener!= null && s_PageRecordListener.size() > 0 ){
            for (OnPageRecordListener listener : s_PageRecordListener){
                listener.onBeforeShowAnim();
            }
        }
    }

    public static void onPageAfterHideAnim(){
        if (s_PageRecordListener!= null && s_PageRecordListener.size() > 0 ){
            for (OnPageRecordListener listener : s_PageRecordListener){
                listener.onAfterHideAnim();
            }
        }
    }

    public static void startRecord(String path){
        if (s_RecordListener.size() > 0 ){
            for (RecordListener listener : s_RecordListener){
                listener.onStartRecord(path);
            }
        }
    }

    public static void pauseRecord(){
        if (s_RecordListener.size() > 0 ){
            for (RecordListener listener : s_RecordListener){
                listener.onPauseRecord();
            }
        }
    }

    public static void resumeRecord(){
        if (s_RecordListener.size() > 0 ){
            for (RecordListener listener : s_RecordListener){
                listener.onResumeRecord();
            }
        }
    }

    public static void onRecording(String timeTip){
        if (s_RecordListener.size() > 0 ){
            for (RecordListener listener : s_RecordListener){
                listener.onRecording(timeTip);
            }
        }
    }

    public static void stopRecord(String stopTip, int status, boolean isSaveRecordFile){
        if (s_RecordListener.size() > 0 ){
            for (RecordListener listener : s_RecordListener){
                listener.onStopRecord( stopTip,status,isSaveRecordFile);
            }
        }
    }

    public interface RecordListener{


        void onStartRecord(String path);
        void onPauseRecord();
        void onResumeRecord();
        void onStopRecord(String stopTip,int status,boolean isSaveRecordFile);
        void onRecording(String timeTip);
    }


    public interface OnPageRecordListener {

        void onStartRecord();
        void onStopRecord();

        void onBeforeShowAnim();
        void onAfterHideAnim();
    }
}
