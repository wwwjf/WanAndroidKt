package com.xianghe.ivy.ui.module.videoedit.music_select.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.xianghe.ivy.R;
import com.xianghe.ivy.constant.Global;
import com.xianghe.ivy.model.MusicBean;
import com.xianghe.ivy.model.MusicList;
import com.xianghe.ivy.model.MusicList1;
import com.xianghe.ivy.mvp.loadPager.BaseMVPLoadingFragment;
import com.xianghe.ivy.ui.module.videoedit.VideoEditActivity;
import com.xianghe.ivy.ui.module.videoedit.music_select.VideoEditMusicShowView;
import com.xianghe.ivy.ui.module.videoedit.music_select.adapter.VideoEditMusicSelectAdapter;
import com.xianghe.ivy.utils.DLLog;
import com.xianghe.ivy.utils.KLog;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @Author: ycl
 * @Date: 2018/10/24 19:32
 * @Desc:
 */
public class VESearchFragment extends BaseMVPLoadingFragment<VESearchContract.View, VESearchPresenter> implements VESearchContract.View {
    private VideoEditMusicSelectAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private SmartRefreshLayout mRefreshView;
    //    private VideoEditMusicSelectDialog dialog;
    private String key;
    private int page = 1;
    private int pageSize = 10;
    private MusicList1 mMusicList;
    private TextView mEmptyView;

    public VESearchFragment() {
    }

    @SuppressLint("ValidFragment")
    public VESearchFragment(VideoEditMusicShowView dialog, String key) {
        this.key = key;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            key = savedInstanceState.getString(Global.PARAMS);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null) {
            outState.putString(Global.PARAMS, key);
        }
    }

    public void refreshData(String key) {
        this.key = key;
        if (mAdapter != null)
            mAdapter.clearData();
        page = 1;
        mPresenter.requestData(key, page, pageSize);
    }

    @Nullable
    @Override
    public VESearchPresenter createPresenter() {
        return new VESearchPresenter();
    }

    @Override
    public void initData() {
        mPresenter.requestData(key, page, pageSize);
    }


    @NotNull
    @Override
    public Object onCreateContentView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle state) {
        View view = inflater.inflate(R.layout.fragment_search_music, container, false);
        mRecyclerView = view.findViewById(R.id.view_list);
        mRefreshView = view.findViewById(R.id.refresh_layout);
        mEmptyView = view.findViewById(R.id.tv_search_empty);
        mRefreshView.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                if (TextUtils.isEmpty(key) || mMusicList == null) return;
                page++;
                mPresenter.requestData(key, page, pageSize);
            }

            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                if (TextUtils.isEmpty(key)) return;
                page = 1;
                mPresenter.requestData(key, page, pageSize);
            }
        });
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
        return view;
    }


    @Override
    public void initEvent() {
    }

    @Override
    public <E> void onError(@Nullable E e) {
        mEmptyView.setVisibility(View.VISIBLE);
        mRefreshView.setVisibility(View.GONE);
    }

    @Override
    public void onMusicSuccess(MusicList1 d) {
        mMusicList = d;
        if (mRefreshView != null) {
            mRefreshView.finishLoadMore();
            mRefreshView.finishRefresh();
            if (mMusicList != null && mMusicList.getTotal() > (page * pageSize))
                mRefreshView.setEnableLoadMore(true);
            else {
                mRefreshView.setEnableLoadMore(false);
            }
        }
        if (mMusicList == null) return;
        if (mMusicList.getList() == null || mMusicList.getList().size() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
            mRefreshView.setVisibility(View.GONE);
            return;
        }
        mEmptyView.setVisibility(View.GONE);
        mRefreshView.setVisibility(View.VISIBLE);
        if (mAdapter != null) {
            if (page == 1) {
                mAdapter.setData(d.getList());
            } else {
                mAdapter.addData(d.getList());
            }
        } else {
            mAdapter = new VideoEditMusicSelectAdapter(getContext(), d.getList()) {
                @Override
                public void startMediaPlayer(String music_link) {
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
                        ((VideoEditActivity) activity).loadMusic(item.getId(), item.getMusic_name(), item.getMusic_link(), true);
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
    }


    public boolean stopAllMusicStatus() {
        boolean status = mAdapter == null ? false : mAdapter.stopAllMusicStatus();
        if (mAdapter != null)
            mAdapter.clearData();
        return status;
    }

    public void resetView() {
        if (mEmptyView != null)
            mEmptyView.setVisibility(View.GONE);
        if(mRefreshView!=null)
            mRefreshView.setVisibility(View.GONE);

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
