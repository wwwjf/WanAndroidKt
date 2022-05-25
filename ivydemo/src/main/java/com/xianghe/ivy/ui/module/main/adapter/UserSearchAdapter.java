package com.xianghe.ivy.ui.module.main.adapter;

import android.text.Html;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.SizeUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.xianghe.ivy.R;
import com.xianghe.ivy.adapter.adapter.BaseQuickAdapter;
import com.xianghe.ivy.adapter.adapter.BaseViewHolder;
import com.xianghe.ivy.manager.UserInfoManager;
import com.xianghe.ivy.model.ListUserBean;

import java.util.List;

public class UserSearchAdapter extends BaseQuickAdapter<ListUserBean, BaseViewHolder> {

    private long mLoginUid;
    RequestOptions requestOptions = new RequestOptions()
            .placeholder(R.mipmap.ic_my_head_default).error(R.mipmap.ic_my_head_default)
            .override(SizeUtils.dp2px(45), SizeUtils.dp2px(45))
            .centerCrop();


    public UserSearchAdapter(@Nullable List<ListUserBean> data) {
        super(R.layout.adapter_main_search_user, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ListUserBean userBean) {
        if (helper.getAdapterPosition() % 2 == 0) {
            helper.itemView.setBackground(mContext.getResources().getDrawable(R.drawable.selector_282834_393945));
        } else {
            helper.itemView.setBackground(mContext.getResources().getDrawable(R.drawable.selector_1e1e28_2e2e38));
        }

        helper.setText(R.id.tv_item_main_search_user_nickname, Html.fromHtml(userBean.getUsername()));
        helper.setText(R.id.tv_item_main_search_user_uid, Html.fromHtml(String.valueOf(mContext.getResources().getString(R.string.common_ivy_uid_hint) + userBean.getUid_html())));
        helper.setGone(R.id.tv_item_main_search_user_follow, false);
        helper.setGone(R.id.tv_item_main_search_user_is_follow, false);
        helper.setGone(R.id.tv_item_main_search_user_is_friend, false);
        if (mLoginUid == -1 || mLoginUid == 0) {
            mLoginUid = UserInfoManager.getUid();
        }
        if (userBean.getIs_attention() == 1) {//关注判断
            helper.setGone(R.id.tv_item_main_search_user_is_follow, true);
        } else {
            helper.setGone(R.id.tv_item_main_search_user_follow, true);
        }

        if (userBean.getIs_friend() == 1){//亲友判断
            helper.setGone(R.id.tv_item_main_search_user_follow, false);
            helper.setGone(R.id.tv_item_main_search_user_is_follow, false);
            helper.setGone(R.id.tv_item_main_search_user_is_friend, true);
        }

        if (mLoginUid == userBean.getUid()) {//自己的信息不显示关注、已关注、亲友
            helper.setGone(R.id.tv_item_main_search_user_follow, false);
            helper.setGone(R.id.tv_item_main_search_user_is_follow, false);
            helper.setGone(R.id.tv_item_main_search_user_is_friend, false);
        }
        helper.addOnClickListener(R.id.tv_item_main_search_user_follow);
        ImageView ivAvatar = helper.itemView.findViewById(R.id.iv_item_main_search_user_avatar);
        Glide.with(mContext).load(userBean.getAvatar()).apply(requestOptions).into(ivAvatar);
    }
}
