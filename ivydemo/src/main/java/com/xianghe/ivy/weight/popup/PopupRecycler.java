package com.xianghe.ivy.weight.popup;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.PopupWindow;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class PopupRecycler extends PopupWindow {
    private RecyclerView mViewList;

    public PopupRecycler(Context context, RecyclerView.Adapter adapter, int width, int height) {
        super(width, height);
        mViewList = new RecyclerView(context);
        mViewList.setLayoutManager(new LinearLayoutManager(context));
        mViewList.setAdapter(adapter);
        CardView cardView = new CardView(context);
        cardView.setRadius(20);
        cardView.addView(mViewList);
        setContentView(cardView);

        setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        // 设置PopupWindow是否能响应外部点击事件
        setOutsideTouchable(true);
        setTouchable(true);
    }
}
