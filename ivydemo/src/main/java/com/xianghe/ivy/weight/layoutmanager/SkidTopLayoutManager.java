package com.xianghe.ivy.weight.layoutmanager;

import android.graphics.PointF;

import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;


public class SkidTopLayoutManager extends RecyclerView.LayoutManager implements  RecyclerView.SmoothScroller.ScrollVectorProvider {
    private int mItemViewWidth;
    private int mItemViewHeight;
    private int mItemCount;
    private int mScrollOffset = 0;

    // 指定滚动到具体位置
    private int currentPos = 0;
    private boolean isScrollToPosition = false;
    // 判断是否snackHelper
    private boolean mHasChild = false;
    // 提供对外的具体位置监听
    private OnScrollListener mListener;

    public int findCurrentVisibleItemPosition() {
        return currentPos;
    }


    @Override
    public void scrollToPosition(int position) {
        super.scrollToPosition(position);
        currentPos = position;
        isScrollToPosition = true;
    }

    @Override
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);
        SkidTopSnapHelper mSnapHelper = new SkidTopSnapHelper();
        mSnapHelper.attachToRecyclerView(view);
    }


    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (state.getItemCount() == 0 || state.isPreLayout()) return;
        removeAndRecycleAllViews(recycler);

        if (!mHasChild) {
            mItemViewWidth = getHorizontalSpace();//item的宽
            mItemViewHeight = getVerticalSpace();//item的高
            mHasChild = true;
        }
        mItemCount = getItemCount();

        // 初始化一次
        if (isScrollToPosition) {
            isScrollToPosition = false;
            mScrollOffset = (currentPos + 1) * mItemViewHeight;
        }

        // 不断校验，计算，一定需要 mScrollOffset 为能够滑动的总高度
        mScrollOffset = Math.min(Math.max(mItemViewHeight, mScrollOffset), mItemCount * mItemViewHeight);

        layoutChild(recycler);
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int pendingScrollOffset = mScrollOffset + dy; // 滑动真实的高度
        mScrollOffset = Math.min(Math.max(mItemViewHeight, mScrollOffset + dy), mItemCount * mItemViewHeight);
        layoutChild(recycler);
        return mScrollOffset - pendingScrollOffset + dy;
    }


    @Override
    public boolean canScrollVertically() {
        return true;
    }

    private void layoutChild(RecyclerView.Recycler recycler) {
        if (getItemCount() == 0) return;
        int topItemPosition = (int) Math.floor(mScrollOffset / mItemViewHeight);// 能够显示的总的个数   滑动的个数  1-8
        int topVisibleHeight = -mScrollOffset % mItemViewHeight; // 底面显示的能够看到的高度 0-height


        final int startPos = topItemPosition - 1;
        final int endPos = topItemPosition < mItemCount ? topItemPosition : topItemPosition - 1; // 0-7


        int layoutCount;
        if (endPos > startPos) {
            layoutCount = 2;
        } else {
            layoutCount = 1;
            topVisibleHeight = 0;
        }
        final int childCount = getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            View childView = getChildAt(i);
            int pos = getPosition(childView);
            if (pos > endPos || pos < startPos) {
                //removeAndRecycleView(childView, recycler);
            }
        }
        detachAndScrapAttachedViews(recycler);

        for (int i = layoutCount - 1; i >= 0; i--) {
            View view = recycler.getViewForPosition(startPos + i);
            addView(view);
            measureChildWithMargins(view, 0, 0);
            if (i == 1) {
                layoutDecoratedWithMargins(view, 0, 0, mItemViewWidth, mItemViewHeight);
            } else {
                layoutDecoratedWithMargins(view, 0, topVisibleHeight, mItemViewWidth, topVisibleHeight + mItemViewHeight);
            }
        }

//        if (endPos == mItemCount - 1 && topVisibleHeight == 0) {
//            currentPos = endPos;
//        } else {
        currentPos = startPos;
//        }
        if (mListener != null) {
            if (topVisibleHeight == 0) {
                if (currentPos == RecyclerView.NO_POSITION) {
                    return;
                }
                mListener.stopScroll(currentPos);
            } else {
                mListener.startScroll();
            }
        }
    }

    /**
     * 获取RecyclerView的显示高度
     */
    public int getVerticalSpace() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }

    /**
     * 获取RecyclerView的显示宽度
     */
    public int getHorizontalSpace() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }


    @Nullable
    @Override
    public PointF computeScrollVectorForPosition(int i) {
        return null;
    }


    // --------------------------------  snackHelper start ------------------------------

    public int findFirstVisibleItemPosition() {
        if (mHasChild) {
            int pos = (int) Math.floor(mScrollOffset / mItemViewHeight);
            return pos - 1;
        }
        return RecyclerView.NO_POSITION;
    }

    public int findLastVisibleItemPosition() {
        if (mHasChild) {
            int pos = (int) Math.floor(mScrollOffset / mItemViewHeight);
            if (mScrollOffset % mItemViewHeight == 0) {
                return pos - 1;
            } else {
                return pos;
            }
        }
        return RecyclerView.NO_POSITION;
    }

    public int calculateDistanceToPosition(int targetPos) {
        int pendingScrollOffset = mItemViewHeight * (targetPos + 1);
        return pendingScrollOffset - mScrollOffset;
    }

    public int getFixedScrollPosition(int direction, float fixValue) {
        if (mHasChild) {
            if (mScrollOffset % mItemViewHeight == 0) {
                return RecyclerView.NO_POSITION;
            }
            float position = mScrollOffset * 1.0f / mItemViewHeight;
            return (int) (direction > 0 ? position + fixValue : position + (1 - fixValue)) - 1;
        }
        return RecyclerView.NO_POSITION;
    }
    // --------------------------------  snackHelper end ------------------------------


    public void setListener(OnScrollListener listener) {
        mListener = listener;
    }

    public interface OnScrollListener {
        void stopScroll(int position); // 滚动到具体一点就调用

        void startScroll(); // 开始滚动就调用
    }
}


