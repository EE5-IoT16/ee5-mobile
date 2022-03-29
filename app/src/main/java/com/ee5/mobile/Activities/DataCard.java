package com.ee5.mobile.Activities;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;

import java.util.ArrayList;

public class DataCard implements Parcelable {
    public String cardTitle;
    public String cardRecord;
    public BarChart barChart;
    BarData barData;
    BarDataSet barDataSet;
    ArrayList barEntries;

    public DataCard(String cardTitle, String cardRecord, BarData barData){
        this.cardTitle = cardTitle;
        this.cardRecord = cardRecord;
        this.barData = barData;
    }

    public String getDataCardTitle(){return cardTitle;}

    public String getDataCardRecord(){return cardRecord;}

    public BarData getBarData(){return barData;}

    public BarDataSet getBarDataSet(){return barDataSet;}

    public ArrayList<BarData> getBarEntries(){return barEntries;}



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.cardTitle);
        dest.writeString(this.cardRecord);
    }

    protected DataCard(Parcel in) {
        this.cardTitle = in.readString();
        this.cardRecord = in.readString();
    }

    public static final Parcelable.Creator<DataCard> CREATOR = new Parcelable.Creator<DataCard>() {
        @Override
        public DataCard createFromParcel(Parcel source) {
            return new DataCard(source);
        }

        @Override
        public DataCard[] newArray(int size) {
            return new DataCard[size];
        }
    };
}
