package com.wwwjf.jetpackdemo.recyclerview;

import android.content.Context;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wwwjf.jetpackdemo.R;

import java.util.List;

public class RecycleAdapter extends RecyclerView.Adapter<RvViewHolder> {
    public static final String TAG = RecycleAdapter.class.getName();
    private final Context context;
    private final List<String> list;

    public RecycleAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_adapter,parent,false);
        Log.e(TAG, "onCreateViewHolder: width:"+parent.getWidth()+",measureWidth:"+parent.getMeasuredWidth());
//        view.getLayoutParams().width = parent.getMeasuredWidth()/2;
        RvViewHolder rvViewHolder = new RvViewHolder(view);
        return rvViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RvViewHolder holder, int position) {
        holder.textView.setText(list.get(position));
    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }
}
