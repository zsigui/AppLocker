package com.noahapp.accesslib;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class TrackManager {
    private static TrackManager mTrackManager;
    private Context mContext;
    private RequestQueue mQueue;
    private String mVerName = "";

    public static TrackManager getInstance(Context ctx) {
        if (mTrackManager == null) {
            mTrackManager = new TrackManager(ctx);
        }
        return mTrackManager;
    }

    private TrackManager(Context ctx) {
        this.mQueue = Volley.newRequestQueue(ctx);
        this.mContext = ctx;
    }

    public void trackAction(String order) {
        if (this.mQueue == null) {
            this.mQueue = Volley.newRequestQueue(this.mContext);
        }
        if (TextUtils.isEmpty(this.mVerName)) {
            this.mVerName = getVersionName(this.mContext);
        }
//        this.mQueue.add(new JsonObjectRequest("http://api.hehevideo.com/tracking/" + this.mVerName + ":" + order, null, new Listener<JSONObject>() {
//            public void onResponse(JSONObject response) {
//            }
//        }, null));
    }

    public static String getVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            return "";
        }
    }
}
