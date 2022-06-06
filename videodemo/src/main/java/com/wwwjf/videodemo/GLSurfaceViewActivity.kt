package com.wwwjf.videodemo

import android.hardware.Camera
import android.os.Bundle
import android.view.SurfaceHolder
import androidx.appcompat.app.AppCompatActivity
import com.wwwjf.videodemo.databinding.ActivityGlsurfaceViewBinding
import java.util.*
import kotlin.Comparator

class GLSurfaceViewActivity : AppCompatActivity() {
    private lateinit var viewbinding:ActivityGlsurfaceViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewbinding = ActivityGlsurfaceViewBinding.inflate(layoutInflater)
        setContentView(viewbinding.root)


    }


}