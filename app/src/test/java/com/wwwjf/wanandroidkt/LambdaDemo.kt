@file:JvmName("LambdaDemoKt")

package com.wwwjf.wanandroidkt


fun main() {
    var method01: (Int) -> Boolean
//    var a1 = method01

    var method02: (Int) -> Boolean = { num -> num == 10 }
    var a2 = method02
    println("aaaaaa2=${a2(1)}")
    println("aaaaaa3=${method02(10)}")

    var method03: (Int) -> Boolean = { num: Int -> num == 10 }
    var method04 = { num: Int -> num == 10 }

    //覆盖操作
    var method07 = { num: Int -> num == 10 }
    method07 = { false }
    println("aaaaaa7=${method07(10)}")

    //高阶函数
    val a8 = method08(1) {
        it == 10
    }


    println("aaaaaa8$a8")
    val a10 = method10(10) {
        println("aaaaaa10:$this")
        this.compareTo(20)
    }
    println("aaaaaa10=$a10")

    method11 {
        println(it)
    }

}

fun method05(num: Int): Boolean {
    return num == 10
}

fun method06(num: Int): Boolean = (num == 10)

fun method08(num1: Int, method09: (Int) -> Boolean) {

    val a8 = method09
    println("aaaaaa8=${a8(1)}")
}

fun <T, E> method10(input1: T, input2: T.() -> E): E {
    return input1.input2()
}

fun method11(method:(Int)->Unit){
    method
}

