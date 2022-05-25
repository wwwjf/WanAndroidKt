package com.xianghe.ivy.ui.module.main.adapter;

import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.ImageView;

import com.blankj.utilcode.util.SizeUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.xianghe.ivy.R;
import com.xianghe.ivy.adapter.adapter.BaseSectionMultiItemQuickAdapter;
import com.xianghe.ivy.adapter.adapter.BaseViewHolder;
import com.xianghe.ivy.manager.UserInfoManager;
import com.xianghe.ivy.model.ListUserBean;
import com.xianghe.ivy.model.MainSearchSectionMultiEntity;

import java.util.List;

public class MainSearchAdapter extends BaseSectionMultiItemQuickAdapter<MainSearchSectionMultiEntity, BaseViewHolder> {

    private int sectionPosition;
    private long mLoginUid;

    RequestOptions requestOptionsAvatar = new RequestOptions()
            .placeholder(R.mipmap.ic_my_head_default).error(R.mipmap.ic_my_head_default)
            .override(SizeUtils.dp2px(45), SizeUtils.dp2px(45))
            .centerCrop();

    public MainSearchAdapter(List<MainSearchSectionMultiEntity> data) {
        super(R.layout.adapter_main_search_header, data);
        addItemType(MainSearchSectionMultiEntity.ITEM_TYPE_USER, R.layout.adapter_main_search_user);
        addItemType(MainSearchSectionMultiEntity.ITEM_TYPE_MOVIE, R.layout.adapter_main_search_movie);
    }

    @Override
    protected void convertHead(BaseViewHolder helper, MainSearchSectionMultiEntity item) {
        sectionPosition = 0;
        helper.setText(R.id.tv_item_user_search_header, item.header);
        helper.setGone(R.id.tv_item_user_search_header_more, item.isMore());
        helper.setText(R.id.tv_item_user_search_header_more, mContext.getResources().getString(R.string.common_more) + item.header);
        helper.addOnClickListener(R.id.tv_item_user_search_header_more);
    }

    @Override
    protected void convert(BaseViewHolder helper, MainSearchSectionMultiEntity item) {

        if (sectionPosition % 2 == 0) {
            helper.itemView.setBackground(mContext.getResources().getDrawable(R.drawable.selector_282834_393945));
        } else {
            helper.itemView.setBackground(mContext.getResources().getDrawable(R.drawable.selector_1e1e28_2e2e38));
        }
        sectionPosition++;

        if (mLoginUid == -1 || mLoginUid == 0) {
            mLoginUid = UserInfoManager.getUid();
        }
        switch (helper.getItemViewType()) {
            case MainSearchSectionMultiEntity.ITEM_TYPE_USER://用户
                if (item != null && item.t != null && item.t.getUserBean() != null) {
                    ListUserBean userBean = item.t.getUserBean();
                    helper.setText(R.id.tv_item_main_search_user_nickname, Html.fromHtml(userBean.getUsername()));
                    helper.setText(R.id.tv_item_main_search_user_uid, Html.fromHtml(String.valueOf(mContext.getResources().getString(R.string.common_ivy_uid_hint) + userBean.getUid_html())));
                    helper.setGone(R.id.tv_item_main_search_user_follow, false);
                    helper.setGone(R.id.tv_item_main_search_user_is_follow, false);
                    helper.setGone(R.id.tv_item_main_search_user_is_friend, false);
                    if (userBean.getIs_attention() == 1) {//关注判断
                        helper.setGone(R.id.tv_item_main_search_user_is_follow, true);
                    } else {
                        helper.setGone(R.id.tv_item_main_search_user_follow, true);
                    }

                    if (userBean.getIs_friend() == 1){//亲友判断
                        helper.setGone(R.id.tv_item_main_search_user_follow, false);
                        helper.setGone(R.id.tv_item_main_search_user_is_follow, false);
                        helper.setGone(R.id.tv_item_main_search_user_is_friend,true);
                    }

                    if (mLoginUid == userBean.getUid()){//自己的信息不显示关注、已关注、亲友
                        helper.setGone(R.id.tv_item_main_search_user_follow, false);
                        helper.setGone(R.id.tv_item_main_search_user_is_follow, false);
                        helper.setGone(R.id.tv_item_main_search_user_is_friend, false);
                    }
                    helper.addOnClickListener(R.id.tv_item_main_search_user_follow);
                    ImageView ivAvatar = helper.itemView.findViewById(R.id.iv_item_main_search_user_avatar);
                    Glide.with(mContext).load(userBean.getAvatar()).apply(requestOptionsAvatar).into(ivAvatar);
                }
                break;
            case MainSearchSectionMultiEntity.ITEM_TYPE_MOVIE://影片
                if (item != null && item.t != null && item.t.getMediaBean() != null) {
                    helper.setText(R.id.tv_item_main_search_movie_name, Html.fromHtml(item.t.getMediaBean().getTitle()));
                    Spanned desc = Html.fromHtml(item.t.getMediaBean().getDescription());
                    helper.setText(R.id.tv_item_main_search_movie_introduction, TextUtils.isEmpty(desc) ? mContext.getString(R.string.media_empty_description) : desc);
                    ImageView ivAvatar = helper.itemView.findViewById(R.id.iv_item_main_search_movie_avatar);
                    Glide.with(mContext).load(item.t.getMediaBean().getAvatar()).apply(requestOptionsAvatar).into(ivAvatar);
                    ImageView ivCover = helper.itemView.findViewById(R.id.iv_item_main_search_movie_cover);
                    Glide.with(mContext).load(item.t.getMediaBean().getCover()).into(ivCover);

                }
                break;
            default:
                break;
        }
    }
}
