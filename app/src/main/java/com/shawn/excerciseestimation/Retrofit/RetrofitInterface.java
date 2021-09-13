package com.shawn.excerciseestimation.Retrofit;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitInterface {

    @POST("/login")
    Call<User> executeLogin(@Body HashMap<String,String> map);

    @POST("/Exercise")
    Call<User> executeExercise(@Body HashMap<String,String> map);

    @POST("/signup")
    Call<Void> executeSignup(@Body HashMap<String,String> map);

    @POST("/saveResult")
    Call<Void> executeSaveResult(@Body HashMap<String,String> map);

    @POST("/getExercises")
    Call<List<Exercise>> loadExercise(@Body HashMap<String,String> map);

    @POST("/getResults")
    Call<List<ExerciseResult>> loadExerciseResults(@Body HashMap<String,String> map);

    @POST("/getSteps")
    Call<List<Steps>> loadSteps(@Body HashMap<String,String> map);

}
