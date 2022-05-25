package com.xianghe.ivy.manager.download;


import androidx.annotation.NonNull;

import com.xianghe.ivy.entity.db.MovieTaskCache;
import com.xianghe.ivy.model.CategoryMovieBean;

import java.util.List;

public interface IMovieDownloadManager extends IDownloadManager {

    public enum Status {
        PENDING,
        RUNNING,
        COMPLETED,
        IDLE,
        UNKNOWN,         // may completed, but no filename can't ensure.
        NO_TASK,
    }

    public enum Error {
        COMPLETED,
        ERROR,
        CANCELED,
        FILE_BUSY,
        SAME_TASK_BUSY,
        PRE_ALLOCATE_FAILED
    }

    public boolean contains(CategoryMovieBean movie);

    public void addTask(CategoryMovieBean movie);

    public void addTask(CategoryMovieBean movie, String file);

    public void removeTask(CategoryMovieBean movie);

    public void removeAllTask();

    public Status getTaskState(CategoryMovieBean movieBean);

    public void startAll(boolean isSerial);

    public void stopAll();

    public void startTask(CategoryMovieBean movieBean);

    public void stopTask(CategoryMovieBean movieBean);

    public void addMovieDownloadListener(MovieDownloadListener listener);

    public void removeMovieDownloadListener(MovieDownloadListener listener);

    public void addMovieTaskListener(MovieTaskListener listener);

    public void removeMovieTaskListener(MovieTaskListener listener);

    public boolean isStartAllUploadTask();

    public void setStartAllUploadTask(boolean startAllUploadTask);

    /**
     * 不想让别人知道具体的实现类, 保护催脆弱的数据结构
     *
     * @param movie movie
     * @return task
     */
    public Object getTask(CategoryMovieBean movie);

    public boolean hasTasks();

    public boolean hasUncompleteTasks();

    public interface CacheManager {
        public void insert(MovieTaskCache cache);

        public void remove(MovieTaskCache cache);

        public void removeAll();

        public List<MovieTaskCache> loadAll(boolean clear);

    }


    public interface MovieTaskListener {
        public void addTask(@NonNull CategoryMovieBean movie);

        public void removeTask(@NonNull CategoryMovieBean movie);

        public void removeAllTask(List<CategoryMovieBean> movies);
    }


    public interface MovieDownloadListener {
        public void taskStart(@NonNull CategoryMovieBean movie);

        public void retry(@NonNull CategoryMovieBean movie);

        public void connected(@NonNull CategoryMovieBean movie, int blockCount, long currentOffset, long totalLength);

        public void progress(@NonNull CategoryMovieBean movie, long currentOffset, long totalLength);

        public void taskCancel(@NonNull CategoryMovieBean movie);

        public void taskCompleted(@NonNull CategoryMovieBean movie);

        public void taskError(@NonNull CategoryMovieBean movie);
    }
}
