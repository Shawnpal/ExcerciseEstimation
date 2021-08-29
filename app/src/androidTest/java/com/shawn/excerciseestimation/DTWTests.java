package com.shawn.excerciseestimation;

import junit.framework.TestCase;

import static org.junit.Assert.assertNotEquals;

public class DTWTests extends TestCase {

    public void testConvertToHashedFrames() {

        int[] storeinput = {52,52,52,52,52,52,63,63,63,63,73,83,83,94,94,104,104,115,125,125,136,146,146,
                157,167,177,177,188,188,198,198,209,209,209,209,209,219,219,219,219,219,219};
        int[] videoinput = {63,73,73,73,73,73,83,83,94,94,104,115,115,125,125,136,146,146,157,157,167,167,
                177,177,177,188,188,188,198,188,188,188,188,188,188,188,188,188,188,188,188,177,177,177,167,167,167,157,157,146};


    }

    public void testSecondDtws(){
        int[] x1 = {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
        int[] x2 = {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
        double d =  Dtw2(x1,x2);
        double delta = 0;
        double expected = 0;
        assertEquals(expected,d,delta);
    }


    public void testFirstDtws(){
        int[] x1 = {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
        int[] x2 = {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
        double d =  Dtw1(x1,x2);
        double delta = 0;
        double expected = 0;
        assertEquals(expected,d,delta);

    }

    public void testSecondDtws2(){
        int[] x1 = {1,1,1,1,1,1,1,1,1,1,1,1,1,2,3,4,3,2,1,1,1,1,1,1,1,1,1,1,1,1,1};
        int[] x2 = {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
        double d =  Dtw2(x1,x2);
        double delta = 0;
        double expected = 9;
        assertEquals(expected,d,delta);
    }
    public void testFirstDtws2(){
        int[] x1 = {1,1,1,1,1,1,1,1,1,1,1,1,1,2,3,4,3,2,1,1,1,1,1,1,1,1,1,1,1,1,1};
        int[] x2 = {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
        double d =  Dtw1(x1,x2);
        double delta = 0;
        double expected = 9;
        assertEquals(expected,d,delta);

    }
    public void testSecondDtws3(){
        int[] x1 = {1,1,1,1,1,1,1,1,1,1,1,1,1,6,7,8,10,8,7,6,1,1,1,1,1,1,1,1,1,1,1};
        int[] x2 = {1};
        double d =  Dtw2(x1,x2);
        double delta = 0;
        double expected = 45;
        assertEquals(expected,d,delta);
    }


    public void testSecondDtws4(){
        int[] x1 = {1,1,1,1,3};
        int[] x2 = {1,1,1,1,1};
        double d =  Dtw2(x1,x2);
        double delta = 0;
        double expected = 2;
        assertEquals(expected,d,delta);
    }
    public void testFirstDtws4(){
        int[] x1 = {1,1,1,1,3};
        int[] x2 = {1,1,1,1,1};
        double d =  Dtw1(x1,x2);
        double delta = 0;
        double expected = 2;
        assertEquals(expected,d,delta);
    }
    public void testSecondDtws5(){
        int[] x1 = {2,2,2,2,2};
        int[] x2 = {1,1,1,1,1};
        double d =  Dtw2(x1,x2);
        double delta = 0;
        double expected = 5;
        assertEquals(expected,d,delta);
    }
    public void testFirstDtws5(){
        int[] x1 = {2,2,2,2,2};
        int[] x2 = {1,1,1,1,1};
        double d =  Dtw1(x1,x2);
        double delta = 0;
        double expected = 5;
        assertEquals(expected,d,delta);
    }


    public void testSecondDtws6(){
        int[] x1 = {0};
        int[] x2 = {1};
        double d =  Dtw2(x1,x2);
        double delta = 0;
        double expected = 1;
        assertEquals(expected,d,delta);
    }
    public void testFirstDtws6(){
        int[] x1 = {0};
        int[] x2 = {1};
        double d =  Dtw1(x1,x2);
        double delta = 0;
        double expected = 1;
        assertEquals(expected,d,delta);
    }

















    public double Dtw1(int[] x1,int[] x2)  {


        int n1 = x1.length;
        int n2 = x2.length;
        double[][] DTW = new double[n1][n2];

        for (int i = 0; i < n1; i++) {
            for (int j = 0; j < n2; j++) {
                DTW[i][j] = Double.POSITIVE_INFINITY;
            }
        }

        DTW[0][0] = 0;
        for (int i = 1; i < n1; i++) {
            DTW[i][0]=Math.abs(x1[i] - x2[0]) + DTW[i-1][0];
        }
        for (int j = 1; j < n2; j++) {
            DTW[0][j]=Math.abs(x1[0] - x2[j]) + DTW[0][j-1];
        }

        for (int i = 1; i < n1; i++) {
            for (int j = 1; j < n2; j++) {
                double cost = Math.abs(x1[i] - x2[j]);
                DTW[i][j] = cost + Math.min(Math.min(DTW[i-1][j],DTW[i-1][j-1]),DTW[i][j-1] );    // insertion
            }
        }

        double d = DTW[--n1][--n2];

        for(int s = Math.max(n1,n2) ; s > 0; s-- )
        {

            if (DTW[n1-1][n2] <= DTW[n1-1][n2-1] && DTW[n1-1][n2] <= DTW[n1][n2-1]) {
                n1--;
            } else if (DTW[n1-1][n2-1] <= DTW[n1][n2-1] && DTW[n1-1][n2-1] <= DTW[n1-1][n2]) {
                n1--;
                n2--;
            } else {
                n2--;
            }

            if (n1<0)
                n1=0;
            else if (n2<0)
                n2=0;
            d = d + DTW[n1][n2];
        }

        return d;

    }
    //another implementation of DTW
    public  double Dtw2(int[] x1,int[] x2) {

        int n1 = x1.length;
        int n2 = x2.length;
        double[][] table = new double[2][n2 + 1];

        table[0][0] = 0;

        for (int i = 1; i <= n2; i++) {
            table[0][i] = Double.POSITIVE_INFINITY;
        }

        for (int i = 1; i <= n1; i++) {
            table[1][0] = Double.POSITIVE_INFINITY;

            for (int j = 1; j <= n2; j++) {


                double cost = Math.abs(x1[i-1] - x2[j-1]);
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

        double d  = table[0][n2];
        return d;
    }
    // @return the distance.





}