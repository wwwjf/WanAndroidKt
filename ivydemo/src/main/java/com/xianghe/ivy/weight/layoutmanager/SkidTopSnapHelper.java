package com.xianghe.ivy.weight.layoutmanager;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * author:  ycl
 * date:  2018/12/19 15:23
 * desc:
 */
public class SkidTopSnapHelper extends PagerSnapHelper {
    private int mDirection; // 记录速度

    @Nullable
    @Override
    public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager, @NonNull View targetView) {
        if (layoutManager instanceof SkidTopLayoutManager) {
            int[] out = new int[2];
            if (layoutManager.canScrollHorizontally()) {
                out[0] = ((SkidTopLayoutManager) layoutManager).calculateDistanceToPosition(
                        layoutManager.getPosition(targetView));
                out[1] = 0;
            } else {
                out[0] = 0;
                out[1] = ((SkidTopLayoutManager) layoutManager).calculateDistanceToPosition(
                        layoutManager.getPosition(targetView));
            }
            return out;
        }
        return null;
    }

    @Nullable
    @Override
    public View findSnapView(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager instanceof SkidTopLayoutManager) {
            SkidTopLayoutManager skidTopLayoutManager = (SkidTopLayoutManager) layoutManager;
            int pos = skidTopLayoutManager.getFixedScrollPosition(mDirection, mDirection != 0 ? 0.8f : 0.5f);
            mDirection = 0;
            if (pos != RecyclerView.NO_POSITION) {
                return layoutManager.findViewByPosition(pos);
            }
        }

        return null;
    }

    @Override
    public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
        final int itemCount = layoutManager.getItemCount();
        if (itemCount == 0) {
            return RecyclerView.NO_POSITION;
        }
        if (layoutManager.canScrollHorizontally()) {
            mDirection = velocityX;
        } else {
            mDirection = velocityY;
        }
        if (layoutManager instanceof SkidTopLayoutManager) {
            final boolean forwardDirection;
            if (layoutManager.canScrollHorizontally()) {
                forwardDirection = velocityX > 0;
            } else {
                forwardDirection = velocityY > 0;
            }
            if (forwardDirection) {
                return ((SkidTopLayoutManager) layoutManager).findLastVisibleItemPosition();
            } else {
                return ((SkidTopLayoutManager) layoutManager).findFirstVisibleItemPosition();
            }
        }
        return RecyclerView.NO_POSITION;
    }
}
