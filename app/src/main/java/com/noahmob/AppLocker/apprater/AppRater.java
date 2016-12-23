package com.noahmob.AppLocker.apprater;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build.VERSION;
import android.util.Log;

import com.amigo.applocker.R;

public class AppRater {
    private static final int DAYS_UNTIL_PROMPT = 13;
    private static int DAYS_UNTIL_PROMPT_FOR_REMIND_LATER = 13;
    private static final int LAUNCHES_UNTIL_PROMPT = 14;
    private static int LAUNCHES_UNTIL_PROMPT_FOR_REMIND_LATER = 15;
    private static final String PREF_APP_VERSION_CODE = "app_version_code";
    private static final String PREF_APP_VERSION_NAME = "app_version_name";
    private static final String PREF_DONT_SHOW_AGAIN = "dontshowagain";
    private static final String PREF_FIRST_LAUNCHED = "date_firstlaunch";
    private static final String PREF_LAUNCH_COUNT = "launch_count";
    private static final String PREF_NAME = "apprater";
    private static final String PREF_REMIND_LATER = "remindmelater";
    private static boolean hideNoButton;
    private static boolean isCancelable = true;
    private static boolean isDark;
    private static boolean isVersionCodeCheckEnabled;
    private static boolean isVersionNameCheckEnabled;
    private static Market market = new GoogleMarket();
    private static boolean themeSet;

    public static void setVersionNameCheckEnabled(boolean versionNameCheck) {
        isVersionNameCheckEnabled = versionNameCheck;
    }

    public static void setVersionCodeCheckEnabled(boolean versionCodeCheck) {
        isVersionCodeCheckEnabled = versionCodeCheck;
    }

    public static void setNumDaysForRemindLater(int daysUntilPromt) {
        DAYS_UNTIL_PROMPT_FOR_REMIND_LATER = daysUntilPromt;
    }

    public static void setNumLaunchesForRemindLater(int launchesUntilPrompt) {
        LAUNCHES_UNTIL_PROMPT_FOR_REMIND_LATER = launchesUntilPrompt;
    }

    public static void setDontRemindButtonVisible(boolean isNoButtonVisible) {
        hideNoButton = isNoButtonVisible;
    }

    public static void setCancelable(boolean cancelable) {
        isCancelable = cancelable;
    }

    public static void app_launched(Context context) {
        app_launched(context, 13, 14);
    }

    public static void app_launched(Context context, int daysUntilPrompt, int launchesUntilPrompt, int daysForRemind, int launchesForRemind) {
        setNumDaysForRemindLater(daysForRemind);
        setNumLaunchesForRemindLater(launchesForRemind);
        app_launched(context, daysUntilPrompt, launchesUntilPrompt);
    }

