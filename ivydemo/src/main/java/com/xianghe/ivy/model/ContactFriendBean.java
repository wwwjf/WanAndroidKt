package com.xianghe.ivy.model;

import com.xianghe.ivy.adapter.adapter.entity.MultiItemEntity;
import com.xianghe.ivy.model.response.UserVideoInvitationResponse;
import com.xianghe.ivy.weight.indexbar.suspension.ISuspensionInterface;

import java.io.Serializable;

public class ContactFriendBean implements Serializable, MultiItemEntity,ISuspensionInterface {
    private static final long serialVersionUID = -2827545478928883796L;

    /**
     * 视频聊天
     */
    public static final int ITEM_TYPE_VIDEO_CHAT = 1;

    public static final int ITEM_TYPE_VIDEO_CHAT_FOOTER = 11;

    /**
     * 可能认识的人
     */
    public static final int ITEM_TYPE_SOCIAL = 2;
    public static final int ITEM_TYPE_SOCIAL_FOOTER = 21;
    /**
     * 我的亲友
     */
    public static final int ITEM_TYPE_RELATIVE_FRIEND = 3;
    public static final int ITEM_TYPE_RELATIVE_FRIEND_FOOTER = 31;
    /**
     * 通讯录
     */
    public static final int ITEM_TYPE_CONTACT = 4;

    private int itemType;

    /**
     * 悬停显示的文字
     */
    private String suspensionTag;

    /**
     * 视频邀请
     */
    private UserVideoInvitationResponse.ListBean mInVitationBean;

    /**
     * 可能认识的人
     */
    private SocialBean.ListBean mSocialBean;

    /**
     * 我的亲友
     */
    private FriendBean mFriendBean;

    /**
     * 通讯录联系人
     */
    private ContactBean mContactBean;

    public UserVideoInvitationResponse.ListBean getInVitationBean() {
        return mInVitationBean;
    }

    public void setInVitationBean(UserVideoInvitationResponse.ListBean inVitationBean) {
        mInVitationBean = inVitationBean;
    }

    public SocialBean.ListBean getSocialBean() {
        return mSocialBean;
    }

    public void setSocialBean(SocialBean.ListBean socialBean) {
        mSocialBean = socialBean;
    }

    public FriendBean getFriendBean() {
        return mFriendBean;
    }

    public void setFriendBean(FriendBean friendBean) {
        mFriendBean = friendBean;
    }

    public ContactBean getContactBean() {
        return mContactBean;
    }

    public void setContactBean(ContactBean contactBean) {
        mContactBean = contactBean;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return this.itemType;
    }

    @Override
    public boolean isShowSuspension() {
        return true;
    }

    public void setSuspensionTag(String suspensionTag) {
        this.suspensionTag = suspensionTag;
    }

    @Override
    public String getSuspensionTag() {
        return suspensionTag;
    }
}
