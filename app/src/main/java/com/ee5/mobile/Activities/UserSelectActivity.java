package com.ee5.mobile.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.ee5.mobile.Interfaces.ServerCallback;
import com.ee5.mobile.R;
import com.ee5.mobile.SupportClasses.APIconnection;
import com.ee5.mobile.SupportClasses.ActModeRecyclerViewAdapter;
import com.ee5.mobile.SupportClasses.Fall;
import com.ee5.mobile.SupportClasses.FallsRecyclerViewAdapter;
import com.ee5.mobile.SupportClasses.RecyclerViewAdapter;
import com.ee5.mobile.SupportClasses.User;
import com.ee5.mobile.SupportClasses.UserSelectRecyclerViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class UserSelectActivity extends AppCompatActivity /*implements UserSelectRecyclerViewAdapter.OnItemClickListener*/{

    private JSONArray userInformation = new JSONArray();

    private int profileId;
    private int userId;
    private User user;
    UserSelectRecyclerViewAdapter myRecyclerViewAdapter;
    RecyclerView myRecyclerView;
    public static ArrayList<User> users = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_select);

        try{
            user = getIntent().getParcelableExtra("user");
            Log.i("userParcel", user.getProfileEmail());
            profileId = user.getProfileId();
        }
        catch(Exception e){
            Log.e("userParcelException", e.toString());
        }
       /* myRecyclerView = findViewById(R.id.userSelect_recyclerview);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        myRecyclerViewAdapter = new UserSelectRecyclerViewAdapter(this, users);
        //myRecyclerViewAdapter.setOnItemClickListener(this);
        myRecyclerView.setAdapter(myRecyclerViewAdapter);*/

        getData();
       /* User user = new User(1, "test", "test", "test");
        users.add(user);*/

    }

    public void getData(){
        APIconnection.getInstance(this);

        APIconnection.getInstance().GETRequest("profileUserLink", new ArrayList<String>(Arrays.asList(Integer.toString(profileId))), new ServerCallback() {
            @Override
            public void onSuccess() {

                JSONArray responseArray = APIconnection.getInstance().getAPIResponse();

                try {
                    for( int i = 0; i < responseArray.length(); i++ ) {
                        JSONObject curObject = responseArray.getJSONObject(i);
                        int userId = curObject.getInt("userId");

                        APIconnection.getInstance().GETRequest("user", new ArrayList<String>(Arrays.asList(Integer.toString(userId))), new ServerCallback() {
                            @Override
                            public void onSuccess() {

                                JSONArray responseArray2 = APIconnection.getInstance().getAPIResponse();
                                try {
                                    JSONObject Object = responseArray2.getJSONObject(0);
                                    userInformation.put(Object);

                                    if (responseArray.length() == userInformation.length()) {
                                        generateUIFromDB();


                                    }
                                    else {
                                        Log.i("UserInformation", userInformation.toString());
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(UserSelectActivity.this, "Not all data could be fetched from the database", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(UserSelectActivity.this, "Not all data could be fetched from the database", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void generateUIFromDB() {
        ScrollView layout = new ScrollView(this);
        layout.setLayoutParams(new ScrollView.LayoutParams(ScrollView.LayoutParams.FILL_PARENT, ScrollView.LayoutParams.FILL_PARENT));
        TableLayout tableLayout = createTableLayout(this);

        JSONObject currentObject;
        int userID, rowCounter = 0;
        String userFirstName, userSurname, userName, userEmail;
        TableRow tableRow = null;
        Button button;

        for (int i = 0; i < userInformation.length(); i++) {
            if (rowCounter%3 == 0) {
                tableRow = createTableRow(this);
                tableLayout.addView(tableRow);
            }

            try {
                currentObject = userInformation.getJSONObject(i);
                userFirstName = currentObject.getString("name");
                userSurname = currentObject.getString("surname");
                userName = userFirstName + " " + userSurname;


                button = createButton(this, i, userName);
                tableRow.addView(button);

                rowCounter++;

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        layout.addView(tableLayout);
        setContentView(layout);
    }

    private TableLayout createTableLayout(Context context) {
        TableLayout tableLayout = new TableLayout(context);
        tableLayout.setLayoutParams(new ScrollView.LayoutParams(ScrollView.LayoutParams.FILL_PARENT, ScrollView.LayoutParams.FILL_PARENT));
        return tableLayout;
    }

    private TableRow createTableRow(Context context) {
        TableRow tableRow = new TableRow(context);
        tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
        tableRow.setPadding(35,0,10,10);
        return tableRow;
    }

    private Button createButton(Context context, int id, String name) {
        Button btnTag = new Button(context);
        btnTag.setWidth(300);

        btnTag.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
        //name = DatabaseUtils.reformatText(name);
        btnTag.setText(name);
        btnTag.setId(id);
        btnTag.setAllCaps(false);
        btnTag.setOnClickListener(this::onButtonClick);
        //btnTag.setCompoundDrawablesWithIntrinsicBounds(null, getImage(this, imagePath), null, null);
        btnTag.cancelLongPress();
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) (ViewGroup.MarginLayoutParams) btnTag.getLayoutParams();
        marginLayoutParams.setMargins(30,0,0,0);
        return btnTag;
    }

    /*
    private Drawable getImage(Context context, String imagePath) {
        imagePath = imagePath.replaceAll(".png", "");
        return context.getDrawable(context.getResources().getIdentifier(imagePath, "drawable", context.getPackageName()));
    }
    */

    protected void onButtonClick(View caller) {

        int ObjectNo = caller.getId();
        try {
            JSONObject Object = userInformation.getJSONObject(ObjectNo);
            userId = Object.getInt("userId");
            user.setUserId(userId);
            user.setUserFirstName(Object.getString("name"));
            user.setUserSurname(Object.getString("surname"));
            user.setUserEmail(Object.getString("email"));
            user.setUserPasscode(Object.getInt("passcode"));
            Log.i("PASSCODE", String.valueOf(user.getUserPasscode()));

            getPhysicalData();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getPhysicalData(){

        APIconnection.getInstance().GETRequest("physicalData", new ArrayList<String>(Arrays.asList(String.valueOf(userId))), new ServerCallback() {

            @Override
            public void onSuccess() {

                JSONArray responseArray = APIconnection.getInstance().getAPIResponse();

                try {

                    JSONObject curObject = responseArray.getJSONObject(0);
                    user.setWeight(curObject.getInt("weight"));
                    user.setHeight(curObject.getInt("height"));
                    user.setAge(curObject.getInt("age"));
                    user.setGender(curObject.getString("gender"));
                    Log.i("API response", curObject.getString("gender"));
                    getGoalsData();

                } catch (JSONException e) {
                    e.printStackTrace();
                    getGoalsData();
                }
            }
        });

    }

    private void getGoalsData(){

        APIconnection.getInstance().GETRequest("goals", new ArrayList<String>(Arrays.asList(String.valueOf(userId))), new ServerCallback() {

            @Override
            public void onSuccess() {

                JSONArray responseArray = APIconnection.getInstance().getAPIResponse();

                try {

                    JSONObject curObject = responseArray.getJSONObject(0);
                    user.setDailyStepGoal(curObject.getInt("dailySteps"));
                    user.setDailyHeartpointGoal(curObject.getInt("dailyHeartP"));
                    Log.i("API response", String.valueOf(curObject.getInt("dailyHeartP")));
                    nextIntent();

                } catch (JSONException e) {
                    e.printStackTrace();
                    nextIntent();
                }
            }
        });

    }

    private void nextIntent(){
        Intent intent = new Intent(this, OverviewActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}