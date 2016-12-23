package com.noahmob.AppLocker.upgrade;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.text.Html;

import com.amigo.applocker.R;

public class AppUpdateManager {
    private CheckVersionModel mCheckVersionModel;
    private Context mContext;
    private Dialog mUpdateDialog;

    public AppUpdateManager(Context context) {
        this.mContext = context;
    }

    public void checkUpdate(Activity activity, boolean checkUpdate, CheckVersionModel checkVersionModel) {
        this.mCheckVersionModel = checkVersionModel;
        showNotifyUserUpdateDialog(activity);
    }

    private void showNotifyUserUpdateDialog(final Activity activity) {
        if (!activity.isFinishing()) {
            if (this.mCheckVersionModel.sholdAutoupdate()) {
                downloadNow();
            } else if (this.mUpdateDialog == null || !this.mUpdateDialog.isShowing()) {
                Builder builder = new Builder(this.mContext);
                if (this.mCheckVersionModel.getUpdatelogs() != null) {
                    builder.setMessage(Html.fromHtml(this.mCheckVersionModel.getUpdatelogs()));
                } else {
                    builder.setMessage(R.string.soft_update_info);
                }
                builder.setTitle(R.string.soft_update_title);
                builder.setPositiveButton(R.string.upgrade_button_ok, new OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        AppUpdateManager.this.downloadNow();
                        if (AppUpdateManager.this.mCheckVersionModel.shouldForceupdate()) {
                            activity.finish();
                        }
                    }
                });
                builder.setNegativeButton(R.string.upgrade_button_cancle, new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (AppUpdateManager.this.mCheckVersionModel.shouldForceupdate()) {
                            activity.finish();
                        }
                    }
                });
                this.mUpdateDialog = builder.create();
                if (!activity.isFinishing()) {
                    this.mUpdateDialog.show();
                }
            }
        }
    }

    private void downloadNow() {
        String url = this.mCheckVersionModel.getDownloadUrl();
        String versionName = this.mCheckVersionModel.getVersioname();
        Intent intent = new Intent(this.mContext, NetworkService.class);
        intent.setAction(NetworkConst.ACTION_DOWNLOAD);
        intent.putExtra(NetworkConst.EXTRA_DOWNLOAD_URL, url);
        intent.putExtra(NetworkConst.EXTRA_DOWNLOAD_VERSIONNAME, versionName);
        intent.putExtra(NetworkConst.EXTRA_DOWNLOAD_SHOW_TOAST, !this.mCheckVersionModel.sholdAutoupdate());
        this.mContext.startService(intent);
    }
}
