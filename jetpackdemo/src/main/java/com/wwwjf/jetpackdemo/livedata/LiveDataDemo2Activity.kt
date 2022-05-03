package com.wwwjf.jetpackdemo.livedata

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.wwwjf.jetpackdemo.R
import com.wwwjf.jetpackdemo.databinding.ActivityLiveDataDemoBinding
import com.wwwjf.jetpackdemo.log

class LiveDataDemo2Activity : AppCompatActivity() {
    private lateinit var binding:ActivityLiveDataDemoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_live_data_demo)


        //观察者
        // 先订阅数据，后修改，能接收到更新 -- 正常情况
        // 先修改数据，后订阅，能接收到之前的数据--数据粘性
        // 去除粘性数据：hook反射，修改源码动态去除 修改version
        MyLiveData.data1.observe(this,object: Observer<String>{
            override fun onChanged(t: String?) {
                binding.tvLiveDataDemo.text = t
            }
        })
        BusLiveData.with("data1",String::class.java,true).observe(this, {
            //更新UI
            binding.tvLiveDataDemo2.text = it
        })


    }
}