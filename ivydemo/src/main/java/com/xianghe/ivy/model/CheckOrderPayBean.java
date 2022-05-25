package com.xianghe.ivy.model;

import java.io.Serializable;

/**
 * @创建者 Allen
 * @创建时间 2019/4/28 10:22
 * @描述      校验是否支付成功
 */
public class CheckOrderPayBean implements Serializable {

    /**
     * creatTime : xxx              	支付时间
     * price : xxx
     * payType : xxx
     * isSuccess : true or false        是否支付成功
     * message : 提示信息
     */

    private String creatTime;
    private String price;
    private String payType;
    private boolean isSuccess;
    private String message;

    public String getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(String creatTime) {
        this.creatTime = creatTime;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }
}
