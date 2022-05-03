package com.ee5.mobile.Activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ee5.mobile.R;
import com.ee5.mobile.SupportClasses.ActModeRecyclerViewAdapter;
import com.ee5.mobile.SupportClasses.Fall;
import com.ee5.mobile.SupportClasses.FallsRecyclerViewAdapter;
import com.ee5.mobile.SupportClasses.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

public class FallActivity extends AppCompatActivity implements FallsRecyclerViewAdapter.OnItemClickListener {

    public static ArrayList<Fall> falls = new ArrayList<>();
    private JsonArrayRequest jsonArrayRequest;
    private String prefixURL = "https://ee5-huzza.herokuapp.com/";
    FallsRecyclerViewAdapter myRecyclerViewAdapter;
    RecyclerView myRecyclerView;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_falls);
        jsonArrayRequest = new JsonArrayRequest(this);

        myRecyclerView = findViewById(R.id.falls_recyclerview);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        getUserId();
        getFalls();
        //getData();
    }

    public void getData(){
        Fall fall = new Fall("test1", "test1");
        falls.add(fall);
    }

    public void getUserId() {
        userId = Integer.toString(3);
    }

    public void getFalls() {
        jsonArrayRequest.getJSONArray(response -> {
            try {
                Log.i("onResponse:", response.toString());
                JSONObject user = response.getJSONObject(0);
                String timeStamp = user.getString("ts");
                String date = timeStamp.substring(0, 10);
                String time = timeStamp.substring(11,19);
                Fall fall = new Fall(date, time);
                falls.add(fall);

                myRecyclerViewAdapter = new FallsRecyclerViewAdapter(this, falls);
                myRecyclerView.setAdapter(myRecyclerViewAdapter);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, prefixURL + "falls/" + userId);
    }

    @Override
    public void onItemClick(int position) {

    }
}
