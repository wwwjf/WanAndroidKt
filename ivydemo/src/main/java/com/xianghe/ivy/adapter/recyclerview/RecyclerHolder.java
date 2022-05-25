package com.xianghe.ivy.adapter.recyclerview;

import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.RecyclerView;

import com.xianghe.ivy.adapter.IAViewHolder;

/**
 * Created by Administrator on 2018/5/17/017.
 * 万能的RecyclerView.ViewHolder。
 */

public class RecyclerHolder extends RecyclerView.ViewHolder implements IAViewHolder {
    private SparseArray<View> mViews = new SparseArray<>();

    public RecyclerHolder(View itemView) {
        super(itemView);
    }

    @Override
    public View getItemView() {
        return itemView;
    }

    @Override
    public <T extends View> T getView(@IdRes int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        //noinspection unchecked
        return (T) view;
    }

    @Override
    public void setText(@IdRes int viewId, @StringRes int resId) {
        View view = getView(viewId);
        ((TextView) view).setText(view.getContext().getString(resId));
    }

    @Override
    public void setText(@IdRes int viewId, CharSequence text) {
        View view = getView(viewId);
        ((TextView) view).setText(text);
    }

    @Override
    public void setText(TextView view, @StringRes int resId) {
        view.setText(view.getContext().getString(resId));
    }

    @Override
    public void setText(TextView view, CharSequence text) {
        view.setText(text);
    }

    @Override
    public void setTextColor(int viewId, int color) {
        View view = getView(viewId);
        ((TextView) view).setTextColor(color);
    }

    @Override
    public void setImageResource(@IdRes int viewId, @DrawableRes int resId) {
        View view = getView(viewId);
        ((ImageView) view).setImageResource(resId);
    }

    @Override
    public void setImageDrawable(int viewId, Drawable drawable) {
        View view = getView(viewId);
        ((ImageView) view).setImageDrawable(drawable);
    }

    @Override
    public void setVisibility(int visibility, int... viewIds) {
        if (viewIds == null) {
            return;
        }

        for (int viewId : viewIds) {
            getView(viewId).setVisibility(visibility);
        }
    }
}
