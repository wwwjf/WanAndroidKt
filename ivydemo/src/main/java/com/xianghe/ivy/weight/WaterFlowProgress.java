package com.xianghe.ivy.weight;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.xianghe.ivy.R;
import com.xianghe.ivy.util.SystemUtil;
import com.xianghe.ivy.utils.LanguageUtil;

/**
 * author:  ycl
 * date:  2018/12/10 17:39
 * desc:
 */
public class WaterFlowProgress extends View {
    private Paint textPaint;
    private Paint.FontMetricsInt fontMetricsInt;
    private RectF rectF = new RectF();

    private float textSize;
    private int textColor;
    private int progress = 0;
    private int max;
    private int finishedColor;
    private int unfinishedColor;
    private String prefixText = "";
    private String suffixText = "%";
    private boolean start = false; // 控制背景是否显示
    private boolean pause = false;
    private boolean complete = false;
    private Bitmap pauseBitpap;
    private Bitmap completeBitpap;
    private Context mContext;
    private boolean isZH = false;

    private final int default_finished_color = Color.argb(127, 0, 0, 0);
    private final int default_unfinished_color = Color.argb(0, 0, 0, 0);
    private final int default_text_color = Color.WHITE;
    private final int default_max = 100;
    private final float default_text_size;
    private final float default_text_min_size;
    private final int min_size;

    private static final String INSTANCE_STATE = "saved_instance";
    private static final String INSTANCE_TEXT_COLOR = "text_color";
    private static final String INSTANCE_TEXT_SIZE = "text_size";
    private static final String INSTANCE_FINISHED_STROKE_COLOR = "finished_stroke_color";
    private static final String INSTANCE_UNFINISHED_STROKE_COLOR = "unfinished_stroke_color";
    private static final String INSTANCE_MAX = "max";
    private static final String INSTANCE_PROGRESS = "progress";
    private static final String INSTANCE_SUFFIX = "suffix";
    private static final String INSTANCE_PREFIX = "prefix";
    private static final String INSTANCE_START = "start";
    private static final String INSTANCE_PAUSE = "pause";
    private static final String INSTANCE_COMPLETE = "complete";
    private static final String INSTANCE_ISZH = "isZH";

    private Paint paint = new Paint();
    private Paint paintImg = new Paint();

    public WaterFlowProgress(Context context) {
        this(context, null);
    }

    public WaterFlowProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaterFlowProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        default_text_min_size = sp2px(getResources(), 7);
        default_text_size = sp2px(getResources(), 11);
        min_size = (int) dp2px(getResources(), 100);

        final TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.WaterFlowProgress, defStyleAttr, 0);
        initByAttributes(attributes);
        attributes.recycle();

        initPainters();
        isZH = LanguageUtil.isSimplifiedChinese(getContext());
    }

    protected void initByAttributes(TypedArray attributes) {
        finishedColor = attributes.getColor(R.styleable.WaterFlowProgress_wf_finished_color, default_finished_color);
        unfinishedColor = attributes.getColor(R.styleable.WaterFlowProgress_wf_unfinished_color, default_unfinished_color);
        textColor = attributes.getColor(R.styleable.WaterFlowProgress_wf_text_color, default_text_color);
        textSize = attributes.getDimension(R.styleable.WaterFlowProgress_wf_text_size, default_text_size);

        setMax(attributes.getInt(R.styleable.WaterFlowProgress_wf_max, default_max));
        setProgress(attributes.getInt(R.styleable.WaterFlowProgress_wf_progress, 0));

        if (attributes.getString(R.styleable.WaterFlowProgress_wf_prefix_text) != null) {
            setPrefixText(attributes.getString(R.styleable.WaterFlowProgress_wf_prefix_text));
        }
        if (attributes.getString(R.styleable.WaterFlowProgress_wf_suffix_text) != null) {
            setSuffixText(attributes.getString(R.styleable.WaterFlowProgress_wf_suffix_text));
        }
        Drawable pause_drawable = attributes.getDrawable(R.styleable.WaterFlowProgress_wf_pause_drawable);
        if (pause_drawable != null) {
            pauseBitpap = drawableToBitmap(pause_drawable);
        }
        Drawable complete_drawable = attributes.getDrawable(R.styleable.WaterFlowProgress_wf_complete_drawable);
        if (complete_drawable != null) {
            completeBitpap = drawableToBitmap(complete_drawable);
        }
    }

    protected void initPainters() {
        textPaint = new TextPaint();
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        textPaint.setAntiAlias(true);

        fontMetricsInt = textPaint.getFontMetricsInt();

        paint.setAntiAlias(true);
        paintImg.setAntiAlias(true);
    }

