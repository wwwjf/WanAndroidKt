package com.wwwjf.videodemo

import android.graphics.*
import android.hardware.Camera
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.wwwjf.base.KLog
import java.io.ByteArrayOutputStream


class SurfaceViewActivity : AppCompatActivity(), SurfaceHolder.Callback {
    private lateinit var sv: SurfaceView
    private lateinit var iv: ImageView
    private lateinit var mCamera: Camera
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_surface_view)
        sv = findViewById(R.id.sv_activity_surface_view)
        iv = findViewById(R.id.iv_activity_surface_view)
        sv.holder.addCallback(this)

        mCamera = Camera.open(0)
        mCamera.parameters = CameraUtil.getParameters(mCamera)
        mCamera.setDisplayOrientation(90)

        mCamera.setPreviewCallback(object : Camera.PreviewCallback {
            override fun onPreviewFrame(data: ByteArray?, camera: Camera?) {
                val previewSize = camera?.parameters?.previewSize
                KLog.e("previewSize:${previewSize?.width},${previewSize?.height}")
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
                    iv.setImageBitmap(CameraUtil.rotateBitmap(bitmap, CameraUtil.getDegree(this@SurfaceViewActivity).toFloat()))
                }
            }
        })

    }

    override fun onStop() {
        super.onStop()

    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        mCamera.setPreviewDisplay(holder)
        mCamera.startPreview()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        mCamera.setPreviewCallback(null)
        mCamera.stopPreview()
        mCamera.release()
    }


}