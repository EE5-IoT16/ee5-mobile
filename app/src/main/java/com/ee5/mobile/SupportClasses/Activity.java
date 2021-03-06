package com.ee5.mobile.SupportClasses;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class Activity implements Comparable<Activity> {
    public Date date;
    public String duration;
    public String steps;
    public String calories;
    public String heartPoints;
    public String avgHeartrate;
    public String maxHeartRate;
    public String distance;

    public Activity(Date date, String duration, String steps, String calories, String heartPoints, String avgHeartrate, String maxHeartRate, String distance) {
        this.date = date;
        this.duration = duration;
        this.steps = steps;
        this.calories = calories;
        this.heartPoints = heartPoints;
        this.avgHeartrate = avgHeartrate;
        this.maxHeartRate = maxHeartRate;
        this.distance = distance;
    }

    public String getAvgHeartrate() {
        return avgHeartrate;
    }

    public void setAvgHeartrate(String avgHeartrate) {
        this.avgHeartrate = avgHeartrate;
    }

    public String getMaxHeartRate() {
        return maxHeartRate;
    }

    public void setMaxHeartRate(String maxHeartRate) {
        this.maxHeartRate = maxHeartRate;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public Date getDateTime() {
        return date;
    }

    public String getDuration() {
        return duration;
    }

    public String getSteps() {
        return steps;
    }

    public String getCalories() {
        return calories;
    }

    public String getHeartPoints() {
        return heartPoints;
    }


    @Override
    public int compareTo(Activity activity) {
        if (getDateTime() == null || activity.getDateTime() == null)
            return 0;
        return getDateTime().compareTo(activity.getDateTime());
    }
}
