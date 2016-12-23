package com.noahmob.AppLocker.Service;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build.VERSION;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat.BigTextStyle;
import android.support.v4.app.NotificationCompat.Builder;

import com.amigo.applocker.BuildConfig;
import com.amigo.applocker.R;
import com.noahmob.AppLocker.ActivityStartingHandler;
import com.noahmob.AppLocker.AppLockerActivity;
import com.noahmob.AppLocker.AppLockerPreference;
import com.noahmob.AppLocker.Utils.DP;
import com.noahmob.AppLocker.listener.ActivityStartingListener;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

public class DetectorService2 extends Service {
    public static final int NOTIFICATION_ID = 1;
    private static boolean constantInited = false;
    private static final Class<?>[] mStartForegroundSignature = new Class[]{Integer.TYPE, Notification.class};
    private static final Class<?>[] mStopForegroundSignature = new Class[]{Boolean.TYPE};
    private static Thread mThread;
    private Method mStartForeground;
    private Object[] mStartForegroundArgs = new Object[2];
    private Method mStopForeground;
    private Object[] mStopForegroundArgs = new Object[1];

    private class MonitorlogThread extends Thread {
        ActivityStartingListener mListener;

        public MonitorlogThread(ActivityStartingListener listener) {
            this.mListener = listener;
        }

        public void run() {
            DP.D("service is running ....");
            while (true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e2) {
                    return;
                }
                ActivityManager am = (ActivityManager) DetectorService2.this.getBaseContext().getSystemService
                        (ACTIVITY_SERVICE);
                if (VERSION.SDK_INT <= 19) {
                    RunningTaskInfo foregroundTaskInfo = (RunningTaskInfo) am.getRunningTasks(1).get(0);
                    String foregroundTaskPackageName = foregroundTaskInfo.topActivity.getPackageName();
                    PackageManager pm = DetectorService2.this.getBaseContext().getPackageManager();
                    PackageInfo foregroundAppPackageInfo = null;
                    String foregroundTaskActivityName = foregroundTaskInfo.topActivity.getShortClassName().toString();
                    try {
                        foregroundAppPackageInfo = pm.getPackageInfo(foregroundTaskPackageName, 0);
                    } catch (NameNotFoundException e) {
                        e.printStackTrace();
                    }

                    if (this.mListener != null) {
                        this.mListener.onActivityStarting(foregroundAppPackageInfo.packageName,
                                foregroundTaskActivityName);
                    }

                } else if (VERSION.SDK_INT < 22) {
                    RunningAppProcessInfo currentInfo = null;
                    Field field = null;
                    try {
                        field = RunningAppProcessInfo.class.getDeclaredField("processState");
                    } catch (NoSuchFieldException e3) {
                        e3.printStackTrace();
                    }
                    for (RunningAppProcessInfo app : am.getRunningAppProcesses()) {
                        if (app.importance == 100 && app.importanceReasonCode == 0) {
                            Integer state = null;
                            try {
                                state = Integer.valueOf(field.getInt(app));
                            } catch (IllegalAccessException e4) {
                                e4.printStackTrace();
                            } catch (IllegalArgumentException e5) {
                                e5.printStackTrace();
                            }
                            if (state == null) {
                                continue;
                            } else if (state.intValue() == 2) {
                                currentInfo = app;
                                break;
                            }
                        }
                    }
                    if (!(currentInfo == null || currentInfo.processName.equals("com.google.android" +
                            ".googlequicksearchbox"))) {
                        String packageName = currentInfo.processName;
                        String activityName = currentInfo.processName;
                        if (packageName.equals(BuildConfig.APPLICATION_ID)) {
                            activityName = ((RunningTaskInfo) am.getRunningTasks(1).get(0)).topActivity
                                    .getShortClassName();
                        }
                        if (this.mListener != null) {
                            this.mListener.onActivityStarting(packageName, activityName);
                        }
                    }
                }
            }
        }
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    void startForegroundCompat(int id, Notification notification) {
        if (this.mStartForeground != null) {
            this.mStartForegroundArgs[0] = Integer.valueOf(id);
            this.mStartForegroundArgs[1] = notification;
            try {
                this.mStartForeground.invoke(this, this.mStartForegroundArgs);
                return;
            } catch (InvocationTargetException e) {
                return;
            } catch (IllegalAccessException e2) {
                return;
            }
        }
        stopForeground(true);
    }

    void stopForegroundCompat(int id) {
        if (this.mStopForeground != null) {
            this.mStopForegroundArgs[0] = Boolean.TRUE;
            try {
                this.mStopForeground.invoke(this, this.mStopForegroundArgs);
                return;
            } catch (InvocationTargetException e) {
                return;
            } catch (IllegalAccessException e2) {
                return;
            }
        }
        stopForeground(false);
    }

    public void onCreate() {
        initConstant();
        try {
            this.mStartForeground = getClass().getMethod("startForeground", mStartForegroundSignature);
            this.mStopForeground = getClass().getMethod("stopForeground", mStopForegroundSignature);
        } catch (NoSuchMethodException e) {
            this.mStopForeground = null;
            this.mStartForeground = null;
        }
        AppLockerPreference.getInstance(this).saveAutoStart(true);
    }

    public void onDestroy() {
        mThread.interrupt();
        stopForegroundCompat(R.string.service_running);
    }

    public void onStart(Intent intent, int startId) {
        handleCommand(intent);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        handleCommand(intent);
        return START_STICKY;
    }

    private void handleCommand(Intent intent) {
        CharSequence text = getText(R.string.service_running);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, AppLockerActivity.class), 0);
        Builder mBuilder = new Builder(this).setSmallIcon(R.drawable.block_all_btn).setContentTitle("AppLocker")
                .setStyle(new BigTextStyle().bigText(text)).setContentText(text).setWhen(System.currentTimeMillis())
                .setPriority(1);
        mBuilder.setContentIntent(contentIntent);
        mBuilder.setAutoCancel(false);
        mBuilder.setOngoing(true);
        startForeground(1, mBuilder.build());
        startMonitorThread((ActivityManager) getSystemService(ACTIVITY_SERVICE));
    }

    private void startMonitorThread(ActivityManager am) {
        if (mThread != null) {
            mThread.interrupt();
        }
        mThread = new MonitorlogThread(new ActivityStartingHandler(this));
        mThread.start();
    }

    private void initConstant() {
        if (!constantInited) {
            Pattern.compile(getResources().getString(R.string.activity_name_pattern), Pattern.CASE_INSENSITIVE);
            getResources().getString(R.string.logcat_command);
            getResources().getString(R.string.logcat_clear_command);
        }
    }
}
