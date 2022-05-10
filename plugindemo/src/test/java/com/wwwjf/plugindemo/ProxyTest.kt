package com.wwwjf.plugindemo

import org.junit.Test
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy


class ProxyTest {

    @Test
    fun proxyRun(){

        ktMethod()
//        javaMethod()

    }

    private fun ktMethod() {
        val userService: UserService = UserServiceImpl()
        val handler = TimerKtHandler(userService)
        val proxy = handler.getProxy() as UserService
        proxy.register("zs")
    }

    private fun javaMethod() {
        val userService: UserService = UserServiceImpl()
        val handler = TimerJavaHandler(userService)
        val proxy = handler.proxy as UserService
        proxy.register("ls")
    }
}