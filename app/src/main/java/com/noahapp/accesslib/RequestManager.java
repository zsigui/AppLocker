package com.noahapp.accesslib;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.noahapp.accesslib.entity.KeyTable;
import com.noahmob.AppLocker.config.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

//import com.facebook.share.internal.ShareConstants;

public class RequestManager {
    private static RequestManager mRequestManager;
    private Context mContext;
    private RequestQueue mQueue;

    public static RequestManager getInstance(Context ctx) {
        if (mRequestManager == null) {
            mRequestManager = new RequestManager(ctx);
        }
        return mRequestManager;
    }

    private RequestManager(Context ctx) {
        this.mQueue = Volley.newRequestQueue(ctx);
        this.mContext = ctx;
    }

    public void startRefreshOffer() {
        this.mQueue.add(new JsonObjectRequest("http://api.hehevideo.com/offer.json", null, new Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                RequestManager.this.initOffer(response);
            }
        }, new ErrorListener() {
            public void onErrorResponse(VolleyError error) {
            }
        }));
    }

    private void initOffer(JSONObject response) {
        ArrayList<KeyTable> mKeyTables = new ArrayList();
        try {
            String recommandLink = response.getString("recommend");
            if (!TextUtils.isEmpty(recommandLink)) {
                mKeyTables.add(new KeyTable("recommend", recommandLink));
            }
            JSONArray jsonArray = response.getJSONArray(Constant.DATA);
            if (jsonArray != null) {
                int length = jsonArray.length();
                for (int i = 0; i < length; i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Iterator it = jsonObject.keys();
                    while (it.hasNext()) {
                        String key = it.next().toString();
                        mKeyTables.add(new KeyTable(key, jsonObject.getString(key)));
                    }
                }
                saveLocal(mKeyTables);
            }
            DP.E("init mKeyWords:" + mKeyTables.size());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveLocal(ArrayList<KeyTable> lists) {
        Set<String> keyTableSets = new HashSet();
        int count = lists.size();
        for (int i = 0; i < count; i++) {
            keyTableSets.add(new Gson().toJson(lists.get(i)));
        }
        PreferenceFile.getInstance(this.mContext).saveKeyTable(keyTableSets);
    }
}
