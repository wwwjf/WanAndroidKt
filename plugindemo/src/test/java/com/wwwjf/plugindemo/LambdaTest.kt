package com.wwwjf.plugindemo

import org.junit.Test

class LambdaTest {

    @Test
    fun method() {
        val a = Array<String>(5) { "a" }
        val sa = IntArray(1)
        val ia = { 1 }
        val printa = { println("abc") }
        val a1: (Int) -> String = { x: Int -> "$x" }
        a.indices.forEach({
            println(it)
        })
        val a2 = Array<String>(5, a1)
        a.forEach {
            println("a:$it")
        }
        a2.forEach {
            println("a2:$it")
        }
    }
}