package com.wwwjf.audiodemo

import android.content.Context
import android.content.Intent
import com.google.auto.service.AutoService
import com.wwwjf.common.autoservice.IAudioDemoService

@AutoService(IAudioDemoService::class)
class AudioDemoServiceImpl:IAudioDemoService {
    override fun startAudioDemoActivity(context: Context) {
        val intent = Intent(context, AudioDemoActivity::class.java)
        context.startActivity(intent)
    }
}