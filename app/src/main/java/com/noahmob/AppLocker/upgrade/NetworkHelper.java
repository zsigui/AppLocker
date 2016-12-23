package com.noahmob.AppLocker.upgrade;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TimeZone;

public class NetworkHelper {
    public static String generateGetMethodUrl(String url, Map<String, String> paramMap) throws UnsupportedEncodingException {
        if (paramMap == null || paramMap.size() == 0) {
            return url;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("?");
        for (Entry<String, String> entry : paramMap.entrySet()) {
            sb.append((String) entry.getKey());
            sb.append("=");
            sb.append(URLEncoder.encode((String) entry.getValue(), "UTF-8"));
            sb.append("&");
        }
        sb.deleteCharAt(sb.length() - 1);
        return url + sb.toString();
    }

    public static String getImei(Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
    }

    public static String getChannelId(Context context) {
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            if (!(appInfo == null || appInfo.metaData == null)) {
                String channelId = appInfo.metaData.get(NetworkConst.CHANNEL_META_DATE_NAME).toString();
                if (channelId != null) {
                    return channelId;
                }
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getVersionCode(Context context) {
        int versionCode = -1;
        try {
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return Integer.toString(versionCode);
    }

    public static int getSubmitInterval(int from, int to) {
        return new Random().nextInt(to - from) + from;
    }

    public static String getUrl(HashMap<String, String> paramMap, String webInterface) throws UnsupportedEncodingException {
        if (paramMap == null || paramMap.size() == 0) {
            return webInterface;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("?");
        for (Entry<String, String> entry : paramMap.entrySet()) {
            sb.append((String) entry.getKey());
            sb.append("=");
            sb.append(URLEncoder.encode((String) entry.getValue(), "UTF-8"));
            sb.append("&");
        }
        sb.deleteCharAt(sb.length() - 1);
        return webInterface + sb.toString();
    }

    public static void scheduleSubmitTask(Context context, Intent intent, int requestCode, int rangeFrom, int rangeTo) {
        AlarmManager am = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        PendingIntent pi = PendingIntent.getService(context.getApplicationContext(), requestCode, intent, 0);
        int interval = getSubmitInterval(rangeFrom, rangeTo);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.HOUR, interval);
        am.set(0, calendar.getTimeInMillis(), pi);
    }

    public static String getBeijingTime(String format, Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.CHINA);
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        return dateFormat.format(date);
    }

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            NetworkInfo mNetworkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public boolean isWifiConnected(Context context) {
        if (context != null) {
            NetworkInfo mWiFiNetworkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getNetworkInfo(1);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public boolean isMobileConnected(Context context) {
        if (context == null) {
            return false;
        }
        NetworkInfo mMobileNetworkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getNetworkInfo(0);
        if (mMobileNetworkInfo != null) {
            return mMobileNetworkInfo.isAvailable();
        }
        return false;
    }

    public static int getConnectedType(Context context) {
        if (context != null) {
            NetworkInfo mNetworkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
                return mNetworkInfo.getType();
            }
        }
        return -1;
    }

    public static String getAppDispatchChannel(Context context) {
        return "10000";
    }
}
