package com.youku.rrtoyewx.barragelibrary;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.ImageSpan;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

/**
 * Created by Rrtoyewx on 16/7/24.
 * You can use BaseBarrageItem to draw you barrage
 * you want to set some properties by the constructors,
 * such as color, size, display information, speed, and acceleration
 */
public abstract class BaseBarrageItem {
    public static final String DEFAULT_CONTENT_STRING = "this is a default barrage message";
    public static final int DEFAULT_TEXT_SIZE = 50;
    public static final int DEFAULT_TEXT_COLOR = Color.BLUE;
    public static final int DEFAULT_SPEED = 5;
    public static final Interpolator DEFAULT_INTERPOLATOR = new LinearInterpolator();
    public static final int DEFAULT_PADDING_SIZE = 20;

    protected Context context;

    protected int color;
    protected int textSize;
    protected TextPaint textPaint;

    protected String contentStr;
    protected SpannableString contentSpanStr;
    private int imageRsd;

    protected int bgRsd;
    protected Paint bgPaint;
    protected Drawable bgDrawable;
    protected int paddingSize;

    protected StaticLayout textStaticLayout;

    protected Rect textRect;
    protected int textStrWidth;
    protected int textStrHeight;

    protected int startXAxes;
    protected int startYAxes;
    protected int endXAxes;
    protected int endYAxes;
    protected long precomputedTime;
    protected long startTime;

    protected int currentXAxes;
    protected int currentYAxes;

    protected int speed;
    protected Interpolator accelerationFactor;


    {
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setStyle(Paint.Style.FILL);
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        textRect = new Rect();
    }

    protected BaseBarrageItem(Context context, @DrawableRes int imageRsd, @DrawableRes int bgRsd, int paddingSize, String contentStr, int color, int textSize, int speed, Interpolator interpolator) {
        this.context = context;
        this.accelerationFactor = interpolator;
        this.color = color;
        this.contentStr = contentStr;
        this.textSize = textSize;
        this.speed = speed;
        this.bgRsd = bgRsd;
        this.imageRsd = imageRsd;
        this.paddingSize = paddingSize;

        init();
    }

    private void init() {
        initPaint();
        initTextInformation();
        initTextStaticLayout();
        initBackgroundDrawable();
    }


    private void initPaint() {
        textPaint.setColor(color);
        textPaint.setTextSize(textSize);
    }

    private void initTextInformation() {
        if (imageRsd != -1) {
            calculateTextWidth();
        } else {
            textPaint.getTextBounds(contentStr, 0, contentStr.length(), textRect);
        }

        textStrWidth = textRect.width();
        textStrHeight = textRect.height();
    }

    private void calculateTextWidth() {
        textPaint.getTextBounds(contentStr + "三字", 0, contentStr.length() + 2, textRect);
        contentStr = "  " + contentStr;
    }

    private void initTextStaticLayout() {
        contentSpanStr = new SpannableString(contentStr);

        if (imageRsd != -1) {
            Drawable d = context.getResources().getDrawable(imageRsd);
            d.setBounds(0, 0, textRect.height(), textRect.height());
            ImageSpan imageSpan = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
            contentSpanStr.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        textStaticLayout = new StaticLayout(contentSpanStr, textPaint, Integer.MAX_VALUE, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
    }

    private void initBackgroundDrawable() {
        if (bgRsd != -1) {
            bgDrawable = context.getResources().getDrawable(bgRsd);
            bgDrawable.setBounds(0, 0, textRect.width() + paddingSize * 2, textRect.height() + paddingSize * 2);
        }
    }

    public final void draw(Canvas canvas) {
        onDraw(canvas);
        onAfterDraw(canvas);
    }

    protected abstract void onDraw(Canvas canvas);

    protected abstract void onAfterDraw(Canvas canvas);

    public void setStartInfo(int startXValue, int startYValue, int endXvalue, int endYvalue) {
        this.startXAxes = startXValue;
        this.startYAxes = startYValue;
        this.endXAxes = endXvalue;
        this.endYAxes = endYvalue;
        precomputedTime = Math.abs(startXValue - endXvalue - textStrWidth) / speed * 10;
        this.startTime = System.currentTimeMillis();

        this.currentXAxes = startXValue;
        this.currentYAxes = startYValue;
    }

    public abstract boolean isDrawEnd();

    public abstract boolean isOverlay(BaseBarrageItem item, BarrageView.CountLevel level);

    public int getCurrentXAxes() {
        return currentXAxes;
    }

    public int getCurrentYAxes() {
        return currentYAxes;
    }

    public void setCurrentXAxes(int currentXAxes) {
        this.currentXAxes = currentXAxes;
    }

    public void setCurrentYAxes(int currentYAxes) {
        this.currentYAxes = currentYAxes;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public Interpolator getAccelerationFactor() {
        return accelerationFactor;
    }

    public void setAccelerationFactor(Interpolator accelerationFactor) {
        this.accelerationFactor = accelerationFactor;
    }

    public int getContentWidth() {
        return textStrWidth;
    }
}
