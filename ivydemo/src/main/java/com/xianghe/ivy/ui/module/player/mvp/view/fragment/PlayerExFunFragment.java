package com.xianghe.ivy.ui.module.player.mvp.view.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.xianghe.ivy.R;
import com.xianghe.ivy.model.CategoryMovieBean;
import com.xianghe.ivy.model.CommentBean;
import com.xianghe.ivy.model.CommentItem;
import com.xianghe.ivy.model.evnetbus.MovieUpdateEvent;
import com.xianghe.ivy.ui.module.player.mvp.contact.ExFunContact;
import com.xianghe.ivy.ui.module.player.mvp.presenter.ExFunPresenter;
import com.xianghe.ivy.ui.module.player.mvp.view.fragment.base.PlayerFrameFragment;
import com.xianghe.ivy.utils.Formatter;
import com.xianghe.ivy.utils.KLog;
import com.xianghe.ivy.utils.ToastUtil;
import com.xianghe.ivy.weight.ClickButton;
import com.xianghe.ivy.weight.CustomProgress;

import org.greenrobot.eventbus.EventBus;

public class PlayerExFunFragment extends PlayerFrameFragment<ExFunContact.IView, ExFunContact.Presenter> implements ExFunContact.IView, View.OnClickListener {
    private static final String TAG = "PlayerExFunFragment";


    private Context mContext;

    private DrawerLayout mDrawerLayout;
    private ClickButton mBtnComment;    // 评论按钮
    private ClickButton mBtnFollow;     // 收藏按钮
    private ClickButton mBtnDianZan;    // 点赞按钮
    private ClickButton mBtnShare;      // 分享按钮

    private TextView mTvCommentCount;   // 评论数
    private TextView mTvFollowCount;    // 收藏数
    private TextView mTvDianzanCount;   // 点赞数
    private TextView mTvShareCount;     // 分享数

    private ShareFragment mFragmentShare;       // 分享fragment(涉及项： 分享、下载、举报)
    private CommentFragment mFragmentComment;   // 评论fragment(涉及项： 评论、回复评论、删除评论)

    private MenuStateListener mMenuStateListener;   // 菜单状态回调

    @Nullable
    @Override
    public ExFunContact.Presenter createPresenter() {
        return new ExFunPresenter(getContext());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_player_ex_fun, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findView(view, savedInstanceState);
        initView(view, savedInstanceState);
        initListener(view, savedInstanceState);
    }

    private void findView(View rootView, Bundle savedInstanceState) {
        mDrawerLayout = rootView.findViewById(R.id.drawer_layout);

        // 评论
        mBtnComment = rootView.findViewById(R.id.btn_comment);
        mTvCommentCount = rootView.findViewById(R.id.tv_comment_count);

        // 收藏
        mBtnFollow = rootView.findViewById(R.id.btn_follow);
        mTvFollowCount = rootView.findViewById(R.id.tv_follow_count);

        // 点赞
        mBtnDianZan = rootView.findViewById(R.id.btn_dianzan);
        mTvDianzanCount = rootView.findViewById(R.id.tv_dianzan_count);

        // 分享
        mBtnShare = rootView.findViewById(R.id.btn_share);
        mTvShareCount = rootView.findViewById(R.id.tv_share_count);

        FragmentManager fragmentManager = getChildFragmentManager();
        // 评论fragment
        mFragmentComment = (CommentFragment) fragmentManager.findFragmentById(R.id.fragment_comment);
        // 分享fragment
        mFragmentShare = (ShareFragment) fragmentManager.findFragmentById(R.id.fragment_share);

    }

    private void initView(View rootView, Bundle savedInstanceState) {
        mDrawerLayout.setEnabled(false);
        mDrawerLayout.setScrimColor(Color.TRANSPARENT);
        lockDrawer(true);

        CategoryMovieBean bean = getMovie();
        updateUI(bean);
    }

    private void initListener(View rootView, Bundle savedInstanceState) {
        mBtnComment.setOnClickListener(this);
        mBtnFollow.setOnClickListener(this);
        mBtnDianZan.setOnClickListener(this);
        mBtnShare.setOnClickListener(this);

        mDrawerLayout.addDrawerListener(mDrawerListener);

        mFragmentComment.setListener(mCommentFragmentListener);
        mFragmentShare.setListener(mShareFragmentListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_comment:
                if(getMovie() != null){
                    onClickComment();
                }
                break;
            case R.id.btn_follow:
                if(getMovie() != null){
                    onClickFollow();
                }
                break;
            case R.id.btn_dianzan:
                if(getMovie() != null){
                    onClickDianZan();
                }

                break;
            case R.id.btn_share:
                if(getMovie() != null){
                    onClickShare();
                }
                break;
        }
    }

    private void onClickComment() {
        if (!mPresenter.isLogin()) {
            showLoginConfirmDialog();
            return;
        }

        mBtnComment.performanceClick();
        openMenu(Meuns.COMMENT);
    }

