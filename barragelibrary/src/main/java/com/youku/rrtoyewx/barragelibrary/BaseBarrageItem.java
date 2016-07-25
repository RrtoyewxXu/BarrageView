package com.youku.rrtoyewx.barragelibrary;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
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
    protected int color;
    protected int textSize;
    protected String contentStr;
    protected Paint paint;

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
        textSize = DEFAULT_TEXT_SIZE;
        color = DEFAULT_TEXT_COLOR;
        contentStr = DEFAULT_CONTENT_STRING;
        speed = DEFAULT_SPEED;
        accelerationFactor = DEFAULT_INTERPOLATOR;

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        textRect = new Rect();
    }


    public BaseBarrageItem(String contentStr) {
        this(contentStr, DEFAULT_TEXT_COLOR, 0);
    }

    public BaseBarrageItem(String contentStr, int color, int textSize) {
        this(contentStr, color, textSize, 0, DEFAULT_INTERPOLATOR);
    }

    public BaseBarrageItem(String contentStr, int color, int textSize, int speed, Interpolator accelerationFactor) {

        if (!TextUtils.isEmpty(contentStr)) {
            this.contentStr = contentStr;
        }

        if (speed > 0) {
            this.speed = speed;
        }

        if (textSize > 0) {
            this.textSize = textSize;
        }

        this.accelerationFactor = accelerationFactor;
        this.color = color;
        init();
    }

    private void init() {
        initPaint();
        initTextInformation();
    }


    private void initPaint() {
        paint.setColor(color);
        paint.setTextSize(textSize);
    }

    private void initTextInformation() {
        paint.getTextBounds(contentStr, 0, contentStr.length(), textRect);
        textStrWidth = textRect.width();
        textStrHeight = textRect.height();
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
