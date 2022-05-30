package com.ee5.mobile.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.ee5.mobile.Interfaces.ServerCallback;
import com.ee5.mobile.R;
import com.ee5.mobile.SupportClasses.APIconnection;
import com.ee5.mobile.SupportClasses.Activity;
import com.ee5.mobile.SupportClasses.User;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class NewActivityActivity extends AppCompatActivity {

    ArrayList<Activity> activities = new ArrayList<>();
    public ArrayList<String> apiData = new ArrayList<>();
    TextView timerText;
    Button stopStartButton;
    ImageButton newAct_back_btn;
    TextInputEditText calories;
    TextInputEditText heartRate;
    TextInputEditText distanceText;
    TextInputEditText activitySteps;

    Timer timer;
    TimerTask timerTask;
    Double time = 0.0;

    private User user;
    private String userId;
    int stepsStart = 0;
    int stepsActivity = 0;
    int currHR = 0;
    Boolean startedActivity = false;

    LocalDateTime localStartDateTime = null;
    Date localDate = null;
    ZonedDateTime startDateTime = null;
    ZonedDateTime stopDateTime = null;
    private DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;

    boolean timerStarted = false;
    boolean activityEnded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_activity);
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

        timerText = findViewById(R.id.timerText);
        stopStartButton = findViewById(R.id.startStopButton);
        calories = findViewById(R.id.calories_edit);
        heartRate = findViewById(R.id.bpm_edit);
        distanceText = findViewById(R.id.distance_edit);
        activitySteps = findViewById(R.id.steps_edit);

        newAct_back_btn = findViewById(R.id.newAct_back_btn);
        newAct_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(NewActivityActivity.this, ActivityModeActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out);
            }
        });
        timer = new Timer();

        final Handler handler = new Handler();
        final int delay = 1000;        //every 5 sec

        handler.postDelayed(new Runnable() {
            public void run() {
                getCurrentSteps();
                getLiveHr();
                calculate();
                Log.d("steps", String.valueOf(stepsActivity));
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    //program goes here when button is pressed
    public void startStopPressed(View view) {
        if (timerStarted == false) {
            startedActivity = true;
            timerStarted = true;
            setButtonUI("Stop", R.color.HeartRed);
            localStartDateTime = LocalDateTime.now();
            localDate = new Date();
            startDateTime = localStartDateTime.atZone(ZoneId.of("UTC-2")).withZoneSameInstant(ZoneId.of("UTC"));
            startTimer();
            getStepsAtStart();
        } else {
            startedActivity = false;
            resetTimer();
            stopDateTime = LocalDateTime.now().atZone(ZoneId.of("UTC-2")).withZoneSameInstant(ZoneId.of("UTC"));
            Log.d("endtime", "endtime = " + stopDateTime.toString());
            endedActivity();
        }
    }

    private void setButtonUI(String indicator, int color) {
        stopStartButton.setText(indicator);
        stopStartButton.setBackgroundColor(ContextCompat.getColor(this, color));
    }

    private void startTimer() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {      //only simple timer so can run on main thread
                    @Override
                    public void run() {
                        time++;
                        timerText.setText(getTimerText());
                    }
                });
            }

        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000);

        ArrayList<String> parameters = new ArrayList<>(Arrays.asList("userId", "startTime"));
        ArrayList<String> values = new ArrayList<>(Arrays.asList(userId, startDateTime.format(formatter)));
        Log.i("POST datetime", values.toString());

        APIconnection.getInstance().POSTRequest("activity", values, parameters, new ServerCallback() {
            @Override
            public void onSuccess() {
                Log.i("POST datetime response", values.toString());
            }
        });
    }

    private void resetTimer() {
        timerTask.cancel();
        setButtonUI("Start", R.color.StepBlue);
        time = 0.0;
        timerStarted = false;
        timerText.setText(formatTime(0, 0, 0));

    }

    public void getStepsAtStart() {
        APIconnection.getInstance().GETRequest("steps", apiData, new ServerCallback() {
            @Override
            public void onSuccess() {
                String responseString = "";
                JSONArray responseArray = APIconnection.getInstance().getAPIResponse();
                try {
                    JSONObject curObject = responseArray.getJSONObject(responseArray.length() - 1);
                    stepsStart = curObject.getInt("steps");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getCurrentSteps() {
        APIconnection.getInstance().GETRequest("steps", apiData, new ServerCallback() {
            @Override
            public void onSuccess() {
                String responseString = "";
                JSONArray responseArray = APIconnection.getInstance().getAPIResponse();
                try {
                    JSONObject curObject = responseArray.getJSONObject(responseArray.length() - 1);
                    int currentSteps = curObject.getInt("steps");
                    Log.d("steps", "read: " + String.valueOf(currentSteps));
                    stepsActivity = currentSteps - stepsStart;
                    if (startedActivity) activitySteps.setText(String.valueOf(stepsActivity));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getLiveHr() {
        APIconnection.getInstance().GETRequest("heartRate", apiData, new ServerCallback() {
            @Override
            public void onSuccess() {
                String responseString = "";
                JSONArray responseArray = APIconnection.getInstance().getAPIResponse();
                try {
                    JSONObject curObject = responseArray.getJSONObject(responseArray.length() - 1);
                    currHR = curObject.getInt("bpm");
                    if (startedActivity) heartRate.setText(String.valueOf(currHR));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void calculate() {
        int height = user.getHeight();
        calculateDistance(height, stepsActivity);
        calculateCalories();
    }

    public void calculateDistance(int height, int steps) {
        Double dist = 0.0;
        Double strideLength = height * 0.43;        //https://www.inchcalculator.com/steps-to-distance-calculator/
        dist = (strideLength * steps) / 100000;
        DecimalFormat df = new DecimalFormat("0.00");
        String result =df.format(dist);
        if (startedActivity) distanceText.setText(String.valueOf(result));
    }

    public void calculateCalories() {
        Double caloriesBurned = 0.0;
        double weight = user.getWeight();
        int height = user.getHeight();
        int age = user.getAge();

        // Need new formula that uses time spend as well;
        double cal = ((time/60) * (currHR - 60) * weight)/200;

        DecimalFormat df = new DecimalFormat("0.00");
        String result =df.format(cal);
        if (startedActivity) calories.setText(String.valueOf(result));
    }

    private String getTimerText() {
        int rounded = (int) Math.round(time);

        int seconds = ((rounded % 86400) % 3600) % 60;
        int minutes = ((rounded % 86400) % 3600) / 60;
        int hours = ((rounded % 86400) / 3600);

        return formatTime(seconds, minutes, hours);
    }

    private String formatTime(int seconds, int minutes, int hours) {
        return String.format("%02d", hours) + " : " + String.format("%02d", minutes) + " : " + String.format("%02d", seconds);
    }

    public void endedActivity() {
        //get data from activity
        Activity activity = new Activity(localDate, null, null, null, null, null, null, null);
        //Activity activity = new Activity(startDateTime, null, null, null, null, null, null, null);
        //post to activityTable in database so it can be fetched in activities recyclerview

        /*ChronoUnit chronoUnit = null;*/
        //Log.d("time", "START = " + String.valueOf(startDateTime));
        //Log.d("time", "STOP = " +String.valueOf(stopDateTime));
        long duration = ChronoUnit.MILLIS.between(startDateTime, stopDateTime);
        Log.d("time", "DURATION = " + String.valueOf(duration));
        long minutes = (ChronoUnit.MINUTES.between(startDateTime, stopDateTime)) * 60;
        Log.d("time", "MIN = " + String.valueOf(minutes));
        long seconds = ChronoUnit.SECONDS.between(startDateTime, stopDateTime);
        Log.d("time", "SEC = " + String.valueOf(seconds));
        long hours = (ChronoUnit.HOURS.between(startDateTime, stopDateTime)) * 3600;
        Log.d("time", "HOUR = " + String.valueOf(hours));
        long total = minutes + seconds + hours;

        ArrayList<String> parameters = new ArrayList<>(Arrays.asList("userId", "startTime", "endTime", "duration"));
        ArrayList<String> values = new ArrayList<>(Arrays.asList(userId, startDateTime.format(formatter), stopDateTime.format(formatter), String.valueOf(total)));
        Log.i("POST datetime", values.toString());

        APIconnection.getInstance().POSTRequest("activity", values, parameters, new ServerCallback() {
            @Override
            public void onSuccess() {
                Log.i("POST datetime response", values.toString());
            }
        });
    }


}

