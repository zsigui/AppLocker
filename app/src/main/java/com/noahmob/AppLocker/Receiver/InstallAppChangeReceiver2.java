package com.noahmob.AppLocker.Receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;
import com.android.volley.RequestQueue;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;

public class InstallAppChangeReceiver2 extends BroadcastReceiver {
    RequestQueue mQueue;

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (!TextUtils.isEmpty(action)) {
            // 捕捉安装ad，关闭他
//            if (action.equals("android.intent.action.PACKAGE_ADDED")) {
//                String apkPackageName = intent.getDataString();
//                if (apkPackageName != null) {
//                    trackAction(context, "launch=" + apkPackageName);
//                    doStartApplicationWithPackageName(context, apkPackageName.substring(8));
//                }
//            } else if (!action.equals("android.intent.action.PACKAGE_REMOVED")) {
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
            ResolveInfo resolveinfo = (ResolveInfo) context.getPackageManager().queryIntentActivities(resolveIntent, 0).iterator().next();
            if (resolveinfo != null) {
                String packageName = resolveinfo.activityInfo.packageName;
                String className = resolveinfo.activityInfo.name;
                Intent intent = new Intent("android.intent.action.MAIN");
                intent.addCategory("android.intent.category.LAUNCHER");
                intent.setComponent(new ComponentName(packageName, className));
                intent.setFlags(268435456);
                context.startActivity(intent);
            }
        }
    }

    private void trackAction(Context context, String order) {
        if (this.mQueue == null) {
            this.mQueue = Volley.newRequestQueue(context);
        }
        this.mQueue.add(new JsonObjectRequest("http://api.hehevideo.com/tracking/" + order, null, new Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
            }
        }, null));
    }
}
