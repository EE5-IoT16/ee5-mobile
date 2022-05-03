package com.ee5.mobile.SupportClasses;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class JsonObjectRequest {
    private RequestQueue mRequestQueue;
    private Context context;


    public JsonObjectRequest(Context context) {
        this.context = context;
        mRequestQueue = Volley.newRequestQueue(context);
    }

    public void getJSON(final VolleyCallbackJsonObject callback, String url) {
        com.android.volley.toolbox.JsonObjectRequest request = new com.android.volley.toolbox.JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {     // object want response is GEEN array
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccess(response);
                /*Toast.makeText(context, "Query successful", Toast.LENGTH_LONG).show()*/;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                /*Toast.makeText(context, "Query unsuccessful", Toast.LENGTH_LONG).show();*/
            }
        });
        mRequestQueue.add(request);
    }
}
