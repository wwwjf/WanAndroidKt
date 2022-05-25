package com.xianghe.ivy.model;

import java.io.Serializable;

public class OpeninstallBean implements Serializable {
    private static final long serialVersionUID = 3347304219987742565L;


    /**
     * id : 36
     * uid : 8273250
     * channel : wx
     * inviteCode : 123456
     */

    /**
     * 视频id
     */
    private String id;
    private String uid;
    private String channel;
    /**
     * 邀请人邀请码
     */
    private String inviteCode;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }
}
