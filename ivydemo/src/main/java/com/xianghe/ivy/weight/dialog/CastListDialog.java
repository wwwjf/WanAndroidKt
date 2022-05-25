package com.xianghe.ivy.weight.dialog;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xianghe.ivy.R;
import com.xianghe.ivy.adapter.recyclerview.ARecyclerAdapter;
import com.xianghe.ivy.adapter.recyclerview.RecyclerHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 导演 及 演员列表 dialog
 */
public class CastListDialog extends AlertDialog {
    private static final String TAG = "CastListDialog";
    private static final int DEFAULT_DURATION = 2000;

    private Handler mHandler = new Handler();

    private TextView mTvDirector;
    private RecyclerView mViewList;

    private long mDuration = DEFAULT_DURATION;  // 自动消失时长
    private String mDirector;       // 导演
    private List<String> mCasts;    // 演员

    public CastListDialog(@NonNull Context context) {
        super(context, R.style.AppTheme_Dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_cast_list);
        init(savedInstanceState);
        findView(savedInstanceState);
        initView(savedInstanceState);
    }

    private void init(Bundle savedInstanceState) {
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |  // 不抢夺焦点
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;    // window外部可点击
        window.setAttributes(params);
        window.setDimAmount(0);     //  遮罩 0
    }

    private void findView(Bundle savedInstanceState) {
        mTvDirector = findViewById(R.id.tv_director);
        mViewList = findViewById(R.id.view_list);
    }

    private void initView(Bundle savedInstanceState) {
        mTvDirector.setText(mDirector);

        if (mCasts == null || mCasts.size() <= 0) {
            return;
        }
        mViewList.setAdapter(new CastAdapter(mCasts));
        mViewList.setLayoutManager(new GridLayoutManager(getContext(), Math.min(mCasts.size(), 4)));    // 根据演员列表设置Grid行数
    }

    @Override
    public void show() {
        Log.d(TAG, "show: " + this);
        super.show();
        mHandler.postDelayed(mDissmissRunnalbe, mDuration);
    }

    @Override
    public void dismiss() {
        Log.d(TAG, "dismiss: " + this);
        super.dismiss();
        mHandler.removeCallbacks(mDissmissRunnalbe);
    }

    private Runnable mDissmissRunnalbe = new Runnable() {
        @Override
        public void run() {
            dismiss();
        }
    };


    /**
     * 演员列表 adapter
     */
    private static class CastAdapter extends ARecyclerAdapter<String> {

        public CastAdapter(@Nullable List<String> datas) {
            super(datas);
        }

        @Override
        protected void onBindViewData(RecyclerHolder holder, int position, List<String> datas) {
            holder.setText(R.id.tv_name, getItem(position));
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            return R.layout.dialog_cast_list_item;
        }

        @Override
        public String getItem(int position) {
            return mDatas.get(position);
        }
    }

    /**
     * 构建器
     */
    public static class Builder {
        private long duration = DEFAULT_DURATION;
        private String director;
        private List<String> casts = new ArrayList<>();

        public String getDirector() {
            return director;
        }

        public Builder setDirector(String director) {
            this.director = director;
            return this;
        }

        public List<String> getCasts() {
            return casts;
        }

        public Builder setCasts(List<String> casts) {
            if (casts == null) {
                this.casts.clear();
            } else {
                this.casts = casts;
            }
            return this;
        }

        public Builder addCast(String cast) {
            this.casts.add(cast);
            return this;
        }

        public Builder addCast(String cast, int index) {
            this.casts.add(index, cast);
            return this;
        }

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }

        public CastListDialog build(Context context) {
            CastListDialog dialog = new CastListDialog(context);
            dialog.mDirector = director;
            dialog.mCasts = casts;
            dialog.mDuration = duration;
            return dialog;
        }
    }
}
