package com.xianghe.ivy.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.StatusUtil;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.xianghe.ivy.R;
import com.xianghe.ivy.adapter.recyclerview.ARecyclerAdapter;
import com.xianghe.ivy.adapter.recyclerview.RecyclerHolder;
import com.xianghe.ivy.adapter.recyclerview.RecyclerViewHelper;
import com.xianghe.ivy.entity.db.UploadTaskCache;
import com.xianghe.ivy.manager.download.IMovieDownloadManager;
import com.xianghe.ivy.manager.download.MovieDownloadManager;
import com.xianghe.ivy.model.CategoryMovieBean;
import com.xianghe.ivy.model.Movie;
import com.xianghe.ivy.utils.KLog;
import com.xianghe.ivy.weight.WaterFlowProgress;

import java.util.List;


public class DownloadItemAdapter extends ARecyclerAdapter<Movie> {
    //private List<Movie> mTaskErrorList = new ArrayList<>(); // 记录下载任务错误, 临时解决方案, 不要乱看, 看多眼睛会瞎.

    private RequestOptions options = new RequestOptions()
            .placeholder(R.mipmap.img_tongyong200)
            .error(R.mipmap.img_tongyong200);


    public DownloadItemAdapter() {
        super(null);
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_download;
    }

    @Nullable
    @Override
    public Movie getItem(int position) {
        if (position < 0 || position >= getItemCount()) {
            return null;
        }
        try {
            return mDatas.get(position);
        } catch (Exception e) {
            // do nothing
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public RecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerHolder holder = super.onCreateViewHolder(parent, viewType);
        holder.itemView.setOnClickListener(this);
        holder.itemView.setOnLongClickListener(this);
        return holder;
    }

    @Override
    protected void onBindViewData(RecyclerHolder holder, int position, List<Movie> datas) {
        Movie movie = getItem(position);

        ImageView ivCover = holder.getView(R.id.iv_cover);
        Glide.with(ivCover)
                .load(movie.getCoverUrl())
                .apply(options)
                .into(ivCover);

        if (movie instanceof CategoryMovieBean) {
            updateDownloadInfo(holder, (CategoryMovieBean) movie);

        } else if (movie instanceof UploadTaskCache) {
            updateUploadInfo(holder, (UploadTaskCache) movie);
        }
    }

    private void updateUploadInfo(RecyclerHolder holder, UploadTaskCache movie) {
        WaterFlowProgress flowProgress = holder.getView(R.id.wfProgress);

        int progress = 0;
        long offset = movie.getOffset();
        long totalLength = movie.getTotelLength();
        if (totalLength > 0) {
            progress = (int) (offset * 100 / totalLength);
        }

        /*UploadManager.Status status = UploadTaskManager.getInstance().getTaskState(movie);
//        KLog.d("DownloadItemAdapter", "updateUploadInfo: " + status + " progress: " + progress);
        switch (status) {
            // --- 针对视频 start----
            case VIDEO_START:
                flowProgress.startVideo();
                flowProgress.setProgress(0);
                holder.setVisibility(View.GONE, R.id.iv_error);
                break;
            case VIDEO_RUNNING:
                flowProgress.startVideo();
                flowProgress.setProgress(progress);
                holder.setVisibility(View.GONE, R.id.iv_error);
                break;
            case VIDEO_COMPLETED:
                flowProgress.setProgress(100);
                flowProgress.completeVideo();
                holder.setVisibility(View.GONE, R.id.iv_error);
                break;
            // --- 针对视频 end----

            case COMPLETED:
                flowProgress.completeVideo(); // 有时候未刷新，所以此处再次开启背景
                flowProgress.setProgress(100);
                holder.setVisibility(View.GONE, R.id.iv_error);
                break;
            case RUNNING:
                flowProgress.completeVideo(); // 有时候未刷新，所以此处再次开启背景
                flowProgress.resume();
                flowProgress.setProgress(progress);
                holder.setVisibility(View.GONE, R.id.iv_error);
                break;
            case PENDING:
            case START:
                flowProgress.setProgress(0);
                holder.setVisibility(View.GONE, R.id.iv_error);
                break;
            case CANCEL:
                flowProgress.pause();
                holder.setVisibility(View.GONE, R.id.iv_error);
                break;
            case ERROR:
            default: // 已下载,未知
                flowProgress.pause();
                holder.setVisibility(View.VISIBLE, R.id.iv_error);
                break;
        }*/
    }

    private void updateDownloadInfo(RecyclerHolder holder, CategoryMovieBean movie) {
        final Context context = holder.itemView.getContext();
        MovieDownloadManager downloadManager = (MovieDownloadManager) MovieDownloadManager.getInstance(context);
        IMovieDownloadManager.Status status = downloadManager.getTaskState(movie);

        int progress = 0;
        DownloadTask task = (DownloadTask) downloadManager.getTask(movie);
        if (task != null) {
            BreakpointInfo info = StatusUtil.getCurrentInfo(task);
            if (info != null) {
                long totalOffset = info.getTotalOffset();
                long totalLength = info.getTotalLength();

                if (totalLength <= 0) {
                    progress = 0;
                } else {
                    progress = (int) (totalOffset * 100 / totalLength);
                }
            }
        }

        WaterFlowProgress flowProgress = holder.getView(R.id.wfProgress);

        flowProgress.completeVideo(); // 有时候未刷新，所以此处再次开启背景  暂时解决下载出现的异常问题

        KLog.d("status: " + status);
        switch (status) {
            case COMPLETED:
                flowProgress.setProgress(100);
                break;
            case RUNNING:
                flowProgress.setProgress(progress);
                break;
            case PENDING: // 准备ok
            case IDLE: // 暂停
                break;
            case UNKNOWN:
            default: // 已下载,未知
                flowProgress.setProgress(progress);
                break;
        }

        // error
        try {
            IMovieDownloadManager.Error errorStatue = downloadManager.getErrorStatue(movie);
            if (errorStatue != null) {
                flowProgress.pause();

                switch (errorStatue) {
                    case CANCELED:
                    case SAME_TASK_BUSY:
                        holder.setVisibility(View.GONE, R.id.iv_error);
                        break;
                    case COMPLETED:
                        flowProgress.resume();
                        break;
                    default:
                        holder.setVisibility(View.VISIBLE, R.id.iv_error);
                        break;
                }

            } else {
                flowProgress.resume();
                holder.setVisibility(View.GONE, R.id.iv_error);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Override
    public void setDatas(List<Movie> datas) {
        super.setDatas(datas);
        notifyDataSetChanged();
    }

    public void addData(Movie movie) {
        if (movie == null) {
            return;
        }
        mDatas.add(movie);
        notifyItemInserted(getItemCount());
    }

    public void addData(int index, Movie movie) {
        if (movie == null) {
            return;
        }
        mDatas.add(index, movie);
        notifyItemInserted(index);
    }

    public void addDatas(List<? extends Movie> movies) {
        if (movies == null) {
            return;
        }
        mDatas.addAll(movies);
        notifyDataSetChanged();
    }

    public Movie removeData(int index) {
        if (index < 0 || index >= getItemCount()) {
            return null;
        }
        Movie result = null;
        result = mDatas.remove(index);
        notifyItemRemoved(index);
        return result;
    }

    public void removeData(Movie movie) {
        int index = findMovie(movie);
        if (index != -1) {
            removeData(index);
        }
    }

    public void removeAll(List<? extends Movie> list) {
        mDatas.removeAll(list);
        notifyDataSetChanged();
    }

    private int findMovie(Movie movie) {
        int index = -1;
        if (movie == null) {
            return index;
        }

        for (int i = 0; i < mDatas.size(); i++) {
            if (mDatas.get(i).equals(movie)) {
                index = i;
                break;
            }
        }
        return index;
    }

    @Override
    public void onClick(View v) {
        RecyclerView recyclerView = RecyclerViewHelper.getParentRecyclerView(v);
        RecyclerView.ViewHolder holder = recyclerView.findContainingViewHolder(v);

        mItemClickListener.onItemClick(recyclerView, holder.getAdapterPosition(), holder, v);
    }
}