    private void onClickFollow() {
        if (!mPresenter.isLogin()) {
            showLoginConfirmDialog();
            return;
        }

        // 放大缩小动画
        mBtnFollow.performanceClick();

        if (mBtnFollow.isSelected()) {
            // 原本已关注, 现在取消关注;
            mPresenter.follow(getMovie().getId(), false);

        } else {
            // 原本未关注, 现在进行关注;
            mPresenter.follow(getMovie().getId(), true);

        }
    }

    public void onClickDianZan() {
        if (!mPresenter.isLogin()) {
            showLoginConfirmDialog();
            return;
        }

        // 点赞不能取消哦
        if (mBtnDianZan.isSelected()) {
            return;
        }

        // 放大缩小动画
        mBtnDianZan.performanceClick();

        if (mBtnDianZan.isSelected()) {
            // 原本已点赞, 现在取消点赞;
            mPresenter.praise(getMovie().getId(), false);

        } else {
            // 原本未点赞, 现在进行点赞;
            mPresenter.praise(getMovie().getId(), true);
        }
    }

    private void onClickShare() {
        KLog.d(TAG, "onClickShare: " + this);
        mBtnShare.performanceClick();
        openMenu(Meuns.SHARE);
    }

    @Override
    public void onMovieChange(CategoryMovieBean movie) {
        super.onMovieChange(movie);
        mFragmentComment.onMovieChange(movie);
        mFragmentShare.onMovieChange(movie);
        updateUI(movie);
    }

    /**
     * 打开drawer
     */
    @Override
    public void openMenu(Meuns menu) {
        if (mMenuStateListener != null) {
            mMenuStateListener.onDrawerOpened();
        }

        mDrawerLayout.openDrawer(Gravity.RIGHT);

        switch (menu) {
            case SHARE:
                mBtnShare.setSelected(true);
                getChildFragmentManager().beginTransaction()
                        .show(mFragmentShare)
                        .hide(mFragmentComment)
                        .commit();
                break;
            case COMMENT:
                mBtnComment.setSelected(true);
                getChildFragmentManager().beginTransaction()
                        .show(mFragmentComment)
                        .hide(mFragmentShare)
                        .commit();
                break;
        }
    }

    /**
     * 关闭drawer
     */
    @Override
    public void closeMenu() {
        mDrawerLayout.closeDrawer(Gravity.RIGHT);
    }

    @Override
    public void showMsg(String msg) {
        ToastUtil.showToast(getContext(), msg);
    }

    @Override
    public void followResult(long mediaId, boolean follow, boolean success) {
        if (success) {
            if (follow) {
                CategoryMovieBean movie = getMovie();
                movie.setIsStar(CategoryMovieBean.START);
                movie.setStar(movie.getStar() + 1);

                // ui 更新
                mBtnFollow.setSelected(true);
                mTvFollowCount.setText(String.valueOf(movie.getStar()));

                // 发送广播
                EventBus.getDefault().post(new MovieUpdateEvent(movie));
            } else {
                CategoryMovieBean movie = getMovie();
                movie.setIsStar(CategoryMovieBean.START_CANCEL);
                movie.setStar(movie.getStar() - 1);

                // ui 更新
                mBtnFollow.setSelected(false);
                mTvFollowCount.setText(String.valueOf(movie.getStar()));

                // 发送广播
                EventBus.getDefault().post(new MovieUpdateEvent(movie));
            }
        }
    }

    @Override
    public void praiseResult(long mediaId, boolean praise, boolean success) {
        if (success) {
            if (praise) {
                // 数据 更新
                CategoryMovieBean movie = getMovie();
                movie.setIsLike(CategoryMovieBean.LIKE);
                movie.setLike(movie.getLike() + 1);

                // ui 更新
                mBtnDianZan.setSelected(true);
                mTvDianzanCount.setText(String.valueOf(movie.getLike()));

                // 发送广播
                EventBus.getDefault().post(new MovieUpdateEvent(movie));

            } else {
                // 数据 更新
                CategoryMovieBean movie = getMovie();
                movie.setIsLike(CategoryMovieBean.DISLIKE);
                movie.setLike(movie.getLike() - 1);

                // ui 更新
                mBtnDianZan.setSelected(false);
                mTvDianzanCount.setText(String.valueOf(movie.getLike()));

                // 发送广播
                EventBus.getDefault().post(new MovieUpdateEvent(movie));
            }
        }
    }

    /**
     * 设置drawer 是否可以拖动操作
     *
     * @param lock true 不可拖动操作, false 可拖动操作.
     */
    public void lockDrawer(boolean lock) {
        if (lock) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } else {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }

    /**
     * 菜单是否打开
     *
     * @return true 打开, false 关闭.
     */
    @Override
    public boolean isMenuOpen() {
        return mDrawerLayout.isDrawerOpen(Gravity.RIGHT);
    }


