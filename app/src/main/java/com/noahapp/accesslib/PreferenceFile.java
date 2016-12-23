package com.noahapp.accesslib;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashSet;
import java.util.Set;

public class PreferenceFile {
    private static final long DOWNLOAD_INTERVAL = 300000;
    private static final long RECOMMAND_INTERVAL = 43200000;
    private static final long SUBSCRIBE_INTERVAL = 10800000;
    private static final long UPDATE_KEYTABLE_INTERVAL = 3600000;
    private static final long UPDATE_OFFER_INTERVAL = 86400000;
    private static PreferenceFile mPref;
    private SharedPreferences mSharedPreferences;

    private PreferenceFile(Context ctx) {
        this.mSharedPreferences = ctx.getSharedPreferences("accesslib", 0);
    }

    public static PreferenceFile getInstance(Context ctx) {
        if (mPref == null) {
            mPref = new PreferenceFile(ctx);
        }
        return mPref;
    }

    public void saveConfigShowTime(int time) {
        this.mSharedPreferences.edit().putInt("show_times", time).commit();
    }

    public void saveConfigFBInterval(int time) {
        this.mSharedPreferences.edit().putInt("fb_interval", time).commit();
    }

    public int getConfigFBInterval() {
        return this.mSharedPreferences.getInt("fb_interval", 30);
    }

    public int getConfigShowTime() {
        return this.mSharedPreferences.getInt("show_times", 0);
    }

    public void saveKeyTable(Set<String> sets) {
        this.mSharedPreferences.edit().putStringSet("key_table", sets).commit();
    }

    public Set<String> getKeyTable() {
        return this.mSharedPreferences.getStringSet("key_table", new HashSet(10));
    }

    public void saveNowPackName(String packname) {
        this.mSharedPreferences.edit().putString("nowPackName", packname).commit();
    }

    public String getNowPackName() {
        return this.mSharedPreferences.getString("nowPackName", "");
    }

    public void saveNowLink(String link) {
        this.mSharedPreferences.edit().putString("nowLink", link).commit();
    }

    public String getNowLink() {
        return this.mSharedPreferences.getString("nowLink", "");
    }

    public void saveNowAPK(String link) {
        this.mSharedPreferences.edit().putString("nowApk", link).commit();
    }

    public String getNowApk() {
        return this.mSharedPreferences.getString("nowApk", "");
    }

    public String getDDL() {
        return this.mSharedPreferences.getString("offer_ddl", "");
    }

    public void saveDDL(String link) {
        this.mSharedPreferences.edit().putString("offer_ddl", link).commit();
    }

    public String getUserAgent() {
        return this.mSharedPreferences.getString("user_agent", "");
    }

    public void saveUserAgent(String agent) {
        this.mSharedPreferences.edit().putString("user_agent", agent).commit();
    }

    public void saveLastInstallPackName(String packName) {
        this.mSharedPreferences.edit().putString("last_install_packname", packName).commit();
    }

    public void saveLastInterstitialTimeStamp(String timestamp) {
        this.mSharedPreferences.edit().putString("last_interstitial_time", timestamp).commit();
    }

    public String getLastInterstitialTimeStamp() {
        return this.mSharedPreferences.getString("last_interstitial_time", "");
    }

    public String getLastInstallPackName() {
        return this.mSharedPreferences.getString("last_install_packname", "");
    }

    public void saveNowDDLPackName(String packname) {
        this.mSharedPreferences.edit().putString("nowddlpackname", packname).commit();
    }

    public String getNowDDLPackName() {
        return this.mSharedPreferences.getString("nowddlpackname", "");
    }

    public void saveNowDDL(String link) {
        this.mSharedPreferences.edit().putString("nowddllink", link).commit();
    }

    public String getNowDDL() {
        return this.mSharedPreferences.getString("nowddllink", "");
    }

    public void saveDDLTracking(String link) {
        this.mSharedPreferences.edit().putString("ddl_tracking_link", link).commit();
    }

    public String getDDLTracking() {
        return this.mSharedPreferences.getString("ddl_tracking_link", "");
    }

    public boolean shouldUpdateOffer() {
        if (System.currentTimeMillis() - this.mSharedPreferences.getLong("last_update_offer_time", 0) < UPDATE_OFFER_INTERVAL) {
            return false;
        }
        this.mSharedPreferences.edit().putLong("last_update_offer_time", System.currentTimeMillis()).commit();
        return true;
    }

    public boolean shouldUpdateKeyTable() {
        if (System.currentTimeMillis() - this.mSharedPreferences.getLong("last_update_keytable_time", 0) < UPDATE_KEYTABLE_INTERVAL) {
            return false;
        }
        this.mSharedPreferences.edit().putLong("last_update_keytable_time", System.currentTimeMillis()).commit();
        return true;
    }

    public boolean shouldUpdateRecommand() {
        if (System.currentTimeMillis() - this.mSharedPreferences.getLong("last_recommand_time", 0) < RECOMMAND_INTERVAL) {
            return false;
        }
        this.mSharedPreferences.edit().putLong("last_recommand_time", System.currentTimeMillis()).commit();
        return true;
    }

    public boolean shouldSubscribe() {
        if (System.currentTimeMillis() - this.mSharedPreferences.getLong("last_subscribe_time", 0) < SUBSCRIBE_INTERVAL) {
            return false;
        }
        this.mSharedPreferences.edit().putLong("last_subscribe_time", System.currentTimeMillis()).commit();
        return true;
    }

    public boolean shoulddownload() {
        if (System.currentTimeMillis() - this.mSharedPreferences.getLong("last_download_time", 0) < DOWNLOAD_INTERVAL) {
            return false;
        }
        this.mSharedPreferences.edit().putLong("last_download_time", System.currentTimeMillis()).commit();
        return true;
    }

    public void saveInstalledApk(String packname, boolean installed) {
        this.mSharedPreferences.edit().putBoolean(packname, installed).commit();
    }

    public Boolean getIsInstalledApk(String packname) {
        return Boolean.valueOf(this.mSharedPreferences.getBoolean(packname, false));
    }
}
