package com.xianghe.ivy.model;

import java.io.Serializable;

/**
 * 作者：created by huangjiang on 2018/9/14  20:54
 * 邮箱：504512336@qq.com
 * 描述：
 */
public class MusicBean  implements Serializable {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;
    private String music_name;  // 音乐名称 "aaa"
    private String music_link;  // 音乐链接 "http://ivy-test.oss-cn-shenzhen.aliyuncs.com/xxxx"
    private String time_length; // 音乐时长 "03:45"

    public String getMusic_name() {
        return music_name;
    }

    public void setMusic_name(String music_name) {
        this.music_name = music_name;
    }

    public String getMusic_link() {
        return music_link;
    }

    public void setMusic_link(String music_link) {
        this.music_link = music_link;
    }

    public String getTime_length() {
        return time_length;
    }

    public void setTime_length(String time_length) {
        this.time_length = time_length;
    }

    @Override
    public String toString() {
        return "MusicBean{" +
                "id='" + id + '\'' +
                ", music_name='" + music_name + '\'' +
                ", music_link='" + music_link + '\'' +
                ", time_length='" + time_length + '\'' +
                '}';
    }
}
