package com.wwwjf.videodemo.media.decoder

import com.wwwjf.videodemo.media.BaseDecoder
import com.wwwjf.videodemo.media.Frame
import com.wwwjf.videodemo.media.IDecoderStateListener


/**
 * 默认解码状态监听器
 *
 * @author Chen Xiaoping (562818444@qq.com)
 * @since LearningVideo
 * @version LearningVideo
 *
 */
interface DefDecodeStateListener: IDecoderStateListener {
    override fun decoderPrepare(decodeJob: BaseDecoder?) {}
    override fun decoderReady(decodeJob: BaseDecoder?) {}
    override fun decoderRunning(decodeJob: BaseDecoder?) {}
    override fun decoderPause(decodeJob: BaseDecoder?) {}
    override fun decodeOneFrame(decodeJob: BaseDecoder?, frame: Frame) {}
    override fun decoderFinish(decodeJob: BaseDecoder?) {}
    override fun decoderDestroy(decodeJob: BaseDecoder?) {}
    override fun decoderError(decodeJob: BaseDecoder?, msg: String) {}
}