package com.xianghe.ivy.weight.download;

/**
*作者：created by huangjiang on 2018/10/11  下午5:24
*邮箱：504512336@qq.com
*描述：
*/
public class DownloadProgressEvent {
    //写入数量
    private long bytesRead;
    //总长度
    private long contentLength;
    //是否下载完成
    private boolean done;
    //下载， code 0 为音乐下载， 1，为视频下载
    private int code;

    public long getBytesRead() {
        return bytesRead;
    }

    public void setBytesRead(long bytesRead) {
        this.bytesRead = bytesRead;
    }

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
