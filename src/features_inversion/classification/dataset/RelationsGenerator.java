package features_inversion.classification.dataset;

import java.util.Arrays;
import java.util.Random;

import features_inversion.util.BooleanArray;
import misc.Permutation;

public class RelationsGenerator {
    public static double[][] genaddRelations(double[][] values, int size, int numAttr, Random random) {
        int n = values.length;

        double[][] extRel = new double[size][numAttr];

        for (int i = 0; i < size; i++) {
            int[] p = Permutation.random(numAttr, random);

            int j = 0;
            while (j < numAttr) {
                int inst = random.nextInt(n);
                int len = random.nextInt(numAttr - j) + 1;

                for (int k = 0; k < len; k++) {
                    int attribute = p[j + k];
                    extRel[i][attribute] = values[inst][attribute];
                }

                j += len;
            }

        }

        return extRel;
    }

    public static double[][] fit(double[][] values, int size, int numAttr, Random random) {

        int len = values.length;

        if (len == size) {
            return values;
        }

        if (len < size) {
            int diff = size - len;
            double[][] app = RelationsGenerator.genaddRelations(values, diff, numAttr, random);

            double[][] big = Arrays.copyOf(values, size);

            for (int i = 0, j = len; i < diff; i++) {
                big[j++] = app[i];
            }

            return big;

        } else {
            boolean[] b = BooleanArray.random(len, size, random);
            double[][] small = new double[size][];

            for (int i = 0, j = 0; i < len; i++) {
                if (b[i]) {
                    small[j++] = values[i];
                }
            }

            return small;
        }
    }

}
