package com.ee5.mobile.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ee5.mobile.Interfaces.ServerCallback;
import com.ee5.mobile.R;
import com.ee5.mobile.SupportClasses.APIconnection;
import com.ee5.mobile.SupportClasses.User;

import java.util.ArrayList;
import java.util.Arrays;

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

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

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

        accountData.add("21");
        accountData.add(firstName);
        accountData.add(surname);
        accountData.add(email);
        accountData.add(password);
        accountData.add("testsalt");

        APIconnection.getInstance().POSTRequest(node, accountData, accountParameters, new ServerCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "Great succes!", Toast.LENGTH_SHORT).show();

                User user = new User(21, firstName, surname, email);

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