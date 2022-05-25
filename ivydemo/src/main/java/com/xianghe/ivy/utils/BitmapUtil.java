package com.xianghe.ivy.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.widget.ImageView;

import androidx.annotation.Nullable;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * <b>@项目名：</b> FinanceExpert<br>
 * <b>@创建者：</b> 陆先俊<br>
 * <b>@创建时间：</b> 2017/10/17 17:43<br>
 * <b>@公司：</b> 深圳市收银家信息技术有限公司<br>
 * <b>@邮箱：</b> CarlLu0712@163.com<br>
 * <p/>
 * <b>@描述</b> ：图片转字节的工具类<br>
 */

public class BitmapUtil {
    /**
     * 图片按比例大小压缩方法
     *
     * @param srcPath （根据路径获取图片并压缩）
     */
    public static Bitmap getimage(String srcPath, float height, float width) {
        try {
            return BitmapFactory.decodeFile(srcPath);
        } catch (OutOfMemoryError error) {
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
            newOpts.inJustDecodeBounds = true;
            Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

            newOpts.inJustDecodeBounds = false;
            int w = newOpts.outWidth;
            int h = newOpts.outHeight;
            // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
            float hh = height;// 这里设置高度为800f
            float ww = width;// 这里设置宽度为480f
            // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
            int be = 1;// be=1表示不缩放
            if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
                be = (int) (newOpts.outWidth / ww);
            } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
                be = (int) (newOpts.outHeight / hh);
            }
            if (be <= 0)
                be = 1;
            newOpts.inSampleSize = be;// 设置缩放比例
            // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
            bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
            return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
        }
    }

    /**
     * 图片按比例大小压缩方法
     *
     * @param srcPath （根据路径获取图片并压缩）
     */
    public static byte[] getimageByte(String srcPath) {
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(srcPath);
            return compressImageByte(bitmap);
        } catch (OutOfMemoryError error) {
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
            newOpts.inJustDecodeBounds = true;
            Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

            newOpts.inJustDecodeBounds = false;
            int w = newOpts.outWidth;
            int h = newOpts.outHeight;
            // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
            float hh = 800f;// 这里设置高度为800f
            float ww = 480f;// 这里设置宽度为480f
            // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
            int be = 1;// be=1表示不缩放
            if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
                be = (int) (newOpts.outWidth / ww);
            } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
                be = (int) (newOpts.outHeight / hh);
            }
            if (be <= 0)
                be = 1;
            newOpts.inSampleSize = be;// 设置缩放比例
            // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
            bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
            return compressImageByte(bitmap);// 压缩好比例大小后再进行质量压缩
        }
    }

    /**
     * 图片按比例大小压缩方法
     */
    public static Bitmap getimageByByte(byte[] imageArray, float height, float width) {
        try {
            return BitmapFactory.decodeByteArray(imageArray, 0, imageArray.length);
        } catch (OutOfMemoryError error) {
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
            newOpts.inJustDecodeBounds = true;
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageArray, 0, imageArray.length);// 此时返回bm为空

            newOpts.inJustDecodeBounds = false;
            int w = newOpts.outWidth;
            int h = newOpts.outHeight;
            // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
            float hh = height;// 这里设置高度为800f
            float ww = width;// 这里设置宽度为480f
            // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
            int be = 1;// be=1表示不缩放
            if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
                be = (int) (newOpts.outWidth / ww);
            } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
                be = (int) (newOpts.outHeight / hh);
            }
            if (be <= 0)
                be = 1;
            newOpts.inSampleSize = be;// 设置缩放比例
            // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
            bitmap = BitmapFactory.decodeByteArray(imageArray, 0, imageArray.length);
            return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
        }
    }

    /**
     * 图片按比例大小压缩方法
     */
    public static void saveImageToFile(Bitmap srcBitmap, File fileName) throws IOException {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        srcBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageArray = baos.toByteArray();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageArray, 0, imageArray.length);// 此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 720;// 这里设置高度为800f
        float ww = 1280;// 这里设置宽度为480f
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeByteArray(imageArray, 0, imageArray.length);
        compressImage(bitmap, fileName);
    }

    private static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 90;
        while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset(); // 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
            if (options == 40) {
                break;
            }
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        return BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
    }

    private static void compressImage(Bitmap image, File file) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FileOutputStream out = new FileOutputStream(file);
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 90;
        while (baos.toByteArray().length / 1024 > 50) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset(); // 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
            if (options == 40) {
                break;
            }
        }
        baos.writeTo(out);
        baos.flush();
        baos.close();
    }

    public static byte[] compressImageByte(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 90;
        while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset(); // 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
            if (options == 40) {
                break;
            }
        }
        return baos.toByteArray();// 把ByteArrayInputStream数据生成图片
    }

    /**
     * 生成倒影图片
     *
     * @param resId   源文件
     * @param context 上下文
     * @return
     */
    public static Bitmap getReverseBitmapById(int resId, Context context) {
        //创建资源Bitmap
        Bitmap sourceBitmap = BitmapFactory.decodeResource(context.getResources(), resId);
        //绘制原图的下一半图片
        Matrix matrix = new Matrix();
        //倒影翻转
        matrix.setScale(1, -1);
        //创建反方向Bitmap
        Bitmap inverseBitmap = Bitmap.createBitmap(sourceBitmap, 0, sourceBitmap.getHeight() / 2, sourceBitmap.getWidth(), sourceBitmap.getHeight() / 3, matrix, false);
        //合成图片
        Bitmap groupbBitmap = Bitmap.createBitmap(sourceBitmap.getWidth(), sourceBitmap.getHeight() + sourceBitmap.getHeight() / 3 + 60, sourceBitmap.getConfig());
        //以合成图片为画布
        Canvas gCanvas = new Canvas(groupbBitmap);
        //将原图和倒影图片画在合成图片上
        gCanvas.drawBitmap(sourceBitmap, 0, 0, null);
        gCanvas.drawBitmap(inverseBitmap, 0, sourceBitmap.getHeight() + 50, null);
        //添加遮罩
        Paint paint = new Paint();
        Shader.TileMode tileMode = Shader.TileMode.CLAMP;
        LinearGradient shader = new LinearGradient(0, sourceBitmap.getHeight() + 50, 0,
                groupbBitmap.getHeight(), Color.BLACK, Color.TRANSPARENT, tileMode);
        paint.setShader(shader);
        //这里取矩形渐变区和图片的交集
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        gCanvas.drawRect(0, sourceBitmap.getHeight() + 50, sourceBitmap.getWidth(), groupbBitmap.getHeight(), paint);
        return groupbBitmap;
    }

    public static Bitmap getReverseBitmapbyDrawable(Bitmap sourceBitmap) {
        //绘制原图的下一半图片
        Matrix matrix = new Matrix();
        //倒影翻转
        matrix.setScale(1, -1);
        //创建反方向Bitmap
        Bitmap inverseBitmap = Bitmap.createBitmap(sourceBitmap, 0, sourceBitmap.getHeight() / 2, sourceBitmap.getWidth(), sourceBitmap.getHeight() / 3, matrix, false);
        //合成图片
        Bitmap groupbBitmap = Bitmap.createBitmap(sourceBitmap.getWidth(), sourceBitmap.getHeight() + sourceBitmap.getHeight() / 3 + 60, sourceBitmap.getConfig());
        //以合成图片为画布
        Canvas gCanvas = new Canvas(groupbBitmap);
        //将原图和倒影图片画在合成图片上
        gCanvas.drawBitmap(sourceBitmap, 0, 0, null);
        gCanvas.drawBitmap(inverseBitmap, 0, sourceBitmap.getHeight() + 50, null);
        //添加遮罩
        Paint paint = new Paint();
        Shader.TileMode tileMode = Shader.TileMode.CLAMP;
        LinearGradient shader = new LinearGradient(0, sourceBitmap.getHeight() + 50, 0,
                groupbBitmap.getHeight(), Color.BLACK, Color.TRANSPARENT, tileMode);
        paint.setShader(shader);
        //这里取矩形渐变区和图片的交集
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        gCanvas.drawRect(0, sourceBitmap.getHeight() + 50, sourceBitmap.getWidth(), groupbBitmap.getHeight(), paint);
        return groupbBitmap;
    }

    /**
     * 获取view的图像
     *
     * @param view view
     * @return bitmap
     */
    @Nullable
    public static Bitmap getBitmapByView(ImageView view) {
        view.setDrawingCacheEnabled(true);
        Bitmap bmp=view.getDrawingCache();
        if(bmp == null) {
            view.setDrawingCacheEnabled(false);
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(bmp);
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public static Bitmap drawable2Bitmap(Drawable drawable,int width,int height) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof NinePatchDrawable) {
            Bitmap bitmap = Bitmap
                    .createBitmap(width,height,
                            drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                    : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, width,height);
            drawable.draw(canvas);
            return bitmap;
        } else {
            return null;
        }
    }

    /**
     * 创建图片的阴影
     *
     * @param sourceBmp    原图
     * @param shadowHeight 阴影高度
     * @param startAlpha   Alpha component \([0..255]\) of the color
     * @param endAlpha     Alpha component \([0..255]\) of the color
     * @return 阴影图片
     */
    @Nullable
    public static Bitmap createBitmapShadow(@Nullable Bitmap sourceBmp, int shadowHeight, int startAlpha, int endAlpha) {
        if (sourceBmp == null) {
            return null;
        }
        int x = 0;
        int y = Math.max(0, Math.min(sourceBmp.getHeight() - shadowHeight, sourceBmp.getHeight())); // 0 <= y <= bmp.getHeight;
        int width = sourceBmp.getWidth();
        int height = Math.min(shadowHeight, sourceBmp.getHeight() - y);

        if (height <= 0) {
            return null;
        }

        Matrix matrix = new Matrix();
        matrix.setScale(1, -1);  // 矩阵 matrix.setScale(1,-1) 上下翻转.
        Bitmap shadowBmp = Bitmap.createBitmap(sourceBmp, x, y, width, height, matrix, false);

        LinearGradient shader = new LinearGradient(0, 0, 0, shadowBmp.getHeight(),
                Color.argb(startAlpha, 0, 0, 0),
                Color.argb(endAlpha, 0, 0, 0),
                Shader.TileMode.MIRROR);
        Paint paint = new Paint();
        paint.setShader(shader);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

        Canvas canvas = new Canvas(shadowBmp);
        canvas.drawRect(canvas.getClipBounds(), paint);

        return shadowBmp;
    }


    /**
     * 在二维码中间添加Logo图案
     */
    private static Bitmap addLogo(Bitmap src, Bitmap logo) {
        if (src == null) {
            return null;
        }

        if (logo == null) {
            return src;
        }

        //获取图片的宽高
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        int logoWidth = logo.getWidth();
        int logoHeight = logo.getHeight();

        Bitmap circleBitmap = formatBitmap(logo);

        if (srcWidth == 0 || srcHeight == 0) {
            return null;
        }

        if (logoWidth == 0 || logoHeight == 0) {
            return src;
        }

        //logo大小为二维码整体大小的1/5
        float scaleFactor = srcWidth * 1.0f / 4 / logoWidth;
        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        //创建一个paint
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(src, 0, 0, null);
            canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
            canvas.drawBitmap(circleBitmap, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);

            canvas.save();
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }

        return bitmap;
    }

    /**
     * 实现圆形图片处理
     */
    public static Bitmap formatBitmap(Bitmap bitmap) {
        // 图片的宽度
        int width = bitmap.getWidth();
        // 图片的高度
        int height = bitmap.getHeight();
        int r = width > height ? height : width;
        // 创建一个画布的背景
        Bitmap backBitmap = Bitmap
                .createBitmap(width, height, Bitmap.Config.ARGB_8888);
        //新建一个画布
        Canvas canvas=new Canvas(backBitmap);
        //创建画笔
        Paint paint=new Paint();
        paint.setAntiAlias(true);

        RectF rectF=new RectF(0, 0, r, r);
        //先画圆
        canvas.drawRoundRect(rectF, r/2, r/2, paint);

        //设置两幅图相交时的一个画笔的处理模式
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        //画头像
        canvas.drawBitmap(bitmap, null, rectF, paint);
        return backBitmap;
    }
}
