package com.noahmob.AppLocker.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReferrerReceiver extends BroadcastReceiver {
    public static final String INSTALL_REFERRER = "com.android.vending.INSTALL_REFERRER";
    public static final String REFERRER_PUSH_URL = "http://ad.noahmob.com/gp/gp_ref.php";

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (!TextUtils.isEmpty(action) && action.equals(INSTALL_REFERRER) && !TextUtils.isEmpty(intent.getStringExtra("referrer"))) {
        }
    }

    private Map<String, String> getParams(String referrer) {
        Map<String, String> paramsMap = new LinkedHashMap();
        paramsMap.put("referrer", referrer);
        return paramsMap;
    }
}
