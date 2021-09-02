package com.shawn.excerciseestimation;

import android.content.Context;
import android.graphics.Point;
import android.os.Environment;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;

public class StorageManager{
    ArrayList<Point[]> PointsArray;
    private int[] Result;
    private ArrayList<int[]> ResultArray = new ArrayList<>();
    String FileName = "yourFileName";
    String textToWrite = " ";
    FileOutputStream outputStream;

    public StorageManager() {


    }

    public StorageManager(ArrayList<Point[]> pointsArray,String fileName,Context ctx) {
        PointsArray = pointsArray;
        FileName  = fileName;

    }

    public Hashtable<Integer,  int[]> getHashTable(String ExerciseType,Context ctx){
        Hashtable<Integer,  int[]> Result = ReadtoHash(ExerciseType,ctx);
        return Result;
    }
    public void writeToFile() {

        int FrameIndex=0;
        for (Point[] Frame : PointsArray) {
            if (Frame==null)
                continue;
            textToWrite  = textToWrite;

            for(Point pt : Frame)
            {
                if (pt==null)
                    textToWrite = textToWrite + 0 +",";
                else
                    textToWrite = textToWrite + pt.y +",";
            }
            textToWrite = textToWrite + "\n ";
            FrameIndex++;
        }
        Log.e("Error",textToWrite);

    }

    private Hashtable<Integer,  int[]> ReadtoHash(String ExerciseType,Context ctx){
        Hashtable<Integer,  int[]> ht1 = new Hashtable<>();
        int[] intarray2 = new int[18];
        InputStreamReader inputreader;
        InputStream inputStream;

        switch (ExerciseType) {
            case "Squats":
                inputStream = ctx.getResources().openRawResource(R.raw.squatresult);
                break;
            case "HighKnee":
                inputStream = ctx.getResources().openRawResource(R.raw.hkresult);
                break;
            case "JumpingJack":
                inputStream = ctx.getResources().openRawResource(R.raw.jjresult);
                break;
            default:
                inputStream = ctx.getResources().openRawResource(R.raw.squatresult);
        }

        if (inputStream != null) {
            inputreader= new InputStreamReader(inputStream);

            try {
                BufferedReader br = new BufferedReader(inputreader);
                String line;
                int frame = 0;
                int i = 0;
                while ((line = br.readLine()) != null) {
                    int[] intarray = new int[19];
                    i =0;
                    StringTokenizer multiTokenizer = new StringTokenizer(line, ",");

                    while (multiTokenizer.hasMoreTokens())
                    {
                        int value = Integer.parseInt(multiTokenizer.nextToken());
                        intarray[i] = value;
                   //     System.out.println(value);
                        i++;
                    }

                    ht1.put(frame,intarray);
                    frame++;
                }
                br.close();
            }
            catch (IOException e) {
                //You'll need to add proper error handling here
            }

        }




        return ht1;
    }


}
