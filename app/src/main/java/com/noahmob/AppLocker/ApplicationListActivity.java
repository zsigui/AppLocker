package com.noahmob.AppLocker;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amigo.applocker.R;
import com.noahapp.accesslib.BaseActivity;
import com.noahapp.accesslib.PreferenceFile;
import com.noahmob.AppLocker.Activity.AppLockerPreferenceActivity;
import com.noahmob.AppLocker.Activity.SetPasswordActivity;
import com.noahmob.AppLocker.Utils.DP;
import com.noahmob.AppLocker.Utils.WaveDrawable;
import com.noahmob.AppLocker.Widget.SlideSwitch;
import com.noahmob.AppLocker.Widget.SlideSwitch.SlideListener;
import com.noahmob.AppLocker.config.Constant;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class ApplicationListActivity extends BaseActivity implements OnClickListener, TextWatcher {
    private static final Intent sSettingsIntent = new Intent("android.settings.ACCESSIBILITY_SETTINGS");
    public final ArrayList<AppItem> Applications = new ArrayList();
    MyListAdapter adpt;
    AppLockerPreference app_pre;
    ArrayList<AppItem> filter_list;
    ListView list_app;
    ProgressDialog loading;
    SharedPreferences sh_Pref;

    public class AppItem implements Comparable<AppItem> {
        public Drawable Icon;
        public Boolean Important = false;
        public Boolean Included = false;
        public String Label = "";
        public String Name = "";
        public String PackageName = "";
        public int layoutType = 0;

        public AppItem(String Label, String Name, String PackageName, Drawable Icon, boolean Included, boolean Important) {
            this.Label = Label;
            this.Name = Name;
            this.Icon = Icon;
            this.PackageName = PackageName;
            this.Included = Boolean.valueOf(Included);
            this.Important = Boolean.valueOf(Important);
        }

        public int getLayoutType() {
            return this.layoutType;
        }

        public void setLayoutType(int layoutType) {
            this.layoutType = layoutType;
        }

        public String getLabel() {
            return this.Label;
        }

        public void setLabel(String label) {
            this.Label = label;
        }

        public String getName() {
            return this.Name;
        }

        public void setName(String name) {
            this.Name = name;
        }

        public String getPackageName() {
            return this.PackageName;
        }

        public void setPackageName(String packageName) {
            this.PackageName = packageName;
        }

        public Drawable getIcon() {
            return this.Icon;
        }

        public void setIcon(Drawable icon) {
            this.Icon = icon;
        }

        public Boolean getIncluded() {
            return this.Included;
        }

        public void setIncluded(Boolean included) {
            this.Included = included;
        }

        public Boolean getImportant() {
            return this.Important;
        }

        public void setImportant(Boolean important) {
            this.Important = important;
        }

        public int compareTo(AppItem another) {
            if (this.Important.booleanValue() && !another.Important.booleanValue()) {
                return -1;
            }
            if (this.Important.booleanValue() || !another.Important.booleanValue()) {
                return this.Label.compareTo(another.Label);
            }
            return 1;
        }
    }

    private class LoadApplicationTask extends AsyncTask<Integer, Integer, Integer> {
        private ArrayList<AppItem> items;

        private LoadApplicationTask() {
            this.items = new ArrayList();
        }

        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            Collections.sort(this.items);
            ApplicationListActivity.this.Applications.addAll(this.items);
            int importantItemNum = getImportItemCount();
            AppItem title_general = new AppItem("", "General", "", null, false, false);
            title_general.setLayoutType(1);
            ApplicationListActivity.this.Applications.add(importantItemNum, title_general);
            AppItem title_important = new AppItem("", "Advanced", "", null, false, false);
            title_important.setLayoutType(1);
            ApplicationListActivity.this.Applications.add(0, title_important);
            ApplicationListActivity.this.loading.dismiss();
            ApplicationListActivity.this.adpt = new MyListAdapter(ApplicationListActivity.this, R.layout.applicationlist_item, ApplicationListActivity.this.Applications, importantItemNum);
            ApplicationListActivity.this.list_app.setAdapter(ApplicationListActivity.this.adpt);
        }

        protected Integer doInBackground(Integer... params) {
            Intent intent = new Intent("android.intent.action.MAIN", null);
            intent.addCategory("android.intent.category.LAUNCHER");
            List<ResolveInfo> mApps = ApplicationListActivity.this.getPackageManager().queryIntentActivities(intent, 0);
            int length = mApps.size();
            for (int i = 0; i < length; i++) {
                ResolveInfo info = (ResolveInfo) mApps.get(i);
                Drawable image = info.loadIcon(ApplicationListActivity.this.getPackageManager());
                boolean included = false;
                for (Object equals : AppLockerPreference.getInstance(ApplicationListActivity.this).getApplicationList()) {
                    if (info.activityInfo.packageName.equals(equals)) {
                        included = true;
                        break;
                    }
                }
                this.items.add(new AppItem(info.activityInfo.loadLabel(ApplicationListActivity.this.getPackageManager()).toString(), info.activityInfo.name, info.activityInfo.packageName, image, included, checkImportance(info.activityInfo.packageName)));
            }
            return Integer.valueOf(0);
        }

        private boolean checkImportance(String packageName) {
//            if (zze.GOOGLE_PLAY_STORE_PACKAGE.equals(packageName) || "com.android.settings".equals(packageName)) {
//                return true;
//            }
            return false;
        }

        private int getImportItemCount() {
            int count = 0;
            for (int i = 0; i < this.items.size(); i++) {
                if (((AppItem) this.items.get(i)).Important.booleanValue()) {
                    count++;
                }
            }
            return count;
        }
    }

    static class MyHolder {
        ImageView app_icon;
        TextView app_text;
        SlideSwitch checkbox;
        ImageView important_icon;

        MyHolder() {
        }
    }

    public class MyListAdapter extends ArrayAdapter<AppItem> {
        final /* synthetic */ boolean assertionsDisabled = (!ApplicationListActivity.class.desiredAssertionStatus());
        private static final int CATAGORY_COUNT = 2;
        Activity activity;
        int importNum;
        ArrayList<AppItem> item_list;
        int resource_id;

        public MyListAdapter(Activity activity, int resource_id, ArrayList<AppItem> browser_grid_data, int num) {
            super(activity, resource_id, browser_grid_data);
            this.activity = activity;
            this.resource_id = resource_id;
            this.item_list = browser_grid_data;
            this.importNum = num;
            sortAppList();
        }

        public MyListAdapter(Activity activity, int resource_id, ArrayList<AppItem> browser_grid_data) {
            super(activity, resource_id, browser_grid_data);
            this.activity = activity;
            this.resource_id = resource_id;
            this.item_list = browser_grid_data;
        }

        private void sortAppList() {
            int i;
            ArrayList<AppItem> tmps = new ArrayList();
            for (i = 0; i < this.importNum + 2; i++) {
                tmps.add(this.item_list.get(i));
            }
            for (i = this.importNum + 2; i < this.item_list.size(); i++) {
                if (((AppItem) this.item_list.get(i)).Included.booleanValue()) {
                    tmps.add(this.importNum + 2, this.item_list.get(i));
                } else {
                    tmps.add(this.item_list.get(i));
                }
            }
            this.item_list.clear();
            this.item_list.addAll(tmps);
        }

        public int getCount() {
            return this.item_list.size();
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            if (getItemViewType(position) == 0) {
                MyHolder holder;
                if (convertView == null) {
                    holder = new MyHolder();
                    View myView = this.activity.getLayoutInflater().inflate(this.resource_id, parent, false);
                    if (assertionsDisabled || myView != null) {
                        holder.app_text = (TextView) myView.findViewById(R.id.app_text);
                        holder.app_icon = (ImageView) myView.findViewById(R.id.app_icon);
                        holder.checkbox = (SlideSwitch) myView.findViewById(R.id.checkbox);
                        holder.important_icon = (ImageView) myView.findViewById(R.id.important_icon);
                        myView.setTag(holder);
                        convertView = myView;
                    } else {
                        throw new AssertionError();
                    }
                }
                holder = (MyHolder) convertView.getTag();
                holder.checkbox.setSlideListener(null);
                final AppItem myitem = (AppItem) this.item_list.get(position);
                holder.app_text.setText(myitem.Label);
                if (myitem.Icon != null) {
                    holder.app_icon.setImageDrawable(myitem.Icon);
                }
                holder.important_icon.setVisibility(View.INVISIBLE);
                holder.checkbox.setState(myitem.Included);
                holder.checkbox.setSlideListener(new SlideListener() {
                    public void open() {
                        myitem.Included = true;
                        MyListAdapter.this.item_list.set(position, myitem);
                        ApplicationListActivity.this.saveToPreference();
                    }

                    public void close() {
                        myitem.Included = false;
                        MyListAdapter.this.item_list.set(position, myitem);
                        ApplicationListActivity.this.saveToPreference();
                    }
                });
            } else {
                TitleHolder titleHoler;
                if (convertView == null) {
                    titleHoler = new TitleHolder();
                    View titleView = this.activity.getLayoutInflater().inflate(R.layout.title_item, parent, false);
                    titleHoler.title = (TextView) titleView.findViewById(R.id.title_text);
                    titleView.setTag(titleHoler);
                    convertView = titleView;
                } else {
                    titleHoler = (TitleHolder) convertView.getTag();
                }
                titleHoler.title.setText(((AppItem) this.item_list.get(position)).Name);
            }
            return convertView;
        }

        public int getItemViewType(int position) {
            return ((AppItem) this.item_list.get(position)).layoutType;
        }

        public int getViewTypeCount() {
            return 2;
        }
    }

    static class TitleHolder {
        TextView title;

        TitleHolder() {
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.applicationlist);
//        FacebookSdk.sdkInitialize(getApplicationContext());
        if (isFirstTime()) {
            findViewById(R.id.allow_all_text).setOnClickListener(this);
            findViewById(R.id.block_all_text).setOnClickListener(this);
            findViewById(R.id.default_text).setOnClickListener(this);
            findViewById(R.id.tabsetting).setOnClickListener(this);
            ((EditText) findViewById(R.id.textSearch)).addTextChangedListener(this);
            this.list_app = (ListView) findViewById(R.id.app_list);
            this.loading = ProgressDialog.show(this, "Please wait", "Gathering application... ");
            new LoadApplicationTask().execute(new Integer[0]);
            if (VERSION.SDK_INT >= 15) {
                CheckIfUseAccessiableService();
            } else if (!PreferenceManager.getDefaultSharedPreferences(this).getString("password", "").equals("")) {
            }
        } else {
            finish();
            startActivity(new Intent(this, SetPasswordActivity.class));
        }
        saveUserAgent();
    }

    private void saveUserAgent() {
        WebView webview = new WebView(this);
        webview.layout(0, 0, 0, 0);
        String ua = webview.getSettings().getUserAgentString();
        if (!TextUtils.isEmpty(ua)) {
            PreferenceFile.getInstance(this).saveUserAgent(ua);
        }
    }

    public static String printKeyHash(Activity context) {
        int i = 0;
        String str = null;
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getApplicationContext().getPackageName(),
                    PackageManager.GET_SIGNATURES);
            DP.D("Package Name=", context.getApplicationContext().getPackageName());
            Signature[] signatureArr = packageInfo.signatures;
            int length = signatureArr.length;
            String key = null;
            while (i < length) {
                try {
                    Signature signature = signatureArr[i];
                    MessageDigest md = MessageDigest.getInstance("SHA");
                    md.update(signature.toByteArray());
                    str = new String(Base64.encode(md.digest(), 0));
                    DP.D("hash key:" + str);
                    i++;
                    key = str;
                } catch (NoSuchAlgorithmException e) {
                    DP.D("No such an algorithm" + e);
                }
            }
            return key;
        } catch (NameNotFoundException e) {
            DP.D("Name not found" + e.toString());
        } catch (Exception e) {
            DP.D("Exception" + e.toString());
        }
        return null;
    }

    private void blockActivity(String packageName, String activityName) {
        if (AppLockerPreference.getInstance(this).isPasswordType()) {
            Intent lockIntent = new Intent(this, LockScreenActivity.class);
            lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            lockIntent.putExtra("locked activity name", activityName).putExtra("locked package name", packageName);
            startActivity(lockIntent);
            return;
        }
        Intent lockIntent = new Intent(this, LockScreenPatternActivity.class);
        lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        lockIntent.putExtra("locked activity name", activityName).putExtra("locked package name", packageName);
        startActivity(lockIntent);
    }

    private void CheckIfUseAccessiableService() {
        blockActivity(getPackageName(), getPackageName());
        if (!isAccessibilitySettingsOn()) {
            showAlertDialog();
        }
    }

    private void GoAccessiableSettingActivity() {
        startActivity(sSettingsIntent);
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.alp_42447968_cmd_android_m_above_toast), Toast.LENGTH_LONG).show();
    }

    @SuppressLint({"NewApi"})
    private void showAlertDialog() {
        Builder builder;
        if (VERSION.SDK_INT >= 11) {
            builder = new Builder(this, 3);
        } else {
            builder = new Builder(this);
        }
        builder.setTitle(getString(R.string.alp_42447968_cmd_Attention));
        builder.setMessage(getString(R.string.alp_42447968_cmd_android_m_above));
        builder.setCancelable(true);
        builder.setPositiveButton(getString(R.string.alp_42447968_cmd_confirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ApplicationListActivity.this.GoAccessiableSettingActivity();
                dialog.dismiss();
            }
        });
        builder.setNeutralButton(getString(R.string.alp_42447968_cmd_cancle), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ApplicationListActivity.this.finish();
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @SuppressLint({"NewApi"})
    private boolean isAccessibleEnabled() {
        List<AccessibilityServiceInfo> infos = ((AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE)).getEnabledAccessibilityServiceList(-1);
        for (AccessibilityServiceInfo info : infos) {
            if (info.getId().equals(getPackageName() + "/com.noahmob.AppLocker.Service.MyAccessibilityService")) {
                return true;
            }
        }
        return false;
    }

    private boolean isAccessibilitySettingsOn() {
        int accessibilityEnabled = 0;
        final String service = getPackageName() + "/com.noahmob.AppLocker.Service.MyAccessibilityService";
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            // ignored
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(
                    getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void startService() {
    }

    private void saveToPreference() {
        ArrayList<String> allowed = new ArrayList();
        Iterator it = this.Applications.iterator();
        while (it.hasNext()) {
            AppItem app = (AppItem) it.next();
            if (app.Included.booleanValue()) {
                allowed.add(app.PackageName);
            }
        }
        AppLockerPreference.getInstance(this).saveApplicationList((String[]) allowed.toArray(new String[0]));
    }

    private boolean isFirstTime() {
        this.app_pre = new AppLockerPreference(getApplicationContext());
        this.sh_Pref = getSharedPreferences(Constant.DATA, 0);
        return this.sh_Pref.getBoolean("isfirst", false);
//        return true;
    }

    private void dismissLoadDialog() {
        if (this.loading != null) {
            this.loading.dismiss();
        }
    }

    protected void onPause() {
        dismissLoadDialog();
//        AppEventsLogger.deactivateApp(this);
        super.onPause();
    }

    protected void onResume() {
//        AppEventsLogger.activateApp(this);
        super.onResume();
    }

    protected void onDestroy() {
        dismissLoadDialog();
        super.onDestroy();
    }

    public void onBackPressed() {
        super.onBackPressed();
    }

    public void WaveAnimation(Context c, View v) {
        if (VERSION.SDK_INT >= 14) {
            WaveDrawable waveDrawable = new WaveDrawable(Color.parseColor("#aa9f9f9f"), 150, true);
            v.setBackgroundDrawable(waveDrawable);
            waveDrawable.startAnimation();
        }
    }

    public void onClick(View view) {
        Iterator it;
        switch (view.getId()) {
            case R.id.tabsetting:
                startActivity(new Intent(this, AppLockerPreferenceActivity.class));
                return;
            case R.id.default_text:
                WaveAnimation(getApplicationContext(), view);
                it = this.Applications.iterator();
                while (it.hasNext()) {
                    AppItem app = (AppItem) it.next();
                    app.Included = app.Important;
                }
                saveToPreference();
                if (this.adpt != null) {
                    this.adpt.notifyDataSetChanged();
                    return;
                }
                return;
            case R.id.block_all_text:
                WaveAnimation(getApplicationContext(), view);
                it = this.Applications.iterator();
                while (it.hasNext()) {
                    ((AppItem) it.next()).Included = true;
                }
                saveToPreference();
                if (this.adpt != null) {
                    this.adpt.notifyDataSetChanged();
                    return;
                }
                return;
            case R.id.allow_all_text:
                WaveAnimation(getApplicationContext(), view);
                it = this.Applications.iterator();
                while (it.hasNext()) {
                    ((AppItem) it.next()).Included = false;
                }
                saveToPreference();
                if (this.adpt != null) {
                    this.adpt.notifyDataSetChanged();
                    return;
                }
                return;
            default:
                return;
        }
    }

    public void afterTextChanged(Editable arg0) {
    }

    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
        this.filter_list = new ArrayList();
        for (int i = 0; i < this.Applications.size(); i++) {
            AppItem data = (AppItem) this.Applications.get(i);
            if (data.Label.toLowerCase(Locale.getDefault()).startsWith(s.toString().toLowerCase(Locale.getDefault()))) {
                this.filter_list.add(data);
            }
        }
        if (getApplicationContext() != null) {
            this.adpt = new MyListAdapter(this, R.layout.applicationlist_item, this.filter_list);
            this.list_app.setAdapter(this.adpt);
        }
    }
}
