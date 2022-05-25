package com.xianghe.ivy.model;

import java.io.Serializable;

public class FriendBean implements Serializable {
    private static final long serialVersionUID = 6619819689094962169L;


    /**
     * id : 8272430
     * mobile : 18100000001
     * avatar : http://www.logoquan.com/upload/list/20180421/logoquan15329711299.PNG
     * vip_level : 普通用户
     */

    private int id;
    /**
     * 申请人微影号
     */
    private long apply_uid;
    private long uid;
    /**
     * 申请人昵称
     */
    private String name;
    private String avatar;
    /**
     * 亲友圈申请列表状态  其他值：未同意   1：同意 2:已申请（本地联系人列表为已邀请状态）
     * 本地通讯录列表 1:已邀请，但是还未注册 2:已是亲友
     */
    private int status;
    private String vip_level;
    private String created_at;

    private String mobile;

    /**
     * 同一个号码一天内只能邀请一次
     * 是否允许再次邀请：0=不允许  1=允许再次邀请
     */
    private int invite_allow;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getVip_level() {
        return vip_level;
    }

    public void setVip_level(String vip_level) {
        this.vip_level = vip_level;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public long getApply_uid() {
        return apply_uid;
    }

    public void setApply_uid(long apply_uid) {
        this.apply_uid = apply_uid;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getInvite_allow() {
        return invite_allow;
    }

    public void setInvite_allow(int invite_allow) {
        this.invite_allow = invite_allow;
    }
}
