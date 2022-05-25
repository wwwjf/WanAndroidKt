package com.xianghe.ivy.adapter.recyclerview;

import android.content.Context;
import android.util.SparseArray;
import android.util.SparseBooleanArray;

import java.util.List;

/**
 * @Author: ycl
 * @Date: 2018/10/29 16:32
 * @Desc:
 */
public abstract class CommonRecyclerViewAdapter<T> extends AbsRecyclerAdapter {
    protected Context mContext;
    protected List<T> mDatas;
    protected final int mItemLayoutId;
    // 用来控制选中情况
    protected final SparseBooleanArray isSelected = new SparseBooleanArray();


    public CommonRecyclerViewAdapter(Context context, List<T> datas, int itemLayoutId) {
        mContext = context;
        mDatas = datas;
        mItemLayoutId = itemLayoutId;
        initData();
    }

    public void initData() {
        synchronized (isSelected) {
            isSelected.clear();
            for (int i = 0; i < mDatas.size(); i++) {
                isSelected.put(i, false);
            }
        }
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return mItemLayoutId;
    }

    @Override
    protected void onBindViewData(RecyclerHolder holder, int position) {
        onBindViewData(holder, position, getItem(position));
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    public abstract void onBindViewData(RecyclerHolder helper, int position, T item);


    public SparseBooleanArray getIsSelected() {
        return isSelected;
    }

}
