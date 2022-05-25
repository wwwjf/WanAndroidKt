package com.xianghe.ivy.ui.module.pic2video;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.xianghe.ivy.R;
import com.xianghe.ivy.adapter.recyclerview.ARecyclerAdapter;
import com.xianghe.ivy.adapter.recyclerview.RecyclerHolder;
import com.xianghe.ivy.adapter.recyclerview.RecyclerViewHelper;
import com.xianghe.ivy.model.MusicBean;

import java.util.List;

public class MusicListAdapter extends ARecyclerAdapter<MusicBean> {
    private int mSelectedItem = -1;

    public MusicListAdapter() {
        super(null);
    }

    @Override
    public RecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerHolder holder = super.onCreateViewHolder(parent, viewType);
        holder.getView(R.id.btn_add).setOnClickListener(this);
        return holder;
    }

    @Override
    protected void onBindViewData(RecyclerHolder holder, int position, List<MusicBean> datas) {
        MusicBean musicBean = getItem(position);


        boolean selected = position == mSelectedItem;
        holder.itemView.setSelected(selected);
        holder.setText(R.id.tv_music_name, musicBean.getMusic_name());

        ImageView ivState = holder.getView(R.id.iv_state);
        Drawable oldDrawable = ivState.getDrawable();
        if (oldDrawable instanceof AnimationDrawable) {
            ((AnimationDrawable) oldDrawable).stop();
        }

        if (selected) {
            ivState.setImageResource(R.drawable.animation_list_ve_music);
            Drawable newDrawable = ivState.getDrawable();
            if (newDrawable instanceof AnimationDrawable) {
                ((AnimationDrawable) newDrawable).start();
            }
        } else {
            ivState.setImageResource(R.drawable.icon_ve_dialog_item_music_def_logo);
        }
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_music;
    }

    @Override
    public MusicBean getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public void setDatas(List<MusicBean> datas) {
        super.setDatas(datas);
        notifyDataSetChanged();
    }

    public void selectedItem(int position) {
        mSelectedItem = position;
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        RecyclerView recyclerView = RecyclerViewHelper.getParentRecyclerView(v);
        RecyclerView.ViewHolder holder = recyclerView.findContainingViewHolder(v);
        if (mItemClickListener != null) {
            mItemClickListener.onItemClick(recyclerView, holder.getAdapterPosition(), holder, v);
        }
    }
}
