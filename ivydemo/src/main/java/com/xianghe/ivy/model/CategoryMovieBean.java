package com.xianghe.ivy.model;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.xianghe.ivy.app.IvyConstants;
import com.xianghe.ivy.manager.UserInfoManager;
import com.xianghe.ivy.utils.IvyUtils;

import java.io.Serializable;

/**
 * 指定分类数据列表实体
 */
public class CategoryMovieBean implements Movie, Serializable, Cloneable {
    private static final String TAG = "CategoryMovieBean";

    private static final long serialVersionUID = 1147056979522438680L;

    public static final int DISLIKE = 0;    // 不喜欢
    public static final int LIKE = 1;       // 喜欢

    public static final int START_CANCEL = 0;   // 取消点赞
    public static final int START = 1;          // 点赞

    public static final int UN_FRIEND = 0;  // 不是亲友
    public static final int IS_FRIEND = 1;  // 亲友

    public static final int UN_ATTENTION = 0;   //  未关注
    public static final int IS_ATTENTION = 1;   //  关注

    public static final int IS_FORWARD = 1;     //  转发
    /**
     * id : 34
     * title : 活了几十年，这样的场面还是第一次见
     * media : http://ivystorage.oss-cn-shenzhen.aliyuncs.com/videos/2018-07-31/20180731vu46f1duyztzr67zvzi.mp4
     * comments : 1
     * star : 1
     * like : 0
     * share:0
     * is_like : 0
     * uid : 8272548
     * created_at : 2018-07-31 12:31:22
     * address :
     * cover : http://ivystorage.oss-cn-shenzhen.aliyuncs.com/images/2018-07-31/20180731vu46f1duyztzr67zvzi.png
     * video_length : 02:24
     * username : yellow
     * avatar : http://thirdwx.qlogo.cn/mmopen/Q3auHgzwzM4zmH5H7x4BKFWEzO5tAIyicDPiaV77O2hvIF0OgfuX5icsBzp1TLmh5bEoJc4rOVKvN2O9B4pnvF8FpiaAYEjsulS1Uic5duaNKhQo/132
     * mTimestamp : 1533011482
     * is_star : 0
     */

    @SerializedName("id")
    private long mId;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("media")
    private String mMedia;
    @SerializedName("comments")
    private int mComments;
    @SerializedName("star")
    private int mStar;
    @SerializedName("like")
    private int mLike;
    @SerializedName("is_like")
    private int mIsLike;
    @SerializedName("uid")
    private long mUid;
    @SerializedName("created_at")
    private String mCreatedAt;
    @SerializedName("address")
    private String mAddress;
    @SerializedName("cover")
    private String mCover;
    @SerializedName("video_length")
    private String mVideoLength;
    @SerializedName("share")
    private int mShare;
    @SerializedName("avatar")
    private String mAvatar;
    @SerializedName("protected_avatar")
    private String mProtectedAvatar;
    @SerializedName("protected_media")
    private String mProtectedMedia;
    @SerializedName("username")
    private String mUsername;
    @SerializedName("is_star")
    private int mIsStar;
    @SerializedName("is_friend")
    private int mIsFriend;      // 是否亲友：0 否  1 是
    @SerializedName("is_attention")
    private int mIsAttention;   // 是否关注：0 否  1 是
    @SerializedName("is_my_homepage")
    private int mIsMyHomepage;
    @SerializedName("is_forward")
    private int mIsForward;         // 是否转发：0 否；1是
    @SerializedName("forward_name")
    private String mForwardName;
    @SerializedName("forward_content")
    private String mForwardContent;
    /**
     * 公开发布：0 私密收藏：1 亲友圈发布：2
     */
    @SerializedName("is_private")
    private long mIsPrivate;
    @SerializedName("is_participate_activity")
    private int mIsParticipateActivity;

    private long startTime;

    @Expose(serialize = false, deserialize = false)
    private long lastTimeMillis; // 记录上次item刷新时间

    /**
     * 是否是录屏视频 0-不是（默认），1-是
     */
    @SerializedName("is_screen_recording")
    private int mIsScreenRecord;

    public long getLastTimeMillis() {
        return lastTimeMillis;
    }

    public void setLastTimeMillis(long lastTimeMillis) {
        this.lastTimeMillis = lastTimeMillis;
    }

    @Override
    public long mediaId() {
        return getId();
    }

