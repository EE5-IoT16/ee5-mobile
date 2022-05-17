package com.ee5.mobile.SupportClasses;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.LineData;

import java.util.ArrayList;

public class DataCard implements Parcelable {
    public String cardTitle;
    public String timeIndicator;
    public String cardRecord;
    public String recordText;
    BarData barDataSteps;
    BarData barDataHp;
    LineData lineDataHr;
    ArrayList<Integer> dataCardStepData;
    ArrayList<Integer> dataCardHpData;
    ArrayList<Integer> dataCardHrData;

    public DataCard(String cardTitle, String timeIndicator, String cardRecord, String recordText, BarData barDataSteps, BarData barDataHp, LineData lineDataHr, ArrayList<Integer> dataCardStepData, ArrayList<Integer> dataCardHpData, ArrayList<Integer> dataCardHrData){
        this.cardTitle = cardTitle;
        this.timeIndicator = timeIndicator;
        this.cardRecord = cardRecord;
        this.recordText = recordText;
        this.barDataSteps = barDataSteps;
        this.barDataHp = barDataHp;
        this.lineDataHr = lineDataHr;
        this.dataCardStepData = dataCardStepData;
        this.dataCardHpData = dataCardHpData;
        this.dataCardHrData = dataCardHrData;
    }

    public String getDataCardTitle(){return cardTitle;}

    public String getDataCardTimeIndicator(){return timeIndicator;}

    public String getDataCardRecord(){return cardRecord;}

    public String getDataCardRecordText(){return recordText;}

    public BarData getBarDataSteps(){return barDataSteps;}

    public BarData getBarDataHp(){return barDataHp;}

    public LineData getlineDataHr(){return lineDataHr;}

    public ArrayList<Integer> getDataCardStepData(){
        return dataCardStepData;}

    public ArrayList<Integer> getDataCardHpData() {
        return dataCardHpData;
    }

    public void setDataCardHpData(ArrayList<Integer> dataCardHpData) {
        this.dataCardHpData = dataCardHpData;
    }

    public ArrayList<Integer> getDataCardHrData() {
        return dataCardHrData;
    }

    public void setDataCardHrData(ArrayList<Integer> dataCardHrData) {
        this.dataCardHrData = dataCardHrData;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.cardTitle);
        dest.writeString(this.cardRecord);
        dest.writeList(this.dataCardStepData);
        dest.writeList(this.dataCardHpData);
        dest.writeList(this.dataCardHrData);
    }

    protected DataCard(Parcel in) {
        this.cardTitle = in.readString();
        this.cardRecord = in.readString();
        this.dataCardStepData = in.readArrayList(null);
        this.dataCardHpData = in.readArrayList(null);
        this.dataCardHrData = in.readArrayList(null);
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
