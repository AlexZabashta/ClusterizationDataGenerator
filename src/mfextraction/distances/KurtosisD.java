package mfextraction.distances;

import org.apache.commons.math3.stat.descriptive.moment.Kurtosis;

import mfextraction.CacheMF;
import mfextraction.MetaFeatureExtractor;

/**
 * Created by sergey on 04.05.16.
 */
public class KurtosisD extends MetaFeatureExtractor {
    @Override
    public String getName() {
        return "MD5";
    }

    @Override
    public double extract(CacheMF cache) {
        double[] distances = cache.normalizedDistances();
        Kurtosis kurt = new Kurtosis();
        return kurt.evaluate(distances);
    }
}
