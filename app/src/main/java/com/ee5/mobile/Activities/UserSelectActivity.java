package com.ee5.mobile.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ee5.mobile.Interfaces.ServerCallback;
import com.ee5.mobile.R;
import com.ee5.mobile.SupportClasses.APIconnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class UserSelectActivity extends AppCompatActivity {

    private Button userButton1;
    private Button userButton2;

    private int profileId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_select);

        APIconnection.getInstance(this);

        APIconnection.getInstance().GETRequest("profileUserLink", new ArrayList<String>(Arrays.asList(Integer.toString(profileId))), new ServerCallback() {
                    @Override
                    public void onSuccess() {

                        JSONArray responseArray = APIconnection.getInstance().getAPIResponse();

                        try {
                            for( int i = 0; i < responseArray.length(); i++ ) {
                                JSONObject curObject = responseArray.getJSONObject(i);
                                int userId = curObject.getInt("userId");

                                APIconnection.getInstance().GETRequest("user", new ArrayList<String>(Arrays.asList(Integer.toString(userId))), new ServerCallback() {
                                    @Override
                                    public void onSuccess() {

                                        JSONArray responseArray2 = APIconnection.getInstance().getAPIResponse();
                                        try {
                                            JSONObject Object = responseArray2.getJSONObject(0);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                userButton1 = (Button) findViewById(R.id.btnUser1);
        userButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        userButton2 = (Button) findViewById(R.id.btnUser2);
        userButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}