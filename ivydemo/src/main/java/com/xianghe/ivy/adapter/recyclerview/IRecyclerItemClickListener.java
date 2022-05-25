package com.xianghe.ivy.adapter.recyclerview;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Administrator on 2018/5/27/027.
 */

public interface IRecyclerItemClickListener {
    public void onItemClick(RecyclerView parent, int position, RecyclerView.ViewHolder holder, View view);
}
