package com.ee5.mobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class OverviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ConstraintLayout constraintLayout = findViewById(R.id.overview_layout);

        Button viewProfile_btn = findViewById(R.id.viewProfile_Btn);
        TextView quote = findViewById(R.id.quote_tv);

        viewProfile_btn.setOnClickListener(v -> {
            Intent intent = new Intent(OverviewActivity.this, ProfileActivity.class);
        });

    }
}
