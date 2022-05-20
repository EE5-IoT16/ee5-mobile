package com.ee5.mobile.Activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ee5.mobile.R;
import com.ee5.mobile.SupportClasses.Activity;
import com.ee5.mobile.SupportClasses.ActModeRecyclerViewAdapter;

import java.util.ArrayList;

public class ActivityModeActivity extends AppCompatActivity implements ActModeRecyclerViewAdapter.OnItemClickListener {

    public ArrayList<Activity> activities = new ArrayList<>();
    Button newActivityBtn;
    ActModeRecyclerViewAdapter myRecyclerViewAdapter;
    RecyclerView myRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activitymode);
        newActivityBtn = findViewById(R.id.newActivity_btn);
        newActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityModeActivity.this, NewActivityActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
    }

    public void parseJson() {
        //temporary:
        Activity activity1 = new Activity(null, null, "835", "345", "29", null, null, null);
        Activity activity2 = new Activity(null, null, "835", "345", "29", null, null, null);
        Activity activity3 = new Activity(null, null, "835", "345", "29", null, null, null);

        activities.add(activity1);
        activities.add(activity2);
        activities.add(activity3);

        //get data from database
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
