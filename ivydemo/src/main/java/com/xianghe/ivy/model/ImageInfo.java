package com.xianghe.ivy.model;

import java.io.Serializable;

/**
 * Created by changle on 2019/3/15.
 */

public class ImageInfo implements Serializable {
    private String imagePath;
    private int imageRotate;

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getImageRotate() {
        return imageRotate;
    }

    public void setImageRotate(int imageRotate) {
        this.imageRotate = imageRotate;
    }
}
