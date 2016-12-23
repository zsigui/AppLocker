package com.noahapp.accesslib.activity;

import android.app.Activity;
import android.os.Bundle;

import com.noahmob.AppLocker.Utils.DP;

//import com.facebook.ads.AdError;
//import com.facebook.ads.NativeAdScrollView;
//import com.facebook.ads.NativeAdView.Type;
//import com.facebook.ads.NativeAdsManager;
//import com.facebook.ads.NativeAdsManager.Listener;

//public class InterstitalActivity extends Activity implements Listener {
public class InterstitalActivity extends Activity {
//    private NativeAdsManager manager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DP.D("InterstitalActivity.show ad here originally");
//        showNativeAd();
    }

//    private void showNativeAd() {
//        DP.E("hs_fb_interstital_init");
//        TrackManager.getInstance(this).trackAction("hs_fb_ads_init");
//        this.manager = new NativeAdsManager(this, "1748050855445951_1833107130273656", 3);
//        this.manager.setListener(this);
//        this.manager.loadAds();
//    }
//
//    public void onAdsLoaded() {
//        Toast.makeText(getApplicationContext(), "", 0).show();
//        setContentView(R.layout.dialog_interstital);
//        PreferenceFile.getInstance(getApplication()).saveLastInterstitialTimeStamp(System.currentTimeMillis() + "");
//        ((LinearLayout) findViewById(R.id.hscrollContainer)).addView(new NativeAdScrollView((Context) this, this.manager, Type.HEIGHT_400));
//        DP.E("hs_fb_interstital_loaded");
//        TrackManager.getInstance(this).trackAction("hs_interstital_ads_loaded");
//    }
//
//    public void onAdError(AdError adError) {
//        finish();
//    }
}
