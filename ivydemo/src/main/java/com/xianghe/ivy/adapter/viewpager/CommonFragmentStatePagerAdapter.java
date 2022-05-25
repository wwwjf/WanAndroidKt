package com.xianghe.ivy.adapter.viewpager;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * @Author: ycl
 * @Date: 2018/10/29 14:16
 * @Desc:
 */
public class CommonFragmentStatePagerAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> mFragmentList;

    public CommonFragmentStatePagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        mFragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int i) {
        return  mFragmentList.get(i);
    }

    @Override
    public int getCount() {
        return mFragmentList != null ? mFragmentList.size() : 0;
    }
}
