package com.youku.rrtoyewx.barrageview;

import android.content.Context;

/**
 * Created by Rrtoyewx on 16/7/25.
 */
public class Utils {
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
