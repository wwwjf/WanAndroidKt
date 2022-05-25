package com.xianghe.ivy.app.em.sound;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.xianghe.ivy.R;
import com.xianghe.ivy.utils.KLog;

/**
 * author:  ycl
 * date:  2019/2/28 10:00
 * desc:
 */
@Deprecated
public class AudioOutGoingManager {
    protected AudioManager audioManager;
    protected SoundPool soundPool;

    protected int outgoing;
    protected int streamID = -1;

    public AudioOutGoingManager(Context ctx) {
        audioManager = (AudioManager) ctx.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        soundPool = new SoundPool(1, AudioManager.STREAM_RING, 0);
//        outgoing = soundPool.load(ctx.getApplicationContext(), R.raw.em_outgoing, 1);
    }

    // 拨出电话
    public void playOutGoing() {
        streamID = playMakeCallSounds();
    }

    // 拨出的电话被接收
    public void stopOutGoing() {
        try {
            if (soundPool != null)
                soundPool.stop(streamID);
            KLog.d("soundPool stop ACCEPTED");
        } catch (Exception e) {
        }
        openSpeakerOn();
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

    public void destroy() {
        if (soundPool != null) {
            soundPool.release();
        }
        if (audioManager != null) {
            audioManager.setMode(AudioManager.MODE_NORMAL);
            audioManager.setMicrophoneMute(false);
        }
    }
}
