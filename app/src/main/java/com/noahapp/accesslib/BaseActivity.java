package com.noahapp.accesslib;

import android.app.Activity;
import android.os.Bundle;

public class BaseActivity extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
    }

    private boolean chekckUpdateOffer() {
        boolean should = PreferenceFile.getInstance(this).shouldUpdateOffer();
        if (should) {
            RequestManager.getInstance(this).startRefreshOffer();
        }
        return should;
    }
}
