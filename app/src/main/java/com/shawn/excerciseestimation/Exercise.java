package com.shawn.excerciseestimation;

import com.google.gson.annotations.SerializedName;

public class Exercise {

    @SerializedName("ExerciseID")
    private String ExerciseID;

    @SerializedName("ExerciseName")
    private String ExerciseName;

    @SerializedName("PersonFirstName")
    private String Firstname;

    @SerializedName("ExerciseType")
    private String ExerciseType;

    @SerializedName("ExerciseTime")
    private String ExerciseTime;

    @SerializedName("NumSets")
    private String NumSets;

    @SerializedName("NumReps")
    private String NumReps;

    @SerializedName("Date")
    private String Date;

    public String getExerciseID() {
        return ExerciseID;
    }

    public String getExerciseName() {
        return ExerciseName;
    }

    public String getFirstname() {
        return Firstname;
    }

    public String getExerciseType() {
        return ExerciseType;
    }

    public String getExerciseTime() {
        return ExerciseTime;
    }

    public String getNumSets() {
        return NumSets;
    }

    public String getNumReps() {
        return NumReps;
    }

    public String getDate() {
        return Date;
    }
}
