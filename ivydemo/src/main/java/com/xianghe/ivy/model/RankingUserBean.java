package com.xianghe.ivy.model;

import com.google.gson.annotations.SerializedName;

public class RankingUserBean {
    /**
     * one_self : 0
     * ranking : 1
     * like_num : 3
     * uid : 8272649
     * name : 陆俊
     * update_date : 03-28 09:40:11
     */

    @SerializedName("one_self")
    private int oneSelf;
    @SerializedName("ranking")
    private int ranking;
    @SerializedName("like_num")
    private int likeNum;
    @SerializedName("uid")
    private long uid;
    @SerializedName("name")
    private String name;
    @SerializedName("avatar")
    private String avatar;
    @SerializedName("update_date")
    private String updateDate;

    public int getOneSelf() {
        return oneSelf;
    }

    public void setOneSelf(int oneSelf) {
        this.oneSelf = oneSelf;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public int getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(int likeNum) {
        this.likeNum = likeNum;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }
}