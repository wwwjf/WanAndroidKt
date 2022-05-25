package com.xianghe.ivy.ui.module.videoedit.music_select.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.xianghe.ivy.R;
import com.xianghe.ivy.adapter.recyclerview.CommonRecyclerViewAdapter;
import com.xianghe.ivy.adapter.recyclerview.RecyclerHolder;
import com.xianghe.ivy.app.IvyApp;
import com.xianghe.ivy.model.MusicBean;
import com.xianghe.ivy.ui.module.videoedit.music_select.fragment.VEHotFragment;
import com.xianghe.ivy.utils.KLog;
import com.xianghe.ivy.utils.NetworkUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: ycl
 * @Date: 2018/10/29 16:44
 * @Desc:
 */
public abstract class VideoEditMusicSelectAdapter extends CommonRecyclerViewAdapter<MusicBean> {
    private int musicLocation = -1;
//    private int addLocation = 0;
//    private VEHotFragment mVEHotFragment;
//    private SparseArray<Boolean> addIsSelected = new SparseArray<Boolean>();


    public VideoEditMusicSelectAdapter(Context context, VEHotFragment veHotFragment, List<MusicBean> datas) {
        super(context, datas, R.layout.item_ve_music);
//        this.mVEHotFragment = veHotFragment;
//        for (int i = 0; i < mDatas.size(); i++) {
//            addIsSelected.put(i, false);
//        }
    }

    public VideoEditMusicSelectAdapter(Context context, List<MusicBean> datas) {
        super(context, datas, R.layout.item_ve_music);
    }

    public void clearData(){
        if(mDatas !=null &&mDatas.size()>0) {
            mDatas.clear();
            notifyDataSetChanged();
        }
    }

    public void addData(ArrayList<MusicBean> list){
        if(mDatas !=null){
            mDatas.addAll(list);
            initData();
            notifyDataSetChanged();
        }
    }

    public void setData(ArrayList<MusicBean> list){
        if(list != null) {
            mDatas = list;
            initData();
            notifyDataSetChanged();
        }
    }

    @Override
    public void onBindViewData(RecyclerHolder helper, final int position, MusicBean item) {
        ImageView iv_def_logo = helper.getView(R.id.item_ve_music_iv_def_logo);
        ImageView iv_sport_logo = helper.getView(R.id.item_ve_music_iv_sport_logo);
        Button btn_add = helper.getView(R.id.item_ve_music_btn_add);
        TextView tv_name = helper.getView(R.id.item_ve_music_tv_name);

//        iv_sport_logo.setImageResource(R.drawable.animation_list_ve_music);
        AnimationDrawable sportLogoDrawable = ((AnimationDrawable) iv_sport_logo.getDrawable());
        tv_name.setText(item.getMusic_name());

        if (getIsSelected().get(position)) {// 如果为true则表示被选中
            iv_def_logo.setVisibility(View.INVISIBLE);
            iv_sport_logo.setVisibility(View.VISIBLE);
            iv_sport_logo.requestFocus();
            iv_sport_logo.setSelected(true);
            tv_name.setSelected(true);
            if (!sportLogoDrawable.isRunning()) {
                sportLogoDrawable.start();
            }
            tv_name.setTextColor(ContextCompat.getColor(mContext, R.color.red_color_FF4855));

            btn_add.setTextColor(ContextCompat.getColor(mContext, R.color.red_color_FF4855));
            btn_add.setBackgroundResource(R.drawable.shape_rectangle_stoke_ff4855_c20dp);
        } else {
            iv_def_logo.setVisibility(View.VISIBLE);
            iv_sport_logo.setVisibility(View.GONE);
            iv_sport_logo.clearFocus();
            iv_sport_logo.setSelected(true);
            tv_name.setSelected(false);
            sportLogoDrawable.stop();
            tv_name.setTextColor(ContextCompat.getColor(mContext, R.color.white));

            btn_add.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            btn_add.setBackgroundResource(R.drawable.shape_rectangle_stoke_ffffff_c20dp);
        }
       /* if (addIsSelected.get(position)) {
            btn_add.setTextColor(ContextCompat.getColor(mContext, R.color.red_color_FF4855));
            btn_add.setBackgroundResource(R.drawable.shape_rectangle_stoke_ff4855_c20dp);
        } else {
            btn_add.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            btn_add.setBackgroundResource(R.drawable.shape_rectangle_stoke_ffffff_c20dp);
        }*/

        // 添加按钮
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMusic(item);
              /*  LinkedList<MusicBean> mSelectedItem = mVEHotFragment.getSelectedItem();
                if (position != addLocation) {
                    if (mSelectedItem != null) {
                        if (mSelectedItem.size() >= 1) {
                            mSelectedItem.remove(mDatas.get(addLocation));
                            addIsSelected.put(addLocation, false);
                        }
                    }
                    addLocation = position;
                }
                // 选中的音频
                if (!addIsSelected.get(position)) {
                    if (mSelectedItem != null)   mSelectedItem.add(mDatas.get(position));
                    addMediaPlayer();
                    addIsSelected.put(position, true);
                    // 不选择该音频
                } else {
                    if (mSelectedItem != null)  mSelectedItem.remove(mDatas.get(position));
                    addIsSelected.put(position, false);
                }
                VideoEditMusicSelectAdapter.this.notifyDataSetChanged();*/
            }
        });
        //播放音乐
        final String music_link = item.getMusic_link();
        helper.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkUtil.isNetworkConnected(IvyApp.getInstance().getApplicationContext())) {
                    notNetwork();
                    return;
                }
                if (position != musicLocation) {
//                    stopMediaPlayer();
                    if (musicLocation != -1) {
                        getIsSelected().put(musicLocation, false);
                        VideoEditMusicSelectAdapter.this.notifyItemChanged(musicLocation);
                    }
                    musicLocation = position;
                }
                // 选中的音频
                if (!getIsSelected().get(position)) {
                    if (!sportLogoDrawable.isRunning()) {
                        sportLogoDrawable.start();
                    }
                    startMediaPlayer(music_link);
                    getIsSelected().put(position, true);
                    // 不选择该音频
                } else {
                    stopMediaPlayer();
                    sportLogoDrawable.stop();
                    getIsSelected().put(position, false);
                }
                VideoEditMusicSelectAdapter.this.notifyItemChanged(position);
            }
        });
    }

    public abstract void startMediaPlayer(String music_link);

    public abstract void stopMediaPlayer();

    public abstract void loadMusic(MusicBean item);
    public abstract void notNetwork( );


    public boolean stopAllMusicStatus() {
        if (musicLocation != -1 && getIsSelected().get(musicLocation)) {
            getIsSelected().put(musicLocation, false);
            VideoEditMusicSelectAdapter.this.notifyItemChanged(musicLocation);
            KLog.d("");
            return true;
        }
        return false;
    }

    public boolean stopAllAddStatus() {
       /* if (addLocation != 0 && addIsSelected.get(addLocation)) {
            getIsSelected().put(addLocation, false);
            notifyDataSetChanged();
            KLog.d("");
            return true;
        }*/
        return false;
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerHolder holder) {
        super.onViewDetachedFromWindow(holder);
        try {
            ImageView iv_sport_logo = holder.getView(R.id.item_ve_music_iv_sport_logo);
            iv_sport_logo.clearAnimation();
            holder.itemView.clearAnimation();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
