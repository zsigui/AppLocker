package com.noahmob.AppLocker.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.amigo.applocker.R;
import com.noahmob.AppLocker.AppLockerPreference;

import java.util.ArrayList;

public class InstallDetectedActivity extends Activity implements OnClickListener {
    public static final String NOTIFY_PACKAGE_NAME = "notify_package_name";
    String m_PackageName;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_install_notify);
        this.m_PackageName = getIntent().getStringExtra("notify_package_name");
        if (TextUtils.isEmpty(this.m_PackageName)) {
            finish();
            return;
        }
        this.m_PackageName = this.m_PackageName.substring(8);
        findViewById(R.id.cancle).setOnClickListener(this);
        findViewById(R.id.lock).setOnClickListener(this);
        String appName = getAppname(this, this.m_PackageName);
        if (TextUtils.isEmpty(appName)) {
            appName = "new app";
        }
        ((TextView) findViewById(R.id.install_content)).setText(getString(R.string.alp_42447968_cmd_install_lock_content, new Object[]{appName}));
    }

    private void saveToPreference(Context ctx, String addPackage) {
        ArrayList<String> allowed = new ArrayList<>();
        for (String packageName : AppLockerPreference.getInstance(ctx).getApplicationList()) {
            allowed.add(packageName);
        }
        allowed.add(addPackage);
        AppLockerPreference.getInstance(ctx).saveApplicationList((String[]) allowed.toArray(new String[0]));
    }

    public String getAppname(Context context, String packname) {
        String name = null;
        try {
            PackageManager pm = context.getPackageManager();
            name = pm.getApplicationLabel(pm.getApplicationInfo(packname, PackageManager.GET_META_DATA)).toString();
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return name;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancle:
                finish();
                return;
            case R.id.lock:
                saveToPreference(this, this.m_PackageName);
                finish();
                return;
            default:
                return;
        }
    }
}
