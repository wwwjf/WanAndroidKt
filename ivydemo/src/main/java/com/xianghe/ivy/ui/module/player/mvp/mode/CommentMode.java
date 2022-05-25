package com.xianghe.ivy.ui.module.player.mvp.mode;

import com.google.gson.JsonElement;
import com.xianghe.ivy.constant.Api;
import com.xianghe.ivy.http.request.NetworkRequest;
import com.xianghe.ivy.model.CommentItem;
import com.xianghe.ivy.model.response.BaseResponse;
import com.xianghe.ivy.model.response.CommentListResponse;
import com.xianghe.ivy.model.response.MediaAddCommentResponse;
import com.xianghe.ivy.network.GsonHelper;
import com.xianghe.ivy.ui.module.player.mvp.contact.CommentContact;
import com.xianghe.ivy.utils.KLog;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

public class CommentMode implements CommentContact.IMode {
    private static final String TAG = "CommentMode";

    @Override
    public Observable<CommentListResponse> loadComment(long mediaId, long startCommentId, int actionType, int pageSize) {
        Map<String, Object> params = new HashMap<>();
        params.put(Api.Key.MEDIA_ID, mediaId);
        params.put(Api.Key.START_COMMENT_ID, startCommentId);
        params.put(Api.Key.ACTION_TYPE, actionType);
        params.put(Api.Key.PAGE_SIZE, pageSize);

        return NetworkRequest.INSTANCE.postMap(Api.Route.Index.MEDIA_COMMENTS_LIST, params)
                .map(new Function<JsonElement, CommentListResponse>() {
                    @Override
                    public CommentListResponse apply(JsonElement jsonElement) throws Exception {
                        return GsonHelper.getsGson().fromJson(jsonElement, CommentListResponse.class);
                    }
                });
    }


    @Override
    public Observable<MediaAddCommentResponse> addComment(long mediaId, int type, long commentId, CommentItem comment) {
        Map<String, Object> params = new HashMap<>();
        params.put(Api.Key.MEDIA_ID, mediaId);
        params.put(Api.Key.CONTENT, comment.getContent());
        params.put(Api.Key.TYPE, type);
        params.put(Api.Key.MEDIA_COMMENTS_ID, commentId);

        return NetworkRequest.INSTANCE.postMap(Api.Route.Index.MEDIA_ADD_COMMENTS, params)
                .map(new Function<JsonElement, MediaAddCommentResponse>() {
                    @Override
                    public MediaAddCommentResponse apply(JsonElement jsonElement) throws Exception {
                        return GsonHelper.getsGson().fromJson(jsonElement, MediaAddCommentResponse.class);
                    }
                });
    }

    @Override
    public Observable<BaseResponse> deleteCommentItem(long commentId) {
        Map<String, Object> params = new HashMap<>();
        params.put(Api.Key.COMMENT_ID, commentId);

        return NetworkRequest.INSTANCE.postMap(Api.Route.Index.MEDIA_DEL_COMMENT, params)
                .map(new Function<JsonElement, BaseResponse>() {
                    @Override
                    public BaseResponse apply(JsonElement jsonElement) throws Exception {
                        return GsonHelper.getsGson().fromJson(jsonElement, BaseResponse.class);
                    }
                });
    }
}
