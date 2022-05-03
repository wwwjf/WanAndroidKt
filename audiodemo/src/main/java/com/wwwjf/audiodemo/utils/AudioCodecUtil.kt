package com.wwwjf.audiodemo.utils

import android.media.MediaExtractor
import android.media.MediaFormat
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.wwwjf.audiodemo.AudioDecodeListener
import com.wwwjf.audiodemo.AudioDecodeRunnable
import com.wwwjf.audiodemo.AudioEncodeRunnable
import com.wwwjf.audiodemo.DecodeOverListener

object AudioCodecUtil {

    private val TAG = AudioCodecUtil::class.java.simpleName
    private val handler = Handler(Looper.getMainLooper())

    /**
     * 将音频文件解码成原始的PCM数据
     */
    fun getPcmFromAudio(audioPath: String, audioSavePath: String, mListener: AudioDecodeListener?) {
        Log.e(TAG, "getPcmFromAudio: audioSavePath:$audioSavePath")
        val extractor = MediaExtractor()
        var audioTrack = -1
        var hasAudio = false
        extractor.setDataSource(audioPath)

        for (i in 0 until extractor.trackCount) {
            val format = extractor.getTrackFormat(i)
            val mime = format.getString(MediaFormat.KEY_MIME)
            if (mime?.startsWith("audio/") == true) {
                audioTrack = i
                hasAudio = true
                break
            }
        }
        if (!hasAudio) {
            Log.e(TAG, "getPcmFromAudio: 没有音轨")
            mListener?.decodeFailed()
            return
        }
        Thread(
            AudioDecodeRunnable(
                extractor,
                audioTrack,
                audioSavePath,
                object : DecodeOverListener {
                    override fun decodeSuccess() {
                        Log.e(TAG, "decodeSuccess: ")
                        handler.post {
                            mListener?.decodeSuccess()
                        }
                    }

                    override fun decodeFailed() {
                        Log.e(TAG, "decodeFailed: ")
                        handler.post {
                            mListener?.decodeFailed()
                        }
                    }
                })
        ).start()
        /*Thread(AudioDecodeRunnable(extractor, audioTrack, audioSavePath,
                object : DecodeOverListener {
                    override fun decodeSuccess() {
                        Log.e(TAG, "decodeSuccess: ")
                        mListener?.decodeSuccess()

                    }

                    override fun decodeFailed() {
                        Log.e(TAG, "decodeFailed: ")
                        mListener?.decodeFailed()
                    }
                })
        ).start()*/

    }

    /**
     * pcm文件转音频
     */
    fun pcmToAudio(pcmPath: String, audioPath: String, mListener: AudioDecodeListener?) {

        Log.e(TAG, "pcmToAudio: 开始编码")
        Thread(AudioEncodeRunnable(pcmPath,audioPath,object : AudioDecodeListener {
            override fun decodeSuccess() {
                Log.e(TAG, "pcmToAudio-decodeSuccess: ")
                handler.post {
                    mListener?.decodeSuccess()
                }
            }

            override fun decodeFailed() {
                Log.e(TAG, "pcmToAudio-decodeFailed: ")
                handler.post {
                    mListener?.decodeFailed()
                }
            }
        })).start()
    }

    @JvmStatic
    fun addADTStoPacket(packet: ByteArray, packetLen: Int) {
        val profile = 2 // AAC LC

        val freqIdx = 4 // 44.1KHz

        val chanCfg = 2 // CPE

        packet[0] = 0xFF.toByte()
        packet[1] = 0xF9.toByte()
        packet[2] = ((profile - 1 shl 6) + (freqIdx shl 2) + (chanCfg shr 2)).toByte()
        packet[3] = ((chanCfg and 3 shl 6) + (packetLen shr 11)).toByte()
        packet[4] = ((packetLen and 0x7FF shr 3).toByte())
        packet[5] = (((packetLen and 7 shl 5) + 0x1F).toByte())
        packet[6] = 0xFC.toByte()
    }
}


