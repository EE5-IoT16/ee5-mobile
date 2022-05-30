package com.ee5.mobile.Activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ee5.mobile.Interfaces.ServerCallback;
import com.ee5.mobile.R;
import com.ee5.mobile.SupportClasses.APIconnection;
import com.ee5.mobile.SupportClasses.Activity;
import com.ee5.mobile.SupportClasses.ActModeRecyclerViewAdapter;
import com.ee5.mobile.SupportClasses.Fall;
import com.ee5.mobile.SupportClasses.FallsRecyclerViewAdapter;
import com.ee5.mobile.SupportClasses.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Random;

public class ActivityModeActivity extends AppCompatActivity implements ActModeRecyclerViewAdapter.OnItemClickListener {

    public ArrayList<Activity> activities = new ArrayList<>();
    public ArrayList<String> apiData = new ArrayList<>();
    Button newActivityBtn;
    ActModeRecyclerViewAdapter myRecyclerViewAdapter;
    RecyclerView myRecyclerView;
    private User user;
    private String userId;
    ImageButton backButton;
    int seconds;
    int minutes;
    int hours;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activitymode);
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

        newActivityBtn = findViewById(R.id.newActivity_btn);
        newActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityModeActivity.this, NewActivityActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        myRecyclerView = findViewById(R.id.am_recyclerView);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        myRecyclerViewAdapter = new ActModeRecyclerViewAdapter(this, activities);
        myRecyclerViewAdapter.setOnItemClickListener(this);
        myRecyclerView.setAdapter(myRecyclerViewAdapter);

        parseJson();

        backButton = findViewById(R.id.activity_back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityModeActivity.this, ProfileActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out);
            }
        });
    }

    public void parseJson() {
        APIconnection.getInstance().GETRequest("activity", apiData, new ServerCallback() {
            @Override
            public void onSuccess() {
                String responseString = "";
                JSONArray responseArray = APIconnection.getInstance().getAPIResponse();
                try {
                    for (int i = 0; i < responseArray.length(); i++) {
                        JSONObject curObject = responseArray.getJSONObject(i);
                        String start = curObject.getString("startTime");
                        String startDate = start.substring(0, 10);
                        String startTime = start.substring(11, 19);
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dddd HH:mm:ss");
                        Date dateTimeStart = formatter.parse(startDate + " " + startTime);
                        String end = "";
                        end = curObject.getString("endTime");
                        if (end == "null") {
                            LocalDateTime currentDate = LocalDateTime.now() ;
                            DateTimeFormatter form = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            end = currentDate.format(form);
                        }
                        String endDate = end.substring(0, 10);
                        String endTime = end.substring(11, 19);
                        Date dateTimeEnd = formatter.parse(endDate + " " + endTime);
                        long duration = dateTimeEnd.getTime() - dateTimeStart.getTime();
                        minutes = calculateDurationMinutes(duration);
                        seconds = calculateDurationSeconds(duration);
                        hours = calculateDurationhours(duration);
                        Random r = new Random();
                        int low = 5;
                        int high = 25;
                        int result = r.nextInt(high-low) + low;
                        int result2 = r.nextInt(high-low) + low;
                        String calories = String.valueOf(result2);
                        String steps = curObject.getString("steps");
                        String maxHr = curObject.getString("maxHeartRate");
                        String avgHr = curObject.getString("averageHeartRate");
                        String distance = curObject.getString("distanceCovered");
                        Activity activity = new Activity(dateTimeStart, hours + "h " + minutes + "min " + seconds + "sec", steps, calories, String.valueOf(result), avgHr, maxHr, distance);
                        activities.add(activity);
                    }
                    myRecyclerViewAdapter = new ActModeRecyclerViewAdapter(getApplicationContext(), activities);
                    myRecyclerView.setAdapter(myRecyclerViewAdapter);
                    Collections.sort(activities, Collections.reverseOrder());

                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                    Log.d("ERROR", String.valueOf(e));
                }
            }
        });
    }

    @Override
    public void onItemClick(int position) {

    }

    public int calculateDurationMinutes(long duration) {
        int minutes = (int) ((duration / (1000 * 60)) % 60);
        return minutes;
    }

    public int calculateDurationSeconds(long duration) {
        int seconds = (int) ((duration / 1000) % 60);
        return seconds;
    }

    public int calculateDurationhours(long duration) {
        int hours = (int) ((duration / (1000 * 60 * 60)) % 60);
        return hours;
    }
}