    @Override
    public void updateUI(CategoryMovieBean bean) {
        if (bean == null) {
            return;
        }
        mBtnFollow.setSelected(bean.getIsStar() == CategoryMovieBean.START);
        mBtnDianZan.setSelected(bean.getIsLike() == CategoryMovieBean.LIKE);

        mTvCommentCount.setText(Formatter.formatNumber(bean.getComments()));
        mTvFollowCount.setText(Formatter.formatNumber(bean.getStar()));
        mTvDianzanCount.setText(Formatter.formatNumber(bean.getLike()));
        mTvShareCount.setText(Formatter.formatNumber(bean.getShare()));
    }

    private CustomProgress mCustomProgress;

    public void showLoginConfirmDialog() {
        if (mCustomProgress == null) {
            mCustomProgress = new CustomProgress(mContext);
        }
        mCustomProgress.show(getString(R.string.common_tips_no_login),
                getString(R.string.common_tips_title),
                getString(R.string.common_cancel),
                getString(R.string.common_ensure),
                view -> mCustomProgress.cancel(),
                view -> {
                    mPresenter.jump2Login(mContext);
                    mCustomProgress.cancel();
                },
                true, null);
    }

    public CommentFragment getFragmentComment() {
        return mFragmentComment;
    }

    public ClickButton getBtnComment() {
        return mBtnComment;
    }

    public ClickButton getBtnDianZan() {
        return mBtnDianZan;
    }

    public ClickButton getBtnFollow() {
        return mBtnFollow;
    }

    public ClickButton getBtnShare() {
        return mBtnShare;
    }

    public MenuStateListener getMenuStateListener() {
        return mMenuStateListener;
    }

    public void setMenuStateListener(MenuStateListener menuStateListener) {
        mMenuStateListener = menuStateListener;
    }

    /**
     * 监听drawer 打开/关闭，drawer 设置可滑动， drawer关闭后设置不可滑动
     */
    private DrawerLayout.DrawerListener mDrawerListener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(@NonNull View view, float v) {
        }

        @Override
        public void onDrawerOpened(@NonNull View view) {
            lockDrawer(false);

            if (mFragmentComment.isVisible()) {
                mFragmentComment.onMenuOpened();

            } else {
                mFragmentShare.onMenuOpened();

            }
        }

        @Override
        public void onDrawerClosed(@NonNull View view) {
            lockDrawer(true);

            if (mFragmentComment.isVisible()) {
                mBtnComment.performanceClick();
                mBtnComment.setSelected(false);
                mFragmentComment.onMenuClosed();

            } else if (mFragmentShare.isVisible()) {
                mBtnShare.performanceClick();
                mBtnShare.setSelected(false);
                mFragmentShare.onMenuClosed();

            }

            if (mMenuStateListener != null) {
                mMenuStateListener.onDrawerClosed();
            }
        }

        @Override
        public void onDrawerStateChanged(int i) {
        }
    };


    /**
     * 分享fragment相关回调
     */
    private ShareFragment.Listener mShareFragmentListener = new ShareFragment.Listener() {
        @Override
        public void onClickEmpty() {
            closeMenu();
        }

        @Override
        public void onShareResult(String name, boolean result) {
            if (result) {
                CategoryMovieBean movie = getMovie();
                if (movie != null) {
                    int shareCount = movie.getShare() + 1;
                    movie.setShare(shareCount);
                    mTvShareCount.setText(String.valueOf(shareCount));
                }
            }
        }

        @Override
        public void onReply(long mediaId, boolean success) {
            if (success) {
                CategoryMovieBean movie = getMovie();
                if (movie != null) {
                    int newCount = mMovieBean.getComments() + 1;
                    mMovieBean.setComments(newCount);
                    mTvCommentCount.setText(String.valueOf(newCount));

                    int shareCount = movie.getShare() + 1;
                    movie.setShare(shareCount);
                    mTvShareCount.setText(String.valueOf(shareCount));
                }
            }
        }
    };

    /**
     * 评论fragment相关回调
     */
    private CommentFragment.CommentFragmentListener mCommentFragmentListener = new CommentFragment.CommentFragmentListener() {
        @Override
        public void onCommentRefresh(CommentBean comment) {
            KLog.d(TAG, "onCommentRefresh");
            int commentsTotal = comment.getCommentsTotal();
            mMovieBean.setComments(commentsTotal);
            mTvCommentCount.setText(String.valueOf(commentsTotal));
        }

        @Override
        public void onCommentRefreshLoadMore(CommentBean comment) {
            KLog.d(TAG, "onCommentRefreshLoadMore");
            int commentsTotal = comment.getCommentsTotal();
            mMovieBean.setComments(commentsTotal);
            mTvCommentCount.setText(String.valueOf(commentsTotal));
        }

        @Override
        public void onCommentItemAdd(CommentBean comment, CommentItem commentItem) {
            KLog.d(TAG, "onCommentItemAdd");
            int commentsTotal = comment.getCommentsTotal();
            mMovieBean.setComments(commentsTotal);
            mTvCommentCount.setText(String.valueOf(commentsTotal));
        }
    };

    public interface MenuStateListener {

        public void onDrawerOpened();

        public void onDrawerClosed();
    }
}
