package com.xianghe.ivy.adapter;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.blankj.utilcode.util.SizeUtils;

import java.util.Objects;

/**
 * 列表底部间隔
 */
public class BottomRvDividerDecoration extends RecyclerView.ItemDecoration {

    private int getSpanCount(RecyclerView parent) {
        // 列数
        int spanCount = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {

            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            spanCount = ((StaggeredGridLayoutManager) layoutManager)
                    .getSpanCount();
        }
        return spanCount;
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int itemPosition = parent.getChildAdapterPosition(view);
        int bottomPosition;
        if (getSpanCount(parent) != -1) {
            bottomPosition = Objects.requireNonNull(parent.getLayoutManager()).getItemCount() - getSpanCount(parent);
        } else {
            bottomPosition = Objects.requireNonNull(parent.getLayoutManager()).getItemCount();
        }
        if (itemPosition >= bottomPosition - 1) {//最后一行
            outRect.set(0, 0, 0, SizeUtils.dp2px(20));
        } else {
            outRect.set(0, 0, 0, 0);
        }
    }
}
