package com.xianghe.ivy.weight.layoutmanager.topgravity;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.xianghe.ivy.utils.KLog;

public class TopGravitySnapHelper extends SnapHelper {
    private static final String TAG = "TopGravitySnapHelper";

    @Nullable
    @Override
    public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager, @NonNull View view) {
        KLog.d(TAG, "view.top = " + view.getTop() + ", view.bottom = " + view.getBottom());

        if (layoutManager instanceof TopGravityLayoutManager) {
            int currentPosition = ((TopGravityLayoutManager) layoutManager).findCurrentPosition();
            int targetPosition = layoutManager.getPosition(view);

            View currentItem = layoutManager.findViewByPosition(currentPosition);
            if (targetPosition > currentPosition) {
                return new int[]{0, currentItem.getBottom()};
            } else {
                return new int[]{0, currentItem.getTop()};
            }
        }
        return new int[]{0, 0};
    }

    @Nullable
    @Override
    public View findSnapView(RecyclerView.LayoutManager layoutManager) {
        KLog.i(TAG, "layoutManager = " + layoutManager);
        if (layoutManager instanceof TopGravityLayoutManager) {
            int currentPosition = ((TopGravityLayoutManager) layoutManager).findCurrentPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                return layoutManager.findViewByPosition(currentPosition);
            }
        }
        return null;
    }

    @Override
    public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
        KLog.w(TAG, "velocityX = " + velocityX + ", velocityY = " + velocityY);
        if (layoutManager instanceof TopGravityLayoutManager) {
            TopGravityLayoutManager topLayoutManager = (TopGravityLayoutManager) layoutManager;
            if (velocityY > 0) {
                KLog.w(TAG, "去到下一页：" + topLayoutManager.findNextPosition());
                return topLayoutManager.findNextPosition();
            } else if (velocityY < 0) {
                KLog.w(TAG, "去到上一页：" + topLayoutManager.findCurrentPosition());
                return topLayoutManager.findCurrentPosition();
            } else {
                KLog.w(TAG, "回复当前页：" + topLayoutManager.findCurrentPosition());
                return topLayoutManager.findCurrentPosition();
            }
        }

        return RecyclerView.NO_POSITION;
    }

    @Override
    public int[] calculateScrollDistance(int velocityX, int velocityY) {
        KLog.d(TAG, "velocityX = " + velocityX + ", velocityY = " + velocityY);
        return super.calculateScrollDistance(velocityX, velocityY);
    }
}
