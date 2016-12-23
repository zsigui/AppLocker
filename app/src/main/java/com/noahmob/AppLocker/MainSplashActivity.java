package com.noahmob.AppLocker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.amigo.applocker.R;

public class MainSplashActivity extends Activity {
    public static final int MAIN_SPLASH_TIME = 4000;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Intent intent = new Intent(MainSplashActivity.this, ApplicationListActivity.class);
            intent.setAction("from_splash");
            MainSplashActivity.this.startActivity(intent);
            MainSplashActivity.this.finish();
            MainSplashActivity.this.overridePendingTransition(0, 0);
        }
    };

    public static void launchSplashActivity(Context context) {
        context.startActivity(new Intent(context, MainSplashActivity.class));
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_splash);
        getWindow().setBackgroundDrawable(null);
        this.mHandler.sendEmptyMessageDelayed(0, 4000);
    }

    public void onBackPressed() {
        this.mHandler.removeMessages(0);
        super.onBackPressed();
    }
}
