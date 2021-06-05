package com.shawn.excerciseestimation;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ExerciseList {


        @POST("/getExercises")
        Call<List<Exercise>> loadExercise(@Body HashMap<String,String> map);

        @POST("/getWalks")
        Call<List<Walks>> loadWalks(@Body HashMap<String,String> map);
    }
