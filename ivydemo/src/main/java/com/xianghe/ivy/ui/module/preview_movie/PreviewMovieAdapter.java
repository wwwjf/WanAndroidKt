package com.xianghe.ivy.ui.module.preview_movie;

import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.xianghe.ivy.R;
import com.xianghe.ivy.adapter.adapter.BaseQuickAdapter;
import com.xianghe.ivy.adapter.adapter.BaseViewHolder;
import com.xianghe.ivy.ui.module.record.model.MovieItemModel;

import java.math.BigDecimal;
import java.util.List;


public class PreviewMovieAdapter extends BaseQuickAdapter<MovieItemModel,BaseViewHolder> implements ViewPagerLinearLayoutManager.OnViewPagerListener {
    private PreviewMovieActivity mActivity;

    private int videoHeight;

    private int height;

    private int width;

    private int mReleasePosition = -1;

    private int mSelectedPosition = -1;

    private int currentPosition;                                                                    //暂停的时间

    private int circlePosition = 0;                                                                 //播放完毕从哪里开始

    private TimeRecordTask mTimeRecordTask;

    PreviewMovieAdapter(int layoutResId, @Nullable List<MovieItemModel> data, PreviewMovieActivity activity, int videoHeight, int width, int height) {
        super(layoutResId, data);
        mActivity = activity;
        this.width = width;
        this.height = height;
        this.videoHeight = videoHeight;
    }

    @Override
    protected void convert(BaseViewHolder helper, MovieItemModel item) {
        setVideoSize(helper, item);
        //获取宽度
        setBgInfo(helper,item);
    }

