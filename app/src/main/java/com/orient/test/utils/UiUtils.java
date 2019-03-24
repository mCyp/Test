package com.orient.test.utils;

import android.content.Context;

/**
 * 屏幕的工具类
 *
 * Created by wangjie on 2019/3/24.
 */

public class UiUtils {

    public static int dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
