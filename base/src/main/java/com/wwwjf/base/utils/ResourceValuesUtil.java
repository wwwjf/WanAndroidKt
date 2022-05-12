package com.wwwjf.base.utils;

import android.content.Context;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取资源文件values下的string.xml信息
 */
public class ResourceValuesUtil {

    /**
     * 获取字符串信息,效果和Context的getString()方法一样
     *
     * @param context     context
     * @param keyStringId 区分大小写
     */
    public static String getString(Context context, String keyStringId) {

        String mmkvString = "";
        /*String languageCode = LanguageManager.getLanguageCode();
        MMKV mmkv;
//        KLog.e("============MMKV文件中查找============keyStringId=" + keyStringId);
        if (StringUtil.IsContainChinese(keyStringId) &&
                EPayConstant.Languange.EN_US.equals(LanguageManager.getLanguageCode())) {
            //中文key在英文翻译文件中查找
            mmkv = MMKV.mmkvWithID(EPayConstant.Languange.EN_US);
        } else {
            mmkv = MMKV.mmkvWithID(languageCode);
        }
        mmkvString = mmkv.getString(keyStringId, "");*/
        int keyId;
        if (StringUtil.isTrimEmpty(mmkvString)) {
            keyId = context.getResources().getIdentifier(keyStringId, "string", context.getPackageName());
        } else {
            return mmkvString;
        }
        if (keyId <= 10000) {//10000数字任意，区分资源文件的id还是接口返回的纯数字数据
            return keyStringId;
        } else {
            return context.getResources().getString(keyId);
        }
    }

    /**
     * 获取 形如【1,身份证;2,护照;6,驾驶执照;7,台胞证;8,港澳通行证;9,其他】数据
     *
     * @param context   context
     * @param keyString keyString
     */
    public static Map<String, String> getMapString(Context context, String keyString) {
        Map<String, String> result = new HashMap<>();
        String string = getString(context, keyString);
        String[] array = null;
        if (string.contains(";")) {
            array = string.split(";");
        }
        if (array == null || array.length <= 0) {
            return result;
        }
        for (String arr : array) {
            String[] split = arr.split(",");
            if (split.length >= 2) {
                result.put(split[0], split[1]);
            }
        }

        return result;
    }


    /**
     * 获取 形如【身份证,护照,驾驶执照,台胞证,港澳通行证,其他】【身份证;护照;驾驶执照;台胞证;港澳通行证;其他】数据
     *
     * @param context   context
     * @param keyString keyString
     */
    public static List<String> getListString(Context context, String keyString) {
        String string = getString(context, keyString);

        return StringUtil.getList(string);
    }


}
