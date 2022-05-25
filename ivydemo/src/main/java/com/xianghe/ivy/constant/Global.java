package com.xianghe.ivy.constant;

/**
 * @Author: ycl
 * @Date: 2018/10/30 18:24
 * @Desc:
 */
public class Global {
    public static final String PARAMS = "params";
    public static final String PARAMS1 = "params1";
    public static final String PARAMS2 = "params2";
    public static final String PARAMS3 = "params3";
    public static final String PARAMS4 = "params4";
    public static final String PARAMS5 = "params5";

    public static final String USER_RELATIONSHIP_CHANGE = "com.xianghe.ivy.userRelationShipChange";

    public static final int CASE_UNKOWN = 0;    // 未知
    public static final int CASE_FOLLOW = 1;    // 关注用户
    public static final int CASE_UNFOLLOW = 2;  // 取消关注用户
    public static final int CASE_ADD_FRIEND = 3;    // 增加好友
    public static final int CASE_DEL_FRIEND = 4;    // 删除好友
    //public static final int CASE_BLACK_LIST = 5;    // 加入黑名单

    public static final String EXTRA_KEY_UID = "extra_key_uid";
    public static final String EXTRA_KEY_CASE = "extra_key_case";

    /**
     * 个人资料编辑页缓存key
     */
    public static final String cache_key_userProfile = "userProfile_";

    /**
     * 个人主页信息缓存key
     */
    public static final String cache_key_userBeanResponse = "userBeanResponse_uid_";

    /**
     * 个人影片信息缓存（保存第一页数据）
     */
    public static final String cache_key_userMovieResponse = "userMovieResponse_uid_";

}
