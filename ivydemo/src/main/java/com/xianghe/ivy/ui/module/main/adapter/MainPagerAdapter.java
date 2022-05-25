package com.xianghe.ivy.ui.module.main.adapter;


import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.xianghe.ivy.ui.module.main.mvp.view.fragment.category.AbsCategoryFragment;

import java.util.List;

public class MainPagerAdapter extends FragmentPagerAdapter {

    private List<AbsCategoryFragment> mFragments = null;

    public MainPagerAdapter(FragmentManager fm, List<AbsCategoryFragment> fragments) {
        super(fm);
        mFragments = fragments;
    }

    @Override
    public AbsCategoryFragment getItem(int i) {
        return mFragments.get(i);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
}
