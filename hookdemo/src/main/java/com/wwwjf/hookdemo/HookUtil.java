package com.wwwjf.hookdemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

public class HookUtil {


    private static final String TARGET_INTENT = "target_intent";
    private static final String TAG = HookUtil.class.getSimpleName();

    public static void hookAMS(Context context) {

        try {
            //获取singleTon对象
            Field singleTonField;
            if (Build.VERSION.SDK_INT<Build.VERSION_CODES.O){//小于8.0
                Log.e(TAG, "hookAMS: <8.0");
                Class<?> activityTaskManager = Class.forName("android.app.ActivityManagerNative");
                singleTonField = activityTaskManager.getDeclaredField("gDefault");
            } else {
                Log.e(TAG, "hookAMS: >=8.0");
                Class<?> activityTaskManager = Class.forName("android.app.ActivityTaskManager");
                singleTonField = activityTaskManager.getDeclaredField("IActivityTaskManagerSingleton");
            }

            singleTonField.setAccessible(true);
            Object singleTon = singleTonField.get(null);

            Class<?> singleTonClass = Class.forName("android.util.Singleton");
            Field mInstanceField = singleTonClass.getDeclaredField("mInstance");
            mInstanceField.setAccessible(true);
            Method getMethod = singleTonClass.getMethod("get");
            Object mInstance = getMethod.invoke(singleTon);


            Class<?> iActivityTaskManager = Class.forName("android.app.IActivityTaskManager");
            Object proxyInstance = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                    new Class[]{iActivityTaskManager}, (proxy, method, args) -> {
                        if ("startActivity".equals(method.getName())) {
                            int index = -1;
                            for (int i = 0; i < args.length; i++) {
                                if (args[i] instanceof Intent) {
                                    index = i;
                                    break;
                                }
                            }
                            Intent proxyIntent = new Intent();
                            proxyIntent.setComponent(new ComponentName(context.getPackageName(), ProxyActivity.class.getName()));
                            Intent intent = (Intent) args[index];
                            proxyIntent.putExtra(TARGET_INTENT, intent);
                            args[index] = proxyIntent;
                        }
                        return method.invoke(mInstance, args);
                    });
            mInstanceField.set(singleTon, proxyInstance);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void hookHandler() {
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Field activityCallbacksField = activityThreadClass.getDeclaredField("sCurrentActivityThread");
            activityCallbacksField.setAccessible(true);
            Object activityThread = activityCallbacksField.get(null);

            Field mHField = activityThreadClass.getDeclaredField("mH");
            mHField.setAccessible(true);
            Handler mH = (Handler) mHField.get(activityThread);

            Field mCallbackField = Handler.class.getDeclaredField("mCallback");
            mCallbackField.setAccessible(true);
            mCallbackField.set(mH, (Handler.Callback) msg -> {
                if (msg.what == 159) {
                    try {
                        Field mActivityCallbacksField = msg.obj.getClass().getDeclaredField("mActivityCallbacks");
                        mActivityCallbacksField.setAccessible(true);
                        List mActivityCallback = (List) mActivityCallbacksField.get(msg.obj);
                        for (int i = 0; i < mActivityCallback.size(); i++) {
                            Object launchActivityItem = mActivityCallback.get(i);
                            Log.e(TAG, launchActivityItem.getClass().getName());
                            if (launchActivityItem.getClass().getName().equals("android.app.servertransaction.LaunchActivityItem")) {
                                Field mIntentField = launchActivityItem.getClass().getDeclaredField("mIntent");
                                mIntentField.setAccessible(true);
                                Intent proxyIntent = (Intent) mIntentField.get(launchActivityItem);

                                Intent intent = proxyIntent.getParcelableExtra(TARGET_INTENT);
                                if (intent != null) {
                                    mIntentField.set(launchActivityItem, intent);
                                }
                            }
                        }
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            });
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
