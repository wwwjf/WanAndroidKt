package com.xianghe.ivy.ui.module.player;

/**
 * 方便测试增加的类, 以后可去掉
 */
public interface IExFunView {

    public enum Meuns {
        SHARE,      // 分享
        COMMENT;    // 评论
    }

    public boolean isMenuOpen();

    public void closeMenu();
}
