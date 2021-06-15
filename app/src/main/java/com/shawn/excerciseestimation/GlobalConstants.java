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

    public static final String FIRST_NAME = "Tarik";
    public static final String LAST_NAME = "Hodzic";
    /** Request camera and external storage permission.   */
    public static final int REQUEST_CAMERA_PERMISSION = 1;

    /** Model input shape for images.   */
    public static final int MODEL_WIDTH = 257;
    public static final int MODEL_HEIGHT = 257;
}

