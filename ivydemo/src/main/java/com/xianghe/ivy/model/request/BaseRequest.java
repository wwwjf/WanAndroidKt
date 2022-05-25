package com.xianghe.ivy.model.request;

import java.io.Serializable;

/**
 * 接口公共请求参数（已使用拦截器设置）
 */
public class BaseRequest implements Serializable{
    private static final long serialVersionUID = 7833377136021903703L;

    /**
     * 用户 token
     */
    private String ticket;

    /**
     *  请求源（None = 0,Android = 1,IOS = 2,Web = 3）
     */
    private String ClientSource = "1";

    /**
     * APP端传系统版本信息 每更新一个版本加1
     */
    private String ClientSystem;

    /**
     * app端版本号
     */
    private String Version;

    /**
     * 接口签名，多个参数用&符号连接之后的转小写md5值
     */
    private String sign;

    /**
     * 默认为1
     */
    private int page;
}
