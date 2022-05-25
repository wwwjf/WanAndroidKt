package com.xianghe.ivy.utils;

import android.util.Log;

import okhttp3.logging.HttpLoggingInterceptor;

public class HttpLogger implements HttpLoggingInterceptor.Logger {
    @Override
    public void log(String message) {
        KLog.d("Http", message);
    }
}
