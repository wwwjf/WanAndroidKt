package com.xianghe.ivy.entity.db;

import com.xianghe.ivy.manager.UserInfoManager;
import com.xianghe.ivy.model.Movie;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Transient;

import java.util.Objects;

@Entity
public class UploadTaskCache implements Movie {

    @Id(autoincrement = true)
    private Long id;

    @Property
    private String fileId; // 上传视频需要的id
    @Property
    private String filePath; //上传路径
    @Property
    private int type;   // 上传类型

    @Property
    private long uid;// 用户id
    @Property
    private String title;// 标题
    @Property
    private String coverUrl; // 封面
    @Property
    private String uploadName; //上传oss文件夹name
    @Property
    private String uploadUrl; //上传url
    @Property
    private String uploadDir; //上传dir,端点续传根据此目录寻找文件

    // 不需要存本地的数据
//    @Transient
//    private UploadManager.Status state; // 状态
    @Transient
    private long offset;
    @Transient
    private long totelLength;
    @Transient
    private long lastTimeMillis;

    // --------- 水印相关 start ------------
    @Property
    private boolean hasWaterMark;// 有无水印  根据此判断是否执行水印添加的逻辑
    @Property
    private String desc;// 概述
    @Property
    private String director;// 导演
    @Property
    private String player;// 演员
    @Property
    private String location;// 位置
    @Property
    private String outFilePath; //水印添加成功之后的path， 用处：在删除资源的时候，避免输出文件被删除，出现异常
    // --------- 水印相关 end ------------

    @Property
    private boolean hasAddHeaderInfo;// 有无执行ffmpeg的添加头部信息命令


    @Generated(hash = 713153111)
    public UploadTaskCache(Long id, String fileId, String filePath, int type, long uid, String title,
                           String coverUrl, String uploadName, String uploadUrl, String uploadDir, boolean hasWaterMark,
                           String desc, String director, String player, String location, String outFilePath,
                           boolean hasAddHeaderInfo) {
        this.id = id;
        this.fileId = fileId;
        this.filePath = filePath;
        this.type = type;
        this.uid = uid;
        this.title = title;
        this.coverUrl = coverUrl;
        this.uploadName = uploadName;
        this.uploadUrl = uploadUrl;
        this.uploadDir = uploadDir;
        this.hasWaterMark = hasWaterMark;
        this.desc = desc;
        this.director = director;
        this.player = player;
        this.location = location;
        this.outFilePath = outFilePath;
        this.hasAddHeaderInfo = hasAddHeaderInfo;
    }

