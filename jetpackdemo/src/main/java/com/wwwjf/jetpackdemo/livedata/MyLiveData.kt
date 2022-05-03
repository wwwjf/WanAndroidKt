package com.wwwjf.jetpackdemo.livedata

import androidx.lifecycle.MutableLiveData

object MyLiveData {

    val info:MutableLiveData<String> by lazy { MutableLiveData() }

    val data1:MutableLiveData<String> by lazy { MutableLiveData() }
}
