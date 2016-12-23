package com.noahmob.AppLocker.Receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;

import com.noahapp.accesslib.PreferenceFile;
import com.noahapp.accesslib.TrackManager;

import java.util.List;

public class InstallAppChangeReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (!TextUtils.isEmpty(action)) {
            String apkPackageName;
            Intent notityIntent;
                // 此处添加广告 ad，删除之
//            if (action.equals("android.intent.action.PACKAGE_ADDED")) {
//                apkPackageName = intent.getDataString();
//                if (apkPackageName != null) {
//                    checkReferren(context);
//                    TrackManager.getInstance(context).trackAction("launch=" + apkPackageName);
//                    doStartApplicationWithPackageName(context, apkPackageName.substring(8));
//                }
//                if (apkPackageName != null) {
//                    notityIntent = new Intent(context, InstallRecommandActivity.class);
//                    notityIntent.setFlags(268435456);
//                    notityIntent.putExtra("notify_package_name", apkPackageName);
//                    context.startActivity(notityIntent);
//                }
//            } else if (action.equals("android.intent.action.PACKAGE_REMOVED")) {
//                apkPackageName = intent.getDataString();
//                if (apkPackageName != null) {
//                    notityIntent = new Intent(context, InstallRecommandActivity.class);
//                    notityIntent.setFlags(268435456);
//                    notityIntent.putExtra("notify_package_name", apkPackageName);
//                    context.startActivity(notityIntent);
//                }
//            }
        }
    }

    private void doStartApplicationWithPackageName(Context context, String packagename) {
        PackageInfo packageinfo = null;
        try {
            packageinfo = context.getPackageManager().getPackageInfo(packagename, 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo != null) {
            Intent resolveIntent = new Intent("android.intent.action.MAIN", null);
            resolveIntent.addCategory("android.intent.category.LAUNCHER");
            resolveIntent.setPackage(packageinfo.packageName);
            List<ResolveInfo> resolveinfoList = context.getPackageManager().queryIntentActivities(resolveIntent, 0);
            if (resolveinfoList.size() > 0 && resolveinfoList.iterator() != null) {
                ResolveInfo resolveinfo = (ResolveInfo) resolveinfoList.iterator().next();
                if (resolveinfo != null) {
                    String packageName = resolveinfo.activityInfo.packageName;
                    String className = resolveinfo.activityInfo.name;
                    Intent intent = new Intent("android.intent.action.MAIN");
                    intent.addCategory("android.intent.category.LAUNCHER");
                    intent.setComponent(new ComponentName(packageName, className));
                    intent.setFlags(268435456);
                    context.startActivity(intent);
                    TrackManager.getInstance(context).trackAction("startApp=" + packageName);
                }
            }
        }
    }

    private void checkReferren(Context context) {
        String link = PreferenceFile.getInstance(context).getNowLink();
        if (!TextUtils.isEmpty(link)) {
//            Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(link));
//            browserIntent.setClassName(zze.GOOGLE_PLAY_STORE_PACKAGE, "com.android.vending.AssetBrowserActivity");
//            browserIntent.setFlags(268435456);
//            context.startActivity(browserIntent);
//            PreferenceFile.getInstance(context).saveNowLink("");
        }
    }
}
