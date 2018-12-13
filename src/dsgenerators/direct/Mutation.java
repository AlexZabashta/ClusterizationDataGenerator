package dsgenerators.direct;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.uma.jmetal.operator.MutationOperator;

import features_inversion.classification.dataset.BinDataset;
import features_inversion.classification.dataset.RelationsGenerator;
import features_inversion.classification.fun.AttributeFunction;
import features_inversion.classification.fun.RandomFunction;
import features_inversion.util.BooleanArray;
import features_inversion.util.FeaturePoint;

public class Mutation implements MutationOperator<BinDataSetSolution> {
    static int randomInt(Random random, int x, int y) {
        return Math.min(x, y) + random.nextInt(Math.abs(x - y) + 1);
    }

    static int randomLocalInt(Random random, int mean) {
        int std = (mean + 1) / 2;
        int val = (int) Math.round((random.nextGaussian() * std + mean));

        if (val < mean - std) {
            val = mean - std;
        }

        if (val == mean) {
            if (random.nextBoolean()) {
                ++val;
            } else {
                --val;
            }
        }

        if (val < 1) {
            if (mean == 1) {
                return 2;
            } else {
                return 1;
            }
        } else {
            return val;
        }
    }

    static double[][] select(double[][] values, int n, int m, boolean[] mask) {
        int len = values.length;
        double[][] result = new double[len][m];

        for (int i = 0; i < len; i++) {
            for (int j = 0, k = 0; j < n; j++) {
                if (mask[j]) {
                    result[i][k++] = values[i][j];
                }
            }
        }

        return result;
    }

    public static void apply(AttributeFunction fun, double[][] values, int index, boolean clazz) {
        for (double[] array : values) {
            array[index] = fun.evaluate(array, clazz);
        }
    }

    @Override
    public BinDataSetSolution execute(BinDataSetSolution source) {
        Random random = new Random();
        BinDataset dataset = source.getDataset();

        double[][] posA = dataset.pos, negA = dataset.neg, posB, negB;
        int attrA = dataset.numAttr, attrB;

        if (random.nextBoolean()) {
            attrB = attrA;
            posB = RelationsGenerator.fit(posA, randomLocalInt(random, posA.length), attrB, random);
            negB = RelationsGenerator.fit(negA, randomLocalInt(random, negA.length), attrB, random);
        } else {
            attrB = randomLocalInt(random, attrA);

            if (attrA < attrB) { // ADD
                posB = new double[posA.length][attrB];
                negB = new double[negA.length][attrB];

                int d = random.nextInt(6);

                for (int i = attrA; i < attrB; i++) {
                    AttributeFunction fun = RandomFunction.generate(random, i, d);
                    apply(fun, posB, i, true);
                    apply(fun, negB, i, false);
                }

            } else { // REMOVE
                boolean[] mask = BooleanArray.random(attrA, attrB, random);
                posB = select(posA, attrA, attrB, mask);
                negB = select(negA, attrA, attrB, mask);
            }

        }

        return new BinDataSetSolution(new BinDataset(posB, negB, attrB));
    }

}
