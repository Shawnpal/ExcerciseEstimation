package com.shawn.excerciseestimation.Retrofit;

import com.google.gson.annotations.SerializedName;
public class Person {

    @SerializedName("UserID")
    private String UserID;

    @SerializedName("FirstName")
    private String Firstname;

    @SerializedName("LastName")
    private String Lastname;

    @SerializedName("Email")
    private String Email;

    @SerializedName("Sex")
    private String Sex;


    @SerializedName("Age")
    private String Age;

    @SerializedName("Weight")
    private String Weight;

    public String getFirstname() {
        return Firstname;
    }

    public String getUserID() {
        return UserID;
    }

    public String getLastname() {
        return Lastname;
    }

    public String getEmail() {
        return Email;
    }

    public String getSex() {
        return Sex;
    }

    public String getAge() {
        return Age;
    }
}
