package com.xianghe.ivy.model.response;

import java.io.Serializable;

/**
 * 支付相关类接口的请求结果
 * @param <T>
 */
public class BasePayResponse<T> implements Serializable {
    private static final long serialVersionUID = 7790967965187517959L;

    public interface PayCode{
        //成功
        int success = 200;
        //其余失败
    }


    private int code;

    private String message;

    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
