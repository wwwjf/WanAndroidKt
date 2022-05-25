package com.xianghe.ivy.model;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 作者：created by huangjiang on 2018/9/14  20:54
 * 邮箱：504512336@qq.com
 * 描述：
 */
public class MusicTagBean implements Serializable {

    private String id;
    private String name;
    @SerializedName(value = "type_code")
    private int type_code;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType_code() {
        return type_code;
    }

    public void setType_code(int type_code) {
        this.type_code = type_code;
    }
}
