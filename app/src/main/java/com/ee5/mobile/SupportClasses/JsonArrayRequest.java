package com.ee5.mobile.SupportClasses;

import static com.loopj.android.http.AsyncHttpClient.log;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

public class JsonArrayRequest {
    private RequestQueue mRequestQueue;
    private Context context;


    public JsonArrayRequest(Context context) {
        this.context = context;
        mRequestQueue = Volley.newRequestQueue(context);
    }

    public void getJSONArray(final VolleyCallbackJsonArray callback, String url) {
        com.android.volley.toolbox.JsonArrayRequest request = new com.android.volley.toolbox.JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                callback.onSuccess(response);
                log.i("JSON onResponse", response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                log.i("JSON onErrorResponse", error.getLocalizedMessage());
            }
        });
        mRequestQueue.add(request);
    }
}