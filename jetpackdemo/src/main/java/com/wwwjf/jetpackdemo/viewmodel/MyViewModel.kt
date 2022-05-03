package com.wwwjf.jetpackdemo.viewmodel

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyViewModel(application: Application) : AndroidViewModel(application) {

    //三百个字段
//...
    //三百个字段
    val phoneInfo: MutableLiveData<String> by lazy { MutableLiveData() }

    init {
        phoneInfo.value = ""//设置默认值
    }

    //定义一个环境
    var context = application

    fun appendNumber(number: String) {
        phoneInfo.value = phoneInfo.value + number
    }

    fun backspaceNumber() {
        var length = phoneInfo.value?.length ?: 0
        if (length > 0) {
            phoneInfo.value = phoneInfo.value?.substring(0, length - 1)
        }
    }

    fun clear(){
        phoneInfo.value = ""
    }

    fun call(){
        val intent = Intent()
        intent.action = Intent.ACTION_CALL
        intent.data = Uri.parse("tel:"+phoneInfo.value)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }
}