package com.xianghe.ivy.ui.module.videoedit.sound;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatSeekBar;

import com.blankj.utilcode.util.ScreenUtils;
import com.xianghe.ivy.R;
import com.xianghe.ivy.ui.module.videoedit.VideoEditPlayUtils;
import com.xianghe.ivy.weight.basepopuwindow.basepopup.BasePopupWindow;


/**
 * @Author: ycl
 * @Date: 2018/11/7 16:58
 * @Desc:
 */
public class VESoundPopupWindow extends BasePopupWindow implements SeekBar.OnSeekBarChangeListener {
    private VideoEditPlayUtils mVideoEditPlayUtils;
    private TextView vepopsoundtv;
    private TextView vepopmusictv;
    private TextView vepoprecordtv;

    public VESoundPopupWindow(Activity context, VideoEditPlayUtils videoEditPlayUtils, String musicPath, String recordPath, boolean isPic) {
        super(context, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.mVideoEditPlayUtils = videoEditPlayUtils;
        setShowAtDown(true);
        setOffsetY((int) (ScreenUtils.getScreenHeight() * 40 / 375.0));
        bindEvent(musicPath != null && !TextUtils.isEmpty(musicPath),
                recordPath != null && !TextUtils.isEmpty(recordPath),isPic);
    }

    private void bindEvent( boolean hasMusic, boolean hasRecord,boolean isPic) {

        View view = findViewById(R.id.ve_pop_ll);
        view.setOnTouchListener((v, event) -> {
            if (VESoundPopupWindow.this.isShowing()) {
                VESoundPopupWindow.this.dismiss();
            }
            return true;
        });
        // 原声
        vepopsoundtv = (TextView) findViewById(R.id.ve_pop_sound_tv);
        RelativeLayout ve_pop_sound_ll = (RelativeLayout) findViewById(R.id.ve_pop_sound_ll);
        ve_pop_sound_ll.setOnTouchListener((v, event) -> true);
        AppCompatSeekBar vepopsoundseekbar = (AppCompatSeekBar) findViewById(R.id.ve_pop_sound_seekbar);
        if (!isPic) {
            vepopsoundtv.setVisibility(View.VISIBLE);
            ve_pop_sound_ll.setVisibility(View.VISIBLE);
        } else {
            vepopsoundtv.setVisibility(View.GONE);
            ve_pop_sound_ll.setVisibility(View.GONE);
        }
        if (mVideoEditPlayUtils != null) {
            setProgress(mVideoEditPlayUtils.getVideoVolume(), vepopsoundseekbar, vepopsoundtv);
        }
        vepopsoundseekbar.setOnSeekBarChangeListener(this);

        // 音乐
        View vepopmusicview = (View) findViewById(R.id.ve_pop_music_view);
        RelativeLayout vepopmusicll = (RelativeLayout) findViewById(R.id.ve_pop_music_ll);
        vepopmusicll.setOnTouchListener((v, event) -> true);
        if (hasMusic) {
            vepopmusicview.setVisibility(View.VISIBLE);
            vepopmusicll.setVisibility(View.VISIBLE);
        } else {
            vepopmusicview.setVisibility(View.GONE);
            vepopmusicll.setVisibility(View.GONE);
        }
        vepopmusictv = (TextView) findViewById(R.id.ve_pop_music_tv);
        AppCompatSeekBar vepopmusicseekbar = (AppCompatSeekBar) findViewById(R.id.ve_pop_music_seekbar);
        if (mVideoEditPlayUtils != null) {
            setProgress(mVideoEditPlayUtils.getMusicVolume(), vepopmusicseekbar, vepopmusictv);
        }
        vepopmusicseekbar.setOnSeekBarChangeListener(this);

        // 配音
        View vepoprecordview = (View) findViewById(R.id.ve_pop_record_view);
        RelativeLayout vepoprecordll = (RelativeLayout) findViewById(R.id.ve_pop_record_ll);
        vepoprecordll.setOnTouchListener((v, event) -> true);
        if (hasRecord) {
            vepoprecordview.setVisibility(View.VISIBLE);
            vepoprecordll.setVisibility(View.VISIBLE);
        } else {
            vepoprecordview.setVisibility(View.GONE);
            vepoprecordll.setVisibility(View.GONE);
        }
        vepoprecordtv = (TextView) findViewById(R.id.ve_pop_record_tv);
        AppCompatSeekBar vepoprecordseekbar = (AppCompatSeekBar) findViewById(R.id.ve_pop_record_seekbar);
        if (mVideoEditPlayUtils != null) {
            setProgress(mVideoEditPlayUtils.getRecordVolume(), vepoprecordseekbar, vepoprecordtv);
        }
        vepoprecordseekbar.setOnSeekBarChangeListener(this);

    }

    private void setProgress(float progress, AppCompatSeekBar vepopsoundseekbar, TextView tv) {
        int currentPro = (int) (progress * 100);
        vepopsoundseekbar.setProgress(currentPro);
        tv.setText("" + currentPro);
    }

    @Override
    protected Animation initShowAnimation() {
        return createVerticalAnimation(-1f, 0);
    }

    @Override
    protected Animation initExitAnimation() {
        return createVerticalAnimation(0, -1f);
    }

    private Animation createVerticalAnimation(float fromY, float toY) {
        Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0f,
                Animation.RELATIVE_TO_SELF,
                0f,
                Animation.RELATIVE_TO_SELF,
                fromY,
                Animation.RELATIVE_TO_SELF,
                toY);
        animation.setDuration(300);
        animation.setInterpolator(new DecelerateInterpolator());
        return animation;
    }

    @Override
    public View getClickToDismissView() {
        return getPopupWindowView();
    }

    @Override
    public View onCreatePopupView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.popup_video_edit_sound, null);
    }

    @Override
    public View initAnimaView() {
        return findViewById(R.id.ve_pop_ll);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        float volume = progress / 100f;
            /* if (volume < 5 / 100f) {
                    mMusicSeekBar.setProgress(10);
                    volume = 5 / 100f;
                }*/
        switch (seekBar.getId()) {
            case R.id.ve_pop_sound_seekbar:
                if (vepopsoundtv != null) {
                    vepopsoundtv.setText("" + progress);
                }
                if (mVideoEditPlayUtils == null) {
                    return;
                }
                mVideoEditPlayUtils.setVideoVolume(volume);
                break;
            case R.id.ve_pop_music_seekbar:
                if (vepopmusictv != null) {
                    vepopmusictv.setText("" + progress);
                }
                if (mVideoEditPlayUtils == null) {
                    return;
                }
                mVideoEditPlayUtils.setMusicVolume(volume);
                // 控制音乐与背景音乐声音一致
//                mVideoEditPlayUtils.setNetMusicVolume(volume);
                break;
            case R.id.ve_pop_record_seekbar:
                if (vepoprecordtv != null) {
                    vepoprecordtv.setText("" + progress);
                }
                if (mVideoEditPlayUtils == null) {
                    return;
                }
                mVideoEditPlayUtils.setRecordVolume(volume);
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
