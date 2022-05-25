package com.xianghe.ivy.model;

import java.io.Serializable;

public class BackGroundAllBean implements Serializable {


    private static final long serialVersionUID = 9204977789581363431L;
    /**
     * id : 1
     * img : http://ivystorage.oss-cn-shenzhen.aliyuncs.com/images/bg/bg1@2x.png
     */

    private String id;
    private String img;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
