package com.xianghe.ivy.model;

import com.google.gson.annotations.SerializedName;

public class MediaDetailBean {
    /**
     * comments_total : 7
     * title : 海鸥
     * avatar : http://thirdwx.qlogo.cn/mmopen/ajNVdqHZLLC7DzvQy2G9ShM1kicx2RsIMgVIlL91UVvxwYQUZO1m2G5re6f02LCEiaLzwuOxIs6qgKgfLeve9Brw/132
     * username : 不奢求
     * cover : http://ivystorage.oss-cn-shenzhen.aliyuncs.com/images/2018-07-31/201807311248455b443aad51b8d.png?security-token=CAISlQJ1q6Ft5B2yfSjIr4j7eM%2FVlZllj%2FG%2BYEncsUcPT%2FpioovytTz2IHBNdHduAuwWtf80mW1X6foYlqJ4T55IQ1Dza8J148zJFd5K%2Bs6T1fau5Jko1beXewHKeSOZsebWZ%2BLmNqS%2FHt6md1HDkAJq3LL%2Bbk%2FMdle5MJqP%2B%2FEFA9MMRVv6F3kkYu1bPQx%2FssQXGGLMPPK2SH7Qj3HXEVBjt3gb6wZ24r%2FtxdaHuFiMzg%2B46JdM%2BN%2BgesD7P5E3bMsuCofk5oEsKPqdihw3wgNR6aJ7gJZD%2FTr6pdyHCzFTmU7WarqKrIYzfFQiPfVnRPEY9uKPnPl5q%2FHVkJ%2Fs1xFOMOdaXiLSXom8x9HeH%2BekJluivi134JemGoABFKAlcja3t6OHYLphkUKR8%2BNGMtUpPkJgZrtlC4v7aLYhY6Vty84Q3WQ9eLlu%2BI4B2HrXuQ4gZqUw87u2ab%2F%2FLxYFOVZoE8%2BwfRykRFW2miUJYGrUIhBvOb5oYA%2BpQHIx0eRgQUrUBHNO%2FOkt54%2B824Gmp8pBdpyF%2FcvEkrMTAZc%3D&OSSAccessKeyId=STS.NKN3uaxFD83UbomUGZCuNMNYW&Expires=1542168856&Signature=ciyeBaiNk3KQNyRW9MedTFCNJGk%3D
     * uid : 8272549
     * created_at : 2018-07-31 16:21:10
     * content : null
     * type : null
     * media : http://ivystorage.oss-cn-shenzhen.aliyuncs.com/videos/2018-07-31/201807311248455b443aad51b8d.mp4?security-token=CAISlQJ1q6Ft5B2yfSjIr4j7eM%2FVlZllj%2FG%2BYEncsUcPT%2FpioovytTz2IHBNdHduAuwWtf80mW1X6foYlqJ4T55IQ1Dza8J148zJFd5K%2Bs6T1fau5Jko1beXewHKeSOZsebWZ%2BLmNqS%2FHt6md1HDkAJq3LL%2Bbk%2FMdle5MJqP%2B%2FEFA9MMRVv6F3kkYu1bPQx%2FssQXGGLMPPK2SH7Qj3HXEVBjt3gb6wZ24r%2FtxdaHuFiMzg%2B46JdM%2BN%2BgesD7P5E3bMsuCofk5oEsKPqdihw3wgNR6aJ7gJZD%2FTr6pdyHCzFTmU7WarqKrIYzfFQiPfVnRPEY9uKPnPl5q%2FHVkJ%2Fs1xFOMOdaXiLSXom8x9HeH%2BekJluivi134JemGoABFKAlcja3t6OHYLphkUKR8%2BNGMtUpPkJgZrtlC4v7aLYhY6Vty84Q3WQ9eLlu%2BI4B2HrXuQ4gZqUw87u2ab%2F%2FLxYFOVZoE8%2BwfRykRFW2miUJYGrUIhBvOb5oYA%2BpQHIx0eRgQUrUBHNO%2FOkt54%2B824Gmp8pBdpyF%2FcvEkrMTAZc%3D&OSSAccessKeyId=STS.NKN3uaxFD83UbomUGZCuNMNYW&Expires=1542168856&Signature=sxAHzLLFm4LQu57FYl%2FyM%2FyOpFY%3D
     * is_author_star : 1
     * address : null
     * video_length : 01:19
     * comments : {"next_page":1,"comments_total":"7","list":[]}
     * is_media_star : 0
     * star : 0
     */

    @SerializedName("comments_total")
    private String mCommentsTotal;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("avatar")
    private String mAvatar;
    @SerializedName("username")
    private String mUsername;
    @SerializedName("cover")
    private String mCover;
    @SerializedName("uid")
    private int mUid;
    @SerializedName("created_at")
    private String mCreatedAt;
    @SerializedName("content")
    private Object mContent;
    @SerializedName("type")
    private Object mType;
    @SerializedName("media")
    private String mMedia;
    @SerializedName(value = "is_author_star",alternate = {"is_like"})
    private int mIsAuthorStar;
    @SerializedName("address")
    private String mAddress;
    @SerializedName("video_length")
    private String mVideoLength;
    @SerializedName("comments")
    private CommentBean mComments;
    @SerializedName("is_media_star")
    private int mIsMediaStar;
    @SerializedName("star")
    private int mStar;
    @SerializedName("is_attention")
    private int isAttention;
    @SerializedName("is_friend")
    private int isFriend;
    /**
     * 分享数量
     */
    private int share;

    private int like;

    public int getIsFriend() {
        return isFriend;
    }

    public void setIsFriend(int isFriend) {
        this.isFriend = isFriend;
    }

    public int getIsAttention() {
        return isAttention;
    }

    public void setIsAttention(int isAttention) {
        this.isAttention = isAttention;
    }

    public String getCommentsTotal() {
        return mCommentsTotal;
    }

    public void setCommentsTotal(String commentsTotal) {
        mCommentsTotal = commentsTotal;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getAvatar() {
        return mAvatar;
    }

    public void setAvatar(String avatar) {
        mAvatar = avatar;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public String getCover() {
        return mCover;
    }

    public void setCover(String cover) {
        mCover = cover;
    }

    public int getUid() {
        return mUid;
    }

    public void setUid(int uid) {
        mUid = uid;
    }

    public String getCreatedAt() {
        return mCreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        mCreatedAt = createdAt;
    }

    public Object getContent() {
        return mContent;
    }

    public void setContent(Object content) {
        mContent = content;
    }

    public Object getType() {
        return mType;
    }

    public void setType(Object type) {
        mType = type;
    }

    public String getMedia() {
        return mMedia;
    }

    public void setMedia(String media) {
        mMedia = media;
    }

    public int getIsAuthorStar() {
        return mIsAuthorStar;
    }

    public void setIsAuthorStar(int isAuthorStar) {
        mIsAuthorStar = isAuthorStar;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public String getVideoLength() {
        return mVideoLength;
    }

    public void setVideoLength(String videoLength) {
        mVideoLength = videoLength;
    }

    public CommentBean getComments() {
        return mComments;
    }

    public void setComments(CommentBean comments) {
        mComments = comments;
    }

    public int getIsMediaStar() {
        return mIsMediaStar;
    }

    public void setIsMediaStar(int isMediaStar) {
        mIsMediaStar = isMediaStar;
    }

    public int getStar() {
        return mStar;
    }

    public void setStar(int star) {
        mStar = star;
    }

    public int getShare() {
        return share;
    }

    public void setShare(int share) {
        this.share = share;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }
}