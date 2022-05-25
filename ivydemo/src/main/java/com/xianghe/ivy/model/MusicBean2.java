package com.xianghe.ivy.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MusicBean2 implements Serializable {

    private static final long serialVersionUID = -5654181805924074046L;
    /**
     * id : 18
     * music_name : serverSound
     * music_like : http://ivytest.i-weiying.com/c4ca/201810/20181011fd7c6324f69b23c3ba274982323d405d?auth_key=1552643573-0-0-aafcb35f11810f8d934770bdff3be73e
     * template_id : 18
     * time_length : 05:02
     */

    @SerializedName("id")
    private Long mId;
    @SerializedName("music_name")
    private String mMusicName;
    @SerializedName("music_like")
    private String mMusicLike;
    @SerializedName("template_id")
    private int mTemplateId;
    @SerializedName("time_length")
    private String mTimeLength;

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }

    public String getMusicName() {
        return mMusicName;
    }

    public void setMusicName(String musicName) {
        mMusicName = musicName;
    }

    public String getMusicLike() {
        return mMusicLike;
    }

    public void setMusicLike(String musicLike) {
        mMusicLike = musicLike;
    }

    public int getTemplateId() {
        return mTemplateId;
    }

    public void setTemplateId(int templateId) {
        mTemplateId = templateId;
    }

    public String getTimeLength() {
        return mTimeLength;
    }

    public void setTimeLength(String timeLength) {
        mTimeLength = timeLength;
    }
}
