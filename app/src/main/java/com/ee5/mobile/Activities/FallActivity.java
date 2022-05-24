package com.ee5.mobile.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ee5.mobile.Interfaces.ServerCallback;
import com.ee5.mobile.R;
import com.ee5.mobile.SupportClasses.APIconnection;
import com.ee5.mobile.SupportClasses.ActModeRecyclerViewAdapter;
import com.ee5.mobile.SupportClasses.Fall;
import com.ee5.mobile.SupportClasses.FallsRecyclerViewAdapter;
import com.ee5.mobile.SupportClasses.JsonArrayRequest;
import com.ee5.mobile.SupportClasses.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class FallActivity extends AppCompatActivity implements FallsRecyclerViewAdapter.OnItemClickListener {

    public static ArrayList<Fall> falls = new ArrayList<>();
    private JsonArrayRequest jsonArrayRequest;
    private String prefixURL = "https://ee5-huzza.herokuapp.com/";
    FallsRecyclerViewAdapter myRecyclerViewAdapter;
    RecyclerView myRecyclerView;
    private String userId;
    private User user;
    ArrayList<String> apiData = new ArrayList<>();
    ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_falls);
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

        myRecyclerView = findViewById(R.id.falls_recyclerview);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        backButton = findViewById(R.id.falls_back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FallActivity.this, ProfileActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

        getFalls();
    }

    public void getFalls() {
        APIconnection.getInstance().GETRequest("falls", apiData, new ServerCallback() {
            @Override
            public void onSuccess() {
                String responseString = "";
                JSONArray responseArray = APIconnection.getInstance().getAPIResponse();
                try {
                    for (int i = 0; i < responseArray.length(); i++) {
                        JSONObject curObject = responseArray.getJSONObject(i);
                        responseString = curObject.getString("ts");
                        String date = responseString.substring(0, 10);
                        String time = responseString.substring(11, 19);
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dddd HH:mm:ss");
                        Date dateTime = formatter.parse(date + " " + time);
                        Fall fall = new Fall(dateTime, date, time);
                        falls.add(fall);
                        Log.d("FALLS", responseString);
                    }
                    myRecyclerViewAdapter = new FallsRecyclerViewAdapter(getApplicationContext(), falls);
                    myRecyclerView.setAdapter(myRecyclerViewAdapter);
                    Collections.sort(falls, Collections.reverseOrder());

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
}
