package com.xianghe.ivy.ui.module.record.adapter;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.xianghe.ivy.R;
import com.xianghe.ivy.adapter.adapter.BaseQuickAdapter;
import com.xianghe.ivy.adapter.adapter.BaseViewHolder;
import com.xianghe.ivy.ui.module.preview_movie.PreviewMovieActivity;
import com.xianghe.ivy.ui.module.record.RecordActivity;
import com.xianghe.ivy.ui.module.record.model.MovieClipModel;
import com.xianghe.ivy.ui.module.record.model.MovieItemModel;
import com.xianghe.ivy.ui.module.videoedit.VideoEditActivity;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static com.xianghe.ivy.ui.module.preview_movie.PreviewMovieActivity.VIDEOEDIT;
import static com.xianghe.ivy.ui.module.preview_movie.PreviewMovieActivity.VIDEORECORD;

import androidx.annotation.Nullable;

public class MovieShowListAdapter extends BaseQuickAdapter<MovieItemModel, BaseViewHolder> implements BaseQuickAdapter.OnItemChildClickListener, BaseQuickAdapter.OnItemClickListener {

    public static final int request_code = 100;

    private String mVideoFileName;

    private TextView mNextText;

    private TextView mLocalText;

    private TextView mCacheText;

    private TextView mSaveCacheText;

    private boolean mNeedBg;

    private onDeleteListener mDeleteListener;

    public  MovieShowListAdapter(int layoutResId, List<MovieItemModel> data,boolean needBg){
        super(layoutResId, data);
        setOnItemChildClickListener(this);
        mNeedBg = needBg;
    }

    public MovieShowListAdapter(int layoutResId, @Nullable List<MovieItemModel> data, TextView nextText, TextView localText, TextView cacheText, String videoFileName) {
        super(layoutResId, data);
        setOnItemChildClickListener(this);
        setOnItemClickListener(this);
        mVideoFileName = videoFileName;
        mNextText = nextText;
        mLocalText = localText;
        mCacheText = cacheText;
    }

    public MovieShowListAdapter(int layoutResId, @Nullable List<MovieItemModel> data, TextView nextText, TextView localText,TextView cacheText,TextView saveCacheText,String videoFileName,boolean needBg) {
        super(layoutResId, data);
        setOnItemChildClickListener(this);
        setOnItemClickListener(this);
        mVideoFileName = videoFileName;
        mNextText = nextText;
        mLocalText = localText;
        mCacheText = cacheText;
        mSaveCacheText = saveCacheText;
        mNeedBg = needBg;
        if (data != null && data.size()>0){
            MovieItemModel movieItemModel = data.get(0);
            if (movieItemModel != null){
                movieItemModel.setPlay(true);
            }
        }
    }

    public MovieShowListAdapter(int layoutResId, @Nullable List<MovieItemModel> data, TextView nextText,String videoFileName,boolean needBg) {
        super(layoutResId, data);
        setOnItemChildClickListener(this);
        setOnItemClickListener(this);
        mVideoFileName = videoFileName;
        mNextText = nextText;
        mNeedBg = needBg;
        if (data != null && data.size()>0){
            MovieItemModel movieItemModel = data.get(0);
            if (movieItemModel != null){
                movieItemModel.setPlay(true);
            }
        }
    }

    @Override
    protected void convert(final BaseViewHolder helper, final MovieItemModel item) {
        if (mNeedBg){
            helper.setBackgroundRes(R.id.view_item_movie_show_list_bg,item.isPlay()?R.drawable.shape_red_frame:R.drawable.shape_white_frame);
        }else{
            helper.setBackgroundRes(R.id.view_item_movie_show_list_bg,R.drawable.shape_white_frame);
        }
        helper.addOnClickListener(R.id.iv_item_movie_show_list_delete).setVisible(R.id.iv_item_movie_show_list_delete, true)
                .setFileImageRoundBitmap(mContext, R.id.iv_item_movie_show_list_pic, new File(item.getFilePath()))
                .setText(R.id.tv_item_movie_show_list_time, String.format(mContext.getString(R.string.common_second), item.getVideoTime()));
    }


    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        if (mDeleteListener != null) {
            mDeleteListener.deleteListener(position);
        }
    }

    public double recordTotalTimeExpertPos(int pos) {
        BigDecimal totalBigDecimal = new BigDecimal(BigInteger.ZERO);
        //遍历集合算总时间
        int size = getData().size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                if (i != pos) {
                    totalBigDecimal = totalBigDecimal.add(getData().get(i).getVideoTime());
                }
            }
            return totalBigDecimal.doubleValue();
        } else {
            return 0;
        }
    }

    public void deleteItem(int position) {
        notifyItemRemoved(position);
        mData.get(position).setFilPicPath("");
        mData.remove(position);
        //判断如果删除到没有的时候让下一步隐藏
        if(mData.size() == 0){
            if (null != mNextText) {
                mNextText.setVisibility(View.GONE);
            }
            if (null != mLocalText){
                mLocalText.setVisibility(View.VISIBLE);
            }
            if (null != mCacheText){
                mCacheText.setVisibility(View.VISIBLE);
            }
            if (null != mSaveCacheText){
                mSaveCacheText.setVisibility(View.GONE);
            }
        }
    }

    public void notifyAll(int position){
        if (mData != null && mData.size() >0) {
            int playPosition = 0;
            //遍历所有的看看哪个是正在播放
            for (int i = 0; i < mData.size(); i++) {
                MovieItemModel movieItemModel = mData.get(i);
                if (movieItemModel.isPlay()) {
                    movieItemModel.setPlay(false);
                    playPosition = i;
                }
            }
            MovieItemModel positionMovieItemModel = mData.get(position);
            positionMovieItemModel.setPlay(true);
            notifyItemChanged(playPosition);
            notifyItemChanged(position);
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Intent intent = new Intent(mContext, PreviewMovieActivity.class);
        intent.putExtra("movie_model_list", new MovieClipModel(mVideoFileName, mData));
        intent.putExtra("movie_model_position", position);
        if (mContext instanceof RecordActivity) {
            intent.putExtra("fromAct", VIDEORECORD);
            ((RecordActivity) mContext).startActivityForResult(intent, 111);
        } else if (mContext instanceof VideoEditActivity) {
            intent.putExtra("fromAct", VIDEOEDIT);
            ((VideoEditActivity) mContext).startActivityForResult(intent, request_code);
        }
    }

    public void setDeleteListener(onDeleteListener deleteListener) {
        mDeleteListener = deleteListener;
    }

    public interface onDeleteListener {
        void deleteListener(int position);
    }
}
