package com.xianghe.ivy.ui.module.record.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class MovieItemModel implements Serializable {

    private static final long serialVersionUID = -5460969390346190011L;

    private int rotate;                                                                             //旋转角度

    private String filePath;                                                                        //文件路径

    private BigDecimal videoTime;                                                                   //视频的总时长

    private String filPicPath;                                                                      //图片的地址

    private Long date;                                                                              //当前的时间

    private int position;

    private int listPosition;

    private boolean video_from;                                                                     //视频来源

    private boolean isPlay;                                                                         //是否正在播放

    private int bitrate;                                                                         // bitrate

    public MovieItemModel() {

    }

    public MovieItemModel(String filePath, BigDecimal videoTime, String filPicPath) {
        this.filePath = filePath;
        this.videoTime = videoTime;
        this.filPicPath = filPicPath;
    }

    public MovieItemModel(String filePath, BigDecimal videoTime, String filPicPath,boolean video_from) {
        this.filePath = filePath;
        this.videoTime = videoTime;
        this.filPicPath = filPicPath;
        this.video_from = video_from;
    }

    public MovieItemModel(String filePath, BigDecimal videoTime) {
        this.filePath = filePath;
        this.videoTime = videoTime;
    }

    public MovieItemModel(String filePath, Long date) {
        this.filePath = filePath;
        this.date = date;
    }

    public boolean isPlay() {
        return isPlay;
    }

    public void setPlay(boolean play) {
        isPlay = play;
    }

    public int getListPosition() {
        return listPosition;
    }

    public boolean isVideo_from() {
        return video_from;
    }

    public void setVideo_from(boolean video_from) {
        this.video_from = video_from;
    }

    public void setListPosition(int listPosition) {
        this.listPosition = listPosition;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getFilPicPath() {
        return filPicPath;
    }

    public void setFilPicPath(String filPicPath) {
        this.filPicPath = filPicPath;
    }

    public int getRotate() {
        return rotate;
    }

    public void setRotate(int rotate) {
        this.rotate = rotate;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public BigDecimal getVideoTime() {
        return videoTime;
    }

    public void setVideoTime(BigDecimal videoTime) {
        this.videoTime = videoTime;
    }

    public int getBitrate() {
        return bitrate;
    }

    public void setBitrate(int bitrate) {
        this.bitrate = bitrate;
    }
}
