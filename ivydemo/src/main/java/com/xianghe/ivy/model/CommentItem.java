package com.xianghe.ivy.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class CommentItem implements Serializable {

    public static final long INVALID_ID = 0;    // 无效id

    public static final int TYPE_NORMAL = 0;       // 评论/回复
    public static final int TYPE_REPLY = 1;    // 转发

    public static final int ACTION_ADD = 1;       // 评论
    public static final int ACTION_REPEAT = 2;    // 回复

    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    /**
     * id : 389
     * content : 通过 v 吃
     * created_at : 2018-09-06 11:57:19
     * uid : 8272524
     * name : 几乎不回
     * avatar : http://ivy-test.oss-cn-shenzhen.aliyuncs.com/d520/201809/20180910ace5792fa86342fd4927575d493b9210.png
     * reply_id :
     * reply_name :
     */
    @SerializedName("id")
    private long mId;           // 视频评论id
    @SerializedName("content")
    private String mContent;    // 评论内容
    @SerializedName("created_at")
    private String mCreatedAt;  // 评论用户id
    @SerializedName("uid")
    private long mUid;          // 评论用户id
    @SerializedName("name")
    private String mName;       // 评论用户名
    @SerializedName("avatar")
    private String mAvatar;     // 评论用户头像
    @SerializedName("reply_uid")
    private long mReplyUid;     // 被回复对象（用户id）
    @SerializedName("reply_name")
    private String mReplyName;  // 被回复用户名
    @SerializedName("type")
    private int mType;  // 被回复用户名

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public String getCreatedAt() {
        return mCreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        mCreatedAt = createdAt;
    }

    public long getUid() {
        return mUid;
    }

    public void setUid(long uid) {
        mUid = uid;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getAvatar() {
        return mAvatar;
    }

    public void setAvatar(String avatar) {
        mAvatar = avatar;
    }

    public long getReplyUid() {
        return mReplyUid;
    }

    public void setReplyUid(long replyUid) {
        mReplyUid = replyUid;
    }

    public String getReplyName() {
        return mReplyName;
    }

    public void setReplyName(String replyName) {
        mReplyName = replyName;
    }

    /**
     * @return {@link #TYPE_NORMAL} 评论、回复;
     * {@link #TYPE_NORMAL} 转发
     */
    public int getType() {
        return mType;
    }

    /**
     * @param type {@link #TYPE_NORMAL} 评论、回复;
     *             {@link #TYPE_NORMAL} 转发
     */
    public void setType(int type) {
        mType = type;
    }

    public long getCreateAtInMillionSecond() {
        try {
            return DATE_FORMAT.parse(mCreatedAt).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}