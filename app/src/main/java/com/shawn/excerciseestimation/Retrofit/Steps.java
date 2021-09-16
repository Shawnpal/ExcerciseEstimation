package com.shawn.excerciseestimation.Retrofit;

import com.google.gson.annotations.SerializedName;

public class Steps {

    @SerializedName("StepID")
    private String StepID;


    @SerializedName("FirstName")
    private String FirstName;

    @SerializedName("Length")
    private String Length;

    @SerializedName("Date")
    private String Date;

    public String getStepID() {
        return StepID;
    }

    public String getFirstName() {
        return FirstName;
    }

    public Float getLength() {
        float f=Float.parseFloat(Length);
        return f;
    }

    public String getDate() {
        return Date;
    }
}

