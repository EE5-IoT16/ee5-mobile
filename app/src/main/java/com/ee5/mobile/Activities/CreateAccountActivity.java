package com.ee5.mobile.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ee5.mobile.Interfaces.ServerCallback;
import com.ee5.mobile.R;
import com.ee5.mobile.SupportClasses.APIconnection;
import com.ee5.mobile.SupportClasses.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class CreateAccountActivity extends AppCompatActivity {

    private String firstName;
    private String surname;
    private String email;
    private String password;
    private String passwordRepeat;

    private EditText firstNameInput;
    private EditText surnameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText passwordRepeatInput;

    private int profileId;
    private int userId;

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        ConstraintLayout constraintLayout = findViewById(R.id.createAccount_layout);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(0);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        APIconnection.getInstance(this);

        firstNameInput = (EditText) findViewById(R.id.editTextFirstName);
        surnameInput = (EditText) findViewById(R.id.editTextSurname);
        emailInput = (EditText) findViewById(R.id.editTextEmail);
        passwordInput = (EditText) findViewById(R.id.editTextPassword);
        passwordRepeatInput = (EditText) findViewById(R.id.editTextPasswordRepeat);


        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<String> accountData = new ArrayList<String>();
                ArrayList<String> accountParameters = new ArrayList<String>();

                firstName = firstNameInput.getText().toString();
                surname = surnameInput.getText().toString();
                email = emailInput.getText().toString();
                password = passwordInput.getText().toString();
                passwordRepeat = passwordRepeatInput.getText().toString();

                if (firstName.equals("") || surname.equals("") || email.equals("") ||
                        password.equals("") || passwordRepeat.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
                }
                else if (!password.equals(passwordRepeat)){
                    Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
                else {
                    createAccount();
                }

            }
        });
    }

    private void createAccount(){

        ArrayList<String> accountData = new ArrayList<String>();
        //API will be updated to auto increment profileId, after which it doesn't need to be passed anymore
        ArrayList<String> accountParameters = new ArrayList<String>(Arrays.asList("profileId", "name", "surname", "email", "password", "salt"));
        String node = "profile";

        accountData.add(firstName);
        accountData.add(surname);
        accountData.add(email);
        accountData.add(password);
        accountData.add("testsalt");

        APIconnection.getInstance().POSTRequest(node, accountData, accountParameters, new ServerCallback() {
            @Override
            public void onSuccess() {

                try {
                    JSONArray responseArray = APIconnection.getInstance().getAPIResponse();
                    JSONObject curObject = responseArray.getJSONObject(0);
                    profileId = curObject.getInt("profileId");
                } catch (Exception e){

                }
                createUser();
            }
        });
    }

    private void createUser(){

        ArrayList<String> userData = new ArrayList<String>();
        //API will be updated to auto increment profileId, after which it doesn't need to be passed anymore
        ArrayList<String> userParameters = new ArrayList<String>(Arrays.asList("name", "surname", "email", "passcode"));
        String node = "user";

        int passcode = ThreadLocalRandom.current().nextInt(0, 9999 + 1);

        userData.add(firstName);
        userData.add(surname);
        userData.add(email);
        userData.add(String.valueOf(passcode));

        APIconnection.getInstance().POSTRequest(node, userData, userParameters, new ServerCallback() {
            @Override
            public void onSuccess() {

                try {
                    JSONArray responseArray = APIconnection.getInstance().getAPIResponse();
                    JSONObject curObject = responseArray.getJSONObject(0);
                    userId = curObject.getInt("userId");
                } catch (Exception e){

                }

                linkProfileUser();

            }
        });
    }

    private void linkProfileUser(){

        ArrayList<String> linkData = new ArrayList<String>();
        //API will be updated to auto increment profileId, after which it doesn't need to be passed anymore
        ArrayList<String> linkParameters = new ArrayList<String>(Arrays.asList("profileId", "userId", "viewOnly"));
        String node = "profileUserLink";

        linkData.add(String.valueOf(profileId));
        linkData.add(String.valueOf(userId));
        linkData.add("false");

        APIconnection.getInstance().POSTRequest(node, linkData, linkParameters, new ServerCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "Great succes!", Toast.LENGTH_SHORT).show();

                User user = new User(profileId, firstName, surname, email, userId, firstName, surname, email);

                Intent overviewIntent = new Intent(getApplicationContext(), OverviewActivity.class);
                overviewIntent.putExtra("user", user);
                startActivity(overviewIntent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }
        });
    }

    private void exitIntent(){
        final Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_down);
    }
}