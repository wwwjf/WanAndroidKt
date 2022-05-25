package com.xianghe.ivy.ui.module.main.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LruCache;

import com.blankj.utilcode.util.StringUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.xianghe.ivy.R;
import com.xianghe.ivy.adapter.recyclerview.ARecyclerAdapter;
import com.xianghe.ivy.adapter.recyclerview.RecyclerHolder;
import com.xianghe.ivy.model.CategoryMovieBean;
import com.xianghe.ivy.utils.BitmapUtil;
import com.xianghe.ivy.utils.Formatter;
import com.xianghe.ivy.utils.KLog;

import java.util.List;

public class MainCategoryAdapter extends ARecyclerAdapter<CategoryMovieBean> {
    private static final String TAG = "MainCategoryAdapter";

    private static final LruCache<String, Bitmap> lruCache = new LruCache<String, Bitmap>((int) Runtime.getRuntime().maxMemory() / (1024 * 8)) {
        @Override
        protected int sizeOf(@NonNull String key, @NonNull Bitmap value) {
            int sumSize = value.getRowBytes() * value.getHeight() / 1024;
//             KLog.e("===sumSize="+sumSize);
            return sumSize;
        }
    };

    private int mFocus = 0;

    private final RequestOptions portraitRequestOptions = new RequestOptions()
            .error(R.mipmap.ic_my_head_default)
            .placeholder(R.mipmap.ic_my_head_default);

    private final RequestOptions coverRequestOptions = new RequestOptions()
            .error(R.mipmap.img_video_default_cover)
            .placeholder(R.mipmap.img_video_default_cover);

    public MainCategoryAdapter() {
        super(null);
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_main_category;
    }

    @Override
    public CategoryMovieBean getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    protected void onBindViewData(RecyclerHolder holder, int position, List datas) {
        final Context context = holder.itemView.getContext();

        boolean showInfo = mFocus == position;
        holder.setVisibility(showInfo ? View.VISIBLE : View.GONE, R.id.btn_play, R.id.layout_movie_info, R.id.layout_movie_toolbar);
        holder.setVisibility(!showInfo ? View.VISIBLE : View.GONE, R.id.view_shade);    // 遮罩

        CategoryMovieBean item = getItem(position);

        // 标题
        holder.setText(R.id.tv_title, item.getTitle());

        // 转发( 用户名 + 转发内容 )
        if (item.getIsForward() == CategoryMovieBean.IS_FORWARD) {
            holder.setVisibility(View.VISIBLE, R.id.tv_reply);
            SpannableString userName = new SpannableString("#" + item.getForwardName() + " ");
            String replyContent = item.getForwardContent();

            userName.setSpan(new ForegroundColorSpan(0xFF9FA7D5), 0, userName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            SpannableStringBuilder replyStr = new SpannableStringBuilder()
                    .append(userName)
                    .append(context.getString(R.string.common_forward));

            if (TextUtils.isEmpty(replyContent)) {
                replyStr.append(context.getString(R.string.common_mark_exclamation));
            } else {
                replyStr.append(context.getString(R.string.common_mark_comma));
            }
            replyStr.append(replyContent);

            holder.setText(R.id.tv_reply, replyStr);
        } else {
            holder.setVisibility(View.GONE, R.id.tv_reply);
        }

        //设置文字内容和大小
        setTextContent(holder, item);

        // 头像
        Glide.with(holder.itemView)
                .load(item.getAvatar())
                .apply(portraitRequestOptions)
                .into(((ImageView) holder.getView(R.id.iv_portrait)));

        // 封面
        final String cover = item.getCover();
        if (!StringUtils.isEmpty(cover)) {
            Glide.with(holder.itemView)
                    .load(new GlideUrl(cover))
                    .apply(coverRequestOptions)
                    .into(new ImageViewTarget<Drawable>((ImageView) holder.getView(R.id.iv_cover)) {
                        @Override
                        protected void setResource(@Nullable Drawable resource) {
                            view.setImageDrawable(resource);
                            if (resource == null) return;
                            Drawable.ConstantState state = resource.getConstantState();
                            if (state == null) return;
                            Bitmap bitmap = BitmapUtil.getBitmapByView(view);
                            if(bitmap == null) return;
                            ImageView ivShadow = holder.getView(R.id.iv_shadow);
                            Bitmap bitmapShadow = BitmapUtil.createBitmapShadow(bitmap, ivShadow.getHeight(), 255, 0);
                            bitmap.recycle();
                            ivShadow.setImageBitmap(bitmapShadow);

                        }
                    });
        }

        //录屏类型的视频显示
        if (item.getIsScreenRecord() == 1) {
            //是否在中间位置
            holder.setVisibility(showInfo ? View.VISIBLE : View.GONE, R.id.iv_movie_state);
            holder.setImageResource(R.id.iv_movie_state, R.mipmap.logo_luping_pic);
        } else {
            holder.setVisibility(View.GONE, R.id.iv_movie_state);
        }

    }

    private void setTextContent(RecyclerHolder holder, CategoryMovieBean item) {
        TextView commentsText = holder.getView(R.id.tv_comments);
        TextView starText = holder.getView(R.id.tv_star);
        TextView likeText = holder.getView(R.id.tv_like);
        TextView shareText = holder.getView(R.id.tv_share);
        String comments = Formatter.formatNumber(item.getComments());
        String star = Formatter.formatNumber(item.getStar());
        String like = Formatter.formatNumber(item.getLike());
        String share = Formatter.formatNumber(item.getShare());
        commentsText.setText(comments);
        starText.setText(star);
        likeText.setText(like);
        shareText.setText(share);
        //判断是否超过了
        if (comments.length() >= 6
                || star.length() >= 6
                || like.length() >= 6
                || share.length() >= 6) {
            //设置字体大小
            commentsText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
            starText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
            likeText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
            shareText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        } else {
            //设置字体大小
            commentsText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            starText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            likeText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            shareText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        }
    }

    public int getFocus() {
        return mFocus;
    }

    public void setFocus(int focus) {
        mFocus = focus;
        try {
            notifyItemRangeChanged(0, getItemCount());
        } catch (Exception e) {
            // do nothing
            //e.printStackTrace();
        }
    }


    @Override
    public void setDatas(List<CategoryMovieBean> datas) {
        super.setDatas(datas);
        notifyDataSetChanged();
    }

    public void addDatas(List<CategoryMovieBean> datas) {
        if (datas == null) {
            return;
        }
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    public void setData(int index, CategoryMovieBean updateMovie) {
        if (updateMovie == null) {
            return;
        }
        mDatas.set(index, updateMovie);
        notifyItemChanged(index);
    }
}
