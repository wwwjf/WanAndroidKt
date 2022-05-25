package com.xianghe.ivy.model;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;

import java.io.Serializable;

/**
 * Created by lulei-ms on 2017/8/23.
 */

public class LocalVideoBean implements Serializable{
    public Bitmap coverImage;
    public long duration; //单位：ms
    public String src_path;
    public long fileSize;
    public int fps;
    public int bitrate;
    public int width;
    public int height;

    public Bitmap getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(Bitmap coverImage) {
        this.coverImage = coverImage;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getSrc_path() {
        return src_path;
    }

    public void setSrc_path(String src_path) {
        this.src_path = src_path;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public int getFps() {
        return fps;
    }

    public void setFps(int fps) {
        this.fps = fps;
    }

    public int getBitrate() {
        return bitrate;
    }

    public void setBitrate(int bitrate) {
        this.bitrate = bitrate;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "LocalVideoBean{" +
                " duration=" + duration +
                ", src_path='" + src_path + '\'' +
                ", fileSize=" + fileSize +
                ", fps=" + fps +
                ", bitrate=" + bitrate +
                ", width=" + width +
                ", height=" + height +
                '}';
    }

    /**
     * 获取本地视频信息
     */
    public static LocalVideoBean getLocalVideoInfo(String path) {
        LocalVideoBean info = new LocalVideoBean();
        info.src_path = path;

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try {
            mmr.setDataSource(path);
            info.duration = (Long.valueOf(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)));
            info.width = Integer.valueOf(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            info.height = Integer.valueOf(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mmr.release();
        }
        return info;
    }
}
