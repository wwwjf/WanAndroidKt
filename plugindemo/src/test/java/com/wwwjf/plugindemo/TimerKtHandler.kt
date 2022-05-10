package com.wwwjf.plugindemo

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

class TimerKtHandler(obj: Any) : InvocationHandler {
    private val TAG = TimerKtHandler::class.java.simpleName

    private var target: Any = obj

    fun getProxy(): Any {
        return Proxy.newProxyInstance(
            UserService::class.java.classLoader,
            target.javaClass.interfaces,
            this)
    }

    override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>): Any? {

        val startTime = System.currentTimeMillis()
        val result = method?.invoke(target, *args)//args 需要加*
        val endTime = System.currentTimeMillis()
        println("$TAG------${target.javaClass.simpleName}.${method?.name}执行时间：${endTime.minus(startTime)}ms")
        return result
    }
}