package com.shawn.excerciseestimation;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.os.Debug;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

public final class DTW {
    private StorageManager storageManager;


    public DTW() {

    }


    public static double[] ConvertToHashedFrames(Hashtable<Integer,  int[]> HashResult, ArrayList<Point[]> PointsArray, String exercisetype)
    {
        int keys = HashResult.size();
        Hashtable<Integer,  int[]> StorageHashTable = new Hashtable<>();
        Hashtable<Integer,  int[]> VideoHashTable = new Hashtable<>();
        for (int i = 0;i < 18; i++) {
            int[] tempintarray = new int[HashResult.size()];
            int[] tempintarray2 = new int[PointsArray.size()];
            int index = 0;
            for (int k = 0;k < keys; k++) {
            int[] temparray = HashResult.get(k);
            tempintarray[index] =temparray[i];
            index++;
            }
            StorageHashTable.put(i,tempintarray);

            int index2 = 0;
            for (Point[] points : PointsArray)
            {
                if(points[i] == null)
                    tempintarray2[index2] = 0;
                else
                    tempintarray2[index2] = points[i].y;

                index2++;
            }
            VideoHashTable.put(i,tempintarray2);
        }


        double[] result = dtw(StorageHashTable,VideoHashTable,10);
        System.out.println(result);
        return result;
    }

    public static double[] dtw(Hashtable<Integer,  int[]> h1, Hashtable<Integer,  int[]> h2, int radius) {

        double[] result = new double[19];
        Set<Integer> keys = h1.keySet();
       int[] intArray = new int[]{ 1,2,3,4,5,6,7,8,9,10 };
        for (int f = 0; f < 18; f++) {
            int[] x1 = h1.get(f);
         //   int[] x2 = new int[]{ 1,1};
            int[] x2 = h2.get(f);
        //    int[] x1 = new int[]{ 1,1,1,1,1,1,1,1,1,1,1,1,11,11,43,33,11,43,54,4,5,6,7,8,9,10,1,6,7,8,9,10,10,13,12,11,43,33,11,43,54,4,5,6,7,8,9,10,10,13,12,11,43,33};


            int n1 = x1.length;
            int n2 = x2.length;
            double[][] table = new double[2][n2 + 1];

            table[0][0] = 0;

            for (int p = 1; p <= n2; p++) {
                table[0][p] = Double.POSITIVE_INFINITY;
            }

            for (int i = 1; i <= n1; i++) {
                int start = Math.max(1, i - radius);
                int end = Math.min(n2, i + radius);

                table[1][start - 1] = Double.POSITIVE_INFINITY;
                if (end < n2) table[1][end + 1] = Double.POSITIVE_INFINITY;

                for (int j = start; j <= end; j++) {
                    double cost = Math.abs(x1[i - 1] - x2[j - 1]);

                    double min = table[0][j - 1];

                    if (min > table[0][j]) {
                        min = table[0][j];
                    }

                    if (min > table[1][j - 1]) {
                        min = table[1][j - 1];
                    }

                    table[1][j] = cost + min;
                }

                double[] swap = table[0];
                table[0] = table[1];
                table[1] = swap;
            }
            result[f] = table[0][n2];
        }
        return result;
    }
}
