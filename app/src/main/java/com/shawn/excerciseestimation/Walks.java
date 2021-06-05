package com.shawn.excerciseestimation;

import com.google.gson.annotations.SerializedName;

public class Walks {

    @SerializedName("WalkID")
    private String WalkID;


    @SerializedName("FirstName")
    private String FirstName;

    @SerializedName("Length")
    private String Length;

    @SerializedName("Date")
    private String Date;

    public String getWalkID() {
        return WalkID;
    }

    public String getFirstName() {
        return FirstName;
    }

    public String getLength() {
        return Length;
    }

    public String getDate() {
        return Date;
    }
}

