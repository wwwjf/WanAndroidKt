package com.wwwjf.hookdemo;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class InstrumentationProxy extends Instrumentation {

    private Instrumentation mInstrumentation;
    public static final String TAG = InstrumentationProxy.class.getSimpleName();

    public InstrumentationProxy(Instrumentation instrumentation) {
        this.mInstrumentation = instrumentation;
    }

    public ActivityResult execStartActivity(
            Context who, IBinder contextThread,
            IBinder token, Activity target,
            Intent intent, int requestCode,
            Bundle options) {
        ComponentName fakeComponentName = intent.getParcelableExtra("fakeComponentName");
        if (fakeComponentName != null){
            intent.putExtra("realComponentName",intent.getComponent());
            intent.setComponent(fakeComponentName);
        }
        Class[] classes = new Class[]{Context.class,IBinder.class,
                IBinder.class,Activity.class,
                Intent.class,int.class,Bundle.class};
        try {
            Method[] declaredMethods = getClass().getSuperclass().getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
                Log.e(TAG, "execStartActivity: "+declaredMethod.getName());
            }
            Method execStartActivity = getClass().getSuperclass().getDeclaredMethod("execStartActivity", classes);
            if (execStartActivity != null){
                return (ActivityResult) execStartActivity.invoke(mInstrumentation,
                        new Object[]{who,contextThread,token,target,intent,requestCode,options});
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Activity newActivity(ClassLoader classLoader,String className,Intent intent)
            throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        ComponentName realComponentName = intent.getParcelableExtra("realComponentName");
        if (realComponentName != null){
            className = realComponentName.getClassName();
        }
        return mInstrumentation.newActivity(classLoader,className,intent);
    }
}
