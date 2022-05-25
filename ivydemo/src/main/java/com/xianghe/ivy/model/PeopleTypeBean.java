package com.xianghe.ivy.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PeopleTypeBean implements Serializable, MediaReportTypeBean.ReportType {
    /**
     * id : 20
     * name : 素质低下
     */

    @SerializedName("id")
    private int mId;
    @SerializedName("type_code")
    private int mTypeCode;
    @SerializedName("name")
    private String mName;

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    @Override
    public int getTypeCode() {
        return mTypeCode;
    }

    public void setTypeCode(int typeCode) {
        mTypeCode = typeCode;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }
}