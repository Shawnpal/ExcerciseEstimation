package com.shawn.excerciseestimation;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainMenuActivity  extends AppCompatActivity {
    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    String  Email;
    private TextView FirstNameText;
    private TextView LastNameText;
    private TextView AgeText;
    private TextView SexText;
    private TextView EmailText;


    LoginResult result;
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
        map.put("Email", Email) ;
        Call<LoginResult> call = retrofitInterface.executeLogin(map);
        call.enqueue(new Callback<LoginResult>(){
            @Override
            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
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
            public void onFailure(Call<LoginResult> call, Throwable t) {
                Toast.makeText(MainMenuActivity.this, t.getMessage(),Toast.LENGTH_LONG);

            }
        });


    }
}
