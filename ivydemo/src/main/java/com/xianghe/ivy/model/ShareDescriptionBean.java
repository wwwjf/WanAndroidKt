package com.xianghe.ivy.model;

public class ShareDescriptionBean {
    /**
     * status : 1
     * info : ok
     * info_code : 10000
     * data : {"title":"活了几十年，这样的场面还是第一次见【点击这里，带我一起冲击万元大奖】","description":"我的影片正在参加微电影人气PK赛，欢迎各路大神围观。帮我点赞助力，共同创作一起拿万元大奖，关注、点赞、转发，大家一起发发发"}
     * api_version : 1.1.0
     */

    private String title;
    private String description;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
