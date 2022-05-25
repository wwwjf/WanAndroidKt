package com.xianghe.ivy.ui.module.user.movie;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.wwwjf.base.utils.ToastUtil;
import com.xianghe.ivy.R;
import com.xianghe.ivy.adapter.adapter.BaseQuickAdapter;
import com.xianghe.ivy.adapter.adapter.loadmore.SimpleLoadMoreView;
import com.xianghe.ivy.app.IvyApp;
import com.xianghe.ivy.app.IvyConstants;
import com.xianghe.ivy.constant.Api;
import com.xianghe.ivy.constant.Global;
import com.xianghe.ivy.constant.RxBusCode;
import com.xianghe.ivy.manager.SystemSettingManager;
import com.xianghe.ivy.manager.UserInfoManager;
import com.xianghe.ivy.model.CategoryMovieBean;
import com.xianghe.ivy.model.evnetbus.MovieUpdateEvent;
import com.xianghe.ivy.model.response.BaseResponse;
import com.xianghe.ivy.mvp.loadPager.BaseMVPLoadingFragment;
import com.xianghe.ivy.network.GsonHelper;
import com.xianghe.ivy.ui.module.player.PlayerOtherActivity;
import com.xianghe.ivy.ui.module.player.mvp.contact.PlayerContact;
import com.xianghe.ivy.ui.push.PushModel;
import com.xianghe.ivy.utils.KLog;
import com.xianghe.ivy.utils.NetworkUtil;
import com.xianghe.ivy.weight.CustomProgress;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import gorden.rxbus2.RxBus;
import gorden.rxbus2.Subscribe;
import gorden.rxbus2.ThreadMode;


