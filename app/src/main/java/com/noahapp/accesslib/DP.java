package com.noahapp.accesslib;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class DP {
    public static final String TAG = "mmmm";

    public class Debug {
        public static final boolean DEBUG_MODE = false;
    }

    private DP() {
    }

    public static <T> void pl(T t) {
    }

    public static <T> void p(T t) {
    }

    public static <T> void I(T logInfo) {
        I("mmmm", logInfo);
    }

    public static <T> void I(String tag, T t) {
    }

    public static <T> void E(T logInfo) {
        E("mmmm", logInfo.toString());
    }

    public static <T> void E(String tag, T t) {
    }

    public static <T> void W(T logInfo) {
        W("mmmm", logInfo.toString());
    }

    public static <T> void W(String tag, T t) {
    }

    public static <T> void D(T logInfo) {
        D("mmmm", logInfo.toString());
    }

    public static <T> void D(String tag, T t) {
    }

    public static <T> void V(T logInfo) {
        V("mmmm", logInfo.toString());
    }

    public static <T> void V(String tag, T logInfo) {
        if (logInfo != null) {
            Log.v(tag, logInfo.toString());
        }
    }

    public static <T> void printLogWtf(T logInfo) {
        Wtf("mmmm", logInfo.toString());
    }

    public static void Wtf(String tag, String logInfo) {
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
