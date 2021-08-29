package com.shawn.excerciseestimation.Retrofit;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitInterface {

    @POST("/login")
    Call<LoginResult> executeLogin(@Body HashMap<String,String> map);

    @POST("/Exercise")
    Call<LoginResult> executeExercise(@Body HashMap<String,String> map);

    @POST("/signup")
    Call<Void> executeSignup(@Body HashMap<String,String> map);

    @POST("/saveResult")
    Call<Void> executeSaveResult(@Body HashMap<String,String> map);

    @POST("/getExercises")
    Call<List<Exercise>> loadExercise(@Body HashMap<String,String> map);

    @POST("/getResults")
    Call<List<ExerciseResult>> loadExerciseResults(@Body HashMap<String,String> map);

    @POST("/getWalks")
    Call<List<Walks>> loadWalks(@Body HashMap<String,String> map);

}
