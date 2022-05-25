package com.xianghe.ivy.app.em;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.xianghe.ivy.app.IvyApp;

import java.util.Set;

public class EMPreferenceManager {
    private SharedPreferences.Editor editor;
    private SharedPreferences mSharedPreferences;

    private static EMPreferenceManager instance = null;
    private static final String KEY_AT_GROUPS = "AT_GROUPS";


    @SuppressLint("CommitPrefEdits")
    private EMPreferenceManager() {
        mSharedPreferences = IvyApp.getInstance().getApplicationContext().getSharedPreferences("EM_SP_AT_MESSAGE", Context.MODE_PRIVATE);
        editor = mSharedPreferences.edit();
    }

    public static EMPreferenceManager getInstance() {
        if (instance == null) {
            synchronized (EMPreferenceManager.class) {
                if (instance == null) {
                    instance = new EMPreferenceManager();
                }
            }
        }
        return instance;
    }


    public synchronized void setAtMeGroups(Set<String> groups) {
        editor.remove(KEY_AT_GROUPS);
        editor.putStringSet(KEY_AT_GROUPS, groups);
        editor.apply();
    }

    public synchronized void clearAtMeGroups() {
        editor.remove(KEY_AT_GROUPS);
        editor.apply();
    }

    public Set<String> getAtMeGroups() {
        return mSharedPreferences.getStringSet(KEY_AT_GROUPS, null);
    }

}
