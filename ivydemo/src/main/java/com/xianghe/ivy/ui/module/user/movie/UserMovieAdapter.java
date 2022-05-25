package com.xianghe.ivy.ui.module.user.movie;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.LruCache;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.xianghe.ivy.R;
import com.xianghe.ivy.adapter.adapter.BaseQuickAdapter;
import com.xianghe.ivy.adapter.adapter.BaseViewHolder;
import com.xianghe.ivy.model.CategoryMovieBean;
import com.xianghe.ivy.utils.BitmapUtil;
import com.xianghe.ivy.utils.KLog;
import com.xianghe.ivy.utils.LanguageUtil;
import com.xianghe.ivy.utils.TimeFormatUtils;

import java.util.List;
import java.util.Locale;

public class UserMovieAdapter extends BaseQuickAdapter<CategoryMovieBean, BaseViewHolder> {

    private boolean mIsShowDelete;
    private int mFocus;
    private String mFlag;

    private static final LruCache<String, Bitmap> lruCache = new LruCache<String, Bitmap>((int) Runtime.getRuntime().maxMemory()/(1024*8)){
        @Override
        protected int sizeOf(@NonNull String key, @NonNull Bitmap value) {
            int sumSize = value.getRowBytes()*value.getHeight()/1024;
//             KLog.e("===sumSize="+sumSize);
            return sumSize;
        }
    };
    public UserMovieAdapter(@Nullable List<CategoryMovieBean> data, boolean isShowDelete, String flag) {
        super(R.layout.adapter_user_movie, data);
        mIsShowDelete = isShowDelete;
        mFlag = flag;
    }

    @Override
    protected void convert(BaseViewHolder helper, CategoryMovieBean item) {
        boolean showInfo = mFocus == helper.getAdapterPosition();
        helper.setText(R.id.tv_layout_user_movie_title, item.getTitle());
        helper.setText(R.id.tv_layout_user_movie_time, TimeFormatUtils.toToday(item.getCreatedAt()));
        helper.setGone(R.id.layout_item_user_movie, showInfo);
        helper.setGone(R.id.view_item_user_shade, !showInfo);
        helper.setGone(R.id.btn_item_user_movie_play, showInfo);
        helper.setGone(R.id.iv_layout_user_movie_avatar, !mFlag.equals("userMovie"));

        boolean isSimpleChinese = LanguageUtil.isSimplifiedChinese(mContext);
        if (item.getIsPrivate() == 1) {//私密
            helper.setGone(R.id.iv_layout_user_movie_state, true);
            if (isSimpleChinese) {
                helper.setImageResource(R.id.iv_layout_user_movie_state, R.mipmap.logo_si);
            } else {
                helper.setImageResource(R.id.iv_layout_user_movie_state, R.mipmap.logo_si_en);
            }
        } else if (item.getIsPrivate() == 2) {//亲友圈
            helper.setGone(R.id.iv_layout_user_movie_state, true);
            if (isSimpleChinese) {
                helper.setImageResource(R.id.iv_layout_user_movie_state, R.mipmap.logo_qin);
            } else {
                helper.setImageResource(R.id.iv_layout_user_movie_state, R.mipmap.logo_qin_en);
            }
        } else {//公开
            if (item.getIsParticipateActivity() == 1) {//活动
                helper.setGone(R.id.iv_layout_user_movie_state, true);
                helper.setImageResource(R.id.iv_layout_user_movie_state, R.mipmap.logo_huodong21);
            } else if (item.getIsScreenRecord() == 1){//录屏视频
                helper.setGone(R.id.iv_layout_user_movie_state,true);
                helper.setImageResource(R.id.iv_layout_user_movie_state,R.mipmap.logo_luping_pic);
            }
            else {
                helper.setGone(R.id.iv_layout_user_movie_state, false);
            }
        }
        helper.setGone(R.id.iv_layout_user_movie_delete, mIsShowDelete);
        helper.addOnClickListener(R.id.iv_layout_user_movie_delete);

        ImageView imageViewCover = helper.getView(R.id.iv_item_user_movie_cover);
        ImageView imageViewAvatar = helper.getView(R.id.iv_layout_user_movie_avatar);

        if (!TextUtils.isEmpty(item.getAvatar())) {
            Glide.with(mContext)
                    .load(item.getAvatar())
                    .apply(requestOptions)
                    .into(imageViewAvatar);
        }

        if (!TextUtils.isEmpty(item.getCover())) {
            Glide.with(mContext)
                    .load(new GlideUrl(item.getCover()))
                    .apply(requestOptionsCover)
                    .into(new ImageViewTarget<Drawable>(imageViewCover) {
                        @Override
                        protected void setResource(@Nullable Drawable resource) {
                            view.setImageDrawable(resource);
                            if (resource == null) return;
                            Drawable.ConstantState state = resource.getConstantState();
                            if (state == null) return;
                            Bitmap bitmap = BitmapUtil.getBitmapByView(view);
                            if(bitmap == null) return;
                            ImageView ivShadow = helper.getView(R.id.iv_shadow);
                            Bitmap bitmapShadow = BitmapUtil.createBitmapShadow(bitmap, ivShadow.getHeight(), 255, 0);
                            bitmap.recycle();
                            ivShadow.setImageBitmap(bitmapShadow);
                        }
                    });
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
        }
    }

    public void setInitFocus(int focus) {
        mFocus = focus;
    }

    public void setRemoveFocus(int focus) {
        mFocus = focus;
        try {
//            notifyItemRangeRemoved(0,getItemCount());
            notifyItemRemoved(focus);
        } catch (Exception e) {
        }
    }

    RequestOptions requestOptions = new RequestOptions()
            .error(R.mipmap.ic_my_head_default)
            .placeholder(R.mipmap.ic_my_head_default);
    RequestOptions requestOptionsCover = new RequestOptions()
            .error(R.mipmap.img_tongyong200)
            .placeholder(R.mipmap.img_tongyong200);
}
