package com.xianghe.ivy.model.response;

import java.io.Serializable;

public class UserTokenBean implements Serializable {

    private static final long serialVersionUID = 1324046915323549224L;

    /**
     * ticket : C540D115-5F28-4DA1-99E4-D77CFD46DC6E
     * uid : 62400
     */

    private String ticket;
    private long uid;
    private String tags;
    private String mobile;
    private String fb_uid;
    private String avatar;
    private String hx_username;
    private String hx_password;

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getFb_uid() {
        return fb_uid;
    }

    public void setFb_uid(String fb_uid) {
        this.fb_uid = fb_uid;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getHx_username() {
        return hx_username;
    }

    public void setHx_username(String hx_username) {
        this.hx_username = hx_username;
    }

    public String getHx_password() {
        return hx_password;
    }

    public void setHx_password(String hx_password) {
        this.hx_password = hx_password;
    }
}
