package com.xianghe.ivy.ui.media.record.audio;

import com.czt.mp3recorder.MP3Recorder;
import com.xianghe.ivy.ui.media.base.IMediaRecorder;
import com.xianghe.ivy.ui.media.config.MediaConfig;
import com.xianghe.ivy.utils.FileUtils;
import com.xianghe.ivy.utils.KLog;

import java.io.File;
import java.io.IOException;

/**
 * @Author: ycl
 * @Date: 2018/10/25 15:54
 * @Desc:
 */
public class AudioRecordUtils implements IMediaRecorder {

    //    private AudioRecordUtils mAudioRecordUtils;
    private MP3Recorder mMP3Recorder;
    private File mFile;

    public AudioRecordUtils() {
        mFile = FileUtils.createTempFile(MediaConfig.AUDIO_DIR, System.currentTimeMillis() + MediaConfig.AUDIO_NAME);
        if (mFile != null) {
            mMP3Recorder = new MP3Recorder(mFile);
        }
        KLog.i("filePath: " + getAudioTempFilePath() + " size: " + getAudioTempFileSize());
    }

   /* public AudioRecordUtils getInstance() {
        if (mAudioRecordUtils == null) {
            synchronized (AudioRecordUtils.class) {
                if (mAudioRecordUtils == null) {
                    mAudioRecordUtils = new AudioRecordUtils();
                }
            }
        }
        return mAudioRecordUtils;
    }
*/

    public MP3Recorder getMP3Recorder() {
        return mMP3Recorder;
    }

    @Override
    public void startRecord() throws IOException {
        if (mMP3Recorder != null && !mMP3Recorder.isRecording()) {
            mMP3Recorder.start();
        }
    }

    @Override
    public void stopRecord() {
        KLog.i("filePath: " + getAudioTempFilePath() + " size: " + getAudioTempFileSize());

        if (mMP3Recorder != null && mMP3Recorder.isRecording()) {
            mMP3Recorder.stop();
        }
    }

    @Override
    public void release() {
        stopRecord();
    }

    //   -----------------  文件管理统一管控  start  --------------------
    public File getAudioTempFile() {
        KLog.i("filePath: " + getAudioTempFilePath() + " size: " + getAudioTempFileSize());
        return mFile;
    }

    public String getAudioTempFilePath() {
        return mFile != null ? mFile.getAbsolutePath() : null;
    }

    private String getAudioTempFileSize() {
        return mFile != null ? FileUtils.sizeTempFile(mFile) : null;
    }

    public boolean deleteAudioTemp() {
        KLog.i("filePath: " + getAudioTempFilePath() + " size: " + getAudioTempFileSize());
        return mFile != null ? FileUtils.deleteTempFile(mFile) : false;
    }
    //   -----------------  文件管理统一管控  end  --------------------


}
