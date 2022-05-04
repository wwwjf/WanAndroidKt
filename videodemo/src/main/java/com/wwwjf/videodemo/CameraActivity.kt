package com.wwwjf.videodemo

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.wwwjf.videodemo.databinding.ActivityCameraBinding
import java.io.File

class CameraActivity : AppCompatActivity() {

    private val TAG: String = CameraActivity::class.java.simpleName
    private lateinit var viewBinding: ActivityCameraBinding
    private lateinit var mImageUri: Uri
    private lateinit var outputImage: File
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        permissionCheck()
    }

    private fun permissionCheck() {

        val checkCameraPermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            Log.e(TAG, "checkCameraPermission: ${checkCameraPermission}")
        if (checkCameraPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1)
        }
        val checkReadPermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        Log.e(TAG, "checkReadPermission: ${checkReadPermission}")
        if (checkReadPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                2
            )
        }
        val checkWritePermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        Log.e(TAG, "checkWritePermission: ${checkWritePermission}")
        if (checkWritePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                3
            )
        }
    }

    fun takePhoto(view: View) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        val parentFile = File(application.externalMediaDirs[0], "externalMediaChild")
//        if (!parentFile.exists()){
//            parentFile.mkdir()
//        }
        val parentFile = externalCacheDir?.absolutePath
        Log.e(TAG, "takePhoto: ${parentFile}")
        outputImage = File("${parentFile}/camerademo.png")
        if (outputImage.exists()) {
            outputImage.delete()
        }
        outputImage.createNewFile()
        if (Build.VERSION.SDK_INT < 24) {
            Log.e(TAG, "takePhoto: <24")
            mImageUri = Uri.fromFile(outputImage)
        } else {
            Log.e(TAG, "takePhoto: >24")
            mImageUri = FileProvider.getUriForFile(this, "$packageName.fileprovider", outputImage)
        }

        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        val resolveActivity = intent.resolveActivity(packageManager)
        Log.e(TAG, "takePhoto: $resolveActivity")
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (RESULT_OK != resultCode) {
            return
        }
        if (requestCode == 100) {
            Log.e(TAG, "onActivityResult: ${data},${data?.hasExtra("data")}")
            viewBinding.ivActivityCamera.setImageURI(mImageUri)
            //添加到系统相册
            addToAlbum()
        }
    }

    private fun addToAlbum() {
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        MediaStore.Images.Media.insertImage(contentResolver,outputImage.absolutePath,"cameraimage",null)
        intent.setData(mImageUri)
        sendBroadcast(intent)
    }
}