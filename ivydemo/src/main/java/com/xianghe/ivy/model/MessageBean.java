package com.xianghe.ivy.model;

import java.io.Serializable;

/**
 * @创建者 Allen
 * @创建时间 2019/4/28 17:16
 * @描述
 */
public class MessageBean implements Serializable {

    /**
     * message : 绑定成功
     */

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
