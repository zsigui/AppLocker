package com.noahmob.AppLocker;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;

public class AppLockerPreference implements OnSharedPreferenceChangeListener {
    private static final String PREF_APPLICATION_LIST = "application_list";
    private static final String PREF_AUTO_START = "start_service_after_boot";
    private static final String PREF_LOCK_TYPE = "lock_type";
    private static final String PREF_PASSWORD = "password";
    private static final String PREF_PSW_RETRIVE_CODE = "psw_retrive_code";
    private static final String PREF_RANDOM_KEYBOARD = "random_password_keyboard";
    private static final String PREF_RELOCK_POLICY = "relock_policy";
    private static final String PREF_RELOCK_TIMEOUT = "relock_timeout";
    private static final String PREF_RELOCK_TIMEOUT_RECENTLY = "relock_timeout_recently";
    private static final String PREF_SERVICE_ENABLED = "service_enabled";
    private static final String PREF_USER_EMAIL = "user_email";
    private static AppLockerPreference mInstance;
    private String[] mApplicationList;
    private boolean mAutoStart;
    private boolean mIsPasswordType;
    private int mLastRelockTimeout;
    private String mPassword;
    private SharedPreferences mPref;
    private boolean mRandom_Keybord;
    private boolean mRelockPolice;
    private int mRelockTimeout;
    private boolean mServiceEnabled;

    public boolean isAutoStart() {
        return this.mAutoStart;
    }

    public boolean isRandomKeyboard() {
        return this.mRandom_Keybord;
    }

    public boolean isServiceEnabled() {
        return this.mServiceEnabled;
    }

    public boolean isRelockPoliceEnabled() {
        return this.mRelockPolice;
    }

    public void saveAutoStart(boolean isAutoStart) {
        this.mAutoStart = isAutoStart;
        this.mPref.edit().putBoolean(PREF_AUTO_START, isAutoStart).commit();
    }

    public void saveServiceEnabled(boolean serviceEnabled) {
        this.mServiceEnabled = serviceEnabled;
        this.mPref.edit().putBoolean(PREF_SERVICE_ENABLED, serviceEnabled).commit();
    }

    public void saveRandomKeyboardEnabled(boolean randomEnabled) {
        this.mRandom_Keybord = randomEnabled;
        this.mPref.edit().putBoolean(PREF_RANDOM_KEYBOARD, randomEnabled).commit();
    }

    public void saveRelockPoliceEnabled(boolean relockPoliceEnabled) {
        this.mRelockPolice = relockPoliceEnabled;
        this.mPref.edit().putBoolean(PREF_RELOCK_POLICY, relockPoliceEnabled).commit();
    }

    public void saveRelockTimeOut(String timeout) {
        this.mRelockTimeout = Integer.parseInt(timeout);
        this.mPref.edit().putString(PREF_RELOCK_TIMEOUT, timeout).commit();
    }

    public void saveUserEmail(String email) {
        this.mPref.edit().putString(PREF_USER_EMAIL, email).commit();
    }

    public String getUserEmail() {
        return this.mPref.getString(PREF_USER_EMAIL, "");
    }

    public void saveRetriveCode(String code) {
        this.mPref.edit().putString(PREF_PSW_RETRIVE_CODE, code).commit();
    }

    public String getRetriveCode() {
        return this.mPref.getString(PREF_PSW_RETRIVE_CODE, "");
    }

    public void saveLastRelockTimeOut(String lastTimeout) {
        this.mLastRelockTimeout = Integer.parseInt(lastTimeout);
        this.mPref.edit().putInt(PREF_RELOCK_TIMEOUT_RECENTLY, this.mLastRelockTimeout).commit();
    }

    public String[] getApplicationList() {
        return this.mApplicationList;
    }

    public void saveApplicationList(String[] applicationList) {
        this.mApplicationList = applicationList;
        String combined = "";
        for (String str : this.mApplicationList) {
            combined = combined + str + ";";
        }
        this.mPref.edit().putString(PREF_APPLICATION_LIST, combined).commit();
    }

    public AppLockerPreference(Context context) {
        this.mPref = PreferenceManager.getDefaultSharedPreferences(context);
        this.mPref.registerOnSharedPreferenceChangeListener(this);
        reloadPreferences();
    }

    private void reloadPreferences() {
        this.mServiceEnabled = this.mPref.getBoolean(PREF_SERVICE_ENABLED, true);
        this.mIsPasswordType = this.mPref.getBoolean(PREF_LOCK_TYPE, true);
        this.mApplicationList = this.mPref.getString(PREF_APPLICATION_LIST, "").split(";");
        this.mAutoStart = this.mPref.getBoolean(PREF_AUTO_START, true);
        this.mPassword = this.mPref.getString(PREF_PASSWORD, "");
        this.mRandom_Keybord = this.mPref.getBoolean(PREF_RANDOM_KEYBOARD, false);
        this.mLastRelockTimeout = this.mPref.getInt(PREF_RELOCK_TIMEOUT_RECENTLY, 2);
        this.mRelockPolice = this.mPref.getBoolean(PREF_RELOCK_POLICY, false);
        if (this.mRelockPolice) {
            try {
                this.mRelockTimeout = Integer.parseInt(this.mPref.getString(PREF_RELOCK_TIMEOUT, "-1"));
                return;
            } catch (Exception e) {
                this.mRelockTimeout = -1;
                return;
            }
        }
        this.mRelockTimeout = -1;
    }

    public static AppLockerPreference getInstance(Context context) {
        if (mInstance != null) {
            return mInstance;
        }
        AppLockerPreference appLockerPreference = new AppLockerPreference(context);
        mInstance = appLockerPreference;
        return appLockerPreference;
    }

    public int getRelockTimeout() {
        return this.mRelockTimeout;
    }

    public int getLastRelockTimeout() {
        return this.mLastRelockTimeout;
    }

    public String getPassword() {
        return this.mPassword;
    }

    public void savePassword(String password) {
        System.out.println(password);
        this.mPassword = password;
        this.mPref.edit().putString(PREF_PASSWORD, password).commit();
        String s = this.mPref.getString(PREF_PASSWORD, "");
    }

    public void saveSecQue(String ans) {
        System.out.println(ans);
        this.mPref.edit().putString("securityAns", ans).commit();
        String s = this.mPref.getString("securityAns", "");
    }

    public boolean getSecQue(String ans) {
        System.out.println(ans);
        if (this.mPref.getString("securityAns", "").equals(ans)) {
            return true;
        }
        return false;
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        reloadPreferences();
    }

    public boolean isPasswordType() {
        return this.mPref.getBoolean(PREF_LOCK_TYPE, true);
    }

    public void savePasswordType(boolean isPin) {
        this.mIsPasswordType = isPin;
        this.mPref.edit().putBoolean(PREF_LOCK_TYPE, isPin).commit();
    }
}
