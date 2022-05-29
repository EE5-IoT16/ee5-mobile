package com.ee5.mobile.Activities;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;


public class NewActivityActivity extends AppCompatActivity {

    ArrayList<Activity> activities = new ArrayList<>();
    public ArrayList<String> apiData = new ArrayList<>();
    TextView timerText;
    Button stopStartButton;
    TextView calories;
    TextView avgHeartRate;
    TextView steps;
    TextView distance;

    Timer timer;
    TimerTask timerTask;
    Double time = 0.0;

    private User user;
    private String userId;

    LocalDateTime localStartDateTime = null;
    ZonedDateTime startDateTime = null;
    ZonedDateTime stopDateTime = null;
    private DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;

    boolean timerStarted = false;
    boolean activityEnded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_activity);

        try{
            apiData.clear();
            user = getIntent().getParcelableExtra("user");
            Log.i("userParcel", user.getProfileEmail() + "1");
            Log.i("userParcel", user.getUserEmail());
            userId = String.valueOf(user.getUserId());
            Log.i("userParcel", userId);
            apiData.add(userId);
        }
        catch(Exception e){
            Log.e("userParcelException", e.toString());
        }

        timerText = findViewById(R.id.timerText);
        stopStartButton = findViewById(R.id.startStopButton);
        calories = findViewById(R.id.calories_edit);
        avgHeartRate = findViewById(R.id.bpm_edit);
        steps = findViewById(R.id.steps_edit);
        distance = findViewById(R.id.distance_edit);

        APIconnection.getInstance(this);

        timer = new Timer();
    }

    //program goes here when button is pressed
    public void startStopPressed(View view) {
        if (timerStarted == false) {
            timerStarted = true;
            setButtonUI("Stop", R.color.HeartRed);
            localStartDateTime = LocalDateTime.now();
            startDateTime = localStartDateTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));
            startTimer();
        } else {
            resetTimer();
            stopDateTime = LocalDateTime.now().atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));
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
        Activity activity = new Activity(localStartDateTime, null, null, null, null, null, null, null);
        //post to activityTable in database so it can be fetched in activities recyclerview

        ArrayList<String> parameters = new ArrayList<>(Arrays.asList("userId", "startTime", "endTime"));
        ArrayList<String> values = new ArrayList<>(Arrays.asList(userId, startDateTime.format(formatter), stopDateTime.format(formatter)));
        Log.i("POST datetime", values.toString());

        APIconnection.getInstance().POSTRequest("activity", values, parameters, new ServerCallback() {
            @Override
            public void onSuccess() {
                Log.i("POST datetime response", values.toString());
            }
        });
    }

}

