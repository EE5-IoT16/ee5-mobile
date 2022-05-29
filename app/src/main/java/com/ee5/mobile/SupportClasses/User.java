package com.ee5.mobile.SupportClasses;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.ee5.mobile.Activities.OverviewActivity;
import com.ee5.mobile.Interfaces.ServerCallback;

import java.util.ArrayList;
import java.util.Arrays;

public class User implements Parcelable {

    /*************************************************************
     * DATA FIELDS
     *************************************************************/

    //*** profile data ***
    private int profileId;
    private String profileFirstName;
    private String profileSurname;
    private String profileEmail;

    //*** user data ***
    //user table
    private int userId;
    private String userFirstName;
    private String userSurname;
    private String userEmail;
    private int userPasscode;

    //physical data table
    private int weight;     //in kg
    private int height;     //in cm
    private int age;        //in years
    private String gender = "X";  //"male" or "female" or "X"

    //goals data table
    private int dailyStepGoal;
    private int dailyHeartpointGoal;

    /*************************************************************
     * CONSTRUCTORS
     *************************************************************/

    public User(int profileId, String profileFirstName, String profileSurname, String profileEmail) {
        this.profileId = profileId;
        this.profileFirstName = profileFirstName;
        this.profileSurname = profileSurname;
        this.profileEmail = profileEmail;

    }

    public User(int profileId, String profileFirstName, String profileSurname, String profileEmail,
                int userId, String userFirstName, String userSurname, String userEmail, int userPasscode) {
        this.profileId = profileId;
        this.profileFirstName = profileFirstName;
        this.profileSurname = profileSurname;
        this.profileEmail = profileEmail;
        this.userId = userId;
        this.userFirstName = userFirstName;
        this.userSurname = userSurname;
        this.userEmail = userEmail;
        this.userPasscode = userPasscode;

    }

    /*************************************************************
     * GETTERS AND SETTERS
     *************************************************************/

    //*** profile data ***

    public int getProfileId() {
        return profileId;
    }

    public String getProfileFirstName() {
        return profileFirstName;
    }

    public void setProfileFirstName(String profileFirstName) {
        this.profileFirstName = profileFirstName;
    }

    public String getProfileSurname() {
        return profileSurname;
    }

    public void setProfileSurname(String profileSurname) {
        this.profileSurname = profileSurname;
    }

    public String getProfileEmail() {
        return profileEmail;
    }

    public void setProfileEmail(String profileEmail) {
        this.profileEmail = profileEmail;
    }

    //*** user data ***
    //user table

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserSurname() {
        return userSurname;
    }

    public void setUserSurname(String userSurname) {
        this.userSurname = userSurname;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public int getUserPasscode() {
        return userPasscode;
    }

    public void setUserPasscode(int userPasscode) {
        this.userPasscode = userPasscode;
    }

    //physical data table

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getBMI(){
        return (int) Math.rint(weight / (height^2));
    }

    public int getRMR(){

        double RMR;

        try {
            if(gender.equals("Female")){
                RMR = 447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age);
            }
            else {
                RMR = 88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age);
            }

            return (int) Math.rint(RMR);
        } catch (Exception e) {
            return 0;
        }
    }

    //goals data table


    public int getDailyStepGoal() {
        return dailyStepGoal;
    }

    public void setDailyStepGoal(int dailyStepGoal) {
        this.dailyStepGoal = dailyStepGoal;
    }

    public int getDailyHeartpointGoal() {
        return dailyHeartpointGoal;
    }

    public void setDailyHeartpointGoal(int dailyHeartpointGoal) {
        this.dailyHeartpointGoal = dailyHeartpointGoal;
    }

    /*************************************************************
     * UPDATE DB INFORMATION METHODS
     *************************************************************/

    public void updatePhysicalData() {

        ArrayList<String> linkData = new ArrayList<String>();
        ArrayList<String> linkParameters = new ArrayList<String>(Arrays.asList("userId", "weight", "height",
                "age", "gender"));
        String node = "physicalData";

        linkData.add(String.valueOf(userId));
        linkData.add(String.valueOf(weight));
        linkData.add(String.valueOf(height));
        linkData.add(String.valueOf(age));
        linkData.add(gender);

        APIconnection.getInstance().PUTRequest(node, linkData, linkParameters, new ServerCallback() {
            @Override
            public void onSuccess() {
                Log.i("UPDATE", "PUT request was succesfully sent");
            }
        });

    }

    public void updateDailyGoals() {

        ArrayList<String> linkData = new ArrayList<String>();
        ArrayList<String> linkParameters = new ArrayList<String>(Arrays.asList("userId", "dailySteps", "dailyHeartP"));
        String node = "goals";

        linkData.add(String.valueOf(userId));
        linkData.add(String.valueOf(dailyStepGoal));
        linkData.add(String.valueOf(dailyHeartpointGoal));

        APIconnection.getInstance().PUTRequest(node, linkData, linkParameters, new ServerCallback() {
            @Override
            public void onSuccess() {
                Log.i("UPDATE", "PUT request was succesfully sent");
            }
        });

    }

    /*************************************************************
     * Parcelable interface method overrides
     *************************************************************/

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.profileId);
        out.writeString(this.profileFirstName);
        out.writeString(this.profileSurname);
        out.writeString(this.profileEmail);
        out.writeInt(this.userId);
        out.writeString(this.userFirstName);
        out.writeString(this.userSurname);
        out.writeString(this.userEmail);
        out.writeInt(this.weight);
        out.writeInt(this.height);
        out.writeInt(this.age);
        out.writeString(this.gender);
        out.writeInt(this.dailyStepGoal);
        out.writeInt(this.dailyHeartpointGoal);
        out.writeInt(this.userPasscode);
    }

    protected User(Parcel in) {
        this.profileId = in.readInt();
        this.profileFirstName = in.readString();
        this.profileSurname = in.readString();
        this.profileEmail = in.readString();
        this.userId = in.readInt();
        this.userFirstName = in.readString();
        this.userSurname = in.readString();
        this.userEmail = in.readString();
        this.weight = in.readInt();
        this.height = in.readInt();
        this.age = in.readInt();
        this.gender = in.readString();
        this.dailyStepGoal = in.readInt();
        this.dailyHeartpointGoal = in.readInt();
        this.userPasscode = in.readInt();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
