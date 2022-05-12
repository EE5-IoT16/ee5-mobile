package com.ee5.mobile.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ee5.mobile.R;
import com.ee5.mobile.SupportClasses.APIconnection;
import com.ee5.mobile.SupportClasses.JsonArrayRequest;
import com.ee5.mobile.SupportClasses.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    private String name;
    private String userId;
    public static int heightUser;

    TextView profileName;
    TextView profileAge;
    TextView profileGender;
    TextView profileRmr;
    TextView profileBmi;
    TextView profileHeight;
    TextView profileWeight;

    Button logoutBtn;
    Button fallButton;
    Button activityBtn;
    private APIconnection apiConnection;
    ArrayList<String> parameters;

    private JsonArrayRequest jsonArrayRequest;
    private String prefixURL = "https://ee5-huzza.herokuapp.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_new);
        /*jsonArrayRequest = new JsonArrayRequest(this);

        profileName = findViewById(R.id.profile_name);
        profileAge = findViewById(R.id.age_data);
        profileGender = findViewById(R.id.gender_data);
        profileRmr = findViewById(R.id.rmr_data);
        profileBmi = findViewById(R.id.bmi_data);
        profileHeight = findViewById(R.id.height_data);
        profileWeight = findViewById(R.id.profile_weightGoal);

        getUserId();
        getUser();
        getPhysicalData();

        fallButton = (Button) findViewById(R.id.fallData_btn);
        fallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, FallActivity.class);
                startActivity(intent);
            }
        });

        activityBtn = findViewById(R.id.activities_btn);
        activityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, ActivityModeActivity.class);
                startActivity(intent);
            }
        });*/


    }

    public void getUserId() {
        userId = Integer.toString(1);
    }

    public void getUser() {
        jsonArrayRequest.getJSONArray(response -> {
            try {
                Log.i("onResponse:", response.toString());

                JSONObject user = response.getJSONObject(0);
                String name = user.getString("name");
                String surName = user.getString("surname");
                profileName.setText(name + " " + surName);
                //get other data (add to database)

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, prefixURL + "user/" + userId);
    }

    public void getPhysicalData() {
        jsonArrayRequest.getJSONArray(response -> {
            try {
                JSONObject user = response.getJSONObject(0);
                String weight = user.getString("weight");
                String height = user.getString("height");
                heightUser = Integer.valueOf(height);
                String age = user.getString("age");
                String gender = user.getString("gender");
                String bmi = user.getString("bmi");
                String rmr = user.getString("rmr");
                profileWeight.setText(weight);
                profileAge.setText(age);
                profileHeight.setText(height);
                profileBmi.setText(bmi);
                profileRmr.setText(rmr);
                profileGender.setText(gender);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, prefixURL + "physicalData/" + userId);
    }
}
