package com.xianghe.ivy.ui.module.player.adapter;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.xianghe.ivy.R;
import com.xianghe.ivy.adapter.recyclerview.AbsRecyclerAdapter;
import com.xianghe.ivy.adapter.recyclerview.RecyclerHolder;
import com.xianghe.ivy.adapter.recyclerview.RecyclerViewHelper;
import com.xianghe.ivy.manager.UserInfoManager;
import com.xianghe.ivy.model.CommentBean;
import com.xianghe.ivy.model.CommentItem;
import com.xianghe.ivy.utils.TimeFormatUtils;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;


public class CommentAdapter extends AbsRecyclerAdapter {
    private CommentBean mCommentBean;

    private RequestOptions mRequestOptions = new RequestOptions()
            .placeholder(R.mipmap.ic_my_head_default)
            .error(R.mipmap.ic_my_head_default);

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_comment;
    }

    @Override
    public RecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerHolder holder = super.onCreateViewHolder(parent, viewType);
        holder.getView(R.id.iv_portrait).setOnClickListener(this);
        holder.getView(R.id.tv_name).setOnClickListener(this);
        holder.getView(R.id.btn_delete).setOnClickListener(this);
        holder.getView(R.id.btn_report).setOnClickListener(this);
        return holder;
    }

    @Override
    public int getItemCount() {
        if (mCommentBean == null || mCommentBean.getList() == null) {
            return 0;
        } else {
            return mCommentBean.getList().size();
        }
    }

    @Override
    protected void onBindViewData(RecyclerHolder holder, int position) {
        Context context = holder.itemView.getContext();

        CommentItem item = getItem(position);

        // 替换 "自己的名字" -> "我"
        final long myUid = UserInfoManager.getUid();
        if (myUid == item.getUid()) {
            holder.setVisibility(View.VISIBLE, R.id.btn_delete);
            holder.setText(R.id.tv_name, context.getString(R.string.comment_name_mine));

        } else {
            holder.setVisibility(View.GONE, R.id.btn_delete);
            holder.setText(R.id.tv_name, item.getName());

        }

        // 设置 回复用户信息
        if (item.getReplyUid() != CommentItem.INVALID_ID) {
            holder.setVisibility(View.VISIBLE, R.id.tv_repeat_user_name);

            if (myUid == item.getReplyUid()) {
                holder.setText(R.id.tv_repeat_user_name, "@" + context.getString(R.string.comment_name_mine));

            } else {
                holder.setText(R.id.tv_repeat_user_name, "@" + item.getReplyName());

            }

        } else {
            holder.setVisibility(View.GONE, R.id.tv_repeat_user_name);
        }

        // 转发
        holder.setVisibility(item.getType() == CommentItem.TYPE_NORMAL ? View.GONE : View.VISIBLE, R.id.view_replay);

        // 评论内容
        holder.setText(R.id.tv_content, item.getContent());
        // 评论时间
        holder.setText(R.id.tv_stamp, getTimeStr(item.getCreateAtInMillionSecond()));
        // 用户头像
        ImageView ivProtrait = holder.getView(R.id.iv_portrait);
        Glide.with(ivProtrait)
                .load(item.getAvatar())
                .apply(mRequestOptions)
                .into(ivProtrait);
    }

    @Nullable
    @Override
    public CommentItem getItem(int position) {
        try {
            mCommentBean.getList().get(position);
        } catch (Exception e) {
            return null;
        }
        return mCommentBean.getList().get(position);
    }

    @Override
    public void onClick(View v) {
        RecyclerView recyclerView = RecyclerViewHelper.getParentRecyclerView(v);
        RecyclerView.ViewHolder holder = recyclerView.findContainingViewHolder(v);
        if (mItemClickListener != null) {
            mItemClickListener.onItemClick(recyclerView, holder.getAdapterPosition(), holder, v);
        }
    }

    @Nullable
    public CommentBean getComment() {
        return mCommentBean;
    }

    public void setComment(CommentBean commentBean) {
        mCommentBean = commentBean;
        notifyDataSetChanged();
    }

    public void addAllData(List<CommentItem> list) {
        if (mCommentBean == null) {
            return;
        }
        mCommentBean.getList().addAll(list);
        notifyItemRangeInserted(getItemCount() - list.size(), getItemCount());
    }

    public void addAllData(int index, List<CommentItem> list) {
        if (mCommentBean == null) {
            return;
        }
        mCommentBean.getList().addAll(index, list);
        notifyItemRangeInserted(index, index + list.size());
    }

    /**
     * 插入数据
     *
     * @param data  data
     * @param index index
     */
    public void addData(CommentItem data, int index) {
        if (mCommentBean == null) {
            return;
        }
        mCommentBean.getList().add(index, data);
        notifyItemInserted(index);
    }

    /**
     * 删除数据
     *
     * @param index index
     */
    public CommentItem removeData(int index) {
        if (mCommentBean == null) {
            return null;
        }
        notifyItemRemoved(index);
        return mCommentBean.getList().remove(index);
    }

    private final SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);

    private String getTimeStr(long time) {
        return TimeFormatUtils.toToday(time);
    }
}
