package com.wwwjf.videodemo

import android.Manifest
import android.content.pm.PackageManager
import android.media.*
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.util.HashMap

/**
 * 视频分离、视频合成
 */
class ExtractorMuxerActivity : AppCompatActivity() {

    private val SDCARD_PATH = "sdcard/DCIM/Camera/"

    private val fileName = "VID_20181219_020225"
    private val fileName1 = "VID_20181222_205848"
    private val fileName2 = "5800dd66dc7d3b2e1c9586187f98fff5"
    private val ORIGIN_PATH = "$SDCARD_PATH$fileName.mp4"
//    private val DEST_PATH = "sdcard/DCIM/DUtil/"

    private lateinit var DEST_PATH: String

    private lateinit var mFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_extractor_muxer)


//        /storage/emulated/0/Android/data/com.wwwjf.videodemo/cacheVID_20181219_020225_video.mp4
//        DEST_PATH = "${externalCacheDir?.absolutePath}/"//APP外部缓存目录

//        /data/user/0/com.wwwjf.videodemo/cache
//        DEST_PATH = "${cacheDir?.absolutePath}/"//APP内部目录
        externalCacheDirs.forEach {
            Log.e("TAG", "onCreate: externalCachedirs:${it.absolutePath}")
        }
        Log.e("TAG", "onCreate: cacheDir:${cacheDir?.absolutePath}")
        val file = getExternalFilesDir("external_video_muxer")//需要权限申请
        val dcimFile = getExternalFilesDir(Environment.DIRECTORY_DCIM)


        if (file?.exists() == true) {
            DEST_PATH = "${file.absolutePath}/"
        }
        DEST_PATH = applicationContext.externalMediaDirs[0].absolutePath + "/"
        val dataFile = Environment.getDataDirectory()
        Log.e("TAG", "onCreate: externalFilesDir:${file?.absolutePath}")
        Log.e("TAG", "onCreate: dcimFile:${dcimFile?.absolutePath}")
        Log.e("TAG", "onCreate: dataFile:${dataFile?.absolutePath}")
        dataFile?.listFiles()?.forEach {

            Log.e("TAG", "onCreate: dataFileList:${it.absolutePath}")
        }
        val mediaFile = applicationContext.externalMediaDirs
        mediaFile.forEach {

            Log.e("TAG", "onCreate: mediaFileList:${it.absolutePath}")
        }

        if (permissionCheck()) {
            Log.e("TAG", "onCreate: 已有权限")
        }
    }

    fun extractVideo(view: View) {
        extractVideo(ORIGIN_PATH)
    }

    fun extractAudio(view: View) {
        extractAudio(ORIGIN_PATH)
    }

    fun muxerVideoAudio(view: View) {
        muxerVideoAudio()
    }


    private fun permissionCheck(): Boolean {

        val permission =
            ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                100
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            Log.e("TAG", "onRequestPermissionsResult: 授权成功")

        }
    }

    /**
     * 分离视频
     */
    private fun extractVideo(path: String) {
        val videoExtractor = MediaExtractor()
        videoExtractor.setDataSource(path)
        var videoIndex = -1
        for (i in 0 until videoExtractor.trackCount) {
            val format = videoExtractor.getTrackFormat(i)
            if (format.getString(MediaFormat.KEY_MIME)?.startsWith("video/") == true) {
                videoIndex = i
                break
            }

        }
        //选择源文件中要分离的轨道
        videoExtractor.selectTrack(videoIndex)

        val trackFormat = videoExtractor.getTrackFormat(videoIndex)


//        val file = File("$DEST_PATH${fileName}_video.mp4")
        val destPath = "$DEST_PATH${fileName}_video.mp4"
//        val destPath = "${DEST_PATH}${fileName}"+"video.mp4"
        Log.e("TAG", "extractVideo: destPath=${destPath}")
        val mediaMuxer = MediaMuxer(
            destPath,
            MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4
        )

        //添加媒体通道
        val writeVideoTractIndex = mediaMuxer.addTrack(trackFormat)

        val byteBuffer = ByteBuffer.allocate(trackFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE))
        val videoBufferInfo = MediaCodec.BufferInfo()

        mediaMuxer.start()

        // 获取帧之间的间隔时间 start
        videoExtractor.readSampleData(byteBuffer, 0)
        if (videoExtractor.sampleFlags == MediaExtractor.SAMPLE_FLAG_SYNC) {
            videoExtractor.advance()
        }
        videoExtractor.readSampleData(byteBuffer, 0)
        val firstVideoPTS = videoExtractor.sampleTime

        videoExtractor.advance()
        videoExtractor.readSampleData(byteBuffer, 0)
        val secondVideoPTS = videoExtractor.sampleTime

        val videoSampleTime = Math.abs(firstVideoPTS.minus(secondVideoPTS))

        Log.e("TAG", "extractVideo: videoSampleTime=$videoSampleTime")
        videoExtractor.unselectTrack(videoIndex)
        videoExtractor.selectTrack(videoIndex)
        // 获取帧之间的间隔时间 end

        while (true) {
            val readSampleSize = videoExtractor.readSampleData(byteBuffer, 0)
            if (readSampleSize < 0) {
                break
            }
            videoBufferInfo.size = readSampleSize
            videoBufferInfo.presentationTimeUs += videoSampleTime
            videoBufferInfo.offset = 0
            videoBufferInfo.flags = videoExtractor.sampleFlags
            Log.e(
                "TAG",
                "extractVideo: presentationTimeUs=${videoBufferInfo.presentationTimeUs}," +
                        "videoSampleTime=${videoSampleTime}," +
                        "videoBufferSize=${videoBufferInfo.size}," +
                        "flag=${videoBufferInfo.flags}"
            )
            mediaMuxer.writeSampleData(writeVideoTractIndex, byteBuffer, videoBufferInfo)
            videoExtractor.advance()

        }

        mediaMuxer.stop()
        videoExtractor.release()
        mediaMuxer.release()
        Log.e("TAG", "extractVideo: 分离视频完成---------------")
    }


    /**
     * 分离音频
     */
    private fun extractAudio(path: String) {

        val audioExtractor = MediaExtractor()
        audioExtractor.setDataSource(path)
        var audioIndex = -1
        val trackCount = audioExtractor.trackCount
        for (i in 0 until trackCount) {
            val trackFormat = audioExtractor.getTrackFormat(i)
            if (trackFormat.getString(MediaFormat.KEY_MIME)?.startsWith("audio/") == true) {
                audioIndex = i
                break
            }
        }
        audioExtractor.selectTrack(audioIndex)

        val trackFormat = audioExtractor.getTrackFormat(audioIndex)
        val audioMuxer = MediaMuxer(
            "$DEST_PATH${fileName}_audio.mp3",
            MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4
        )
        val writeAudioIndex = audioMuxer.addTrack(trackFormat)
        audioMuxer.start()

        val byteBuffer = ByteBuffer.allocate(trackFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE))
        val bufferInfo = MediaCodec.BufferInfo()

        audioExtractor.readSampleData(byteBuffer, 0)
        if (audioExtractor.sampleFlags == MediaExtractor.SAMPLE_FLAG_SYNC) {
            audioExtractor.advance()
        }
        audioExtractor.readSampleData(byteBuffer, 0)
        val secondTime = audioExtractor.sampleTime
        audioExtractor.advance()
        audioExtractor.readSampleData(byteBuffer, 0)
        val thirdTime = audioExtractor.sampleTime
        val timeStampAudio = Math.abs(secondTime.minus(thirdTime))
        Log.e("TAG", "extractAudio: timeStampAudio=${timeStampAudio}")
        audioExtractor.unselectTrack(audioIndex)
        audioExtractor.selectTrack(audioIndex)
        // 获取帧之间的间隔时间 start
        // 获取帧之间的间隔时间 end


        while (true) {
            val readSampleSize = audioExtractor.readSampleData(byteBuffer, 0)
            if (readSampleSize < 0) {
                break
            }
            bufferInfo.size = readSampleSize
            bufferInfo.flags = audioExtractor.sampleFlags
            bufferInfo.offset = 0
            bufferInfo.presentationTimeUs += timeStampAudio
            Log.e(
                "TAG",
                "extractVideo: presentationTimeUs=${bufferInfo.presentationTimeUs}," +
                        "videoSampleTime=${timeStampAudio}," +
                        "videoBufferSize=${bufferInfo.size}," +
                        "flag=${bufferInfo.flags}"
            )
            audioMuxer.writeSampleData(writeAudioIndex, byteBuffer, bufferInfo)
            audioExtractor.advance()
        }

        audioMuxer.stop()
        audioExtractor.release()
        audioMuxer.release()
        Log.e("TAG", "extractAudio: 分离音频完成------")

    }


    /**
     * 合成视频、音频
     */
    private fun muxerVideoAudio() {

        val muxer = MediaMuxer(
            "$DEST_PATH${fileName}_compose.mp4",
            MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4
        )
        val videoExtractor = MediaExtractor()
        videoExtractor.setDataSource("$DEST_PATH${fileName}_video.mp4")
        var videoIndex = -1
        var videoFormat: MediaFormat? = null
        for (i in 0 until videoExtractor.trackCount) {
            videoFormat = videoExtractor.getTrackFormat(i)
            if (videoFormat?.getString(MediaFormat.KEY_MIME)?.startsWith("video/") == true) {
                videoIndex = i
                break
            }
        }
        var writeVideoIndex = -1
        if (videoFormat != null) {
            writeVideoIndex = muxer.addTrack(videoFormat)
        }
        videoExtractor.selectTrack(videoIndex)



        val audioExtractor2 = MediaExtractor()
        audioExtractor2.setDataSource("$SDCARD_PATH${fileName2}.mp4")
        var audioIndex2 = -1
        var audioFormat2: MediaFormat? = null
        for (i in 0 until audioExtractor2.trackCount) {
            audioFormat2 = audioExtractor2.getTrackFormat(i)
            if (audioFormat2?.getString(MediaFormat.KEY_MIME)?.startsWith("audio/") == true) {
                audioIndex2 = i
            }
        }
        var writeAudioIndex2 = -1
        if (audioFormat2 != null) {
            writeAudioIndex2 = muxer.addTrack(audioFormat2)
        }
        audioExtractor2.selectTrack(audioIndex2)

        val audioExtractor = MediaExtractor()
        audioExtractor.setDataSource("$DEST_PATH${fileName1}_audio.mp3")
        var audioIndex = -1
        var audioFormat: MediaFormat? = null
        for (i in 0 until audioExtractor.trackCount) {
            audioFormat = audioExtractor.getTrackFormat(i)
            if (audioFormat?.getString(MediaFormat.KEY_MIME)?.startsWith("audio/") == true) {
                audioIndex = i
            }
        }
        var writeAudioIndex = -1
        if (audioFormat != null) {
            writeAudioIndex = muxer.addTrack(audioFormat)
        }
        audioExtractor.selectTrack(audioIndex)
        Log.e("TAG", "muxerVideoAudio: $writeVideoIndex,$writeAudioIndex,$writeAudioIndex2")
        //启动前，要确定所有添加的轨道
        muxer.start()

        val totalTime = muxerVideo(muxer,videoExtractor,videoFormat!!,writeVideoIndex)
        muxerAudio(muxer,totalTime,audioExtractor2,audioFormat2!!,writeAudioIndex2)
        muxerAudio(muxer,totalTime,audioExtractor,audioFormat!!,writeAudioIndex)

        muxer.stop()
        muxer.release()

        val videoExtractorCompose = MediaExtractor()
        videoExtractorCompose.setDataSource("$DEST_PATH${fileName}_compose.mp4")
        Log.e("TAG", "muxerVideoAudio: 合成成功------轨道数量：${videoExtractorCompose.trackCount}")
        videoExtractorCompose.release()



    }

    fun muxerVideo(muxer: MediaMuxer,videoExtractor: MediaExtractor,videoFormat: MediaFormat,writeVideoIndex:Int):Long{

        val videoBufferInfo = MediaCodec.BufferInfo()

        val videoByteBuffer =
            ByteBuffer.allocate(videoFormat!!.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE))

        //
        videoExtractor.readSampleData(videoByteBuffer, 0)
        if (videoExtractor.sampleFlags == MediaExtractor.SAMPLE_FLAG_SYNC) {
            videoExtractor.advance()
        }
        videoExtractor.readSampleData(videoByteBuffer, 0)
        var videoSecondTime = videoExtractor.sampleTime
        videoExtractor.advance()
        var videoThirdTime = videoExtractor.sampleTime
        var videoTimeStamp = Math.abs(videoSecondTime.minus(videoThirdTime))
        //

