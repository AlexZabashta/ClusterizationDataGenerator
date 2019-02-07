package mfextraction.statistical;

import mfextraction.CacheMF;
import mfextraction.MetaFeatureExtractor;
import utils.StatisticalUtils;

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
    public double extract(CacheMF cache) {
        double sum = 0;

        double[][] attributeToDoubleArray = cache.attributeToDoubleArray();
        final int length = attributeToDoubleArray.length;

        if (length < 2) {
            return 0;
        }
        final int count = length * (length - 1) / 2;

        for (int i = 0; i < length; i++) {
            double[] values1 = attributeToDoubleArray[i];
            for (int j = 0; j < i; j++) {
                double[] values2 = attributeToDoubleArray[j];
                sum += StatisticalUtils.linearCorrelationCoefficient(values1, values2);
            }
        }

        return sum / count;
    }
}
