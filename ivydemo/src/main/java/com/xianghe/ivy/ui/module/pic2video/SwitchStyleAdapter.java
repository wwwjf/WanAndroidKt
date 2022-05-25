package com.xianghe.ivy.ui.module.pic2video;

import android.content.Context;

import androidx.annotation.Nullable;

import com.xianghe.ivy.R;
import com.xianghe.ivy.adapter.recyclerview.ARecyclerAdapter;
import com.xianghe.ivy.adapter.recyclerview.RecyclerHolder;

import java.util.List;

public class SwitchStyleAdapter extends ARecyclerAdapter<SwitchModel> {
    private int mSelectedItem = -1;

    public SwitchStyleAdapter(@Nullable List<SwitchModel> datas) {
        super(datas);
    }

    @Override
    protected void onBindViewData(RecyclerHolder holder, int position, List<SwitchModel> datas) {
        final Context ctx = holder.itemView.getContext();
        SwitchModel model = getItem(position);

        holder.itemView.setSelected(mSelectedItem == position);

        int resId = 0;
        String txt = null;
        switch (model.getType()) {
            case SwitchModel.Type.NORMAL:
                resId = R.mipmap.pic_yuanpian;
                txt = ctx.getString(R.string.switch_model_normal);
                break;
            case SwitchModel.Type.QUICK:
                resId = R.mipmap.pic_kuaisuqiehuan;
                txt = ctx.getString(R.string.switch_model_kuaisuqiehuan);
                break;
            case SwitchModel.Type.FADE:
                resId = R.mipmap.pic__heisezhuanchang;
                txt = ctx.getString(R.string.switch_model_heisezhuanchang);
                break;
            case SwitchModel.Type.HORIZONTALSCROLLING:
                resId = R.mipmap.pic_hengxiang;
                txt = ctx.getString(R.string.switch_model_horizontal);
                break;
            case SwitchModel.Type.VERTICALSCROLLING:
                resId = R.mipmap.pic__zongxiang;
                txt = ctx.getString(R.string.switch_model_vertical);
                break;
            case SwitchModel.Type.GRAY:
                resId = R.mipmap.pic__heibai;
                txt = ctx.getString(R.string.switch_model_heibai);
                break;
        }
        holder.setImageResource(R.id.iv_img, resId);
        holder.setText(R.id.tv_text, txt);
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_switch_style;
    }

    @Override
    public SwitchModel getItem(int position) {
        return mDatas.get(position);
    }

    public void setSelectedItem(int position) {
        mSelectedItem = position;
        notifyDataSetChanged();
    }

    public int getSelectedItem() {
        return mSelectedItem;
    }
}
