package com.xianghe.ivy.model.request;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 影片上传内容
 */
public class MovieUploadRequest implements Serializable {

    private static final long serialVersionUID = -7276250505423912834L;

    /**
     * 标题
     */
    private String title;

    /**
     * 编剧，以’,’分隔，例如：’张三,李四’，主演和导演类似
     */
    private String screenwriter;

    /**
     * 主演
     */
    private String staring;

    /**
     * 导演
     */
    private String director;

    /**
     * 封面图
     */
    private String media;

    /**
     * 时长 （单位：秒）
     */
    private int length;

    /**
     * 位置信息
     */
    private String location;

    /**
     * 私密收藏：1 公开发布：0 亲友圈发布：2
     */
    @SerializedName("private")
    private int privates;

    /**
     * 原创：1 转发：0
     */
    private int owner;

    /**
     * 选择的标签 id，以英文逗号分隔
     */
    private String tags;

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getScreenwriter() {
        return screenwriter;
    }

    public void setScreenwriter(String screenwriter) {
        this.screenwriter = screenwriter;
    }

    public String getStaring() {
        return staring;
    }

    public void setStaring(String staring) {
        this.staring = staring;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getPrivates() {
        return privates;
    }

    public void setPrivates(int privates) {
        this.privates = privates;
    }

    public int getOwner() {
        return owner;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "MovieUploadRequest{" +
                "title='" + title + '\'' +
                ", screenwriter='" + screenwriter + '\'' +
                ", staring='" + staring + '\'' +
                ", director='" + director + '\'' +
                ", media='" + media + '\'' +
                ", length=" + length +
                ", location='" + location + '\'' +
                ", privates=" + privates +
                ", owner=" + owner +
                ", tags='" + tags + '\'' +
                '}';
    }
}
