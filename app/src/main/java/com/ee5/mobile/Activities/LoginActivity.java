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

import com.ee5.mobile.Interfaces.ServerCallback;
import com.ee5.mobile.R;
import com.ee5.mobile.SupportClasses.APIconnection;

import org.json.JSONArray;
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

    private APIconnection loginRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        APIconnection.getInstance(this);

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
                loginParameters.add("email");
                login();

            }
        });
    }

    public void login() {

        APIconnection.getInstance().GETRequest("profile", loginData, new ServerCallback() {
            @Override
            public void onSuccess() {

                String responseString = "";

                JSONArray responseArray = APIconnection.getInstance().getAPIResponse();

                try {
                    for( int i = 0; i < responseArray.length(); i++ ) {
                        JSONObject curObject = responseArray.getJSONObject(i);
                        responseString += curObject.getString("password") + " : " + curObject.getString("salt") + "\n";
                        Log.i("API response", responseString);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });





        //responseString = loginRequest.GETRequest(loginData);


        //Log.i("response:", String.valueOf(loginResponse));

    }

    //Setup connection to database

    // GET request
    // SELECT password FROM User WHERE email = "email"

    //if query result returns none > wrong email
    //else if passwords do not match > wrong password


}