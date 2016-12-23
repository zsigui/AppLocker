package com.noahmob.AppLocker;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build.VERSION;
import android.os.Handler;
import android.util.Log;

import com.amigo.applocker.BuildConfig;
import com.noahmob.AppLocker.config.Constant;
import com.noahmob.AppLocker.listener.ActivityStartingListener;

import java.lang.reflect.Field;
import java.util.Hashtable;
import java.util.List;

public class ActivityStartingHandler implements ActivityStartingListener {
    private static Hashtable<String, Runnable> tempAllowedPackages = new Hashtable();
    private Handler handler;
    private String lastRunningActivity = "";
    private String lastRunningPackage;
    private ActivityManager mAm;
    private Context mContext;

    private class RemoveFromTempRunnable implements Runnable {
        private String mPackageName;

        public RemoveFromTempRunnable(String pname) {
            this.mPackageName = pname;
        }

        public void run() {
            Log.d("Detector", "Lock timeout Expires: " + this.mPackageName);
            ActivityStartingHandler.tempAllowedPackages.remove(this.mPackageName);
        }
    }

    public ActivityStartingHandler(Context context) {
        this.mContext = context;
        this.handler = new Handler();
        this.mAm = (ActivityManager) this.mContext.getSystemService(Context.ACTIVITY_SERVICE);
        this.lastRunningPackage = getRunningPackage();
        context.registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                try {
                    String packagename = intent.getStringExtra("com.noahmob.AppLocker.extra.package.name");
                    if (AppLockerPreference.getInstance(ActivityStartingHandler.this.mContext).getRelockTimeout() > 0) {
                        if (ActivityStartingHandler.tempAllowedPackages == null) {
                            ActivityStartingHandler.tempAllowedPackages = new Hashtable();
                        }
                        if (ActivityStartingHandler.tempAllowedPackages.containsKey(packagename)) {
                            Log.d("Detector", "Extending timeout for: " + packagename);
                            ActivityStartingHandler.this.handler.removeCallbacks((Runnable) ActivityStartingHandler
                                    .tempAllowedPackages.get(packagename));
                        }
                        Runnable runnable = new RemoveFromTempRunnable(packagename);
                        ActivityStartingHandler.tempAllowedPackages.put(packagename, runnable);
                        ActivityStartingHandler.this.handler.postDelayed(runnable, (long) ((AppLockerPreference
                                .getInstance(ActivityStartingHandler.this.mContext).getRelockTimeout() * 1000) * 60));
                        ActivityStartingHandler.this.log();
                    }
                    ActivityStartingHandler.this.lastRunningPackage = packagename;
                    ActivityStartingHandler.this.lastRunningActivity = "";
                } catch (Exception e) {
                }
            }
        }, new IntentFilter("com.noahmob.AppLocker.applicationpassedtest"));
    }

    private void log() {
        String output = "temp allowed: ";
        for (String p : tempAllowedPackages.keySet()) {
            output = output + p + ", ";
        }
        Log.d("Detector", output);
    }

    private String getRunningPackage() {
        String topActivityName = "";
        if (VERSION.SDK_INT <= 19) {
            List<RunningTaskInfo> infos = this.mAm.getRunningTasks(1);
            if (infos.size() < 1) {
                return null;
            }
            topActivityName = ((RunningTaskInfo) infos.get(0)).topActivity.getPackageName();
        } else {
            RunningAppProcessInfo currentInfo = null;
            Field field = null;
            try {
                field = RunningAppProcessInfo.class.getDeclaredField("processState");
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            for (RunningAppProcessInfo app : this.mAm.getRunningAppProcesses()) {
                if (app.importance == 100 && app.importanceReasonCode == 0) {
                    Integer state = null;
                    try {
                        state = Integer.valueOf(field.getInt(app));
                    } catch (IllegalAccessException e2) {
                        e2.printStackTrace();
                    } catch (IllegalArgumentException e3) {
                        e3.printStackTrace();
                    }
                    if (state != null && state.intValue() == 2) {
                        currentInfo = app;
                        break;
                    }
                }
            }
            if (!(currentInfo == null || currentInfo.processName.equals("com.google.android.googlequicksearchbox"))) {
                String packageName = currentInfo.processName;
                topActivityName = currentInfo.processName;
                if (packageName.equals(BuildConfig.APPLICATION_ID)) {
                    topActivityName = ((RunningTaskInfo) this.mAm.getRunningTasks(1).get(0)).topActivity
                            .getShortClassName();
                }
            }
        }
        return topActivityName;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onActivityStarting(String paramString1, String paramString2) {
        try {
            if (paramString1.equals(this.lastRunningPackage))
                return;
            if (!paramString1.equals(this.mContext.getPackageName())) {
                String[] arrayOfString = AppLockerPreference.getInstance(this.mContext).getApplicationList();
                if ((AppLockerPreference.getInstance(this.mContext).getRelockTimeout() > 0) && (tempAllowedPackages.containsKey(paramString1)))
                    return;
                int i = arrayOfString.length;
                if (Constant.flag)
                    i = 0;
                while (true)
                {
                    if (i < arrayOfString.length)
                    {
                        if (arrayOfString[i].equals(paramString1))
                            blockActivity(paramString1, paramString2);
                    }
                    else
                    {
                        this.lastRunningPackage = paramString1;
                        this.lastRunningActivity = "";
                        return;
                    }
                    i += 1;
                }
            }
            if ((this.lastRunningActivity.equals(".AppLockerPreferenceActivity")) || (this.lastRunningActivity.equals
                    (".SetPasswordActivity")) || (this.lastRunningActivity.equals(".SetMailActivity")))
                return;
        } finally {
        }
        if (paramString2.equals(".LockPatternActivity"))
            return;
        if (paramString2.equals(".LockScreenActivity"))
            return;
        if (paramString2.equals(".LockScreenPatternActivity"))
            return;
        if (paramString2.equals(".AppLockerPreferenceActivity")) {
            this.lastRunningActivity = paramString2;
            return;
        }
        if (paramString2.equals(".SetMailActivity")) {
            this.lastRunningActivity = paramString2;
            return;
        }
        if (paramString2.equals(".SetPasswordActivity")) {
            this.lastRunningActivity = paramString2;
            return;
        }
        if (paramString2.equals(".RetrivePswActivity"))
            return;
        if (Constant.flag)
            blockActivity(paramString1, paramString2);
    }

    private void blockActivity(String packageName, String activityName) {
        if (AppLockerPreference.getInstance(this.mContext).isPasswordType()) {
            Intent lockIntent = new Intent(this.mContext, LockScreenActivity.class);
            lockIntent.addFlags(268435456);
            lockIntent.putExtra("locked activity name", activityName).putExtra("locked package name", packageName);
            this.mContext.startActivity(lockIntent);
            return;
        }
        Intent lockIntent = new Intent(this.mContext, LockScreenPatternActivity.class);
        lockIntent.addFlags(268435456);
        lockIntent.putExtra("locked activity name", activityName).putExtra("locked package name", packageName);
        this.mContext.startActivity(lockIntent);
    }

    public static void clearAllowList() {
        if (tempAllowedPackages != null) {
            tempAllowedPackages.clear();
        }
    }
}
