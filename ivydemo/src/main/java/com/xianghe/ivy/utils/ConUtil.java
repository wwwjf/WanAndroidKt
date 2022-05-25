package com.xianghe.ivy.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.xianghe.ivy.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class ConUtil {

    public static String getUUIDString(Context mContext) {
        return UUID.randomUUID().toString();
    }

    /**
     * 输出toast
     */
    public static void showToast(Context context, String str) {
        if (context != null) {
            Toast toast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
            // 可以控制toast显示的位置
            toast.setGravity(Gravity.TOP, 0, 30);
            toast.show();
        }
    }

    /**
     * 保存bitmap至指定Picture文件夹
     */
    public static String saveBitmap(Context mContext, Bitmap bitmaptosave, String name) {
        if (bitmaptosave == null)
            return null;

        File mediaStorageDir = mContext.getExternalFilesDir("megvii");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String bitmapFileName = name + System.currentTimeMillis() + "";
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mediaStorageDir + "/" + bitmapFileName);
            boolean successful = bitmaptosave.compress(Bitmap.CompressFormat.JPEG, 75, fos);

            if (successful)
                return mediaStorageDir.getAbsolutePath() + "/" + bitmapFileName;
            else
                return null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 保存bitmap至指定Picture文件夹
     */
    public static boolean saveBitmap(Bitmap bitmaptosave, String path) {
        if (bitmaptosave == null)
            return false;

        File mediaStorageDir = new File(path);

        if (mediaStorageDir.exists() && !mediaStorageDir.delete()) {
            return false;
        }
        FileOutputStream fos = null;
        boolean result = false;
        try {
            fos = new FileOutputStream(mediaStorageDir);
            result = bitmaptosave.compress(Bitmap.CompressFormat.JPEG, 75, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 切图
     */
    public static Bitmap cutImage(RectF rect, byte[] data, Camera camera, boolean mIsVertical) {
        byte[] imageData = data;
        Camera.Parameters parameters = camera.getParameters();
        int width = parameters.getPreviewSize().width;
        int height = parameters.getPreviewSize().height;
        if (mIsVertical) {
            imageData = RotaterUtil.rotate(data, width, height, 90);
            width = parameters.getPreviewSize().height;
            height = parameters.getPreviewSize().width;
        }

        YuvImage yuv = new YuvImage(imageData, parameters.getPreviewFormat(), width, height, null);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuv.compressToJpeg(new Rect(0, 0, width, height), 50, out);

        byte[] bytes = out.toByteArray();
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        rect = new RectF(rect.left * bitmap.getWidth(), rect.top * bitmap.getHeight(), rect.right * bitmap.getWidth(),
                rect.bottom * bitmap.getHeight());
        return cropImage(rect, bitmap);
    }

    /**
     * 切图
     */
    public static Bitmap cropImage(RectF rect, Bitmap bitmap) {
        Log.w("ceshi", "rect===" + rect);
        float width = rect.width() * 1;
        if (width > bitmap.getWidth()) {
            width = bitmap.getWidth();
        }

        float hight = rect.height() * 1;
        if (hight > bitmap.getHeight()) {
            hight = bitmap.getHeight();
        }

        float l = rect.centerX() - (width / 2);
        if (l < 0) {
            l = 0;
        }
        float t = rect.centerY() - (hight / 2);
        if (t < 0) {
            t = 0;
        }
        if (l + width > bitmap.getWidth()) {
            width = bitmap.getWidth() - l;
        }
        if (t + hight > bitmap.getHeight()) {
            hight = bitmap.getHeight() - t;
        }

        return Bitmap.createBitmap(bitmap, (int) l, (int) t, (int) width, (int) hight);

    }

    public static byte[] readModel(Context context) {
        InputStream inputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int count = -1;
        try {
            inputStream = context.getResources().openRawResource(R.raw.megviiidcard_0_3_0_model);
            while ((count = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, count);
            }
            byteArrayOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return byteArrayOutputStream.toByteArray();
    }

}
