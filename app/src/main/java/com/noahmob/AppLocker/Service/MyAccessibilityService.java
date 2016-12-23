package com.noahmob.AppLocker.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.view.accessibility.AccessibilityEvent;

import com.noahapp.accesslib.BaseAccessibilityService;
import com.noahmob.AppLocker.AppLockerPreference;
import com.noahmob.AppLocker.LockScreenActivity;
import com.noahmob.AppLocker.LockScreenPatternActivity;
import com.noahmob.AppLocker.Utils.DP;
import com.noahmob.AppLocker.config.Constant;

import java.util.Hashtable;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class MyAccessibilityService extends BaseAccessibilityService {
    private static Hashtable<String, Runnable> tempAllowedPackages = new Hashtable();
    private Handler handler = new Handler();
    private String lastRunningPackage;

    private class RemoveFromTempRunnable implements Runnable {
        private String mPackageName;

        public RemoveFromTempRunnable(String pname) {
            this.mPackageName = pname;
        }

        public void run() {
            DP.D("Lock timeout Expires: " + this.mPackageName);
            MyAccessibilityService.tempAllowedPackages.remove(this.mPackageName);
        }
    }


    protected void onServiceConnected() {
        super.onServiceConnected();
        registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                try {
                    String packagename = intent.getStringExtra("com.noahmob.AppLocker.extra.package.name");
                    MyAccessibilityService.this.handleTempAllowListEvent(packagename);
                    MyAccessibilityService.this.lastRunningPackage = packagename;
                } catch (Exception e) {
                }
            }
        }, new IntentFilter("com.noahmob.AppLocker.applicationpassedtest"));
    }

    public void onAccessibilityEvent(AccessibilityEvent event) {
        super.onAccessibilityEvent(event);
        if (VERSION.SDK_INT >= 15) {
            CharSequence topPackageName = event.getPackageName();
            if (topPackageName != null) {
                handleLockEvent(topPackageName.toString());
            }
            reCheckClickEvent(event, AppLockerPreference.getInstance(this).getApplicationList());
        }
    }

    private void handleTempAllowListEvent(String packageName) {
        if (AppLockerPreference.getInstance(this).getRelockTimeout() > 0) {
            if (tempAllowedPackages == null) {
                tempAllowedPackages = new Hashtable();
            }
            if (tempAllowedPackages.containsKey(packageName)) {
                this.handler.removeCallbacks((Runnable) tempAllowedPackages.get(packageName));
            }
            Runnable runnable = new RemoveFromTempRunnable(packageName);
            tempAllowedPackages.put(packageName, runnable);
            this.handler.postDelayed(runnable, (long) ((AppLockerPreference.getInstance(this).getRelockTimeout() *
                    1000) * 60));
        }
    }

    // tag:need to attention
    private void handleLockEvent(String paramString) {
        if (paramString.equals(this.lastRunningPackage))
            return;
        if (paramString.equals(getPackageName()))
            return;
        if (paramString.contains("systemui"))
            return;
        if (paramString.contains("inputmethod"))
            return;
        if (paramString.equals("com.google.android.googlequicksearchbox"))
            return;
        if ((paramString.equals("com.android.packageinstaller")) || (paramString.equals("com.google.android" +
                ".packageinstaller")))
            return;
        String[] arrayOfString = AppLockerPreference.getInstance(this).getApplicationList();
        if ((AppLockerPreference.getInstance(this).getRelockTimeout() > 0) && (tempAllowedPackages.containsKey
                (paramString)))
            return;
        if (Constant.flag) {
            for (String anArrayOfString : arrayOfString) {
                if (anArrayOfString.equals(paramString))
                    blockActivity(paramString, paramString);
            }
        }
        this.lastRunningPackage = paramString;
    }

    private void blockActivity(String packageName, String activityName) {
        if (AppLockerPreference.getInstance(this).isPasswordType()) {
            Intent lockIntent = new Intent(this, LockScreenActivity.class);
            lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            lockIntent.putExtra("locked activity name", activityName).putExtra("locked package name", packageName);
            startActivity(lockIntent);
            return;
        }
        Intent lockIntent = new Intent(this, LockScreenPatternActivity.class);
        lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        lockIntent.putExtra("locked activity name", activityName).putExtra("locked package name", packageName);
        startActivity(lockIntent);
    }

    public void onInterrupt() {
        super.onInterrupt();
    }

    public static void clearAllowList() {
        if (tempAllowedPackages != null) {
            tempAllowedPackages.clear();
        }
    }
}
