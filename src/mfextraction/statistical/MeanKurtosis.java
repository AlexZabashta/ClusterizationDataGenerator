package mfextraction.statistical;

import mfextraction.CacheMF;
import mfextraction.MetaFeatureExtractor;
import utils.StatisticalUtils;

/**
 * Created by warrior on 22.03.15.
 */
public class MeanKurtosis extends MetaFeatureExtractor {

    public static final String NAME = "Mean kurtosis";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double extract(CacheMF cache) {
        double[][] attributeToDoubleArray = cache.attributeToDoubleArray();
        final int count = attributeToDoubleArray.length;
        if (count == 0) {
            return 0;
        }

        double sum = 0;
        for (int i = 0; i < count; i++) {
            double[] values = attributeToDoubleArray[i];
            double mean = StatisticalUtils.mean(values);
            double variance = StatisticalUtils.variance(values, mean);
            if (variance > 1e-9) {
                sum += StatisticalUtils.centralMoment(values, 4, mean) / Math.pow(variance, 2);
            }
        }

        return sum / count;

    }
}
