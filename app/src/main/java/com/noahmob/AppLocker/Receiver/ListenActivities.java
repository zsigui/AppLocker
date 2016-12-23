package com.noahmob.AppLocker.Receiver;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import com.noahmob.AppLocker.LockScreenActivity;

public class ListenActivities extends Thread {
    ActivityManager am = null;
    Context context = null;
    boolean exit = false;

    public ListenActivities(Context con) {
        this.context = con;
        this.am = (ActivityManager) this.context.getSystemService(Context.ACTIVITY_SERVICE);
    }

    public void run() {
        Looper.prepare();
        while (!this.exit) {
            String activityName = ((RunningTaskInfo) this.am.getRunningTasks(10).get(0)).topActivity.getClassName();
            Log.d("topActivity", "CURRENT Activity ::" + activityName);
            if (activityName.equals("com.android.packageinstaller.UninstallerActivity")) {
                this.context.startActivity(new Intent(this.context, LockScreenActivity.class).setFlags(268435456));
                this.exit = true;
                Toast.makeText(this.context, "Done with pre-uninstallation tasks... Exiting Now", Toast.LENGTH_SHORT).show();
            } else if (activityName.equals("com.android.settings.ManageApplications")) {
                this.exit = true;
            }
        }
        Looper.loop();
    }
}
