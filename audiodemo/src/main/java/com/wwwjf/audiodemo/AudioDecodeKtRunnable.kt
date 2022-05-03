package com.wwwjf.audiodemo

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.os.Build
import android.util.Log
import java.io.FileOutputStream
import java.nio.ByteBuffer
import kotlin.Exception

class AudioDecodeKtRunnable : Runnable {
    private val TIME_USEC: Long = 10000
    private var mDecodeOverListener: DecodeOverListener
    private var mPcmPath: String
    private var mAudioTrack: Int
    private var mExtractor: MediaExtractor
    private val TAG = AudioDecodeKtRunnable::class.java.simpleName

    constructor(
        extractor: MediaExtractor,
        audioTrack: Int,
        audioSavePath: String,
        decodeOverListener: DecodeOverListener
    ) {
        this.mExtractor = extractor
        this.mAudioTrack = audioTrack
        this.mPcmPath = audioSavePath
        this.mDecodeOverListener = decodeOverListener
    }

    override fun run() {

        try {
            val format = mExtractor.getTrackFormat(mAudioTrack)
            val mime = format.getString(MediaFormat.KEY_MIME)
            val audioCodec = mime?.let { MediaCodec.createDecoderByType(it) }
            if (audioCodec == null){
                mDecodeOverListener.decodeFailed()
                return
            }
            audioCodec.configure(format, null, null, 0)

            //启动
            audioCodec.start()

            val inputInfo = MediaCodec.BufferInfo()
            val decodeBufferInfo = MediaCodec.BufferInfo()
            audioCodec.inputBuffers
//            val inputBuffers = audioCodec.getInputBuffer(audioCodec.dequeueInputBuffer(TIME_USEC))
            var outputBuffers = audioCodec.getOutputBuffer(
                audioCodec.dequeueOutputBuffer(
                    decodeBufferInfo,
                    TIME_USEC
                )
            )

            val fileOutputStream = FileOutputStream(mPcmPath)
            var codecSuccess = false
            var inputDone = false
            while (!codecSuccess) {
                Log.e(TAG, "running1")
                if (!inputDone) {
                    // 返回用于填充有效数据的输入buffer的索引，如果当前没有可用的buffer，则返回-1
                    val inputIndex: Int = audioCodec.dequeueInputBuffer(TIME_USEC) ?: 0
                    if (inputIndex >= 0) {
                        // 从分离器拿出输入，写入解码器
                        val inputBuffer = getInputBuffer(audioCodec,inputIndex)
                        // 将position置为0，并不清除buffer内容
//                            inputBuffer.clear()
                        // 将MediaExtractor读取数据到inputBuffer
                        val sampleSize = inputBuffer?.let { mExtractor.readSampleData(it, 0) }?:-1
                        // 表示所有数据已经读取完毕
                        if (sampleSize < 0) {
                            audioCodec.queueInputBuffer(
                                inputIndex, 0, 0, 0,
                                MediaCodec.BUFFER_FLAG_END_OF_STREAM
                            )
                        } else {
                            inputInfo.offset = 0
                            inputInfo.size = sampleSize
                            inputInfo.flags = MediaCodec.BUFFER_FLAG_SYNC_FRAME
                            inputInfo.presentationTimeUs = mExtractor.sampleTime
                            Log.e(TAG, "run: 往解码器写入数据，当前时间戳为：${inputInfo.presentationTimeUs}")

                            audioCodec.queueInputBuffer(
                                inputIndex, inputInfo.offset, sampleSize,
                                inputInfo.presentationTimeUs, 0
                            )
                            // 读取下一帧数据
                            mExtractor.advance()
                        }
                    }
                }


                // dequeueInputBuffer dequeueOutputBuffer 返回值解释
                // INFO_TRY_AGAIN_LATER=-1 等待超时
                // INFO_OUTPUT_FORMAT_CHANGED=-2 媒体格式更改
                // INFO_OUTPUT_BUFFERS_CHANGED=-3 缓冲区已更改（过时）
                // 大于等于0的为缓冲区数据下标

                // 整体解码结束标记
                var decodeOutputDone = false
                var chunkPCM: ByteArray
                while (!decodeOutputDone) {

                    Log.e(TAG, "running3")
                    val outputIndex = audioCodec.dequeueInputBuffer(TIME_USEC)
                    if (outputIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                        // 没有可用的解码器
                        decodeOutputDone = true
                        Log.e(TAG, "run: INFO_TRY_AGAIN_LATER")
                    } else if (outputIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                        outputBuffers = getOutputBuffer(audioCodec,outputIndex)
                        Log.e(TAG, "run: INFO_OUTPUT_BUFFERS_CHANGED")
                    } else if (outputIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        val newOutputFormat = audioCodec.outputFormat
                        Log.e(TAG, "run: INFO_OUTPUT_FORMAT_CHANGED")
                    } else if (outputIndex < 0) {

                        Log.e(TAG, "run: <0")
                    } else {
                        Log.e(TAG, "run: else")
                        val outputBuffer: ByteBuffer?
                        outputBuffer = getOutputBuffer(audioCodec,outputIndex)

                        chunkPCM = ByteArray(decodeBufferInfo.size)
                        outputBuffer?.get(chunkPCM)
                        outputBuffer?.clear()

                        fileOutputStream.write(chunkPCM)
                        fileOutputStream.flush()
                        Log.e(TAG, "run: 释放输出流缓冲区${outputIndex}")
                        outputIndex?.let { audioCodec.releaseOutputBuffer(outputIndex, false) }

                        // 编解码结束
                        Log.e(TAG, "run: ${decodeBufferInfo.flags}")
                        if ((decodeBufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                            mExtractor.release()
                            audioCodec?.stop()
                            audioCodec?.release()
                            codecSuccess = true
                            decodeOutputDone = true
                        }

                    }
                }


            }


            fileOutputStream.close()
            mDecodeOverListener.decodeSuccess()
        } catch (e: Exception) {
            e.printStackTrace()
            mDecodeOverListener.decodeFailed()
        }

    }

    fun getInputBuffer(mediaCodec: MediaCodec, index: Int): ByteBuffer? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return mediaCodec.getInputBuffer(index)
        } else {
            return mediaCodec.inputBuffers[index]
        }
    }

    fun getOutputBuffer(mediaCodec: MediaCodec, index: Int): ByteBuffer? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return mediaCodec.getOutputBuffer(index)
        } else {
            return mediaCodec.outputBuffers[index]
        }
    }

}
