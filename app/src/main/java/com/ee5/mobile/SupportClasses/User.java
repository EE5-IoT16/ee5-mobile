package com.ee5.mobile.SupportClasses;

public class User{

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
     * CONSTRUCTOR
     *************************************************************/

    public User(int profileId, String profileFirstName, String profileSurname, String profileEmail) {
        this.profileId = profileId;
        this.profileFirstName = profileFirstName;
        this.profileSurname = profileSurname;
        this.profileEmail = profileEmail;
    }

    /*************************************************************
     * GETTERS AND SETTERS (some setters need to be updated so that they send the new data to the DB)
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
}
