package com.xianghe.ivy.ui.module.pic2video;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.xianghe.ivy.R;
import com.xianghe.ivy.adapter.recyclerview.IRecyclerItemClickListener;
import com.xianghe.ivy.constant.Api;
import com.xianghe.ivy.constant.URL;
import com.xianghe.ivy.http.request.NetworkRequest;
import com.xianghe.ivy.model.MusicBean;
import com.xianghe.ivy.model.MusicTagBean;
import com.xianghe.ivy.model.response.BaseResponse;
import com.xianghe.ivy.network.GsonHelper;
import com.xianghe.ivy.utils.KLog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MusicListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, IRecyclerItemClickListener {
    private static final String TAG = "MusicListFragment";

    private MusicTagBean mMusicTag;

    private View mLayoutDataEmpty;
    private SwipeRefreshLayout mLayoutRefresh;
    private RecyclerView mViewList;

    private MusicListAdapter mAdapter;

    private OnMusicSelectedListener mMusicSelectedListener;

    public static MusicListFragment create(MusicTagBean musicTagBean) {
        MusicListFragment fragment = new MusicListFragment();
        fragment.mMusicTag = musicTagBean;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_music_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findView(view, savedInstanceState);
        initView(view, savedInstanceState);
        initListener(view, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        KLog.w(TAG, "outState = " + outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        KLog.d(TAG, "savedInstanceState = " + savedInstanceState);
    }

    private void findView(View view, Bundle savedInstanceState) {
        mLayoutDataEmpty = view.findViewById(R.id.layout_data_empty);
        mLayoutRefresh = view.findViewById(R.id.layout_refresh);
        mViewList = view.findViewById(R.id.view_list);
    }

    private void initView(View view, Bundle savedInstanceState) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mAdapter = new MusicListAdapter();
        mViewList.setLayoutManager(layoutManager);
        mViewList.setAdapter(mAdapter);

        DividerItemDecoration divider = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(getActivity().getResources().getDrawable(R.drawable.shape_rectangle_soild_999999));
        mViewList.addItemDecoration(divider);
    }

    private void initListener(View view, Bundle savedInstanceState) {
        mLayoutRefresh.setOnRefreshListener(this);
        mAdapter.setItemClickListener(this);
    }

    @Override
    public void onRefresh() {
        loadData();
    }

    @SuppressLint("CheckResult")
    public void onShow() {
        if (mMusicTag == null) {
            return;
        }

        if (mAdapter.getItemCount() <= 0) {
            loadData();
        }
    }


    public String getTitle() {
        return mMusicTag == null ? null : mMusicTag.getName();
    }

    @SuppressLint("CheckResult")
    private void loadData() {
        mLayoutRefresh.setRefreshing(true);

        Map<String, Object> params = new HashMap<>();
        params.put(Api.Key.TYPE, mMusicTag.getId());
        NetworkRequest.INSTANCE.postMap(URL.MUSICLIST, params)
                .map(new Function<JsonElement, BaseResponse<List<MusicBean>>>() {
                    @Override
                    public BaseResponse<List<MusicBean>> apply(JsonElement jsonElement) throws Exception {
                        BaseResponse<List<MusicBean>> response = GsonHelper.getsGson()
                                .fromJson(jsonElement, new TypeToken<BaseResponse<List<MusicBean>>>() {
                                }.getType());
                        return response;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BaseResponse<List<MusicBean>>>() {
                    @Override
                    public void accept(BaseResponse<List<MusicBean>> response) throws Exception {
                        KLog.d(TAG, response);
                        mLayoutRefresh.setRefreshing(false);

                        List<MusicBean> musicList = response.getData();
                        mAdapter.setDatas(musicList);

                        if (mAdapter.getItemCount() > 0) {
                            mLayoutDataEmpty.setVisibility(View.GONE);
                        } else {
                            mLayoutDataEmpty.setVisibility(View.VISIBLE);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        KLog.e(TAG, throwable.toString());
                        mLayoutRefresh.setRefreshing(false);
                    }
                });
    }

    @Override
    public void onItemClick(RecyclerView parent, int position, RecyclerView.ViewHolder holder, View view) {
        mAdapter.selectedItem(position);
        if (mMusicSelectedListener != null) {
            mMusicSelectedListener.onMusicSelected(this, mAdapter.getItem(position));
        }
    }

    public OnMusicSelectedListener getMusicSelectedListener() {
        return mMusicSelectedListener;
    }

    public void setMusicSelectedListener(OnMusicSelectedListener musicSelectedListener) {
        mMusicSelectedListener = musicSelectedListener;
    }

    public interface OnMusicSelectedListener {
        public void onMusicSelected(MusicListFragment fragment, MusicBean musicBean);
    }
}
