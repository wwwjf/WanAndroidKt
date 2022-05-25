package com.xianghe.ivy.model.response;

import java.io.Serializable;

/**
 * 修改头像
 */
public class updateAvatarResponse implements Serializable {
    private static final long serialVersionUID = -6542572500786493422L;
    private String img_url;

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }
}
