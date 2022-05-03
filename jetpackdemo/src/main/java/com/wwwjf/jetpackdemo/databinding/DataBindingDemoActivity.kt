package com.wwwjf.jetpackdemo.databinding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.wwwjf.jetpackdemo.R
import com.wwwjf.jetpackdemo.databinding.model.User
import com.wwwjf.jetpackdemo.log

class DataBindingDemoActivity : AppCompatActivity() {

    private lateinit var binding:ActivityDataBindingDemoBinding
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_data_binding_demo)
        user = User("zs",20,1)
        user.nameF.set("lisi")
        user.ageF.set(30)
        user.sexF.set(2)
        val user1 = User()
        val user2 = User("zs2",100)
        log("user2.name=${user2.name},user2.age=${user2.age}")
        LayoutInflater::class.java.getDeclaredField("")
        binding.u = user

        Thread {
            for (i in 0..10){
                Thread.sleep(2000)
                user.nameF.set(user.name+i)
            }
        }.start()
    }
}