package com.xianghe.ivy.ui.module.videopush.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xianghe.ivy.R;
import com.xianghe.ivy.adapter.adapter.BaseQuickAdapter;
import com.xianghe.ivy.model.LocationInfo;
import com.xianghe.ivy.ui.module.videopush.adapter.LocationInfoAdapter;
import com.xianghe.ivy.utils.KLog;

import java.util.List;

/**
 * @Author: ycl
 * @Date: 2018/10/25 14:11
 * @Desc:
 */
public class VideoPushLocationDialog extends Dialog {

    private RecyclerView mRecyclerView;

    private List<LocationInfo> mLocationList;
    private LocationInfoAdapter mLocationAdapter;
    private onItemClickListener mClickListener;


    public VideoPushLocationDialog(@NonNull Context context, List<LocationInfo> list, onItemClickListener clickListener) {
        super(context, R.style.DialogStyleRightTranslut);
        this.mClickListener = clickListener;
        this.mLocationList = list;
        setCanceledOnTouchOutside(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_video_push_location);
        this.mRecyclerView = (RecyclerView) findViewById(R.id.vp_dialog_recycleview);
        initAdapter();
    }

    private void initAdapter() {
        mLocationAdapter = new LocationInfoAdapter(mLocationList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //添加自定义分割线
        DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(getContext().getResources().getDrawable(R.drawable.shape_rectangle_soild_999999));
        mRecyclerView.addItemDecoration(divider);

        mRecyclerView.setAdapter(mLocationAdapter);
        mLocationAdapter.bindToRecyclerView(mRecyclerView);
        mLocationAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                List<LocationInfo> data = adapter.getData();
                if (position >= 0 && position < data.size()) {
                    Object obj = data.get(position);
                    if (null != obj && obj instanceof LocationInfo) {
                        LocationInfo locationInfo = (LocationInfo) obj;
                        if (mClickListener != null) {
                            VideoPushLocationDialog.this.dismiss();
                            mClickListener.click(locationInfo);
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 设置window偏右
        getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getWindow().getAttributes().gravity = Gravity.RIGHT;
    }


    public interface onItemClickListener {
        public void click(LocationInfo locationInfo);
    }
}

