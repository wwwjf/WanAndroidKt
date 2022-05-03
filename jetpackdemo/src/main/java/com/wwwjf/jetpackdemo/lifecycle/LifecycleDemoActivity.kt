package com.wwwjf.jetpackdemo.lifecycle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wwwjf.jetpackdemo.R
import com.wwwjf.jetpackdemo.livedata.MyLiveData
import com.wwwjf.jetpackdemo.log

class LifecycleDemoActivity : AppCompatActivity() {
    lateinit var myPresenter: MyPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lifecycle_demo)
        log("onCreate------")
        myPresenter = MyPresenter()
        lifecycle.addObserver(myPresenter)
    }

    override fun onStart() {
        super.onStart()
        log("onStart------")
    }

    override fun onResume() {
        super.onResume()
        log("onResume------")
    }

    override fun onPause() {
        super.onPause()
        log("onPause------")
    }

    override fun onStop() {
        super.onStop()
        log("onStop------")
    }

    override fun onDestroy() {
        super.onDestroy()
        log("onDestroy------")
    }
}