package com.wwwjf.audiodemo

import android.Manifest
import android.content.pm.PackageManager
import android.media.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.wwwjf.audiodemo.databinding.ActivityAudioBinding
import com.wwwjf.audiodemo.utils.AudioCodecUtil
import com.wwwjf.base.utils.DateUtil
import com.wwwjf.audiodemo.utils.PcmToWavUtil
import java.io.*


class AudioDemoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAudioBinding
    private var audioRecord: AudioRecord? = null
    private var mMediaRecorder: MediaRecorder? = null
    private lateinit var mAudioFileName: String
    private var recordBuffSize = 0
    private lateinit var fos: FileOutputStream
    private var isRecording = false
    private val TAG = AudioDemoActivity::class.simpleName
    private lateinit var mediaPlayer: MediaPlayer

    private val SDCARD_PATH = "sdcard/DCIM/Camera/"

    private val fileName = "VID_20181219_020225"
    private val fileName1 = "VID_20181222_205848"
    private val fileName2 = "5800dd66dc7d3b2e1c9586187f98fff5"
    private lateinit var DEST_PATH: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_audio)

//        val file = getExternalFilesDir("external_video_muxer")//需要权限申请
//        val dcimFile = getExternalFilesDir(Environment.DIRECTORY_DCIM)
        /*if (file?.exists() == true) {
            DEST_PATH = "${file.absolutePath}/"
        }*/
        DEST_PATH = applicationContext.externalMediaDirs[0].absolutePath + "/"
        val permission = arrayOf(Manifest.permission.RECORD_AUDIO)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkSelfPermission(Manifest.permission.RECORD_AUDIO).equals(PackageManager.PERMISSION_GRANTED)) {
                requestPermissions(permission, 100)
            } else {
                Log.d(TAG, "onCreate: 已获取权限")
            }
        }


        mediaPlayer = MediaPlayer()
        val list = cacheDir.list()
        list.forEach {
            Log.e(TAG, "onCreate: ${it}")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            Toast.makeText(this, "音频权限授权成功", Toast.LENGTH_SHORT).show()
        }
    }

    fun startMediaRecord() {
        // 开始录音
        /* ①Initial：实例化MediaRecorder对象 */
        if (mMediaRecorder == null)
            mMediaRecorder = MediaRecorder()
        try {
            /* ②setAudioSource/setVedioSource */
            mMediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC) // 设置麦克风
            /*
            * ②设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default THREE_GPP(3gp格式
            * * ，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)
            * */
            mMediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            /* ②设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default 声音的（波形）的采样 */
            mMediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            mAudioFileName = DateUtil.format(System.currentTimeMillis())
            /* ③准备 */
            mMediaRecorder?.setOutputFile("$DEST_PATH${mAudioFileName}.m4a")
            mMediaRecorder?.prepare()
            /* ④开始 */
            mMediaRecorder?.start()
        } catch (e: IllegalStateException) {
        } catch (e: IOException) {
        }
    }

    /**
     * 停止录音
     */
    fun stopMediaRecord() {
        //有一些网友反应在5.0以上在调用stop的时候会报错，翻阅了一下谷歌文档发现上面确实写的有可能会报错的情况，捕获异常清理一下就行了，感谢大家反馈！
        try {
            mMediaRecorder?.stop()
            mMediaRecorder?.release()
            mMediaRecorder = null
//            mAudioRecordPath = ""
        } catch (e: RuntimeException) {
            mMediaRecorder?.reset()
            mMediaRecorder?.release()
            mMediaRecorder = null
            val file = File(mAudioFileName)
            if (file.exists()) file.delete()
            mAudioFileName = ""
        }
    }

    private fun startAudioRecord() {

        var read:Int
        val data = ByteArray(recordBuffSize)
        Log.e(TAG, "startAudioRecord: data=${data.size}")
        audioRecord?.startRecording()
        isRecording = true
        fos.let {
            while (isRecording) {
                Log.e(TAG, "startAudioRecord: 录制中")
                read = audioRecord?.read(data, 0, recordBuffSize)?:-1
                if (read != AudioRecord.ERROR_INVALID_OPERATION) {
                    fos.write(data)
                }
            }
        }
        fos.flush()
        fos.close()

    }

    private fun createAudioRecord() {
        recordBuffSize = AudioRecord.getMinBufferSize(
            GlobalConfig.SAMPLE_RATE_INHZ,
            GlobalConfig.CHANNEL_CONFIG,
            GlobalConfig.AUDIO_FORMAT
        )
        Log.e(TAG, "createAudioRecord: recordBuffSize=${recordBuffSize}")
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC, GlobalConfig.SAMPLE_RATE_INHZ,
            GlobalConfig.CHANNEL_CONFIG, GlobalConfig.AUDIO_FORMAT, recordBuffSize
        )
        mAudioFileName = com.wwwjf.base.utils.DateUtil.format(System.currentTimeMillis())
        fos = FileOutputStream("$DEST_PATH$mAudioFileName.pcm")
    }

    fun startRecord(view: View) {
//        startMediaRecord()
        Thread {
            createAudioRecord()
            startAudioRecord()
        }.start()
    }

    fun stopRecord(view: View) {
//        stopMediaRecord()

        isRecording = false
        // 释放资源
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
        Log.e(TAG, "startAudioRecord: 停止录制")

    }


    fun playRecord(view: View) {
        playMediaRecord()
    }

    private fun playMediaRecord() {
//        mediaPlayer.setDataSource(File(cacheDir,"481").absolutePath)
//        /data/data/com.wwwjf.audio/cache/1648784753991.m4a

        mediaPlayer.reset()
        val filePath = "$DEST_PATH$mAudioFileName.mp3"
        Log.e(TAG, "play: ${filePath}")
        val fis = FileInputStream(filePath)
        Log.e(TAG, "playMediaRecord: ${fis.fd}")
        mediaPlayer.setDataSource(fis.fd)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            Log.e(TAG, "play: 准备播放-${mediaPlayer.duration}")
            it.start()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mediaPlayer.setOnDrmPreparedListener { mp, status ->
                Log.e(TAG, "play: drmPrepared")
            }
        }
        mediaPlayer.setOnCompletionListener {
            Log.e(TAG, "play: 播放完成")
//            it.prepare()
        }
    }

    fun codecToPcm(view: View) {
        AudioCodecUtil.getPcmFromAudio(
            "$SDCARD_PATH$fileName1.mp4",
            "$DEST_PATH$fileName1.pcm",
            object : AudioDecodeListener {
                override fun decodeSuccess() {
                    Log.e(TAG, "音频解码完成")

                }

                override fun decodeFailed() {
                    Log.e(TAG, "音频解码失败")
                }
            })
    }

    fun codecToAudio(view: View) {
        /*AudioCodec.pcmToAudio(
            "$DEST_PATH$mAudioFileName.pcm",
            "$DEST_PATH$mAudioFileName.aac",
            object : AudioDecodeListener {
                override fun decodeSuccess() {
                    Log.e(TAG, "音频编码完成")
                }

                override fun decodeFailed() {
                    Log.e(TAG, "音频编码失败")
                }
            })*/

        val pcmToWavUtil = PcmToWavUtil(
            GlobalConfig.SAMPLE_RATE_INHZ,
            GlobalConfig.CHANNEL_CONFIG,
            GlobalConfig.AUDIO_FORMAT
        )
        pcmToWavUtil.pcmToWav(
            "$DEST_PATH$mAudioFileName.pcm",
            "$DEST_PATH$mAudioFileName.mp3")


    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}