package com.wwwjf.base;

public interface BaseConstant {

    /**
     * 权限
     */
    interface Permission {
        int REQUEST_CODE = 1001;
    }

    /**
     * Activity 请求code
     */
    interface ActivityCode {

        /**
         * Standard activity result: operation canceled.
         */
        int RESULT_CANCELED = 0;
        /**
         * Standard activity result: operation succeeded.
         */
        int RESULT_OK = -1;
        /**
         * Start of user-defined activity results.
         */
        int RESULT_FIRST_USER = 1;

        /**
         * 登录成功后返回
         */
        int RESULT_LOGIN_CODE_2 = 2;


        int REQUEST_CODE_10001 = 10001;
        int REQUEST_CODE_10002 = 10002;
        int REQUEST_CODE_10003 = 10003;
        int REQUEST_CODE_10004 = 10004;
        int REQUEST_CODE_10005 = 10005;
        int REQUEST_CODE_10006 = 10006;
        int REQUEST_CODE_10007 = 10007;
        int REQUEST_CODE_10008 = 10008;
        int REQUEST_CODE_10009 = 10009;
        int REQUEST_CODE_10010 = 10010;
        int REQUEST_CODE_10011 = 10011;
    }


    /**
     * intent传值key
     */
    interface IntentKey {
        String PARAMS_1 = "params_1";
        String PARAMS_2 = "params_2";
        String PARAMS_3 = "params_3";
        String PARAMS_4 = "params_4";
        String PARAMS_5 = "params_5";
        String PARAMS_6 = "params_6";
        String PARAMS_7 = "params_7";
        String PARAMS_8 = "params_8";
    }

    /**
     * bundle传值key
     */
    interface BundleKey {
        String PARAMS_1 = "params_1";
        String PARAMS_2 = "params_2";
        String PARAMS_3 = "params_3";
        String PARAMS_4 = "params_4";

    }

}
