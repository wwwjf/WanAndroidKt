package com.xianghe.ivy.ui.module.main.mvp.view.fragment.category;

import android.app.Activity;
import android.content.Intent;

import com.xianghe.ivy.R;
import com.xianghe.ivy.constant.Api;
import com.xianghe.ivy.constant.Global;
import com.xianghe.ivy.model.CategoryMovieBean;
import com.xianghe.ivy.model.UserBean;
import com.xianghe.ivy.ui.module.player.PlayerActivity;
import com.xianghe.ivy.utils.KLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 首页 - 关注 fragment
 * <p>
 * 针对，首页 - 关注 一些细节业务做细微调整
 */
public class MainFollowFragment extends UserCategoryFragment {
    private static final String TAG = "MainFollowFragment";

    private final int REFRESH_TYPE_NONE = 0;                // 不需要刷新
    private final int REFRESH_TYPE_FOLLOW = 1;              // 关注刷新
    private final int REFRESH_TYPE_UNFOLLOW = 2;            // 取消关注刷新
    private final int REFRESH_TYPE_FOLLOW_AND_UNFOLLOW = 3; // 关注&取消关注刷新

    private Map<Long, Integer> mFollowChangeMap = new HashMap<>();

    @Override
    public String getCategory() {
        return Api.Value.CategoryType.FOLLOW;
    }

    @Override
    protected String getDateEmptyText() {
        return getString(R.string.index_follows_data_empty);
    }

    @Override
    protected int getDataEmptyIcon() {
        return R.mipmap.img_member_empty;
    }

    @Override
    public void onShow() {
        super.onShow();
        int refreshType = getRefreshType();
        //KLog.d(TAG, refreshType2Str(refreshType));
        if (refreshType != REFRESH_TYPE_NONE) {
            refreshData();
        }
    }


    private int getRefreshType() {
        if (mFollowChangeMap.size() <= 0) {
            return REFRESH_TYPE_NONE;
        }

        boolean hasFollow = false;
        boolean hasUnFollow = false;

        Iterator<Map.Entry<Long, Integer>> iterator = mFollowChangeMap.entrySet().iterator();
        while (iterator.hasNext() && !hasFollow && !hasUnFollow) {
            Map.Entry<Long, Integer> entry = iterator.next();
            Integer caseWhat = entry.getValue();
            if (caseWhat == Global.CASE_FOLLOW) {
                hasFollow = true;
            } else if (caseWhat == Global.CASE_UNFOLLOW) {
                hasUnFollow = true;
            }
        }

        if (hasFollow && hasUnFollow) {
            return REFRESH_TYPE_FOLLOW_AND_UNFOLLOW;
        } else if (hasFollow) {
            return REFRESH_TYPE_FOLLOW;
        } else if (hasUnFollow) {
            return REFRESH_TYPE_UNFOLLOW;
        } else {
            return REFRESH_TYPE_NONE;
        }
    }

    @Override
    public void refreshCategoryList(List<CategoryMovieBean> movies, boolean formUser) {
        super.refreshCategoryList(movies, formUser);
        mFollowChangeMap.clear();
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
        int refreshType = getRefreshType();
        KLog.d(TAG, refreshType2Str(refreshType));
        if (refreshType == REFRESH_TYPE_NONE) {
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
        Integer oldCase = mFollowChangeMap.get(uid);
        switch (newCast) {
            case Global.CASE_FOLLOW:    // 增加新关注
                if (oldCase == null) {
                    mFollowChangeMap.put(uid, newCast);
                } else {
                    if (oldCase == Global.CASE_UNFOLLOW) {
                        // 有取消关注操作记录，抵消操作
                        mFollowChangeMap.remove(uid);
                    } else {
                        mFollowChangeMap.put(uid, newCast);
                    }
                }
                break;
            case Global.CASE_UNFOLLOW:  // 取消关注
                if (oldCase == null) {
                    mFollowChangeMap.put(uid, newCast);
                } else {
                    if (oldCase == Global.CASE_FOLLOW) {
                        // 有关注操作记录，抵消操作
                        mFollowChangeMap.remove(uid);
                    } else {
                        mFollowChangeMap.put(uid, newCast);
                    }
                }
                break;
        }
    }

    private String refreshType2Str(int refreshType) {
        String result = String.valueOf(refreshType);
        switch (refreshType) {
            case REFRESH_TYPE_FOLLOW:
                result = "增加新关注 刷新";
                break;
            case REFRESH_TYPE_UNFOLLOW:
                result = "取消关注 刷新";
                break;
            case REFRESH_TYPE_FOLLOW_AND_UNFOLLOW:
                result = "增加和取消关注 刷新";
                break;
            case REFRESH_TYPE_NONE:
                result = "不刷新";
                break;
        }
        return result;
    }
}
