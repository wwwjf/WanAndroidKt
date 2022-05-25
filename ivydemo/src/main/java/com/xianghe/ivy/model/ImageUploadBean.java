package com.xianghe.ivy.model;

import java.io.Serializable;

public class ImageUploadBean implements Serializable {
    private static final long serialVersionUID = 6847707654489341777L;

    private String img_url;

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }
}
