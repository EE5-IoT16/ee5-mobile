package com.ee5.mobile.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ee5.mobile.R;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OverviewActivity extends AppCompatActivity implements RecyclerViewAdapter.OnItemClickListener {

    RecyclerViewAdapter myRecyclerViewAdapter;
    RecyclerView myRecyclerView;
    ArrayList<DataCard> dataCards = new ArrayList<>();
    ArrayList barEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        ConstraintLayout constraintLayout = findViewById(R.id.overview_layout);

        Button viewProfile_btn = findViewById(R.id.viewProfile_Btn);
        TextView stepsLeft_num = findViewById(R.id.stepsLeft_num);
        TextView quote = findViewById(R.id.quote_tv);

        //TODO: get number from User object
        stepsLeft_num.setText("100");

        String quoteString = "Look in the mirror, that’s the only competition.";

        //quoteString = getColoredText();

        //quote = get from http request to some api
        quote.setText(getColoredText(quoteString, "#4FA4FF"));


        parseJson();
        myRecyclerView = findViewById(R.id.recyclerView);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        myRecyclerViewAdapter = new RecyclerViewAdapter(this, dataCards);
        myRecyclerViewAdapter.setOnItemClickListener(this);
        myRecyclerView.setAdapter(myRecyclerViewAdapter);
    }


    public Spanned getColoredText(String text, String color) {
        String[] words = text.split(" ");
        int wordCount = words.length;

        int backSpace = 4;
        words[backSpace] = words[backSpace];

        String[] beforeColor = Arrays.copyOfRange(words, 0, backSpace);
        String[] afterColor = Arrays.copyOfRange(words, backSpace + 1, wordCount);

        String output = String.join(" ", beforeColor) + " " + "<font color=" + color + ">" + words[backSpace] + "</font>" + "<br> " + String.join(" ", afterColor);
        return Html.fromHtml(output);
    }

    public void parseJson() {       //here we retrieve all information about a datacard object

        String cardTitle = "Steps";
        String cardRecord = "4,383";

        getEntries();
        BarDataSet barDataSet = new BarDataSet(barEntries, "");
        BarData barData = new BarData(barDataSet);

        DataCard dataCard1 = new DataCard(cardTitle, cardRecord, barData);
        DataCard dataCard2 = new DataCard(cardTitle, cardRecord, barData);
        DataCard dataCard3 = new DataCard(cardTitle, cardRecord, barData);

        dataCards.add(dataCard1);
        dataCards.add(dataCard2);
        dataCards.add(dataCard3);
    }

    private void getEntries() {
        barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(2f, 0));
        barEntries.add(new BarEntry(4f, 1));
        barEntries.add(new BarEntry(6f, 1));
        barEntries.add(new BarEntry(8f, 3));
        barEntries.add(new BarEntry(7f, 4));
        barEntries.add(new BarEntry(3f, 3));
    }

    @Override
    public void onItemClick(int position) {
        Intent detailIntent = new Intent(this, DataCardDetail.class);
        DataCard clickedItem = dataCards.get(position);
        detailIntent.putExtra("dataCard", clickedItem);
        startActivity(detailIntent);
    }
}
