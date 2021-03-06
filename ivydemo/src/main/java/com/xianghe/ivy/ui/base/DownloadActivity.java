package com.xianghe.ivy.ui.base;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.JsonElement;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.StatusUtil;
import com.xianghe.ivy.R;
import com.xianghe.ivy.adapter.DownloadItemAdapter;
import com.xianghe.ivy.adapter.recyclerview.IRecyclerItemClickListener;
import com.xianghe.ivy.adapter.recyclerview.IRecyclerItemLongClickListener;
import com.xianghe.ivy.constant.Api;
import com.xianghe.ivy.entity.db.UploadTaskCache;
import com.xianghe.ivy.http.request.NetworkRequest;
import com.xianghe.ivy.http.request.RequestError;
import com.xianghe.ivy.http.response.RespCode;
import com.xianghe.ivy.manager.download.IMovieDownloadManager;
import com.xianghe.ivy.manager.download.MovieDownloadManager;
import com.xianghe.ivy.model.CategoryMovieBean;
import com.xianghe.ivy.model.Movie;
import com.xianghe.ivy.model.response.BaseResponse;
import com.xianghe.ivy.model.response.MediaDetailResponse;
import com.xianghe.ivy.mvp.IBaseConctact;
import com.xianghe.ivy.network.GsonHelper;
import com.xianghe.ivy.utils.KLog;
import com.xianghe.ivy.utils.ToastUtil;
import com.xianghe.ivy.weight.CustomProgress;
import com.xianghe.ivy.weight.behavior.DrawerSheetBehavior;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * ?????????????????????UI??????????????????UI???????????????????????????
 *
 * @param <V>
 * @param <P>
 */
public abstract class DownloadActivity<V extends IBaseConctact.IBaseView, P extends IBaseConctact.IBasePresenter<V>> extends DownloadWeightActivity<V, P> {
    private static final String TAG = "DownloadActivity";

    protected Context mContext;
    private Handler mHandler;
    private RecyclerView mDownloadViewList;
    private DownloadItemAdapter mTaskAdapter;

    private NetworkConnectChangedReceiver mNetworkReceiver = new NetworkConnectChangedReceiver();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initMy(savedInstanceState);
        findViewMy(savedInstanceState);
        initViewMy(savedInstanceState);
        initDrawerBehavior(savedInstanceState);
        initListenerMy(savedInstanceState);
    }

    // ??????????????????????????????????????????
    protected boolean isNeedDownUpLoad() {
        return false;
    }

    private void initMy(Bundle savedInstanceState) {
        mContext = this;
    }

    private void findViewMy(@Nullable Bundle savedInstanceState) {
        if (!isNeedDownUpLoad()) {
            return;
        }
        mDownloadViewList = findViewById(R.id.download_menu_view_list);
    }

    private void initViewMy(@Nullable Bundle savedInstanceState) {
        if (!isNeedDownUpLoad()) {
            return;
        }
        mHandler = new Handler();
        mDownloadViewList.setLayoutManager(new LinearLayoutManager(this));
        mTaskAdapter = new DownloadItemAdapter();
        mDownloadViewList.setAdapter(mTaskAdapter);

        RecyclerView.ItemAnimator animator = mDownloadViewList.getItemAnimator();
        animator.setChangeDuration(0);
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        mDownloadViewList.setItemAnimator(animator);
    }

    private void initDrawerBehavior(Bundle savedInstanceState) {
        mDrawerBehavior.setDraggable(false);
        mDrawerBehavior.setState(DrawerSheetBehavior.STATE_HIDDEN);
    }

    private void initListenerMy(@Nullable Bundle savedInstanceState) {
        if (!isNeedDownUpLoad()) {
            return;
        }
        mTaskAdapter.setItemClickListener(mDownloadListItemClickListener);
        mTaskAdapter.setItemLongClickListener(mDownloadListItemLongClickListener);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!isNeedDownUpLoad()) {
            return;
        }
        mNetworkReceiver.startReceiver(this);

        // ??????
        final IMovieDownloadManager downloadManager = MovieDownloadManager.getInstance(mContext);
        Object[] tasks = downloadManager.getTasks();
        mTaskAdapter.removeAll(mTaskAdapter.getDatas());
        if (tasks != null) {
            List<Movie> movies = new ArrayList<>();
            for (int i = 0; i < tasks.length; i++) {
                Object task = tasks[i];
                if (task instanceof DownloadTask) {
                    DownloadTask downloadTask = (DownloadTask) task;
                    StatusUtil.Status status = StatusUtil.getStatus(downloadTask);
                    CategoryMovieBean movieBean = (CategoryMovieBean) (downloadTask).getTag();
                    if (status == StatusUtil.Status.COMPLETED) {
                        downloadManager.removeTask(movieBean);
                    } else {
                        movies.add(movieBean);
                    }
                }
            }
            mTaskAdapter.addDatas(movies);
        }
        downloadManager.addMovieTaskListener(mMovieTaskListener);
        downloadManager.addMovieDownloadListener(mMovieDownloadListener);

        // ??????
        /*UploadTaskManager uploadManager = UploadTaskManager.getInstance();
        List<UploadManager> uploadTasks = uploadManager.getTasks();
        if (uploadTasks != null && !uploadTasks.isEmpty()) {
            List<Movie> movies = new ArrayList<>();
            try {
                for (int i = 0; i < uploadTasks.size(); i++) {
                    UploadManager manager = uploadTasks.get(i);
                    UploadTaskCache tag = manager.getTag();
                    if (tag != null && tag.getState() == UploadManager.Status.COMPLETED) {
                        // ???????????????????????????item??????????????????????????????????????????
                        uploadManager.removeItemTask(manager, tag);
                        i--;
                    } else {
                        movies.add(tag);
                    }
                }
            } catch (Exception e) {
                KLog.e(e);
                e.printStackTrace();
            }
            // data movies
            if (!movies.isEmpty()) {
                mTaskAdapter.addDatas(movies);
            }
        }
        Log.d(TAG, "onStart" + mTaskAdapter.getItemCount());
        uploadManager.addMovieTaskListener(mUploadTaskListener);
        uploadManager.addMovieDownloadListener(mUploadListener);
*/

        updateDrawerState();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (!isNeedDownUpLoad()) {
            return;
        }
        mNetworkReceiver.stopReceiver(this);


        MovieDownloadManager.getInstance(mContext).removeMovieTaskListener(mMovieTaskListener);
        MovieDownloadManager.getInstance(mContext).removeMovieDownloadListener(mMovieDownloadListener);
