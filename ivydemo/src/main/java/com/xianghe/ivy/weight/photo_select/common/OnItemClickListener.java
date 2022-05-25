package com.xianghe.ivy.weight.photo_select.common;


import com.xianghe.ivy.weight.photo_select.bean.Image;

/**
 * @author yuyh.
 * @date 2016/8/5.
 */
public interface OnItemClickListener {

    int onCheckedClick(int position, Image image);

    void onImageClick(int position, Image image);
}
