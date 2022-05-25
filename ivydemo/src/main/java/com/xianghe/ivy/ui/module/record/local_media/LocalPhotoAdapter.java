package com.xianghe.ivy.ui.module.record.local_media;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.xianghe.ivy.R;
import com.xianghe.ivy.adapter.adapter.BaseQuickAdapter;
import com.xianghe.ivy.adapter.adapter.BaseViewHolder;
import com.xianghe.ivy.ui.module.record.model.MovieItemModel;
import com.xianghe.ivy.utils.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import icepick.State;

public class LocalPhotoAdapter extends BaseQuickAdapter<MovieItemModel, BaseViewHolder> implements BaseQuickAdapter.OnItemChildClickListener, BaseQuickAdapter.OnItemClickListener {

    @State
    ArrayList<MovieItemModel> selectList;

    LocalPhotoAdapter(int layoutResId, @Nullable List<MovieItemModel> data) {
        super(layoutResId, data);
        selectList = new ArrayList<>();
        setOnItemChildClickListener(this);
        setOnItemClickListener(this);
    }

    @Override
    protected void convert(BaseViewHolder helper, MovieItemModel item) {
        helper.setFileImage(mContext, R.id.iv_item_local_photo_icon, item.getFilePath())
                .addOnClickListener(R.id.iv_item_local_photo_tick)
                .addOnClickListener(R.id.tv_item_local_photo_check_number);

        //遍历重置选择的item
        if (selectList != null && selectList.size() > 0) {
            resetItemView(helper, item);
        }
    }

    private void resetItemView(BaseViewHolder helper, MovieItemModel item) {
        if (selectList.contains(item)) {
            helper.setVisible(R.id.tv_item_local_photo_check_number, true)
                    .setVisible(R.id.iv_item_local_photo_tick, false);
            helper.setText(R.id.tv_item_local_photo_check_number, (selectList.indexOf(item) + 1) + "");
        } else {
            helper.setVisible(R.id.iv_item_local_photo_tick, true)
                    .setVisible(R.id.tv_item_local_photo_check_number, false);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        MovieItemModel movieItemModel = mData.get(position);
        movieItemModel.setPosition(position);
        switch (view.getId()) {
            case R.id.iv_item_local_photo_tick:
                if (isFileExsit(movieItemModel.getFilePath())) {
                    if (selectList.size() < 10) {
                        selectList.add(movieItemModel);
                        //添加到集合中
                        if (view.getVisibility() == View.VISIBLE) {
                            view.setVisibility(View.GONE);
                        }
                        View selectView = adapter.getViewByPosition(position + adapter.getHeaderLayoutCount(), R.id.tv_item_local_photo_check_number);
                        if (selectView instanceof TextView) {
                            selectView.setVisibility(View.VISIBLE);
                            movieItemModel.setListPosition(selectList.indexOf(movieItemModel) + 1);
                            ((TextView) selectView).setText("" + (selectList.indexOf(movieItemModel) + 1));
                        }
                    }else{
                        ToastUtil.showToastCenter(mContext,mContext.getString(R.string.local_photo_more_select_photo));
                    }
                } else {
                    ToastUtil.showToastCenter(mContext, mContext.getString(R.string.file_no_exist));
                }
                break;
            case R.id.tv_item_local_photo_check_number:
                //先移除
                selectList.remove(movieItemModel);
                View selectRemoveView = adapter.getViewByPosition(movieItemModel.getPosition() + adapter.getHeaderLayoutCount(), R.id.tv_item_local_photo_check_number);
                View tickRemoveView = adapter.getViewByPosition(movieItemModel.getPosition() + adapter.getHeaderLayoutCount(), R.id.iv_item_local_photo_tick);
                if (movieItemModel.getListPosition() == movieItemModel.getListPosition()) {
                    if (tickRemoveView != null) {
                        tickRemoveView.setVisibility(View.VISIBLE);
                    }

                    if (selectRemoveView != null && selectRemoveView.getVisibility() == View.VISIBLE) {
                        selectRemoveView.setVisibility(View.GONE);
                    }
                }

                //先遍历集合取出每一个item
                for (int i = 0; i < selectList.size(); i++) {
                    MovieItemModel movieItem = selectList.get(i);
                    //获取每一个的position
                    View selectView = adapter.getViewByPosition(movieItem.getPosition() + adapter.getHeaderLayoutCount(), R.id.tv_item_local_photo_check_number);
                    //判断position是不是一样的
                    if (movieItem.getListPosition() > movieItemModel.getListPosition()) {
                        if (selectView instanceof TextView) {
                            ((TextView) selectView).setText("" + (movieItem.getListPosition() - 1));
                            movieItem.setListPosition(movieItem.getListPosition() - 1);
                        }
                    }
                }
                break;
        }

        if (mContext instanceof LocalMediaActivity) {
            if (selectList != null && selectList.size() > 0) {
                ((LocalMediaActivity) mContext).changeEnsureBtn(true);
            } else {
                ((LocalMediaActivity) mContext).changeEnsureBtn(false);
            }
        }
    }

    private boolean isFileExsit(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        } else {
            File file = new File(filePath);
            return file.exists();
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        String filePath = mData.get(position).getFilePath();
        Intent startIntent = PhotoActivity.getStartIntent(mContext, filePath);
        mContext.startActivity(startIntent);
    }
}
