package com.noahmob.AppLocker.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.amigo.applocker.BuildConfig;

public class UninstallIntentReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        String[] packageNames = intent.getStringArrayExtra("android.intent.extra.PACKAGES");
        if (packageNames != null) {
            for (String packageName : packageNames) {
                if (packageName != null && packageName.equals(BuildConfig.APPLICATION_ID)) {
                    new ListenActivities(context).start();
                }
            }
        }
    }
}
