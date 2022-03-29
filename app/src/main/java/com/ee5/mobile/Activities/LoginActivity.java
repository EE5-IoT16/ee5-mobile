package com.ee5.mobile.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ee5.mobile.R;
import com.ee5.mobile.SupportClasses.APIconnection;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private String email, password;
    private ArrayList<String> loginData = new ArrayList<String>();
    private ArrayList<String> loginParameters = new ArrayList<String>();

    private EditText emailInput;
    private EditText passwordInput;

    private Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ConstraintLayout constraintLayout = findViewById(R.id.login_layout);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(0);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        emailInput = (EditText) findViewById(R.id.emailInput);
        passwordInput = (EditText) findViewById(R.id.passwordInput);

        continueButton = (Button) findViewById(R.id.btnContinue);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email = emailInput.getText().toString();
                password = passwordInput.getText().toString();
                loginData.add(email);
                loginData.add(password);
                loginParameters.add("email");
                loginParameters.add("password");
                login();

            }
        });
    }

    public void login() {

        String responseString = "";
        APIconnection loginRequest = new APIconnection(this,"User", loginParameters, loginData);
        /*
        JSONObject loginResponse = loginRequest.GETRequest();
        try {
            responseString += loginResponse.getString("password") + " : " + loginResponse.getString("salt") + "\n";
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("API response", responseString);
        */
        responseString = loginRequest.GETRequest();
        Log.i("response:", responseString);

    }

    //Setup connection to database

    // GET request
    // SELECT password FROM User WHERE email = "email"

    //if query result returns none > wrong email
    //else if passwords do not match > wrong password


}