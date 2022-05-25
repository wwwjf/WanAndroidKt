package com.xianghe.ivy.adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xianghe.ivy.R;
import com.xianghe.ivy.weight.indexbar.suspension.ISuspensionInterface;

import java.util.List;

public class BackgroundSuspensionDecoration extends RecyclerView.ItemDecoration {

    private Paint firstPaint;
    private Paint secondPaint;
    private List<? extends ISuspensionInterface> mDataList;
    private int oldPos;

    public BackgroundSuspensionDecoration(Context context, @NonNull List<? extends ISuspensionInterface> list) {
        firstPaint = new Paint();
        firstPaint.setColor(context.getResources().getColor(R.color.black_color_282834));
        secondPaint = new Paint();
        secondPaint.setColor(context.getResources().getColor(R.color.black_color_1e1e28));
        mDataList = list;
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            int pos = parent.getChildAdapterPosition(child);
            boolean isFist;

            if (pos >= 1 && mDataList!=null && mDataList.size() >= 1) {
                if (mDataList.get(pos).getSuspensionTag() != null &&
                        !mDataList.get(pos).getSuspensionTag().equals(mDataList.get(pos - 1).getSuspensionTag())) {
                    oldPos = pos;
                }

            }
            //新的section时，把pos置为0;
            pos = pos - oldPos;
            isFist = pos % 2 == 0;
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
}
