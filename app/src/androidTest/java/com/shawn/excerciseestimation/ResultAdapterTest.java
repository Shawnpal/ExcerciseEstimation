package com.shawn.excerciseestimation;

import android.widget.TextView;

import com.shawn.excerciseestimation.Retrofit.ExerciseResult;

import junit.framework.TestCase;

public class ResultAdapterTest extends TestCase {

    public void testGetView() {


        String result = "0.0 0.0 0.0 0.0 0.0";
        String[] companies = result.split("\\s+");
        int index =0;
        String expected = "0.0";
        String output = companies[0];
        assertEquals(expected,output,0);



    }
}