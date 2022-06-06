package com.wwwjf.videodemo

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.hardware.Camera
import android.util.Log
import android.view.Surface

object CameraUtil {


    @JvmStatic
    fun getParameters(camera: Camera): Camera.Parameters {
        val width = 1280
        val height = 720
        val parameters = camera.parameters
        //镜头缩放比例
        parameters.zoom = 2
        //自动对焦
        parameters.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO
        parameters.supportedPreviewSizes.forEach {
            Log.e("TAG", "getParameters: supportedPreviewSizes=[${it.width},${it.height}]")
        }
        //预览时大小
        parameters.setPreviewSize(width, height)
        parameters.supportedPreviewFpsRange.forEach {
            Log.e(
                "TAG",
                "getParameters: supportedPreviewFpsRange=[${it[0]},${it[1]}],${parameters.previewFormat}"
            )
        }
        //预览时每秒显示的帧数范围
        parameters.setPreviewFpsRange(7000, 30000)
        //图片格式
        parameters.pictureFormat = ImageFormat.JPEG
        parameters.jpegQuality = 100
        parameters.setPictureSize(width, height)

        return parameters
    }

    fun getDegree(activity: Activity): Int {
        // 获取当前屏幕旋转的角度
        val rotating = activity.windowManager.defaultDisplay.rotation
        var degree = 0// 度数
        // 根据手机旋转的角度，来设置surfaceView的显示的角度
        when (rotating) {
            Surface.ROTATION_0 ->
                degree = 90
            Surface.ROTATION_90 ->
                degree = 0
            Surface.ROTATION_180 ->
                degree = 270
            Surface.ROTATION_270 ->
                degree = 180
        }
        return degree;
    }


    fun rotateBitmap(origin: Bitmap?, degree: Float): Bitmap? {
        if (origin == null) {
            return null
        }
        val width = origin.width
        val height = origin.height
        val matrix = Matrix()
        matrix.setRotate(degree)
        // 围绕原地进行旋转
        val newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false)
        if (newBM == origin) {
            return newBM
        }
        origin.recycle()
        return newBM
    }
}