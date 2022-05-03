package com.wwwjf.wanandroidkt

class Person(val name:String) {
    fun showNam(name: String){

    }
    companion object{
        fun showName(name: String){
            println("name=${name}")
        }
    }
}

fun main() {
    Person("zs")
}