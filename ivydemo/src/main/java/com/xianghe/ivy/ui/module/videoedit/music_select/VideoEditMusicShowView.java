package com.xianghe.ivy.ui.module.videoedit.music_select;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.ghoome.g1.util.DensityUtils;
import com.google.android.material.tabs.TabLayout;
import com.xianghe.ivy.R;
import com.xianghe.ivy.adapter.viewpager.CommonFragmentPagerAdapter;
import com.xianghe.ivy.model.MusicTagBean;
import com.xianghe.ivy.model.response.BaseResponse;
import com.xianghe.ivy.ui.module.videoedit.VideoEditActivity;
import com.xianghe.ivy.ui.module.videoedit.music_select.fragment.VEHotFragment;
import com.xianghe.ivy.ui.module.videoedit.music_select.fragment.VESearchFragment;

import java.util.ArrayList;
import java.util.List;

import icepick.State;

public class VideoEditMusicShowView extends LinearLayout {
    private AppCompatActivity mContext;

    private TabLayout mVeMusicSelectTabla;

    private ViewPager mVeMusicSelectVp;

    private CommonFragmentPagerAdapter mPagerAdapter;

    @State
    ArrayList<MusicTagBean> list;

    @State
    int width;

    @State
    int lastId = -1; // 区分是否是当前页面
    private EditText mEditSearchEt;         //音乐搜索框
    private FrameLayout mSearchMusicList;
    private TextView mSearchCancel;
    private VESearchFragment fragment;
    private ImageView mSearchIcon;
    private boolean canClear;

    public VideoEditMusicShowView(Context context) {
        this(context, null);
    }

