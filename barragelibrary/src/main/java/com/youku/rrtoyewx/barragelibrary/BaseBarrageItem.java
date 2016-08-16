package com.youku.rrtoyewx.barragelibrary;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.text.Layout;
import android.text.SpannableString;
import android.text.StaticLayout;
import android.text.TextPaint;
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

    protected Drawable imageDrawable;
    protected int imageRsd;
    protected int imageWidth = 75;
    protected int imageHeight = 42;
    protected int imageDistanceText = 10;

    protected int bgRsd;
    protected Paint bgPaint;
    protected Drawable bgDrawable;
    protected int paddingSize;

    protected StaticLayout textStaticLayout;

    protected Rect textRect;
    protected int offset;
    protected int textStrWidth;
    protected int textStrHeight;

    protected int startXAxes;
    protected int startYAxes;
    protected int endXAxes;
    protected int endYAxes;
    protected long preComputedTime;
    protected long startTime;

    protected int currentXAxes;
    protected int currentYAxes;

    protected int totalHeight;
    protected int totalWidth;

    protected int speed;
    protected Interpolator accelerationFactor;


    {
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setStyle(Paint.Style.FILL);
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        textRect = new Rect();
    }

    protected BaseBarrageItem(Context context, @DrawableRes int imageRsd, int imageHeight, int imageWidth, int imageDistanceText, @DrawableRes int bgRsd, int paddingSize, String contentStr, int color, int textSize, int speed, Interpolator interpolator) {
        this.context = context;
        this.accelerationFactor = interpolator;
        this.color = color;
        this.contentStr = contentStr;
        this.textSize = textSize;
        this.speed = speed;
        this.bgRsd = bgRsd;
        this.imageRsd = imageRsd;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.imageDistanceText = imageDistanceText;
        this.paddingSize = paddingSize;

        init();
    }

    private void init() {
        initPaint();
        initTextInformation();
        initImageDrawable();
        initBackgroundDrawable();
        initTotalRect();
    }

    private void initPaint() {
        textPaint.setColor(color);
        textPaint.setTextSize(textSize);
    }

    private void initTextInformation() {
        contentStr = contentStr.trim();

        textPaint.getTextBounds(contentStr, 0, contentStr.length(), textRect);

        textStrWidth = textRect.width();
        textStrHeight = textRect.height();

        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();

        offset = (int) (textStrHeight - Math.abs(fontMetrics.ascent) - Math.abs(fontMetrics.bottom));
        contentSpanStr = new SpannableString(contentStr);
        textStaticLayout = new StaticLayout(contentSpanStr, textPaint, Integer.MAX_VALUE, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
    }

    private void initImageDrawable() {

        if (imageRsd != -1) {
            imageDrawable = context.getResources().getDrawable(imageRsd);
            imageDrawable.setBounds(0, 0, imageWidth, imageHeight);
        }
    }

    private void initBackgroundDrawable() {
        if (bgRsd != -1) {
            bgDrawable = context.getResources().getDrawable(bgRsd);
            if (imageRsd != -1) {
                bgDrawable.setBounds(0, 0, textRect.width() + paddingSize * 2 + imageWidth + imageDistanceText, textRect.height() + paddingSize);
            } else {
                bgDrawable.setBounds(0, 0, textRect.width() + paddingSize * 2, textRect.height() + paddingSize);
            }
        }
    }

    private void initTotalRect() {
        if (imageDrawable == null && bgDrawable == null) {
            totalWidth = textRect.width();
            totalHeight = textRect.height();
        } else if (imageDrawable == null && bgDrawable != null) {
            totalHeight = textRect.height() + paddingSize;
            totalWidth = textRect.width() + paddingSize * 2;
        } else if (imageDrawable != null && bgDrawable == null) {
            totalHeight = textRect.height();
            totalWidth = textRect.width() + imageWidth + imageDistanceText;
        } else {
            totalWidth = textRect.width() + paddingSize * 2 + imageWidth + imageDistanceText;
            totalHeight = textRect.height() + paddingSize;
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
        preComputedTime = Math.abs(startXValue - endXvalue + totalWidth) / speed * 10;
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
