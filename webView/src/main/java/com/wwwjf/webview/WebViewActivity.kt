package com.wwwjf.webview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.wwwjf.webview.databinding.ActivityWebViewBinding
import com.wwwjf.webview.utils.Constant

class WebViewActivity : AppCompatActivity() {
    private lateinit var binding:ActivityWebViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_web_view)
        val title = intent.getStringExtra(Constant.TITLE)
        val url = intent.getStringExtra(Constant.URL)
        val showActionBar = intent.getBooleanExtra(Constant.SHOW_ACTIONBAR,true)
        binding.tvTitle.setText(title)
        binding.viewActionBar.visibility = View.VISIBLE.takeIf { showActionBar }?:View.GONE
        binding.webView.settings.javaScriptEnabled = true
        url?.let { binding.webView.loadUrl(it) }
    }
}