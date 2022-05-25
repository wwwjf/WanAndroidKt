package com.xianghe.ivy.ui.module.record.local_media;

import com.xianghe.ivy.adapter.adapter.entity.MultiItemEntity;

public class LocalItemBean implements MultiItemEntity {

    private String filePath;

    private int type;

    public LocalItemBean(String filePath, int type) {
        this.filePath = filePath;
        this.type = type;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int getItemType() {
        return type;
    }
}
