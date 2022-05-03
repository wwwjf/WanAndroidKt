package com.wwwjf.jetpackdemo.livedata

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.wwwjf.jetpackdemo.R
import com.wwwjf.jetpackdemo.databinding.ActivityLiveDataDemoBinding
import com.wwwjf.jetpackdemo.log

class LiveDataDemoActivity : AppCompatActivity() {
    private lateinit var binding:ActivityLiveDataDemoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_live_data_demo)

        //观察者
        MyLiveData.info.observe(this, {
            //更新UI
            binding.tvLiveDataDemo.text = it
        })

        //触发数据改变
        MyLiveData.info.postValue("触发更新")

        binding.btnLiveDataDemo.setOnClickListener {

            //粘性数据
            MyLiveData.data1.value = "先修改数据，后订阅"
            BusLiveData.with("data1",String::class.java,false).value = "先修改数据，后订阅2"
            log("------跳转前---------")
            startActivity(Intent(this,LiveDataDemo2Activity::class.java))
        }

    }

    override fun onRestart() {
        super.onRestart()
        log("onReStart()......")
    }

    override fun onStart() {
        super.onStart()
        log("onStart......")
    }

    override fun onResume() {
        super.onResume()
        log("onResume......")
    }

    override fun onStop() {
        super.onStop()
        log("onStop()......")
    }
}