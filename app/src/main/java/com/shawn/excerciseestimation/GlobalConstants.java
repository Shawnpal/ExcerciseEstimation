package com.shawn.excerciseestimation;

import android.app.Application;

public class GlobalConstants extends Application {


    private String BASE_URL = "http://10.0.2.2:8090";

        public String getBASE_URL() {
            return BASE_URL;
        }

        public void setBASE_URL(String someVariable) {
            this.BASE_URL = someVariable;
        }
}

