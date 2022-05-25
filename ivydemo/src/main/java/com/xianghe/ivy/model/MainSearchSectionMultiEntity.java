package com.xianghe.ivy.model;


import com.xianghe.ivy.adapter.adapter.entity.MultiItemEntity;
import com.xianghe.ivy.adapter.adapter.entity.SectionMultiEntity;

public class MainSearchSectionMultiEntity extends SectionMultiEntity<MainSearchSectionBean> implements MultiItemEntity {

    public static final int ITEM_TYPE_USER = 1;
    public static final int ITEM_TYPE_MOVIE = 2;
    private int mItemType;
    private boolean mIsMore;


    public MainSearchSectionMultiEntity(boolean isHeader, String header, boolean isMore,int itemType) {
        super(isHeader, header);
        mIsMore = isMore;
        mItemType = itemType;
    }

    public MainSearchSectionMultiEntity(int itemType) {
        super(false,null);
        mItemType = itemType;
    }

    @Override
    public void setT(MainSearchSectionBean mainSearchSectionBean) {
        super.setT(mainSearchSectionBean);
    }

    @Override
    public int getItemType() {
        return mItemType;
    }

    public boolean isMore() {
        return mIsMore;
    }
}
