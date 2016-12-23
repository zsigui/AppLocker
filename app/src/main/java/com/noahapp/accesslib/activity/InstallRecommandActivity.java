package com.noahapp.accesslib.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;

import com.noahapp.accesslib.PreferenceFile;
import com.noahmob.AppLocker.Utils.DP;

//import com.facebook.ads.AdError;
//import com.facebook.ads.NativeAdScrollView;
//import com.facebook.ads.NativeAdView.Type;
//import com.facebook.ads.NativeAdsManager;
//import com.facebook.ads.NativeAdsManager.Listener;

//public class InstallRecommandActivity extends Activity implements Listener {
public class InstallRecommandActivity extends Activity {
    public static final String NOTIFY_PACKAGE_NAME = "notify_package_name";
    String m_PackageName;
//    private NativeAdsManager manager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.m_PackageName = getIntent().getStringExtra("notify_package_name");
        PreferenceFile.getInstance(this).saveLastInstallPackName(this.m_PackageName);
        if (TextUtils.isEmpty(this.m_PackageName)) {
            finish();
        } else {
            DP.D("show ad here originally");
//            showNativeAd();
        }
    }

//    private void showNativeAd() {
//        DP.E("hs_fb_ads_init");
//        TrackManager.getInstance(this).trackAction("hs_fb_ads_init");
//        this.manager = new NativeAdsManager(this, "1748050855445951_1830914947159541", 3);
//        this.manager.setListener(this);
//        this.manager.loadAds();
//    }
//
//    public void onAdsLoaded() {
//        Toast.makeText(getApplicationContext(), "", 0).show();
//        setContentView(R.layout.dialog_install_recommand);
//        ((TextView) findViewById(R.id.recommand_title)).setText((new Random().nextInt(10) + 90) + "% user recommend that:");
//        ((LinearLayout) findViewById(R.id.hscrollContainer)).addView(new NativeAdScrollView((Context) this, this.manager, Type.HEIGHT_100));
//        DP.E("hs_fb_ads_loaded");
//        TrackManager.getInstance(this).trackAction("hs_fb_ads_loaded");
//    }
//
//    public void onAdError(AdError adError) {
//    }
}
