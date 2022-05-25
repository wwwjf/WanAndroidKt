package com.xianghe.ivy.weight;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class MarqueeTextView extends AppCompatTextView {
    private boolean isMarquee = false;

    public MarqueeTextView(Context context) {
        this(context, null);
    }

    public MarqueeTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MarqueeTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFocusable(true);
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        setSingleLine();
        setFocusableInTouchMode(true);
    }

    @Override
    public boolean isFocused() {
        return isMarquee;
    }

    public void startMarquee() {
        isMarquee = true;
        requestFocus();
    }

    public void stopMarquee() {
        isMarquee = false;
        clearFocus();
    }

    public boolean isMarquee() {
        return isMarquee;
    }
}
