package com.xianghe.ivy.model;

public class MainSearchSectionBean {

    private int itemType;
    private ListUserBean mUserBean;
    private ListMediaBean mMediaBean;

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public ListUserBean getUserBean() {
        return mUserBean;
    }

    public void setUserBean(ListUserBean userBean) {
        mUserBean = userBean;
    }

    public ListMediaBean getMediaBean() {
        return mMediaBean;
    }

    public void setMediaBean(ListMediaBean mediaBean) {
        mMediaBean = mediaBean;
    }
}
