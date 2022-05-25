package com.xianghe.ivy.weight.download;

public interface onDownloadProgressCallBack {
        void onSuccessDownload(String path);

        void onErrorDownload(Throwable t);

        void onProgressDownload(int progress);
    }