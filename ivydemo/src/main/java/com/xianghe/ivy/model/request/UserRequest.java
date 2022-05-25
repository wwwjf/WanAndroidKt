package com.xianghe.ivy.model.request;

import java.io.Serializable;

/**
 * 注册、密码登录、手机验证码登录、微信登录、重置密码相关请求参数
 */
public class UserRequest implements Serializable {
    private static final long serialVersionUID = 8299715797380613282L;
    /**
     * 手机号码
     */
    private String mobile;
    /**
     * 密码
     */
    private String smscode;
    /**
     * 密码（密码必须为6-16位字母数字符号组合）
     */
    private String password;

    /**
     * 推送设备id
     */
    private String registerId;

    /**
     * 微信登录授权码
     */
    private String code;

    /**
     * facebook 登录token
     */
    private String access_token;

    /**
     * 邀请码
     */
    private String inviteCode="";


    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getSmscode() {
        return smscode;
    }

    public void setSmscode(String smscode) {
        this.smscode = smscode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRegisterId() {
        return registerId;
    }

    public void setRegisterId(String registerId) {
        this.registerId = registerId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }
}
