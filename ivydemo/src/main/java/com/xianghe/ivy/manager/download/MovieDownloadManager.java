package com.xianghe.ivy.manager.download;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.liulishuo.okdownload.DownloadListener;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.OkDownload;
import com.liulishuo.okdownload.StatusUtil;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;
import com.liulishuo.okdownload.core.listener.DownloadListener1;
import com.liulishuo.okdownload.core.listener.assist.Listener1Assist;
import com.xianghe.ivy.entity.db.MovieTaskCache;
import com.xianghe.ivy.model.CategoryMovieBean;
import com.xianghe.ivy.network.GsonHelper;
import com.xianghe.ivy.ui.media.config.MediaConfig;
import com.xianghe.ivy.ui.module.videocall.service.FileUtil;
import com.xianghe.ivy.utils.KLog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovieDownloadManager implements IMovieDownloadManager {
    private static final String TAG = "MovieDownloadManager";

    private volatile static IMovieDownloadManager mInstance = null;

    private final Context mContext;
    private Config mConfig = new Config();
    private Map<DownloadTask, Error> mTaskList = new HashMap<>();
    private List<MovieTaskListener> mTaskListeners = new ArrayList<>();
    private List<MovieDownloadListener> mDownloadListeners = new ArrayList<>();

    private CacheManager mCacheManager;

    private boolean isStartAllUploadTask = false; // 每次进入界面，如果有上传任务未完成，就根据此标识，在上传列表中控制只是添加集合，并执行上传操作

    public static IMovieDownloadManager getInstance(Context ctx) {
        if (mInstance == null) {
            synchronized (MovieDownloadManager.class) {
                if (mInstance == null) {
                    mInstance = new MovieDownloadManager(ctx);
                }
            }
        }
        return mInstance;
    }

    private MovieDownloadManager(Context ctx) {
        mContext = ctx.getApplicationContext();
        mCacheManager = new MoveTaskCacheManager(ctx);
        List<MovieTaskCache> caches = mCacheManager.loadAll(true);

        // 没有上传操作，就打开开关，控制之后数据添加到队列直接执行上传
        if (caches == null || caches.isEmpty()) {
            setStartAllUploadTask(true);
        }

        for (MovieTaskCache cache : caches) {
            CategoryMovieBean movieBean = GsonHelper.getsGson().fromJson(cache.getData(), CategoryMovieBean.class);
            //if (TextUtils.isEmpty(cache.getLocalPath())) {
            addTask(movieBean);
//            } else {
//                addTask(movieBean, cache.getLocalPath());
//            }
        }
    }

    @Override
    public synchronized boolean contains(CategoryMovieBean movie) {
        return findTaskByMovie(movie) != null;
    }

    @Override
    public synchronized void addTask(CategoryMovieBean movie) {
        KLog.d(TAG, "addTask: movie = " + movie + ", 下载到默文件夹.");

        if (movie == null) {
            return;
        }
        if (contains(movie)) {
            return;
        }

        //String extensionName = FileUtils.getExtensionName(movie.getProtectedMedia());
        String extensionName = "mp4";
        File file = new File(new File(Environment.getExternalStorageDirectory(), MediaConfig.AUDIO_DIR_DOWNLOAD_MEDIA), "ivy_" + System.currentTimeMillis() + "." + extensionName);
        DownloadTask downloadTask = new DownloadTask.Builder(movie.getMediaOssUrl(), file)
                .setMinIntervalMillisCallbackProcess(mConfig.getMinIntervalMillisCallbackProcess())
                .setPassIfAlreadyCompleted(true)
                .build();
        downloadTask.setTag(movie);
        mTaskList.put(downloadTask, null);

        // cache task
        MovieTaskCache taskCache = MovieTaskCache.build(downloadTask, movie, MovieTaskCache.TYPE_DOWNLOAD);
        mCacheManager.insert(taskCache);

        for (MovieTaskListener taskListener : mTaskListeners) {
            taskListener.addTask(movie);
        }
    }

    @Override
    public synchronized void addTask(CategoryMovieBean movie, String file) {
        KLog.d(TAG, "addTask: movie = " + movie + ", 下载到指定路径. file = " + file);
        if (movie == null) {
            return;
        }
        if (contains(movie)) {
            return;
        }

        DownloadTask downloadTask = new DownloadTask.Builder(movie.getMediaOssUrl(), new File(file))
                .setMinIntervalMillisCallbackProcess(mConfig.getMinIntervalMillisCallbackProcess())
                .setPassIfAlreadyCompleted(true)
                .build();
        downloadTask.setTag(movie);
        mTaskList.put(downloadTask, null);

        // cache task
        MovieTaskCache taskCache = MovieTaskCache.build(downloadTask, movie, MovieTaskCache.TYPE_DOWNLOAD);
        mCacheManager.insert(taskCache);

        for (MovieTaskListener taskListener : mTaskListeners) {
            taskListener.addTask(movie);
        }
    }

    @Override
    public synchronized void removeTask(CategoryMovieBean movie) {
        KLog.d(TAG, "尝试移除下载任务... ：" + movie);
        DownloadTask task = findTaskByMovie(movie);
        if (task != null) {
            task.cancel();
            mCacheManager.remove(MovieTaskCache.build(task, movie, MovieTaskCache.TYPE_DOWNLOAD));
            mTaskList.remove(task);
            KLog.d(TAG, "已移除下载任务：" + movie);

            for (MovieTaskListener taskListener : mTaskListeners) {
                taskListener.removeTask(movie);
            }
        }
    }

    @Override
    public void removeAllTask() {
        setStartAllUploadTask(true);// 打开开关，能够自动下载

        List<CategoryMovieBean> movies = new ArrayList<>();
        for (Map.Entry<DownloadTask, Error> entry : mTaskList.entrySet()) {
            DownloadTask task = entry.getKey();
            task.cancel();
            movies.add((CategoryMovieBean) task.getTag());
        }

        mCacheManager.removeAll();
        mTaskList.clear();
        for (MovieTaskListener taskListener : mTaskListeners) {
            taskListener.removeAllTask(movies);
        }
    }

    @Override
    public Status getTaskState(CategoryMovieBean movie) {
        DownloadTask task = findTaskByMovie(movie);
        if (task != null) {
            StatusUtil.Status status = StatusUtil.getStatus(task);
            return coverStatue(status);
        }
        return Status.NO_TASK;
    }

    @Override
    public void startAll(boolean isSerial) {
        try {
            for (Map.Entry<DownloadTask, Error> entry : mTaskList.entrySet()) {
                DownloadTask task = entry.getKey();
                StatusUtil.Status status = StatusUtil.getStatus(task);
                KLog.d(TAG, "status = " + status);

                if (status != StatusUtil.Status.RUNNING) {
                    task.enqueue(mDownloadListener);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopAll() {
        DownloadTask[] tasks = new DownloadTask[mTaskList.size()];
        int i = 0;
        for (Map.Entry<DownloadTask, Error> entry : mTaskList.entrySet()) {
            tasks[i] = entry.getKey();
            i = i + 1;
        }
        OkDownload.with().downloadDispatcher().cancel(tasks);
    }

    @Override
    public synchronized void startTask(CategoryMovieBean movie) {
        DownloadTask task = findTaskByMovie(movie);
        if (task != null) {
            task.enqueue(mDownloadListener);
        }
    }

    @Override
    public synchronized void stopTask(CategoryMovieBean movie) {
        DownloadTask task = findTaskByMovie(movie);
        if (task != null) {
            task.cancel();
        }
    }

    @Override
    public void addMovieDownloadListener(MovieDownloadListener listener) {
        if (mDownloadListeners.contains(listener)) {
            return;
        }
        mDownloadListeners.add(listener);
    }

    @Override
    public void removeMovieDownloadListener(MovieDownloadListener listener) {
        mDownloadListeners.remove(listener);
    }

    @Override
    public void addMovieTaskListener(MovieTaskListener listener) {
        if (mTaskListeners.contains(listener)) {
            return;
        }
        mTaskListeners.add(listener);
    }

    @Override
    public void removeMovieTaskListener(MovieTaskListener listener) {
        mTaskListeners.remove(listener);
    }

    @Override
    public DownloadTask getTask(CategoryMovieBean movie) {
        return findTaskByMovie(movie);
    }

    @Override
    public Object[] getTasks() {
        DownloadTask[] tasks = new DownloadTask[mTaskList.size()];
        int i = 0;
        for (Map.Entry<DownloadTask, Error> entry : mTaskList.entrySet()) {
            tasks[i] = entry.getKey();
            i = i + 1;
        }
        return tasks;
    }

    @Override
    public boolean hasTasks() {
        return mTaskList != null && !mTaskList.isEmpty();
    }

    @Override
    public boolean hasUncompleteTasks() {
        if (mTaskList != null) {
            for (Map.Entry<DownloadTask, Error> entry : mTaskList.entrySet()) {
                DownloadTask task = entry.getKey();
                if (!StatusUtil.isCompleted(task)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Nullable
    private DownloadTask findTaskByMovie(CategoryMovieBean movieBean) {
        if (movieBean == null) {
            return null;
        }

        for (Map.Entry<DownloadTask, Error> entry : mTaskList.entrySet()) {
            DownloadTask task = entry.getKey();
            if (task.getTag() instanceof CategoryMovieBean && task.getTag().equals(movieBean)) {
                return task;
            }
        }
        return null;
    }

    public Error getErrorStatue(CategoryMovieBean movieBean) throws IllegalStateException {
        for (Map.Entry<DownloadTask, Error> entry : mTaskList.entrySet()) {
            DownloadTask task = entry.getKey();
            if (task.getTag().equals(movieBean)) {
                return entry.getValue();
            }
        }

        throw new IllegalStateException("Can't find the corresponding movie");
    }

    /**
     * 将OkDownload的任务状态转换成IMovieDownloadManager定义的任务状态
     *
     * @param status
     * @return
     */
    private Status coverStatue(StatusUtil.Status status) {
        switch (status) {
            case IDLE:
                return Status.IDLE;
            case PENDING:
                return Status.PENDING;
            case RUNNING:
                return Status.RUNNING;
            case COMPLETED:
                return Status.COMPLETED;
        }
        return Status.UNKNOWN;
    }

    @Nullable
    private Error coverError(EndCause cause) {
        if (cause == null) {
            return null;
        }
        switch (cause) {
            case COMPLETED:
                return Error.COMPLETED;
            case ERROR:
                return Error.ERROR;
            case CANCELED:
                return Error.CANCELED;
            case FILE_BUSY:
                return Error.FILE_BUSY;
            case SAME_TASK_BUSY:
                return Error.SAME_TASK_BUSY;
            case PRE_ALLOCATE_FAILED:
                return Error.PRE_ALLOCATE_FAILED;
        }
        return null;
    }

    private final DownloadListener mDownloadListener = new DownloadListener1() {
        @Override
        public void taskStart(@NonNull DownloadTask task, @NonNull Listener1Assist.Listener1Model model) {
            if (mTaskList.containsKey(task)) {
                mTaskList.put(task, null);
            }
            KLog.i(TAG, "taskStart: task = " + task.getFilename() + ", model = " + model);
            for (MovieDownloadListener downloadListener : mDownloadListeners) {
                downloadListener.taskStart((CategoryMovieBean) task.getTag());
            }
        }

        @Override
        public void retry(@NonNull DownloadTask task, @NonNull ResumeFailedCause cause) {
            KLog.w(TAG, "retry: task = " + task.getFilename() + ", cause = " + cause);
            for (MovieDownloadListener downloadListener : mDownloadListeners) {
                downloadListener.retry((CategoryMovieBean) task.getTag());
            }
        }

        @Override
        public void connected(@NonNull DownloadTask task, int blockCount, long currentOffset, long totalLength) {
            KLog.i(TAG, "connected: task = " + task.getFilename() + ", blockCount = " + currentOffset);
            for (MovieDownloadListener downloadListener : mDownloadListeners) {
                downloadListener.connected((CategoryMovieBean) task.getTag(), blockCount, currentOffset, totalLength);
            }
        }

        @Override
        public void progress(@NonNull DownloadTask task, long currentOffset, long totalLength) {
            float precent = currentOffset * 100f / totalLength;
            KLog.d(TAG, String.format("progress: task = %s ---- %.2f", task.getFilename(), precent));
            for (MovieDownloadListener downloadListener : mDownloadListeners) {
                downloadListener.progress((CategoryMovieBean) task.getTag(), currentOffset, totalLength);
            }
        }

        @Override
        public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause, @NonNull Listener1Assist.Listener1Model model) {
            KLog.e(TAG, "taskEnd: task = " + task.getFilename() + ", cause = " + cause + ", realCause = " + realCause + ", model = " + model);
            if (mTaskList.containsKey(task)) {
                mTaskList.put(task, coverError(cause));
            }
            switch (cause) {
                case CANCELED:
                    for (MovieDownloadListener downloadListener : mDownloadListeners) {
                        downloadListener.taskCancel((CategoryMovieBean) task.getTag());
                    }
                    break;
                case COMPLETED:
                    File file = task.getFile();
//                    FileUtils.scanFile(mContext, file);
                    if (file != null && file.exists()) {
                        FileUtil.fileScanVideo(mContext, file.getAbsolutePath());
                    }
                    KLog.d(TAG, "相册刷新：file = " + file);
                    for (MovieDownloadListener downloadListener : mDownloadListeners) {
                        downloadListener.taskCompleted((CategoryMovieBean) task.getTag());
                    }
                    break;
                case SAME_TASK_BUSY:// 同样的任务
                case ERROR:
                default:
                    for (MovieDownloadListener downloadListener : mDownloadListeners) {
                        downloadListener.taskError((CategoryMovieBean) task.getTag());
                    }
                    break;
            }
        }
    };

    public static class Config {
        private Uri mParentDirUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), MediaConfig.AUDIO_DIR_DOWNLOAD_MEDIA));

        private Integer mMinIntervalMillsCallbackProcess;

        public Uri getParentDirUri() {
            return mParentDirUri;
        }

        public Config setParentDirUri(@NonNull Uri parentDirUri) {
            mParentDirUri = parentDirUri;
            return this;
        }

        public Config setParentPathFile(@NonNull File parentPathFile) {
            if (parentPathFile.isFile()) {
                throw new IllegalArgumentException("parent path only accept directory path");
            }

            mParentDirUri = Uri.fromFile(parentPathFile);
            return this;
        }

        public Config setParentPath(@NonNull String parentPath) {
            return setParentPathFile(new File(parentPath));
        }

        public int getMinIntervalMillisCallbackProcess() {
            return mMinIntervalMillsCallbackProcess == null ? DownloadTask.Builder.DEFAULT_MIN_INTERVAL_MILLIS_CALLBACK_PROCESS : mMinIntervalMillsCallbackProcess;
        }

        public Config setMinIntervalMillisCallbackProcess(Integer minIntervalMillisCallbackProcess) {
            mMinIntervalMillsCallbackProcess = minIntervalMillisCallbackProcess;
            return this;
        }
    }

    @Override
    public boolean isStartAllUploadTask() {
        return isStartAllUploadTask;
    }

    @Override
    public void setStartAllUploadTask(boolean startAllUploadTask) {
        isStartAllUploadTask = startAllUploadTask;
    }


}
