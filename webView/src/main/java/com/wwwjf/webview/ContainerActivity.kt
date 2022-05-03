package com.wwwjf.webview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.wwwjf.webview.databinding.ActivityContainerBinding
import com.wwwjf.webview.utils.Constant

class ContainerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContainerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_container)
        binding.tvTitle.setText(intent.getStringExtra(Constant.TITLE))
        binding.viewActionBar.visibility = View.GONE
        binding.ivBack.setOnClickListener { finish()}

        val transaction = supportFragmentManager.beginTransaction()
        val fragment = intent.getStringExtra(Constant.URL)?.let { WebViewFragment.newInstance(it) }
        fragment?.let { transaction.replace(R.id.view_activity_container,fragment).commit() }


    }
}