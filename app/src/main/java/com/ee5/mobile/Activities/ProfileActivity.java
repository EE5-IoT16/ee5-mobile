package com.ee5.mobile.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Arrays;

public class ProfileActivity extends AppCompatActivity {

    private User user;
    private String userId;
    public static int heightUser;
    private static final String TAG = "ProfileActivity";
    private JSONArray streakData = new JSONArray();

    TextView profileName;
    TextInputEditText profileAge;
    AutoCompleteTextView profileGender;
    TextInputEditText profileRmr;
    TextInputEditText profileBmi;
    TextInputEditText profileHeight;
    TextInputEditText profileWeight;
    TextInputEditText stepGoal;
    TextInputEditText heartPointGoal;
    MaterialTextView profileStepsRecord;
    MaterialTextView profileHpRecord;
    MaterialTextView profileStreakRecord;
    MaterialTextView streakCurrent;
    Button logoutButton;

    Button logoutBtn;
    Button fallButton;
    Button activityBtn;
    Button addUserBtn;
    ImageButton back_btn;
    private APIconnection apiConnection;
    ArrayList<String> parameters;
    ArrayList<String> apiData = new ArrayList<>();
    String currentStreak = "a";

    private JsonArrayRequest jsonArrayRequest;
    private String prefixURL = "https://ee5-huzza.herokuapp.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        APIconnection.getInstance(this);
        try {
            apiData.clear();
            user = getIntent().getParcelableExtra("user");
            userId = String.valueOf(user.getUserId());
            Log.i("userParcel", userId);
            apiData.add(userId);
        } catch (Exception e) {
            Log.e("userParcelException", e.toString());
        }
        jsonArrayRequest = new JsonArrayRequest(this);

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

        //Goal section
        stepGoal = findViewById(R.id.stepgoal_edit);
        heartPointGoal = findViewById(R.id.heartpointgoal_edit);

        profileStepsRecord = findViewById(R.id.steps_record);
        profileHpRecord = findViewById(R.id.hp_record);
        profileStreakRecord = findViewById(R.id.streak_record);
        streakCurrent = findViewById(R.id.streak_record_current);

        getUserId();
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

        addUserBtn = findViewById(R.id.addUser_btn);
        addUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateData();

                final Intent intent = new Intent(ProfileActivity.this, AddUserActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
    }

    public void getUserId() {
        userId = Integer.toString(1);
    }

    public void getUserPhysicalData() {
        profileName.setText(user.getUserFirstName() + " " + user.getUserSurname());
        profileWeight.setText(String.valueOf(user.getWeight()));
        profileAge.setText(String.valueOf(user.getAge()));
        profileHeight.setText(String.valueOf(user.getHeight()));
        profileBmi.setText(String.valueOf(user.getBMI()));
        profileRmr.setText(String.valueOf(user.getRMR()));
        profileGender.setText(String.valueOf(user.getGender()), false);
        stepGoal.setText(String.valueOf(user.getDailyStepGoal()));
        heartPointGoal.setText(String.valueOf(user.getDailyHeartpointGoal()));
    }

    public void getUserRecords() {
        apiData.clear();
        apiData.add(String.valueOf(user.getUserId()));
        APIconnection.getInstance().POSTRequest("records", apiData, new ArrayList<String>(Arrays.asList("userId")), new ServerCallback() {
            @Override
            public void onSuccess() {
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
                            String currentStreak = curObject.getString("currentStreak");
                            profileStepsRecord.setText(stepRecord + " steps in a single day");
                            profileHpRecord.setText(hpRecord + " heart points in a single day");
                            profileStreakRecord.setText(streakRecord + " days is your longest streak");
                            if(currentStreak == "null"){
                                streakCurrent.setVisibility(View.GONE);
                            }
                            else{
                                streakCurrent.setText("Your current streak is " + currentStreak + ". Beat your goal for " + ((Integer.valueOf(streakRecord) - Integer.valueOf(currentStreak)) + 1) + " more days to get a new record.");

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
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
        try {
            user.setWeight(Integer.parseInt(profileWeight.getText().toString()));
            user.setAge(Integer.parseInt(profileAge.getText().toString()));
            user.setHeight(Integer.parseInt(profileHeight.getText().toString()));
            user.setGender(profileGender.getText().toString());
            user.setDailyStepGoal(Integer.valueOf(stepGoal.getText().toString()));
            user.setDailyHeartpointGoal(Integer.valueOf(heartPointGoal.getText().toString()));

        } catch (NumberFormatException e) {
            e.printStackTrace();
            Log.e("update", e.toString());
        }
        user.updatePhysicalData();
        user.updateDailyGoals();
    }
}
