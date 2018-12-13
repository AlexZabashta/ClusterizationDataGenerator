import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import dsgenerators.EndSearch;
import dsgenerators.ErrorFunction;
import dsgenerators.ListMetaFeatures;
import dsgenerators.hyparam.BayesNetGen;
import dsgenerators.hyparam.GMMGen;
import dsgenerators.hyparam.GeneratorBuilder;
import dsgenerators.hyparam.PGProblem;
import dsgenerators.hyparam.RDG1Gen;
import dsgenerators.vect.SimpleProblem;
import features_inversion.classification.dataset.BinDataMutation;
import features_inversion.classification.dataset.BinDataset;
import features_inversion.classification.fun.AttributeFunction;
import features_inversion.classification.fun.RandomFunction;
import misc.FolderUtils;
import weka.core.Instances;

public class NullTest {
    public static void main(String[] args) throws Exception {
        final int[] mfIndices = { 4, 5, 6, 7, 8, 9, 10, 11, 12, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33 };
        final int m = mfIndices.length;

        Random random = new Random();

        while (true) {

            int d = random.nextInt(10);
            int a = random.nextInt(10) + 1;
            int p = random.nextInt(10) + 1;
            int n = random.nextInt(10) + 1;

            ErrorFunction error = new ErrorFunction() {

                @Override
                public int length() {
                    return 1;
                }

                @Override
                public double[] componentwise(BinDataset dataset) throws EndSearch {
                    return new double[] { random.nextDouble() };
                }

                @Override
                public double aggregate(double[] vector) {
                    return vector[0];
                }
            };

            SimpleProblem problem = new SimpleProblem(a, p, n, error, null);
            BinDataset dataSet = problem.build(problem.createSolution());

            for (int i : mfIndices) {
                double val = dataSet.getMetaFeature(i);
                if (Double.isInfinite(val)) {
                    System.out.println(ListMetaFeatures.getName(i));
                    System.out.println(dataSet.WEKAInstances());
                    return;
                }
            }

        }

    }
}
