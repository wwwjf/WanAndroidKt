package com.xianghe.ivy.model;

import java.io.Serializable;

/**
 * 影片评论
 */
public class UserDynamicBean implements Serializable {

    /**
     * id : 488
     * type : 1
     * uid : 8272574
     * name : 13200550055
     * avatar : http://ivy-test.oss-cn-shenzhen.aliyuncs.com/4389/201808/201808299a76efb03e94902e6de6e23e60790c62.png?
     * media_id : 367
     * content : 测试评论193
     * cover : http://ivy-test.oss-cn-shenzhen.aliyuncs.com/4389/201810/2018101145ce55e40cdaf74735702414582e572b.png?
     * duration : 7686
     * created_at : 2018-11-05 15:27:00
     * descript : 评论了你的影片
     */
    private static final long serialVersionUID = -2044473237410178194L;
    private int id;
    /**
     * 动态类型  1：评论你的影片 2：回复你的评论 3：转发评论 4：发起视频聊天
     */
    private int type;
    private long uid;
    private String name;
    private String avatar;
    private int media_id;
    private String content;
    private String cover;
    private long duration;
    private String created_at;
    private String descript;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getMedia_id() {
        return media_id;
    }

    public void setMedia_id(int media_id) {
        this.media_id = media_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getDescript() {
        return descript;
    }

    public void setDescript(String descript) {
        this.descript = descript;
    }
}
