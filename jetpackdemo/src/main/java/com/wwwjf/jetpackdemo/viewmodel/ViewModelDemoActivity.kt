package com.wwwjf.jetpackdemo.viewmodel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.wwwjf.jetpackdemo.R
import com.wwwjf.jetpackdemo.databinding.ActivityViewModelDemoBinding
import com.wwwjf.jetpackdemo.room.Student

class ViewModelDemoActivity : AppCompatActivity() {

    private lateinit var binding:ActivityViewModelDemoBinding
    private lateinit var viewModel:MyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_model_demo)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_view_model_demo)
        viewModel = ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory(application))
            .get(MyViewModel::class.java)
        binding.vm = viewModel
        //感应
        binding.lifecycleOwner = this

    }
}