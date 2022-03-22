package com.ee5.mobile.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.widget.ProgressBar;

import com.ee5.mobile.R;

public class OverviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ConstraintLayout constraintLayout = findViewById(R.id.overview_layout);

        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress(50);
    }
}
