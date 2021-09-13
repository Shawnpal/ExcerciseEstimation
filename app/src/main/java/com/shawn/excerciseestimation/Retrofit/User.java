package com.shawn.excerciseestimation.Retrofit;

import com.google.gson.annotations.SerializedName;
public class User {

    @SerializedName("Email")
    private String Email;

    @SerializedName("FirstName")
    private String Firstname;

    @SerializedName("LastName")
    private String Lastname;



    @SerializedName("Sex")
    private String Sex;


    @SerializedName("Age")
    private String Age;

    @SerializedName("Weight")
    private String Weight;

    public String getFirstname() {
        return Firstname;
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
