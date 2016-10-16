package com.cop290.ashwattha.assn1.moodle;

import com.android.volley.VolleyError;

/**
 * Created by Harsh on 19-02-2016.
 */

// Interface for volley request
public interface ResponseListener {
    void onSuccess(String response,String URL);
    void onError(VolleyError e);

}
