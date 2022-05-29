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

public class ProfileActivity extends AppCompatActivity {

    private String name;
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
    MaterialTextView profileStepsRecord;
    MaterialTextView profileHpRecord;
    MaterialTextView profileStreakRecord;
    MaterialTextView streakCurrent;
    Button logoutButton;
    User user;

    Button logoutBtn;
    Button fallButton;
    Button activityBtn;
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

        profileStepsRecord = findViewById(R.id.steps_record);
        profileHpRecord = findViewById(R.id.hp_record);
        profileStreakRecord = findViewById(R.id.streak_record);
        streakCurrent = findViewById(R.id.streak_record_current);

        //getUserId();
        getUserName();
        getUserPhysicalData();
        getCurrentStreak();
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
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });


    }


    public void getUserName() {
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
                    Log.d("ERRER", e.toString());
                }
            }
        });
    }

    public void getUserPhysicalData() {
        APIconnection.getInstance().GETRequest("physicaldata", apiData, new ServerCallback() {
            @Override
            public void onSuccess() {
                JSONArray responseArray = APIconnection.getInstance().getAPIResponse();
                try {
                    JSONObject curObject = responseArray.getJSONObject(0);
                    String weight = curObject.getString("weight");
                    String height = curObject.getString("height");
                    heightUser = Integer.parseInt(height);
                    Log.d("abc", curObject.toString());
                    String age = curObject.getString("age");
                    String gender = curObject.getString("gender");
                    String bmi = curObject.getString("bmi");
                    String rmr = curObject.getString("rmr");
                    profileWeight.setText(weight);
                    profileAge.setText(age);
                    profileHeight.setText(height);
                    profileBmi.setText(bmi);
                    profileRmr.setText(rmr);
                    profileGender.setText(gender);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getUserRecords() {
        APIconnection.getInstance().GETRequest("records", apiData, new ServerCallback() {
            @Override
            public void onSuccess() {
                String responseString = "";
                JSONArray responseArray = APIconnection.getInstance().getAPIResponse();
                try {
                    JSONObject curObject = responseArray.getJSONObject(0);
                    Log.d("ERRER", curObject.toString());
                    String stepRecord = curObject.getString("maxStepDay");
                    String hpRecord = curObject.getString("maxHeartPointDay");
                    String streakRecord = curObject.getString("streak");
                    profileStepsRecord.setText(stepRecord + " steps in a single day");
                    profileHpRecord.setText(hpRecord + " heart points in a single day");
                    profileStreakRecord.setText(streakRecord + " days is your longest streak");
                    curObject = streakData.getJSONObject(0);
                    String currStreak = curObject.getString("currentStreak");
                    Toast.makeText(getApplicationContext(), currStreak, Toast.LENGTH_SHORT).show();
                    streakCurrent.setText("Your current streak is " + currStreak + ". Beat your goal for " +
                            ((Integer.valueOf(streakRecord) - Integer.valueOf(currStreak)) + 1) + " more days to get a new record.");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getCurrentStreak() {
        APIconnection.getInstance().GETRequest("goalsCompleted", apiData, new ServerCallback() {
            @Override
            public void onSuccess() {
                String responseString = "";
                JSONArray responseArray = APIconnection.getInstance().getAPIResponse();
                try {
                    JSONObject curObject = responseArray.getJSONObject(responseArray.length() - 1);
                    streakData.put(curObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void exitIntent() {
        final Intent intent = new Intent(ProfileActivity.this, OverviewActivity.class);
        intent.putExtra("user", user);

        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, R.anim.slide_right_out);

    }
}
