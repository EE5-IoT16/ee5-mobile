package com.ee5.mobile.SupportClasses;

import android.os.Parcel;
import android.os.Parcelable;

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

    //physical data table
    private int weight;     //in kg
    private int height;     //in cm
    private int age;        //in years
    private String gender;  //"male" or "female"

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
                 int userId, String userFirstName, String userSurname, String userEmail) {
        this.profileId = profileId;
        this.profileFirstName = profileFirstName;
        this.profileSurname = profileSurname;
        this.profileEmail = profileEmail;
        this.userId = userId;
        this.userFirstName = userFirstName;
        this.userSurname = userSurname;
        this.userEmail = userEmail;

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

    private int userPasscode;

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
        return weight / (height^2);
    }

    public int getRMR(){

        double RMR;

        if(gender.equals("Female")){
            RMR = 447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age);
        }
        else {
            RMR = 88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age);
        }

        return (int) Math.rint(RMR);
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
