package com.shawn.excerciseestimation;

import android.os.Handler;
import android.util.Log;

import com.shawn.excerciseestimation.Retrofit.ExerciseResult;
import com.shawn.excerciseestimation.Retrofit.User;
import com.shawn.excerciseestimation.Retrofit.RestClient;
import com.shawn.excerciseestimation.Retrofit.RetrofitInterface;
import com.shawn.excerciseestimation.Retrofit.Steps;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.junit.Assert.assertNotEquals;

public class RetrofitInterfaceTests extends TestCase {

    public void testGrabingSingle() {

        RetrofitInterface ExerciseAPI = RestClient.getClient();
        HashMap<String, String> map = new HashMap<>();
        map.put("email", "Shawnchen1915@Gmail.com");
        Call<User> call = ExerciseAPI.executeLogin(map);
        call.enqueue(new Callback<User>(){
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.code()==200)
                {
                    User result = response.body();
                    String output = (result.getFirstname()).replace(" ", ""); //Results returns a lot of spaces in the name we need to trim
                    assertEquals("Shawn", output);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("error",t.toString());
            }


        });
    }



    public void testGrabingMultiple() {

        RetrofitInterface ExerciseAPI = RestClient.getClient();
        HashMap<String, String> map = new HashMap<>();
        map.put("FirstName", "Shawn");
         List<Steps> Wlist = new ArrayList<Steps>();
        Call<List<Steps>> loadBarCall = ExerciseAPI.loadSteps(map);
        loadBarCall.enqueue(new Callback<List<Steps>>() {


            @Override
            public void onResponse(Call<List<Steps>> call, Response<List<Steps>> response) {

                for (Steps size : response.body()) {
                    Wlist.add(size);

                }
                assertEquals(3,Wlist.size(),0);
            }

            @Override
            public void onFailure(Call<List<Steps>> call, Throwable t) {

            }


        });
    }



    public void testSavePerson() {

        RetrofitInterface retrofitInterface = RestClient.getClient();
        HashMap<String, String> map = new HashMap<>();
        map.put("UserID", "U10002");
        map.put("FirstName", "Shawn");
        map.put("LastName", "Chen");
        map.put("Email", "shawn@test.com");
        map.put("Sex", "Male");
        map.put("Age", "28");

        Call<Void> call = retrofitInterface.executeSignup(map);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                assertEquals(201,response.code(),0);
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
            }

        });
    }



    public void testSaveWalk() {

        RetrofitInterface retrofitInterface = RestClient.getClient();
        double[] Result = {12,12,13,34,23,24,35,35,22,43,12,42,35,56,223,24,12,12,24,34,14,64,34};
        ExerciseResult exerciseResult = new ExerciseResult(Result,"Squat","ShawnChen1915@gmail.com");
        HashMap<String, String> map = new HashMap<>();
        map.put("Result", exerciseResult.getResult()) ;
        map.put("ExerciseID", exerciseResult.getExerciseID()) ;
        map.put("ResultUID",exerciseResult.getResultUID());
        map.put("PersonEmail",exerciseResult.getPersonEmail());
        map.put("Date",exerciseResult.getDate());

        Call<Void> call = retrofitInterface.executeSignup(map);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                assertEquals(201,response.code(),0);
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
            }

        });
    }

    public void testSaveMultiple() {

        RetrofitInterface ExerciseAPI = RestClient.getClient();
        HashMap<String, String> map = new HashMap<>();
        map.put("PersonUI", "U10003");
        List<Steps> Wlist = new ArrayList<Steps>();
        Call<List<Steps>> loadBarCall = ExerciseAPI.loadSteps(map);
        loadBarCall.enqueue(new Callback<List<Steps>>() {


            @Override
            public void onResponse(Call<List<Steps>> call, Response<List<Steps>> response) {

                for (Steps size : response.body()) {
                    Wlist.add(size);

                }

            }

            @Override
            public void onFailure(Call<List<Steps>> call, Throwable t) {

            }


        });
    }


    public void testGrabingSingleandDelay() {

        RetrofitInterface ExerciseAPI = RestClient.getClient();
        HashMap<String, String> map = new HashMap<>();
        map.put("email", "Shawnchen1915@Gmail.com");
        Call<User> call = ExerciseAPI.executeLogin(map);
        final String[] output = new String[1];
        call.enqueue(new Callback<User>(){
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.code()==200)
                {
                    User result = response.body();
                    output[0] = (result.getFirstname()).replace(" ", ""); //Results returns a lot of spaces in the name we need to trim

                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("error",t.toString());
            }


        });
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            assertEquals("Shawn", output[0]);
            }, 4000);

    }
}