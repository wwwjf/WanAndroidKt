package com.wwwjf.common.business.mvp;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;


public class BaseFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<Object> mFragments;

    private String[] mTabs;

    private Context mContext;

    /**
     * 基类BaseFragmentPagerAdapter的构造
     *
     * @param fm        FragmentManager
     * @param fragments 需要管理的fragment对象的集合，类型需要是fragment或者其子类
     * @param tabs      indicator显示的title的数组
     */
    public BaseFragmentPagerAdapter(Context context, FragmentManager fm,
                                    List<Object> fragments,
                                    String[] tabs) {
        super(fm);
        mContext = context;
        mFragments = new ArrayList<>();
        mFragments = fragments;
        mTabs = tabs;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    /**
     * 获取position位置的fragment对象
     *
     * @param position 位置
     * @return fragment对象
     */
    @Override
    public Fragment getItem(int position) {
        if (mFragments.get(position) instanceof Fragment){
            return (Fragment) mFragments.get(position);
        }else if (mFragments.get(position) instanceof Class){
            return Fragment.instantiate(mContext,((Class) mFragments.get(position)).getName());
        }
        return null;
    }

    /**
     * 获取适配器设置的fragment的数量
     *
     * @return 多少个fragment
     */
    @Override
    public int getCount() {
        return mFragments.size();
    }

    /**
     * 设置indicator的显示的title文字
     *
     * @param position 指定位置
     * @return 文字的信息
     */
    @Override
    public CharSequence getPageTitle(int position) {
        return mTabs[position];
    }


    /**
     * 静态内部类
     * 缓存fragment的holder对象
     */
    public static class Builder {

        private final ArrayList<Object> fragments = new ArrayList<>();

        private FragmentManager manager;

        private Context mContext;

        public Builder(FragmentManager manager, Context context) {
            this.manager = manager;
            mContext = context;
        }


        /**
         * 添加需要显示的fragment
         *
         * @param f fragment对象
         * @return holder对象
         */
        public Builder add(Object f) {
            fragments.add(f);
            return this;
        }

        /**
         * 获取fragment对象
         *
         * @param position 位置
         * @return fragment对象
         */
        public Fragment getFragment(int position) {
            if (fragments.get(position) instanceof Fragment){
                return (Fragment) fragments.get(position);
            }else if (fragments.get(position) instanceof Class){
                return Fragment.instantiate(mContext,((Class) fragments.get(position)).getName());
            }
            return null;
        }

        /**
         * 调用set方法获取Fragment适配器
         *
         * @param tabs 需要设置的incicator显示的title
         * @return 适配器
         */
        public BaseFragmentPagerAdapter build(String[] tabs) {
            return new BaseFragmentPagerAdapter(mContext,manager, fragments, tabs);
        }
    }
}
