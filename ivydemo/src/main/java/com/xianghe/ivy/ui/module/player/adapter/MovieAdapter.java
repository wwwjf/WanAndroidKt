package com.xianghe.ivy.ui.module.player.adapter;


import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.xianghe.ivy.R;
import com.xianghe.ivy.adapter.recyclerview.ARecyclerAdapter;
import com.xianghe.ivy.adapter.recyclerview.RecyclerHolder;
import com.xianghe.ivy.model.CategoryMovieBean;
import com.xianghe.ivy.ui.module.player.widget.IvyVideoView;
import com.xianghe.ivy.ui.module.player.widget.IvyVideoWidgetView;

import java.util.List;

public class MovieAdapter extends ARecyclerAdapter<CategoryMovieBean> implements IvyVideoView.GestureListener {
    private static final String TAG = "MovieAdapter";

    private final RequestOptions mOptions = new RequestOptions()
            .error(R.mipmap.img_video_default_cover)
            .placeholder(R.mipmap.img_video_default_cover);

    private IvyVideoView.GestureListener mGestureListener;

    public MovieAdapter(@Nullable List<CategoryMovieBean> datas) {
        super(datas);
    }

    @Override
    public RecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerHolder holder = super.onCreateViewHolder(parent, viewType);
        IvyVideoView videoView = holder.getView(R.id.video_view);
        videoView.setGestureListener(this);
        return holder;
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_movie;
    }

    @Override
    protected void onBindViewData(RecyclerHolder holder, int position, List datas) {
        CategoryMovieBean item = getItem(position);
        IvyVideoView videoView = holder.getView(R.id.video_view);
        videoView.setUp(item.getMediaOssUrl(true), true, item.getTitle());
        videoView.setLooping(true);

        videoView.hasLoadTure = true;
        videoView.isFirstLoad = true;
        videoView.isFirstLoadTwo = true;
        //holder.setText(R.id.tv_title, item.getTitle());

        // 显示地址
//        String address = item.getAddress();
//        boolean isShowAddress = (address != null) && TextUtils.isEmpty(address.trim());
//        holder.setText(R.id.tv_address, address);
//        holder.setVisibility(isShowAddress ? View.GONE : View.VISIBLE, R.id.tv_address);

        Glide.with(holder.itemView)
                .load(item.getCover())
                .apply(mOptions)
                .into((ImageView) holder.getView(R.id.iv_cover));
    }


    @Override
    public CategoryMovieBean getItem(int position) {
       // try {
            return mDatas.get(position);
        /*}catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void setDatas(List<CategoryMovieBean> datas) {
        super.setDatas(datas);
        notifyDataSetChanged();
    }

    public void addDatas(List<CategoryMovieBean> movies) {
        if (movies == null || movies.size() == 0) {
            return;
        }
        mDatas.addAll(movies);
        try {
            notifyItemRangeInserted(mDatas.size() - movies.size(), mDatas.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public IvyVideoView.GestureListener getGestureListener() {
        return mGestureListener;
    }

    public void setGestureListener(IvyVideoView.GestureListener gestureListener) {
        mGestureListener = gestureListener;
    }


    @Override
    public void onRight2LeftFling(View ivyVideoView, float deltaX, float deltaY, float totalMoveX, float totalMoveY) {
        if (mGestureListener != null) {
            mGestureListener.onRight2LeftFling(ivyVideoView, deltaX, deltaY, totalMoveX, totalMoveY);
        }
    }

    @Override
    public void onLeft2RightFling(View ivyVideoView, float deltaX, float deltaY, float totalMoveX, float totalMoveY) {
        if (mGestureListener != null) {
            mGestureListener.onLeft2RightFling(ivyVideoView, deltaX, deltaY, totalMoveX, totalMoveY);
        }
    }

    @Override
    public void onDoubleTap(View ivyVideoView, MotionEvent event) {
        if (mGestureListener != null) {
            mGestureListener.onDoubleTap(ivyVideoView, event);
        }
    }
}
