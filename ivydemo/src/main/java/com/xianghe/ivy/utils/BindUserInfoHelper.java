package com.xianghe.ivy.utils;

import com.xianghe.ivy.model.BindUserInfoBean;

import java.io.Serializable;

/**
 * @创建者 Allen
 * @创建时间 2019/4/25 14:07
 * @描述      微信,支付宝首次提现时用户需要绑定
 */
public class BindUserInfoHelper implements Serializable {

    private static BindUserInfoHelper mInstance = null;

    private BindUserInfoBean mInfoBean;


    public static int mBindType = 0;        //默认为0 , 微信 1, 支付宝 2

    public static BindUserInfoHelper getInstance() {
        if (mInstance == null) {
            synchronized (BindUserInfoHelper.class) {
                if (mInstance == null) {
                    mInstance = new BindUserInfoHelper();
                }
            }
        }
        return mInstance;
    }

    public BindUserInfoBean getUserInfo(){
        return mInfoBean;
    }

    public void setUserInfo(BindUserInfoBean bean){
        mInfoBean = bean;
    }
}