    public static void app_launched(Context context, int daysUntilPrompt, int launchesUntilPrompt) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, 0);
        Editor editor = prefs.edit();
        ApplicationRatingInfo ratingInfo = ApplicationRatingInfo.createApplicationInfo(context);
        if (isVersionNameCheckEnabled && !ratingInfo.getApplicationVersionName().equals(prefs.getString(PREF_APP_VERSION_NAME, "none"))) {
            editor.putString(PREF_APP_VERSION_NAME, ratingInfo.getApplicationVersionName());
            resetData(context);
            commitOrApply(editor);
        }
        if (isVersionCodeCheckEnabled && ratingInfo.getApplicationVersionCode() != prefs.getInt(PREF_APP_VERSION_CODE, -1)) {
            editor.putInt(PREF_APP_VERSION_CODE, ratingInfo.getApplicationVersionCode());
            resetData(context);
            commitOrApply(editor);
        }
        if (!prefs.getBoolean(PREF_DONT_SHOW_AGAIN, false)) {
            int launches;
            int days;
            if (prefs.getBoolean(PREF_REMIND_LATER, false)) {
                days = DAYS_UNTIL_PROMPT_FOR_REMIND_LATER;
                launches = LAUNCHES_UNTIL_PROMPT_FOR_REMIND_LATER;
            } else {
                days = daysUntilPrompt;
                launches = launchesUntilPrompt;
            }
            long launch_count = prefs.getLong(PREF_LAUNCH_COUNT, 0) + 1;
            editor.putLong(PREF_LAUNCH_COUNT, launch_count);
            Long date_firstLaunch = Long.valueOf(prefs.getLong(PREF_FIRST_LAUNCHED, 0));
            if (date_firstLaunch.longValue() == 0) {
                date_firstLaunch = Long.valueOf(System.currentTimeMillis());
                editor.putLong(PREF_FIRST_LAUNCHED, date_firstLaunch.longValue());
            }
            if (launch_count >= ((long) launches) || System.currentTimeMillis() >= date_firstLaunch.longValue() + ((long) ((((days * 24) * 60) * 60) * 1000))) {
                showRateAlertDialog(context, editor);
            }
            commitOrApply(editor);
        }
    }

    public static void showRateDialog(Context context) {
        showRateAlertDialog(context, null);
    }

    public static void rateNow(Context context) {
        try {
            context.startActivity(new Intent("android.intent.action.VIEW", market.getMarketURI(context)));
        } catch (ActivityNotFoundException e) {
            Log.e(AppRater.class.getSimpleName(), "Market Intent not found");
        }
    }

    public static void setMarket(Market market) {
        market = market;
    }

    public static Market getMarket() {
        return market;
    }

    @TargetApi(11)
    public static void setDarkTheme() {
        isDark = true;
        themeSet = true;
    }

    @TargetApi(11)
    public static void setLightTheme() {
        isDark = false;
        themeSet = true;
    }

    @SuppressLint({"NewApi"})
    private static void showRateAlertDialog(final Context context, final Editor editor) {
        Builder builder;
        if (VERSION.SDK_INT >= 11) {
            builder = new Builder(context, 3);
        } else {
            builder = new Builder(context);
        }
        ApplicationRatingInfo ratingInfo = ApplicationRatingInfo.createApplicationInfo(context);
        builder.setTitle(String.format(context.getString(R.string.dialog_title), new Object[]{ratingInfo.getApplicationName()}));
        builder.setMessage(context.getString(R.string.rate_message));
        builder.setCancelable(isCancelable);
        builder.setPositiveButton(context.getString(R.string.rate), new OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                AppRater.rateNow(context);
                if (editor != null) {
                    editor.putBoolean(AppRater.PREF_DONT_SHOW_AGAIN, true);
                    AppRater.commitOrApply(editor);
                }
                dialog.dismiss();
            }
        });
        builder.setNeutralButton(context.getString(R.string.later), new OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (editor != null) {
                    editor.putLong(AppRater.PREF_FIRST_LAUNCHED, Long.valueOf(System.currentTimeMillis()).longValue());
                    editor.putLong(AppRater.PREF_LAUNCH_COUNT, 0);
                    editor.putBoolean(AppRater.PREF_REMIND_LATER, true);
                    editor.putBoolean(AppRater.PREF_DONT_SHOW_AGAIN, false);
                    AppRater.commitOrApply(editor);
                }
                dialog.dismiss();
            }
        });
        if (!hideNoButton) {
            builder.setNegativeButton(context.getString(R.string.no_thanks), new OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (editor != null) {
                        editor.putBoolean(AppRater.PREF_DONT_SHOW_AGAIN, true);
                        editor.putBoolean(AppRater.PREF_REMIND_LATER, false);
                        editor.putLong(AppRater.PREF_FIRST_LAUNCHED, System.currentTimeMillis());
                        editor.putLong(AppRater.PREF_LAUNCH_COUNT, 0);
                        AppRater.commitOrApply(editor);
                    }
                    dialog.dismiss();
                }
            });
        }
        builder.show();
    }

    @SuppressLint({"NewApi"})
    private static void commitOrApply(Editor editor) {
        if (VERSION.SDK_INT > 8) {
            editor.apply();
        } else {
            editor.commit();
        }
    }

    public static void resetData(Context context) {
        Editor editor = context.getSharedPreferences(PREF_NAME, 0).edit();
        editor.putBoolean(PREF_DONT_SHOW_AGAIN, false);
        editor.putBoolean(PREF_REMIND_LATER, false);
        editor.putLong(PREF_LAUNCH_COUNT, 0);
        editor.putLong(PREF_FIRST_LAUNCHED, System.currentTimeMillis());
        commitOrApply(editor);
    }
}
