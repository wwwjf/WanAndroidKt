package com.xianghe.ivy.model;

import com.google.gson.annotations.SerializedName;

public class MonthFormatBean {
    /**
     * date_format : 201903
     * title : 本月榜单
     */

    @SerializedName("date_format")
    private int dateFormat;
    @SerializedName("title")
    private String title;

    public int getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(int dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}