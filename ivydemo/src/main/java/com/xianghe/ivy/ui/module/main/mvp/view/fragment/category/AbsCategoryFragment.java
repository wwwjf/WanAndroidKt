package com.xianghe.ivy.ui.module.main.mvp.view.fragment.category;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.blankj.utilcode.util.ToastUtils;
import com.leochuan.CarouselLayoutManager;
import com.leochuan.MyPageSnapHelper;
import com.leochuan.ScrollHelper;
import com.xianghe.ivy.R;
import com.xianghe.ivy.adapter.recyclerview.IRecyclerItemClickListener;
import com.xianghe.ivy.constant.Global;
import com.xianghe.ivy.manager.SystemSettingManager;
import com.xianghe.ivy.manager.UserInfoManager;
import com.xianghe.ivy.model.CategoryMovieBean;
import com.xianghe.ivy.mvp.BaseMVPFragment;
import com.xianghe.ivy.ui.module.main.adapter.MainCategoryAdapter;
import com.xianghe.ivy.ui.module.main.mvp.MainCategoryContact;
import com.xianghe.ivy.ui.module.main.mvp.presenter.MainCategoryPresenter;
import com.xianghe.ivy.ui.module.player.PlayerActivity;
import com.xianghe.ivy.ui.module.player.mvp.contact.PlayerContact;
import com.xianghe.ivy.utils.KLog;
import com.xianghe.ivy.utils.NetworkUtil;
import com.xianghe.ivy.weight.dialog.ConfirmDialog;

import java.util.List;

import xiao.free.horizontalrefreshlayout.HorizontalRefreshLayout;
import xiao.free.horizontalrefreshlayout.RefreshCallBack;
import xiao.free.horizontalrefreshlayout.refreshhead.LoadingRefreshHeader;


public abstract class AbsCategoryFragment extends BaseMVPFragment<MainCategoryContact.View, MainCategoryContact.Presenter> implements MainCategoryContact.View, RefreshCallBack, View.OnClickListener {
    private static final String TAG = "AbsCategoryFragment";

    private static final int REQ_CODE_PLAY_MOVIE = 666;
    protected static final int PAGE_SIZE = 10;

    protected Context mContext;

    private int mCurPage = 1;                 // 当前页

    private View mLayoutLoading;                // loading

    private View mLayoutDataEmpty;              // 空数据页面
    private ImageView mIvEmpty;
    private TextView mTvDataEmpty;

    private View mLayoutInternetUnavailable;    // 网络不可用
    private View mBtnReLoad;

    private HorizontalRefreshLayout mRefreshLayout;
    protected RecyclerView mViewList;

    protected MainCategoryAdapter mAdapter;
    protected CarouselLayoutManager mLayoutManager;

    protected Handler mHandler = new Handler();

    public abstract String getCategory();

    @Nullable
    @Override
    public MainCategoryContact.Presenter createPresenter() {
        return new MainCategoryPresenter(getContext());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        KLog.d(TAG, "savedInstanceState = " + savedInstanceState);
        super.onCreate(savedInstanceState);
        init(savedInstanceState);
        try {
            mPresenter.receiveBroadcast();
        } catch (Exception e) {
            e.printStackTrace();
            // 修改系统配置时mPresenter 出现null
        }
    }

