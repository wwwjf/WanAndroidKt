package com.xianghe.ivy.ui.module.pic2video;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.xianghe.ivy.R;
import com.xianghe.ivy.constant.URL;
import com.xianghe.ivy.http.request.NetworkRequest;
import com.xianghe.ivy.model.MusicBean;
import com.xianghe.ivy.model.MusicTagBean;
import com.xianghe.ivy.model.response.BaseResponse;
import com.xianghe.ivy.network.GsonHelper;
import com.xianghe.ivy.utils.KLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MusicSelectedDialog extends DialogFragment {
    private static final String TAG = "MusicSelectedDialog";

    private TabLayout mLayoutTab;
    private ViewPager mViewPager;
    private MyPagerAdapter mAdapter;

    private OnMusicSelectedListener mListener;

    private MusicListFragment.OnMusicSelectedListener mMusicSelectedListener = new MusicListFragment.OnMusicSelectedListener() {
        @Override
        public void onMusicSelected(MusicListFragment fragment, MusicBean musicBean) {
            if (mListener != null) {
                mListener.onMusicSelected(MusicSelectedDialog.this, musicBean);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rooView = inflater.inflate(R.layout.dialog_music_selected, container, false);
        return rooView;
    }

    @SuppressLint("CheckResult")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findView(view, savedInstanceState);
        initView(view, savedInstanceState);
        initListener(view, savedInstanceState);

        Map<String, Object> params = new HashMap<>();
        NetworkRequest.INSTANCE.postMap(URL.MUSIC_TYPE, params)
                .map(new Function<JsonElement, BaseResponse<List<MusicTagBean>>>() {
                    @Override
                    public BaseResponse<List<MusicTagBean>> apply(JsonElement jsonElement) throws Exception {
                        BaseResponse<List<MusicTagBean>> response = GsonHelper.getsGson()
                                .fromJson(jsonElement, new TypeToken<BaseResponse<List<MusicTagBean>>>() {
                                }.getType());
                        return response;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BaseResponse<List<MusicTagBean>>>() {
                    @Override
                    public void accept(BaseResponse<List<MusicTagBean>> response) throws Exception {
                        KLog.d(TAG, response);
                        List<MusicTagBean> musicTags = response.getData();
                        if (musicTags != null && musicTags.size() > 0) {
                            List<MusicListFragment> musicListFragments = new ArrayList<>();
                            //mLayoutTab.removeAllTabs();
                            for (MusicTagBean musicTag : musicTags) {
                                mLayoutTab.addTab(mLayoutTab.newTab().setText(musicTag.getName()));
                                MusicListFragment fragment = MusicListFragment.create(musicTag);
                                fragment.setMusicSelectedListener(mMusicSelectedListener);
                                musicListFragments.add(fragment);
                            }

                            mAdapter = new MyPagerAdapter(musicListFragments, getChildFragmentManager());
                            mViewPager.setAdapter(mAdapter);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        KLog.e(TAG, throwable.toString());
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        if (window == null) {
            return;
        }

        window.setBackgroundDrawable(new ColorDrawable(0xb3000000));
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.gravity = Gravity.RIGHT;
        layoutParams.dimAmount = 0;
        layoutParams.verticalMargin = 0;
        layoutParams.horizontalMargin = 0;
        window.setAttributes(layoutParams);
    }

    private void findView(View view, Bundle savedInstanceState) {
        mLayoutTab = view.findViewById(R.id.layout_tab);
        mViewPager = view.findViewById(R.id.view_pager);
    }

    private void initView(View view, Bundle savedInstanceState) {
        mLayoutTab.setupWithViewPager(mViewPager);
    }

    private void initListener(View view, Bundle savedInstanceState) {
        mViewPager.addOnPageChangeListener(mOnPageChangeListener);
    }

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {
            notifyShowChange(i);
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };

    private void notifyShowChange(int position) {
        MusicListFragment fragment = mAdapter.getItem(position);
        if (fragment != null) {
            fragment.onShow();
        }
    }

    @Override
    public int show(FragmentTransaction transaction, String tag) {
        notifyShowChange(0);
        return super.show(transaction, tag);
    }

    public OnMusicSelectedListener getListener() {
        return mListener;
    }

    public void setListener(OnMusicSelectedListener listener) {
        mListener = listener;
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        private List<? extends MusicListFragment> mFragments;

        public MyPagerAdapter(List<? extends MusicListFragment> fragments, FragmentManager fm) {
            super(fm);
            mFragments = fragments;
        }

        @Override
        public MusicListFragment getItem(int i) {
            return mFragments.get(i);
        }

        @Override
        public int getCount() {
            return mFragments == null ? 0 : mFragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return getItem(position).getTitle();
        }
    }

    public interface OnMusicSelectedListener {
        public void onMusicSelected(MusicSelectedDialog dialog, MusicBean music);
    }
}