//        videoExtractor.unselectTrack(videoIndex)
//        videoExtractor.selectTrack(videoIndex)
        var videoSampleSize = -1
        var videoPresentationTimeUs = -1L
        while (true) {
            videoSampleSize = videoExtractor.readSampleData(videoByteBuffer, 0)
            if (videoSampleSize < 0) {
                break
            }
            videoBufferInfo.size = videoSampleSize
            videoBufferInfo.presentationTimeUs += videoTimeStamp
            videoBufferInfo.offset = 0
            videoBufferInfo.flags = videoExtractor.sampleFlags
            Log.e(
                "TAG",
                "muxerVideo: presentationTimeUs=${videoBufferInfo.presentationTimeUs}," +
                        "videoSampleTime=${videoTimeStamp}," +
                        "videoBufferSize=${videoBufferInfo.size}," +
                        "flag=${videoBufferInfo.flags}"
            )
            videoPresentationTimeUs = videoBufferInfo.presentationTimeUs
            muxer.writeSampleData(writeVideoIndex, videoByteBuffer, videoBufferInfo)
            videoExtractor.advance()
        }
        videoExtractor.release()
        return videoPresentationTimeUs

    }

    fun muxerAudio(muxer: MediaMuxer,videoPresentationTimeUs:Long,audioExtractor:MediaExtractor,audioFormat: MediaFormat,writeAudioIndex: Int){

        val audioBufferInfo = MediaCodec.BufferInfo()

        val audioByteBuffer =
            ByteBuffer.allocate(audioFormat!!.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE))

        //
        audioExtractor.readSampleData(audioByteBuffer, 0)
        if (audioExtractor.sampleFlags == MediaExtractor.SAMPLE_FLAG_SYNC) {
            audioExtractor.advance()
        }
        audioExtractor.readSampleData(audioByteBuffer, 0)
        val audioSecondTime = audioExtractor.sampleTime
        audioExtractor.advance()
        val audioThirdTime = audioExtractor.sampleTime
        val audioTimeStamp = Math.abs(audioSecondTime.minus(audioThirdTime))
        //

