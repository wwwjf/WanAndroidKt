package com.wwwjf.hookdemo;

import android.app.Application;
import android.app.Instrumentation;
import android.util.Log;


import com.wwwjf.hookdemo.hotfix.Hotfix;

import java.io.File;
import java.lang.reflect.Field;

public class HookApp extends Application {

    public static final String TAG = HookApp.class.getSimpleName();
    @Override
    public void onCreate() {
        super.onCreate();

        Hotfix.installPatch(this,new File(getExternalFilesDir(""),"hotfix.dex"));
//        HookUtil.hookAMS(this);
//        HookUtil.hookHandler();
        hookInstrumentation();
    }
    private void loge(Object message){
        Log.e(TAG,"---"+message);
    }

    private void hookInstrumentation() {
        try {
//            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            Field mMainThreadField = getBaseContext().getClass().getDeclaredField("mMainThread");
            mMainThreadField.setAccessible(true);
            Object o = mMainThreadField.get(getBaseContext());
            loge(o);
            Field mInstrumentation = o.getClass().getDeclaredField("mInstrumentation");
            mInstrumentation.setAccessible(true);
            Instrumentation instrumentation = (Instrumentation) mInstrumentation.get(o);
            InstrumentationProxy instrumentationProxy = new InstrumentationProxy(instrumentation);
            mInstrumentation.set(o,instrumentationProxy);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
