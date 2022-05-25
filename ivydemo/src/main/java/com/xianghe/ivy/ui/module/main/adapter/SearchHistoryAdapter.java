package com.xianghe.ivy.ui.module.main.adapter;

import androidx.annotation.Nullable;

import com.xianghe.ivy.R;
import com.xianghe.ivy.adapter.adapter.BaseQuickAdapter;
import com.xianghe.ivy.adapter.adapter.BaseViewHolder;
import com.xianghe.ivy.entity.db.SearchHistory;

import java.util.List;

/**
 * Created by wengjianfeng on 2018/12/13.
 */

public class SearchHistoryAdapter extends BaseQuickAdapter<SearchHistory, BaseViewHolder> {

    public SearchHistoryAdapter(@Nullable List<SearchHistory> data) {
        super(R.layout.adapter_main_search_history, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchHistory item) {
        helper.setText(R.id.tv_item_main_search_history_keyWord, item.getSearchKey());
        helper.addOnClickListener(R.id.iv_item_main_search_history_delete);
    }
}
