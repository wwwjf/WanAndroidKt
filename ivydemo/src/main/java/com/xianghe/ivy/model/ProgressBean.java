package com.xianghe.ivy.model;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;

import com.xianghe.ivy.model.request.MovieUploadRequest;

import java.io.Serializable;

/**
 * 作者：created by huangjiang on 2018/10/10  10:43
 * 邮箱：504512336@qq.com
 * 描述：进度实体
 */
@SuppressLint("ParcelCreator")
public class ProgressBean implements Serializable {
    //用户id
    private long uid;
    //影片名称
    private String name;
    //影片第一帧图片 -- 上传
    private Bitmap image;
    //影片第一帧图片 -- 下载
    private String downLoadImage;
    //影片本地路径
    private String path;
    //影片，0 为上传， 1 为下载
    private int state;
    //进度条进度 长度
    private long loadSizeLength;
    //进度条进度
    private long loadSize;
    //是否出错
    private boolean isErr;
    //下载影片信息
    private CategoryMovieBean movieBean;
    //上传影片信息
    private MovieUploadRequest movieUploadRequest;

    public boolean isStop() {
        return isStop;
    }

    public void setStop(boolean stop) {
        isStop = stop;
    }

    //暂停
    private boolean isStop;

    public long getLoadSizeLength() {
        return loadSizeLength;
    }

    public void setLoadSizeLength(long loadSizeLength) {
        this.loadSizeLength = loadSizeLength;
    }

    public boolean isErr() {
        return isErr;
    }

    public void setErr(boolean err) {
        isErr = err;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public long getLoadSize() {
        return loadSize;
    }

    public void setLoadSize(long uploadSize) {
        this.loadSize = uploadSize;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getDownLoadImage() {
        return downLoadImage;
    }

    public void setDownLoadImage(String downLoadImage) {
        this.downLoadImage = downLoadImage;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public MovieUploadRequest getMovieUploadRequest() {
        return movieUploadRequest;
    }

    public void setMovieUploadRequest(MovieUploadRequest movieUploadRequest) {
        this.movieUploadRequest = movieUploadRequest;
    }

    public CategoryMovieBean getMovieBean() {
        return movieBean;
    }

    public void setMovieBean(CategoryMovieBean movieBean) {
        this.movieBean = movieBean;
    }

    @Override
    public String toString() {
        return "ProgressBean{" +
                "uid=" + uid +
                ", name='" + name + '\'' +
                ", image=" + image +
                ", downLoadImage='" + downLoadImage + '\'' +
                ", path='" + path + '\'' +
                ", state=" + state +
                ", loadSizeLength=" + loadSizeLength +
                ", loadSize=" + loadSize +
                ", isErr=" + isErr +
                ", movieBean=" + movieBean +
                ", movieUploadRequest=" + movieUploadRequest +
                ", isStop=" + isStop +
                '}';
    }
}
