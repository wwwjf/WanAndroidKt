package com.wwwjf.jetpackdemo.lifecycle

import android.util.Log
import com.wwwjf.jetpackdemo.lifecycle.IPresenter
import com.wwwjf.jetpackdemo.log

class MyPresenter: IPresenter {
    override fun onCreate() {
        log("lifecycle_onCreate()......")
    }


    override fun onStart() {
        log("lifecycle_onStart()......")
    }

    override fun onResume() {
        log("lifecycle_onResume()......")
    }

    override fun onPause() {
        log("lifecycle_onPause()......")
    }

    override fun onStop() {
        log("lifecycle_onStop()......")
    }

    override fun onDestroy() {
        log("lifecycle_onDestroy()......")
    }
}