public class UserMovieFragment extends BaseMVPLoadingFragment<UserMovieContract.View, UserMoviePresenter>
        implements UserMovieContract.View, SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    Unbinder mBinder;
    View mEmptyView;


    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout mRefreshLayout;
    @BindView(R.id.recyclerView_user_movie)
    RecyclerView mRecyclerViewUserMovie;


    private UserMovie2Adapter mUserMovie2Adapter;
    private List<CategoryMovieBean> mMovieList;
    private CustomProgress mCustomProgress;
    private int mPage;
    private long mUid;
    private boolean isShowDelete;
    private long mLoginId;
    private int focusPosition;

    protected Handler mHandler = new Handler();

    private UserRelationshipBroadCast mUserRelationshipBroadCast;

    @Override
    public boolean isNeedRefresh() {
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            if (!TextUtils.isEmpty(mParam1)) {
                mUid = Long.parseLong(mParam1);
            }
        }
        //注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Global.USER_RELATIONSHIP_CHANGE);
        mUserRelationshipBroadCast = new UserRelationshipBroadCast();
        requireActivity().registerReceiver(mUserRelationshipBroadCast,intentFilter);
    }

    @Override
    public void onStart() {
        super.onStart();
        RxBus.get().register(this);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void initEvent() {
        mEmptyView = getLayoutInflater().inflate(R.layout.view_empty_movie_data, null);
        mBinder = ButterKnife.bind(this, requireView());
        mCustomProgress = new CustomProgress(getContext());
        mMovieList = new ArrayList<>();
        mLoginId = UserInfoManager.getUid();
        if (mUid == 0 || mUid == mLoginId) {
            isShowDelete = true;
        }
        mUserMovie2Adapter = new UserMovie2Adapter(mMovieList,isShowDelete);

        mUserMovie2Adapter.setOnLoadMoreListener(this, mRecyclerViewUserMovie);
        mUserMovie2Adapter.setLoadMoreView(new SimpleLoadMoreView());
        mUserMovie2Adapter.setOnItemClickListener(mOnItemClickListener);
        mUserMovie2Adapter.setOnItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()) {
                case R.id.iv_layout_user_movie2_delete:
                    showDeleteDialog(adapter, view, position);
                    break;
                default:
                    break;
            }
        });

        mRecyclerViewUserMovie.setLayoutManager(new GridLayoutManager(getContext(),2));
        mRecyclerViewUserMovie.setAdapter(mUserMovie2Adapter);
        mRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.red_color_FF4855));
        mRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void initData() {
        dismissLoading();
        mPage = Api.Value.START_PAGE;
        if (UserInfoManager.isLogin()) {
            if (mPresenter != null) {
                mPresenter.requestData(mUid, mPage);
            }
        } else {
            if (!NetworkUtil.isNetworkConnected(requireContext())) {
                showError();
            } else {
                showEmptyView(mEmptyView);
            }
        }
    }

    @NotNull
    @Override
    public Object onCreateContentView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle state) {
        return initViews(inflater, container);
    }

    protected View initViews(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_user_movie, container, false);
    }

    /**
     * 显示删除提示
     *
     * @param adapter
     * @param view
     * @param position
     */
    private void showDeleteDialog(BaseQuickAdapter adapter, View view, int position) {
        CategoryMovieBean movieBean = (CategoryMovieBean) adapter.getData().get(position);
        mCustomProgress.show(getString(R.string.common_is_delete_media),
                getString(R.string.common_tips_title),
                getString(R.string.common_cancel),
                getString(R.string.common_ensure),
                v -> mCustomProgress.cancel(),
                v -> {
                    if (mPresenter != null) {
                        mPresenter.deleteUserMovie(movieBean.getId(), position);
                        mCustomProgress.cancel();
                    }
                },
                true, null);
    }

    /**
     * 条目点击监听
     */
    private BaseQuickAdapter.OnItemClickListener mOnItemClickListener = new BaseQuickAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(),
                    view, getString(R.string.cover_scene_transition));
            if (!NetworkUtil.isWifiAvailable(mContext) && !SystemSettingManager.isAutoPlay()) {
                mCustomProgress.show(getString(R.string.common_gprs_tips),
                        getString(R.string.common_gprs_tips_title),
                        getString(R.string.common_cancel),
                        getString(R.string.common_ensure),
                        v -> mCustomProgress.cancel(),
                        v -> {
                            SystemSettingManager.saveIsAutoPlay(true);
                            PlayerContact.IView.Data data = new PlayerContact.IView.Data();
                            data.setLoadType(PlayerContact.IView.Data.LOAD_TYPE_USER_MEDIA);
                            data.setUid(mUid);
                            data.setIndex(position);
                            data.setMovies(mMovieList);
                            data.setMine(mUid == mLoginId || mUid == 0);
                            Intent intent = PlayerOtherActivity.getStartIntent(mContext, data);
                            startActivityForResult(intent, IvyConstants.REQUEST_CODE_20003, compat.toBundle());
                            mCustomProgress.cancel();
                        },
                        true, null);

            } else {
                PlayerContact.IView.Data data = new PlayerContact.IView.Data();
                data.setLoadType(PlayerContact.IView.Data.LOAD_TYPE_USER_MEDIA);
                data.setUid(mUid);
                data.setIndex(position);
                data.setMovies(mMovieList);
                data.setMine(true);
                Intent intent = PlayerOtherActivity.getStartIntent(mContext, data);
                startActivityForResult(intent, IvyConstants.REQUEST_CODE_20003, compat.toBundle());
            }
        }
    };



    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBinder != null) {
            mBinder.unbind();
        }
        RxBus.get().unRegister(this);
        EventBus.getDefault().unregister(this);
        if (mUserRelationshipBroadCast != null) {
            requireActivity().unregisterReceiver(mUserRelationshipBroadCast);
        }
    }

    @Nullable
    @Override
    public UserMoviePresenter createPresenter() {
        return new UserMoviePresenter();
    }

    private void showToast(String msg) {
        ToastUtil.show(requireActivity(), msg);
    }

    @Override
    public void onFailed(Throwable throwable) {
        mRefreshLayout.setRefreshing(false);
        String message = throwable.getMessage();
        if (!TextUtils.isEmpty(message)) {
            showToast(message);
        }
        if (mMovieList.isEmpty()) {
            showEmptyView(mEmptyView);
        }
    }

    @Override
    public void onRequestDataSuccess(BaseResponse<List<CategoryMovieBean>> d, int page) {
        mRefreshLayout.setRefreshing(false);
        mMovieList.clear();
        focusPosition = 0;
        if (d.getData() == null || d.getData().size() == 0) {
            showEmptyView(mEmptyView);
        } else {
            showSuccess();
            mMovieList.addAll(d.getData());
        }
        mUserMovie2Adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoadMoreDataSuccess(BaseResponse<List<CategoryMovieBean>> response, int page) {
        if (response.getData() == null || response.getData().size()==0) {
            mUserMovie2Adapter.loadMoreComplete();//加载更多数据完成
            mUserMovie2Adapter.loadMoreEnd(false);
            showSuccess();
//            showToast(getString(R.string.view_no_more_media_data));
            mPage--;
        } else {
            mMovieList.addAll(response.getData());
            mUserMovie2Adapter.loadMoreComplete();
            mUserMovie2Adapter.setEnableLoadMore(true);
            showSuccess();
        }
        if (mMovieList.size() == 0) {
            showEmptyView(mEmptyView);
        }
        mUserMovie2Adapter.notifyDataSetChanged();
    }

    @Override
    public void onNetworkError(Throwable throwable) {
        mRefreshLayout.setRefreshing(false);
        String message = throwable.getMessage();
        if (!TextUtils.isEmpty(message)) {
            showToast(message);
        }
        showError();
    }


    @Override
    public void onDeleteUserMovieSuccess(BaseResponse<String> response, int positon) {
        showToast(getString(R.string.user_movie_delete_success));
        RxBus.get().send(RxBusCode.ACT_MOVIE_DELETE);
        int size = mUserMovie2Adapter.getData().size();
        if (size > positon) {
            mMovieList.remove(positon);
        }
        // 处理缓存信息
        String userMoviesCache = GsonHelper.object2JsonStr(mMovieList);
        long cacheUid;
        if (mUid == 0 || mUid == -1) {
            cacheUid = UserInfoManager.getUid();
        } else {
            cacheUid = mUid;
        }
        IvyApp.getInstance().getACache().put(Global.cache_key_userMovieResponse+cacheUid,userMoviesCache);

        if (mUserMovie2Adapter.getData().size() == 0) {
            clearNetworkCache();
            mPage = Api.Value.START_PAGE;
            if (mPresenter != null) {
                mPresenter.requestData(mUid, mPage);
            }
        }

        mUserMovie2Adapter.notifyDataSetChanged();

    }

    @Override
    public void onDeleteUserMovieFailed(Throwable throwable) {
        String message = throwable.getMessage();
        if (!TextUtils.isEmpty(message)) {
            showToast(message);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case IvyConstants.REQUEST_CODE_20003:
                //关注数、获赞数、收藏数、亲友数变化
                RxBus.get().send(RxBusCode.ACT_RELATIVE_FRIEND);
                //更新数据
                PlayerOtherActivity.Data result = PlayerOtherActivity.parseData(data);
                if (result == null) {
                    return;
                }
                mMovieList.clear();
                mMovieList.addAll(result.getMovies());
                mPage = (int) (Math.ceil(mMovieList.size() * 1f / Api.Value.START_PAGE) + 1);
                mUserMovie2Adapter.notifyDataSetChanged();
                mRecyclerViewUserMovie.scrollToPosition(result.getIndex());

                //处理缓存
                long cacheUid;
                if (mUid == 0 || mUid == -1) {
                    cacheUid = UserInfoManager.getUid();
                } else {
                    cacheUid = mUid;
                }
                IvyApp.getInstance().getACache().remove(Global.cache_key_userMovieResponse+cacheUid);
                break;
            default:
                break;
        }
    }

    @Subscribe(code = RxBusCode.ACT_USER_LOGOUT, threadMode = ThreadMode.MAIN)
    public void logoutEvent() {
        KLog.e("------退出登录，刷新影片列表");
        initLogoutView();
    }

    @Subscribe(code = RxBusCode.ACT_USER_LOGIN, threadMode = ThreadMode.MAIN)
    public void loginEvent() {
        clearNetworkCache();
        mPage = Api.Value.START_PAGE;
        if (mPresenter != null) {
            mPresenter.requestData(mUid, mPage);
        }
    }

    @Subscribe(code = RxBusCode.ACT_USER_MOVIE_PUBLISH_SUCCESS, threadMode = ThreadMode.MAIN)
    public void userMoviePublishEvent() {
        if (focusPosition >= Api.Value.START_PAGE) {
            mRecyclerViewUserMovie.scrollToPosition(0);
        }
        clearNetworkCache();
        mPage = Api.Value.START_PAGE;
        if (mPresenter != null) {
            mPresenter.requestData(mUid, mPage);
        }
    }

    @Subscribe(code = RxBusCode.ACT_USER_UNLOGIN_VIEW, threadMode = ThreadMode.MAIN)
    public void userUnLoginView() {
        initLogoutView();
    }

    @Subscribe(code = RxBusCode.ACT_USER_PUSH_RELATIVE, threadMode = ThreadMode.MAIN)
    public void pushRelativeEvent(PushModel pushModel) {
        KLog.e("-----有亲友通过了你的亲友申请");
        if (pushModel != null) {
            long uid = pushModel.getId();
            for (CategoryMovieBean movieBean : mMovieList) {
                if (movieBean.getUid() == uid) {
                    movieBean.setIsFriend(1);
                }
            }
            mUserMovie2Adapter.notifyDataSetChanged();
        }
    }

    @org.greenrobot.eventbus.Subscribe(threadMode = org.greenrobot.eventbus.ThreadMode.MAIN)
    public void onReceiveEventMovieUpdate(MovieUpdateEvent event) {
        clearNetworkCache();
        mPage = Api.Value.START_PAGE;
        if (mPresenter != null) {
            mPresenter.requestData(mUid, mPage);
        }
    }

    private void initLogoutView() {
        mMovieList.clear();
        mUserMovie2Adapter.notifyDataSetChanged();
        showEmpty();
        showEmptyView(mEmptyView);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        KLog.e("--------------onConfigurationChanged");
    }

    /**
     * 清除接口缓存数据
     */
    private void clearNetworkCache() {
        long cacheUid;
        if (mUid == 0 || mUid == -1) {
            cacheUid = UserInfoManager.getUid();
        } else {
            cacheUid = mUid;
        }
        IvyApp.getInstance().getACache().remove(Global.cache_key_userMovieResponse+cacheUid);
    }

    @Override
    public void onRefresh() {
        mPage = Api.Value.START_PAGE;
        if (!UserInfoManager.isLogin()) {//未登录不请求数据
            mRefreshLayout.setRefreshing(false);
            return;
        }
        if (mPresenter != null) {
            mPresenter.requestData(mUid, mPage);
        }
    }

    @Override
    public void onLoadMoreRequested() {
        if (mPresenter != null) {
            mPage++;
            mPresenter.requestData(mUid, mPage);
        }
    }

    class UserRelationshipBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            long uid = intent.getLongExtra(Global.EXTRA_KEY_UID, 0);
            int caseWhat = intent.getIntExtra(Global.EXTRA_KEY_CASE, Global.CASE_UNKOWN);
            if (mUid != uid){
                return;
            }
            for (CategoryMovieBean movieBean : mMovieList) {
                switch (caseWhat){
                    case Global.CASE_FOLLOW:
                        movieBean.setIsAttention(1);
                        break;
                    case Global.CASE_UNFOLLOW:
                        movieBean.setIsAttention(0);
                        break;
                    case Global.CASE_ADD_FRIEND:
                        movieBean.setIsFriend(2);
                        break;
                    case Global.CASE_DEL_FRIEND:
                        movieBean.setIsFriend(0);
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
