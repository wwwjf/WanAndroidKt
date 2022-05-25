package com.xianghe.ivy.ui.module.videopush.adapter;

import android.content.Context;

import androidx.annotation.Nullable;

import com.xianghe.ivy.R;
import com.xianghe.ivy.adapter.adapter.BaseQuickAdapter;
import com.xianghe.ivy.adapter.adapter.BaseViewHolder;
import com.xianghe.ivy.entity.db.TagsCategory;

import java.util.List;

public class MovieTagsAdapter extends BaseQuickAdapter<TagsCategory, BaseViewHolder> {

    public MovieTagsAdapter(@Nullable List<TagsCategory> data) {
        super(R.layout.item_vp_movie_tag, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, TagsCategory item) {
        helper.setText(R.id.textView_adapter_movieTag, getTagString(mContext, item));
        if (item.isSelected()) {
            helper.setBackgroundRes(R.id.textView_adapter_movieTag,
                    R.drawable.shape_rectangle_soild_ff4855_c6dp);
        } else {
            helper.setBackgroundRes(R.id.textView_adapter_movieTag,
                    R.drawable.shape_rectangle_soild_4dffff_c6dp);
        }
        helper.addOnClickListener(R.id.textView_adapter_movieTag);
    }

    private String getTagString(Context context, TagsCategory tag) {
        switch (tag.getTid()) {
            case 1: return context.getString(R.string.movie_tag_1);
            case 4: return context.getString(R.string.movie_tag_4);
            case 5: return context.getString(R.string.movie_tag_5);
            case 6: return context.getString(R.string.movie_tag_6);
            case 7: return context.getString(R.string.movie_tag_7);
            case 9: return context.getString(R.string.movie_tag_9);
            case 10: return context.getString(R.string.movie_tag_10);
            case 12: return context.getString(R.string.movie_tag_12);
            case 48: return context.getString(R.string.movie_tag_48);
            case 49: return context.getString(R.string.movie_tag_49);
            case 50: return context.getString(R.string.movie_tag_50);
            case 52: return context.getString(R.string.movie_tag_52);
            case 53: return context.getString(R.string.movie_tag_53);
            case 54: return context.getString(R.string.movie_tag_54);
            case 55: return context.getString(R.string.movie_tag_55);
            case 56: return context.getString(R.string.movie_tag_56);
            case 57: return context.getString(R.string.movie_tag_57);
            case 58: return context.getString(R.string.movie_tag_58);
            case 59: return context.getString(R.string.movie_tag_59);
            case 60: return context.getString(R.string.movie_tag_60);
            case 61: return context.getString(R.string.movie_tag_61);
            case 62: return context.getString(R.string.movie_tag_62);
            case 63: return context.getString(R.string.movie_tag_63);
        }
        return null;
    }
}
