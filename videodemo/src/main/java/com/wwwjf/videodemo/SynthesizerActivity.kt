package com.wwwjf.videodemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Surface
import android.view.View
import com.cxp.learningvideo.media.DefDecoderStateListener
import com.cxp.learningvideo.media.encoder.DefEncodeStateListener
import com.cxp.learningvideo.media.encoder.VideoEncoder
import com.wwwjf.base.utils.DateUtil
import com.wwwjf.videodemo.databinding.ActivitySynthesizerBinding
import com.wwwjf.videodemo.media.BaseDecoder
import com.wwwjf.videodemo.media.Frame
import com.wwwjf.videodemo.media.IDecoder
import com.wwwjf.videodemo.media.decoder.AudioDecoder
import com.wwwjf.videodemo.media.decoder.DefDecodeStateListener
import com.wwwjf.videodemo.media.decoder.VideoDecoder
import com.wwwjf.videodemo.media.encoder.AudioEncoder
import com.wwwjf.videodemo.media.encoder.BaseEncoder
import com.wwwjf.videodemo.media.muxer.MMuxer
import com.wwwjf.videodemo.opengl.drawer.SoulVideoDrawer
import com.wwwjf.videodemo.opengl.drawer.VideoDrawer
import com.wwwjf.videodemo.opengl.egl.CustomerGLRenderer
import java.util.concurrent.Executors

class SynthesizerActivity : AppCompatActivity(), MMuxer.IMuxerStateListener {
    private val TAG = SynthesizerActivity::class.java.simpleName
    private lateinit var viewBinding: ActivitySynthesizerBinding
    private val path = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath}/Camera/8a554b2ce6a3d0ee34d534602429885d.mp4"

    private val threadPool = Executors.newFixedThreadPool(10)

    private var renderer = CustomerGLRenderer()

    private var audioDecoder: IDecoder? = null
    private var videoDecoder: IDecoder? = null

    private lateinit var videoEncoder: VideoEncoder
    private lateinit var audioEncoder: AudioEncoder

    private lateinit var muxer:MMuxer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySynthesizerBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val filePath = "${externalMediaDirs[0].absolutePath}/${DateUtil.format(System.currentTimeMillis())}.mp4"
        muxer = MMuxer(filePath)
        muxer.setStateListener(this)
    }

    fun synthesizer(view: View) {
        viewBinding.btnActivitySynthesizer.text = "正在编码..."
        viewBinding.btnActivitySynthesizer.isEnabled = false
        initVideo()
        initAudio()
        initAudioEncoder()
        initVideoEncoder()
    }

    private fun initVideo() {
        val drawer = SoulVideoDrawer()
        drawer.setVideoSize(1920,1080)
        drawer.getSurfaceTexture {
            initVideoDecoder(path,Surface(it))
        }
        renderer.addDrawer(drawer)

    }

    private fun initVideoDecoder(path: String, surface: Surface) {
        videoDecoder?.stop()
        videoDecoder = VideoDecoder(path,null,surface).withoutSync()
        videoDecoder!!.setStateListener(object :DefDecodeStateListener{
            override fun decodeOneFrame(decodeJob: BaseDecoder?, frame: Frame) {
                super.decodeOneFrame(decodeJob, frame)
                renderer.notifySwap(frame.bufferInfo.presentationTimeUs)
                videoEncoder.encodeOneFrame(frame)
            }

            override fun decoderFinish(decodeJob: BaseDecoder?) {
                super.decoderFinish(decodeJob)
                videoEncoder.endOfStream()
            }
        })
        videoDecoder!!.goOn()
        //启动解码线程
        threadPool.execute(videoDecoder)

    }

    private fun initAudio() {

        audioDecoder?.stop()
        audioDecoder = AudioDecoder(path).withoutSync()
        audioDecoder!!.setStateListener(object :DefDecoderStateListener{
            override fun decodeOneFrame(decodeJob: BaseDecoder?, frame: Frame) {
                super.decodeOneFrame(decodeJob, frame)
                audioEncoder.encodeOneFrame(frame)
            }

            override fun decoderFinish(decodeJob: BaseDecoder?) {
                super.decoderFinish(decodeJob)
                audioEncoder.endOfStream()
            }
        })
        audioDecoder!!.goOn()

        threadPool.execute(audioDecoder)

    }

    private fun initAudioEncoder() {
        audioEncoder = AudioEncoder(muxer)
        threadPool.execute(audioEncoder)

    }

    private fun initVideoEncoder() {

        videoEncoder = VideoEncoder(muxer,1920,1080)
        renderer.setRenderMode(CustomerGLRenderer.RenderMode.RENDER_WHEN_DIRTY)
        renderer.setSurface(videoEncoder.getEncodeSurface()!!,1920,1080)
        videoEncoder.setStateListener(object :DefEncodeStateListener{
            override fun encoderFinish(encoder: BaseEncoder) {
                renderer.stop()
            }
        })
        threadPool.execute(videoEncoder)

    }

    override fun onMuxerStart() {
        Log.e(TAG, "onMuxerStart: 开始合成...")
    }

    override fun onMuxerFinish() {
        runOnUiThread {
            viewBinding.btnActivitySynthesizer.isEnabled = true
            viewBinding.btnActivitySynthesizer.text = "编码完成"
        }

        audioDecoder?.stop()
        videoDecoder?.stop()
        audioDecoder = null
        videoDecoder = null
    }
}