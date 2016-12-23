package com.noahapp.accesslib;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class DP {
    public static final String TAG = "mmmm";

    public class Debug {
        public static final boolean DEBUG_MODE = true;
    }

    private DP() {
    }

    public static <T> void pl(T t) {
        if (Debug.DEBUG_MODE) {
            System.out.println(t == null ? "" : t.toString());
        }
    }

    public static <T> void p(T t) {
        if (Debug.DEBUG_MODE) {
            System.out.print(t == null ? "" : t.toString());
        }
    }

    public static <T> void I(T logInfo) {
        I("mmmm", logInfo);
    }

    public static <T> void I(String tag, T t) {
        if (Debug.DEBUG_MODE) {
            Log.i(tag, t == null ? "" : t.toString());
        }
    }

    public static <T> void E(T logInfo) {
        E(TAG, logInfo.toString());
    }

    public static <T> void E(String tag, T t) {
        if (Debug.DEBUG_MODE) {
            Log.e(tag, t == null ? "" : t.toString());
        }
    }

    public static <T> void W(T logInfo) {
        W(TAG, logInfo.toString());
    }

    public static <T> void W(String tag, T t) {
        if (Debug.DEBUG_MODE) {
            Log.w(tag, t == null ? "" : t.toString());
        }
    }

    public static <T> void D(T logInfo) {
        D(TAG, logInfo.toString());
    }

    public static <T> void D(String tag, T t) {
        if (Debug.DEBUG_MODE) {
            Log.d(tag, t == null ? "" : t.toString());
        }
    }

    public static <T> void V(T logInfo) {
        V(TAG, logInfo.toString());
    }

    public static <T> void V(String tag, T logInfo) {
        if (Debug.DEBUG_MODE) {
            Log.v(tag, logInfo == null ? "" : logInfo.toString());
        }
    }

    public static <T> void printLogWtf(T logInfo) {
        Wtf(TAG, logInfo.toString());
    }

    public static void Wtf(String tag, String logInfo) {
        if (Debug.DEBUG_MODE) {
            Log.wtf(tag, logInfo);
        }
    }

    public static <T> void toast(Context context, T toastInfo) {
        if (context != null && toastInfo != null) {
            Toast.makeText(context, toastInfo.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public static <T> void toast(Context context, T toastInfo, int timeLen) {
        if (context != null && toastInfo != null && timeLen > 0) {
            Toast.makeText(context, toastInfo.toString(), timeLen).show();
        }
    }

    public static void printBaseInfo() {
    }

    public static void printFileNameAndLinerNumber() {
    }

    public static int printLineNumber() {
        return 0;
    }

    public static int printLineNumber(String inputStr) {
        return TextUtils.isEmpty(inputStr) ? 0 : 0;
    }

    public static void printMethod() {
    }

    public static void printFileNameAndLinerNumber(String printInfo) {
        if (printInfo == null) {
        }
    }
}
