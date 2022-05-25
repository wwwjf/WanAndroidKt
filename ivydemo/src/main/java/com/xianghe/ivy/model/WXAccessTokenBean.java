package com.xianghe.ivy.model;

/**
 * @创建者 Allen
 * @创建时间 2019/4/25 15:34
 * @描述
 */
public class WXAccessTokenBean {

    /**
     * access_token : ACCESS_TOKEN          接口调用凭证
     * expires_in : 7200
     * refresh_token : REFRESH_TOKEN
     * openid : OPENID                      授权用户唯一标识
     * scope : SCOPE
     */

    private String access_token;
    private int expires_in;
    private String refresh_token;
    private String openid;
    private String scope;
    private String unionid;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
