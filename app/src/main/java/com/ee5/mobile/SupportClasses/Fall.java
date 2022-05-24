package com.ee5.mobile.SupportClasses;

import java.util.Comparator;
import java.util.Date;

public class Fall implements Comparable<Fall>{
    private String date;
    private String time;
    private Date dateTime;


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Date getDateTime(){
        return dateTime;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Fall(Date dateTime, String date, String time) {
        this.dateTime = dateTime;
        this.date = date;
        this.time = time;
    }

    @Override
    public int compareTo(Fall fall) {
        if (getDateTime() == null || fall.getDateTime() == null)
            return 0;
        return getDateTime().compareTo(fall.getDateTime());
    }
}
