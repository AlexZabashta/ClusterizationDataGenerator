package old;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dsgenerators.EndSearch;
import dsgenerators.ErrorFunction;
import dsgenerators.EuclideanDist;
import dsgenerators.ListMetaFeatures;
import dsgenerators.direct.GDSProblem;
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
import misc.Experiment;
import misc.FolderUtils;
import weka.core.Instances;

public class InfTest {
    public static void main(String[] args) throws Exception {
        final int[] mfIndices = { 4, 5, 6, 7, 8, 9, 10, 11, 12, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33 };
        final int n = mfIndices.length;


        final double[] sum0 = new double[n];
        final double[] sum1 = new double[n];
        final double[] sum2 = new double[n];

        final List<BinDataset> datasets = new ArrayList<BinDataset>();
        final List<String> fileNames = new ArrayList<String>();

        for (File file : new File("data\\bin_all\\").listFiles()) {
            try (FileReader reader = new FileReader(file)) {
                Instances instances = new Instances(reader);
                instances.setClassIndex(instances.numAttributes() - 1);
                BinDataset dataset = BinDataset.fromInstances(instances);

                if (instances.numAttributes() > 300 || instances.numInstances() > 1000) {
                    System.err.println(file.getName() + " too big");
                    continue;
                }

                boolean nan = false;
                for (int i = 0; i < n; i++) {
                    double val = dataset.getMetaFeature(mfIndices[i]);
                    if (Double.isNaN(val) || Double.isInfinite(val)) {
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
                    fileNames.add(file.getName());
                    System.err.println(file.getName() + " added");
                }

            } catch (Exception e) {
                System.err.println(file.getName() + " " + e.getMessage());
            }
        }

        System.out.println(Arrays.toString(sum0));
        System.out.println(Arrays.toString(sum1));
        System.out.println(Arrays.toString(sum2));

        final double[] weight = new double[n];

        for (int i = 0; i < n; i++) {
            double mX1 = sum1[i] / sum0[i];
            double mX2 = sum2[i] / sum0[i];

            double mean = mX1;
            double var = mX2 - mX1 * mX1;
            double std = Math.sqrt(var);

            weight[i] = 1 / std;
        }

        System.out.println(Arrays.toString(weight));

        final int size = datasets.size();

        final int limit = 2048;

        ExecutorService threads = Executors.newFixedThreadPool(8);

        List<Experiment> exp = new ArrayList<>();

        for (int targetIndex = 0; targetIndex < size; targetIndex++) {

            BinDataset targetDataset = datasets.get(targetIndex);
            final double[] target = new double[n];
            for (int i = 0; i < n; i++) {
                target[i] = targetDataset.getMetaFeature(mfIndices[i]);
            }

            final String fileName = fileNames.get(targetIndex).replace('_', '-');

            final ErrorFunction ef = new EuclideanDist(target, weight, mfIndices, true);

            GDSProblem gdsProblem = new GDSProblem(targetDataset.numAttr, targetDataset.pos.length, targetDataset.neg.length, ef, null);

            for (int rep = 0; rep < 100; rep++) {
                BinDataset dataset = gdsProblem.createSolution().getVariableValue(0);
                double[] v = ef.componentwise(dataset);

                for (int i = 0; i < n; i++) {
                    if (Double.isInfinite(v[i])) {
                        System.out.println(dataset.WEKAInstances());
                        System.out.println(ListMetaFeatures.getName(mfIndices[i]) + " is INF");
                        return;
                    }

                    if (Double.isNaN(v[i])) {
                        System.out.println(dataset.WEKAInstances());
                        System.out.println(ListMetaFeatures.getName(mfIndices[i]) + " is NAN");
                        return;
                    }
                }

                double aggr = ef.aggregate(v);
                if (Double.isInfinite(aggr)) {
                    System.out.println(dataset.WEKAInstances());
                    System.out.println(Arrays.toString(v));
                    System.out.println("aggr is INF");
                    return;
                }

                if (Double.isNaN(aggr)) {
                    System.out.println(dataset.WEKAInstances());
                    System.out.println(Arrays.toString(v));
                    System.out.println("aggr is NAN");
                    return;
                }

                
                
                
            }

        }
    }
}
