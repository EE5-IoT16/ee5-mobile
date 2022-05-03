package com.ee5.mobile.SupportClasses;

public class Fall {
    String date;
    String time;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Fall(String date, String time) {
        this.date = date;
        this.time = time;
    }
}
