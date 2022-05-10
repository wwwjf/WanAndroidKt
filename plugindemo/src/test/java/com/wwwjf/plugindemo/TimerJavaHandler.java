package com.wwwjf.plugindemo;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class TimerJavaHandler implements InvocationHandler {

    public static final String TAG = TimerJavaHandler.class.getSimpleName();

    private final Object target;

    public TimerJavaHandler(Object target) {
        this.target = target;
    }

    public Object getProxy(){
        return Proxy.newProxyInstance(target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        long startTime = System.currentTimeMillis();
        Object result = method.invoke(target, args);
        long endTime = System.currentTimeMillis();
        System.out.println(TAG+"----"+target.getClass().getSimpleName()+"."+method.getName()+"耗时统计："+(endTime-startTime)+"ms");
        return result;
    }
}
