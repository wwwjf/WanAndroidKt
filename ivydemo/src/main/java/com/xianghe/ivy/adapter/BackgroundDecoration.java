package com.xianghe.ivy.adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xianghe.ivy.R;

/**
 * 两种颜色背景相间
 */
public class BackgroundDecoration extends RecyclerView.ItemDecoration {
    private Paint firstPaint;
    private Paint secondPaint;

    public BackgroundDecoration(Context context) {
        firstPaint = new Paint();
        firstPaint.setColor(context.getResources().getColor(R.color.black_color_282834));
        secondPaint = new Paint();
        secondPaint.setColor(context.getResources().getColor(R.color.black_color_1e1e28));
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            int pos = parent.getChildAdapterPosition(child);
            boolean isFist = pos % 2 == 0;
            float left = child.getLeft();
            float right = child.getRight();
            float top = child.getTop();
            float bottom = child.getBottom();
            if (isFist) {
                c.drawRect(left, top, right, bottom, firstPaint);
            } else {
                c.drawRect(left, top, right, bottom, secondPaint);
            }
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

    }
}