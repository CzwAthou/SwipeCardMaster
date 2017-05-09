package com.athou.swipecard;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by athou on 2017/5/8.
 */

public class CardConfig {
    //屏幕上最多同时显示几个Item
    public static int MAX_SHOW_COUNT;

    //每一级Scale相差0.05f
    public static float SCALE_GAP;
    //每一级translationY位移差
    public static int TRANS_Y_GAP;

    public static void init(Context context) {
        MAX_SHOW_COUNT = 4;
        SCALE_GAP = 0.05f;
        TRANS_Y_GAP = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, context.getResources().getDisplayMetrics());
    }
}
