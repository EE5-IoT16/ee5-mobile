package com.ee5.mobile.Activities;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.ee5.mobile.R;
import com.ee5.mobile.SupportClasses.Activity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class NewActivityActivity extends AppCompatActivity {

    ArrayList<Activity> activities = new ArrayList<>();
    TextView timerText;
    Button stopStartButton;
    TextView calories;
    TextView avgHeartRate;
    TextView steps;
    TextView distance;

    Timer timer;
    TimerTask timerTask;
    Double time = 0.0;

    LocalDateTime startDateTime = null;

    boolean timerStarted = false;
    boolean activityEnded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_activity);

        timerText = findViewById(R.id.timerText);
        stopStartButton = findViewById(R.id.startStopButton);
        calories = findViewById(R.id.calories_edit);
        avgHeartRate = findViewById(R.id.bpm_edit);
        steps = findViewById(R.id.steps_edit);
        distance = findViewById(R.id.distance_edit);

        timer = new Timer();
    }

    //program goes here when button is pressed
    public void startStopPressed(View view) {
        if (timerStarted == false) {
            timerStarted = true;
            setButtonUI("Stop", R.color.HeartRed);
            startDateTime = LocalDateTime.now();
            startTimer();
        } else {
            resetTimer();
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
        Activity activity = new Activity(startDateTime, null, null, null, null, null, null, null);
        //post to activityTable in database so it can be fetched in activities recyclerview
    }

}

