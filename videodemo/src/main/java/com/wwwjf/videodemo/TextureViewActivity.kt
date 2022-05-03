package com.wwwjf.videodemo

import android.graphics.*
import android.hardware.Camera
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.TextureView
import android.widget.ImageView
import java.io.ByteArrayOutputStream

class TextureViewActivity : AppCompatActivity(), TextureView.SurfaceTextureListener {

    lateinit var tv:TextureView
    lateinit var iv:ImageView
    lateinit var mCamera:Camera
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_texture_view)

        tv = findViewById(R.id.tv_activity_texture_view)
        iv = findViewById(R.id.iv_activity_texture_view)
        tv.surfaceTextureListener = this

        mCamera = Camera.open(0)
        mCamera.setDisplayOrientation(90)
        mCamera.parameters = CameraUtil.getParameters(mCamera)

        mCamera.setPreviewCallback(object : Camera.PreviewCallback {
            override fun onPreviewFrame(data: ByteArray?, camera: Camera?) {
                val previewSize = camera?.parameters?.previewSize
                previewSize?.let {
                    val yuvImage = YuvImage(
                        data,
                        ImageFormat.NV21,
                        previewSize.width,
                        previewSize.height,
                        null
                    )
                    val bos = ByteArrayOutputStream()
                    yuvImage.compressToJpeg(
                        Rect(0, 0, previewSize.width, previewSize.height),
                        100,
                        bos
                    )
                    val imageBytes = bos.toByteArray()
                    val options = BitmapFactory.Options()
                    options.inPreferredConfig = Bitmap.Config.RGB_565

                    val bitmap =
                        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size, options)
                    iv.setImageBitmap(CameraUtil.rotateBitmap(bitmap, CameraUtil.getDegree(this@TextureViewActivity).toFloat()))
                }
            }
        })

    }


    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        mCamera.setPreviewTexture(surface)
        mCamera.startPreview()
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {

    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        mCamera.setPreviewCallback(null)
        mCamera.stopPreview()
        mCamera.release()
        return false
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
    }
}