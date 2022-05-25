package com.xianghe.ivy.adapter.viewpager;


import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class CommonFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragments = null;
    private List<String> mTitle = null;

    public CommonFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        mFragments = fragments;
    }

    public CommonFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragments, List<String> title) {
        super(fm);
        mFragments = fragments;
        mTitle = title;
    }

    @Override
    public Fragment getItem(int i) {
        return mFragments.get(i);
    }

    @Override
    public int getCount() {
        return mFragments == null ? 0 : mFragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitle == null ? null : mTitle.get(position);
    }

    public List<Fragment> getFragments() {
        return mFragments;
    }
}
