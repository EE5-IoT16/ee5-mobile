package com.ee5.mobile.Activities;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.ee5.mobile.Interfaces.ServerCallback;
import com.ee5.mobile.R;
import com.ee5.mobile.SupportClasses.APIconnection;
import com.ee5.mobile.SupportClasses.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private String email, password;

    private EditText emailInput;
    private EditText passwordInput;

    private Button continueButton;
    private Button createAccountButton;

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

                ArrayList<String> loginData = new ArrayList<String>();

                email = emailInput.getText().toString();
                password = passwordInput.getText().toString();
                loginData.add(email);

                if (email.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please enter an email address", Toast.LENGTH_SHORT).show();
                } else {
                    login(loginData);
                }

            }
        });

        createAccountButton = (Button) findViewById(R.id.btnCreateAccount);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CreateAccountActivity.class);
                startActivity(intent);
            }
        });
    }

    public void login(ArrayList<String> loginData) {

        APIconnection.getInstance().GETRequest("profile", loginData, new ServerCallback() {
            int responseProfileId;
            @Override
            public void onSuccess() {

                String responseString = "";
                String responsePassword = "";
                String responseSalt = "";
                String responseFirstName = "";
                String responseSurname = "";
                String responseEmail = "";
                JSONArray responseArray = APIconnection.getInstance().getAPIResponse();

                try {
                    for( int i = 0; i < responseArray.length(); i++ ) {
                        JSONObject curObject = responseArray.getJSONObject(i);
                        responseString += curObject.getString("password") + " : " + curObject.getString("salt") + "\n";
                        responsePassword += curObject.getString("password");
                        responseSalt += curObject.getString("salt");
                        responseFirstName += curObject.getString("name");
                        responseSurname += curObject.getString("surname");
                        responseEmail += curObject.getString("email");
                        responseProfileId = curObject.getInt("profileId");
                        Log.i("API response", responsePassword);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (password.equals(responsePassword)){
                    User user = new User(responseProfileId, responseFirstName, responseSurname, responseEmail);

                    Intent overviewIntent = new Intent(getApplicationContext(), UserSelectActivity.class);
                    overviewIntent.putExtra("user", user);
                    startActivity(overviewIntent);
                } else {
                    Toast.makeText(getApplicationContext(), "Incorrect email or password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}