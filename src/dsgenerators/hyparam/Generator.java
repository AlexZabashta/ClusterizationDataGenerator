package dsgenerators.hyparam;

import java.util.Random;

import features_inversion.classification.dataset.BinDataset;
import features_inversion.classification.dataset.RelationsGenerator;
import weka.core.Instance;
import weka.core.Instances;

public interface Generator {
    static BinDataset convert(int a, int p, int n, Instances instances, Random random) {

        int classIndex = instances.classIndex();

        int cntP = 0, cntN = 0;

        for (Instance instance : instances) {

            if (instance.classValue() < 0.5) {
                ++cntN;
            } else {
                ++cntP;
            }
        }

        if (cntN == 0) {
            ++cntN;
        }

        if (cntP == 0) {
            ++cntP;
        }

        double[][] pos = new double[cntP][a];
        double[][] neg = new double[cntN][a];
        cntP = cntN = 0;

        for (Instance instance : instances) {
            double[] vec;

            if (instance.classValue() < 0.5) {
                vec = neg[cntN++];
            } else {
                vec = pos[cntP++];
            }

            for (int i = 0, j = 0; i <= a; i++) {
                if (i != classIndex) {
                    try {
                        vec[j++] = instance.value(i);
                    } catch (ArrayIndexOutOfBoundsException error) {
                        System.err.println(a + " " + p + " " + n);
                        throw error;
                    }
                }
            }
        }

        for (; cntP < pos.length; cntP++) {
            for (int j = 0; j < a; j++) {
                pos[cntP][j] = random.nextGaussian();
            }
        }

        for (; cntN < neg.length; cntN++) {
            for (int j = 0; j < a; j++) {
                neg[cntN][j] = random.nextGaussian();
            }
        }

        if (pos.length < neg.length) {
            double[][] tmp = pos;
            pos = neg;
            neg = tmp;
        }
        pos = RelationsGenerator.fit(pos, p, a, random);
        neg = RelationsGenerator.fit(neg, n, a, random);

        return new BinDataset(pos, neg, a);
    }

    BinDataset generate() throws Exception;
}
