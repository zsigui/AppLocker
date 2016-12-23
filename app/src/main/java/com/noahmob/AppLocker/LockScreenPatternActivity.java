package com.noahmob.AppLocker;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;

import haibison.android.lockpattern.utils.AlpSettings.Security;

public class LockScreenPatternActivity extends Activity {
    public static final String ACTION_APPLICATION_PASSED = "com.noahmob.AppLocker.applicationpassedtest";
    public static final String BlockedActivityName = "locked activity name";
    public static final String BlockedPackageName = "locked package name";
    public static final String EXTRA_PACKAGE_NAME = "com.noahmob.AppLocker.extra.package.name";
    private static final int REQ_ENTER_PATTERN = 2;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Security.setAutoSavePattern(this, true);
        if (getIntent().getStringExtra("locked package name").equals(getPackageName())) {
            PendingIntent piForgotPattern = PendingIntent.getActivity(getApplicationContext(), 0, new Intent
                    (LockPatternActivity.ACTION_CREATE_PATTERN, null, getApplicationContext(), LockPatternActivity
                            .class), 0);
            Intent intent = new Intent(LockPatternActivity.ACTION_COMPARE_PATTERN, null, getApplicationContext(),
                    LockPatternActivity.class);
            intent.putExtra(LockPatternActivity.EXTRA_PENDING_INTENT_FORGOT_PATTERN, piForgotPattern);
            intent.putExtra("locked package name", getIntent().getStringExtra("locked package name"));
            startActivityForResult(intent, 2);
            return;
        }
        Intent intent = new Intent(LockPatternActivity.ACTION_COMPARE_PATTERN, null, getApplicationContext(),
                LockPatternActivity.class);
        intent.putExtra("locked package name", getIntent().getStringExtra("locked package name"));
        startActivityForResult(intent, 2);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("requestCode=" + requestCode + "\nresultCode=" + resultCode);
        switch (requestCode) {
            case 2:
                switch (resultCode) {
                    case -1:
                        test_passed();
                        System.out.println("ok");
                        return;
                    case 0:
                        System.out.println("cancel");
                        test_passed();
                        return;
                    case 2:
                        return;
                    default:
                        return;
                }
            default:
                return;
        }
    }

    private void test_passed() {
        sendBroadcast(new Intent().setAction("com.noahmob.AppLocker.applicationpassedtest").putExtra("com.noahmob" +
                ".AppLocker.extra.package.name", getIntent().getStringExtra("locked package name")));
        finish();
    }

    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN").addCategory("android.intent.category.HOME").addFlags(67108864);
        startActivity(intent);
        finish();
    }
}
