package com.wwwjf.plugindemo

import org.junit.Test

class VarargsTest {

    @Test
    fun method(){
        val arr = arrayOf("a","b","c")
        method1(*arr)
    }

    fun method1(vararg args: String){
        args.forEach {
            println("args1:$it")
        }
        for (arg in args) {
            println("args2:$arg")
        }
    }
}