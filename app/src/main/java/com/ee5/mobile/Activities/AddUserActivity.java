package com.ee5.mobile.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ee5.mobile.Interfaces.ServerCallback;
import com.ee5.mobile.R;
import com.ee5.mobile.SupportClasses.APIconnection;
import com.ee5.mobile.SupportClasses.User;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class AddUserActivity extends AppCompatActivity {

    private User user;

    TextView YourId;
    TextView YourPasscode;
    TextInputEditText NewId;
    TextInputEditText NewPasscode;

    private String userId;
    private String userPasscode;
    private String profileId;

    Button addUserBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        APIconnection.getInstance(this);

        try{
            user = getIntent().getParcelableExtra("user");

            Log.i("userParcel", user.getProfileEmail() + "1");
            Log.i("userParcel", user.getUserEmail());
            profileId = String.valueOf(user.getProfileId());
        }
        catch(Exception e){
            Log.e("userParcelException", e.toString());
        }

        YourId = findViewById(R.id.textYourId);
        YourPasscode = findViewById(R.id.textYourPasscode);
        NewId = findViewById(R.id.TextInputNewId);
        NewPasscode = findViewById(R.id.TextInputNewPasscode);

        YourId.setText(String.valueOf(user.getUserId()));
        YourPasscode.setText(String.valueOf(user.getUserPasscode()));

        addUserBtn = findViewById(R.id.btnAddUser);
        addUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    userId = NewId.getText().toString();
                    userPasscode = NewPasscode.getText().toString();
                    checkPasscode();
                } catch (Exception e){
                    Toast.makeText(AddUserActivity.this, "Please enter the ID and Passcode of the user you want to add", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    private void checkPasscode(){

        APIconnection.getInstance().GETRequest("user", new ArrayList<String>(Arrays.asList(userId)), new ServerCallback() {

            @Override
            public void onSuccess() {

                String responsePasscode = "";
                JSONArray responseArray = APIconnection.getInstance().getAPIResponse();

                try {

                    JSONObject curObject = responseArray.getJSONObject(0);
                    responsePasscode = String.valueOf(curObject.getInt("passcode"));
                        Log.i("API response", responsePasscode);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "User ID or passcode is incorrect", Toast.LENGTH_SHORT).show();
                }

                if (userPasscode.equals(responsePasscode)){
                    addUser();
                } else {
                    Toast.makeText(getApplicationContext(), "User ID or passcode is incorrect", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addUser(){

        ArrayList<String> linkData = new ArrayList<String>();
        //API will be updated to auto increment profileId, after which it doesn't need to be passed anymore
        ArrayList<String> linkParameters = new ArrayList<String>(Arrays.asList("profileId", "userId", "viewOnly"));
        String node = "profileUserLink";

        linkData.add(profileId);
        linkData.add(userId);
        linkData.add("true");

        APIconnection.getInstance().POSTRequest(node, linkData, linkParameters, new ServerCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "User with ID: " + userId + " succesfully added to your account", Toast.LENGTH_SHORT).show();
            }
        });
    }
}