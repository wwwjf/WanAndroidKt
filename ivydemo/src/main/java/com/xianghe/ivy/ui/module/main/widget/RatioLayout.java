package com.xianghe.ivy.ui.module.main.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xianghe.ivy.R;

import java.util.ArrayList;

public class RatioLayout extends FrameLayout {
    private static final String TAG = "ScaleLayout";

    private final ArrayList<View> mMatchParentChildren = new ArrayList<>(1);

    private float mWidthScale = 0;
    private float mHeightScale = 0;

    public RatioLayout(@NonNull Context context) {
        this(context, null);
    }

    public RatioLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RatioLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RatioLayout);
        String dimensionRatio = a.getString(R.styleable.RatioLayout_dimension_ratio);
        if (!TextUtils.isEmpty(dimensionRatio)) {
            String[] ratio = dimensionRatio.split(":");
            mWidthScale = Float.valueOf(ratio[0]);
            mHeightScale = Float.valueOf(ratio[1]);
        }
        a.recycle();
    }

    public void setAspectScale(float width, float height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Size cannot be negative.");
        }

        mWidthScale = width;
        mHeightScale = height;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mWidthScale <= 0 || mHeightScale <= 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        int count = getChildCount();

        Log.d(TAG, MeasureSpec.toString(widthMeasureSpec) + ", " + MeasureSpec.toString(heightMeasureSpec));
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        // 如果不是 MeasureSpec.EXACTLY 就去测量child
        final boolean measureMatchParentChildren = modeWidth != MeasureSpec.EXACTLY || modeHeight != MeasureSpec.EXACTLY;
        mMatchParentChildren.clear();

        int maxHeight = 0;
        int maxWidth = 0;
        int childState = 0;

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (getMeasureAllChildren() || child.getVisibility() != GONE) {
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                maxWidth = Math.max(maxWidth,
                        child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin);
                maxHeight = Math.max(maxHeight,
                        child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin);
                childState = combineMeasuredStates(childState, child.getMeasuredState());
                if (measureMatchParentChildren) {
                    if (lp.width == LayoutParams.MATCH_PARENT ||
                            lp.height == LayoutParams.MATCH_PARENT) {
                        mMatchParentChildren.add(child);
                    }
                }
            }
        }

        // Check against our minimum height and width
        maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());

        // Check against our foreground's minimum height and width
        final Drawable drawable = getForeground();
        if (drawable != null) {
            maxHeight = Math.max(maxHeight, drawable.getMinimumHeight());
            maxWidth = Math.max(maxWidth, drawable.getMinimumWidth());
        }


        // by luwei start
        // setMeasuredDimension(
        // resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
        // resolveSizeAndState(maxHeight, heightMeasureSpec, childState << MEASURED_HEIGHT_STATE_SHIFT));

        if (maxWidth < maxHeight * mWidthScale / mHeightScale) {
            int finalHeight = (int) (maxWidth * mHeightScale / mWidthScale);
            setMeasuredDimension(maxWidth, finalHeight);
            measureChildren(MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(finalHeight, MeasureSpec.EXACTLY));
        } else {
            int finalWidth = (int) (maxHeight * mWidthScale / mHeightScale);
            setMeasuredDimension(finalWidth, maxHeight);
            measureChildren(MeasureSpec.makeMeasureSpec(finalWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.EXACTLY));
        }
        // by luwei end

        count = mMatchParentChildren.size();
        if (count > 1) {
            for (int i = 0; i < count; i++) {
                final View child = mMatchParentChildren.get(i);
                final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

                final int childWidthMeasureSpec;
                if (lp.width == LayoutParams.MATCH_PARENT) {
                    final int width = Math.max(0, getMeasuredWidth()
                            - lp.leftMargin - lp.rightMargin);
                    childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                            width, MeasureSpec.EXACTLY);
                } else {
                    childWidthMeasureSpec = getChildMeasureSpec(getMeasuredWidth(),
                            lp.leftMargin + lp.rightMargin,
                            lp.width);
                }

                final int childHeightMeasureSpec;
                if (lp.height == LayoutParams.MATCH_PARENT) {
                    final int height = Math.max(0, getMeasuredHeight()
                            - lp.topMargin - lp.bottomMargin);
                    childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                            height, MeasureSpec.EXACTLY);
                } else {
                    childHeightMeasureSpec = getChildMeasureSpec(getMeasuredHeight(),
                            lp.topMargin + lp.bottomMargin,
                            lp.height);
                }

                child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            }
        }


    }
}
