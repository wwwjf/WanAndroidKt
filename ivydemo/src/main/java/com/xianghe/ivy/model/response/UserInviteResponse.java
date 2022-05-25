package com.xianghe.ivy.model.response;

import java.io.Serializable;

public class UserInviteResponse implements Serializable {
    private static final long serialVersionUID = -8124226084022128794L;


    /**
     * status : 3  // 状态：1：已邀请，被邀请人未注册 2：已是亲友 3已邀请，被邀请人已注册
     * uid : 8272639 // 被邀请用户id：被邀请用户未注册时为0
     */

    private int status;
    private int uid;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }
}
