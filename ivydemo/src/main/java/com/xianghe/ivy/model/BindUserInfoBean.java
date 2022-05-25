package com.xianghe.ivy.model;

/**
 * @创建者 Allen
 * @创建时间 2019/4/25 15:53
 * @描述
 */
public class BindUserInfoBean {
    /**
     * openid : OPENID          普通用户的标识
     * nickname : NICKNAME      普通用户昵称
     * sex : 1                  普通用户性别，1为男性，2为女性
     * province : PROVINCE      普通用户个人资料填写的省份
     * city : CITY              普通用户个人资料填写的城市
     * country : COUNTRY        国家，如中国为CN
     */

    private String openid;
    private String nickname;
    private int sex;
    private String language;
    private String city;
    private String province;
    private String country;
    private String headimgurl;
    private String unionid;

    public String getOpenid() {
        return openid;
    }

    public String getNickname() {
        return nickname;
    }

    public int getSex() {
        return sex;
    }

    public String getLanguage() {
        return language;
    }

    public String getCity() {
        return city;
    }

    public String getProvince() {
        return province;
    }

    public String getCountry() {
        return country;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public String getUnionid() {
        return unionid;
    }
}