    @Override
    public void onDestroy() {
        try {
            mPresenter.unReceiveBroadcast();
        } catch (Exception e) {
            e.printStackTrace();
            // 修改系统配置时mPresenter 出现null
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        KLog.d(TAG, "savedInstanceState = " + savedInstanceState);
        return inflater.inflate(R.layout.fragment_main_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        KLog.d(TAG, "view = " + view + ", savedInstanceState = " + savedInstanceState);
        super.onViewCreated(view, savedInstanceState);
        findView(view, savedInstanceState);
        initView(view, savedInstanceState);
        initListener(view, savedInstanceState);

        refreshData();
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        // 听过app放置久了会出现重复视频, 重新计算页数
        mCurPage = (int) Math.ceil(mAdapter.getItemCount() * 1f / PAGE_SIZE);
        KLog.d(TAG, "LoadMoreCategoryList 重置 category = " + getCategory() + ", curPage = " + mCurPage);
    }

    protected void init(Bundle savedInstanceState) {
        mAdapter = new MainCategoryAdapter();
    }

    private void findView(View rootView, Bundle savedInstanceState) {
        mLayoutLoading = rootView.findViewById(R.id.layout_loading);

        mLayoutDataEmpty = rootView.findViewById(R.id.layout_data_empty);
        mIvEmpty = rootView.findViewById(R.id.iv_empty);
        mTvDataEmpty = rootView.findViewById(R.id.tv_data_empty);

        mLayoutInternetUnavailable = rootView.findViewById(R.id.layout_internet_unavailable);
        mBtnReLoad = rootView.findViewById(R.id.btn_reload);

        mRefreshLayout = rootView.findViewById(R.id.layout_refresh);
        mViewList = rootView.findViewById(R.id.view_list);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView(View view, Bundle savedInstanceState) {
        mTvDataEmpty.setText(getDateEmptyText());
        mIvEmpty.setImageResource(getDataEmptyIcon());

        mRefreshLayout.setRefreshHeader(new LoadingRefreshHeader(mContext), HorizontalRefreshLayout.LEFT);
        mRefreshLayout.setRefreshHeader(new LoadingRefreshHeader(mContext), HorizontalRefreshLayout.RIGHT);

        mViewList.post(new Runnable() {
            @Override
            public void run() {
                int measuredWidth = mViewList.getMeasuredWidth();
                int measuredHeight = mViewList.getMeasuredHeight();
                KLog.w(TAG, "measuredWidth = " + measuredWidth + ", measuredHeight = " + measuredHeight);
                int maxVisibleItemCount = 5;
                int itemSpace = 0;
                int itemWidth = 0;
                if (measuredWidth * 1 / 1 > measuredHeight) {
                    itemWidth = measuredHeight * 1 / 1;
                } else {
                    itemWidth = measuredWidth;
                }
                itemSpace = (int) (((maxVisibleItemCount * itemWidth) - measuredWidth) / (maxVisibleItemCount - 1) * 0.85);
                mLayoutManager = new CarouselLayoutManager.Builder(mContext, itemSpace)
                        .setMoveSpeed(.6f)
                        .setMinScale(0.45f)
                        .setMaxVisibleItemCount(5)
                        .build();

                mViewList.setLayoutManager(mLayoutManager);
                mViewList.setAdapter(mAdapter);

                //判断是否有数据
                if (mAdapter.getDatas() != null && mAdapter.getDatas().size() > 0) {
                    refreshCategoryList(mAdapter.getDatas(), false);
                }

                MyPageSnapHelper snapHelper = new MyPageSnapHelper();
                snapHelper.attachToRecyclerView(mViewList);     // 自动居中, 注:在 setLayoutManager 之后设置.

                // 去掉 item 动画
                ((SimpleItemAnimator) mViewList.getItemAnimator()).setSupportsChangeAnimations(false);
            }
        });
    }

    private void initListener(View view, Bundle savedInstanceState) {
        mRefreshLayout.setRefreshCallback(this);
        mViewList.addOnScrollListener(mOnScrollListener);
        mAdapter.setItemClickListener(mItemClickListener);
        mBtnReLoad.setOnClickListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        KLog.d(TAG, "onActivityResult: requestCode = " + requestCode + ", resultCode = " + resultCode + ", data = " + data);
        switch (requestCode) {
            case REQ_CODE_PLAY_MOVIE:
                onResultPlayMovie(requestCode, resultCode, data);
                break;
        }
    }

    protected void onResultPlayMovie(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            PlayerActivity.Data result = PlayerActivity.parseData(data);
            if (result == null) {
                KLog.w(TAG, "onResultPlayMovie 获取不到返回数据. ");
                return;
            }
            refreshCategoryList(result.getMovies(), false);
            mAdapter.setFocus(result.getIndex());
            mViewList.scrollToPosition(result.getIndex());
        }
    }


    @Override
    public void refreshCategoryList(List<CategoryMovieBean> movies, boolean formUser) {
        mAdapter.setDatas(movies);
        mViewList.scrollToPosition(0);
        mCurPage = (int) (Math.ceil(mAdapter.getItemCount() * 1f / PAGE_SIZE));
        KLog.d(TAG, "更新前端数据 category = " + getCategory() + ", page = " + mCurPage + ", movies.size() =  " + (movies == null ? 0 : movies.size()));
        if (mAdapter.getItemCount() > 0) {
            mRefreshLayout.setVisibility(View.VISIBLE);
            mLayoutDataEmpty.setVisibility(View.GONE);
            mLayoutInternetUnavailable.setVisibility(View.GONE);

        } else {
            showDateEmpty();

        }
    }

    @Override
    public void loadMoreCategoryList(int page, List<CategoryMovieBean> movies) {
        KLog.d(TAG, "更新前端数据 category = " + getCategory() + ", page = " + page + ", movies.size() =  " + (movies == null ? 0 : movies.size()));
        if (movies == null || movies.size() <= 0) {
            return;
        }

        mCurPage = page;
        mAdapter.addDatas(movies);
        if (mAdapter.getItemCount() > 0) {
            KLog.d(TAG, "显示列表 adapter.count = " + mAdapter.getItemCount());
            mRefreshLayout.setVisibility(View.VISIBLE);
            mLayoutDataEmpty.setVisibility(View.GONE);
            mLayoutInternetUnavailable.setVisibility(View.GONE);

        } else {
            showDateEmpty();
            KLog.d(TAG, "显示空数据页面");
        }
    }

    @Override
    public void showErrorMsg(String msg) {
        Log.i(TAG, "showMsg: " + msg);
        ToastUtils.showShort(msg);
    }

    @Override
    public void onLeftRefreshing() {
        try {
            mPresenter.refreshCategoryList(getCategory(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRightRefreshing() {
        loadMoreData(true);
    }


    @Override
    public void finishLoadMore(long delay) {
        mRefreshLayout.onRefreshComplete();
    }

    @Override
    public void showDateEmpty() {
        mRefreshLayout.setVisibility(View.VISIBLE);
        mLayoutDataEmpty.setVisibility(View.VISIBLE);
        mLayoutInternetUnavailable.setVisibility(View.GONE);
    }

    @Override
    public void showNetWorkError() {
        mRefreshLayout.setVisibility(View.GONE);
        mLayoutDataEmpty.setVisibility(View.GONE);
        mLayoutInternetUnavailable.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLoading(boolean show) {
        mLayoutLoading.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void updateMovie(CategoryMovieBean updateMovie) {
        KLog.i(TAG, "updateMovie: " + updateMovie);
        try {
            List<CategoryMovieBean> movies = mAdapter.getDatas();
            for (int position = 0; position < movies.size(); position++) {
                CategoryMovieBean movie = movies.get(position);
                if (movie.getId() == updateMovie.getId()) {
                    mAdapter.setData(position, updateMovie);
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_reload:
                onClickReload();
                break;
        }
    }

    private void onClickReload() {
        mPresenter.refreshCategoryList(getCategory(), false);
    }

    @Override
    public void onAddFriend(long uid) {
        List<CategoryMovieBean> movies = mAdapter.getDatas();
        if (movies == null) {
            return;
        }
        for (CategoryMovieBean movie : movies) {
            if (movie.getUid() == uid) {
                movie.setIsFriend(CategoryMovieBean.IS_FRIEND);
            }
        }
    }

    @Override
    public void onUserRelationShipChange(long uid, int caseWhat) {
        if (uid == 0) {
            return;
        }

        List<CategoryMovieBean> movies = mAdapter.getDatas();
        if (movies == null) {
            return;
        }
        for (CategoryMovieBean movie : movies) {
            if (movie.getUid() == uid) {
                switch (caseWhat) {
                    case Global.CASE_FOLLOW:
                        movie.setIsAttention(CategoryMovieBean.IS_ATTENTION);
                        break;
                    case Global.CASE_UNFOLLOW:
                        movie.setIsAttention(CategoryMovieBean.UN_ATTENTION);
                        break;
                    case Global.CASE_ADD_FRIEND:
                        movie.setIsFriend(CategoryMovieBean.IS_FRIEND);
                        break;
                    case Global.CASE_DEL_FRIEND:
                        movie.setIsFriend(CategoryMovieBean.UN_FRIEND);
                        break;
                }
            }
        }
    }

    @Override
    public void updateMyPortrait(String newUrl) {
        KLog.d(TAG, "category = " + getCategory() + ", newUrl = " + newUrl);
        List<CategoryMovieBean> movies = mAdapter.getDatas();
        if (movies == null) {
            return;
        }

        long myUid = UserInfoManager.getUid();
        for (CategoryMovieBean movie : movies) {
            if (movie.getUid() == myUid) {
                movie.setAvatar(newUrl);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @DrawableRes
    protected abstract int getDataEmptyIcon();

    protected abstract String getDateEmptyText();

    /**
     * 条目点击监听
     */
    private IRecyclerItemClickListener mItemClickListener = new IRecyclerItemClickListener() {
        @Override
        public void onItemClick(RecyclerView parent, int position, RecyclerView.ViewHolder holder, View view) {
            int centerPosition = mLayoutManager.getCurrentPosition();
            KLog.i(TAG, "onItemClick pos = " + position + " centerPosition = " + centerPosition);
            if (centerPosition == position) {
                if (!NetworkUtil.isWifiAvailable(mContext) && !SystemSettingManager.isAutoPlay()) {
                    ConfirmDialog dialog = new ConfirmDialog.Builder()
                            .setTitleText(getString(R.string.dialog_mobile_networks_title))
                            .setContentText(getString(R.string.dialog_mobile_networks_content))
                            .setBtnPositiveText(getString(R.string.common_cancel))
                            .setBtnNagtiveText(getString(R.string.dialog_mobile_networks_btn_positive_text))
                            .setBtnNagtiveTextColor(0xFF007AFF)
                            .build();

                    dialog.setBtnClickListener(new ConfirmDialog.BtnClickListener() {
                        @Override
                        public void onPositiveBtnClick(ConfirmDialog dialog, View view) {
                            dialog.dismiss();
                        }

                        @Override
                        public void onNagtiveBtnClick(ConfirmDialog dialog, View view) {
                            SystemSettingManager.saveIsAutoPlay(true);
                            PlayerContact.IView.Data data = new PlayerContact.IView.Data();
                            data.setIndex(position);
                            data.setMovies(mAdapter.getDatas());
                            data.setCategory(getCategory());
                            data.setHideMenu(false);
                            Intent intent = PlayerActivity.getStartIntent(mContext, data);
                            startActivityForResult(intent, REQ_CODE_PLAY_MOVIE);
                            dialog.dismiss();
                        }
                    });

                    dialog.show(getChildFragmentManager(), "");
                } else {
                    PlayerContact.IView.Data data = new PlayerContact.IView.Data();
                    data.setIndex(position);
                    data.setMovies(mAdapter.getDatas());
                    data.setCategory(getCategory());
                    data.setHideMenu(false);

                    ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                            Pair.create(holder.itemView.findViewById(R.id.iv_portrait), getString(R.string.portrait_scene_transition)),
                            Pair.create(holder.itemView, getString(R.string.cover_scene_transition)));
                    Intent intent = PlayerActivity.getStartIntent(mContext, data);
                    startActivityForResult(intent, REQ_CODE_PLAY_MOVIE, compat.toBundle());
                }

            } else if (position == RecyclerView.NO_POSITION && centerPosition != RecyclerView.NO_POSITION) {
                ScrollHelper.smoothScrollToPosition(mViewList, mLayoutManager, centerPosition);
            } else {
                ScrollHelper.smoothScrollToTargetView(parent, holder.itemView);
            }
        }
    };

    /**
     * 滚动监听, 控制刷新及卡顿优化处理.
     */
    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {

        private int mmCenter;

        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            int centerPosition = mLayoutManager.getCurrentPosition();
            if (centerPosition != mmCenter) {
                onCenterPositionChange(centerPosition, mmCenter);
                mmCenter = centerPosition;
            }
            getActivity().startPostponedEnterTransition();
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            int centerPosition = mLayoutManager.getCurrentPosition();
            if (centerPosition != mmCenter) {
                onCenterPositionChange(centerPosition, mmCenter);
                mmCenter = centerPosition;
            }
            getActivity().startPostponedEnterTransition();
        }

        private void onCenterPositionChange(int newPosition, int oldPosition) {
            if (newPosition > oldPosition) {
                // 从 右向左 滚动
                if (mAdapter.getItemCount() - newPosition < 5) {
                    loadMoreData(false);
                }
            }
            changeFocus(newPosition);
        }
    };

    @Override
    public void clearData() {
        refreshCategoryList(null, false);
    }

    @Override
    public void refreshData() {
        try {
            mPresenter.refreshCategoryList(getCategory(), false);
        } catch (Exception e) {
            e.printStackTrace();
            // 修改系统配置时mPresenter 出现null
        }
    }

    private void loadMoreData(boolean fromUser) {
        try {
            mPresenter.loadMoreCategoryList(getCategory(), mCurPage + 1, fromUser);
        } catch (Exception e) {
            e.printStackTrace();
            // 修改系统配置时mPresenter 出现null
        }
    }

    private void changeFocus(int position) {
        mHandler.removeCallbacks(mFocusRunnable);
        mFocusRunnable = new Runnable() {
            @Override
            public void run() {
                mAdapter.setFocus(position);
            }
        };
        mHandler.postDelayed(mFocusRunnable, 60);
    }

    public int getCurPage() {
        return mCurPage;
    }

    protected void setCurPage(int curPage) {
        mCurPage = curPage;
    }

    private Runnable mFocusRunnable = null;

    public static void logMovies(String tag, List<CategoryMovieBean> movie) {
        if (movie == null) {
            KLog.d(tag, "movies 为空");
            return;
        }

        for (CategoryMovieBean movieBean : movie) {
            KLog.d(tag, movieBean.toString());
        }
    }
}
