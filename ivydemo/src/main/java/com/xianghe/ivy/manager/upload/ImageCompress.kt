package com.xianghe.ivy.manager.upload

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.xianghe.ivy.utils.KLog
import java.io.ByteArrayOutputStream

/**
 * author:  ycl
 * date:  2018/11/13 9:45
 * desc:
 */
object ImageCompress {
    @JvmField
    val MAX_VALUE = 1024
    @JvmField
    val MAX_SIZE = 200 * 1024//最大200kb

    fun compressScale(path: String, maxOut: Int, maxSize: Int): ByteArray {
        val opts = BitmapFactory.Options()
        opts.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, opts)
        opts.inJustDecodeBounds = false
        var width = opts.outWidth
        var height = opts.outHeight
        val scale = Math.max(1, Math.max(width, height) / maxOut)
        opts.inSampleSize = scale
        KLog.i(" startScale   width $width  height $height  scale $scale")
        val bitmap = BitmapFactory.decodeFile(path, opts)
        width = bitmap.width
        height = bitmap.height
        KLog.i(" endScale  width ${width}  height ${height}")
        val boas = ByteArrayOutputStream()
        var quality = 100
        do {
            boas.reset()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, boas)
            quality -= 10
        } while (boas.size() > maxSize)
        val arrays = boas.toByteArray()
        boas.close()
        bitmap.recycle()
        return arrays
    }
}