package com.xianghe.ivy.ui.module.record.local_media;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.xianghe.ivy.R;
import com.xianghe.ivy.mvp.BaseActivity;

import org.jetbrains.annotations.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class VideoPlayActivity extends BaseActivity {

    private Unbinder mBind;

    @BindView(R.id.activity_video_play_layout)
    FrameLayout mFrameLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        mBind = ButterKnife.bind(this);
        init();
    }

    private void init() {
        String file_path = getIntent().getStringExtra("file_path");
        if (!TextUtils.isEmpty(file_path)) {
            if (file_path.endsWith(".mp4")) {
                //播放视频
                playVideo(file_path);
            } else {
                //查看图片
                ImageView imageView = new ImageView(this);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.gravity = Gravity.CENTER;
                mFrameLayout.addView(imageView, layoutParams);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                Glide.with(this).load(file_path).into(imageView);
            }
        }
    }

    private void playVideo(String file_path) {
        final VideoView vv = new VideoView(this);
        vv.setVideoURI(Uri.parse(file_path));
        vv.start();
        vv.setOnPreparedListener(mp -> {
            mp.start();
            mp.setLooping(true);
        });
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        mFrameLayout.addView(vv, params);
    }


    @OnClick(R.id.iv_activity_video_play_back)
    void onViewClicked(View view) {
        finish();
    }

    @Override
    protected void onDestroy() {
        if (mBind != null) {
            mBind.unbind();
        }
        super.onDestroy();
    }
}
