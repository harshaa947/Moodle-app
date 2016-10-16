package com.cop290.ashwattha.assn1.moodle;

/**
 * Created by Harsh on 19-02-2016.
 */
// A single instance volley class
import android.content.Context;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

import java.net.CookieHandler;
import java.net.CookieManager;


public class VolleyInstance {
    private static VolleyInstance mInstance;
    private RequestQueue mRequestQueue;

    private static Context mCtx;

    private VolleyInstance(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized VolleyInstance getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleyInstance(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {

            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
            CookieManager cookieManage = new CookieManager();
            CookieHandler.setDefault(cookieManage);
        }

        return mRequestQueue;
    }

    public RequestQueue getCustomRequestQueue(int kilobyte){
        if (mRequestQueue == null) {
            Cache cache = new DiskBasedCache(mCtx.getCacheDir(), kilobyte * 1024);
            Network network = new BasicNetwork(new HurlStack());
            mRequestQueue = new RequestQueue(cache, network);
            mRequestQueue.start();
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }



}



