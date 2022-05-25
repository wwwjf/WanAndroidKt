package com.xianghe.ivy.adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.xianghe.ivy.R;

/**
 * 两种不同颜色相间
 */
public class ColorPhaseRvDividerDecoration extends RecyclerView.ItemDecoration {
    private Paint mFirstPaint;
    private Paint mSecondPaint;

    public ColorPhaseRvDividerDecoration(Context context) {
        mFirstPaint = new Paint();
        mFirstPaint.setColor(context.getResources().getColor(R.color.black_color_1e1e28));
        mSecondPaint = new Paint();
        mSecondPaint.setColor(context.getResources().getColor(R.color.black_color_282834));
    }


    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            int pos = parent.getChildAdapterPosition(child);
            boolean isLeft = pos % 2 == 0;
            if (isLeft) {
                float left = child.getLeft();
                float right = child.getRight();
                float top = child.getTop();
                float bottom = child.getBottom();
                c.drawRect(left, top, right, bottom, mFirstPaint);
            } else {
                float left = child.getLeft();
                float right = child.getRight();
                float top = child.getTop();
                float bottom = child.getBottom();
                c.drawRect(left, top, right, bottom, mSecondPaint);

            }
        }
    }
}
