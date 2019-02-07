package mfextraction.distances;

import org.apache.commons.math3.stat.descriptive.moment.Skewness;

import mfextraction.CacheMF;
import mfextraction.MetaFeatureExtractor;

/**
 * Created by sergey on 04.05.16.
 */
public class SkewnessD extends MetaFeatureExtractor {
    @Override
    public String getName() {
        return "MD4";
    }

    @Override
    public double extract(CacheMF cache) {
        double[] finalDist = cache.normalizedDistances();
        Skewness sk = new Skewness();
        return sk.evaluate(finalDist);
    }
}
