package com.noahmob.AppLocker.apprater;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public final class ApplicationRatingInfo {
    private String applicationName;
    private int applicationVersionCode;
    private String applicationVersionName;

    public String getApplicationName() {
        return this.applicationName;
    }

    public int getApplicationVersionCode() {
        return this.applicationVersionCode;
    }

    public String getApplicationVersionName() {
        return this.applicationVersionName;
    }

    private ApplicationRatingInfo() {
    }

    public static ApplicationRatingInfo createApplicationInfo(Context context) {
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo = null;
        PackageInfo packageInfo = null;
        try {
            applicationInfo = packageManager.getApplicationInfo(context.getApplicationInfo().packageName, 0);
            packageInfo = packageManager.getPackageInfo(context.getApplicationInfo().packageName, 0);
        } catch (NameNotFoundException e) {
        }
        ApplicationRatingInfo resultInfo = new ApplicationRatingInfo();
        resultInfo.applicationName = packageManager.getApplicationLabel(applicationInfo).toString();
        resultInfo.applicationVersionCode = packageInfo.versionCode;
        resultInfo.applicationVersionName = packageInfo.versionName;
        return resultInfo;
    }
}
