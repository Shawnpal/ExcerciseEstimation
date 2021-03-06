package com.shawn.excerciseestimation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.shawn.excerciseestimation.Retrofit.User;
import com.shawn.excerciseestimation.Retrofit.RetrofitInterface;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainMenuActivity  extends AppCompatActivity {
    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    String  Email;
    private TextView FirstNameText;
    private TextView LastNameText;
    private TextView AgeText;
    private TextView SexText;
    private TextView EmailText;


    User result;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);


        retrofit = new Retrofit.Builder()
                .baseUrl(((GlobalConstants) this.getApplication()).getBASE_URL())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);
        Intent intent = getIntent();
        Email = intent.getStringExtra("Email");

        FirstNameText = (TextView)findViewById(R.id.txtFirstname);
        LastNameText = (TextView)findViewById(R.id.txtLastname);
        AgeText = (TextView)findViewById(R.id.txtAge);
        SexText = (TextView)findViewById(R.id.txtSex);
        EmailText = (TextView)findViewById(R.id.txtEmail);



        HashMap<String, String> map = new HashMap<>();
        map.put("email", Email) ;
        Call<User> call = retrofitInterface.executeLogin(map);
        call.enqueue(new Callback<User>(){
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.code()==200)
                {

                    result = response.body();
                    FirstNameText.setText((result.getFirstname()).replace(" ", "")); //Results returns a lot of spaces in the name we need to trim
                    LastNameText.setText((result.getLastname()).replace(" ", ""));
                    AgeText.setText((result.getAge()).replace(" ", ""));
                    EmailText.setText((result.getEmail()).replace(" ", ""));
                    SexText.setText((result.getSex()).replace(" ", ""));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(MainMenuActivity.this, t.getMessage(),Toast.LENGTH_LONG);

            }
        });


    }

    public void OnOpenExercise(View view)
    {
        Intent intent = new Intent(getApplicationContext(), VideoActivity.class);
        intent.putExtra("Email", Email );
        startActivity(intent);
    }

    public void OnOpenMonitor(View view)
    {
        Intent intent = new Intent(getApplicationContext(), EMonitorActivity.class);
        intent.putExtra("Email", Email );
        startActivity(intent);
    }


}
