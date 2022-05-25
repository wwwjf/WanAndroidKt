package com.xianghe.ivy.model.response;

import java.io.Serializable;

/**
 * 手机号验证
 */
public class VerifyNumberBean implements Serializable {
    private static final long serialVersionUID = -2140722053808461242L;
    /**
     * status : 1 未注册 0：已注册
     */

    private int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
