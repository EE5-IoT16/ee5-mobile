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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class ActivityModeActivity extends AppCompatActivity implements ActModeRecyclerViewAdapter.OnItemClickListener {

    public ArrayList<Activity> activities = new ArrayList<>();
    public ArrayList<String> apiData = new ArrayList<>();
    Button newActivityBtn;
    ActModeRecyclerViewAdapter myRecyclerViewAdapter;
    RecyclerView myRecyclerView;
    private User user;
    private String userId;
    ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activitymode);
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
            }
        });
        myRecyclerView = findViewById(R.id.am_recyclerView);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        myRecyclerViewAdapter = new ActModeRecyclerViewAdapter(this, activities);
        myRecyclerViewAdapter.setOnItemClickListener(this);
        myRecyclerView.setAdapter(myRecyclerViewAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(myRecyclerView);

        parseJson();

        backButton = findViewById(R.id.activity_back_btn);
        newActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityModeActivity.this, ProfileActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
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
                        responseString = curObject.getString("startTime");
                        String date = responseString.substring(0, 10);
                        String time = responseString.substring(11, 19);
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dddd HH:mm:ss");
                        Date dateTime = formatter.parse(date + " " + time);
                        String endTime = curObject.getString("endTime");
                        String calories = curObject.getString("caloriesBurned");
                        String steps = curObject.getString("steps");
                        String maxHr = curObject.getString("maxHeartRate");
                        String avgHr = curObject.getString("averageHeartRate");
                        String distance = curObject.getString("distanceCovered");
                        Activity activity = new Activity(dateTime, null, steps, calories, null, avgHr, maxHr, distance);
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

    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            Toast.makeText(ActivityModeActivity.this, "on Move", Toast.LENGTH_SHORT).show();
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
            //Remove swiped item from list and notify the RecyclerView
            int position = viewHolder.getAdapterPosition();
            activities.remove(position);
            myRecyclerViewAdapter.notifyDataSetChanged();
            //delete from database

        }
    };

}
