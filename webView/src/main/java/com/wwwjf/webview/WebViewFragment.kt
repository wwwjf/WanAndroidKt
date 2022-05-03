package com.wwwjf.webview

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.wwwjf.webview.client.WebClient
import com.wwwjf.webview.databinding.FragmentWebViewBinding
import com.wwwjf.webview.utils.Constant

class WebViewFragment : Fragment(), WebCallBack {
    private var mUrl: String? = null
    private lateinit var binding: FragmentWebViewBinding

    companion object {
        fun newInstance(url: String): WebViewFragment {
            val fragment = WebViewFragment()
            val bundle = Bundle()
            bundle.putString(Constant.URL, url)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mUrl = arguments?.getString(Constant.URL)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_web_view, container, false)
        binding.webViewFragment.settings.javaScriptEnabled = true
        binding.webViewFragment.webChromeClient = WebClient(this)
        mUrl?.let { it -> binding.webViewFragment.loadUrl(it) }
        return binding.root
    }

    override fun onPageStarted(url: String) {

    }

    override fun onPageFinished(url: String) {
    }

    override fun updateTitle(title: String) {
    }

    override fun onError() {
    }


}