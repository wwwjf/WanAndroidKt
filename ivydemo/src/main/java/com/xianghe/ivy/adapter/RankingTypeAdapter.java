package com.xianghe.ivy.adapter;


import androidx.annotation.Nullable;

import com.xianghe.ivy.R;
import com.xianghe.ivy.adapter.recyclerview.ARecyclerAdapter;

import java.util.List;

public abstract class RankingTypeAdapter<T> extends ARecyclerAdapter<T> {

    public RankingTypeAdapter(@Nullable List<T> datas) {
        super(datas);
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_ranking_type;
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public void setDatas(List<T> datas) {
        super.setDatas(datas);
        notifyDataSetChanged();
    }
}
