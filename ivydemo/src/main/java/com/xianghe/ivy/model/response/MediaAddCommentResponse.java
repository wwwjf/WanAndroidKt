package com.xianghe.ivy.model.response;

import com.google.gson.annotations.SerializedName;
import com.xianghe.ivy.model.CommentItem;

public class MediaAddCommentResponse extends BaseResponse<MediaAddCommentResponse.DataBean> {


    public static class DataBean {
        /**
         * id : 1425
         * uid : 8272648
         * username : 用户5809421306859
         * avatar :
         * content : vg
         * replay_count : 0
         * created_at : 2018-11-20 13:46:22
         * reply_uid : 0
         * reply_name :
         */

        @SerializedName("id")
        private int mId;
        @SerializedName("uid")
        private long mUid;
        @SerializedName("username")
        private String mUsername;
        @SerializedName("avatar")
        private String mAvatar;
        @SerializedName("content")
        private String mContent;
        @SerializedName("replay_count")
        private int mReplayCount;
        @SerializedName("created_at")
        private String mCreatedAt;
        @SerializedName("reply_uid")
        private int mReplyUid;
        @SerializedName("reply_name")
        private String mReplyName;

        public CommentItem cover2CommentItem() {
            CommentItem item = new CommentItem();
            item.setId(getId());
            item.setUid(getUid());
            item.setName(getUsername());
            item.setAvatar(getAvatar());
            item.setContent(getContent());
            item.setReplyUid(getReplyUid());
            item.setCreatedAt(getCreatedAt());
            item.setReplyName(getReplyName());
            return item;
        }

        public int getId() {
            return mId;
        }

        public void setId(int id) {
            mId = id;
        }

        public long getUid() {
            return mUid;
        }

        public void setUid(long uid) {
            mUid = uid;
        }

        public String getUsername() {
            return mUsername;
        }

        public void setUsername(String username) {
            mUsername = username;
        }

        public String getAvatar() {
            return mAvatar;
        }

        public void setAvatar(String avatar) {
            mAvatar = avatar;
        }

        public String getContent() {
            return mContent;
        }

        public void setContent(String content) {
            mContent = content;
        }

        public int getReplayCount() {
            return mReplayCount;
        }

        public void setReplayCount(int replayCount) {
            mReplayCount = replayCount;
        }

        public String getCreatedAt() {
            return mCreatedAt;
        }

        public void setCreatedAt(String createdAt) {
            mCreatedAt = createdAt;
        }

        public int getReplyUid() {
            return mReplyUid;
        }

        public void setReplyUid(int replyUid) {
            mReplyUid = replyUid;
        }

        public String getReplyName() {
            return mReplyName;
        }

        public void setReplyName(String replyName) {
            mReplyName = replyName;
        }
    }
}
