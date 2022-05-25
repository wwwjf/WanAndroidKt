package com.xianghe.ivy.ui.module.videopush.adapter;

import android.widget.TextView;

import androidx.annotation.Nullable;

import com.xianghe.ivy.R;
import com.xianghe.ivy.adapter.adapter.BaseQuickAdapter;
import com.xianghe.ivy.adapter.adapter.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

public abstract class MovieActorAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public MovieActorAdapter() {
        this(new ArrayList<String>());
    }

    public MovieActorAdapter(@Nullable List<String> data) {
        super(R.layout.item_vp_actor, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.item_et_desc, item);
        helper.setOnClickListener(R.id.item_iv_delete, v -> {
            // 删除逻辑
            try {
                remove(helper.getPosition());
            } catch (Exception e) {
                e.printStackTrace();
            }
            remove();
        });
        TextView tv = helper.getView(R.id.item_et_desc);
        tv.setOnClickListener(v -> {
            onClick(helper.getPosition(), item);
        });
    }

    public abstract void onClick(int pos, String content);

    public abstract void remove();

    public String getAppendItem() {
        if (getData() == null || getData().size() == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        int len = getData().size();
        for (int i = 0; i < len; i++) {
            builder.append(getData().get(i));
            if (i < len - 1) {
                builder.append(",");
            }
        }
        return builder.toString();
    }
}
