package com.xianghe.ivy.ui.module.player.mvp.view.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xianghe.ivy.R;
import com.xianghe.ivy.adapter.recyclerview.IRecyclerItemClickListener;
import com.xianghe.ivy.constant.Api;
import com.xianghe.ivy.manager.UserInfoManager;
import com.xianghe.ivy.model.CommentBean;
import com.xianghe.ivy.model.CommentItem;
import com.xianghe.ivy.model.CategoryMovieBean;
import com.xianghe.ivy.ui.module.player.adapter.CommentAdapter;
import com.xianghe.ivy.ui.module.player.dialog.TextInputDialog;
import com.xianghe.ivy.ui.module.player.mvp.contact.CommentContact;
import com.xianghe.ivy.ui.module.player.mvp.presenter.CommentPresenter;
import com.xianghe.ivy.ui.module.player.mvp.view.fragment.base.MenuFragment;
import com.xianghe.ivy.utils.KLog;
import com.xianghe.ivy.weight.CustomProgress;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;


public class CommentFragment extends MenuFragment<CommentContact.IView, CommentContact.IPresenter>
        implements CommentContact.IView, View.OnClickListener, IRecyclerItemClickListener,
        OnRefreshListener, OnLoadMoreListener {

    private static final String TAG = "CommentFragment";

    private static final int REQ_CODE_COMMENT_MEDIA = 666;  // request code 评论
    private static final int REQ_CODE_COMMENT_REPEAT = 999; // request code 回复

    private final int PAGE_SIZE = 10;

    private final SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    private Context mContext = null;

    private SmartRefreshLayout mRefreshLayout;  // 上拉加载更多下拉刷新
    private ClassicsFooter mRefreshFooter;

    private TextView mTvCommentCount;   // 评论总数
    private RecyclerView mViewList;     // 评论列表
    private TextView mTvInput;              // 输出栏
    private View mProgress;             // loading

    private CommentAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private CommentFragmentListener mListener;

    @Nullable
    @Override
    public CommentContact.IPresenter createPresenter() {
        return new CommentPresenter(getContext());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        mAdapter = new CommentAdapter();
        init(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_comment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findView(view, savedInstanceState);
        initView(view, savedInstanceState);
        initListener(view, savedInstanceState);
    }

    private void init(@SuppressWarnings("unused") Bundle savedInstanceState) {
        ClassicsFooter.REFRESH_FOOTER_FINISH = getString(R.string.comment_load_complete);
    }

    private void findView(View rootView, @SuppressWarnings("unused") Bundle savedInstanceState) {
        mRefreshLayout = rootView.findViewById(R.id.refresh_layout);
        mRefreshFooter = rootView.findViewById(R.id.refresh_footer);

        mTvCommentCount = rootView.findViewById(R.id.tv_comment_count);
        mViewList = rootView.findViewById(R.id.view_list);
        mTvInput = rootView.findViewById(R.id.tv_input);
        mProgress = rootView.findViewById(R.id.progress);
    }

    private void initView(View rootView, @SuppressWarnings("unused") Bundle savedInstanceState) {
        mLayoutManager = new LinearLayoutManager(getContext());
        mViewList.setLayoutManager(mLayoutManager);
        mViewList.setAdapter(mAdapter);
    }

    private void initListener(View rootView, @SuppressWarnings("unused") Bundle savedInstanceState) {
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setOnLoadMoreListener(this);

        mTvInput.setOnClickListener(this);
        mAdapter.setItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // 该dialog使用 activity实现的,
        // 在onActivityResult中获取输入结果.
        TextInputDialog.from(mContext)
                .setHint(mContext.getString(R.string.comment_input_hint))
                .setText((String) mTvInput.getTag())
                .show(this, REQ_CODE_COMMENT_MEDIA);
    }


    @Override
    public void onItemClick(RecyclerView parent, int position, RecyclerView.ViewHolder holder, View view) {
        switch (view.getId()) {
            case R.id.tv_name:
                onCommentItemClickName(position, view);
                break;
            case R.id.btn_delete:
                onCommentItemClickDelete(position, view);
                break;
            case R.id.btn_report:
                onCommentItemClickReport(position, view);
                break;
            case R.id.iv_portrait:
                onCommentItemClickPortrait(position, view);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_COMMENT_MEDIA:
                onResultCommentMediaInput(requestCode, resultCode, data);
                break;
            case REQ_CODE_COMMENT_REPEAT:
                onResultCommentRepeatInput(requestCode, resultCode, data);
                break;
        }
    }

    /**
     * 评论输入结果回调
     *
     * @param requestCode requestCode
     * @param resultCode  resultCode
     * @param data        data
     */
    @SuppressWarnings("unused")
    private void onResultCommentMediaInput(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            String inputText = TextInputDialog.parseResult(data);
            if (inputText == null || TextUtils.isEmpty(inputText.trim())) {
                Toast.makeText(mContext, mContext.getString(R.string.comment_content_is_null_hint), Toast.LENGTH_SHORT).show();
            } else {
                String inputContent = inputText.trim();
                mTvInput.setTag(inputContent);

                CommentItem commentBean = new CommentItem();
                commentBean.setUid(UserInfoManager.getUid());
                commentBean.setContent(inputContent);
                commentBean.setCreatedAt(mDateFormat.format(System.currentTimeMillis()));

                // 上传服务器
                if (mPresenter != null) {
                    mPresenter.addCommentItem(mMovieBean.getId(), commentBean);
                }
            }
        }
    }

    /**
     * 回复输入结果回调
     *
     * @param requestCode requestCode
     * @param resultCode  resultCode
     * @param data        data
     */
    @SuppressWarnings("unused")
    private void onResultCommentRepeatInput(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {

            // 解析用户数据
            String inputText = TextInputDialog.parseResult(data);
            Serializable tag = TextInputDialog.parseTag(data);

            CommentItem repeatComment = null;
            if (tag instanceof CommentItem) {
                repeatComment = (CommentItem) tag;
            }

            if (inputText == null || TextUtils.isEmpty(inputText.trim())) {
                Toast.makeText(mContext, mContext.getString(R.string.comment_content_is_null_hint), Toast.LENGTH_SHORT).show();
                return;
            }
            String inputContent = inputText.trim();
            mTvInput.setTag(inputContent);

            CommentItem commentBean = new CommentItem();
            commentBean.setUid(UserInfoManager.getUid());
            commentBean.setContent(inputContent);
            commentBean.setCreatedAt(mDateFormat.format(System.currentTimeMillis()));

            if (repeatComment != null) {
                commentBean.setReplyUid(repeatComment.getUid());
                commentBean.setReplyName(repeatComment.getName());
                if (mPresenter != null) {
                    mPresenter.repeatComment(mMovieBean.getId(), repeatComment.getId(), commentBean);
                }
            } else {
                KLog.e(TAG, "repeatComment is null");
            }
        }
    }

    /**
     * 点击评论的 username
     *
     * @param position position
     * @param view     view
     */
    @SuppressWarnings("unused")
    private void onCommentItemClickName(int position, View view) {
        //Toast.makeText(mContext, "click name position=" + position, Toast.LENGTH_SHORT).show();
    }

    /**
     * 点击评论的 删除按钮
     *
     * @param position position
     * @param view     view
     */
    @SuppressWarnings("unused")
    private void onCommentItemClickDelete(int position, View view) {
        // 删除弹出确认框
        CustomProgress dialog = new CustomProgress(mContext);
        dialog.show(getString(R.string.comment_delete_dialog_content),
                getString(R.string.common_tips_title),
                getString(R.string.common_cancel),
                getString(R.string.common_delete),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {   // 点击取消
                        dialog.dismiss();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {   // 点击确认
                        try {
                            int firstItem = mLayoutManager.findFirstVisibleItemPosition();    //第一个可见item
                            CommentItem firstVisibleComment = mAdapter.getItem(firstItem);
                            CommentBean commentBean = mAdapter.getComment();
                            CommentItem commentItem = mAdapter.getItem(position);

                            if (mPresenter != null && commentBean != null) {
                                mPresenter.deleteCommentItem(commentBean.getMediaId(), commentItem, firstVisibleComment == null ? 0 : firstVisibleComment.getId());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                }, true, null);
    }

    /**
     * 点击评论的 回复按钮
     *
     * @param position position
     * @param view     view
     */
    @SuppressWarnings("unused")
    private void onCommentItemClickReport(int position, View view) {
        CommentItem item = mAdapter.getItem(position);
        if (item == null) {
            KLog.w("comment item on position = " + position + " is null");
            return;
        }

        TextInputDialog.from(mContext)
                .setHint(String.format(getString(R.string.comment_pattern), item.getName()))
                .setTag(item)
                .setText((String) mTvInput.getTag())
                .show(this, REQ_CODE_COMMENT_REPEAT);
    }


    @SuppressWarnings("unused")
    private void onCommentItemClickPortrait(int position, View view) {
        CommentItem commentItem = mAdapter.getItem(position);
        if (commentItem == null) {
            KLog.d(TAG, "onCommentItemClickPortrait: commentItem is null return. ");
            return;
        }
        if (mPresenter != null) {
            mPresenter.jump2UserInfo(getContext(), commentItem.getUid());
        }
    }

    /**
     * 当评论列表被打开时回调该方法
     */
    @Override
    public void onMenuOpened() {
        if (mPresenter != null) {
            mPresenter.refreshCommentData(getMovie().getId(), 0, PAGE_SIZE);
        }
    }

    /**
     * 当评论列表被关闭时回调该方法
     */
    @Override
    public void onMenuClosed() {

    }

    @Override
    public void onMovieChange(CategoryMovieBean movie) {
        super.onMovieChange(movie);
        mAdapter.setComment(null);
        mTvInput.setTag(null);
        if (movie != null) {
            mTvCommentCount.setText(String.format(Locale.US, "( %d )", movie.getComments()));
        }
    }

    @Override
    public void refreshComment(CommentBean comment) {
        mTvCommentCount.setText(String.format(Locale.US, "( %d )", comment.getCommentsTotal()));
        mAdapter.setComment(comment);
        mViewList.scrollToPosition(0);

        if (mListener != null) {
            mListener.onCommentRefresh(comment);
        }
    }

    @Override
    public void addComment(CommentBean comment, boolean addAtHead) {
        CommentBean localComment = mAdapter.getComment();
        if (localComment == null) {
            mAdapter.setComment(comment);
        } else if (localComment.getMediaId() != comment.getMediaId()) {
            // should never happen.
            KLog.w(TAG, "评论与原评论 不是同一media 取消刷新");

        } else {
            if (addAtHead) {
                KLog.d(TAG, "addComment：添加到头部");
                localComment.setCommentsTotal(comment.getCommentsTotal());
                mTvCommentCount.setText(String.format(Locale.US, "( %d )", localComment.getCommentsTotal()));
                mAdapter.addAllData(0, comment.getList());
                mViewList.smoothScrollToPosition(0);

                if (mListener != null) {
                    mListener.onCommentRefreshLoadMore(mAdapter.getComment());
                }
            } else {
                KLog.d(TAG, "addComment：添加到尾部");
                localComment.setCommentsTotal(comment.getCommentsTotal());
                mTvCommentCount.setText(String.format(Locale.US, "( %d )", localComment.getCommentsTotal()));
                mAdapter.addAllData(comment.getList());

                if (mListener != null) {
                    mListener.onCommentRefreshLoadMore(mAdapter.getComment());
                }
            }
        }
    }

    @Override
    public void addCommentItem(CommentItem commentItem) {
        if (commentItem == null) {
            Log.e(TAG, "addCommentItem: commentItem is null.");
            return;
        }
        mTvInput.setTag(null);

        CommentBean commentBean = mAdapter.getComment();
        commentBean.setCommentsTotal(commentBean.getCommentsTotal() + 1);
        mTvCommentCount.setText(String.format(Locale.US, "( %d )", commentBean.getCommentsTotal()));
        mAdapter.addData(commentItem, 0);
        mLayoutManager.scrollToPosition(0);

        if (mListener != null) {
            mListener.onCommentItemAdd(mAdapter.getComment(), commentItem);
        }
    }

    @Override
    public void showLoading() {
        if (mProgress == null) {
            return;
        }
        mProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        if (mProgress == null) {
            return;
        }
        mProgress.setVisibility(View.GONE);
    }

    @Override
    public void showMsg(CharSequence text) {
        ToastUtils.showShort(text);
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        // 下拉
        refreshLayout.finishRefresh(80);

        CommentBean commentBean = mAdapter.getComment();
        if (commentBean == null || commentBean.getList() == null || commentBean.getList().size() <= 0) {
            // 没有评论数据，startCommentId = 0;
            if (mPresenter != null) {
                mPresenter.loadMoreCommentData(mMovieBean.getId(), 0, Api.Value.ActionType.FORWARD, PAGE_SIZE);
            }
        } else {
            // 有评论数据，加载更多 startCommentId = 最上id + 1;
            List<CommentItem> commentItems = commentBean.getList();
            CommentItem lastCommentItem = commentItems.get(0);
            if (mPresenter != null) {
                mPresenter.loadMoreCommentData(commentBean.getMediaId(), lastCommentItem.getId() + 1, Api.Value.ActionType.FORWARD, PAGE_SIZE);
            }
        }
    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        // 上拉
        refreshLayout.finishLoadMore(80);

        CommentBean commentBean = mAdapter.getComment();
        if (commentBean == null || commentBean.getList() == null || commentBean.getList().size() <= 0) {
            // 没有评论数据，startCommentId = 0;
            if (mPresenter != null) {
                mPresenter.loadMoreCommentData(mMovieBean.getId(), 0, Api.Value.ActionType.BACKWARD, PAGE_SIZE);
            }
        } else {
            // 有评论数据，加载更多 startCommentId = 最下面id - 1;
            List<CommentItem> commentItems = commentBean.getList();
            CommentItem lastCommentItem = commentItems.get(commentItems.size() - 1);
            if (mPresenter != null) {
                mPresenter.loadMoreCommentData(commentBean.getMediaId(), lastCommentItem.getId() - 1, Api.Value.ActionType.BACKWARD, PAGE_SIZE);
            }
        }
    }

    public void requestRefreshComment(long startCommentId) {
        if (mPresenter != null) {
            mPresenter.firstRefreshCommentData(mMovieBean.getId(), startCommentId, PAGE_SIZE);
        }
    }

    public CommentFragmentListener getListener() {
        return mListener;
    }

    public void setListener(CommentFragmentListener listener) {
        mListener = listener;
    }

    @SuppressWarnings("UnnecessaryInterfaceModifier")
    public static interface CommentFragmentListener {
        public void onCommentRefresh(CommentBean comment);

        public void onCommentRefreshLoadMore(CommentBean comment);

        public void onCommentItemAdd(CommentBean comment, CommentItem commentItem);
    }
}
