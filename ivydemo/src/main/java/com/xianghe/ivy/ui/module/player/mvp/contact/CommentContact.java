package com.xianghe.ivy.ui.module.player.mvp.contact;

import android.content.Context;

import com.xianghe.ivy.model.CommentBean;
import com.xianghe.ivy.model.CommentItem;
import com.xianghe.ivy.model.response.BaseResponse;
import com.xianghe.ivy.model.response.CommentListResponse;
import com.xianghe.ivy.model.response.MediaAddCommentResponse;
import com.xianghe.ivy.mvp.IBaseConctact;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

public interface CommentContact extends IBaseConctact {


    public interface IView extends IBaseConctact.IBaseView {

        public void refreshComment(CommentBean comment);

        public void addComment(CommentBean data, boolean addAtHead);

        public void addCommentItem(CommentItem commentItem);

        public void showLoading();

        public void hideLoading();

        public void showMsg(CharSequence text);

    }


    public interface IPresenter extends IBaseConctact.IBasePresenter<IView> {
        public void firstRefreshCommentData(long mediaId, long startCommentId, int pageSize);

        public void refreshCommentData(long mediaId, long startCommentId, int pageSize);

        public void loadMoreCommentData(long mediaId, long startCommentId, int actionType, int pageSize);

        /**
         * 评论
         *
         * @param mediaId   mediaId
         * @param comment   comment
         */
        public void addCommentItem(long mediaId, CommentItem comment);

        /**
         * 回复评论
         *
         * @param mediaId   mediaId
         * @param commentId commentId
         * @param comment   comment
         */
        public void repeatComment(long mediaId, long commentId, CommentItem comment);

        /**
         * 删除评论
         * 评论删除成功, 需要刷新当前的评论列表 (注意：是刷新， 不管之前加载了多少数据, 删除评论后只保留屏幕上第一条看见数据的10条)
         *
         * @param mediaId          mediaId
         * @param delItem          CommentItem
         * @param refreshCommentId 删除完后刷新评论列表的起始id.
         */
        public void deleteCommentItem(long mediaId, CommentItem delItem, long refreshCommentId);

        /**
         * 跳转用户详情
         *
         * @param uid 跳转的用户uid
         */
        public void jump2UserInfo(Context context, long uid);
    }


    public interface IMode extends IBaseConctact.IBaseMode {

        public Observable<CommentListResponse> loadComment(long mediaId, long startCommentId, int actionType, int pageSize);

        public Observable<MediaAddCommentResponse> addComment(long mediaId, int type, long commentId, CommentItem comment);

        public Observable<BaseResponse> deleteCommentItem(long commentId);
    }
}