//        audioExtractor.unselectTrack(audioIndex)
//        audioExtractor.selectTrack(audioIndex)
        var audioSampleSize = -1
        while (true) {
            audioSampleSize = audioExtractor.readSampleData(audioByteBuffer, 0)
            if (audioSampleSize < 0 ||
                videoPresentationTimeUs <= audioBufferInfo.presentationTimeUs) {//以视频时长为准
                break
            }
            audioBufferInfo.size = audioSampleSize
            audioBufferInfo.presentationTimeUs += audioTimeStamp
            audioBufferInfo.offset = 0
            audioBufferInfo.flags = audioExtractor.sampleFlags
            Log.e(
                "TAG",
                "muxerAudio: presentationTimeUs=${audioBufferInfo.presentationTimeUs}," +
                        "videoSampleTime=${audioTimeStamp}," +
                        "videoBufferSize=${audioBufferInfo.size}," +
                        "flag=${audioBufferInfo.flags}"
            )
            muxer.writeSampleData(writeAudioIndex, audioByteBuffer, audioBufferInfo)
            audioExtractor.advance()

        }

        audioExtractor.release()
    }

    private fun separate() {
        mFile = File("$SDCARD_PATH$fileName.mp4")
        if (!mFile.exists()) {
            Log.e("TAG", "mp4文件不存在")
            return
        }
        val extractor = MediaExtractor() //实例一个MediaExtractor
        try {
            Log.e("TAG", "separate: abusolutePath=${mFile.absolutePath}")
            extractor.setDataSource(mFile.getAbsolutePath()) //设置添加MP4文件路径
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val trackCount = extractor.trackCount //获得通道数量
        var videoTrackIndex = 0 //视频轨道索引
        var videoMediaFormat: MediaFormat? = null //视频格式
        var audioTrackIndex = 0 //音频轨道索引
        var audioMediaFormat: MediaFormat? = null
        /**
         * 查找需要的视频轨道与音频轨道index
         */
        for (i in 0 until trackCount) { //遍历所以轨道
            val itemMediaFormat = extractor.getTrackFormat(i)
            val itemMime = itemMediaFormat.getString(MediaFormat.KEY_MIME)
            if (itemMime!!.startsWith("video")) { //获取视频轨道位置
                videoTrackIndex = i
                videoMediaFormat = itemMediaFormat
                continue
            }
            if (itemMime.startsWith("audio")) { //获取音频轨道位置
                audioTrackIndex = i
                audioMediaFormat = itemMediaFormat
                continue
            }
        }
        val videoFile = File("${DEST_PATH}video.mp4")
        val audioFile = File("${DEST_PATH}audio.acc")
        if (videoFile.exists()) {
            videoFile.delete()
        }
        if (audioFile.exists()) {
            audioFile.delete()
        }
        try {
            val videoOutputStream = FileOutputStream(videoFile)
            val audioOutputStream = FileOutputStream(audioFile)

            /**
             * 分离视频
             */
            val maxVideoBufferCount =
                videoMediaFormat!!.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE) //获取视频的输出缓存的最大大小
            val videoByteBuffer = ByteBuffer.allocate(maxVideoBufferCount)
            extractor.selectTrack(videoTrackIndex) //选择到视频轨道
            var len = 0
            while (extractor.readSampleData(videoByteBuffer, 0).also { len = it } != -1) {
                val bytes = ByteArray(len)
                videoByteBuffer.get(bytes) //获取字节
                videoOutputStream.write(bytes) //写入字节
                videoByteBuffer.clear()
                extractor.advance() //预先加载后面的数据
            }
            videoOutputStream.flush()
            videoOutputStream.close()
            extractor.unselectTrack(videoTrackIndex) //取消选择视频轨道
            Log.e("TAG", "separate: 分离视频完成---------")
            /**
             * 分离音频
             */
            val maxAudioBufferCount =
                audioMediaFormat!!.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE) //获取音频的输出缓存的最大大小
            val audioByteBuffer = ByteBuffer.allocate(maxAudioBufferCount)
            extractor.selectTrack(audioTrackIndex) //选择音频轨道
            len = 0
            while (extractor.readSampleData(audioByteBuffer, 0).also { len = it } != -1) {
                val bytes = ByteArray(len)
                audioByteBuffer.get(bytes)
                /**
                 * 添加adts头
                 */
                val adtsData = ByteArray(len + 7)
                addADTStoPacket(adtsData, len + 7)
                System.arraycopy(bytes, 0, adtsData, 7, len)
                audioOutputStream.write(bytes)
                audioByteBuffer.clear()
                extractor.advance()
            }
            audioOutputStream.flush()
            audioOutputStream.close()
        } catch (e: FileNotFoundException) {
            Log.e("TAG", "separate: 错误原因=$e")
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        extractor.release() //释放资源
    }

    private fun addADTStoPacket(packet: ByteArray, packetLen: Int) {
        /*
        标识使用AAC级别 当前选择的是LC
        一共有1: AAC Main 2:AAC LC (Low Complexity) 3:AAC SSR (Scalable Sample Rate) 4:AAC LTP (Long Term Prediction)
        */
        val profile = 2
        val frequencyIndex = 0x04 //设置采样率
        val channelConfiguration = 2 //设置频道,其实就是声道

        // fill in ADTS data
        packet[0] = 0xFF.toByte()
        packet[1] = 0xF9.toByte()
        packet[2] =
            ((profile - 1 shl 6) + (frequencyIndex shl 2) + (channelConfiguration shr 2)).toByte()
        packet[3] = ((channelConfiguration and 3 shl 6) + (packetLen shr 11)).toByte()
        packet[4] = (packetLen and 0x7FF shr 3).toByte()
        packet[5] = ((packetLen and 7 shl 5) + 0x1F).toByte()
        packet[6] = 0xFC.toByte()
    }

}