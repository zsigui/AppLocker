package com.noahmob.AppLocker.upgrade;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat.Builder;
import android.text.TextUtils;
import android.widget.Toast;

import com.amigo.applocker.R;
import com.noahmob.AppLocker.upgrade.NetworkConst.HttpParam;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

public class NetworkService extends IntentService {
    private static final int DOWNLOAD_ERROR_NET = 6;
    private static final int DOWNLOAD_FINISHED = 2;
    private static final int DOWNLOAD_IDLE = 0;
    private static final int DOWNLOAD_NOTIFY_FAILED = 3;
    private static final int DOWNLOAD_NOTIFY_PROGRESS = 5;
    private static final int DOWNLOAD_NOTIFY_START = 4;
    private static final int DOWNLOAD_NOTIFY_SUCESS = 2;
    private static final int DOWNLOAD_SPACE_ERROR = 7;
    private static final int DOWNLOAD_START = 1;
    private static final int FEEDBACK_FAILED = 1;
    private static final int FEEDBACK_SUCESS = 0;
    private static final int NOTIFY_ID_DOWNLOAD = 1;
    private static volatile int mDownloadState = 0;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 2:
                    NetworkService.mDownloadState = 2;
                    NetworkService.this.notifyDownloadCompleted((String) msg.obj);
                    return;
                case 3:
                    NetworkService.mDownloadState = 2;
                    NetworkService.this.notifyNmDownloadFailed(msg.arg1, (String) msg.obj);
                    return;
                case 4:
                    NetworkService.this.notifyNmStartDownload((String) msg.obj);
                    return;
                case 5:
                    NetworkService.this.updateDownloadNmProgress(msg.arg1, (String) msg.obj);
                    return;
                case 6:
                    NetworkService.mDownloadState = 2;
                    NetworkService.this.notifyNmDownloadFailedForNeterror(msg.arg1, (String) msg.obj);
                    return;
                case 7:
                    NetworkService.mDownloadState = 2;
                    Toast.makeText(NetworkService.this, R.string.upgrade_download_space_error, Toast.LENGTH_SHORT)
                            .show();
                    return;
                default:
                    return;
            }
        }
    };
    private NotificationManager mNm;
    private boolean showToast = true;

    public NetworkService() {
        super("networkService");
    }

    public void onCreate() {
        super.onCreate();
        this.mNm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    public void onStart(Intent intent, int startId) {
        if (TextUtils.isEmpty(intent.getAction()) || !intent.getAction().equals(NetworkConst.ACTION_DOWNLOAD)) {
            return;
        }
        if (mDownloadState == 0 || mDownloadState == 2) {
            mDownloadState = 1;
            super.onStart(intent, startId);
            return;
        }
        Toast.makeText(this, R.string.str_download_state_ing, Toast.LENGTH_SHORT).show();
    }

    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        if (action.equals(NetworkConst.ACTION_FEEDBACK)) {
            commitContent2Server(getParamMapByUserData(intent.getStringExtra(NetworkConst.FEEDBACK_CONTENT), intent
                    .getStringExtra(NetworkConst.FEEDBACK_EMAIL)));
        } else if (action.equals(NetworkConst.ACTION_DOWNLOAD)) {
            startDownloadApk(intent);
        }
    }

    private Map<String, String> getParamMapByUserData(String content, String email) {
        Map<String, String> paramMap = new LinkedHashMap();
        String deviceID = OptimUtils.getDeviceId(this);
        String httpParam = HttpParam.serial.toString();
        if (TextUtils.isEmpty(deviceID)) {
            deviceID = "null";
        }
        paramMap.put(httpParam, deviceID);
        httpParam = HttpParam.email.toString();
        if (TextUtils.isEmpty(email)) {
            email = "null";
        }
        paramMap.put(httpParam, email);
        paramMap.put(HttpParam.versionId.toString(), OptimUtils.getVersion(this));
        paramMap.put(HttpParam.phoneModelName.toString(), Build.MODEL);
        paramMap.put(HttpParam.resolutionName.toString(), OptimUtils.getScreenResolution(this));
        paramMap.put(HttpParam.androidVersion.toString(), VERSION.RELEASE);
        paramMap.put(HttpParam.type.toString(), String.valueOf(2));
        paramMap.put(HttpParam.date.toString(), OptimUtils.timeFormatToDay(System.currentTimeMillis()));
        paramMap.put(HttpParam.content.toString(), content);
        return paramMap;
    }

    public void commitContent2Server(Map<String, String> map) {
    }

    private void startDownloadApk(Intent intent) {
        String url = intent.getStringExtra(NetworkConst.EXTRA_DOWNLOAD_URL);
        String versionName = intent.getStringExtra(NetworkConst.EXTRA_DOWNLOAD_VERSIONNAME);
        this.showToast = intent.getBooleanExtra(NetworkConst.EXTRA_DOWNLOAD_SHOW_TOAST, true);
        if (Environment.getExternalStorageState().equals("mounted")) {
            String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + OptimConst.DOWNLOAD_APK_DIR;
            File targetDir = new File(dir);
            if (!targetDir.exists()) {
                targetDir.mkdir();
            }
            dir = dir + NetworkConst.DOWNLOAD_APK_NAME + ".apk";
            File apkFile = new File(dir);
            if (apkFile.exists()) {
                apkFile.delete();
            }
            downloadApk(url, dir);
        }
    }

    private void downloadApk(String downloadUrl, String downloadApkPath) {
        // 先屏蔽，后面看需要整改
//        IOException e;
//        MalformedURLException e2;
//        Throwable th;
//        FileOutputStream fos = null;
//        InputStream is = null;
//        int rate = 0;
//        String fileName = new File(downloadApkPath).getName();
//        try {
//            HttpURLConnection conn = (HttpURLConnection) new URL(downloadUrl).openConnection();
//            conn.connect();
//            if (conn.getResponseCode() != 200) {
//                this.mHandler.obtainMessage(6, 0, 0, fileName).sendToTarget();
//                if (fos != null) {
//                    try {
//                        fos.close();
//                    } catch (IOException e3) {
//                        e3.printStackTrace();
//                        return;
//                    }
//                }
//                if (is != null) {
//                    is.close();
//                    return;
//                }
//                return;
//            }
//            int totalSize = conn.getContentLength();
//            if (OptimUtils.getSdAvailSpace() <= ((long) (totalSize + 1024))) {
//                this.mHandler.obtainMessage(7).sendToTarget();
//                if (fos != null) {
//                    try {
//                        fos.close();
//                    } catch (IOException e32) {
//                        e32.printStackTrace();
//                        return;
//                    }
//                }
//                if (is != null) {
//                    is.close();
//                    return;
//                }
//                return;
//            }
//            this.mHandler.obtainMessage(4, fileName).sendToTarget();
//            int currentSize = 0;
//            is = conn.getInputStream();
//            FileOutputStream fos2 = new FileOutputStream(downloadApkPath);
//            try {
//                byte[] buf = new byte[10240];
//                long currentTime = System.currentTimeMillis();
//                while (true) {
//                    int numread = is.read(buf);
//                    if (numread < 0) {
//                        break;
//                    }
//                    fos2.write(buf, 0, numread);
//                    currentSize += numread;
//                    if (System.currentTimeMillis() - currentTime > 1000) {
//                        currentTime = System.currentTimeMillis();
//                        rate = (currentSize * 100) / totalSize;
//                        this.mHandler.obtainMessage(5, rate, 0, fileName).sendToTarget();
//                    }
//                }
//                if (currentSize == totalSize) {
//                    this.mHandler.obtainMessage(2, downloadApkPath).sendToTarget();
//                }
//                if (fos2 != null) {
//                    try {
//                        fos2.close();
//                    } catch (IOException e322) {
//                        e322.printStackTrace();
//                        fos = fos2;
//                        return;
//                    }
//                }
//                if (is != null) {
//                    is.close();
//                }
//                fos = fos2;
//            } catch (MalformedURLException e4) {
//                e2 = e4;
//                fos = fos2;
//            } catch (IOException e5) {
//                e322 = e5;
//                fos = fos2;
//            } catch (Throwable th2) {
//                th = th2;
//                fos = fos2;
//            }
//        } catch (MalformedURLException e6) {
//            e2 = e6;
//            try {
//                this.mHandler.obtainMessage(6, rate, 0, fileName).sendToTarget();
//                DP.E("MalformedURLException:" + e2.toString());
//                if (fos != null) {
//                    try {
//                        fos.close();
//                    } catch (IOException e3222) {
//                        e3222.printStackTrace();
//                        return;
//                    }
//                }
//                if (is != null) {
//                    is.close();
//                }
//            } catch (Throwable th3) {
//                th = th3;
//                if (fos != null) {
//                    try {
//                        fos.close();
//                    } catch (IOException e32222) {
//                        e32222.printStackTrace();
//                        throw th;
//                    }
//                }
//                if (is != null) {
//                    is.close();
//                }
//                throw th;
//            }
//        } catch (IOException e7) {
//            e32222 = e7;
//            this.mHandler.obtainMessage(6, rate, 0, fileName).sendToTarget();
//            DP.E("IOException:" + e32222.toString());
//            if (fos != null) {
//                try {
//                    fos.close();
//                } catch (IOException e322222) {
//                    e322222.printStackTrace();
//                    return;
//                }
//            }
//            if (is != null) {
//                is.close();
//            }
//        }
    }

    private void notifyNmStartDownload(String title) {
        if (this.showToast) {
            Toast.makeText(getApplicationContext(), R.string.str_start_download, Toast.LENGTH_LONG).show();
        }
        this.mNm.cancel(1);
        updateDownloadNmProgress(0, title);
    }

    private void updateDownloadNmProgress(int progress, String title) {
        try {
            long when = System.currentTimeMillis();
            Notification notification = new Builder(this).setContentTitle(title).setTicker(getResources().getString(R
                    .string.str_start_download)).setSmallIcon(R.drawable.icon_notification).setProgress(100,
                    progress, false).setContentInfo(progress + "%").getNotification();
            notification.flags = 2;
            this.mNm.notify(1, notification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void notifyNmDownloadFailed(int progress, String title) {
        try {
            Notification notification = new Builder(this).setProgress(100, progress, false).setSmallIcon(R.drawable
                    .icon_notification).setContentTitle(title).setContentText(getText(R.string.str_download_apk_fail)
            ).getNotification();
            notification.flags = 16;
            this.mNm.notify(1, notification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void notifyNmDownloadFailedForNeterror(int progress, String title) {
        try {
            Notification notification = new Builder(this).setProgress(100, progress, false).setSmallIcon(R.drawable
                    .icon_notification).setContentTitle(title).setContentText(getText(R.string
                    .str_download_apk_fail_neterror)).getNotification();
            long when = System.currentTimeMillis();
            notification.flags = 16;
            this.mNm.notify(1, notification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void notifyDownloadCompleted(String filePath) {
        Intent intent = new Intent();
        intent.addFlags(268435456);
        intent.addFlags(67108864);
        intent.setAction("android.intent.action.VIEW");
        intent.setDataAndType(Uri.fromFile(new File(filePath)), "application/vnd.android.package-archive");
        try {
            Builder builder = new Builder(this).setContentTitle(new File(filePath).getName())
                    .setContentText(getText(R.string.str_download_apk_success))
                    .setContentIntent(PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT))
                    .setSmallIcon(R.drawable.icon_notification);
            Bitmap bp = getLargeIcon(filePath);
            if (bp != null) {
                builder.setLargeIcon(bp);
            }
            Notification notification = builder.getNotification();
            notification.flags = 16;
            this.mNm.notify(1, notification);
        } catch (Exception e) {
            e.printStackTrace();
        }
        startActivity(intent);
    }

    private Bitmap getLargeIcon(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        Drawable drawable = null;
        if (file.exists()) {
            drawable = getIconFromAPKFile(file);
        }
        if (drawable != null) {
            return drawableToBitmap(drawable, 50, 50);
        }
        return null;
    }

    private Drawable getIconFromAPKFile(File file) {
        Drawable drawable = null;
        PackageManager pm = getApplicationContext().getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(file.getAbsolutePath(), PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            appInfo.sourceDir = file.getAbsolutePath();
            appInfo.publicSourceDir = file.getAbsolutePath();
            try {
                drawable = appInfo.loadIcon(pm);
            } catch (OutOfMemoryError e) {
            } catch (Exception e2) {
            }
        }
        return drawable;
    }

    private Bitmap drawableToBitmap(Drawable drawable, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
