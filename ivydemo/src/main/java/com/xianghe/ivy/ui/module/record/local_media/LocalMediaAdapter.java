package com.xianghe.ivy.ui.module.record.local_media;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.xianghe.ivy.R;
import com.xianghe.ivy.adapter.adapter.BaseQuickAdapter;
import com.xianghe.ivy.adapter.adapter.BaseViewHolder;
import com.xianghe.ivy.ui.module.record.model.MovieItemModel;
import com.xianghe.ivy.utils.ToastUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import icepick.State;


public class LocalMediaAdapter extends BaseQuickAdapter<MovieItemModel, BaseViewHolder> implements BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.OnItemChildClickListener {

    @State
    ArrayList<MovieItemModel> selectList;
    private int mSelectVideoCount;
    private double mSelectVideoTime;

    public LocalMediaAdapter(int layoutResId, @Nullable List<MovieItemModel> data, String videoFileName, int selectVideoCount, double selectVideoTime) {
        super(layoutResId, data);
        selectList = new ArrayList<>();
        mSelectVideoCount = selectVideoCount;
        mSelectVideoTime = selectVideoTime;
        setOnItemClickListener(this);
        setOnItemChildClickListener(this);
    }

    @Override
    protected void convert(BaseViewHolder helper, MovieItemModel item) {
        helper.setFileImage(mContext, R.id.iv_item_local_media_icon, item.getFilePath())
                .addOnClickListener(R.id.iv_item_local_media_tick)
                .addOnClickListener(R.id.tv_item_local_media_check_number);
        //显示时间
        int time = item.getVideoTime().intValue();
        String hour = "";
        if (time / 3600 > 0) {
            if (time / 3600 > 10) {
                hour = time / 3600 + ":";
            } else {
                hour = "0" + time / 3600 + ":";
            }
        }
        String minute;
        if (time / 60 > 0) {
            if (time / 60 > 9) {
                minute = time / 60 + ":";
            } else {
                minute = "0" + time / 60 + ":";
            }
        } else {
            minute = "00:";
        }
        helper.setText(R.id.tv_item_local_media_time, hour + minute + (time % 60 > 9 ? time % 60 : "0" + time % 60));

        //遍历重置选择的item
        if (selectList != null && selectList.size() > 0) {
            resetItemView(helper, item);
        }
    }

    private void resetItemView(BaseViewHolder helper, MovieItemModel item) {
        if (selectList.contains(item)) {
            helper.setVisible(R.id.tv_item_local_media_check_number, true)
                    .setVisible(R.id.iv_item_local_media_tick, false);
            helper.setText(R.id.tv_item_local_media_check_number, (selectList.indexOf(item) + 1) + "");
        } else {
            helper.setVisible(R.id.iv_item_local_media_tick, true)
                    .setVisible(R.id.tv_item_local_media_check_number, false);
        }
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        //检查文件是否有问题，如果有问题直接提示
        Intent intent = new Intent(mContext, VideoPlayActivity.class);
        intent.putExtra("file_path", mData.get(position).getFilePath());
        mContext.startActivity(intent);
//        if (checkMP4Success(position)){
//            Intent intent = new Intent(mContext, PreviewMovieActivity.class);
//            ArrayList<MovieItemModel> objects = new ArrayList<>();
//            objects.add(mData.get(position));
//            //提取音频
//            intent.putExtra("movie_model_list", new MovieClipModel(mVideoFileName,objects));
//            intent.putExtra("fromAct",LOCAL);
//            mContext.startActivity(intent);
//            ((LocalMediaActivity)mContext).finish();
//        }else{
//            ToastUtil.showToastCenter(mContext,mContext.getString(R.string.local_file_error));
//        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        MovieItemModel movieItemModel = mData.get(position);
        if (movieItemModel.getVideoTime().doubleValue() > 0){
            movieItemModel.setPosition(position);
            switch (view.getId()) {
                case R.id.iv_item_local_media_tick:
                    if (mSelectVideoCount < 40) {
                        if (selectList.size() >= 10) {
                            ToastUtil.showToastCenter(mContext, mContext.getString(R.string.local_video_more_five));
                            return;
                        }
                    } else {
                        if (selectList.size() >= 50 - mSelectVideoCount) {
                            ToastUtil.showToastCenter(mContext, mContext.getString(R.string.local_video_more_fifty));
                            return;
                        }
                    }

                    if (mSelectVideoCount==0 && selectList.size()==0){

                    } else {
                        if (isOutTime(movieItemModel)) {
                            ToastUtil.showToastCenter(mContext, mContext.getString(R.string.local_video_time_limit));
                            return;
                        }
                    }
                    selectList.add(movieItemModel);
                    //添加到集合中
                    if (view.getVisibility() == View.VISIBLE) {
                        view.setVisibility(View.GONE);
                    }
                    View selectView = adapter.getViewByPosition(position, R.id.tv_item_local_media_check_number);
                    if (selectView != null) {
                        selectView.setVisibility(View.VISIBLE);
                    }
                    if (selectView instanceof TextView) {
                        movieItemModel.setListPosition(selectList.indexOf(movieItemModel) + 1);
                        ((TextView) selectView).setText("" + (selectList.indexOf(movieItemModel) + 1));
                    }
                    break;
                case R.id.tv_item_local_media_check_number:
                    //先移除
                    selectList.remove(movieItemModel);
                    View selectRemoveView = adapter.getViewByPosition(movieItemModel.getPosition(), R.id.tv_item_local_media_check_number);
                    View tickRemoveView = adapter.getViewByPosition(movieItemModel.getPosition(), R.id.iv_item_local_media_tick);
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
                        View selectCheckView = adapter.getViewByPosition(movieItem.getPosition(), R.id.tv_item_local_media_check_number);
                        //判断position是不是一样的
                        if (movieItem.getListPosition() > movieItemModel.getListPosition()) {
                            if (selectCheckView instanceof TextView) {
                                ((TextView) selectCheckView).setText("" + (movieItem.getListPosition() - 1));
                                movieItem.setListPosition(movieItem.getListPosition() - 1);
                            }
                        }
                    }
                    break;
            }

            //判断集合有数据没有，有就然按钮可以点击
            if (mContext instanceof LocalMediaActivity) {
                if (selectList != null && selectList.size() > 0) {
                    ((LocalMediaActivity) mContext).changeEnsureBtn(true);
                } else {
                    ((LocalMediaActivity) mContext).changeEnsureBtn(false);
                }
            }
        }else {
            ToastUtil.showToastCenter(mContext, mContext.getString(R.string.local_file_error));
        }
    }

    private boolean isOutTime(MovieItemModel movieSelectItemModel) {
        BigDecimal totalDecimal = BigDecimal.ZERO;
        if (selectList != null && selectList.size() > 0) {
            for (MovieItemModel movieItemModel : selectList) {
                totalDecimal = totalDecimal.add(movieItemModel.getVideoTime());
            }
        }
        totalDecimal = totalDecimal.add(movieSelectItemModel.getVideoTime());
        return totalDecimal.compareTo(BigDecimal.valueOf(180 -mSelectVideoTime)) > 0;
    }
}