    public VideoEditMusicShowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = (AppCompatActivity) context;
        width = DensityUtils.Companion.getScreenWidth(mContext) / 2;
        setOrientation(VERTICAL);
        initView(context);
    }

    private void inSearchStatus() {
        if (mPagerAdapter != null) {
            List<Fragment> fragmentList = mPagerAdapter.getFragments();
            if (fragmentList != null && !fragmentList.isEmpty()) {
                for (int i = 0; i < fragmentList.size(); i++) {
                    Fragment fragment = fragmentList.get(i);
                    if (fragment != null && fragment instanceof VEHotFragment) {
                        VEHotFragment f = ((VEHotFragment) fragment);
                        if (f.stopAllMusicStatus()) {
                            break;
                        }
                    }
                }
            }
        }
        if (mContext != null && !mContext.isFinishing() && mContext instanceof VideoEditActivity) {
            VideoEditActivity act = ((VideoEditActivity) mContext);
            if (act.mVideoEditPlayUtils != null) {
                act.mVideoEditPlayUtils.audioNetMusicStop(); // 停止网络音乐
                act.mVideoEditPlayUtils.openAllVolume(); // 打开声音
            }
        }

        if (fragment != null) {
            fragment.stopAllMusicStatus();
            fragment.resetView();
        }
        mSearchCancel.setVisibility(VISIBLE);
        mSearchMusicList.setVisibility(VISIBLE);
        mVeMusicSelectTabla.setVisibility(GONE);
        mVeMusicSelectVp.setVisibility(GONE);
    }

    private void exitSearchStatus() {
        mEditSearchEt.setText("");
        mEditSearchEt.clearFocus();
        if (mContext != null && !mContext.isFinishing() && mContext instanceof VideoEditActivity) {
            VideoEditActivity act = ((VideoEditActivity) mContext);
            if (act.mVideoEditPlayUtils != null) {
                act.mVideoEditPlayUtils.audioNetMusicStop(); // 停止网络音乐
                act.mVideoEditPlayUtils.openAllVolume(); // 打开声音
            }
        }
        if (fragment != null) {
            fragment.stopAllMusicStatus();
        }
        mVeMusicSelectTabla.setVisibility(VISIBLE);
        mVeMusicSelectVp.setVisibility(VISIBLE);
        mSearchMusicList.setVisibility(GONE);
        mSearchCancel.setVisibility(GONE);
    }

    private void search() {
        mEditSearchEt.clearFocus();
        String txt = mEditSearchEt.getText().toString();
        if (TextUtils.isEmpty(txt)) {
            exitSearchStatus();
        }else {
            initSearchFragment(txt);
        }
    }


    private void initSearchFragment(String key) {
        if (fragment == null) {
            fragment = new VESearchFragment(this, key);
            FragmentManager fragmentManager = mContext.getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fl_search_music_list, fragment);
            transaction.commit();
        } else {
            fragment.refreshData(key);
        }
    }

    public void show(ArrayList<MusicTagBean> list) {
        this.list = list;
        initTabLayout();
        setVisibility(VISIBLE);
    }

    public void dismiss() {
        exitSearchStatus();
        if (getVisibility() == VISIBLE) {
            setVisibility(GONE);
            //  关闭音乐，恢复本地音乐的声音，文字滚动
            if (mContext != null && !mContext.isFinishing() && mContext instanceof VideoEditActivity) {
                VideoEditActivity act = ((VideoEditActivity) mContext);

                act.showCurrentUi();
                act.startScrollText();  // 开启文字滚动
                if (act.mVideoEditPlayUtils != null) {
                    act.mVideoEditPlayUtils.audioNetMusicStop(); // 停止网络音乐
                    act.mVideoEditPlayUtils.openAllVolume(); // 打开声音
                }
            }
        }
    }

    public boolean isVisible() {
        return getVisibility() == VISIBLE;
    }

    @Override
    public void setVisibility(int visibility) {
        showAnimation(visibility == GONE);
        super.setVisibility(visibility);
    }

    private void showAnimation(boolean isShow) {
        Animation inAnimation;
        if (isShow) {
            inAnimation = AnimationUtils.loadAnimation(mContext, R.anim.anim_translate_right_in);
        } else {
            inAnimation = AnimationUtils.loadAnimation(mContext, R.anim.anim_translate_right_out);
        }
        startAnimation(inAnimation);
    }

    private void initTabLayout() {
        List<String> title = null;
        List<Fragment> fragmentList = null;
        if (list != null && !list.isEmpty()) {
            title = new ArrayList<>();
            fragmentList = new ArrayList<>();
            for (MusicTagBean item : list) {
                try {
                    fragmentList.add(new VEHotFragment(this, Integer.parseInt(item.getId())));
                    title.add(BaseResponse.infoCode2String(getContext(), item.getType_code()));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        mPagerAdapter = new CommonFragmentPagerAdapter(
                mContext.getSupportFragmentManager(),
                fragmentList,
                title
        );
        mVeMusicSelectVp.setAdapter(mPagerAdapter);
        mVeMusicSelectVp.setCurrentItem(0);
        mVeMusicSelectVp.setOffscreenPageLimit(title.size());
        mVeMusicSelectTabla.setupWithViewPager(mVeMusicSelectVp);
    }

    private void initView(Context context) {
        View view = View.inflate(context, R.layout.dialog_video_edit_music_select, null);
        mVeMusicSelectTabla = view.findViewById(R.id.ve_music_select_tabla);
        mVeMusicSelectVp = view.findViewById(R.id.ve_music_select_viewpager);
        mSearchCancel = view.findViewById(R.id.tv_search_cancel);
        mEditSearchEt = view.findViewById(R.id.et_video_edit_search);
        mSearchMusicList = view.findViewById(R.id.fl_search_music_list);
        mSearchIcon = view.findViewById(R.id.iv_icon_search);
        addView(view, width, ViewGroup.LayoutParams.MATCH_PARENT);
        mEditSearchEt.setOnFocusChangeListener((v, focus) -> {
            if (focus) {
                inSearchStatus();
            }
        });
        mSearchCancel.setOnClickListener(v -> exitSearchStatus());
        mSearchIcon.setOnClickListener(v -> {
            if (canClear)
                mEditSearchEt.setText("");
        });
        mEditSearchEt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null && imm.isActive()) {
                    imm.hideSoftInputFromWindow(mEditSearchEt.getWindowToken(), 0);
                }
                search();
            }
            return false;
        });
        mEditSearchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String txt = mEditSearchEt.getText().toString();
                if (TextUtils.isEmpty(txt)) {
                    canClear=false;
                    mSearchIcon.setImageResource(R.drawable.ic_search_pressed);
                } else {
                    canClear=true;
                    mSearchIcon.setImageResource(R.drawable.ic_close_selector);
                }
            }
        });
    }

    public int getLastId() {
        return lastId;
    }

    public void setLastId(int lastId) {
        this.lastId = lastId;
    }

    public void stopAllMusicStatus(int id) {
        if (id != lastId) {
            if (mPagerAdapter != null) {
                List<Fragment> fragmentList = mPagerAdapter.getFragments();
                if (fragmentList != null && !fragmentList.isEmpty()) {
                    for (int i = 0; i < fragmentList.size(); i++) {
                        Fragment fragment = fragmentList.get(i);
                        if (fragment != null && fragment instanceof VEHotFragment) {
                            VEHotFragment f = ((VEHotFragment) fragment);
                            if (f.getId() != id && f.stopAllMusicStatus()) {
                                break;
                            }
                        }
                    }
                }
            }
            if (fragment != null) {
                fragment.stopAllMusicStatus();
            }
            setLastId(id);
        }
    }

}
