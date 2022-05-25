package com.xianghe.ivy.ui.module.main.adapter;

import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.SizeUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.xianghe.ivy.R;
import com.xianghe.ivy.adapter.adapter.BaseQuickAdapter;
import com.xianghe.ivy.adapter.adapter.BaseViewHolder;
import com.xianghe.ivy.model.ListMediaBean;

import java.util.List;

public class MovieSearchAdapter extends BaseQuickAdapter<ListMediaBean, BaseViewHolder> {

    RequestOptions requestOptions = new RequestOptions()
            .placeholder(R.mipmap.ic_my_head_default).error(R.mipmap.ic_my_head_default)
            .override(SizeUtils.dp2px(45), SizeUtils.dp2px(45))
            .centerCrop();


    public MovieSearchAdapter(@Nullable List<ListMediaBean> data) {
        super(R.layout.adapter_main_search_movie, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ListMediaBean item) {
        if (helper.getAdapterPosition() % 2 ==0){
            helper.itemView.setBackground(mContext.getResources().getDrawable(R.drawable.selector_282834_393945));
        } else {
            helper.itemView.setBackground(mContext.getResources().getDrawable(R.drawable.selector_1e1e28_2e2e38));
        }

        helper.setText(R.id.tv_item_main_search_movie_name, Html.fromHtml(item.getTitle()));
        Spanned desc = Html.fromHtml(item.getDescription());
        helper.setText(R.id.tv_item_main_search_movie_introduction, TextUtils.isEmpty(desc) ? mContext.getString(R.string.media_empty_description) : desc);
        ImageView ivAvatar = helper.itemView.findViewById(R.id.iv_item_main_search_movie_avatar);
        Glide.with(mContext).load(item.getAvatar()).apply(requestOptions).into(ivAvatar);
        ImageView ivCover = helper.itemView.findViewById(R.id.iv_item_main_search_movie_cover);
        Glide.with(mContext).load(item.getCover()).into(ivCover);

    }
}
