package com.shawn.excerciseestimation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.shawn.excerciseestimation.Retrofit.User;
import com.shawn.excerciseestimation.Retrofit.RestClient;
import com.shawn.excerciseestimation.Retrofit.RetrofitInterface;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = "http://10.0.2.2:8090";
    private EditText nametext;
    private User result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        RetrofitInterface ExerciseAPI = RestClient.getClient();

        retrofit = new Retrofit.Builder()
                .baseUrl(((GlobalConstants) this.getApplication()).getBASE_URL())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);
        nametext = findViewById(R.id.TextPersonName);



        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA))
            {

            }
            else
            {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},1 );
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE))
            {

            }
            else
            {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1 );
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            {

            }
            else
            {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1 );
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED)
        {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET))
            {

            }
            else
            {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.INTERNET},1 );
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    public void OnOffline(View view) {
        Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
        startActivity(intent);
    }


        public void Onlogin(View view)
    {


        String Email = nametext.getText().toString();
        HashMap<String, String> map = new HashMap<>();
        map.put("email", Email) ;

        Call<User> call = retrofitInterface.executeLogin(map);
        call.enqueue(new Callback<User>(){
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.code()==200)
                {

                    User result = response.body();
                    String resultTrimed = (result.getEmail()).replace(" ", ""); //Results returns a lot of spaces in the name we need to trim
                    Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
                    intent.putExtra("Email", resultTrimed );
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(LoginActivity.this, t.getMessage(),Toast.LENGTH_LONG);

            }
        });

    }
}