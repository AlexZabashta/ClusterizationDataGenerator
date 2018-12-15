package old;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import features_inversion.classification.dataset.BinDataset;
import weka.core.Instances;

public class SerializeDataset {

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

                if (dataset.numAttr > 300) {
                    continue;
                }

                if (dataset.pos.length + dataset.neg.length > 1000) {
                    continue;
                }

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
                   // datasets.add(dataset);
                }

            } catch (Exception e) {
                System.err.println(e.getLocalizedMessage());
            }
        }

        for (int i = 0; i < n; i++) {
            double mX1 = sum1[i] / sum0[i];
            double mX2 = sum2[i] / sum0[i];

            double mean = mX1;
            double var = mX2 - mX1 * mX1;
            double std = Math.sqrt(var);

            System.out.printf("%2d %10.5f %10.5f%n", mfIndices[i], mean, std);

        }

        System.out.println(datasets.size());

    }

}
