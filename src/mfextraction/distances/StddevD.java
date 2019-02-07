package mfextraction.distances;

import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import mfextraction.CacheMF;
import mfextraction.MetaFeatureExtractor;

/**
 * Created by sergey on 04.05.16.
 */
public class StddevD extends MetaFeatureExtractor {
    @Override
    public String getName() {
        return "MD3";
    }

    @Override
    public double extract(CacheMF cache) {
        double[] finalDist = cache.normalizedDistances();
        StandardDeviation sd = new StandardDeviation();
        return sd.evaluate(finalDist);
    }
}
