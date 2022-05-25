package com.xianghe.ivy.weight;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xianghe.ivy.R;

/**
 * github -> https://github.com/fangzhengo500/PicReflectionDemo
 * by LooSu
 */
public class ReflectLayout extends FrameLayout {
    private static final String TAG = "ReflectLayout";

    public ReflectLayout(@NonNull Context context) {
        this(context, null);
    }

    public ReflectLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReflectLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            if (childAt.getVisibility() != View.VISIBLE) {
                continue;
            }

            ViewGroup.LayoutParams params = childAt.getLayoutParams();
            if (params instanceof LayoutParams) {
                LayoutParams layoutParams = (LayoutParams) params;
                boolean reflect = layoutParams.reflectAble;         // 开启阴影效果
                float reflectHeight = layoutParams.reflectHeight;   // 阴影高度
                float reflectSpace = layoutParams.reflectSpace;     // 阴影间隔
                int startAlpha = (int) Math.max(0, Math.min(layoutParams.reflectStartAlpha * 255, 255));    // 阴影起始透明度
                int endAlpha = (int) Math.max(0, Math.min(layoutParams.reflectEndAlpha * 255, 255));        // 阴影结束透明度

                float viewLeft = childAt.getLeft() + childAt.getTranslationX();         // view 的视图位置 left
                float viewBottom = childAt.getBottom() + childAt.getTranslationY();     // view 的视图位置 bottom

                if (reflect) {
                    // 启用阴影效果

                    // 计算实际的阴影高度
                    int shadowHeight = (int) reflectHeight;
                    if (reflectHeight == LayoutParams.MATCH_PARENT) {
                        shadowHeight = (int) (getHeight() - getPaddingBottom() - (viewBottom + reflectSpace));
                    }

                    // 绘制阴影效果
                    Bitmap source = getBitmapByView(childAt);
                    if (source != null && !source.isRecycled()) {
                        Bitmap shadow = createBitmapShadow(source, shadowHeight, startAlpha, endAlpha);
                        if (shadow != null) {
                            canvas.drawBitmap(shadow, viewLeft, viewBottom + reflectSpace, null);
                        }
                    }
                } else {
                    // do nothing
                }
            }
        }
    }

    @Override
    protected ReflectLayout.LayoutParams generateDefaultLayoutParams() {
        return new ReflectLayout.LayoutParams(ReflectLayout.LayoutParams.MATCH_PARENT, ReflectLayout.LayoutParams.MATCH_PARENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        return new ReflectLayout.LayoutParams(lp);
    }

    @Override
    public ReflectLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new ReflectLayout.LayoutParams(getContext(), attrs);
    }

    public static class LayoutParams extends FrameLayout.LayoutParams {
        private static final float DEFAULT_START_ALPHA = .95f;

        public boolean reflectAble = false;
        public float reflectHeight = 0;
        private float reflectSpace = 0;
        private float reflectStartAlpha = DEFAULT_START_ALPHA;
        private float reflectEndAlpha = 0f;

        public LayoutParams(@NonNull Context c, @Nullable AttributeSet attrs) {
            super(c, attrs);

            final TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.ReflectLayout_Layout);
            reflectAble = a.getBoolean(R.styleable.ReflectLayout_Layout_reflect, false);

            /*
             *  <attr name="reflect_height" format="reference|dimension">
             *      <flag name="match_parent" value="-1" />
             *  </attr>
             *  因为 match_parent = -1， 为 integer, 直接使用 TypedArray.getDimension() 会crash.
             *  这里使用 TypedArray.getLayoutDimension() 获取 reflectHeight.
             */
            reflectHeight = a.getLayoutDimension(R.styleable.ReflectLayout_Layout_reflect_height, MATCH_PARENT);
            reflectSpace = a.getDimension(R.styleable.ReflectLayout_Layout_reflect_space, 0);
            reflectStartAlpha = a.getFloat(R.styleable.ReflectLayout_Layout_reflect_start_alpha, DEFAULT_START_ALPHA);
            reflectEndAlpha = a.getFloat(R.styleable.ReflectLayout_Layout_reflect_end_alpha, 0);
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int width, int height, int gravity) {
            super(width, height, gravity);
        }

        public LayoutParams(@NonNull ViewGroup.LayoutParams source) {
            super(source);
        }
    }

    /**
     * 获取view的图像
     *
     * @param view view
     * @return bitmap
     */
    @Nullable
    protected Bitmap getBitmapByView(View view) {
        Bitmap result = null;
        try {
            view.setDrawingCacheEnabled(true);
            Bitmap drawingCache = view.getDrawingCache();
            if (drawingCache != null && !drawingCache.isRecycled()) {
                result = drawingCache.copy(Bitmap.Config.ARGB_8888, true);
                view.setDrawingCacheEnabled(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
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
    private Bitmap createBitmapShadow(@Nullable Bitmap sourceBmp, int shadowHeight, int startAlpha, int endAlpha) {
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
}
