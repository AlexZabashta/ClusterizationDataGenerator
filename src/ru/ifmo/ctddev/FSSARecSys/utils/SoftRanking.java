package ru.ifmo.ctddev.FSSARecSys.utils;

/**
 * Created by sergey on 16.12.15.
 */

import java.util.Arrays;

public class SoftRanking {

    public static double distance(double[] a, double[] b) {
        double d = 0;
        int n = Math.min(a.length, b.length);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int x = Double.compare(a[i], a[j]);
                int y = Double.compare(b[i], b[j]);

                if (x * y < 0) {
                    d += 1;
                    continue;
                }

                if ((x * y == 0) && (x + y != 0)) {
                    d += 0.5;
                    continue;
                }

            }
        }
        return d;
    }

    public static double[] aggregate(double[][] u) {
        if (u.length == 0) {
            return new double[0];
        }

        int n = u[0].length;

        for (double[] a : u) {
            n = Math.min(n, a.length);
        }

        double[][] v = new double[n][n], l = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                for (double[] a : u) {
                    if (a[i] > a[j]) {
                        ++l[i][j];
                    }

                    if (a[i] < a[j]) {
                        ++v[i][j];
                    }

                }
            }
        }

        double[] weigh = new double[n];
        Arrays.fill(weigh, n);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    if (v[i][j] > l[i][j]) {
                        weigh[i] -= 1.0;
                    }
                    if (v[i][j] == l[i][j]) {
                        weigh[i] -= 0.5;
                    }
                }
            }
        }

        return weigh;
    }
}
