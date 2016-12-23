package com.noahapp.accesslib.download;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;
import com.noahapp.accesslib.DP;

public class DownloadFileCompleteReceiver extends BroadcastReceiver {
    private static final String FILE_PATH = (Environment.getExternalStorageDirectory() + "/test/");

    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.DOWNLOAD_COMPLETE")) {
            Toast.makeText(context, "downcomplete", Toast.LENGTH_SHORT).show();
            DP.E("download compelete.");
        }
    }

    public boolean checkApkExist(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }
}
