package com.wwwjf.videodemo

import android.Manifest
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Criteria
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.MediaController
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

    fun openAlbum(view: View) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(intent, 101)
    }

    fun takeVideo(view: View){
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        startActivityForResult(intent,102)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (RESULT_OK != resultCode) {
            return
        }
        Log.e(TAG, "onActivityResult: ${data},${data?.hasExtra("data")},${data?.data}")
        if (requestCode == 100) {
            viewBinding.ivActivityCamera.visibility = View.VISIBLE
            viewBinding.videoActivityCamera.visibility = View.GONE
//            viewBinding.ivActivityCamera.setImageURI(mImageUri)
            cropPicture(mImageUri)
            //添加到系统相册
            addToAlbum()
        } else if (requestCode == 101) {
            viewBinding.ivActivityCamera.visibility = View.VISIBLE
            viewBinding.videoActivityCamera.visibility = View.GONE
            //content://com.android.providers.media.documents/document/image%3A846379
            val uri = data?.data
            val path = getAbsolutePath(uri)
            viewBinding.ivActivityCamera.setImageURI(uri)

        } else if (requestCode == 102){
            viewBinding.ivActivityCamera.visibility = View.GONE
            viewBinding.videoActivityCamera.visibility = View.VISIBLE
            val uri = data?.data
            viewBinding.videoActivityCamera.setVideoURI(uri)
            viewBinding.videoActivityCamera.requestFocus()
            viewBinding.videoActivityCamera.setOnPreparedListener {
                it.isLooping = true
            }
            viewBinding.videoActivityCamera.start()
            val mediaController = MediaController(this)
            viewBinding.videoActivityCamera.setMediaController(mediaController)
            mediaController.setMediaPlayer(viewBinding.videoActivityCamera)
            mediaController.show()

        }
    }

    private fun getAbsolutePath(uri: Uri?): String {
        if (uri == null) {
            return ""
        }
        var selection:String
        var path =""
        Log.e(TAG, "getAbsolutePath: isAbsolute=${uri.isAbsolute},isHierarchical=${uri.isHierarchical},isOpaque=${uri.isOpaque},isRelative=${uri.isRelative}")
        Log.e(TAG, "getAbsolutePath: path=${uri.path}")
        Log.e(TAG, "getAbsolutePath: encodePath=${uri.encodedPath}")
        Log.e(TAG, "getAbsolutePath: pathSegments=${uri.pathSegments}")
        Log.e(TAG, "getAbsolutePath: lastPathSegment=${uri.lastPathSegment}")
        if (DocumentsContract.isDocumentUri(this,uri)){
            val documentId = DocumentsContract.getDocumentId(uri)
            Log.e(TAG, "getAbsolutePath: documentId=$documentId")
            if ("com.android.providers.media.documents".equals(uri.authority)){
                selection = "${MediaStore.Images.Media._ID}=${documentId.split(":")[1]}"
                path = queryPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection)
            } else if ("com.android.providers.downloads.documents".equals(uri.authority)){
                val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                documentId.toLong())
                path = queryPath(contentUri,null)
            }

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && DocumentsContract.isRootUri(this,uri)){
            Log.e(TAG, "getAbsolutePath: rootUri")
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && DocumentsContract.isRootsUri(this,uri)){
            Log.e(TAG, "getAbsolutePath: rootsUri")

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && DocumentsContract.isTreeUri(uri)){
            Log.e(TAG, "getAbsolutePath: treeUri")

        }else if ("content".equals(uri.scheme)){

            // content://media/external_primary/images/media/844444
            // content://com.oneplus.filemanager/storage/emulated/0/Pictures/Screenshots/Screenshot_20220224-201816.jpg
            path = queryPath(uri,null)

        }else if ("file".equals(uri.scheme)){
            path = uri.path.toString()
        }

        return path
    }

    fun queryPath(uri: Uri,selection:String?):String{

        val cursor = contentResolver.query(uri, null, selection, null, null)
        var path = ""///storage/emulated/0/Pictures/3.png
        if (cursor?.moveToFirst() == true) {
            try {
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                path = cursor.getString(columnIndex)
            } catch (e:Exception){
                e.printStackTrace()
                Log.e(TAG, "queryPath: exception")
            } finally {
                cursor.close()
            }
        }
        Log.e(TAG, "getImagePath: $path")
        return path
    }

    private fun addToAlbum() {
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        MediaStore.Images.Media.insertImage(
            contentResolver,
            outputImage.absolutePath,
            "cameraimage",
            null
        )
        intent.setData(mImageUri)
        sendBroadcast(intent)
    }


    /**
     * 剪裁图片
     */
    private fun cropPicture(imageUri: Uri) {
        val width = viewBinding.ivActivityCamera.width
        val height = viewBinding.ivActivityCamera.height
        Log.e(TAG, "cropPicture: width=$width,height=$height")
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(outputImage.absolutePath,options)

        val pictureW = options.outWidth
        val pictureH = options.outHeight
        options.inJustDecodeBounds = false
        val scale = Math.min(pictureW/width,pictureH/height)
        Log.e(TAG, "cropPicture: pictureW=$pictureW,pictureH=$pictureH,scale=$scale")
        options.inSampleSize =scale

        val bitmap = BitmapFactory.decodeFile(outputImage.absolutePath,options)
        Log.e(TAG, "cropPicture: bitmap size=${bitmap.byteCount}")//40108032  1602540
        viewBinding.ivActivityCamera.setImageBitmap(bitmap)
    }

}