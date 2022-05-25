package com.xianghe.ivy.ui.module.videoedit.music_select;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.ghoome.g1.util.DensityUtils;
import com.google.android.material.tabs.TabLayout;
import com.xianghe.ivy.R;
import com.xianghe.ivy.adapter.viewpager.CommonFragmentPagerAdapter;
import com.xianghe.ivy.constant.Global;
import com.xianghe.ivy.model.MusicTagBean;
import com.xianghe.ivy.model.response.BaseResponse;
import com.xianghe.ivy.ui.module.videoedit.VideoEditActivity;
import com.xianghe.ivy.ui.module.videoedit.music_select.fragment.VEHotFragment;
import com.xianghe.ivy.utils.KLog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: ycl
 * @Date: 2018/10/25 14:11
 * @Desc:
 */
public class VideoEditMusicSelectDialog extends DialogFragment {

    private TabLayout mVeMusicSelectTabla;
    private ViewPager mVeMusicSelectVp;

    private CommonFragmentPagerAdapter mPagerAdapter;
    private List<MusicTagBean> list;
    private int width;
    private int lastId = -1; // 区分是否是当前页面

    @SuppressLint("ValidFragment")
    public VideoEditMusicSelectDialog(List<MusicTagBean> list) {
        this.list = list;
    }

    public VideoEditMusicSelectDialog() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyleRightTranslut);
        width = DensityUtils.Companion.getScreenWidth(getContext()) / 2;
        if (savedInstanceState != null) {
            list = (List<MusicTagBean>) savedInstanceState.getSerializable(Global.PARAMS);
            lastId = savedInstanceState.getInt(Global.PARAMS1);
            width = savedInstanceState.getInt(Global.PARAMS2);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null) {
            outState.putSerializable(Global.PARAMS, (Serializable) list);
            outState.putInt(Global.PARAMS1, lastId);
            outState.putInt(Global.PARAMS2, width);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_video_edit_music_select, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        initView(view);
        initTabLayout();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = width;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.RIGHT;
        window.setAttributes(params);
        setCancelable(true);
    }

    private void initTabLayout() {
        List<String> title = null;
        List<Fragment> fragmentList = null;
        if (list != null && !list.isEmpty()) {
            title = new ArrayList<>();
            fragmentList = new ArrayList<>();
            for (MusicTagBean item : list) {
                try {
//                    fragmentList.add(new VEHotFragment(this, Integer.parseInt(item.getId())));
                    title.add(BaseResponse.infoCode2String(getContext(), item.getType_code()));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        mPagerAdapter = new CommonFragmentPagerAdapter(
                getChildFragmentManager(),
                fragmentList,
                title
        );
        mVeMusicSelectVp.setAdapter(mPagerAdapter);
        mVeMusicSelectVp.setCurrentItem(0);
        mVeMusicSelectVp.setOffscreenPageLimit(title.size());
        mVeMusicSelectTabla.setupWithViewPager(mVeMusicSelectVp);

      /*  int left = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                10, Resources.getSystem().getDisplayMetrics());
        int right = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                10, Resources.getSystem().getDisplayMetrics());
        TabLayoutUtils.setIndicator(mVeMusicSelectTabla, left, right);*/
    }

    private void initView(View view) {
        mVeMusicSelectTabla = (TabLayout) view.findViewById(R.id.ve_music_select_tabla);
        mVeMusicSelectVp = (ViewPager) view.findViewById(R.id.ve_music_select_viewpager);
    }

    public void show(FragmentManager manager) {
        super.show(manager, System.currentTimeMillis() + "");
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
                        if (fragment != null && fragment.isAdded() && fragment instanceof VEHotFragment) {
                            VEHotFragment f = ((VEHotFragment) fragment);
                            if (f.getId() != id && f.stopAllMusicStatus()) {
                                break;
                            }
                        }
                    }
                }
            }
            setLastId(id);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        KLog.d("onDismiss  DialogInterface");
        //  关闭音乐，恢复本地音乐的声音，文字滚动
        Activity activity = getActivity();
        if (activity != null && !activity.isFinishing() && activity instanceof VideoEditActivity) {
            VideoEditActivity act = ((VideoEditActivity) activity);

            act.showCurrentUi();
            act.startScrollText();  // 开启文字滚动
            if (act.mVideoEditPlayUtils != null) {
                act.mVideoEditPlayUtils.audioNetMusicStop(); // 停止网络音乐
                act.mVideoEditPlayUtils.openAllVolume(); // 打开声音
            }
        }
    }

//    public static String typeCode2Str(Context context, int typeCode) {
//        switch (typeCode) {
//            case 21000: return context.getString(R.string.type_code_21000);
//            case 21001: return context.getString(R.string.type_code_21001);
//            case 21002: return context.getString(R.string.type_code_21002);
//            case 21003: return context.getString(R.string.type_code_21003);
//            case 21004: return context.getString(R.string.type_code_21004);
//            case 21005: return context.getString(R.string.type_code_21005);
//            case 21006: return context.getString(R.string.type_code_21006);
//            case 21007: return context.getString(R.string.type_code_21007);
//            case 21008: return context.getString(R.string.type_code_21008);
//            case 21009: return context.getString(R.string.type_code_21009);
//            case 21010: return context.getString(R.string.type_code_21010);
//            case 21011: return context.getString(R.string.type_code_21011);
//            case 21012: return context.getString(R.string.type_code_21012);
//            case 21013: return context.getString(R.string.type_code_21013);
//            case 21014: return context.getString(R.string.type_code_21014);
//            case 21015: return context.getString(R.string.type_code_21015);
//            case 21016: return context.getString(R.string.type_code_21016);
//            case 21017: return context.getString(R.string.type_code_21017);
//            case 21018: return context.getString(R.string.type_code_21018);
//            case 21019: return context.getString(R.string.type_code_21019);
//        }
//        return null;
//    }
}
