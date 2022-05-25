package com.xianghe.ivy.adapter.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by LuWei on 2018/5/17/017.
 * 通用的RecyclerAdapter
 */

public abstract class AbsRecyclerAdapter extends RecyclerView.Adapter<RecyclerHolder> implements View.OnClickListener, View.OnLongClickListener {
    protected IRecyclerItemClickListener mItemClickListener;
    protected IRecyclerItemLongClickListener mItemLongClickListener;

    /**
     * 获取layoutId
     *
     * @param viewType {@link #getItemViewType(int)}
     * @return layoutId
     */
    protected abstract int getItemLayoutId(int viewType);

    @Override
    public RecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(getItemLayoutId(viewType), parent, false);
        RecyclerHolder holder = new RecyclerHolder(itemView);
        holder.itemView.setOnClickListener(this);
        return holder;
    }
    @Override
    public void onClick(View v) {
        RecyclerView recyclerView = RecyclerViewHelper.getParentRecyclerView(v);
        RecyclerView.ViewHolder holder = recyclerView.findContainingViewHolder(v);
        if (v == holder.itemView) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(recyclerView, holder.getAdapterPosition(), holder, v);
            }
        }
    }
    @Override
    public void onBindViewHolder(RecyclerHolder holder, int position) {
        onBindViewData(holder, position);
    }

    /**
     * 当{@link RecyclerView.Adapter#onBindViewHolder(RecyclerView.ViewHolder, int)}
     *
     * @param holder
     * @param position
     */
    protected abstract void onBindViewData(RecyclerHolder holder, int position);

    public void setItemClickListener(IRecyclerItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    public void setItemLongClickListener(IRecyclerItemLongClickListener itemLongClickListener) {
        mItemLongClickListener = itemLongClickListener;
    }

    public abstract Object getItem(int position);



    @Override
    public boolean onLongClick(View v) {
        RecyclerView recyclerView = RecyclerViewHelper.getParentRecyclerView(v);
        RecyclerView.ViewHolder holder = recyclerView.findContainingViewHolder(v);
        if (v == holder.itemView) {
            if (mItemLongClickListener != null) {
                return mItemLongClickListener.onItemLongClick(recyclerView, holder.getAdapterPosition(), holder, v);
            }
        }
        return false;
    }
}
