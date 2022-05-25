package com.xianghe.ivy.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.xianghe.ivy.R;
import com.xianghe.ivy.adapter.recyclerview.AbsRecyclerAdapter;
import com.xianghe.ivy.adapter.recyclerview.RecyclerHolder;
import com.xianghe.ivy.manager.UserInfoManager;
import com.xianghe.ivy.model.EmptyRankingUserBean;
import com.xianghe.ivy.model.HuoDongInfoBean;
import com.xianghe.ivy.model.RankingUserBean;
import com.xianghe.ivy.ui.base.FloatingMenuActivity;
import com.xianghe.ivy.ui.module.user.UserActivity;
import com.xianghe.ivy.weight.dialog.RankingDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class RankingAdapter extends AbsRecyclerAdapter {
    private static final String TAG = "RankingAdapter";

    public static final int ITEM_VIEW_TYPE_TOP3 = 1;
    public static final int ITEM_VIEW_TYPE_NORMAL = 2;
    public static final int ITEM_VIEW_TYPE_INFO = 3;
    private final RankingDialog.MyLayoutManager mLayoutManager;
    private final Context mContext;

    private RequestOptions mOptions = new RequestOptions()
            .error(R.mipmap.ic_my_head_default)
            .placeholder(R.mipmap.ic_my_head_default);

    private List<RankingUserBean> mRankings = new ArrayList<>();
    private List<HuoDongInfoBean> mHuoDongs = new ArrayList<>();

    public RankingAdapter(Context context, RankingDialog.MyLayoutManager layoutManager) {
        mContext = context;
        mLayoutManager = layoutManager;
    }


    @Override
    protected int getItemLayoutId(int viewType) {
        if (viewType == ITEM_VIEW_TYPE_TOP3) {
            return R.layout.item_ranking_top3;
        } else if (viewType == ITEM_VIEW_TYPE_INFO) {
            return R.layout.item_ranking_info;
        }
        return R.layout.item_ranking;
    }

    @Override
    protected void onBindViewData(RecyclerHolder holder, int position) {
        Object item = getItem(position);
        switch (getItemViewType(position)) {
            case ITEM_VIEW_TYPE_TOP3:
                if (item instanceof EmptyRankingUserBean) {
                    // 设置不可见，简单粗暴
                    holder.setVisibility(View.INVISIBLE,
                            R.id.layout_portrait,
                            R.id.tv_name,
                            R.id.tv_date,
                            R.id.tv_count);

                } else if (item instanceof RankingUserBean) {
                    // 设置可见
                    holder.setVisibility(View.VISIBLE,
                            R.id.layout_portrait,
                            R.id.tv_name,
                            R.id.tv_date,
                            R.id.tv_count);

                    RankingUserBean rankingInfoBean = (RankingUserBean) item;
                    int imgId = 0;
                    switch (rankingInfoBean.getRanking()) {
                        case 1:
                            imgId = R.mipmap.pic_no1;
                            break;
                        case 2:
                            imgId = R.mipmap.pic_no2;
                            break;
                        case 3:
                            imgId = R.mipmap.pic_no3;
                            break;
                    }

                    View view = holder.getView(R.id.layout_portrait);
                    view.setBackgroundResource(imgId);

                    holder.setText(R.id.tv_name, getDisplayName(rankingInfoBean));
                    holder.setText(R.id.tv_date, rankingInfoBean.getUpdateDate());
                    holder.setText(R.id.tv_count,  String.format(Locale.US, "%d", rankingInfoBean.getLikeNum()));

                    ImageView ivPortrait = holder.getView(R.id.iv_portrait);
                    Glide.with(ivPortrait)
                            .load(rankingInfoBean.getAvatar())
                            .apply(mOptions)
                            .into(ivPortrait);
                    onClickIcon(ivPortrait,rankingInfoBean.getUid());
                }
                break;

            case ITEM_VIEW_TYPE_NORMAL:
                if (item instanceof RankingUserBean) {
                    RankingUserBean rankingInfoBean = (RankingUserBean) item;
                    holder.setText(R.id.tv_ranking, String.valueOf(rankingInfoBean.getRanking()));
                    holder.setText(R.id.tv_name, getDisplayName(rankingInfoBean));
                    holder.setText(R.id.tv_date, rankingInfoBean.getUpdateDate());
                    holder.setText(R.id.tv_count, String.format(Locale.US, "%d", rankingInfoBean.getLikeNum()));

                    ImageView ivPortrait = holder.getView(R.id.iv_portrait);
                    Glide.with(ivPortrait)
                            .load(rankingInfoBean.getAvatar())
                            .apply(mOptions)
                            .into(ivPortrait);
                    onClickIcon(ivPortrait,rankingInfoBean.getUid());
                }
                break;
            case ITEM_VIEW_TYPE_INFO:
                if (item instanceof HuoDongInfoBean) {
                    HuoDongInfoBean huoDongInfoBean = (HuoDongInfoBean) item;
                    ImageView ivImage = holder.getView(R.id.iv_image);
                    Glide.with(ivImage)
                            .load(new GlideUrl(huoDongInfoBean.getActivityPhotos()))
                            .into(new ImageViewTarget<Drawable>(ivImage) {
                                @Override
                                protected void setResource(@Nullable Drawable resource) {
                                    ivImage.setImageDrawable(resource);
                                }
                            });
                }
                break;
            default:
        }
    }

    private void onClickIcon(View ivPortrait,long userId){
        ivPortrait.setOnClickListener(v -> {
            if (!UserInfoManager.isLogin()) {
//                mContext.startActivity(new Intent(mContext, LoginActivity.class));
                return;
            }
            Intent intent = new Intent(mContext, UserActivity.class);
            intent.putExtra(FloatingMenuActivity.KEY_HIDE_MENU, true);
            Uri uri = Uri.parse("ivy://UserActivity?uid=" + userId);
            intent.setData(uri);
            mContext.startActivity(intent);
        });
    }

    @Override
    public Object getItem(int position) {
        switch (getItemViewType(position)) {
            case ITEM_VIEW_TYPE_TOP3:
            case ITEM_VIEW_TYPE_NORMAL: {
                if (mRankings != null && mRankings.size() > 0) {
                    return mRankings.get(position);
                }
                return null;
            }
            case ITEM_VIEW_TYPE_INFO: {
                if (mHuoDongs != null && mHuoDongs.size() > 0) {
                    return mHuoDongs.get(position - mRankings.size());
                }
                return null;
            }
            default:
                return null;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position >= mRankings.size()) {
            return ITEM_VIEW_TYPE_INFO;
        } else {
            if (position < 3) {
                return ITEM_VIEW_TYPE_TOP3;
            } else {
                return ITEM_VIEW_TYPE_NORMAL;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mRankings == null ? 1 : mRankings.size() + 1;
    }

    public void setRankings(List<RankingUserBean> rankings) {
        if (rankings == null) {
            mRankings.clear();
        } else {
            // 第1名站C位，第2名站左边，第3名站右边, 实现思路：list数据换位
            // 这里逻辑有个不严谨的地方, 现在只是根据 列表位置判断排名，如果服务返回数据不严格按照排名排序返回，
            // rankings.size() >= 3 的逻辑无效.
            if (rankings.size() == 0) {
                // 不处理
            } else if (rankings.size() == 1) {
                // 在第一名头尾插入数据，让第一名在中间
                rankings.add(0, new EmptyRankingUserBean());
                rankings.add(new EmptyRankingUserBean());
            } else if (rankings.size() == 2) {
                Collections.reverse(rankings);
                rankings.add(new EmptyRankingUserBean());
            } else {
                // 1、2名数据 换位
                RankingUserBean no1 = rankings.remove(0);
                RankingUserBean no2 = rankings.remove(0);
                rankings.add(0, no1);
                rankings.add(0, no2);
            }
            mRankings = rankings;
        }
        notifyDataSetChanged();
    }

    public int getRankingListSize() {
        //空指针直接抓异常处理
        try {
            return mRankings.size();
        } catch (Exception e) {
            return 0;
        }
    }

    public void setHuoDong(HuoDongInfoBean huoDongs) {
        mHuoDongs.clear();
        mHuoDongs.add(huoDongs);
        notifyDataSetChanged();
    }

    private String getDisplayName(RankingUserBean user) {
        if (user.getUid() == UserInfoManager.getUid()) {
            return mContext.getString(R.string.comment_name_mine);
        } else {
            return user.getName();
        }
    }
}