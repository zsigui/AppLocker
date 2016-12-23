package com.noahmob.AppLocker.Utils;

import android.content.Context;
import android.view.WindowManager;
import com.noahmob.AppLocker.upgrade.OptimConst;

public class ScreenUtils {
    public static final int HDPI_800_480 = 0;
    public static final int HDPI_854_480 = 1;
    public static final int HDPI_960_540 = 2;

    public static int getScreenWidth(Context context) {
        return ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
    }

    public static int getScreenHeight(Context context) {
        return ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight();
    }

    public static int px2dip(Context context, float pxValue) {
        return (int) ((pxValue / context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    public static int dip2px(Context context, float dipValue) {
        return (int) ((dipValue * context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    public static boolean ismdpiDesity(Context context) {
        return context.getResources().getDisplayMetrics().density == 1.0f;
    }

    public static boolean ishdpiDesity(Context context) {
        return context.getResources().getDisplayMetrics().density == OptimConst.DENSITY_SCALE_HDPI;
    }

    public static boolean isxhdpiDesity(Context context) {
        return context.getResources().getDisplayMetrics().density == OptimConst.DENSITY_SCALE_XHDPI;
    }

    public static boolean isxxhdpiDesity(Context context) {
        return context.getResources().getDisplayMetrics().density == OptimConst.DENSITY_SCALE_XXHDPI;
    }

    public static boolean isLowDeviceFlag(Context context) {
        return context.getResources().getDisplayMetrics().density <= 1.0f;
    }

    public static int getHdpiScreenResolution(Context context) {
        int height = getScreenHeight(context);
        if (height == 854) {
            return 1;
        }
        if (height == 960) {
            return 2;
        }
        return 0;
    }

    public static int sp2px(Context context, float spValue) {
        return (int) ((spValue * context.getResources().getDisplayMetrics().scaledDensity) + 0.5f);
    }

    public static float getScaledDensity(Context context) {
        return context.getResources().getDisplayMetrics().scaledDensity;
    }

    public static String getScreenResloution(Context context) {
        return getScreenHeight(context) + "*" + getScreenWidth(context);
    }

    public static int getStatusBarHeight(Context ctx) {
        int statusBarHeight = 0;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            statusBarHeight = ctx.getResources().getDimensionPixelSize(Integer.parseInt(clazz.getField("status_bar_height").get(clazz.newInstance()).toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusBarHeight;
    }
}
