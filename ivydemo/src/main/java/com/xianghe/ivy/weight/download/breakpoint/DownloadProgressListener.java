package com.xianghe.ivy.weight.download.breakpoint;

/**
 * Created by ${R.js} on 2018/3/22.
 */

public interface DownloadProgressListener {

    /**
     * @param read 已下载长度
     * @param contentLength 总长度
     * @param done 是否下载完毕
     * @param type 0 音频 1 视频
     */
    void progress(long read, long contentLength, boolean done,int type);

}