    @Generated(hash = 1604158475)
    public UploadTaskCache() {
    }


    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileId() {
        return this.fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public long mediaId() {
        return getId();
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }

    @Override
    public String getCoverUrl() {
        return this.coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getUploadName() {
        return uploadName;
    }

    public void setUploadName(String uploadName) {
        this.uploadName = uploadName;
    }

    public String getUploadUrl() {
        return this.uploadUrl;
    }

    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    public long getOffset() {
        return this.offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public long getTotelLength() {
        return this.totelLength;
    }

    public void setTotelLength(long totelLength) {
        this.totelLength = totelLength;
    }

    public long getLastTimeMillis() {
        return lastTimeMillis;
    }

    public void setLastTimeMillis(long lastTimeMillis) {
        this.lastTimeMillis = lastTimeMillis;
    }


    public boolean isHasWaterMark() {
        return hasWaterMark;
    }

    public void setHasWaterMark(boolean hasWaterMark) {
        this.hasWaterMark = hasWaterMark;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOutFilePath() {
        return outFilePath;
    }

    public void setOutFilePath(String outFilePath) {
        this.outFilePath = outFilePath;
    }

    public boolean isHasAddHeaderInfo() {
        return hasAddHeaderInfo;
    }

    public void setHasAddHeaderInfo(boolean hasAddHeaderInfo) {
        this.hasAddHeaderInfo = hasAddHeaderInfo;
    }

    /*    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UploadTaskCache cache = (UploadTaskCache) o;
        return type == cache.type &&
                uid == cache.uid &&
                Objects.equals(fileId, cache.fileId) &&
                Objects.equals(filePath, cache.filePath) &&
                Objects.equals(title, cache.title) &&
                Objects.equals(coverUrl, cache.coverUrl) &&
                Objects.equals(uploadName, cache.uploadName) &&
                Objects.equals(uploadUrl, cache.uploadUrl) &&
                Objects.equals(uploadDir, cache.uploadDir);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileId, filePath, type, uid, title, coverUrl, uploadName, uploadUrl, uploadDir);
    }*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UploadTaskCache cache = (UploadTaskCache) o;
        return type == cache.type &&
                uid == cache.uid &&
                Objects.equals(fileId, cache.fileId) &&
                Objects.equals(title, cache.title) &&
                Objects.equals(uploadDir, cache.uploadDir) &&
                Objects.equals(desc, cache.desc) &&
                Objects.equals(director, cache.director) &&
                Objects.equals(player, cache.player);
    }

    @Override
    public int hashCode() {

        return Objects.hash(fileId, type, uid, title, uploadDir, desc, director, player);
    }

    public static UploadTaskCache build(String fileId, String filePath, int type, String title, String coverUrl,
                                        String description, String director, String player, String location) {
        UploadTaskCache cache = new UploadTaskCache();
        cache.setFileId(fileId);
        cache.setFilePath(filePath);
        cache.setType(type);
        cache.setUid(UserInfoManager.getUid());
        cache.setTitle(title);
        cache.setCoverUrl(coverUrl);
        cache.setDesc(description);
        cache.setDirector(director);
        cache.setPlayer(player);
        cache.setLocation(location);
        return cache;
    }

    public static UploadTaskCache copy(UploadTaskCache cache) {
        UploadTaskCache c = new UploadTaskCache();
        c.setFileId(cache.getFileId());
        c.setFilePath(cache.getFilePath());
        c.setType(cache.getType());
        c.setUid(cache.getUid());
        c.setTitle(cache.getTitle());
        c.setCoverUrl(cache.getCoverUrl());
        c.setUploadName(cache.getUploadName());
        c.setUploadUrl(cache.getUploadUrl());
        c.setUploadDir(cache.getUploadDir());
        c.setHasWaterMark(cache.isHasWaterMark());
        c.setDesc(cache.getDesc());
        c.setDirector(cache.getDirector());
        c.setPlayer(cache.getPlayer());
        c.setLocation(cache.getLocation());
        c.setOutFilePath(cache.getOutFilePath());
        c.setHasAddHeaderInfo(cache.isHasAddHeaderInfo());
        return c;
    }

   /* @Override
    public String toString() {
        return "UploadTaskCache{" +
                "fileId='" + fileId + '\'' +
                ", title='" + title + '\'' +
                ", uploadName='" + uploadName + '\'' +
                ", uploadDir='" + uploadDir + '\'' +
                ", offset=" + offset +
                ", totelLength=" + totelLength +
                '}';
    }*/

    @Override
    public String toString() {
        return "UploadTaskCache{" +
                "id=" + id +
                ", fileId='" + fileId + '\'' +
                ", filePath='" + filePath + '\'' +
                ", uid=" + uid +
                ", coverUrl='" + coverUrl + '\'' +
                ", uploadUrl='" + uploadUrl + '\'' +
                ", uploadDir='" + uploadDir + '\'' +
                ", hasWaterMark=" + hasWaterMark +
                ", hasAddHeaderInfo=" + hasAddHeaderInfo +
                ", outFilePath='" + outFilePath + '\'' +
                '}';
    }

    public boolean getHasWaterMark() {
        return this.hasWaterMark;
    }

    public boolean getHasAddHeaderInfo() {
        return this.hasAddHeaderInfo;
    }
}
