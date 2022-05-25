package com.xianghe.ivy.model;

import com.google.gson.annotations.SerializedName;

public class WeekFormatBean {
    /**
     * date_format : 13
     * title : 本周榜单
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