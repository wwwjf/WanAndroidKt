package com.wwwjf.jetpackdemo.recyclerview;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.wwwjf.jetpackdemo.R;

public class RvViewHolder extends RecyclerView.ViewHolder {

    ImageView imageView;
    TextView textView;
    public RvViewHolder(View view){
        super(view);
        imageView = view.findViewById(R.id.iv_item_adapter);
        textView = view.findViewById(R.id.tv_item_adapter);
    }
}
