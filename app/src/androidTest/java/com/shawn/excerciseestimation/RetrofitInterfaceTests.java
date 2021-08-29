package com.shawn.excerciseestimation;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.shawn.excerciseestimation.Retrofit.Exercise;
import com.shawn.excerciseestimation.Retrofit.LoginResult;
import com.shawn.excerciseestimation.Retrofit.RestClient;
import com.shawn.excerciseestimation.Retrofit.RetrofitInterface;
import com.shawn.excerciseestimation.Retrofit.Walks;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.junit.Assert.assertNotEquals;

public class RetrofitInterfaceTests extends TestCase {

    public void testGrabingSingle() {

        RetrofitInterface ExerciseAPI = RestClient.getClient();
        HashMap<String, String> map = new HashMap<>();
        map.put("email", "Shawnchen1915@Gmail.com");
        Call<LoginResult> call = ExerciseAPI.executeLogin(map);
        call.enqueue(new Callback<LoginResult>(){
            @Override
            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                if(response.code()==200)
                {
                    LoginResult result = response.body();
                    String output = (result.getFirstname()).replace(" ", ""); //Results returns a lot of spaces in the name we need to trim
                    assertEquals("Shawn", output);
                }
            }

            @Override
            public void onFailure(Call<LoginResult> call, Throwable t) {
                Log.e("error",t.toString());
            }


        });
    }



    public void testGrabingMultiple() {

        RetrofitInterface ExerciseAPI = RestClient.getClient();
        HashMap<String, String> map = new HashMap<>();
        map.put("FirstName", "Shawn");
         List<Walks> Wlist = new ArrayList<Walks>();
        Call<List<Walks>> loadBarCall = ExerciseAPI.loadWalks(map);
        loadBarCall.enqueue(new Callback<List<Walks>>() {


            @Override
            public void onResponse(Call<List<Walks>> call, Response<List<Walks>> response) {

                for (Walks size : response.body()) {
                    Wlist.add(size);

                }
                assertEquals(3,Wlist.size(),0);
            }

            @Override
            public void onFailure(Call<List<Walks>> call, Throwable t) {

            }


        });
    }



    public void testSaveSingle() {

        RetrofitInterface ExerciseAPI = RestClient.getClient();
        HashMap<String, String> map = new HashMap<>();
        map.put("FirstName", "Shawn");
        List<Walks> Wlist = new ArrayList<Walks>();
        Call<List<Walks>> loadBarCall = ExerciseAPI.loadWalks(map);
        loadBarCall.enqueue(new Callback<List<Walks>>() {


            @Override
            public void onResponse(Call<List<Walks>> call, Response<List<Walks>> response) {

                for (Walks size : response.body()) {
                    Wlist.add(size);

                }

            }

            @Override
            public void onFailure(Call<List<Walks>> call, Throwable t) {

            }


        });
    }

    public void testSaveMultiple() {

        RetrofitInterface ExerciseAPI = RestClient.getClient();
        HashMap<String, String> map = new HashMap<>();
        map.put("FirstName", "Shawn");
        List<Walks> Wlist = new ArrayList<Walks>();
        Call<List<Walks>> loadBarCall = ExerciseAPI.loadWalks(map);
        loadBarCall.enqueue(new Callback<List<Walks>>() {


            @Override
            public void onResponse(Call<List<Walks>> call, Response<List<Walks>> response) {

                for (Walks size : response.body()) {
                    Wlist.add(size);

                }

            }

            @Override
            public void onFailure(Call<List<Walks>> call, Throwable t) {

            }


        });
    }
}