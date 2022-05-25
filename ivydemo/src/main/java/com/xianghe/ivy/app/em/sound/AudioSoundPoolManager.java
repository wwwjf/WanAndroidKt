package com.xianghe.ivy.app.em.sound;

import android.content.Context;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Handler;

import com.xianghe.ivy.R;
import com.xianghe.ivy.utils.KLog;


/**
 * author:  ycl
 * date:  2019/2/26 14:49
 * desc:
 */
@Deprecated
public class AudioSoundPoolManager {
    protected AudioManager audioManager;
    protected SoundPool soundPool;
    protected Ringtone ringtone;
    protected int outgoing = -1;
    protected int streamID = -1;

    private Context context;

    private Handler handler;

    private static AudioSoundPoolManager instance = null;

    public static AudioSoundPoolManager getInstance(Context ctx) {
        if (instance == null) {
            synchronized (AudioSoundPoolManager.class) {
                if (instance == null) {
                    instance = new AudioSoundPoolManager(ctx.getApplicationContext());
                }
            }
        }
        return instance;
    }

    // 拨出电话
    public void playOutGoing() {
        if (streamID == -1) {
            handler.postDelayed(() -> streamID = playMakeCallSounds(), 300);
        }
    }

    // 拨出的电话被接收
    public void stopOutGoing() {
        if (streamID == -1) {
            return;
        }
        try {
            if (soundPool != null)
                soundPool.stop(streamID);
            streamID = -1;
            KLog.d("soundPool stop ACCEPTED");
        } catch (Exception e) {
        }
        openSpeakerOn();
    }

    public void playInComing() {
        Uri ringUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        audioManager.setMode(AudioManager.MODE_RINGTONE);
        audioManager.setSpeakerphoneOn(true);
        ringtone = RingtoneManager.getRingtone(context, ringUri);
        if (!ringtone.isPlaying()) {
            ringtone.play();
        }
    }

    // 接听电话
    public void stopInComing() {
        openSpeakerOn();
        if (ringtone != null&&ringtone.isPlaying())
            ringtone.stop();
    }

    public AudioSoundPoolManager(Context ctx) {
        this.context = ctx;
        audioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
        soundPool = new SoundPool(1, AudioManager.STREAM_RING, 0);
//        outgoing = soundPool.load(ctx, R.raw.em_outgoing, 1);
        handler = new Handler();
    }

    private int playMakeCallSounds() {
        try {
            audioManager.setMode(AudioManager.MODE_RINGTONE);
            audioManager.setSpeakerphoneOn(true);

            // play
            int id = soundPool.play(outgoing, // sound resource
                    0.3f, // left volume
                    0.3f, // right volume
                    1,    // priority
                    -1,   // loop，0 is no loop，-1 is loop forever
                    1);   // playback rate (1.0 = normal playback, range 0.5 to 2.0)
            return id;
        } catch (Exception e) {
            return -1;
        }
    }


    private void openSpeakerOn() {
        try {
            if (!audioManager.isSpeakerphoneOn())
                audioManager.setSpeakerphoneOn(true);
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeSpeakerOn() {
        try {
            if (audioManager != null) {
                // int curVolume =
                // audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
                if (audioManager.isSpeakerphoneOn())
                    audioManager.setSpeakerphoneOn(false);
                audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                // audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                // curVolume, AudioManager.STREAM_VOICE_CALL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void destroy() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if (soundPool != null)
            soundPool.release();
        if (ringtone != null && ringtone.isPlaying())
            ringtone.stop();
        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.setMicrophoneMute(false);

        streamID = -1;

    }
}
