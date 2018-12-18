package mfextraction.statistical;

import mfextraction.MetaFeatureExtractor;
import utils.StatisticalUtils;
import weka.core.Instances;

/**
 * Created by warrior on 22.03.15.
 */
public class MeanLinearCorrelationCoefficient extends MetaFeatureExtractor {

    private static final String NAME = "Mean absolute linear correlation coefficient of all possible pairs of features";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double extract(Instances instances) {
        double sum = 0;
        int count = 0;
        for (int i = 0; i < instances.numAttributes(); i++) {
            double[] values1 = instances.attributeToDoubleArray(i);
            for (int j = i + 1; j < instances.numAttributes(); j++) {
                double[] values2 = instances.attributeToDoubleArray(j);
                sum += StatisticalUtils.linearCorrelationCoefficient(values1, values2);
                count++;
            }
        }
        return sum / count;
    }
}
