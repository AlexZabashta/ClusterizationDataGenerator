package mfextraction.distances;

import org.apache.commons.math3.stat.descriptive.moment.Mean;

import mfextraction.CacheMF;
import mfextraction.MetaFeatureExtractor;

/**
 * Created by sergey on 10.03.16.
 */
public class MeanD extends MetaFeatureExtractor {

    @Override
    public String getName() {
        return "MD1";
    }

    @Override
    public double extract(CacheMF cache) {
        double[] finalDist = cache.normalizedDistances();
        Mean m = new Mean();
        return m.evaluate(finalDist);
    }
}
