package com.xianghe.ivy.manager.download;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;
import com.liulishuo.okdownload.core.listener.DownloadListener1;
import com.liulishuo.okdownload.core.listener.assist.Listener1Assist;
import com.xianghe.ivy.utils.KLog;

public abstract class ApkDownloadListener extends DownloadListener1 {
    @Override
    public void taskStart(@NonNull DownloadTask task, @NonNull Listener1Assist.Listener1Model model) {
        KLog.e("===taskStart");
        onTaskStart(task,model);
    }

    @Override
    public void retry(@NonNull DownloadTask task, @NonNull ResumeFailedCause cause) {
        KLog.e("===retry");
    }

    @Override
    public void connected(@NonNull DownloadTask task, int blockCount, long currentOffset, long totalLength) {
        KLog.e("===connected");
    }

    @Override
    public void progress(@NonNull DownloadTask task, long currentOffset, long totalLength) {
        onProgress(task,currentOffset,totalLength);
    }

    @Override
    public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause, @NonNull Listener1Assist.Listener1Model model) {
        KLog.e("===taskEnd");
        onTaskEnd(task,cause,realCause,model);
    }

    public abstract void onTaskStart(@NonNull DownloadTask task, @NonNull Listener1Assist.Listener1Model model);

    public abstract void onProgress(@NonNull DownloadTask task, long currentOffset, long totalLength);

    public abstract void onTaskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause, @NonNull Listener1Assist.Listener1Model model);
}
