package com.noahmob.AppLocker.upgrade;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.Toast;

import com.amigo.applocker.R;
import com.noahmob.AppLocker.Utils.DP;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class CheckLatestVersionTask extends AsyncTask<Void, Void, Void> {
    private boolean mAuto = false;
    private String mCheckUpdateApkUrl;
    private CheckVersionModel mCheckVersionModel;
    private Activity mContext;
    private AppUpdateManager mManager;

    public CheckLatestVersionTask(Activity context, boolean isAuto) {
        this.mContext = context;
        this.mAuto = isAuto;
        this.mManager = new AppUpdateManager(context);
        this.mCheckVersionModel = new CheckVersionModel();
    }

    protected Void doInBackground(Void... params) {
        PackageManager pm = this.mContext.getPackageManager();
        return null;
    }

    private Map<String, String> getUpdateUrlParams(int versionCode) {
        Map<String, String> paramsMap = new LinkedHashMap();
        paramsMap.put("apkType", NetworkConst.APK_TYPE);
//        paramsMap.put(ServerProtocol.FALLBACK_DIALOG_PARAM_VERSION, String.valueOf(versionCode));
        String channel = NetworkHelper.getAppDispatchChannel(this.mContext);
        if (!TextUtils.isEmpty(channel)) {
            paramsMap.put("channel", channel);
        }
        paramsMap.put("language", Locale.getDefault().getLanguage());
        paramsMap.put("country", Locale.getDefault().getCountry());
        return paramsMap;
    }

    protected void onPreExecute() {
        super.onPreExecute();
    }

    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        String code = this.mCheckVersionModel.getCode();
        if (code == null) {
            return;
        }
        if (code.equals(NetworkConst.UPDATE_CODE_OK)) {
            this.mManager.checkUpdate(this.mContext, true, this.mCheckVersionModel);
        } else if (!code.equals(NetworkConst.UPDATE_CODE_NO_LATEST_VERSION)) {
            DP.E("Serve return error");
        } else if (!this.mAuto) {
            Toast.makeText(this.mContext, R.string.soft_update_no, 1).show();
        }
    }

    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
