package com.ee5.mobile.SupportClasses;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.CollationElementIterator;
import java.util.ArrayList;

public class APIconnection extends AppCompatActivity {

    private RequestQueue requestQueue;
    private String requestURL = "https://ee5-huzza.herokuapp.com/";

    private String responseString = "";
    private JSONObject JSONResponse;

    private Context activityContext;



    public APIconnection (Context context, String node, ArrayList<String> parameters, ArrayList<String> values){

        requestURL = requestURL + node + "?";
        activityContext = context;

        for (int i = 0; i < parameters.size(); i++) {

            if (i == parameters.size() - 1) {
                requestURL = requestURL + parameters.get(i) + "=" + values.get(i);
            } else {
                requestURL = requestURL + parameters.get(i) + "=" + values.get(i) + "&";
            }
        }
        Log.i("requestURL:", requestURL);
    }

    /**
     * Retrieve information from DB with Volley JSONRequest
     */
    public String GETRequest()
    {
        requestQueue = Volley.newRequestQueue( activityContext );


        JsonArrayRequest submitRequest = new JsonArrayRequest(Request.Method.GET, requestURL, null,

                new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray response)
                    {
                        JSONResponse = new JSONObject();
                        try {
                            for( int i = 0; i < response.length(); i++ ) {
                                JSONResponse = response.getJSONObject(i);
                                responseString += JSONResponse.getString("email") + " : " + JSONResponse.getString("salt") + "\n";
                            }
                        }
                        catch( JSONException e )
                        {
                            Log.e( "Database", e.getMessage(), e );
                        }
                    }
                },

                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        responseString = error.getLocalizedMessage();
                    }
                }
        );

        requestQueue.add(submitRequest);
        return responseString;
    }
}