    private void setVideoSize(BaseViewHolder helper, MovieItemModel item) {
        VideoView videoView = helper.getView(R.id.sv_adapter_preview_movie_videoView);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) videoView.getLayoutParams();
        if (width != 0 && height != 0){
            layoutParams.width = BigDecimal.valueOf(height>width?height:width).multiply(BigDecimal.valueOf(videoHeight)).divide(BigDecimal.valueOf(height>width?width:height),0,BigDecimal.ROUND_HALF_UP).intValue();
        }else{
            layoutParams.width = BigDecimal.valueOf(16).multiply(BigDecimal.valueOf(videoHeight)).divide(BigDecimal.valueOf(9),0,BigDecimal.ROUND_HALF_UP).intValue();
        }
        videoView.setLayoutParams(layoutParams);
        videoView.setVideoPath(item.getFilePath());
    }

    private void setBgInfo(BaseViewHolder helper, MovieItemModel item) {
        ImageView bgImage = helper.getView(R.id.sv_adapter_preview_movie_bgIcon);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) bgImage.getLayoutParams();
        if (width != 0 && height != 0){
            layoutParams.width = BigDecimal.valueOf(height>width?height:width).multiply(BigDecimal.valueOf(videoHeight)).divide(BigDecimal.valueOf(height>width?width:height),0,BigDecimal.ROUND_HALF_UP).intValue();
        }else{
            layoutParams.width = BigDecimal.valueOf(16).multiply(BigDecimal.valueOf(videoHeight)).divide(BigDecimal.valueOf(9),0,BigDecimal.ROUND_HALF_UP).intValue();
        }
        bgImage.setLayoutParams(layoutParams);
        if (TextUtils.isEmpty(item.getFilPicPath())){
            Glide.with(mActivity).load(item.getFilePath()).into(bgImage);
        }else{
            Glide.with(mActivity).load(item.getFilPicPath()).into(bgImage);
        }
    }

    @Override
    public void onPageRelease(boolean isNext, int position) {
        if (mReleasePosition != position){
            int index;
            if (isNext){
                index = 0;
            }else {
                index = 1;
            }
            releaseVideo(index);
        }
        mReleasePosition = position;
    }

    @Override
    public void onPageSelected(int position, boolean isBottom) {
        if (mSelectedPosition != position){
            if (mActivity != null){
                mActivity.resetClipView(position);
            }
            playVideo(0);
        }
        mSelectedPosition = position;
    }

    @Override
    public void onLayoutComplete() {
        playVideo(0);
    }

    public void playVideo(int position) {
        circlePosition = position;
        View itemView = getRecyclerView().getChildAt(0);
        final VideoView videoView = itemView.findViewById(R.id.sv_adapter_preview_movie_videoView);
        ImageView bgIcon = itemView.findViewById(R.id.sv_adapter_preview_movie_bgIcon);
        if (videoView.isPlaying()){
            videoView.seekTo(position);
        }else{
            videoView.start();
        }
        videoView.setOnInfoListener((mp, what, extra) -> {
            bgIcon.animate().alpha(0).setDuration(200).start();
            return false;
        });

        videoView.setOnCompletionListener(mp -> {
            mp.seekTo(position);
            mp.start();
        });
    }

    public void playVideo(double start,double end) {
        circlePosition = (int) start;
        View itemView = getRecyclerView().getChildAt(0);
        final VideoView videoView = itemView.findViewById(R.id.sv_adapter_preview_movie_videoView);
        ImageView bgIcon = itemView.findViewById(R.id.sv_adapter_preview_movie_bgIcon);
        if (mTimeRecordTask == null){
            mTimeRecordTask = new TimeRecordTask(videoView,start,end);
        }else{
            mTimeRecordTask.setStartAndEnd(videoView,start,end);
        }
        mTimeRecordTask.recordTime();
        videoView.setOnInfoListener((mp, what, extra) -> {
            bgIcon.animate().alpha(0).setDuration(200).start();
            return false;
        });
    }

    private void releaseVideo(int index){
        View itemView = getRecyclerView().getChildAt(index);
        final VideoView videoView = itemView.findViewById(R.id.sv_adapter_preview_movie_videoView);
        ImageView bgIcon = itemView.findViewById(R.id.sv_adapter_preview_movie_bgIcon);
        bgIcon.animate().alpha(1).start();
        videoView.stopPlayback();
        if (mActivity != null){
            mActivity.stopClip();
        }
    }

    public void destroyVideo(){
        View itemView = getRecyclerView().getChildAt(0);
        final VideoView videoView = itemView.findViewById(R.id.sv_adapter_preview_movie_videoView);
        videoView.stopPlayback();

        //判断是否有任务
        if (mTimeRecordTask != null){
            mTimeRecordTask.destroy();
        }
    }

    public void pauseVideo(){
        View itemView = getRecyclerView().getChildAt(0);
        final VideoView videoView = itemView.findViewById(R.id.sv_adapter_preview_movie_videoView);
        currentPosition = videoView.getCurrentPosition();
        //判断是否有任务
        if (mTimeRecordTask != null){
            mTimeRecordTask.setVideoView(videoView);
            mTimeRecordTask.stop();
        }else{
            videoView.stopPlayback();
        }
    }

    public void resumeVideo(){
        //判断任务是否为空
        View itemView = getRecyclerView().getChildAt(0);
        final VideoView videoView = itemView.findViewById(R.id.sv_adapter_preview_movie_videoView);

        //判断是否有任务
        if (mTimeRecordTask != null){
            mTimeRecordTask.setVideoView(videoView);
            mTimeRecordTask.recordTime(currentPosition);
        }else{
            videoView.seekTo(currentPosition);
            videoView.start();
            videoView.setOnCompletionListener(mp -> {
                mp.seekTo(circlePosition);
                mp.start();
            });
        }
    }

    private class TimeRecordTask implements Runnable, MediaPlayer.OnPreparedListener, MediaPlayer.OnSeekCompleteListener {

        private VideoView mVideoView;

        private double mStartTime;

        private double mEndTime;

        private int currentTime;

        private Handler mHandler;

        public TimeRecordTask(VideoView videoView,double startTime,double endTime){
            mHandler = new Handler(Looper.getMainLooper());
            mVideoView = videoView;
            mStartTime = startTime;
            mEndTime = endTime;
            mVideoView.setOnPreparedListener(this);
        }

        public void setStartAndEnd(VideoView videoView,double startTime,double endTime){
            mVideoView = videoView;
            mStartTime = startTime;
            mEndTime = endTime;
            mVideoView.setOnPreparedListener(this);
        }

        public void setVideoView(VideoView videoView){
            mVideoView = videoView;
        }

        @Override
        public void run() {
             //判断时间是否可以播放
            if (mVideoView != null){
                if (mEndTime > mStartTime){
                    //判断是否正在播放
                    //获取播放的时间
                    currentTime = mVideoView.getCurrentPosition();
                    //判断如果结束时间大了
                    if (currentTime>=mEndTime){
                        //停止播放
                        //重新开始播放
                        resetPlayer();
                    }else{
                        //重新记录时间
                        mHandler.postDelayed(this,100);
                    }
                }
            }
        }

        private void resetPlayer() {
            //重新开始
            recordTime();
        }

        public void recordTime(){
            //重新播放
            if (mVideoView != null){
                mVideoView.seekTo((int) mStartTime);
                //重新记录时间
                mHandler.post(this);
            }
        }

        public void recordTime(int time){
            //重新播放
            if (mVideoView != null){
                mVideoView.seekTo(time);
                //重新记录时间
                mHandler.post(this);
            }
        }

        public void stop(){
            if (mVideoView != null){
                mVideoView.stopPlayback();
            }
            //移除回调
            if (mHandler != null){
                mHandler.removeCallbacks(this);
            }
        }

        public void destroy(){
            if (mVideoView != null){
                mVideoView.stopPlayback();
            }
            //移除回调
            if (mHandler != null){
                mHandler.removeCallbacksAndMessages(null);
            }
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            mp.setOnSeekCompleteListener(this);
        }

        @Override
        public void onSeekComplete(MediaPlayer mp) {
            mp.start();
        }
    }
}
