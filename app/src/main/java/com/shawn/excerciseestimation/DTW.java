package com.shawn.excerciseestimation;

public final class DTW {

    public static double d(int[] x1, int[] x2, int radius) {
        int n1 = x1.length;
        int n2 = x2.length;
        double[][] table = new double[2][n2 + 1];

        table[0][0] = 0;

        for (int i = 1; i <= n2; i++) {
            table[0][i] = Double.POSITIVE_INFINITY;
        }

        for (int i = 1; i <= n1; i++) {
            int start = Math.max(1, i - radius);
            int end = Math.min(n2, i + radius);

            table[1][start-1] = Double.POSITIVE_INFINITY;
            if (end < n2) table[1][end+1] = Double.POSITIVE_INFINITY;

            for (int j = start; j <= end; j++) {
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

        return table[0][n2];
    }
}
