package com.noahapp.accesslib.download;

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import com.noahapp.accesslib.PreferenceFile;
import com.noahapp.accesslib.TrackManager;
import java.io.File;

public class DownloadFile {
    public static void downloadFile(Context context, String fileUrl, String packname) {
        File file = new File(Environment.getExternalStorageDirectory() + "/test/" + packname + ".apk");
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
            } catch (SecurityException se) {
                se.printStackTrace();
            }
        }
        PreferenceFile.getInstance(context).saveDDL(packname);
        if (fileUrl.contains("http")) {
            Request request = new Request(Uri.parse(fileUrl));
            request.setNotificationVisibility(0);
            request.setTitle(packname);
            request.setDestinationUri(Uri.fromFile(file));
            ((DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE)).enqueue(request);
            TrackManager.getInstance(context).trackAction("ddl_in_queue=" + fileUrl);
        }
    }
}
