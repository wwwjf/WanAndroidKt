package com.xianghe.ivy.ui.module.record.local_media;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xianghe.ivy.R;
import com.xianghe.ivy.mvp.BaseActivity;
import com.xianghe.ivy.mvp.BaseVideoCallActivity;

import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

public class PhotoActivity extends BaseVideoCallActivity {
    private static final String KEY_DATA = "key_data";

    public static Intent getStartIntent(Context context, String data) {
        Intent intent = new Intent(context, PhotoActivity.class);
        intent.putExtra(KEY_DATA, data);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        Intent intent = getIntent();
        Serializable data = intent.getSerializableExtra(KEY_DATA);

        ImageView imageView = (ImageView) findViewById(R.id.iv_image);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Glide.with(this)
                .load(data)
                .into(imageView);
    }
}
//public class PhotoActivity extends BaseActivity {
//    private static final String KEY_DATA = "key_data";
//
//    private RecyclerView mViewList;
//
//    public static Intent getStartIntent(Context context, ArrayList<? extends Serializable> data) {
//        Intent intent = new Intent(context, PhotoActivity.class);
//        intent.putExtra(KEY_DATA, data);
//        return intent;
//    }
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_photo);
//        Intent intent = getIntent();
//        ArrayList<Serializable> data = (ArrayList<Serializable>) intent.getSerializableExtra(KEY_DATA);
//        mViewList = findViewById(R.id.view_list);
//        mViewList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
//        mViewList.setAdapter(new MyAdapter(data));
//
//        PagerSnapHelper snapHelper = new PagerSnapHelper();
//        snapHelper.attachToRecyclerView(mViewList);
//    }
//
//    private class MyAdapter extends ARecyclerAdapter<Serializable> {
//
//        public MyAdapter(@Nullable List datas) {
//            super(datas);
//        }
//
//        @Override
//        public RecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            RecyclerHolder holder = super.onCreateViewHolder(parent, viewType);
//            return holder;
//        }
//
//        @Override
//        protected void onBindViewData(RecyclerHolder holder, int position, List datas) {
//            Object item = getItem(position);
//            Glide.with(PhotoActivity.this)
//                    .load(item)
//                    .into((ImageView) holder.getView(R.id.iv_image));
//        }
//
//        @Override
//        protected int getItemLayoutId(int viewType) {
//            return R.layout.item_photo;
//        }
//
//        @Override
//        public Serializable getItem(int position) {
//            return mDatas.get(position);
//        }
//
//        @Override
//        public void onClick(View v) {
//            PhotoActivity.this.finish();
//        }
//    }
//}
