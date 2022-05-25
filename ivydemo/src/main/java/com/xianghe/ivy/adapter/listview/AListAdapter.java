package com.xianghe.ivy.adapter.listview;

import android.view.View;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public abstract class AListAdapter<T> extends AbsListAdapter<ListViewHolder> {
    private List<T> mDatas;

    public AListAdapter(List<T> datas) {
        if (datas == null) {
            mDatas = new ArrayList<>();
        } else {
            mDatas = datas;
        }
    }

    @Override
    protected ListViewHolder createViewHolder(int position, @NonNull View itemView) {
        return new ListViewHolder(itemView);
    }

    @Override
    public int getCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    public void setDatas(List<T> datas) {
        if (datas == null) {
            mDatas = new ArrayList<>();
        } else {
            mDatas.clear();
        }
    }
}
