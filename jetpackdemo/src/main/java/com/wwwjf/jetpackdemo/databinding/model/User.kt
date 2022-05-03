package com.wwwjf.jetpackdemo.databinding.model

import androidx.databinding.BaseObservable
import androidx.databinding.ObservableField

class User @JvmOverloads constructor(var name: String? = "", var age:Int?=0, var sex:Int? = 0){

     val nameF:ObservableField<String> by lazy { ObservableField() }
    val ageF:ObservableField<Int> by lazy { ObservableField() }
    val sexF:ObservableField<Int> by lazy { ObservableField() }
 }