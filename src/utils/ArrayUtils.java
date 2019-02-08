package utils;

import java.util.Locale;

public class ArrayUtils {

    public static double[][] copy(double[][] array) {
        int length = array.length;
        double[][] clone = new double[length][];
        for (int i = 0; i < length; i++) {
            clone[i] = array[i].clone();
        }
        return clone;
    }

    public static double[] copy(int n, double[] array) {
        double[] copy = new double[n];
        System.arraycopy(array, 0, copy, 0, n);
        return copy;
    }

    public static double[][] copy(int n, int m, double[][] matrix) {
        double[][] copy = new double[n][m];
        for (int i = 0; i < n; i++) {
            copy[i] = copy(m, matrix[i]);
        }
        return copy;
    }

    public static void print(double[] array) {
        for (double s : array) {
            System.out.printf(Locale.ENGLISH, "%7.3f ", s);
        }
    }

    public static void print(double[][] matrix) {
        for (double[] array : matrix) {
            print(array);
            System.out.println();
        }
        System.out.println();
    }

}
