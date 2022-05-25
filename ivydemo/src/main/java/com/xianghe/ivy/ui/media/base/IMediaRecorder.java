package com.xianghe.ivy.ui.media.base;

import java.io.IOException;

/**
 * @Author: ycl
 * @Date: 2018/10/25 16:06
 * @Desc:
 */
public interface IMediaRecorder {

    public void startRecord() throws IOException;


    public void stopRecord();


    public void release();



}
