package com.ee5.mobile.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.ee5.mobile.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class OverviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        ConstraintLayout constraintLayout = findViewById(R.id.overview_layout);

        Button viewProfile_btn = findViewById(R.id.viewProfile_Btn);
        TextView stepsLeft_num = findViewById(R.id.stepsLeft_num);
        TextView quote = findViewById(R.id.quote_tv);
        Button setup_btn = findViewById(R.id.setupBtn);

        viewProfile_btn.setOnClickListener(v -> {
            Intent intent = new Intent(OverviewActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        setup_btn.setOnClickListener(view -> {
            Intent intent = new Intent(OverviewActivity.this, SetupActivity.class);
            startActivity(intent);
        });


        //TODO: get number from User object
        stepsLeft_num.setText("100");

        String quoteString = "Look in the mirror, that’s the only competition.";

        //quoteString = getColoredText();

        //quote = get from http request to some api
        quote.setText(getColoredText(quoteString, "#4FA4FF"));

    }

    public Spanned getColoredText(String text, String color) {
        String[] words = text.split(" ");
        int wordCount = words.length;

        int backSpace = 4;
        words[backSpace] = words[backSpace];

        String[] beforeColor = Arrays.copyOfRange(words,0,backSpace);
        String[] afterColor = Arrays.copyOfRange(words, backSpace +1, wordCount);

        String output = String.join(" ",beforeColor)+ " " + "<font color=" + color + ">" + words[backSpace] + "</font>"+ "<br> " +String.join(" ",afterColor);
        return Html.fromHtml(output);
    }
}
