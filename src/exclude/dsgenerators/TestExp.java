package exclude.dsgenerators;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;

import exclude.dsgenerators.direct.GenOverObj;
import exclude.dsgenerators.vect.GenOverDVect;
import exclude.features_inversion.classification.dataset.BinDataset;
import weka.core.Instances;

public class TestExp {

    public static void main(String[] args) {

        final int[] mfIndices = { 4, 5, 6, 7, 8, 9, 10, 11, 12, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33 };
        final int n = mfIndices.length;

        final double[] sum0 = new double[n];
        final double[] sum1 = new double[n];
        final double[] sum2 = new double[n];

        final List<BinDataset> datasets = new ArrayList<BinDataset>();

        for (File file : new File("data\\bin_undin\\").listFiles()) {
            try (FileReader reader = new FileReader(file)) {
                Instances instances = new Instances(reader);
                instances.setClassIndex(instances.numAttributes() - 1);
                BinDataset dataset = BinDataset.fromInstances(instances);

                boolean nan = false;
                for (int i = 0; i < n; i++) {
                    double val = dataset.getMetaFeature(mfIndices[i]);
                    if (Double.isNaN(val)) {
                        nan = true;
                    } else {
                        sum0[i] += 1;
                        sum1[i] += val;
                        sum2[i] += val * val;
                    }
                }

                if (nan) {
                    System.err.println(file.getName() + " mf is NaN");
                } else {
                    datasets.add(dataset);
                }

            } catch (Exception e) {
                System.err.println(e.getLocalizedMessage());
            }
        }

        System.out.println(Arrays.toString(sum0));
        System.out.println(Arrays.toString(sum1));
        System.out.println(Arrays.toString(sum2));

        final double[] target = new double[n];
        final double[] weight = new double[n];

        for (int i = 0; i < n; i++) {
            double mX1 = sum1[i] / sum0[i];
            double mX2 = sum2[i] / sum0[i];

            double mean = mX1;
            double var = mX2 - mX1 * mX1;
            double std = Math.sqrt(var);

            target[i] = mean;
            weight[i] = 1 / std;
        }

        System.out.println(Arrays.toString(target));
        System.out.println(Arrays.toString(weight));

        EuclideanDist errorFunction = new EuclideanDist(target, weight, mfIndices);

        // GenOverObj g = new GenOverObj(datasets);

        long time = System.currentTimeMillis();

        int pop = 50;

        double sumAttributes = 0, sumPosInstances = 0, sumNegInstances = 0;
        for (BinDataset dataset : datasets) {
            sumAttributes += dataset.numAttr;
            sumPosInstances += Math.max(dataset.pos.length, dataset.neg.length);
            sumNegInstances += Math.min(dataset.pos.length, dataset.neg.length);
        }

        // Attributes

        int numAttributes = (int) Math.round(sumAttributes / datasets.size());
        int numPosInstances = (int) Math.round(sumPosInstances / datasets.size());
        int numNegInstances = (int) Math.round(sumNegInstances / datasets.size());

        double closestval = Double.POSITIVE_INFINITY;

        for (BinDataset dataset : datasets) {
            double cur = errorFunction.evaluate(dataset);

            if (cur < closestval) {
                closestval = cur;
                numAttributes = dataset.numAttr;
                numPosInstances = dataset.pos.length;
                numNegInstances = dataset.neg.length;
            }

        }
        System.out.printf("a = %d, p = %d, n = %d%n", numAttributes, numPosInstances, numNegInstances);

        errorFunction.best = Double.POSITIVE_INFINITY;

        DatasetGenerator g = new GenOverDVect(datasets);
        // DatasetGenerator g = new GenOverObj(datasets);

        System.out.println("size = " + g.generate(numAttributes, numPosInstances, numNegInstances, errorFunction, pop, pop * 40).size());

        System.out.println("time " + (System.currentTimeMillis() - time));

    }

}
