package com.shawn.excerciseestimation.Retrofit;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class ExerciseResult {

    @SerializedName("Result")
    String result;

    @SerializedName("ResultUID")
    String resultUID;

    @SerializedName("ExerciseID")
    String ExerciseID;

    @SerializedName("PersonEmail")
    String PersonEmail;

    @SerializedName("Date")
    String Date;

    public ExerciseResult(String ExerciseID, String personUID) {

        this.resultUID = setUuidResultID();
        this.ExerciseID = setExerciseID(ExerciseID);
        PersonEmail = "shawnchen1915@gmail.com";
        Date = setDate();
    }

    public ExerciseResult(double[] result,String ExerciseID, String personUID) {
        this.result = ConverttoString(result);
        this.resultUID = setUuidResultID();
        this.ExerciseID = setExerciseID(ExerciseID);
        PersonEmail = "shawnchen1915@gmail.com";
        Date = setDate();
    }

    public ExerciseResult(String result, String resultUID, String personUID, String date) {
        this.result = result;
        this.resultUID = resultUID;
        PersonEmail = personUID;
        Date = date;
    }

    public String getResult() {
        return result;
    }

    private void setResult(String result) {

        this.result = result;
    }

    private String ConverttoString(double[]  result) {

            String StringResult = "";

            for (double d : result)
            {
                StringResult =  StringResult + Double.toString(d) + " ";
            }

            return StringResult;

    }

    private String setExerciseID(String exerciseID) {
        switch (exerciseID) {
            case "Squats":
                return "ER10001";
            case "JumpingJack":
                return "ER10002";
            case "KneeElbow":
                return "ER10003";
            default:
                return "ER10004";
        }
    }

    public String getResultUID() {
        return resultUID;
    }

    private String setUuidResultID() {
        UUID uniResultID =  UUID.randomUUID();
        String uuidAsString = "E"+uniResultID.toString();
        return uuidAsString;
    }
    private String setDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        java.util.Date date = new Date();
        String dateString = formatter.format(date);
        return dateString;
    }
    public String getPersonEmail() {
        return PersonEmail;
    }

    private void setPersonUID(String personUID) {
        PersonEmail = personUID;
    }

    public String getDate() {
        return Date;
    }
    public String getExerciseID() {
        return ExerciseID;
    }

    private void setDate(String date) {
        Date = date;
    }
}
