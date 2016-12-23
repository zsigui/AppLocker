package com.noahmob.AppLocker.apprater;

import android.content.Context;
import android.net.Uri;

public class GoogleMarket implements Market {
    private static String marketLink = "market://details?id=";

    public Uri getMarketURI(Context context) {
        return Uri.parse(marketLink + context.getPackageName().toString());
    }
}
