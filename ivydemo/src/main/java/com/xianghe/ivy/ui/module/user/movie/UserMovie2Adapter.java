package com.xianghe.ivy.ui.module.user.movie;

import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.xianghe.ivy.R;
import com.xianghe.ivy.adapter.adapter.BaseQuickAdapter;
import com.xianghe.ivy.adapter.adapter.BaseViewHolder;
import com.xianghe.ivy.model.CategoryMovieBean;
import com.xianghe.ivy.utils.LanguageUtil;
import com.xianghe.ivy.utils.TimeFormatUtils;

import java.util.List;

public class UserMovie2Adapter extends BaseQuickAdapter<CategoryMovieBean, BaseViewHolder> {

    private boolean mIsShowDelete;

    public UserMovie2Adapter(@Nullable List<CategoryMovieBean> data, boolean isShowDelete) {
        super(R.layout.adapter_user_movie2, data);
        mIsShowDelete = isShowDelete;
    }

    @Override
    protected void convert(BaseViewHolder helper, CategoryMovieBean item) {
        helper.setText(R.id.tv_layout_user_movie2_time, TimeFormatUtils.toToday(item.getCreatedAt()));

        boolean isSimpleChinese = LanguageUtil.isSimplifiedChinese(mContext);
        if (item.getIsPrivate() == 1) {//私密
            helper.setGone(R.id.iv_layout_user_movie2_state, true);
            if (isSimpleChinese) {
                helper.setImageResource(R.id.iv_layout_user_movie2_state, R.mipmap.logo_si);
            } else {
                helper.setImageResource(R.id.iv_layout_user_movie2_state, R.mipmap.logo_si_en);
            }
        } else if (item.getIsPrivate() == 2) {//亲友圈
            helper.setGone(R.id.iv_layout_user_movie2_state, true);
            if (isSimpleChinese) {
                helper.setImageResource(R.id.iv_layout_user_movie2_state, R.mipmap.logo_qin);
            } else {
                helper.setImageResource(R.id.iv_layout_user_movie2_state, R.mipmap.logo_qin_en);
            }
        } else {//公开
            if (item.getIsParticipateActivity() == 1) {//活动
                helper.setGone(R.id.iv_layout_user_movie2_state, true);
                helper.setImageResource(R.id.iv_layout_user_movie2_state, R.mipmap.logo_huodong21);
            } else if (item.getIsScreenRecord() == 1) {//录屏视频
                helper.setGone(R.id.iv_layout_user_movie2_state, true);
                helper.setImageResource(R.id.iv_layout_user_movie2_state, R.mipmap.logo_luping_pic);
            } else {
                helper.setGone(R.id.iv_layout_user_movie2_state, false);
            }
        }
        helper.setGone(R.id.iv_layout_user_movie2_delete, mIsShowDelete);
        helper.addOnClickListener(R.id.iv_layout_user_movie2_delete);

        ImageView imageViewCover = helper.getView(R.id.iv_item_user_movie2_cover);


        if (!TextUtils.isEmpty(item.getCover())) {
            Glide.with(mContext)
                    .load(new GlideUrl(item.getCover()))
                    .apply(requestOptionsCover)
                    .into(imageViewCover);
        }
    }

    RequestOptions requestOptionsCover = new RequestOptions()
            .error(R.mipmap.img_tongyong200)
            .placeholder(R.mipmap.img_tongyong200);
}
