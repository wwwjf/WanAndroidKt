package com.wwwjf.jetpackdemo.recyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RvManager extends GridLayoutManager {
    private static final String TAG = RvManager.class.getName();

    public RvManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public RvManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public RvManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {

        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        return params;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);


    }

    @Override
    public void onMeasure(@NonNull RecyclerView.Recycler recycler, @NonNull RecyclerView.State state, int widthSpec, int heightSpec) {
        super.onMeasure(recycler, state, widthSpec, heightSpec);
        int widthMode = View.MeasureSpec.getMode(widthSpec);
        int heightMode = View.MeasureSpec.getMode(heightSpec);
        switch (widthMode) {
            case View.MeasureSpec.EXACTLY:
                Log.e(TAG, "widthMode-onMeasure: EXACTLY");
                break;
            case View.MeasureSpec.AT_MOST:
                Log.e(TAG, "widthMode-onMeasure: AT_MOST");
                break;
            case View.MeasureSpec.UNSPECIFIED:
                Log.e(TAG, "widthMode-onMeasure: UNSPECIFIED");
                break;
            default:
                break;
        }
        switch (heightMode) {
            case View.MeasureSpec.EXACTLY:
                Log.e(TAG, "heightMode-onMeasure: EXACTLY");
                break;
            case View.MeasureSpec.AT_MOST:
                Log.e(TAG, "heightMode-onMeasure: AT_MOST");
                break;
            case View.MeasureSpec.UNSPECIFIED:
                Log.e(TAG, "heightMode-onMeasure: UNSPECIFIED");
                break;
            default:
                break;
        }
        int widthSize = View.MeasureSpec.getSize(widthSpec);
        int heightSize = View.MeasureSpec.getSize(heightSpec);

        Log.e(TAG, "onMeasure: widthSize:"+widthSize+",heightSize:"+heightSize);
        setMeasuredDimension(widthSize, heightSize);
    }


    @Override
    public void setMeasuredDimension(int widthSize, int heightSize) {
        super.setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    public void measureChild(@NonNull View child, int widthUsed, int heightUsed) {
//        super.measureChild(child, widthUsed, heightUsed);
        Log.e(TAG, "measureChild: measureWidth:" + child.getMeasuredWidth() + ",measureHeight:" + child.getMeasuredHeight());
        Log.e(TAG, "measureChild: widthUsed:" + widthUsed + ",heightUsed:" + heightUsed);
        child.setLayoutParams(new RecyclerView.LayoutParams(1080/getSpanCount(),child.getMeasuredHeight()));
    }
}
