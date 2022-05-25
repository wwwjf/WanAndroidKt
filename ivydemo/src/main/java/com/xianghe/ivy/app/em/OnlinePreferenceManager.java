package com.xianghe.ivy.app.em;

import android.content.Context;
import android.content.SharedPreferences;

import com.xianghe.ivy.app.IvyApp;
import com.xianghe.ivy.utils.KLog;

import java.util.HashSet;
import java.util.Set;

/**
 * author:  ycl
 * date:  2019/3/27 17:41
 * desc:  存储在线的通话视频 （接通就存储，关闭就删除，一定要关闭）
 */
public class OnlinePreferenceManager {

    private SharedPreferences.Editor editor;
    private SharedPreferences mSharedPreferences;

    private static OnlinePreferenceManager instance = null;
    private static final String KEY_AT_GROUPS = "AT_ONLINE";

    private Set<String> onLineSet = null;

    public static OnlinePreferenceManager getInstance() {
        if (instance == null) {
            synchronized (OnlinePreferenceManager.class) {
                if (instance == null) {
                    instance = new OnlinePreferenceManager();
                }
            }
        }
        return instance;
    }

    public OnlinePreferenceManager() {
        mSharedPreferences = IvyApp.getInstance().getApplicationContext().getSharedPreferences("EM_SP_AT_ONLINE", Context.MODE_PRIVATE);
        editor = mSharedPreferences.edit();

        onLineSet = getAtMeGroups();
        if (onLineSet == null) {
            onLineSet = new HashSet<String>();
        }
    }

    public synchronized void putString(String msg) {
        KLog.d("msg: "+msg);
        if (!onLineSet.contains(msg)) {
            onLineSet.add(msg);

            editor.remove(KEY_AT_GROUPS);
            editor.putStringSet(KEY_AT_GROUPS, onLineSet);
            editor.apply();
        }
    }

    public synchronized void remove(String msg) {
        KLog.d("msg: "+msg);
        if (onLineSet.contains(msg)) {
            onLineSet.remove(msg);

            editor.remove(KEY_AT_GROUPS);
            editor.putStringSet(KEY_AT_GROUPS, onLineSet);
            editor.apply();
        }
    }

    public synchronized void clear() {
        onLineSet.clear();

        editor.remove(KEY_AT_GROUPS);
        editor.apply();
    }

    private Set<String> getAtMeGroups() {
        return mSharedPreferences.getStringSet(KEY_AT_GROUPS, null);
    }

    public Set<String> getOnLineSet() {
        return onLineSet;
    }
}
