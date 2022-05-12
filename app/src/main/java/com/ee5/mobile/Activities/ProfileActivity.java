package com.ee5.mobile.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ee5.mobile.R;
import com.ee5.mobile.SupportClasses.APIconnection;
import com.ee5.mobile.SupportClasses.JsonArrayRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    private String name;
    private String userId;
    public static int heightUser;
    private static final String TAG = "ProfileActivity";

    TextView profileName;
    TextInputLayout profileAge;
    TextInputLayout profileGender;
    TextInputLayout profileRmr;
    TextInputLayout profileBmi;
    TextInputLayout profileHeight;
    TextInputLayout profileWeight;

    Button logoutBtn;
    Button fallButton;
    Button activityBtn;
    ImageButton back_btn;
    private APIconnection apiConnection;
    ArrayList<String> parameters;

    private JsonArrayRequest jsonArrayRequest;
    private String prefixURL = "https://ee5-huzza.herokuapp.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        jsonArrayRequest = new JsonArrayRequest(this);

        //top section
        profileName = findViewById(R.id.name_tv);

        //About you section
        profileAge = findViewById(R.id.age_layout);
        profileGender = findViewById(R.id.gender_layout);
        profileHeight = findViewById(R.id.height_layout);
        profileWeight = findViewById(R.id.weight_layout);
        //non editable from About you section
        profileRmr = findViewById(R.id.rmr_layout);
        profileBmi = findViewById(R.id.bmi_layout);

        getUserId();
        getUser();
        getPhysicalData();

        back_btn = findViewById(R.id.profile_back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitIntent();
            }
        });

        fallButton = (Button) findViewById(R.id.fall_data_btn);
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
        });

        logoutBtn = findViewById(R.id.logout_btn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Log user out of app
            }
        });


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
                heightUser = Integer.parseInt(height);
                Log.d(TAG, "getPhysicalData: ");
                String age = user.getString("age");
                String gender = user.getString("gender");
                String bmi = user.getString("bmi");
                String rmr = user.getString("rmr");
                profileWeight.setPlaceholderText(weight);
                profileAge.setPlaceholderText(age);
                profileHeight.setPlaceholderText(height);
                profileBmi.setPlaceholderText(bmi);
                profileRmr.setPlaceholderText(rmr);
                profileGender.setPlaceholderText(gender);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, prefixURL + "physicalData/" + userId);
    }

    private void exitIntent(){
        final Intent intent = new Intent(ProfileActivity.this, OverviewActivity.class);
        startActivity(intent);
    }
}
