package features_inversion.classification.fus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import weka.core.Instances;

public class Fusion {

    public static void main(String[] args) {
        Random random = new Random();
        Fusion fusion = new Fusion(random);

        double[][][] a = new double[2][][];
        double[][][] b = new double[3][][];

        a[0] = new double[2][7];
        a[1] = new double[4][7];
        b[0] = new double[3][6];
        b[1] = new double[1][6];
        b[2] = new double[5][6];
        fill(a, random);
        fill(b, random);
        print(a);
        print(b);

        print(fusion.fuse(a, b));

    }

    public static void fill(double[][][] v, Random random) {
        for (double[][] cls : v) {
            for (double[] inst : cls) {
                for (int i = 0; i < inst.length; i++) {
                    inst[i] = random.nextInt(10);
                }
            }
        }
    }

    public static void print(double[][][] v) {
        for (double[][] cls : v) {
            for (double[] inst : cls) {
                for (double atr : inst) {
                    System.out.printf("%7.0f ", atr);
                }
                System.out.println();
            }
            System.out.println();
        }

        System.out.println(v.length);
    }

    final Random random;

    public Fusion(Random random) {
        this.random = random;
    }

    public boolean[] array(int n) {
        return array(n, random.nextInt(n) + 1);
        // return array(n, n);
    }

    public boolean[] array(int n, int m) {
        boolean[] array = new boolean[n];

        for (int i = 0; i < m; i++) {
            array[i] = true;
        }

        for (int i = m; i < n; i++) {
            int j = random.nextInt(i + 1);

            boolean temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }

        return array;
    }

    public double value(double min, double max) {
        return min + random.nextDouble() * (max - min);
    }

    public int value(int min, int max) {
        return min + random.nextInt(max - min + 1);
    }

    public double[][][] fuse(double[][][] x, double[][][] y) {
        double[][][][] values = new double[][][][] { x, y };

        int[] numAttributes = new int[2];
        int[] numClasses = new int[2];
        int[] numInstances = new int[2];

        for (int i = 0; i < 2; i++) {
            numClasses[i] = values[i].length;
            numAttributes[i] = -1;
            for (double[][] clazz : values[i]) {
                numInstances[i] += clazz.length;
                for (double[] instance : clazz) {
                    if (numAttributes[i] == -1) {
                        numAttributes[i] = instance.length;
                    }
                    if (numAttributes[i] != instance.length) {
                        return null;
                    }
                }
            }
        }

        boolean[][] useAttribute = new boolean[2][];

        for (int i = 0; i < 2; i++) {
            useAttribute[i] = array(numAttributes[i]);
        }

        double[][] max = new double[2][];
        double[][] min = new double[2][];

        for (int i = 0; i < 2; i++) {
            int n = numAttributes[i];
            max[i] = new double[n];
            Arrays.fill(max[i], Double.NEGATIVE_INFINITY);

            min[i] = new double[n];
            Arrays.fill(min[i], Double.POSITIVE_INFINITY);

            for (double[][] clazz : values[i]) {
                for (double[] instance : clazz) {
                    for (int j = 0; j < n; j++) {
                        max[i][j] = Math.max(max[i][j], instance[j]);
                        min[i][j] = Math.min(min[i][j], instance[j]);
                    }
                }
            }
        }

        int totalAttributes = 0;

        for (boolean[] ua : useAttribute) {
            for (boolean b : ua) {
                if (b) {
                    ++totalAttributes;
                }
            }
        }

        int totalClasses = value(Math.min(numClasses[0], numClasses[1]), Math.max(numClasses[0], numClasses[1]));

        List<double[]>[] val = new List[totalClasses];

        for (int c = 0; c < totalClasses; c++) {
            val[c] = new ArrayList<double[]>();
        }

        int[] offset = new int[] { random.nextInt(totalClasses), random.nextInt(totalClasses) };

        for (int i = 0; i < 2; i++) {
            int n = numClasses[i];

            for (int c = 0; c < n; c++) {
                int m = values[i][c].length;

                boolean[] useInstance = array(m);

                for (int j = 0; j < m; j++) {
                    if (useInstance[j]) {
                        double[] instance = new double[totalAttributes];
                        int p = 0;

                        for (int w = 0; w < 2; w++) {
                            for (int q = 0; q < numAttributes[w]; q++) {
                                if (useAttribute[w][q]) {
                                    if (i == w) {
                                        instance[p++] = values[w][c][j][q];
                                    } else {
                                        instance[p++] = value(min[w][q], max[w][q]);
                                    }
                                }
                            }
                        }

                        val[(c + offset[i]) % totalClasses].add(instance);
                    }
                }

            }

        }

        double[][][] z = new double[totalClasses][][];

        for (int c = 0; c < totalClasses; c++) {
            int n = val[c].size();
            z[c] = new double[n][];
            for (int i = 0; i < n; i++) {
                z[c][i] = val[c].get(i);
            }
        }

        return z;

    }
}
