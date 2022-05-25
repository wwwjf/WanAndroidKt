package com.xianghe.ivy.ui.module.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;

import java.security.MessageDigest;

import jp.wasabeef.glide.transformations.BitmapTransformation;

public class ReflectTransformation extends BitmapTransformation {
    private static final int VERSION = 1;
    private static final String ID = "com.xianghe.ivy.ui.module.main.ReflectTransformation." + VERSION;


    private int width;
    private int height;

    public ReflectTransformation(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    protected Bitmap transform(Context context, BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        width = width == 0 ? toTransform.getWidth() : width;
        height = height == 0 ? toTransform.getHeight() : height;

        Bitmap.Config config =
                toTransform.getConfig() != null ? toTransform.getConfig() : Bitmap.Config.ARGB_8888;
        Bitmap bitmap = pool.get(width, height, config);

        bitmap.setHasAlpha(true);

        Matrix matrix = new Matrix();
        matrix.setScale(1, -1);  // 矩阵 matrix.setScale(1,-1) 上下翻转.

        Canvas canvas = new Canvas(bitmap);

        canvas.drawBitmap(toTransform,0,0,null );

        LinearGradient shader = new LinearGradient(0, 0, 0, bitmap.getHeight(),
                Color.argb(1, 0, 0, 0),
                Color.argb(0, 0, 0, 0),
                Shader.TileMode.MIRROR);
        Paint paint = new Paint();
        paint.setShader(shader);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

        canvas.drawRect(canvas.getClipBounds(), paint);

        return bitmap;
    }

    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) {
        messageDigest.update((ID + width + height).getBytes(CHARSET));
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public int hashCode() {
        return ID.hashCode() + width * 100000 + height * 1000;
    }
}
