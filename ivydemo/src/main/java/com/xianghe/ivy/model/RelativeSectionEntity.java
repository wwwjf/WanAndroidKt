package com.xianghe.ivy.model;


import com.xianghe.ivy.adapter.adapter.entity.SectionEntity;

public class RelativeSectionEntity extends SectionEntity<FriendBean> {

    public RelativeSectionEntity(boolean isHeader, String header) {
        super(isHeader, header);
    }

    public RelativeSectionEntity(FriendBean friendBean) {
        super(friendBean);
    }
}
