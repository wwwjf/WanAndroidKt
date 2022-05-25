package com.xianghe.ivy.ui.module.player.mvp.presenter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.xianghe.ivy.R;
import com.xianghe.ivy.constant.Api;
import com.xianghe.ivy.model.CommentBean;
import com.xianghe.ivy.model.CommentItem;
import com.xianghe.ivy.model.response.BaseResponse;
import com.xianghe.ivy.model.response.CommentListResponse;
import com.xianghe.ivy.model.response.MediaAddCommentResponse;
import com.xianghe.ivy.mvp.BasePresenter;
import com.xianghe.ivy.ui.module.player.mvp.contact.CommentContact;
import com.xianghe.ivy.ui.module.player.mvp.mode.CommentMode;
import com.xianghe.ivy.ui.module.user.UserActivity;
import com.xianghe.ivy.utils.KLog;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class CommentPresenter extends BasePresenter<CommentContact.IView> implements CommentContact.IPresenter {
    private static final String TAG = "CommentPresenter";

    private Context mContext;
    private CommentContact.IMode mMode = new CommentMode();

    public CommentPresenter(Context context) {
        mContext = context;
    }

    /**
     * 该方法主要区分方法{@link  CommentContact.IPresenter#refreshCommentData},
     * 第一次加载数据发现第一条评论id和服务器返回的首条评论id不一致, 给用户友好的提示
     *
     * @param mediaId
     * @param startCommentId
     * @param pageSize
     */
    @Override
    public void firstRefreshCommentData(long mediaId, long startCommentId, int pageSize) {
        Log.i(TAG, "load comment data: mediaId = " + mediaId);
        view().showLoading();
        add(mMode.loadComment(mediaId, startCommentId, Api.Value.ActionType.BACKWARD, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<CommentListResponse>() {
                    @Override
                    public void accept(CommentListResponse response) throws Exception {
                        Log.i(TAG, "load comment success: " + response);
                        if (view() != null) {
                            view().hideLoading();
                            view().refreshComment(response.getData());

                            if (startCommentId > 0 && response != null && response.getStatus() == BaseResponse.Status.OK) {
                                CommentBean commentBean = response.getData();
                                if (commentBean != null && commentBean.getList() != null && commentBean.getList().size() > 0) {
                                    if (commentBean.getList().get(0).getId() != startCommentId) {
                                        // 首条id与请求不一致，说明评论已经被删了
                                        view().showMsg(mContext.getResources().getString(R.string.comment_is_delete));
                                    }
                                } else if (commentBean != null && commentBean.getList() == null || commentBean.getList().size() <= 0) {
                                    // 没有评论了，说明评论已经被删了
                                    view().showMsg(mContext.getResources().getString(R.string.comment_is_delete));
                                }
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.i(TAG, "load comment error: " + throwable);
                        if (view() != null) {
                            view().hideLoading();
                        }
                    }
                }));
    }

    @Override
    public void refreshCommentData(long mediaId, long startCommentId, int pageSize) {
        Log.i(TAG, "load comment data: mediaId = " + mediaId);
        view().showLoading();
        add(mMode.loadComment(mediaId, startCommentId, Api.Value.ActionType.BACKWARD, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<CommentListResponse>() {
                    @Override
                    public void accept(CommentListResponse response) throws Exception {
                        Log.i(TAG, "load comment success: " + response);
                        if (view() != null) {
                            view().hideLoading();
                            view().refreshComment(response.getData());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.i(TAG, "load comment error: " + throwable);
                        if (view() != null) {
                            view().hideLoading();
                        }
                    }
                }));
    }

    @Override
    public void loadMoreCommentData(long mediaId, long startCommentId, int actionType, int pageSize) {
        Log.i(TAG, "load comment data: mediaId = " + mediaId);
        view().showLoading();
        add(mMode.loadComment(mediaId, startCommentId, actionType, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<CommentListResponse>() {
                    @Override
                    public void accept(CommentListResponse response) throws Exception {
                        Log.i(TAG, "load comment success: " + response);
                        if (view() != null) {
                            view().hideLoading();
                            view().addComment(response.getData(), actionType == Api.Value.ActionType.FORWARD);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.i(TAG, "load comment error: " + throwable);
                        if (view() != null) {
                            view().hideLoading();
                        }
                    }
                }));
    }

    @Override
    public void addCommentItem(long mediaId, CommentItem comment) {
        if (comment == null) {
            KLog.w(TAG, "cancel add comment, that comment is null");
            return;
        }
        add(mMode.addComment(mediaId, CommentItem.ACTION_ADD, 0, comment)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MediaAddCommentResponse>() {
                    @Override
                    public void accept(MediaAddCommentResponse response) throws Exception {
                        if (view() != null) {
                            if (response.getStatus() == BaseResponse.Status.OK) {
                                MediaAddCommentResponse.DataBean data = response.getData(); // 这行垃圾代码，后台的锅app已上线，username 和 name 字段无法做兼容了.
                                if (data == null) {
                                    return;
                                }

                                view().addCommentItem(data.cover2CommentItem());
                                view().showMsg(mContext.getString(R.string.comment_add_success));
                            } else {
                                view().showMsg(BaseResponse.infoCode2String(mContext, response.getInfoCode()));
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.i(TAG, "load comment error: " + throwable);
                        if (view() != null) {
                            view().showMsg(mContext.getString(R.string.common_net_error_please_try_again));
                        }
                    }
                }));
    }

    @Override
    public void repeatComment(long mediaId, long commentId, CommentItem comment) {
        if (comment == null) {
            KLog.w(TAG, "cancel repeat comment, that comment is null");
            return;
        }
        add(mMode.addComment(mediaId, CommentItem.ACTION_REPEAT, commentId, comment)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MediaAddCommentResponse>() {
                    @Override
                    public void accept(MediaAddCommentResponse response) throws Exception {
                        if (view() != null) {
                            if (response.getStatus() == BaseResponse.Status.OK) {
                                MediaAddCommentResponse.DataBean data = response.getData(); // 这行垃圾代码，后台的锅app已上线，username 和 name 字段无法做兼容了.
                                if (data == null) {
                                    return;
                                }
                                view().addCommentItem(data.cover2CommentItem());
                                view().showMsg(mContext.getString(R.string.comment_repeat_success));
                            } else {
                                view().showMsg(BaseResponse.infoCode2String(mContext, response.getInfoCode()));
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.i(TAG, "load comment error: " + throwable);
                        if (view() != null) {
                            view().showMsg(mContext.getString(R.string.common_net_error_please_try_again));
                        }
                    }
                }));
    }

    @Override
    public void deleteCommentItem(long mediaId, CommentItem item, long refreshCommentId) {
        if (item == null) {
            KLog.w(TAG, "cancel del comment, that item is null");
            return;
        }
        add(mMode.deleteCommentItem(item.getId()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BaseResponse>() {
                    @Override
                    public void accept(BaseResponse response) throws Exception {
                        Log.i(TAG, "delete comment response: " + response);
                        if (view() != null) {
                            if (response.getStatus() == BaseResponse.Status.OK) {
                                view().showMsg(BaseResponse.infoCode2String(mContext, response.getInfoCode()));
                                refreshCommentData(mediaId, refreshCommentId, 10);
                            } else {
                                view().showMsg(BaseResponse.infoCode2String(mContext, response.getInfoCode()));
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.i(TAG, "delete comment error: " + throwable);
                        if (view() != null) {
                            view().showMsg(mContext.getString(R.string.common_net_error_please_try_again));
                        }
                    }
                }));
    }

    @Override
    public void jump2UserInfo(Context context, long uid) {
        // 跳转我的
        Intent intent = new Intent(context, UserActivity.class);
        Uri uri = Uri.parse("ivy://UserActivity?uid=" + uid);
        intent.setData(uri);
        context.startActivity(intent);
    }
}
