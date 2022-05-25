package com.xianghe.ivy.entity.db;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class VideoPushCache {
    @Id(autoincrement = true)
    private Long id;

    // mUid
    private long uid;

    private String director;
    private String player;
    @Generated(hash = 2124400838)
    public VideoPushCache(Long id, long uid, String director, String player) {
        this.id = id;
        this.uid = uid;
        this.director = director;
        this.player = player;
    }
    @Generated(hash = 212163239)
    public VideoPushCache() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public long getUid() {
        return this.uid;
    }
    public void setUid(long uid) {
        this.uid = uid;
    }
    public String getDirector() {
        return this.director;
    }
    public void setDirector(String director) {
        this.director = director;
    }
    public String getPlayer() {
        return this.player;
    }
    public void setPlayer(String player) {
        this.player = player;
    }
}
