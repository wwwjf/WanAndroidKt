package com.xianghe.ivy.app;

/**
 * 接口地址不再取该类下的值，在build.gradle中配置
 */
@Deprecated
public class URLS {

    /**
     * URL地址
     */
//    public static final String BASE_URL = "http://206.i-weiying.com";//临时地址
    public static final String BASE_URL = "http://ivy.i-weiying.com/api/";//正式地址


    //------h5页面相关url start----------------
    public static final String LOCAL_URL = "http://192.168.1.235:8099";
    public static final String BASE_TEMP_URL_WAP = "http://206wap.i-weiying.com";
    public static final String BASE_TEST_URL_WAP = "http://testwap.i-weiying.com";//测试地址
    public static final String BASE_PRE_URL_WAP = "http://prewap.i-weiying.com"; //预正式地址
    public static final String BASE_PROTECT_URL_WAP = "http://ivywap.i-weiying.com";//正式地址

    public static final String BASE_URL_WAP = BASE_PRE_URL_WAP;
}
