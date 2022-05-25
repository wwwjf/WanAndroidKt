package com.xianghe.ivy.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HuoDongListBean {
    @SerializedName("list")
    private List<HuoDongInfoBean> list;

    public List<HuoDongInfoBean> getList() {
        return list;
    }

    public void setList(List<HuoDongInfoBean> list) {
        this.list = list;
    }
}
