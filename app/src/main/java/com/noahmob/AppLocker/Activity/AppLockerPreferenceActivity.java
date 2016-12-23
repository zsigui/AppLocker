package com.noahmob.AppLocker.Activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.Toast;

import com.amigo.applocker.R;
import com.noahmob.AppLocker.ActivityStartingHandler;
import com.noahmob.AppLocker.AppLockerPreference;
import com.noahmob.AppLocker.LockPatternActivity;
import com.noahmob.AppLocker.Service.DetectorService;
import com.noahmob.AppLocker.Service.MyAccessibilityService;
import com.noahmob.AppLocker.adapter.SettingAdapter;
import com.noahmob.AppLocker.adapter.SettingAdapter.CheboxChangeListener;
import com.noahmob.AppLocker.entity.SettingItem;
import com.noahmob.AppLocker.upgrade.CheckLatestVersionTask;

import java.util.ArrayList;

import haibison.android.lockpattern.utils.AlpSettings.Security;

public class AppLockerPreferenceActivity extends PreferenceActivity {
    private static final int REQ_CHANGE_PATTERN = 2;
    private static final int REQ_CREATE_PATTERN = 1;
    int MY_DIALOG = 1;
    int PASSWORD_TYPE = 4;
    String Password1 = "";
    int SET_PWD_DIALOG = 2;
    int SET_RELOCK_TIME = 3;
    AppLockerPreference appLockerPreference;
    View item_Applocker_Service;
    View item_Change_Password;
    View item_Random_keyboard;
    View item_Rate_app;
    View item_Select_Relock_time;
    View item_set_Relock_Time;
    SettingAdapter mAdapter;
    private int nowRelockTimeIndex = -1;
    String password = "";
    char[] pattern;
    OnSharedPreferenceChangeListener serviceEnabledListener = new OnSharedPreferenceChangeListener() {
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (!key.equals("service_enabled")) {
                return;
            }
            if (sharedPreferences.getBoolean(key, false)) {
                AppLockerPreferenceActivity.this.startService();
            } else {
                AppLockerPreferenceActivity.this.stopService();
            }
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settinglist);
        findViewById(R.id.back).setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                AppLockerPreferenceActivity.this.onBackPressed();
            }
        });
        ListView settingList = (ListView) findViewById(android.R.id.list);
        this.mAdapter = new SettingAdapter(this, R.layout.settinglist_item, getSettingData());
        settingList.setAdapter(this.mAdapter);
        settingList.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
                AppLockerPreferenceActivity.this.handleSettingAction(((SettingItem) AppLockerPreferenceActivity.this
                        .mAdapter.getItem(position)).getAction());
            }
        });
        this.mAdapter.setCheckBoxChangeLister(new CheboxChangeListener() {
            public void changed(boolean isEnable, int positon) {
                AppLockerPreferenceActivity.this.handleSettingAction(((SettingItem) AppLockerPreferenceActivity.this
                        .mAdapter.getItem(positon)).getAction());
            }
        });
        this.appLockerPreference = new AppLockerPreference(getApplicationContext());
    }

    private void stopService() {
        stopService(new Intent(this, DetectorService.class));
    }

    private void startService() {

    }

    public boolean verifyPassword() {
        if (this.password == null) {
            return false;
        }
        return this.password.equals(AppLockerPreference.getInstance(this).getPassword());
    }

    @Deprecated
    protected Dialog onCreateDialog(int id) {
        if (id == this.MY_DIALOG) {
            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            View mylayout = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout
                    .dialog_custome, null);
            final AlertDialog myDialog = new Builder(this).create();
            myDialog.setTitle("Edit Password");
            myDialog.setView(mylayout, 0, 0, 0, 0);
            myDialog.show();
            final EditText EditText_OldPwd = (EditText) mylayout.findViewById(R.id.EditText_OldPwd);
            final EditText EditText_Pwd1 = (EditText) mylayout.findViewById(R.id.EditText_Pwd1);
            Button ok = (Button) mylayout.findViewById(R.id.button_ok);
            final Button button = ok;
            EditText_OldPwd.addTextChangedListener(new TextWatcher() {
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    AppLockerPreferenceActivity.this.password = EditText_OldPwd.getText().toString();
                    if (AppLockerPreferenceActivity.this.verifyPassword()) {
                        EditText_Pwd1.setEnabled(true);
                        button.setEnabled(true);
                    }
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void afterTextChanged(Editable s) {
                }
            });
            ok.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    Password1 = EditText_Pwd1.getText().toString();
                    if (Password1.equals("")) {
                        Toast.makeText(getApplicationContext(), "insert password", Toast.LENGTH_SHORT).show();
                    } else {
                        System.out.println(Password1);
                        appLockerPreference.savePassword(Password1);
                        myDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Password Change", Toast.LENGTH_SHORT).show();
                    }
                    myDialog.cancel();
                    EditText_OldPwd.setText("");
                    EditText_Pwd1.setText("");
                }
            });
            mylayout.findViewById(R.id.button_Cancel).setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    EditText_OldPwd.setText("");
                    EditText_Pwd1.setText("");
                    myDialog.cancel();
                }
            });
            return myDialog;
        }
        if (id == this.SET_PWD_DIALOG) {
            EditText editText = new EditText(this);
            editText.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            editText.setHint("Enter minimum 4 digit password");
            editText.setInputType(2);
            Builder alertDialogBuilder = new Builder(this);
            alertDialogBuilder.setTitle("Create Password");
            alertDialogBuilder.setView(editText);
            final EditText view = editText;
            alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    String password = view.getText().toString();
                    if (!password.equals("")) {
                        AppLockerPreferenceActivity.this.appLockerPreference.savePassword(password);
                        AppLockerPreferenceActivity.this.appLockerPreference.savePasswordType(true);
                    }
                }
            });
            alertDialogBuilder.setCancelable(false).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            final AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            alertDialog.getButton(-1).setEnabled(false);
            editText.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {
                    if (view.getText().toString().length() >= 4) {
                        alertDialog.getButton(-1).setEnabled(true);
                    } else {
                        alertDialog.getButton(-1).setEnabled(false);
                    }
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });
        }
        if (id == this.SET_RELOCK_TIME) {
            new Builder(this).setSingleChoiceItems(getResources().getStringArray(R.array.relock_timeout_entries),
                    this.nowRelockTimeIndex, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    AppLockerPreferenceActivity.this.nowRelockTimeIndex = which;
                    AppLockerPreference.getInstance(AppLockerPreferenceActivity.this).saveRelockTimeOut
                            (AppLockerPreferenceActivity.this.getResources().getStringArray(R.array
                                    .relock_timeout_values)[which]);
                    dialog.dismiss();
                    AppLockerPreferenceActivity.this.mAdapter.notifyDataSetChanged();
                }
            }).create().show();
        }
        if (id == this.PASSWORD_TYPE) {
            new Builder(this).setSingleChoiceItems(new String[]{"Password", "Pattern"}, this.appLockerPreference
                    .isPasswordType() ? 0 : 1, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    boolean z;
                    AppLockerPreferenceActivity appLockerPreferenceActivity = AppLockerPreferenceActivity.this;
                    if (which == 0) {
                        z = true;
                    } else {
                        z = false;
                    }
                    appLockerPreferenceActivity.ChangePsw(z);
                    dialog.dismiss();
                }
            }).create().show();
        }
        return null;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                System.out.println("result==" + resultCode);
                if (resultCode == -1) {
                    this.pattern = data.getCharArrayExtra(LockPatternActivity.EXTRA_PATTERN);
                    Toast.makeText(getApplicationContext(), "Pattern Successfully Created", Toast.LENGTH_SHORT).show();
                    this.appLockerPreference.savePasswordType(false);
                    showEmailActivity();
                    return;
                }
                return;
            default:
                return;
        }
    }

    private ArrayList<SettingItem> getSettingData() {
        ArrayList<SettingItem> tmp = new ArrayList<>();
        tmp.add(new SettingItem(getResources().getString(R.string.setting_item_service_enable), 0, 0));
        tmp.add(new SettingItem(getResources().getString(R.string.setting_item_change_password), 1, 1));
        tmp.add(new SettingItem(getResources().getString(R.string.setting_item_service_relock_time), 0, 2));
        tmp.add(new SettingItem(getResources().getString(R.string.setting_item_service_current_relocktime), 1, 3));
        tmp.add(new SettingItem(getResources().getString(R.string.setting_item_service_lock_type), 2, 5));
        tmp.add(new SettingItem(getResources().getString(R.string.setting_item_service_password_retrive), 2, 6));
        tmp.add(new SettingItem(getResources().getString(R.string.setting_item_service_rateus), 2, 4));
        tmp.add(new SettingItem(getResources().getString(R.string.setting_item_service_upgrade), 2, 7));
        return tmp;
    }

    private void handleSettingAction(int action) {
        switch (action) {
            case 0:
                boolean isServiceEnable = AppLockerPreference.getInstance(this).isServiceEnabled();
                if (isServiceEnable) {
                    stopService();
                } else {
                    startService();
                }
                AppLockerPreference.getInstance(this).saveServiceEnabled(!isServiceEnable);
                this.mAdapter.notifyDataSetChanged();
                return;
            case 1:
                ChangePsw(this.appLockerPreference.isPasswordType());
                return;
            case 2:
                boolean isRelockEnable = AppLockerPreference.getInstance(this).isRelockPoliceEnabled();
                if (isRelockEnable) {
                    AppLockerPreference.getInstance(this).saveLastRelockTimeOut(AppLockerPreference.getInstance(this)
                            .getRelockTimeout() + "");
                    AppLockerPreference.getInstance(this).saveRelockTimeOut("-1");
                    ActivityStartingHandler.clearAllowList();
                    if (VERSION.SDK_INT > 22) {
                        MyAccessibilityService.clearAllowList();
                    }
                } else {
                    AppLockerPreference.getInstance(this).saveRelockTimeOut(AppLockerPreference.getInstance(this)
                            .getLastRelockTimeout() + "");
                }
                AppLockerPreference.getInstance(this).saveRelockPoliceEnabled(!isRelockEnable);
                this.mAdapter.notifyDataSetChanged();
                return;
            case 3:
                if (AppLockerPreference.getInstance(this).isRelockPoliceEnabled()) {
                    showDialog(this.SET_RELOCK_TIME);
                    int nowTimeOut = AppLockerPreference.getInstance(this).getRelockTimeout();
                    String[] arrayTimeOut = getResources().getStringArray(R.array.relock_timeout_values);
                    for (int i = 0; i < arrayTimeOut.length; i++) {
                        if (Integer.parseInt(arrayTimeOut[i]) == nowTimeOut) {
                            this.nowRelockTimeIndex = i;
                        }
                    }
                }
                this.mAdapter.notifyDataSetChanged();
                return;
            case 4:
                startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" +
                        getPackageName())));
                return;
            case 5:
                showDialog(this.PASSWORD_TYPE);
                return;
            case 6:
                startActivity(new Intent(this, SetMailActivity.class));
                return;
            case 7:
                new CheckLatestVersionTask(this, true).execute(new Void[0]);
                return;
            default:
                return;
        }
    }

    private void GoAutoStartManagerSetting() {
        if ("Xiaomi".equals(Build.BRAND)) {
            Intent taskIntent = new Intent();
            taskIntent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart" +
                    ".AutoStartManagementActivity"));
            taskIntent.addFlags(268435456);
            startActivity(taskIntent);
        }
    }

    private void ChangePsw(boolean pinType) {
        if (pinType) {
            startActivity(new Intent(this, SetPasswordActivity.class));
            return;
        }
        Security.setAutoSavePattern(this, true);
        startActivityForResult(new Intent(LockPatternActivity.ACTION_CREATE_PATTERN, null, this, LockPatternActivity
                .class), 1);
    }

    private void showEmailActivity() {
        if (TextUtils.isEmpty(AppLockerPreference.getInstance(this).getUserEmail())) {
            startActivity(new Intent(this, SetMailActivity.class));
        }
    }

    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
