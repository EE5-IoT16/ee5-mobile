package com.ee5.mobile.Activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ee5.mobile.R;

public class DataCardDetail extends AppCompatActivity {
    private TextView dataCardDetailTitle;
    private TextView dataCardDetailRecord;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview_detail);

        dataCardDetailTitle = findViewById(R.id.rv_detail_cardTitle);
        dataCardDetailRecord = findViewById(R.id.rv_detail_record_Data);

        DataCard dataCard = getIntent().getExtras().getParcelable("dataCard");

        dataCardDetailTitle.setText(dataCard.getDataCardTitle());
        dataCardDetailRecord.setText(dataCard.getDataCardRecord());

    }
}
