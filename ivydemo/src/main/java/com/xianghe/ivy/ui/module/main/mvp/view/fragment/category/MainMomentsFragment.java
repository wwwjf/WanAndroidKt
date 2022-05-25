package com.xianghe.ivy.ui.module.main.mvp.view.fragment.category;

import android.app.Activity;
import android.content.Intent;

import com.xianghe.ivy.R;
import com.xianghe.ivy.constant.Api;
import com.xianghe.ivy.constant.Global;
import com.xianghe.ivy.model.CategoryMovieBean;
import com.xianghe.ivy.ui.module.player.PlayerActivity;
import com.xianghe.ivy.utils.KLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainMomentsFragment extends UserCategoryFragment {
    private static final String TAG = "MainMomentsFragment";

    private final int REFRESH_TYPE_NONE = 0;                // 不需要刷新
    private final int REFRESH_TYPE_ADD_FRIEND = 1;          // 增加亲友刷新
    private final int REFRESH_TYPE_DEL_FRIEND = 2;          // 删除亲友刷新
    private final int REFRESH_TYPE_ADD_AND_DEL_FRIEND = 3;  // 添加&删除亲友刷新

    private Map<Long, Integer> mFirendChangeMap = new HashMap<>();

    @Override
    public String getCategory() {
        return Api.Value.CategoryType.FRIEND;
    }

    @Override
    protected String getDateEmptyText() {
        return getString(R.string.index_moments_data_empty);
    }

    @Override
    protected int getDataEmptyIcon() {
        return R.mipmap.img_qinyouquan;
    }

    @Override
    public void onShow() {
        super.onShow();
        if (mFirendChangeMap != null && mFirendChangeMap.size() <= 0) {
            refreshData();
        }
    }

    @Override
    public void refreshCategoryList(List<CategoryMovieBean> movies, boolean formUser) {
        super.refreshCategoryList(movies, formUser);
        mFirendChangeMap.clear();
    }

    /**
     * 全屏播放返回后台回调该方法
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onResultPlayMovie(int requestCode, int resultCode, Intent data) {
        if (mFirendChangeMap != null && mFirendChangeMap.size() <= 0) {
            KLog.d(TAG, "不重新请求 滚动到指定位置。");
            if (resultCode == Activity.RESULT_OK) {
                PlayerActivity.Data result = PlayerActivity.parseData(data);
                int index = result.getIndex();
                ArrayList<CategoryMovieBean> movies = result.getMovies();

                refreshCategoryList(movies, false);
                mAdapter.setFocus(index);
                mViewList.scrollToPosition(index);
            }
        } else {
            KLog.d(TAG, "重新刷新数据");
            refreshData();
        }
    }

    @Override
    public void onUserRelationShipChange(long uid, int newCast) {
        KLog.d(TAG, "uid = " + uid + ", case = " + newCast);
        Integer oldCase = mFirendChangeMap.get(uid);
        switch (newCast) {
            case Global.CASE_ADD_FRIEND:    // 增加新亲友
                if (oldCase == null) {
                    mFirendChangeMap.put(uid, newCast);
                } else {
                    if (oldCase == Global.CASE_DEL_FRIEND) {
                        // 有取消关注操作记录，抵消操作
                        mFirendChangeMap.remove(uid);
                    } else {
                        mFirendChangeMap.put(uid, newCast);
                    }
                }
                break;
            case Global.CASE_DEL_FRIEND:  // 取消关注
                if (oldCase == null) {
                    mFirendChangeMap.put(uid, newCast);
                } else {
                    if (oldCase == Global.CASE_ADD_FRIEND) {
                        // 有关注操作记录，抵消操作
                        mFirendChangeMap.remove(uid);
                    } else {
                        mFirendChangeMap.put(uid, newCast);
                    }
                }
                break;
        }
    }
}
