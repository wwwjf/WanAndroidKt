package com.xianghe.ivy.model.response;

import java.io.Serializable;

/**
 * 影片内容上传后返回id
 */
public class MediaUploadResponse implements Serializable {
    private static final long serialVersionUID = 4604642936493982884L;

    private int media_id;

    public int getMedia_id() {
        return media_id;
    }

    public void setMedia_id(int media_id) {
        this.media_id = media_id;
    }
}
