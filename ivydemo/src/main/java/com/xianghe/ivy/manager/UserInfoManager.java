package com.xianghe.ivy.manager;

import com.blankj.utilcode.util.SPUtils;
import com.xianghe.ivy.app.IvyConstants;
import com.xianghe.ivy.app.em.EMManager;
import com.xianghe.ivy.model.GetTokenBean;
import com.xianghe.ivy.model.response.UserTokenBean;

/**
 * 用户信息
 */
public class UserInfoManager {
    public static final int INVALID_UID = -1;

    private static SPUtils spUtils = SPUtils.getInstance(IvyConstants.SP_USERINFO);

    public static boolean isLogin() {
        return spUtils.getBoolean(IvyConstants.IS_LOGIN);
    }

    public static String getTicket() {
        return spUtils.getString(IvyConstants.TICKET, "");
    }

    public static String getHX_userName() {
        return spUtils.getString(IvyConstants.HX_USERNAME, "");
    }

    public static String getHX_password() {
        return spUtils.getString(IvyConstants.HX_PASSWORD, "");
    }

    public static String getInvitationCode(){
        return spUtils.getString(IvyConstants.INVITATION_CODE, "");
    }
    public static void setInvitationCode(String code){
        saveString(IvyConstants.INVITATION_CODE,code);
    }


    public static String getInviterInvitationCode(){
        return spUtils.getString(IvyConstants.INVITER_INVITATION_CODE, "");
    }
    public static void setInviterInvitationCode(String code){
        saveString(IvyConstants.INVITER_INVITATION_CODE,code);
    }

    /**
     * 用户id
     *
     * @return 获取不到uid返回-1 {@link UserInfoManager#INVALID_UID};
     */
    public static long getUid() {
        return spUtils.getLong(IvyConstants.UID, INVALID_UID);
    }

    public static String getAvatar() {
        return spUtils.getString(IvyConstants.USER_AVATAR, "");
    }

    public static String getUserTags() {
        return spUtils.getString(IvyConstants.USER_TAGS, "");
    }

    public static String getString(String key, String def) {
        return spUtils.getString(key, def);
    }

    public static int getInt(String key, int defaultRes) {
        return spUtils.getInt(key, defaultRes);
    }

    public static boolean getBoolean(String key) {
        return spUtils.getBoolean(key, false);
    }

    public static void putBoolean(String key,boolean value) {
        spUtils.put(key, value,true);
    }

    public static void saveString(String key, String content) {
        spUtils.put(key, content);
    }

    /**
     * 保存用户信息
     *
     * @param userTokenBean
     * @return
     */
    public static void saveUser(UserTokenBean userTokenBean, String mobie) {
        spUtils.put(IvyConstants.HX_USERNAME, userTokenBean.getHx_username());
        spUtils.put(IvyConstants.HX_PASSWORD, userTokenBean.getHx_password());
        spUtils.put(IvyConstants.TICKET, userTokenBean.getTicket());
        spUtils.put(IvyConstants.UID, userTokenBean.getUid());
        spUtils.put(IvyConstants.USER_TAGS, userTokenBean.getTags());
        spUtils.put(IvyConstants.USER_MOBILE, mobie);
        spUtils.put(IvyConstants.IS_LOGIN, true);
        spUtils.put(IvyConstants.USER_AVATAR, userTokenBean.getAvatar());
    }

    public static boolean getHxIsLogin() {
        return spUtils.getBoolean(IvyConstants.HX_LOGIN, false);
    }

    public static void putHxIsLogin(boolean hxIsLogin) {
        spUtils.put(IvyConstants.HX_LOGIN, hxIsLogin);
    }

    public static boolean getUserIsMember(){
        return spUtils.getBoolean(IvyConstants.USER_IS_MEMBER,false);
    }

    public static void putUserIsMember(boolean isMember){
        spUtils.put(IvyConstants.USER_IS_MEMBER,isMember);
    }


    /**
     * 退出登录
     */
    public static void exitUser() {
        // 退出环信
        EMManager.INSTANCE.logout();

        spUtils.put(IvyConstants.HX_USERNAME, "");
        spUtils.put(IvyConstants.HX_PASSWORD, "");
        spUtils.put(IvyConstants.IS_LOGIN, false);
        spUtils.put(IvyConstants.TICKET, "");
        spUtils.put(IvyConstants.UID, 0L);
        spUtils.put(IvyConstants.USER_TAGS, "");
        spUtils.put(IvyConstants.USER_MOBILE, "");
        spUtils.put(IvyConstants.USER_AVATAR, "");
        spUtils.put(IvyConstants.USER_IS_MEMBER,false);
    }

    public static void saveOSSToken(GetTokenBean gettokenBean) {
        // 上传参数
        spUtils.put(IvyConstants.ACCESSKEYID, gettokenBean.getAccessKeyId(), true);
        spUtils.put(IvyConstants.ACCESSKEYSECRET, gettokenBean.getAccessKeySecret(), true);
        spUtils.put(IvyConstants.ENDPOINT, gettokenBean.getEndpoint(), true);
        spUtils.put(IvyConstants.BUCKETNAME, gettokenBean.getBucketName(), true);
        spUtils.put(IvyConstants.CALLBACKURL, gettokenBean.getCallbackUrl(), true);
        spUtils.put(IvyConstants.CALLBACKBODY, gettokenBean.getCallbackBody(), true);
        // 上传token
        spUtils.put(IvyConstants.SECURITYTOKEN, gettokenBean.getSecurityToken(), true);
        spUtils.put(IvyConstants.EXPIRES_IN, gettokenBean.getExpires_in(), true);
        // 上传目录
        GetTokenBean.XpathBean bean = gettokenBean.getXpath();
        if (bean != null) {
            spUtils.put(IvyConstants.AVATAR_PATH, bean.getAvatar(), true);
            spUtils.put(IvyConstants.CORER_PATH, bean.getCover(), true);
            spUtils.put(IvyConstants.MUSIC_PATH, bean.getMusic(), true);
            spUtils.put(IvyConstants.VIDEO_PATH, bean.getVideo(), true);
            spUtils.put(IvyConstants.MEMBER_AVATAR, bean.getMember_avatar(), true);
            spUtils.put(IvyConstants.SHAREHOLDER_SIGN, bean.getShareholder_sign(), true);
            spUtils.put(IvyConstants.IDCARD_PATH, bean.getIdcard(), true);
        }

        GetTokenBean.OSSBean oosBean = gettokenBean.getOss_cdn();
        if (oosBean != null) {
            spUtils.put(IvyConstants.CDN_ENABLE, oosBean.getCdn_enable(), true);
            spUtils.put(IvyConstants.CDN_URL, oosBean.getCdn_url(), true);
            spUtils.put(IvyConstants.CDN_MASK, oosBean.getCdn_key(), true);
        }
    }
}
