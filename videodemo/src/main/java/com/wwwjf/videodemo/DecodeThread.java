package com.wwwjf.videodemo;

import android.media.MediaCodec;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class DecodeThread implements Runnable {
    public static final String TAG = DecodeThread.class.getSimpleName();
    private final MediaCodec mMediaCodec;
    private final DataInputStream mInputStream;

    public DecodeThread(MediaCodec mediaCodec, DataInputStream inputStream){
        mMediaCodec = mediaCodec;
        mInputStream = inputStream;
    }

    @Override
    public void run() {
        decode();
    }

    private void decode() {
        // 获取一组缓存区(8个)
        ByteBuffer[] inputBuffers = mMediaCodec.getInputBuffers();
        // 解码后的数据，包含每一个buffer的元数据信息
        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        // 获取缓冲区的时候，需要等待的时间(单位：毫秒)
        long timeoutUs = 10000;
        byte[] streamBuffer = null;
        int bytes_cnt = 0;
        try {
            // 返回可用的字节数组
            streamBuffer = getBytes(mInputStream);
            // 得到可用字节数组长度
            bytes_cnt = streamBuffer.length;
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 没有得到可用数组
        if (bytes_cnt == 0) {
            streamBuffer = null;
        }
        // 每帧的开始位置
        int startIndex = 0;
        // 定义记录剩余字节的变量
        int remaining = bytes_cnt;
        // while(true)大括号内的内容是获取一帧，解码，然后显示；直到获取最后一帧，解码，结束
        while (true) {
            // 当剩余的字节=0或者开始的读取的字节下标大于可用的字节数时  不在继续读取
            if (remaining == 0 || startIndex >= remaining) {
                break;
            }

            // 寻找帧头部
            int nextFrameStart = findHeadFrame(streamBuffer, startIndex + 2, remaining);

            // 找不到头部返回-1
            if (nextFrameStart == -1) {
                nextFrameStart = remaining;
            }
            // 得到可用的缓存区
            int inputIndex = mMediaCodec.dequeueInputBuffer(timeoutUs);
            // 有可用缓存区
            if (inputIndex >= 0) {
                ByteBuffer byteBuffer = inputBuffers[inputIndex];
                byteBuffer.clear();
                // 将可用的字节数组(一帧)，传入缓冲区
                byteBuffer.put(streamBuffer, startIndex, nextFrameStart - startIndex);
                // 把数据传递给解码器
                mMediaCodec.queueInputBuffer(inputIndex, 0, nextFrameStart - startIndex, 0, 0);
                // 指定下一帧的位置
                startIndex = nextFrameStart;
            } else {
                continue;
            }

            int outputIndex = mMediaCodec.dequeueOutputBuffer(info, timeoutUs);
            if (outputIndex >= 0) {
                // 加入try catch的目的是让界面显示的慢一点，这个步骤可以省略
                try {
                    Thread.sleep(24);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 将处理过的数据交给surfaceview显示
                mMediaCodec.releaseOutputBuffer(outputIndex, true);
            } else if (outputIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED){
                //可以得到实际分辨率，修改宽高比
                Log.e(TAG, "decode: INFO_OUTPUT_FORMAT_CHANGED");
//                fixHW();
            }
        }
    }

    /**
     * 查找帧头部的位置
     * 在实际的H264数据帧中，往往帧前面带有00 00 00 01 或 00 00 01分隔符
     * @param bytes 数据
     * @param start 开始位置
     * @param totalSize 总和
     * @return 返回位置
     */
    private int findHeadFrame(byte[] bytes, int start, int totalSize) {
        for (int i = start; i < totalSize - 4; i++) {
            if (((bytes[i] == 0x00) && (bytes[i + 1] == 0x00) && (bytes[i + 2] == 0x00) && (bytes[i + 3] == 0x01)) || ((bytes[i] == 0x00) && (bytes[i + 1] == 0x00) && (bytes[i + 2] == 0x01))) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 获得可用的字节数组
     * @param is
     * @return
     * @throws IOException
     */
    public static byte[] getBytes(InputStream is) throws IOException {
        int len;
        int size = 1024;
        byte[] buf;

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        buf = new byte[size];
        while ((len = is.read(buf, 0, size)) != -1) {
            // 将读取的数据写入到字节输出流
            bos.write(buf, 0, len);
        }
        // 将这个流转换成字节数组
        buf = bos.toByteArray();
        return buf;
    }


}
