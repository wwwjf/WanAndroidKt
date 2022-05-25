package com.xianghe.ivy.model;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HuoDongInfoBean {
    /**
     * id : 13
     * content : <p><span style="color: rgb(230, 0, 0);">1.点击邀请按钮，将当前页分享给您未下载过i微影App的好友</span></p><p><span style="color: rgb(230, 0, 0);">2.通过微信邀请，好友下载App后必须通过微信登录方式登录方可被认定为邀请成功；通过手机短信邀请，好友下载App后必须通过手机号登录方可被认定为邀请成功，手机短信24小时内有效</span></p><p><span style="color: rgb(230, 0, 0);">3.在法律允许范围内，i微影对此次活动有最终解释权</span></p>
     * start_time : 2019-03-01 10:37:27
     * end_time : 2020-03-31 14:47:45
     * activity_type : 1
     * created_at : 2019-01-16 10:38:09
     * updated_at : 2019-03-27 17:35:48
     * activity_photos : null
     */
    public static final int ACTIVITY_TYPE_INVITED = 1;
    public static final int ACTIVITY_TYPE_JI_ZHAN = 2;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    @SerializedName("id")
    private int id;
    @SerializedName("content")
    private String content;
    @SerializedName("start_time")
    private String startTime;
    @SerializedName("end_time")
    private String endTime;
    @SerializedName("activity_type")
    private int activityType;   // 1:邀请活动;2:集赞活动
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;
    @SerializedName("activity_photos")
    private String activityPhotos;
    @SerializedName("last_show_time")
    private String lastShowTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getActivityType() {
        return activityType;
    }

    public void setActivityType(int activityType) {
        this.activityType = activityType;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getActivityPhotos() {
        return activityPhotos;
    }

    public void setActivityPhotos(String activityPhotos) {
        this.activityPhotos = activityPhotos;
    }

    public String getLastShowTime() {
        return lastShowTime;
    }

    public void setLastShowTime(String lastShowTime) {
        lastShowTime = lastShowTime;
    }

    public long getEndTimeByMillisecond() {
        try {
            Date date = DATE_FORMAT.parse(endTime);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public long getLastShowTimeByMillisecond() {
        try {
            Date date = DATE_FORMAT.parse(lastShowTime);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public long getStartTimeByMillisecond() {
        try {
            Date date = DATE_FORMAT.parse(startTime);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public String toString() {
        return "HuoDongInfoBean{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", activityType='" + activityType + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", activityPhotos=" + activityPhotos +
                '}';
    }
}
