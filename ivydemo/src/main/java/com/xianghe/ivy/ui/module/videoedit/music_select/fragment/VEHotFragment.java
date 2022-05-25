package com.xianghe.ivy.ui.module.videoedit.music_select.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.xianghe.ivy.R;
import com.xianghe.ivy.constant.Global;
import com.xianghe.ivy.model.MusicBean;
import com.xianghe.ivy.mvp.loadPager.BaseMVPLoadingFragment;
import com.xianghe.ivy.ui.module.videoedit.VideoEditActivity;
import com.xianghe.ivy.ui.module.videoedit.music_select.VideoEditMusicShowView;
import com.xianghe.ivy.ui.module.videoedit.music_select.adapter.VideoEditMusicSelectAdapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @Author: ycl
 * @Date: 2018/10/24 19:32
 * @Desc:
 */
public class VEHotFragment extends BaseMVPLoadingFragment<VEHotContract.View, VEHotPresenter> implements VEHotContract.View {
    private VideoEditMusicSelectAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private int id;
//    private VideoEditMusicSelectDialog dialog;
    private VideoEditMusicShowView dialog;

    public VEHotFragment() {
    }

    @SuppressLint("ValidFragment")
    public VEHotFragment(VideoEditMusicShowView dialog, int id) {
        this.dialog = dialog;
        this.id = id;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            id = savedInstanceState.getInt(Global.PARAMS);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null) {
            outState.putInt(Global.PARAMS, id);
        }
    }

    @Nullable
    @Override
    public VEHotPresenter createPresenter() {
        return new VEHotPresenter();
    }

    @Override
    public void initData() {
        mPresenter.requestData(id);
    }

    @NotNull
    @Override
    public Object onCreateContentView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle state) {
        mRecyclerView = new RecyclerView(getContext());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        //添加自定义分割线
        DividerItemDecoration divider = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(getActivity().getResources().getDrawable(R.drawable.shape_rectangle_soild_999999));
        mRecyclerView.addItemDecoration(divider);

        RecyclerView.ItemAnimator animator = mRecyclerView.getItemAnimator();
        animator.setAddDuration(0);
        animator.setChangeDuration(0);
        animator.setMoveDuration(0);
        animator.setRemoveDuration(0);
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        mRecyclerView.setItemAnimator(animator);

        return mRecyclerView;
    }


    @Override
    public void initEvent() {
    }

    @Override
    public <E> void onError(@Nullable E e) {
        super.onError(e);
        showEmpty();
    }

    @Override
    public void onMusicSuccess(List<MusicBean> d) {
        mAdapter = new VideoEditMusicSelectAdapter(getContext(), this, d) {
            @Override
            public void startMediaPlayer(String music_link) {
                try {
                    if (dialog != null && dialog.isVisible()) {
                        dialog.stopAllMusicStatus(id);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                FragmentActivity activity = getActivity();
                if (activity != null && !activity.isFinishing() && activity instanceof VideoEditActivity) {
                    VideoEditActivity act = ((VideoEditActivity) activity);

                    // 播放网络音乐，关闭本地音乐的声音，停止文字滚动
                    act.stopScrollText(); // 关闭文字滚动
                    if (act.mVideoEditPlayUtils != null) {
                        act.mVideoEditPlayUtils.closeAllVolume();  // 关闭声音
                        act.mVideoEditPlayUtils.audioNetMusicPlay(music_link);  // 播放网络音乐
                    }
                }
            }

            @Override
            public void stopMediaPlayer() {
                FragmentActivity activity = getActivity();
                if (activity != null && !activity.isFinishing() && activity instanceof VideoEditActivity) {
                    VideoEditActivity act = ((VideoEditActivity) activity);
                    if (act.mVideoEditPlayUtils != null) {
                        act.mVideoEditPlayUtils.audioNetMusicStop();
                    }
                }
            }

            @Override
            public void loadMusic(MusicBean item) {
                FragmentActivity activity = getActivity();
                if (activity != null && !activity.isFinishing() && activity instanceof VideoEditActivity) {
                    ((VideoEditActivity) activity).loadMusic(item.getId(), item.getMusic_name(), item.getMusic_link(),true);
                }
            }

            @Override
            public void notNetwork() {
                showToast("当前无网络～");
            }

           /* @Override
            public void addMediaPlayer() {
                try {
                    if (dialog != null && dialog.isVisible()) {
                        dialog.stopAllAddStatus(id);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }*/
        };
        mRecyclerView.setAdapter(mAdapter);
    }


    public boolean stopAllMusicStatus() {
        return mAdapter == null ? false : mAdapter.stopAllMusicStatus();
    }

    public boolean stopAllAddStatus() {
        return mAdapter == null ? false : mAdapter.stopAllAddStatus();
    }

    /*public LinkedList<MusicBean> getSelectedItem() {
        FragmentActivity activity = getActivity();
        if (activity != null && !activity.isFinishing() && activity instanceof VideoEditActivity) {
            return ((VideoEditActivity) activity).mSelectedItem;
        }
        return null;
    }*/
}
