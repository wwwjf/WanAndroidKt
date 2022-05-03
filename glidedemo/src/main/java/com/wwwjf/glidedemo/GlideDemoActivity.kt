package com.wwwjf.glidedemo

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.RequestOptions
import java.util.ArrayList

class GlideDemoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_glide_demo)
        val iv = findViewById<ImageView>(R.id.iv)
        Glide.with(this).load("").into(iv)
        Glide.with(Fragment()).asBitmap().load("").into(iv)
        Glide.with(this)
            .load(
                GlideUrl("http:www.baidu.com",
                    LazyHeaders.Builder().addHeader("EPAY-ACCESS-KEY", "").build()
                )
            )
            .into(iv)

        Glide.with(this)
            .load(CustomGlideUrl("http:www.baidu.com"))
            .into(iv)

        Glide.with(this)
            .load("")
            .apply(RequestOptions().error(R.drawable.ic_launcher_background))
            .error(R.drawable.ic_launcher_background)
            .placeholder(R.drawable.ic_launcher_background)
            .fallback(ColorDrawable(Color.GRAY))
            .into(iv)

        Glide.with(this)
            .load("")
            .thumbnail(Glide.with(this).load(""))
            .into(iv)

        val manager = Glide.with(this)
    }
}