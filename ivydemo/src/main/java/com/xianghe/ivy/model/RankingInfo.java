package com.xianghe.ivy.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RankingInfo {
    /**
     * week_format : [{"date_format":"13","title":"本周榜单"},{"date_format":12,"title":"18日-24日榜单"},{"date_format":11,"title":"11日-17日榜单"},{"date_format":10,"title":"04日-10日榜单"}]
     * month_format : [{"date_format":"201903","title":"本月榜单"},{"date_format":"201902","title":"02月榜单"},{"date_format":"201901","title":"01月榜单"},{"date_format":"201812","title":"12月榜单"}]
     * type : 1
     * activity_title : 微电影有奖征集活动
     * activity_content :
     * list : [{"one_self":0,"ranking":1,"like_num":"3","uid":"8272649","name":"陆俊","update_date":"03-28 09:40:11"},{"one_self":0,"ranking":2,"like_num":"3","uid":"8272901","name":"用户6588813126925","update_date":"03-28 11:50:24"},{"one_self":0,"ranking":3,"like_num":"3","uid":"8272980","name":"用户数已经开始","update_date":"03-31 09:29:59"},{"one_self":0,"ranking":4,"like_num":"1","uid":"8272952","name":"球球大王","update_date":"03-28 09:40:23"},{"one_self":0,"ranking":5,"like_num":"1","uid":"8272601","name":"将计就计🐶⌚️🐶🎣🎣","update_date":"03-28 09:40:30"}]
     */

    @SerializedName("type")
    private int type;
    @SerializedName("activity_title")
    private String activityTitle;
    @SerializedName("activity_content")
    private String activityContent;
    @SerializedName("week_format")
    private List<WeekFormatBean> weekFormat;
    @SerializedName("month_format")
    private List<MonthFormatBean> monthFormat;
    @SerializedName("list")
    private List<RankingUserBean> list;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getActivityTitle() {
        return activityTitle;
    }

    public void setActivityTitle(String activityTitle) {
        this.activityTitle = activityTitle;
    }

    public String getActivityContent() {
        return activityContent;
    }

    public void setActivityContent(String activityContent) {
        this.activityContent = activityContent;
    }

    public List<WeekFormatBean> getWeekFormat() {
        return weekFormat;
    }

    public void setWeekFormat(List<WeekFormatBean> weekFormat) {
        this.weekFormat = weekFormat;
    }

    public List<MonthFormatBean> getMonthFormat() {
        return monthFormat;
    }

    public void setMonthFormat(List<MonthFormatBean> monthFormat) {
        this.monthFormat = monthFormat;
    }

    public List<RankingUserBean> getList() {
        return list;
    }

    public void setList(List<RankingUserBean> list) {
        this.list = list;
    }
}