/*
    @Override
    public void invalidate() {
        initPainters();
        super.invalidate();
    }*/


    public boolean isStartVideo() {
        return start;
    }

    public void startVideo() {
        if (!start) {
            prefixText = mContext.getString(R.string.commom_transform);
            this.start = true;
        }
        
        // resume
        if (pause) {
            this.pause = false;
        }
    }

    public void completeVideo() {
        if (start) {
            prefixText = "";
            this.start = false;
        }
    }

    public boolean isPause() {
        return pause;
    }

    public void pause() {
        if (!pause) {
            this.pause = true;
            invalidate();
        }
    }

    public void resume() {
        if (pause) {
            this.pause = false;
            invalidate();
        }
    }


    public boolean isComplete() {
        return complete;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int p) {
        // 避免重复绘制
        if (progress == p) {
            return;
        }
        this.progress = p;
        if (this.progress > getMax()) {
            this.progress %= getMax();
        }
        // 暂停了就不绘制
        if (pause) {
            return;
        }
        // 允许重恢
        if (complete) {
            complete = false;
        }
        invalidate();
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        if (max > 0) {
            this.max = max;
            invalidate();
        }
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
        this.invalidate();
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        this.invalidate();
    }

    public int getFinishedColor() {
        return finishedColor;
    }

    public void setFinishedColor(int finishedColor) {
        this.finishedColor = finishedColor;
        this.invalidate();
    }

    public int getUnfinishedColor() {
        return unfinishedColor;
    }

    public void setUnfinishedColor(int unfinishedColor) {
        this.unfinishedColor = unfinishedColor;
        this.invalidate();
    }

    public String getPrefixText() {
        return prefixText;
    }

    public void setPrefixText(String prefixText) {
        this.prefixText = prefixText;
        this.invalidate();
    }

    public String getSuffixText() {
        return suffixText;
    }

    public void setSuffixText(String suffixText) {
        this.suffixText = suffixText;
        this.invalidate();
    }

    public String getDrawText() {
        return getProgress() + getSuffixText();
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return min_size;
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        return min_size;
    }

    public float getProgressPercentage() {
        return getProgress() / (float) getMax();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        rectF.set(0, 0, MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 暂停状态
        if (pause) {
            drawImage(canvas, pauseBitpap);
            return;
        }
        if (complete) {
            drawImage(canvas, completeBitpap);
            return;
        }
        if (!start) {
            float yHeight = getProgress() / (float) getMax() * getHeight();
            paint.setColor(getUnfinishedColor());
            canvas.drawRect(0, 0, rectF.right, getHeight() - yHeight, paint);

            paint.setColor(getFinishedColor());
            canvas.drawRect(0, getHeight() - yHeight, rectF.right, rectF.bottom, paint);
        }

        String preText = getPrefixText();
        if (!TextUtils.isEmpty(preText)) { // 转换中
            String text = getDrawText();
            if (!TextUtils.isEmpty(text)) {
                if (!isZH) { // 不是国语就缩小字体
                    textPaint.setTextSize(default_text_min_size);
                    fontMetricsInt = textPaint.getFontMetricsInt();
                }
                canvas.drawText(preText, (getWidth() - textPaint.measureText(preText)) / 2.0f, getWidth() / 2 - fontMetricsInt.descent, textPaint);
                if (!isZH) { // 不是国语就缩小字体
                    textPaint.setTextSize(textSize);
                    fontMetricsInt = textPaint.getFontMetricsInt();
                }
                canvas.drawText(text, (getWidth() - textPaint.measureText(text)) / 2.0f, getWidth() / 2 - fontMetricsInt.ascent, textPaint);
            }
        } else { // 仅仅上传中
            String text = getDrawText();
            if (!TextUtils.isEmpty(text)) {
                float textHeight = textPaint.descent() + textPaint.ascent();
                canvas.drawText(text, (getWidth() - textPaint.measureText(text)) / 2.0f, (getWidth() - textHeight) / 2.0f, textPaint);
            }
        }

        // start 到了100页不需要显示打钩
        if (!start && getProgress() == getMax()) {
            complete = true;
            invalidate();
        }
    }

    private void drawImage(Canvas canvas, Bitmap b) {
        if (b != null && !b.isRecycled()) {
            paint.setColor(getFinishedColor());
            canvas.drawRect(0, 0, rectF.right, rectF.bottom, paint);

            canvas.save();
            canvas.translate((getWidth() - b.getWidth()) / 2, (getHeight() - b.getHeight()) / 2);
            canvas.drawBitmap(b, 0, 0, paintImg);
            canvas.restore();
        }
    }

    private Bitmap drawableToBitmap(Drawable drawable) // drawable 转换成bitmap
    {
        int width = drawable.getIntrinsicWidth();// 取drawable的长宽
        int height = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;// 取drawable的颜色格式
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);// 建立对应bitmap
        Canvas canvas = new Canvas(bitmap);// 建立对应bitmap的画布
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);// 把drawable内容画到画布中
        return bitmap;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putInt(INSTANCE_TEXT_COLOR, getTextColor());
        bundle.putFloat(INSTANCE_TEXT_SIZE, getTextSize());
        bundle.putInt(INSTANCE_FINISHED_STROKE_COLOR, getFinishedColor());
        bundle.putInt(INSTANCE_UNFINISHED_STROKE_COLOR, getUnfinishedColor());
        bundle.putInt(INSTANCE_MAX, getMax());
        bundle.putInt(INSTANCE_PROGRESS, getProgress());
        bundle.putString(INSTANCE_SUFFIX, getSuffixText());
        bundle.putString(INSTANCE_PREFIX, getPrefixText());
        bundle.putBoolean(INSTANCE_START, isStartVideo());
        bundle.putBoolean(INSTANCE_PAUSE, isPause());
        bundle.putBoolean(INSTANCE_COMPLETE, isComplete());
        bundle.putBoolean(INSTANCE_ISZH, isZH);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            textColor = bundle.getInt(INSTANCE_TEXT_COLOR);
            textSize = bundle.getFloat(INSTANCE_TEXT_SIZE);
            finishedColor = bundle.getInt(INSTANCE_FINISHED_STROKE_COLOR);
            unfinishedColor = bundle.getInt(INSTANCE_UNFINISHED_STROKE_COLOR);
            initPainters();
            setMax(bundle.getInt(INSTANCE_MAX));
            setProgress(bundle.getInt(INSTANCE_PROGRESS));
            prefixText = bundle.getString(INSTANCE_PREFIX);
            suffixText = bundle.getString(INSTANCE_SUFFIX);
            start = bundle.getBoolean(INSTANCE_START);
            pause = bundle.getBoolean(INSTANCE_PAUSE);
            complete = bundle.getBoolean(INSTANCE_COMPLETE);
            isZH = bundle.getBoolean(INSTANCE_ISZH);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    public float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public float sp2px(Resources resources, float sp) {
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    public void destroy() {
        if (pauseBitpap != null && !pauseBitpap.isRecycled()) {
            pauseBitpap.recycle();
        }
        if (completeBitpap != null && !completeBitpap.isRecycled()) {
            completeBitpap.recycle();
        }
    }
}
