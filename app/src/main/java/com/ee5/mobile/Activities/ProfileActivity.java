package com.ee5.mobile.Activities;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.ee5.mobile.R;

public class ProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        LinearLayout linearLayout = findViewById(R.id.profile_layout);

        //ProgressBar progressBar = findViewById(R.id.progressBar);
        //progressBar.setProgress(50);
    }
}
