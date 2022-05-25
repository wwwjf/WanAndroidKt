package com.xianghe.ivy.ui.module.videopush.adapter;


import androidx.annotation.Nullable;

import com.xianghe.ivy.R;
import com.xianghe.ivy.adapter.adapter.BaseQuickAdapter;
import com.xianghe.ivy.adapter.adapter.BaseViewHolder;
import com.xianghe.ivy.model.LocationInfo;

import java.util.List;

public class LocationInfoAdapter extends BaseQuickAdapter<LocationInfo, BaseViewHolder> {
    public LocationInfoAdapter(@Nullable List<LocationInfo> data) {
        super(R.layout.item_location, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, LocationInfo item) {
        helper.setText(R.id.tv_location, item.getTitle());
    }
}
