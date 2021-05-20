package com.shawn.excerciseestimation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class LoginActivity extends AppCompatActivity {

    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = "http://10.0.2.2:8090";
    private EditText nametext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)
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



    public void Onlogin(View view)
    {
        String fName = nametext.getText().toString();
        HashMap<String, String> map = new HashMap<>();
        map.put("FirstName", fName) ;

        Call<LoginResult> call = retrofitInterface.executeLogin(map);
        call.enqueue(new Callback<LoginResult>(){
            @Override
            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                if(response.code()==200)
                {

                    LoginResult result = response.body();
              //      Log.i("Success",result.toString());
                    String resultTrimed = (result.getFirstname()).replace(" ", ""); //Results returns a lot of spaces in the name we need to trim
                    Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
                    intent.putExtra("USERNAME", resultTrimed );
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<LoginResult> call, Throwable t) {
                Toast.makeText(LoginActivity.this, t.getMessage(),Toast.LENGTH_LONG);

            }
        });

    }
}