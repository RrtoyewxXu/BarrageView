package com.youku.rrtoyewx.barragelibrary;

import android.graphics.Canvas;
import android.view.animation.Interpolator;

import static com.youku.rrtoyewx.barragelibrary.BarrageView.CountLevel;

/**
 * Created by Rrtoyewx on 16/7/24.
 * Conventional barrage, the direction from left to right
 */
public class NormalBarrageItem extends BaseBarrageItem {
    private static final String TAG = NormalBarrageItem.class.getSimpleName();


    public NormalBarrageItem(String contentStr, int color, int textSize, int speed, Interpolator accelerationFactor) {
        super(contentStr, color, textSize, speed, accelerationFactor);
    }

    public NormalBarrageItem(String contentStr, int color, int textSize) {
        super(contentStr, color, textSize);
    }

    public NormalBarrageItem(String contentStr) {
        super(contentStr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawText(contentStr, currentXAxes, currentYAxes, paint);
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
}
