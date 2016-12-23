package com.noahmob.AppLocker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class AppLockerActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, ApplicationListActivity.class));
        finish();
    }

    private void startService() {
    }

    public void onBackPressed() {
        super.onBackPressed();
    }
}
