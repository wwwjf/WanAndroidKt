package com.wwwjf.plugindemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Proxy

class pluginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plugin)

    }
}