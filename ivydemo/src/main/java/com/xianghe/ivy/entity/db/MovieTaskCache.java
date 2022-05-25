package com.xianghe.ivy.entity.db;

import com.liulishuo.okdownload.DownloadTask;
import com.xianghe.ivy.model.CategoryMovieBean;
import com.xianghe.ivy.network.GsonHelper;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class MovieTaskCache {
    public static final int TYPE_DOWNLOAD = 0;
    public static final int TYPE_PULOAD = 1;

    @Id(autoincrement = true)
    private Long id;

    private String localPath;
    private String downloadUrl;

    private int type;
    private int uid;

    private String data;

    @Generated(hash = 994766929)
    public MovieTaskCache(Long id, String localPath, String downloadUrl, int type, int uid,
            String data) {
        this.id = id;
        this.localPath = localPath;
        this.downloadUrl = downloadUrl;
        this.type = type;
        this.uid = uid;
        this.data = data;
    }

    @Generated(hash = 526799262)
    public MovieTaskCache() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocalPath() {
        return this.localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getDownloadUrl() {
        return this.downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getData() {
        return this.data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public static MovieTaskCache build(DownloadTask task, CategoryMovieBean movie, int type) {
        MovieTaskCache cache = new MovieTaskCache();
        cache.setDownloadUrl(movie.getMediaOssUrl());
        cache.setLocalPath(task.getFile() == null ? null : task.getFile().getAbsolutePath());
        cache.setType(type);
        cache.setData(GsonHelper.object2JsonStr(movie));
        return cache;
    }

    @Override
    public String toString() {
        return "MovieTaskCache{" +
                "id=" + id +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", uid=" + uid +
                '}';
    }
}
