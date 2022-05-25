package com.xianghe.ivy.manager;

import com.blankj.utilcode.util.SPUtils;
import com.xianghe.ivy.app.IvyConstants;

/**
 * 系统设置信息
 */
public class SystemSettingManager {
    private static SPUtils spUtils = SPUtils.getInstance(IvyConstants.SP_SYSTEM_SETTING);

    /**
     * 设置是否流量播放
     *
     * @param isGprsPlay
     */
    public static void saveIsAutoPlay(boolean isGprsPlay) {
        spUtils.put(IvyConstants.KEY_GRPS_PLAY, isGprsPlay);
    }

    /**
     * 是否流量播放
     *
     * @return
     */
    public static boolean isAutoPlay() {
        return spUtils.getBoolean(IvyConstants.KEY_GRPS_PLAY);
    }

    /**
     * @Date 创建时间: 2018/9/28
     * @Description 描述：存储 REGISTRATION_ID
     */
    public static void saveRegistrationID(String registrationID) {
        spUtils.put(IvyConstants.REGISTRATION_ID, registrationID);
    }

    /**
     * 读取 REGISTRATION_ID
     *
     * @return
     */
    public static String readRegistrationID() {
        return spUtils.getString(IvyConstants.REGISTRATION_ID);
    }

    /**
     * 是是否 REGISTRATION_ID 存在
     *
     * @return
     */
    public static boolean isRegistrationID() {
        return spUtils.contains(IvyConstants.REGISTRATION_ID);
    }
}

