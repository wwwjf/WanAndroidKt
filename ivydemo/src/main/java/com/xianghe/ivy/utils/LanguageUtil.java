package com.xianghe.ivy.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;

import androidx.annotation.Nullable;

import java.util.Locale;

public class LanguageUtil {
    private static final String SP_NAME = "language_config";
    private static final String SP_KEY_LANG = "sp_key_lang";
    private static final String SP_KEY_COUNTRY = "sp_key_country";

    public static boolean isSimplifiedChinese(Context context) {
        Locale locales;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locales = context.getResources().getConfiguration().getLocales().get(0);
        } else {
            locales = context.getResources().getConfiguration().locale;
        }
        String language = locales.getLanguage();
        return "zh".equalsIgnoreCase(language);
    }

    public static boolean isSimplifiedChinese(Locale locale) {
        String language = locale.getLanguage();
        return "zh".equalsIgnoreCase(language);
    }

    @Nullable
    public static String getLastSettingLanguage(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
                .getString(SP_KEY_LANG, null);
    }

    @Nullable
    public static String getLastSettingCountry(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
                .getString(SP_KEY_COUNTRY, null);
    }

    public static void changeLanguage(Context context, Locale locale) {
        try {
            Context appCtx = context.getApplicationContext();
            Resources res = context.getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration config = res.getConfiguration();

            if (appCtx != context) {
                Resources appRes = appCtx.getResources();
                DisplayMetrics appDm = appRes.getDisplayMetrics();
                Configuration appConfig = appRes.getConfiguration();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    appConfig.setLocale(locale);

                } else {
                    appConfig.locale = locale;
                }
                appRes.updateConfiguration(appConfig, appDm);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                config.setLocale(locale);

            } else {
                config.locale = locale;
            }

            res.updateConfiguration(config, dm);

            // save sp
            context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit()
                    .putString(SP_KEY_LANG, locale.getLanguage())
                    .putString(SP_KEY_COUNTRY, locale.getCountry())
                    .apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
