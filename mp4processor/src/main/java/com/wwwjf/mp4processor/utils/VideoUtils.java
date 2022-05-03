package com.wwwjf.mp4processor.utils;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.Gravity;


import androidx.annotation.DrawableRes;

import com.wwwjf.mp4processor.av.Mp4Processor;
import com.wwwjf.mp4processor.core.Renderer;
import com.wwwjf.mp4processor.cut.VideoCrop;
import com.wwwjf.mp4processor.gl.BaseFilter;
import com.wwwjf.mp4processor.gl.WaterMarkFilter;

import java.io.IOException;

public class VideoUtils {

    /**
     * 添加水印
     */
    public static Mp4Processor start(String srcVideoFile,
                                     String dstVideoFile,
                                     @DrawableRes final int logoResource, // 右上角logo
                                     @DrawableRes final int watermarkResource, // 中间水印logo
                                     @DrawableRes final int locationResource, // 位置logo
                                     final String[] director, // 导演
                                     final String[] desc, // 演员
                                     final String sumText, // 概述
                                     final String title, // 标题
                                     final String location, // 位置
                                     final Context context,
                                     Mp4Processor.OnProgressListener progressListener) throws IOException {
        srcVideoFile = srcVideoFile.replace("//", "/");
        final Mp4Processor processor = new Mp4Processor();
        processor.setOutputPath(dstVideoFile);
        processor.setInputPath(srcVideoFile);
        processor.setOnCompleteListener(progressListener);
        processor.setRenderer(new Renderer() {
            BaseFilter mFilter;

            @Override
            public void create() {
                mFilter = new WaterMarkFilter();
                ((WaterMarkFilter) mFilter)
                        .setBitmapLogo(BitmapFactory.decodeResource(context.getResources(), logoResource)) // 设置logo
                        .setPositionLogo(50, 20) // logo位置
                        .setGravityLogo(Gravity.RIGHT) // 设置logo方向
                        .setBitmapDirector(context, watermarkResource, director, desc) // 中间水印
                        .setTextOverView(context, sumText) // 概述
                        .setTextTitleAndLocation(context, title, location, locationResource);
                mFilter.create();
            }

            @Override
            public void sizeChanged(int width, int height) {
                mFilter.sizeChanged(width, height);
            }

            @Override
            public void draw(int texture) {
                mFilter.draw(texture, processor.getPresentationTime());
            }

            @Override
            public void destroy() {
                mFilter.destroy();
            }
        });
        processor.start(context);
        return processor;
    }

    /**
     * 停止添加水印
     */
    public static void stop(Mp4Processor processor) throws InterruptedException {
        if (processor != null) {
            processor.release();
        }
    }

    /**
     * 开始视频裁剪
     */
    public static VideoCrop startVideoCut(Context context,
                                          String inputVideoPath,
                                          String outputVideoPath,
                                          long startTime,  // 单位mms
                                          long endTime, // 单位mms
                                          boolean swipeWH, // 是否置换宽高
                                          VideoCrop.OnEncoderListener listener) {
        inputVideoPath = inputVideoPath.replace("//", "/");
        outputVideoPath = outputVideoPath.replace("//", "/");
        VideoCrop crop = new VideoCrop(context);
        crop.setEncoderListener(listener);
        crop.start(inputVideoPath, outputVideoPath, startTime, endTime,swipeWH);
        return crop;
    }

    /**
     * 停止视频裁剪
     */
    public static void stopVideoCut(VideoCrop videoCrop) {
        if (videoCrop != null) {
            videoCrop.stop();
        }
    }
}