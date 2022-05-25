package com.xianghe.ivy.entity.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import org.greenrobot.greendao.annotation.Generated;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class MovieItemDb implements Serializable {

    private static final long serialVersionUID = 9100570029689628346L;

    @Id(autoincrement = true)
    private Long id;

    private String uid;

    private long key;

    private String filePath;                                                                        //文件路径

    private Double videoTime;                                                                       //视频的总时长

    private String filPicPath;                                                                      //图片的地址

    private int from;                                                                               //从哪里进来

    private String musicPath;                                                                       //音乐的路径

    private String voicePath;                                                                       //录音的路径

    private boolean video_from;                                                                      //视频来源本地还是什么

    private String video_name;                                                                      //影片名称

    private String video_desc;                                                                      //影片描述

    private String video_types;                                                                     //影片类别

    private String video_director;                                                                  //影片导演

    private String video_actor;                                                                     //影片演员

    private String location;                                                                        //定位地址

    private String musicName;                                                                       //音乐名称

    private String musicId;                                                                       //音乐id

    private int push_type;                                                                          //发布类型

    private int rotate;                                                                             //旋转角度

    private String picList;                                                                         //照片的地址

    private boolean picToMovie;                                                                     //是否是照片合成视频

    private int templetType;                                                                        //模板的type

    private int isScreenRecord = 0;                                                                 //是否录屏视频 0-不是，1-是






    @Generated(hash = 2043243493)
    public MovieItemDb(Long id, String uid, long key, String filePath, Double videoTime, String filPicPath, int from,
            String musicPath, String voicePath, boolean video_from, String video_name, String video_desc,
            String video_types, String video_director, String video_actor, String location, String musicName,
            String musicId, int push_type, int rotate, String picList, boolean picToMovie, int templetType,
            int isScreenRecord) {
        this.id = id;
        this.uid = uid;
        this.key = key;
        this.filePath = filePath;
        this.videoTime = videoTime;
        this.filPicPath = filPicPath;
        this.from = from;
        this.musicPath = musicPath;
        this.voicePath = voicePath;
        this.video_from = video_from;
        this.video_name = video_name;
        this.video_desc = video_desc;
        this.video_types = video_types;
        this.video_director = video_director;
        this.video_actor = video_actor;
        this.location = location;
        this.musicName = musicName;
        this.musicId = musicId;
        this.push_type = push_type;
        this.rotate = rotate;
        this.picList = picList;
        this.picToMovie = picToMovie;
        this.templetType = templetType;
        this.isScreenRecord = isScreenRecord;
    }

    @Generated(hash = 1194537604)
    public MovieItemDb() {
    }




    

    public int getTempletType() {
        return templetType;
    }

    public void setTempletType(int templetType) {
        this.templetType = templetType;
    }

    public String getPicList() {
        return picList;
    }

    public void setPicList(String picList) {
        this.picList = picList;
    }

    public boolean isPicToMovie() {
        return picToMovie;
    }

    public void setPicToMovie(boolean picToMovie) {
        this.picToMovie = picToMovie;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getMusicId() {
        return musicId;
    }

    public void setMusicId(String musicId) {
        this.musicId = musicId;
    }

    public int getRotate() {
        return rotate;
    }

    public void setRotate(int rotate) {
        this.rotate = rotate;
    }

    public int getPush_type() {
        return push_type;
    }

    public void setPush_type(int push_type) {
        this.push_type = push_type;
    }

    public String getVideo_types() {
        return video_types;
    }

    public void setVideo_types(String video_types) {
        this.video_types = video_types;
    }

    public String getVideo_director() {
        return video_director;
    }

    public void setVideo_director(String video_director) {
        this.video_director = video_director;
    }

    public String getVideo_actor() {
        return video_actor;
    }

    public void setVideo_actor(String video_actor) {
        this.video_actor = video_actor;
    }

    public boolean isVideo_from() {
        return video_from;
    }

    public String getVideo_name() {
        return video_name;
    }

    public void setVideo_name(String video_name) {
        this.video_name = video_name;
    }

    public String getVideo_desc() {
        return video_desc;
    }

    public void setVideo_desc(String video_desc) {
        this.video_desc = video_desc;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean getVideo_from() {
        return video_from;
    }

    public void setVideo_from(boolean video_from) {
        this.video_from = video_from;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public String getMusicPath() {
        return musicPath;
    }

    public void setMusicPath(String musicPath) {
        this.musicPath = musicPath;
    }

    public String getVoicePath() {
        return voicePath;
    }

    public void setVoicePath(String voicePath) {
        this.voicePath = voicePath;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getKey() {
        return this.key;
    }

    public void setKey(long key) {
        this.key = key;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Double getVideoTime() {
        return this.videoTime;
    }

    public void setVideoTime(Double videoTime) {
        this.videoTime = videoTime;
    }

    public String getFilPicPath() {
        return this.filPicPath;
    }

    public void setFilPicPath(String filPicPath) {
        this.filPicPath = filPicPath;
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean getPicToMovie() {
        return this.picToMovie;
    }

    public int getIsScreenRecord() {
        return isScreenRecord;
    }

    public void setIsScreenRecord(int isScreenRecord) {
        this.isScreenRecord = isScreenRecord;
    }
}
