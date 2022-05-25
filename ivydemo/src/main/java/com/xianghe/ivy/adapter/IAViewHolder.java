package com.xianghe.ivy.adapter;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.StringRes;

/**
 * Created by LuWei on 2018/8/1.
 */

public interface IAViewHolder {
    public View getItemView();

    public <T extends View> T getView(@IdRes int viewId);

    public void setText(@IdRes int viewId, @StringRes int resId);

    public void setText(@IdRes int viewId, CharSequence text);

    public void setText(TextView view, @StringRes int resId);

    public void setText(TextView view, CharSequence text);

    public void setTextColor(@IdRes int viewId, @ColorInt int color);

    public void setImageResource(@IdRes int viewId, @DrawableRes int resId);

    public void setImageDrawable(@IdRes int viewId, Drawable drawable);

    public void setVisibility(int visible, @IdRes int[] viewIds);
}