    @Override
    public String getCoverUrl() {
        return getCover();
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getMedia() {
        return mMedia;
    }

    public void setMedia(String media) {
        mMedia = media;
    }

    public int getComments() {
        return mComments;
    }

    public void setComments(int comments) {
        mComments = comments;
    }

    public int getStar() {
        return mStar;
    }

    public void setStar(int star) {
        mStar = star;
    }

    public int getLike() {
        return mLike;
    }

    public void setLike(int like) {
        mLike = like;
    }

    public int getIsLike() {
        return mIsLike;
    }

    public void setIsLike(int isLike) {
        mIsLike = isLike;
    }

    public long getUid() {
        return mUid;
    }

    public void setUid(long uid) {
        mUid = uid;
    }

    public String getCreatedAt() {
        return mCreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        mCreatedAt = createdAt;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public String getCover() {
        return mCover;
    }

    public void setCover(String cover) {
        mCover = cover;
    }

    public String getVideoLength() {
        return mVideoLength;
    }

    public void setVideoLength(String videoLength) {
        mVideoLength = videoLength;
    }

    public int getShare() {
        return mShare;
    }

    public void setShare(int share) {
        mShare = share;
    }

    public String getAvatar() {
        return mAvatar;
    }

    public void setAvatar(String avatar) {
        mAvatar = avatar;
    }

    public String getProtectedAvatar() {
        return mProtectedAvatar;
    }

    public void setProtectedAvatar(String protectedAvatar) {
        mProtectedAvatar = protectedAvatar;
    }

    public String getProtectedMedia() {
        return mProtectedMedia;
    }

    public void setProtectedMedia(String protectedMedia) {
        mProtectedMedia = protectedMedia;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public int getIsStar() {
        return mIsStar;
    }

    public void setIsStar(int isStar) {
        mIsStar = isStar;
    }

    public long getIsPrivate() {
        return mIsPrivate;
    }

    public void setIsPrivate(long isPrivate) {
        this.mIsPrivate = isPrivate;
    }

    public int getIsMyHomepage() {
        return mIsMyHomepage;
    }

    public void setIsMyHomepage(int isMyHomepage) {
        this.mIsMyHomepage = isMyHomepage;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public int getIsFriend() {
        return mIsFriend;
    }

    public void setIsFriend(int isFriend) {
        mIsFriend = isFriend;
    }

    public int getIsAttention() {
        return mIsAttention;
    }

    public void setIsAttention(int isAttention) {
        mIsAttention = isAttention;
    }

    public int getIsForward() {
        return mIsForward;
    }

    public void setIsForward(int isForward) {
        mIsForward = isForward;
    }

    public String getForwardName() {
        return mForwardName;
    }

    public void setForwardName(String forwardName) {
        mForwardName = forwardName;
    }

    public String getForwardContent() {
        return mForwardContent;
    }

    public void setForwardContent(String forwardContent) {
        mForwardContent = forwardContent;
    }

    public int getIsParticipateActivity() {
        return mIsParticipateActivity;
    }

    public void setIsParticipateActivity(int isParticipateActivity) {
        mIsParticipateActivity = isParticipateActivity;
    }

    public String getMediaOssUrl() {
        return getMediaOssUrl(false);
    }

    public int getIsScreenRecord() {
        return mIsScreenRecord;
    }

    public void setIsScreenRecord(int isScreenRecord) {
        mIsScreenRecord = isScreenRecord;
    }

    /**
     * 目前发现使用ip地址的url 下载任务总是失败，所有下载任务的时候不要开启httpdns
     *
     * @param withHttpDns true 使用 httpdns， false 不适用 httpdns
     * @return
     */
    public String getMediaOssUrl(boolean withHttpDns) {
        String result;
        try {
            //判断服务器是否有开启cdn
            int enable = UserInfoManager.getInt(IvyConstants.CDN_ENABLE, 0);
            if (enable == 1) {
                String time = ((System.currentTimeMillis() + UserInfoManager.getInt(IvyConstants.EXPIRES_IN, 3600) + 3600) / 1000) + "";
                String url = UserInfoManager.getString(IvyConstants.CDN_URL, "");
                String key = UserInfoManager.getString(IvyConstants.CDN_MASK, "");
                String protectedMedia = "";
                if (TextUtils.isEmpty(getProtectedMedia())) {
                    protectedMedia = "/" + getMediaUrl(getMedia());
                } else {
                    protectedMedia = "/" + getUrl(getProtectedMedia());
                }
                String md5String = IvyUtils.md5(protectedMedia + "-" + time + "-0-0-" + key);
                String auth_key = "auth_key=" + time + "-0-0-" + md5String;

                String ip = null;

                if (withHttpDns) {
                    try {
                        //URL sourceUrl = new URL("http://" + url);
//                        ip = IvyApp.sHttpDns.getIpByHostAsync(url);
                        //KLog.e(TAG, "ip = " + ip);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (TextUtils.isEmpty(ip)) {
                    result = "http://" + url + protectedMedia + "?" + auth_key;
                } else {
                    result = "http://" + ip + protectedMedia + "?" + auth_key;
                }
            } else {
                result = "";
                if (result == null) {
                    result = getMedia();
                }
            }
        } catch (Exception e) {
            result = getMedia();
        }

        return result;
    }

    private String getMediaUrl(String url) {
        //截掉头尾
        int index = url.indexOf(".com/");
        int endIndex = 0;
        if (url.contains("?")) {
            endIndex = url.indexOf("?");
        }
        if (index != -1 && endIndex != 0) {
            return url.substring(index + 5, endIndex);
        }
        return "";
    }

    private String getUrl(String url) {
        //判断是否有.com/
        int index = url.indexOf(".com/");
        if (index != -1) {
            return url.substring(index + 5);
        }
        return "";
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean equals = false;
        if (obj instanceof CategoryMovieBean) {
            CategoryMovieBean bean = (CategoryMovieBean) obj;
            equals = bean.getId() == this.getId() && TextUtils.equals(bean.getMediaOssUrl(), this.getMediaOssUrl());
        }
        return equals;
    }

    @Override
    public CategoryMovieBean clone() {
        try {
            return (CategoryMovieBean) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String toString() {
        return "{" + "title = " + mTitle + "\t\t" + "mIsAttention = " + mIsAttention + '\t' + "mIsFriend = " + mIsFriend + '}';
    }
}
