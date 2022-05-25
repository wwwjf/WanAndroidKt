package com.xianghe.ivy.model;

import java.io.Serializable;

public class MemberTeamBean implements Serializable {
    private static final long serialVersionUID = 5366054526900180433L;


    /**
     *  userName  : xxx
     *  recommendNum  : xxxx
     *  earnings  : xxx
     *  creatEarnings  : xxx
     *  userid  : xxx
     * avatar : xxx
     *  creatTime  : xxx
     * earningType : xxx
     */

    private String userName;
    private String recommendNum;
    private String earnings;
    private String creatEarnings;
    private String userid;
    private String avatar;
    private String creatTime;
    private String earningType;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRecommendNum() {
        return recommendNum;
    }

    public void setRecommendNum(String recommendNum) {
        this.recommendNum = recommendNum;
    }

    public String getEarnings() {
        return earnings;
    }

    public void setEarnings(String earnings) {
        this.earnings = earnings;
    }

    public String getCreatEarnings() {
        return creatEarnings;
    }

    public void setCreatEarnings(String creatEarnings) {
        this.creatEarnings = creatEarnings;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(String creatTime) {
        this.creatTime = creatTime;
    }

    public String getEarningType() {
        return earningType;
    }

    public void setEarningType(String earningType) {
        this.earningType = earningType;
    }
}
