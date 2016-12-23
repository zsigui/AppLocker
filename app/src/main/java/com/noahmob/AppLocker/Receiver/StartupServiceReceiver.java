package com.noahmob.AppLocker.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.noahmob.AppLocker.AppLockerPreference;

public class StartupServiceReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            if (AppLockerPreference.getInstance(context).isAutoStart() && !AppLockerPreference.getInstance(context).isServiceEnabled()) {
                AppLockerPreference.getInstance(context).saveServiceEnabled(false);
            }
        } else if (!AppLockerPreference.getInstance(context).isServiceEnabled()) {
        }
    }
}
