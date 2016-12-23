package com.noahmob.AppLocker.Utils;

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

    public static <T> void pl(T printInfo) {
        if (printInfo != null) {
            System.out.println(printInfo);
        }
    }

    public static <T> void p(T printInfo) {
        if (printInfo != null) {
            System.out.print(printInfo);
        }
    }

    public static <T> void I(T logInfo) {
        I("mmmm", logInfo);
    }

    public static <T> void I(String tag, T logInfo) {
        if (tag != null && logInfo != null) {
            Log.i(tag, logInfo.toString());
        }
    }

    public static <T> void E(T logInfo) {
        E("mmmm", logInfo.toString());
    }

    public static <T> void E(String tag, T logInfo) {
        if (tag != null && logInfo != null) {
            Log.e(tag, logInfo.toString());
        }
    }

    public static <T> void W(T logInfo) {
        W("mmmm", logInfo.toString());
    }

    public static <T> void W(String tag, T logInfo) {
        if (tag != null && logInfo != null) {
            Log.w(tag, logInfo.toString());
        }
    }

    public static <T> void D(T logInfo) {
        D("mmmm", logInfo.toString());
    }

    public static <T> void D(String tag, T logInfo) {
        if (tag != null && logInfo != null) {
            Log.d(tag, logInfo.toString());
        }
    }

    public static <T> void V(T logInfo) {
        V("mmmm", logInfo.toString());
    }

    public static <T> void V(String tag, T logInfo) {
        if (tag != null || logInfo != null) {
            Log.v(tag, logInfo.toString());
        }
    }

    public static <T> void printLogWtf(T logInfo) {
        Wtf("mmmm", logInfo.toString());
    }

    public static void Wtf(String tag, String logInfo) {
        if (tag != null && logInfo != null) {
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
        StringBuffer strBuffer = new StringBuffer();
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        strBuffer.append("; class:").append(stackTrace[1].getClassName()).append("; method:").append(stackTrace[1].getMethodName()).append("; number:").append(stackTrace[1].getLineNumber()).append("; fileName:").append(stackTrace[1].getFileName());
        pl(strBuffer.toString());
    }

    public static void printFileNameAndLinerNumber() {
        StringBuffer strBuffer = new StringBuffer();
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        strBuffer.append("; fileName:").append(stackTrace[1].getFileName()).append("; number:").append(stackTrace[1].getLineNumber());
        pl(strBuffer.toString());
    }

    public static int printLineNumber() {
        StringBuffer strBuffer = new StringBuffer();
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        strBuffer.append("; number:").append(stackTrace[1].getLineNumber());
        pl(strBuffer.toString());
        return stackTrace[1].getLineNumber();
    }

    public static int printLineNumber(String inputStr) {
        if (TextUtils.isEmpty(inputStr)) {
            return 0;
        }
        StringBuffer strBuffer = new StringBuffer();
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        strBuffer.append("; number:").append(stackTrace[1].getLineNumber());
        strBuffer.append(inputStr);
        pl(strBuffer.toString());
        return stackTrace[1].getLineNumber();
    }

    public static void printMethod() {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("; number:").append(new Throwable().getStackTrace()[1].getMethodName());
        pl(strBuffer.toString());
    }

    public static void printFileNameAndLinerNumber(String printInfo) {
        if (printInfo != null) {
            StringBuffer strBuffer = new StringBuffer();
            StackTraceElement[] stackTrace = new Throwable().getStackTrace();
            StringBuffer append = strBuffer.append("; fileName:").append(stackTrace[1].getFileName()).append("; number:").append(stackTrace[1].getLineNumber()).append("\n");
            if (printInfo == null) {
                printInfo = "";
            }
            append.append(printInfo);
            pl(strBuffer.toString());
        }
    }
}
