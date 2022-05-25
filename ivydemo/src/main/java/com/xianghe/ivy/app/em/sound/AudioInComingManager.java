package com.xianghe.ivy.app.em.sound;

import android.content.Context;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import com.xianghe.ivy.utils.KLog;

/**
 * author:  ycl
 * date:  2019/2/28 10:05
 * desc:
 */

public class AudioInComingManager {
    private Context context;

    protected AudioManager audioManager;
    protected Ringtone ringtone;

    public AudioInComingManager(Context ctx) {
        this.context = ctx;
        audioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
        Uri ringUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE); // 系统铃声
        ringtone = RingtoneManager.getRingtone(context, ringUri);
    }

    public void playInComing() {
        if (audioManager == null || ringtone == null) {
            return;
        }
        audioManager.setMode(AudioManager.MODE_RINGTONE);
        audioManager.setSpeakerphoneOn(true);
        ringtone.play();
        KLog.d("ringtone.play()");
    }

    // 接听电话
    public AudioInComingManager stopInComing() {
        openSpeakerOn();
        if (ringtone != null)
            ringtone.stop();
        KLog.d("ringtone.stop()");
        return this;
    }

    private void openSpeakerOn() {
        if (audioManager == null) {
            return;
        }
        try {
            if (!audioManager.isSpeakerphoneOn())
                audioManager.setSpeakerphoneOn(true);
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void destroy() {
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
            ringtone = null;
        }
        if (audioManager != null) {
            audioManager.setMode(AudioManager.MODE_NORMAL);
            audioManager.setMicrophoneMute(false);
            audioManager = null;
        }
        KLog.d("destroy");
    }
}
