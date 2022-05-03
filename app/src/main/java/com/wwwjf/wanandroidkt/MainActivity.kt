package com.wwwjf.wanandroidkt

import android.content.Intent
import android.media.MediaMetadataRetriever
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewStub
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wwwjf.base.autoservice.BaseServiceLoader
import com.wwwjf.common.autoservice.IAudioDemoService
import com.wwwjf.common.autoservice.IWebViewActivityService
import com.wwwjf.common.autoservice.IWebViewFragmentService
import com.wwwjf.wanandroidkt.adapter.RvAdapter
import com.wwwjf.wanandroidkt.adapter.RvViewHolder
import com.wwwjf.wanandroidkt.adapter.SlideAdapter
import com.wwwjf.wanandroidkt.databinding.ActivityMainBinding
import com.wwwjf.webview.WebViewActivity
import kotlinx.coroutines.*
import java.util.*

class MainActivity : AppCompatActivity() {
    private val TAG: String = MainActivity::class.java.simpleName
    private lateinit var binding:ActivityMainBinding
    private lateinit var viewStub: ViewStub
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        setContentView(binding.root)


        initRv1()
        viewStub = findViewById(R.id.viewStub_content)
        classLoader.getResourceAsStream("")

    }

    private fun initRv1() {

        val list = mutableListOf<String>()
        for (i in 0..20){
            list.add("$i")
        }
        binding.rv1.layoutManager = LinearLayoutManager(this,RecyclerView.VERTICAL,false)
        /*binding.rv1.adapter =  object :RvAdapter<String>(this,android.R.layout.simple_list_item_1,list){
            override fun convert(holder: RvViewHolder, t: String) {
                val tv = holder.getView<TextView>(android.R.id.text1)
                tv.text = t
            }
        }*/
        binding.rv1.adapter = SlideAdapter(this)

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.e(TAG, "onSaveInstanceState: ----------------")
    }

    fun click(view: View) = runBlocking {//阻塞式
        Log.e(TAG, "---当前线程1：${Thread.currentThread().name}")
        //协程环境
        launch(Dispatchers.IO) {
            Log.e(TAG, "---当前线程2：${Thread.currentThread().name}")
            repeat(5) {
                Thread.sleep(100)
                Log.e(TAG, "---当前线程3：${Thread.currentThread().name}-${SystemClock.currentThreadTimeMillis()}")
            }

        }
        val result = async(Dispatchers.IO) {
            "修改文字"
        }
        result.await()
        withContext(coroutineContext){

        }

    }

    fun addViewStub(view: View) {
        try {

            val v = viewStub.inflate()
            val tvContent = findViewById<TextView>(R.id.tv_viewstub_child)
            tvContent.setText("修改viewstub子view文字")

        } catch (e:Exception){
            if(viewStub.visibility == View.GONE){
                viewStub.visibility = View.VISIBLE
            } else {
                viewStub.visibility = View.GONE
            }
        }


    }

    fun webViewClickActivity(view: View) {
        val webViewService = BaseServiceLoader.load(IWebViewActivityService::class.java)
        webViewService?.startWebViewActivity(this,"https://www.baidu.com","白度",false)
    }

    fun webViewClickFragment(view:View){
        val webViewService = BaseServiceLoader.load(IWebViewFragmentService::class.java)
        webViewService?.startWebViewFragmentActivity(this,"https://www.baidu.com","baidu")

    }

    fun toAudioDemo(view: View){
        val audioDemoService = BaseServiceLoader.load(IAudioDemoService::class.java)
        audioDemoService?.startAudioDemoActivity(this)
    }
}