package com.example.hookdemo

import android.os.Handler
import android.os.Looper
import com.wwwjf.wanandroidkt.FuClass
import com.wwwjf.wanandroidkt.ZiClass
import kotlin.contracts.contract


fun main() {

    //lambada表达式
    val add: (Int, Int) -> Int = { num1, num2 -> num1 + num2 }
    println("add=${add(1, 2)}")

    lenMethod(1, 2, 3, 4, 55, 44)

    //打印$符号
    println("${'$'}${add(1, 10)}")

    var info: String? = null
    info?.forEach(::print)
    println(info?.length ?: "")
    //null 为空时不执行.length
    println(info?.length)
    //断言info不为空，执行.length NLP异常
//    println(info!!.length)
    val infoLet = info.let {
        println("let输出info=$it")
        "infolet"
    }
    println("infolet=$infoLet")

    info = "also"
    //also返回值是传入对象本身
    val infoAlso = info.also {
        println("also输出info=$it")
        "infoalso"
    }
    println("infoalso=$infoAlso")

    info = null
    val infoRun = info.run {
        println("run输出info=$this")
        "infoRun"
    }
    println("infoRun=$infoRun")

    info = "apply"
    val infoApply = info.apply {
        println("apply输出info=$info")
        "infoApply"
    }
    println("infoApply=$infoApply")

    //循环
    for (i in 9 downTo 1) {
        println("i=$i")
    }

    //比较
    val name1 = 10000.12
    val name2 = 10000.12
    println("比较内容：${name1.equals(name2)}")
    println("比较内容：${name1 == name2}")
    println("比较地址：${name1 === name2}")

    //数组
    val numList = Array(20,{ value:Int ->(value+1000)})
    for (i in numList) {
        println("i=$i")
    }

    //条件比较大小 表达式
    val result = if (name1>name2) "name1大于name2" else "name1不大于name2"
    println("result=$result")

    //条件
    val num = 100
    when(num){
        in -100..0 -> println("-100-0")
        in 0..100 -> println("0-100")
        in 100..200 -> println("100-200")
        else-> println("其他")
    }

    //循环 标签
    asdf@ for (i in 1..10){
        for (j in 1..10){
            if (j==5){
//                break //结束里层循环
                break@asdf//结束外层循环
            }
        }
    }

    val person = Person()
    val student = Student(1,1)

    //data数据类
    val user1 = User(1,"zs",1)
    val user2 = User(1,"ls")
    val user3 = User(1)

    user1.run { println("user1.run:$id,$name,$sex") }
    with(user1){
        println("user1 with:$id,$name,$sex")
    }
    //object单例类
    NetworkManager.requestData()

    /*Handler(Looper.getMainLooper(),Handler.Callback {
        true
    })*/

    //自定义判空和系统判空
    val token: String? = ""
    if (checkTokenIsValid(token)) {//这里判空处理交于函数来处理，根据函数返回值做判断
        println("token length is ${token?.length}")//编译异常: 报token是个可空类型，需要做判空处理。这时候是不是就很郁闷了
    }
    if (!token.isNullOrEmpty()){
        token.length
    }

    //out in 对应Java中的extend super
    var fuClass = FuClass()
    val ziClass = ZiClass()

    var list1:MutableList<out FuClass> = ArrayList<ZiClass>()
//     var fuClass1 = list1.get(0)//能获取
//    list1.add(ziClass) //不能修改
    val list2:MutableList<in ZiClass> = ArrayList<FuClass>()
    list2.add(ziClass)
    showList2(list2)



}







fun showList2(list2: MutableList<in ZiClass>) {
//    var ziClass2:ZiClass = list2.get(0)
    list2.add(ZiClass())
    for (any in list2) {
        println(any)
    }
}


//可变长参数
fun lenMethod(vararg values: Int) {
    for (value in values) {
        println(value)
    }
}

class FunDemo {
    val name:String? = null

    fun show() {
        //标签
        println(this.name)
        println(this@FunDemo.name)
    }
}

open class Person(id:Int)//主构造
{
    constructor(id: Int,name: String) : this(id)//次构造 需要调用主构造

    constructor():this(id = 1)//次构造
}

class Student:Person{//只能继承open修饰的类

    constructor(id: Int,sex:Int)
}

data class User(val id: Int=0, val name: String="", val sex: Int=1)

object NetworkManager{
    fun requestData() {

    }
}


fun checkTokenIsValid(token: String?): Boolean{
    return token != null && token.isNotBlank()
}

class Teacher<in Person>{
    fun setData(person: Person){

    }

    //不能get 只能set
    /*fun getData():Person? {
        return Person()
    }*/
}