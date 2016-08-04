package com.youku.rrtoyewx.barragelibrary;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.DrawableRes;
import android.view.animation.Interpolator;

import static com.youku.rrtoyewx.barragelibrary.BarrageView.CountLevel;

/**
 * Created by Rrtoyewx on 16/7/24.
 * Conventional barrage, the direction from left to right
 */
public class NormalBarrageItem extends BaseBarrageItem {
    private static final String TAG = NormalBarrageItem.class.getSimpleName();

    private NormalBarrageItem(Context context, @DrawableRes int imageRsd, @DrawableRes int bgRsd, int paddingSize, String contentStr, int color, int textSize, int speed, Interpolator interpolator) {
        super(context, imageRsd, bgRsd, paddingSize, contentStr, color, textSize, speed, interpolator);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBackground(canvas);
        drawText(canvas);
    }

    private void drawText(Canvas canvas) {
        canvas.save();
        canvas.translate(currentXAxes, currentYAxes - textRect.height());
        textStaticLayout.draw(canvas);
        canvas.restore();
    }

    private void drawBackground(Canvas canvas) {
        if (bgDrawable != null) {
            canvas.save();
            canvas.translate(currentXAxes - paddingSize, currentYAxes - textRect.height() - paddingSize);
            bgDrawable.draw(canvas);
            canvas.restore();
        }
    }

    @Override
    protected void onAfterDraw(Canvas canvas) {
        if (System.currentTimeMillis() - startTime <= precomputedTime) {
            double factor = (System.currentTimeMillis() - startTime) * 1.0 / precomputedTime;
            float interpolation = accelerationFactor.getInterpolation((float) factor);

            currentXAxes = (int) (startXAxes - (startXAxes - endXAxes + textStrWidth) * interpolation);
        } else {
            currentXAxes = -textStrWidth;
        }
    }

    @Override
    public boolean isDrawEnd() {
        return currentXAxes + getContentWidth() <= endXAxes;
    }

    @Override
    public boolean isOverlay(BaseBarrageItem item, CountLevel level) {
        int distanceWidth = -1;
        switch (level) {
            case HIGH:
                distanceWidth = (int) (textStrWidth * 0.5);
                break;
            case MEDIUM:
                distanceWidth = (int) (textStrWidth * 0.7);
                break;
            case LOW:
                distanceWidth = (int) (textStrWidth * 1.2);

        }
        return currentXAxes + distanceWidth > startXAxes;
    }


    public static class BarrageItemBuilder {
        private int color = DEFAULT_TEXT_COLOR;
        private int textSize = DEFAULT_TEXT_SIZE;
        private String contentStr = DEFAULT_CONTENT_STRING;
        private int imageRsd = -1;
        private int bgRsd = -1;
        private int paddingSize = DEFAULT_PADDING_SIZE;
        private int speed = DEFAULT_SPEED;
        private Interpolator interpolator = DEFAULT_INTERPOLATOR;

        public BarrageItemBuilder color(int color) {
            this.color = color;
            return this;
        }

        public BarrageItemBuilder textSize(int textSize) {
            this.textSize = textSize;
            return this;
        }

        public BarrageItemBuilder contentStr(String contentStr) {
            this.contentStr = contentStr;
            return this;
        }

        public BarrageItemBuilder imageRsd(@DrawableRes int imageRsd) {
            this.imageRsd = imageRsd;
            return this;
        }

        public BarrageItemBuilder bgRsd(@DrawableRes int bgRsd) {
            this.bgRsd = bgRsd;
            return this;
        }

        public BarrageItemBuilder paddingSize(int paddingSize) {
            this.paddingSize = paddingSize;
            return this;
        }

        public BarrageItemBuilder speed(int speed) {
            this.speed = speed;
            return this;
        }

        public BarrageItemBuilder interpolator(Interpolator interpolator) {
            this.interpolator = interpolator;
            return this;
        }

        public NormalBarrageItem create(Context context) {
            return new NormalBarrageItem(context, imageRsd, bgRsd, paddingSize, contentStr, color, textSize, speed, interpolator);
        }
    }
}
