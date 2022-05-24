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

import com.ee5.mobile.Interfaces.ServerCallback;
import com.ee5.mobile.R;
import com.ee5.mobile.SupportClasses.APIconnection;
import com.ee5.mobile.SupportClasses.JsonArrayRequest;
import com.ee5.mobile.SupportClasses.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    private User user;
    private String userId;
    public static int heightUser;
    private static final String TAG = "ProfileActivity";

    TextView profileName;
    TextInputEditText profileAge;
    AutoCompleteTextView profileGender;
    TextInputEditText profileRmr;
    TextInputEditText profileBmi;
    TextInputEditText profileHeight;
    TextInputEditText profileWeight;
    TextView profileStepsRecord;
    TextView profileHpRecord;
    TextView profileStreakRecord;

    Button logoutBtn;
    Button fallButton;
    Button activityBtn;
    ImageButton back_btn;
    private APIconnection apiConnection;
    ArrayList<String> parameters;
    ArrayList<String> apiData = new ArrayList<>();

    private JsonArrayRequest jsonArrayRequest;
    private String prefixURL = "https://ee5-huzza.herokuapp.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        jsonArrayRequest = new JsonArrayRequest(this);

        try{
            user = getIntent().getParcelableExtra("user");
            Log.i("userParcel", user.getProfileEmail() + "1");
            Log.i("userParcel", user.getUserEmail());
            userId = String.valueOf(user.getUserId());
            Log.i("userParcel", userId);
        }
        catch(Exception e){
            Log.e("userParcelException", e.toString());
        }

        //top section
        profileName = findViewById(R.id.name_tv);

        //About you section
        profileAge = findViewById(R.id.age_edit);
        profileGender = findViewById(R.id.gender_edit);
        profileHeight = findViewById(R.id.height_edit);
        profileWeight = findViewById(R.id.weight_edit);
        //non editable from About you section
        profileRmr = findViewById(R.id.rmr_edit);
        profileBmi = findViewById(R.id.bmi_edit);

        profileStepsRecord = findViewById(R.id.steps_record);
        profileHpRecord = findViewById(R.id.hp_record);
        profileStreakRecord = findViewById(R.id.streak_record);

        getUserId();
        getUserName();
        getUserPhysicalData();
        getUserRecords();

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

                updateData();

                Intent intent = new Intent(ProfileActivity.this, FallActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        activityBtn = findViewById(R.id.activities_btn);
        activityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateData();

                Intent intent = new Intent(ProfileActivity.this, ActivityModeActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        logoutBtn = findViewById(R.id.logout_btn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateData();

                final Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });


    }

    public void getUserId() {
        userId = Integer.toString(1);
    }

    public void getUserName() {
        apiData.clear();
        apiData.add(userId);
        APIconnection.getInstance().GETRequest("user", apiData, new ServerCallback() {
            @Override
            public void onSuccess() {
                String responseString = "";
                JSONArray responseArray = APIconnection.getInstance().getAPIResponse();
                try {
                    JSONObject curObject = responseArray.getJSONObject(0);
                    responseString = curObject.getString("name");
                    responseString += " " + curObject.getString("surname");
                    profileName.setText(responseString);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("ERROR", e.toString());
                }
            }
        });
    }

    public void getUserPhysicalData() {
        profileWeight.setText(String.valueOf(user.getWeight()));
        profileAge.setText(String.valueOf(user.getAge()));
        profileHeight.setText(String.valueOf(user.getHeight()));
        profileBmi.setText(String.valueOf(user.getBMI()));
        profileRmr.setText(String.valueOf(user.getRMR()));
        profileGender.setText(String.valueOf(user.getGender()), false);
    }

    public void getUserRecords(){
        apiData.clear();
        apiData.add(userId);
        APIconnection.getInstance().GETRequest("records", apiData, new ServerCallback() {
            @Override
            public void onSuccess() {
                String responseString = "";
                JSONArray responseArray = APIconnection.getInstance().getAPIResponse();
                try {
                    JSONObject curObject = responseArray.getJSONObject(0);
                    Log.d("ERROR", curObject.toString());
                    String stepRecord = curObject.getString("maxStepDay");
                    String hpRecord = curObject.getString("maxHeartPointDay");
                    String streakRecord = curObject.getString("streak");
                    profileStepsRecord.setText(stepRecord + " steps in a single day");
                    profileHpRecord.setText(hpRecord + " heart points in a single day");
                    profileStreakRecord.setText(streakRecord + " days is your longest streak");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void exitIntent() {

        updateData();

        final Intent intent = new Intent(ProfileActivity.this, OverviewActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, R.anim.slide_right_out);

    }

    private void updateData() {

        user.setWeight(Integer.parseInt(profileWeight.getText().toString()));
        user.setAge(Integer.parseInt(profileAge.getText().toString()));
        user.setHeight(Integer.parseInt(profileHeight.getText().toString()));
        user.setGender(profileGender.getText().toString());
    }
}
