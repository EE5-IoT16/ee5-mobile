package com.ee5.mobile.SupportClasses;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.LineData;

import java.util.ArrayList;

public class DataCard implements Parcelable {
    public String cardTitle;
    public String timeIndicator;
    public String cardRecord;
    public String recordText;
    BarData barData;
    LineData lineData;
    ArrayList<Integer> dataCardData;

    public DataCard(String cardTitle, String timeIndicator,String cardRecord, String recordText, BarData barData, LineData lineData, ArrayList<Integer> dataCardData){
        this.cardTitle = cardTitle;
        this.timeIndicator = timeIndicator;
        this.cardRecord = cardRecord;
        this.recordText = recordText;
        this.barData = barData;
        this.lineData = lineData;
        this.dataCardData = dataCardData;
    }

    public String getDataCardTitle(){return cardTitle;}

    public String getDataCardTimeIndicator(){return timeIndicator;}

    public String getDataCardRecord(){return cardRecord;}

    public String getDataCardRecordText(){return recordText;}

    public BarData getBarData(){return barData;}

    public LineData getlineData(){return lineData;}

    public ArrayList<Integer> getDataCardData(){return dataCardData;}


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.cardTitle);
        dest.writeString(this.cardRecord);
        dest.writeList(this.dataCardData);
    }

    protected DataCard(Parcel in) {
        this.cardTitle = in.readString();
        this.cardRecord = in.readString();
        this.dataCardData = in.readArrayList(null);
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
