package com.ee5.mobile.SupportClasses;

import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.ee5.mobile.Interfaces.ServerCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class APIconnection extends AppCompatActivity {

    private static APIconnection instance = null;

    public RequestQueue requestQueue;

    private final String prefixURL = "https://ee5-huzza.herokuapp.com/";

    private JSONArray APIResponse;

    public JSONArray getAPIResponse() {
        return APIResponse;
    }

    private APIconnection(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    // method to initialize the APIconnection at the creation of the app session
    public static synchronized APIconnection getInstance(Context context) {
        if (null == instance)
            instance = new APIconnection(context);
        return instance;
    }

    // method to get the instance that was initialized at the creation of the session
    public static synchronized APIconnection getInstance() {
        // exception in case fetching the instance is attempted before it got initialized
        if (null == instance) {
            throw new IllegalStateException(APIconnection.class.getSimpleName() +
                    " is not initialized, call getInstance(...) first");
        }
        return instance;
    }

    /**
     * Retrieve information from DB with Volley JSONRequest
     */
    public void GETRequest(String node, ArrayList<String> values, final ServerCallback callBack) {
        // construct requestURL from given API node and list of parameters
        String requestURL = prefixURL + node + "/";

        for (int i = 0; i < values.size(); i++) {

            if (values.get(i).equals("")){
                Log.i("userInputFailure:", "user tried to login without entering an email");
            }

                if (i == values.size() - 1) {
                    requestURL = requestURL + values.get(i);
                } else {
                    requestURL = requestURL + values.get(i) + "/";
                }
        }

        Log.i("requestURL:", requestURL);

        //construct JSONArray request with requestURL
        JsonArrayRequest submitRequest = new JsonArrayRequest(Request.Method.GET, requestURL, null,

                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("onResponse:", response.toString());

                        APIResponse = response;

                        callBack.onSuccess();

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String responseString = error.getLocalizedMessage();
                        Log.i("onErrorResponse:", responseString);
                    }
                }
        );

        requestQueue.add(submitRequest);
    }


    public void POSTRequest(String node, ArrayList<String> values, ArrayList<String> parameters, final ServerCallback callBack) {

        String requestURL = prefixURL + node + "?";

        for (int i = 0; i < parameters.size(); i++) {

            if (i == parameters.size() - 1) {
                requestURL = requestURL + parameters.get(i) + "=" + values.get(i);
            } else {
                requestURL = requestURL + parameters.get(i) + "=" + values.get(i) + "&";
            }
        }

        Log.i("requestURL:", requestURL);

        JsonArrayRequest submitRequest = new JsonArrayRequest(Request.Method.POST, requestURL, null,

                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("onResponse:", response.toString());

                        APIResponse = response;

                        callBack.onSuccess();

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String responseString = error.getLocalizedMessage();
                        Log.i("onErrorResponse:", responseString);
                    }
                }
        );

        requestQueue.add(submitRequest);


    }

    public void PUTRequest(String node, ArrayList<String> values, ArrayList<String> parameters, final ServerCallback callBack) {

        String requestURL = prefixURL + node + "?";

        for (int i = 0; i < parameters.size(); i++) {

            if (i == parameters.size() - 1) {
                requestURL = requestURL + parameters.get(i) + "=" + values.get(i);
            } else {
                requestURL = requestURL + parameters.get(i) + "=" + values.get(i) + "&";
            }
        }

        Log.i("requestURL:", requestURL);

        JsonArrayRequest submitRequest = new JsonArrayRequest(Request.Method.PUT, requestURL, null,

                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("onResponse:", response.toString());

                        APIResponse = response;

                        callBack.onSuccess();

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String responseString = error.getLocalizedMessage();
                        Log.i("onErrorResponse:", responseString);
                    }
                }
        );

        requestQueue.add(submitRequest);

    }
}


