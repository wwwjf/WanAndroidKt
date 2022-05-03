package com.wwwjf.jetpackdemo.viewbinding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wwwjf.jetpackdemo.R
import com.wwwjf.jetpackdemo.databinding.ActivityViewBindingDemoBinding

class ViewBindingDemoActivity : AppCompatActivity() {
    private lateinit var viewBinding:ActivityViewBindingDemoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityViewBindingDemoBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        viewBinding.tvActivityViewbindingdemo.text = "手动设置"

    }
}