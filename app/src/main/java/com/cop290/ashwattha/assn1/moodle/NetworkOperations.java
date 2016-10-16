package com.cop290.ashwattha.assn1.moodle;

/**
 * Created by Harsh on 19-02-2016.
 */

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.LinkedHashMap;




public class NetworkOperations {
        // check connectivity
    public boolean NetworkCheck(Context context){
        ConnectivityManager connectivityManager =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

     // sends request process them using responselistener interface
    public void sendStringrequest(final String URL,  final ResponseListener mListener,Context context){

        StringRequest req = new StringRequest(Request.Method.POST,URL,

                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String s) {
                        mListener.onSuccess(s,URL);
                    }
                } ,new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mListener.onError(volleyError);
            }
        });

        VolleyInstance.getInstance(context).addToRequestQueue(req);
    }
}


