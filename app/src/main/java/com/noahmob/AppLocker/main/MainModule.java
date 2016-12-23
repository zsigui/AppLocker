package com.noahmob.AppLocker.main;

import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;

import com.amigo.applocker.R;
import com.noahmob.AppLocker.AppLockerPreference;
import com.noahmob.AppLocker.ApplicationListActivity;
import com.noahmob.AppLocker.config.Constant;

import java.util.ArrayList;
import java.util.List;

public class MainModule {
    AppLockerPreference appLockerPreference;
    protected Context mContext;
    protected Resources mRes = this.mContext.getResources();
    SharedPreferences sh_Pref;

    public MainModule(Context context) {
        this.mContext = context;
    }

    public void createShortCutOnDesktopIfFirstLaunch() {
    }

    private boolean isAvilible(Context context, String packageName) {
        List<PackageInfo> pinfo = context.getPackageManager().getInstalledPackages(0);
        List<String> pName = new ArrayList();
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                pName.add(((PackageInfo) pinfo.get(i)).packageName);
            }
        }
        return pName.contains(packageName);
    }

    public int getVersionCode(Context context) {
        int i = 0;
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            return i;
        }
    }

    private boolean isCreateNshareShortcut() {
        this.appLockerPreference = new AppLockerPreference(this.mContext);
        this.sh_Pref = this.mContext.getSharedPreferences(Constant.DATA, 0);
        return this.sh_Pref.getBoolean("nshare_short_created", false);
    }

    private void setCreatedNshareShortcut() {
        Editor editor = this.sh_Pref.edit();
        editor.putBoolean("nshare_short_created", true);
        editor.commit();
    }

    private int getLastNshareShortCutCreateVersion() {
        this.appLockerPreference = new AppLockerPreference(this.mContext);
        this.sh_Pref = this.mContext.getSharedPreferences(Constant.DATA, 0);
        return this.sh_Pref.getInt("nshare_versioncode", 0);
    }

    private void setLastNshareShortCutCreateVersion(int versioncode) {
        Editor editor = this.sh_Pref.edit();
        editor.putInt("nshare_versioncode", versioncode);
        editor.commit();
    }

    public void addShortcut() {
        Intent addIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        ShortcutIconResource icon = ShortcutIconResource.fromContext(this.mContext, R.drawable.ic_launcher);
        Intent myIntent = new Intent(this.mContext, ApplicationListActivity.class);
        myIntent.setAction("android.intent.action.MAIN");
        myIntent.addCategory("android.intent.category.LAUNCHER");
        addIntent.putExtra("android.intent.extra.shortcut.NAME", this.mContext.getString(R.string.app_name));
        addIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", icon);
        addIntent.putExtra("android.intent.extra.shortcut.INTENT", myIntent);
        addIntent.putExtra("duplicate", false);
        this.mContext.sendBroadcast(addIntent);
    }
}
