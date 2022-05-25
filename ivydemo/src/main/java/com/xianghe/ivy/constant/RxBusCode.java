package com.xianghe.ivy.constant;

/**
 * @Author: ycl
 * @Date: 2018/11/7 14:26
 * @Desc:
 */
public class RxBusCode {

    public static final int ACT_UNDEFINE = 0x0000;

    public static final int ACT_FINISH_VIDEO_RECORD = 0x0001;

    public static final int ACT_FINISH_VIDEO_EDIT = 0x0002;

    public static final int ACT_VIDEO_EDIT_DIALOG_DISMISS = 0x0003;
    /**
     * 微信登录成功
     */
    public static final int ACT_WECHAT_LOGIN_SUCCESS = 0x0004;
    /**
     * 退出登录
     */
    public static final int ACT_USER_LOGOUT = 0x0005;
    /**
     * 密码登录、验证码登录、注册后自动登录、忘记密码后自动登录
     */
    public static final int ACT_USER_LOGIN = 0x0006;

    //用户被挤下去的通知
    public static final int ACT_USER_PUSH_LOGOUT = 0x0007;

    //亲友通过
    public static final int ACT_USER_PUSH_RELATIVE = 0x0020;

    //关注列表首页
    public static final int ACT_FOLLOW = 0x0008;

    //亲友圈首页
    public static final int ACT_QYQLIST = 0x0009;

    //评论回复
    public static final int ACT_COMMENT = 0x000A;

    //我的亲友圈
    public static final int ACT_QYQAdd = 0x000B;

    //签名成功
    public static final int HASSIGN = 0x000C;

    //股东信息修改成功
    public static final int IDENTIFICATION_PERSON_INFO = 0x000D;

    //股东信息修改成功
    public static final int CHANGE_PERSON_INFO = 0x000E;
    /**
     * 我的亲友列表数量变化、用户资料关注数、获赞数、收藏数变化
     */
    public static final int ACT_RELATIVE_FRIEND = 0x000F;

    /**
     * 短信发送成功
     */
    public static final int ACT_SEND_MESSAGE = 0x0010;

    /**
     * 影片发布成功
     */
    public static final int ACT_USER_MOVIE_PUBLISH_SUCCESS = 0x0011;

    public static final int ACT_USER_UNLOGIN_VIEW = 0x0012;
    /**
     * 左边个人信息布局显示、隐藏
     */
    public static final int ACT_USER_INFO_CHANGE = 0x0013;

    public static final int MOVIE_INFO_UP_DATE = 0x0014;

    /**
     * 删除个人影片
     */
    public static final int ACT_MOVIE_DELETE = 0x0015;

    // 视频编辑页面返回，刷新,删除,置位 视频录制页面数据
    public static final int ACT_VIDEO_RECORD_REFRESH_BY_EDIT = 0x0016;

    /**
     * 用户黑名单信息变化
     */
    public static final int ACT_BLACKLIST_CHANGE = 0x0017;

    /**
     * 点击搜索
     */
    public static final int ACT_MAIN_SEARCH = 0x0018;


    /**
     * 搜索中的关注用户
     */
    public static final int ACT_MAIN_SEARCH_FOLLOW = 0x0019;

    /**
     * 首页个人头像更新
     */
    public static final int ACT_MAIN_USER_PROFILE_UPDATE_AVATAR = 0x0020;

    /**
     * 首页个人昵称更新 全部变量
     */
    public static final int ACT_MAIN_USER_PROFILE_UPDATE_NICKNAME = 0x0021;


    /**
     * 保存到草稿箱的回调
     */
    public static final int ACT_VIDEO_PUSH_REFRESH_CACHE = 0X0022;


    /**
     * 保存到草稿箱返回回调
     */
    public static final int ACT_VIDEO_PUSH_REFRESH_BACK = 0X0023;

    /*裁剪*/
    public static final int ACT_VIDEO_PRE_REFRESH_CLIP = 0X0024;

    public static final int ACT_VIDEO_EDIT_CACHE = 0X0025;
    //获取用户手机微信信息(openID, 昵称)
    public static final int GET_WECHAT_USER_INFO = 0X0026;

    //会员支付成功后刷新相关数据
    public static final int PAY_SUCCESS_REFRESH_DATA = 0X0027;
}