//        UploadTaskManager.getInstance().removeMovieTaskListener(mUploadTaskListener);
//        UploadTaskManager.getInstance().removeMovieDownloadListener(mUploadListener);

        removeUpOrDownloadTaskImmediately();
    }

    // ------------------------------------------ adapter ???????????????????????????  start  ------------------------------------------------
    /**
     * ??????/?????? ????????????????????????
     */
    private final IRecyclerItemClickListener mDownloadListItemClickListener = new IRecyclerItemClickListener() {
        @Override
        public void onItemClick(RecyclerView parent, int position, RecyclerView.ViewHolder holder, View view) {
            // ?????????????????????
            onClickItem(parent, position, holder, view);
        }
    };
    private final IRecyclerItemLongClickListener mDownloadListItemLongClickListener = new IRecyclerItemLongClickListener() {
        @Override
        public boolean onItemLongClick(RecyclerView parent, int position, RecyclerView.ViewHolder holder, View view) {
            // ??????????????????
            onClickItemBtnCancel(parent, position, holder, view);
            return true;
        }
    };

    // ------------------ ???????????? start ------------------
    private void onClickItem(RecyclerView parent, int position, RecyclerView.ViewHolder holder, View view) {
        Movie movie = mTaskAdapter.getItem(position);
        if (movie instanceof CategoryMovieBean) {
            startOrStopDownloadTask((CategoryMovieBean) movie);
        } else if (movie instanceof UploadTaskCache) {
            startOrStopUploadTask((UploadTaskCache) movie);
        }
    }

    private synchronized void startOrStopUploadTask(UploadTaskCache movie) {
        /*UploadTaskManager downloadManager = UploadTaskManager.getInstance();
        UploadManager.Status status = downloadManager.getTaskState(movie);
        KLog.d("status:" + status);
        if (status == UploadManager.Status.RUNNING) {
            //KLog.d("stopTask");
//            downloadManager.stopTask(movie);
        } else if (downloadManager.canStartTask(status)) {
            KLog.d("startTask");
            downloadManager.startTask(movie);
        }*/
    }

    private void startOrStopDownloadTask(CategoryMovieBean movie) {
        IMovieDownloadManager downloadManager = MovieDownloadManager.getInstance(mContext);
        IMovieDownloadManager.Status status = downloadManager.getTaskState(movie);
        KLog.d("status:" + status);
        if (status == IMovieDownloadManager.Status.RUNNING) {
            //KLog.d("stopTask");
            //downloadManager.stopTask(movie);
        } else if (status == IMovieDownloadManager.Status.PENDING || status == IMovieDownloadManager.Status.IDLE || status == IMovieDownloadManager.Status.UNKNOWN) {
            KLog.d("startTask");
            downloadManager.startTask(movie);
        }
    }
    // ------------------ ???????????? end ------------------

    // ------------------ ?????????????????? start ------------------
    private void onClickItemBtnCancel(RecyclerView parent, int position, RecyclerView.ViewHolder holder, View view) {
        Movie movie = mTaskAdapter.getItem(position);

        if (movie instanceof CategoryMovieBean) {
            cancelDownloadManager((CategoryMovieBean) movie);
        } else if (movie instanceof UploadTaskCache) {
            cancelUploadManager((UploadTaskCache) movie);
        }
    }

    private void cancelDownloadManager(CategoryMovieBean movie) {
        if (movie == null) {
            return;
        }
        if (mCustomProgress == null) {
            mCustomProgress = new CustomProgress(this);
        }
        if (mCustomProgress != null) {
            mCustomProgress.show(getString(R.string.download_cancel_dialog_content),
                    getString(R.string.common_tips_title),
                    getString(R.string.download_cancel_dialog_btn_nagtive),
                    getString(R.string.common_ensure),
                    v -> mCustomProgress.dismiss(), v -> {
                        MovieDownloadManager.getInstance(mContext).removeTask(movie);
                        mCustomProgress.dismiss();
                    }, true, dialog -> mCustomProgress.dismiss());
        }
    }

    private void cancelUploadManager(UploadTaskCache movie) {
        if (movie == null) {
            return;
        }
        if (mCustomProgress == null) {
            mCustomProgress = new CustomProgress(this);
        }
        if (mCustomProgress != null) {
            mCustomProgress.show(getString(R.string.upload_cancel_dialog_content),
                    getString(R.string.common_tips_title),
                    getString(R.string.upload_cancel_dialog_btn_positive),
                    getString(R.string.common_ensure),
                    v -> mCustomProgress.dismiss(), v -> {
//                        UploadTaskManager.getInstance().removeTask(movie);
                        // ??????all??????
//                        UploadTaskManager.getInstance().deleteAllVideoUploadFile(movie);

                        mCustomProgress.dismiss();
                    }, true, dialog -> mCustomProgress.dismiss());
        }
    }
    // ------------------ ?????????????????? end ------------------


    // ------------------ ???????????? start ------------------
    private void onClickItemBtnReDownload(RecyclerView parent, int position, RecyclerView.ViewHolder holder, View view) {
        Movie movie = mTaskAdapter.getItem(position);

        if (movie instanceof CategoryMovieBean) {
            redownloadMovie((CategoryMovieBean) movie);
        } else if (movie instanceof UploadTaskCache) {
            reUploadMovie((UploadTaskCache) movie);
        }
    }

    @SuppressLint("CheckResult")
    private void redownloadMovie(CategoryMovieBean movie) {
        Map<String, Object> params = new HashMap<>();
        params.put(Api.Key.MEDIA_ID, movie.getId());
        params.put(Api.Key.START_COMMENT_ID, 0);
        params.put(Api.Key.ACTION_TYPE, Api.Value.ActionType.FORWARD);
        params.put(Api.Key.PAGE_SIZE, 0);

        NetworkRequest.INSTANCE.postMap(Api.Route.Index.MEDIA_MEDIA_DETAIL, params)
                .map(new Function<JsonElement, MediaDetailResponse>() {
                    @Override
                    public MediaDetailResponse apply(JsonElement jsonElement) throws Exception {
                        return GsonHelper.getsGson().fromJson(jsonElement, MediaDetailResponse.class);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MediaDetailResponse>() {
                    @Override
                    public void accept(MediaDetailResponse response) throws Exception {
                        KLog.i(TAG, "load comment success: " + response);
                        if (response.getStatus() == BaseResponse.Status.OK) {
                            if (response.getData() != null) {
                                // ???????????????url???, ??????????????????
                                CategoryMovieBean newMovie = movie.clone();
                                String url = response.getData().getMedia();
                                newMovie.setMedia(url);
                                MovieDownloadManager.getInstance(mContext).addTask(newMovie);

                                // ?????????????????????
                                MovieDownloadManager.getInstance(mContext).removeTask(movie);
                            }
                        } else {
                            showShort(getString(R.string.download_reload_failed));
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        showShort(getString(R.string.common_network_error));
                    }
                });
    }

    private void reUploadMovie(UploadTaskCache movie) {
        if (movie == null) {
            return;
        }
//        UploadTaskManager.getInstance().removeTask(movie);
//        UploadTaskManager.getInstance().addTask(movie);
    }
    // ------------------ ???????????? end ------------------

    // ------------------------------------------ adapter ???????????????????????????  end  ------------------------------------------------


    /**
     * ????????????UI, ?????????????????????.
     * ???????????????????????????.
     *
     * @param movie
     */
    public void refreshDownloadListIfNeed(CategoryMovieBean movie) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - movie.getLastTimeMillis() < 200) {
            return;
        }
        movie.setLastTimeMillis(currentTime);

        if (mTaskAdapter != null && mTaskAdapter.getDatas() != null) {
            int pos = ((ArrayList) mTaskAdapter.getDatas()).indexOf(movie);
            if (pos >= 0 && pos < mTaskAdapter.getItemCount()) {
                mTaskAdapter.notifyItemChanged(pos);
            }
        }
    }

    /**
     * ?????????????????????????????????
     * ????????? ?????????????????????, ????????????.
     * ??????/????????? ???????????????.
     */
    private void updateDrawerState() {
        // ????????????Drawer????????????, ??????????????????.
        mHandler.removeCallbacks(mRunnable);
        mHandler.postDelayed(mRunnable, 200);
    }

    Runnable mRemoveTakDelayRunnable = null;

    private void removeUpOrDownloadTaskImmediately() {
        if (mRemoveTakDelayRunnable != null) {
            mHandler.removeCallbacks(mRemoveTakDelayRunnable);
            mRemoveTakDelayRunnable.run();
            mRemoveTakDelayRunnable = null;
        }
    }

    private void removeDownloadTaskDelay(CategoryMovieBean movie) {
        // ??????????????????????????????????????????????????????????????????,
        // ????????????????????????????????????
        removeUpOrDownloadTaskImmediately();
        mRemoveTakDelayRunnable = new Runnable() {
            @Override
            public void run() {
                MovieDownloadManager.getInstance(mContext).removeTask(movie);
                showShort(getString(R.string.download_finish_tips) + movie.getTitle());
                mRemoveTakDelayRunnable = null;
            }
        };
        mHandler.postDelayed(mRemoveTakDelayRunnable, 1000);
    }

    private void removeUploadTaskDelay(UploadTaskCache uploadMovie) {
        // ??????????????????????????????????????????????????????????????????,
        // ????????????????????????????????????
        removeUpOrDownloadTaskImmediately();
        mRemoveTakDelayRunnable = new Runnable() {
            @Override
            public void run() {
//                UploadTaskManager.getInstance().removeTask(uploadMovie);
                if (uploadMovie.getTitle() != null) {
                    showShort(getString(R.string.download_upload_finish_tips) + uploadMovie.getTitle());
                }
                mRemoveTakDelayRunnable = null;

                // ??????all??????
//                UploadTaskManager.getInstance().deleteAllVideoUploadDirExpertEditActivity();
            }
        };
        mHandler.postDelayed(mRemoveTakDelayRunnable, 1000);
    }


    /**
     * ?????? - ????????????
     */
    private final IMovieDownloadManager.MovieTaskListener mMovieTaskListener = new IMovieDownloadManager.MovieTaskListener() {

        @Override
        public void addTask(@NonNull CategoryMovieBean movie) {
            mTaskAdapter.addData(movie);
            updateDrawerState();
        }

        @Override
        public void removeTask(@NonNull CategoryMovieBean movie) {
            mTaskAdapter.removeData(movie);
            updateDrawerState();
        }

        @Override
        public void removeAllTask(List<CategoryMovieBean> movies) {
            mTaskAdapter.removeAll(movies);
            updateDrawerState();
        }
    };

    /**
     * ?????? - ???????????? - ????????????
     */
    private final IMovieDownloadManager.MovieDownloadListener mMovieDownloadListener = new IMovieDownloadManager.MovieDownloadListener() {
        @Override
        public void taskStart(@NonNull CategoryMovieBean movie) {
            refreshDownloadListIfNeed(movie);
        }

        @Override
        public void retry(@NonNull CategoryMovieBean movie) {
            refreshDownloadListIfNeed(movie);
        }

        @Override
        public void connected(@NonNull CategoryMovieBean movie, int blockCount, long currentOffset, long totalLength) {
            refreshDownloadListIfNeed(movie);
        }

        @Override
        public void progress(@NonNull CategoryMovieBean movie, long currentOffset, long totalLength) {
            refreshDownloadListIfNeed(movie);
        }

        @Override
        public void taskCancel(@NonNull CategoryMovieBean movie) {
            refreshDownloadListIfNeed(movie);
        }

        @Override
        public void taskCompleted(@NonNull CategoryMovieBean movie) {
            KLog.w(TAG, "taskCompleted: " + movie);
            //refreshDownloadListIfNeed(movie);
            mTaskAdapter.notifyDataSetChanged();

            // ????????????, ?????????????????????
            removeDownloadTaskDelay(movie);
        }

        @Override
        public void taskError(@NonNull CategoryMovieBean movie) {
            KLog.w(TAG, "taskError: " + movie);
            mTaskAdapter.notifyDataSetChanged();


            // ????????????, ????????????
//            DownloadTask task = (DownloadTask) MovieDownloadManager.getInstance(mContext).getTask(movie);
//            if (task.getFile() != null) {
//                task.getFile().delete();
//            }
        }
    };

    /**
     * ?????? - ????????????
     */
    /*private UploadManager.uploadTaskListener mUploadTaskListener = new UploadManager.uploadTaskListener() {
        @Override
        public void addTask(@NonNull UploadTaskCache mCache) {
            KLog.w(TAG, "addTask task = " + mCache);
            mTaskAdapter.addData(mCache);
            updateDrawerState();
        }

        @Override
        public void removeTask(@NonNull UploadTaskCache mCache) {
            KLog.w(TAG, "removeTask task = " + mCache);
            mTaskAdapter.removeData(mCache);
            updateDrawerState();
        }

        @Override
        public void removeAllTask(List<UploadTaskCache> movies) {
            KLog.w(TAG, "removeAllTask tasks = " + movies);

            mTaskAdapter.removeAll(movies);
            updateDrawerState();
        }
    };
*/
    /**
     * ?????? - ???????????? - ????????????
     */
    /*private UploadManager.uploadListener mUploadListener = new UploadManager.uploadListener() {
        @Override
        public void onSuccessUpload(UploadTaskCache mCache, String path) {
            KLog.d(TAG, "onSuccessUpload------------------");
            UploadManager task = (UploadManager) UploadTaskManager.getInstance().getTask(mCache);
            if (task != null) {
                UploadTaskManager.getInstance().deleteAllVideoUploadFile(task.getTag());
            }
            mTaskAdapter.notifyDataSetChanged();

            onActivityUploadSuccess();
            removeUploadTaskDelay(mCache);
        }

        @Override
        public void onErrorUpload(UploadTaskCache mCache, Throwable t) {
            KLog.d(TAG, "onErrorUpload----------");
            if (mContext == null) {
                return;
            }
            if (t instanceof RequestError) {
                RequestError re = (RequestError) t;
                if (re.getMessage() == null || TextUtils.isEmpty(re.getMessage())) {
                    refreshUploadItemCache(mCache);
                    return;
                }
                if (re.getState() == RespCode.CODE_608) { // ????????????
                    refreshUploadItemCache(mCache);
                    return;
                }
                if (re.getState() == RespCode.CODE_612) { // ??????
                    refreshUploadItemCache(mCache);
                    return;
                }
                if (re.getState() == RespCode.CODE_614 || re.getState() == RespCode.CODE_603) { // ??????0?????????????????????
                    KLog.d(re.getMessage());
                    showErrorDialog(re.getMessage());
                    // ??????????????????????????????
                    mCache.setTitle(null);
                    removeUploadTaskDelay(mCache); // ?????????????????????
                    return;
                }
                KLog.d(re.getMessage());
                ToastUtil.showToast(mContext, re.getMessage());
            } else {
                if (t.getMessage() == null || TextUtils.isEmpty(t.getMessage())) {
                    refreshUploadItemCache(mCache);
                    return;
                }
                KLog.d(t.getMessage());
            }
            refreshUploadItemCache(mCache);
        }

        @Override
        public void onProgressUpload(UploadTaskCache mCache, long currentOffset, long totalLength) {
//            KLog.d("progress: mCache = " + mCache + "---------------" + (currentOffset * 100f / totalLength) + "%");
            Log.d(TAG, "onProgressUpload-----------------" + currentOffset);
            refreshUploadItem(mCache);
        }
    };
*/

    private void showErrorDialog(String msg) {
        if (msg == null) {
            return;
        }
        if (mCustomProgress == null) {
            mCustomProgress = new CustomProgress(this);
        }
        if (mCustomProgress != null) {
            mCustomProgress.show(msg,
                    getString(R.string.common_tips_title),
                    null,
                    getString(R.string.common_ensure),
                    null,
                    v -> mCustomProgress.dismiss(),
                    true, dialog -> mCustomProgress.dismiss());
        }
    }

    // ????????????????????????????????????
    private synchronized void showShort(String msg) {
        ToastUtils.showShort(msg);
    }


    private synchronized void refreshUploadItem(UploadTaskCache mCache) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - mCache.getLastTimeMillis() < 200) {
            return;
        }
        mCache.setLastTimeMillis(currentTime);
        refreshUploadItemCache(mCache);
    }

    private synchronized void refreshUploadItemCache(UploadTaskCache mCache) {
        if (mTaskAdapter != null && mTaskAdapter.getDatas() != null) {
            int pos = ((ArrayList) mTaskAdapter.getDatas()).indexOf(mCache);
            if (pos >= 0 && pos < mTaskAdapter.getItemCount()) {
                mTaskAdapter.notifyItemChanged(pos);
            }
        }
    }

    protected void onNetWorkConnected() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mDrawerBehavior.getState() == DrawerSheetBehavior.STATE_EXPANDED) {
            mDrawerBehavior.setState(DrawerSheetBehavior.STATE_COLLAPSED);
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    public Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            if (mTaskAdapter.getItemCount() > 0) {
                // ??????????????????
                // 1. ??????????????????, ?????????-????????????.
                // 2. ??????????????????, ??????????????????.
                if (mDrawerBehavior.getState() == DrawerSheetBehavior.STATE_HIDDEN) {
                    mDrawerBehavior.setState(DrawerSheetBehavior.STATE_COLLAPSED);
                    KLog.d(TAG, "?????? - ??????");
                } else {
                    // do nothing
                    KLog.d(TAG, "?????? - ?????????????????? " + mDrawerBehavior.getState());
                }
            } else {
                // ??????????????????, ????????????
                mDrawerBehavior.setState(DrawerSheetBehavior.STATE_HIDDEN);
                KLog.d(TAG, "??????");
            }
        }
    };

    protected void onActivityUploadSuccess() {
    }


    public class NetworkConnectChangedReceiver extends BroadcastReceiver {
        public void startReceiver(Context context) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            context.registerReceiver(this, filter);
        }

        public void stopReceiver(Context context) {
            context.unregisterReceiver(this);
        }

        private String getConnectionType(int type) {
            String connType = "";
            if (type == ConnectivityManager.TYPE_MOBILE) {
                connType = "3G????????????";
            } else if (type == ConnectivityManager.TYPE_WIFI) {
                connType = "WIFI??????";
            }
            return connType;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            // ??????????????????????????????????????????????????????
            if (!intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                return;
            }
            ConnectivityManager cmm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cmm == null) {
                return;
            }

            Network[] allNetworks = cmm.getAllNetworks();
            if (allNetworks == null || allNetworks.length <= 0) {
                return;
            }

            for (Network network : allNetworks) {
                NetworkInfo networkInfo = cmm.getNetworkInfo(network);
                if (network == null || networkInfo == null) {
                    continue;
                }
                KLog.d(TAG, networkInfo.getTypeName() + "  " + networkInfo.isConnected());
                if (networkInfo.isConnected()) {
                    // ??????????????????????????????????????????...
                    if (MovieDownloadManager.getInstance(context).isStartAllUploadTask()) {
                        MovieDownloadManager.getInstance(context).startAll(true);
                    }
                    /*if (UploadTaskManager.getInstance().isStartAllUploadTask()) {
                        UploadTaskManager.getInstance().startAll();
                    }*/
                    onNetWorkConnected();
                    return;
                }
            }
        }
    }
}
